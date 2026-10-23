package de.iani.cubesideutils.bukkit.commands;

import static org.junit.jupiter.api.Assertions.*;

import com.destroystokyo.paper.event.server.AsyncTabCompleteEvent;
import de.iani.cubesideutils.commands.ArgsParser;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.logging.Handler;
import java.util.logging.LogRecord;
import java.util.logging.Logger;
import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.SimpleCommandMap;
import org.bukkit.entity.Player;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.plugin.IllegalPluginAccessException;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.scheduler.BukkitScheduler;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class AsyncTabCompletionTest {
    private final Thread mainThread = Thread.currentThread();
    private final BlockingQueue<Runnable> tasks = new LinkedBlockingQueue<>();
    private final List<AsyncTabCompletionBridge> bridges = new ArrayList<>();
    private final List<LogRecord> logs = new ArrayList<>();
    private final Map<String, Boolean> permissions = new HashMap<>();
    private final ExecutorService eventThread = Executors.newSingleThreadExecutor(runnable -> {
        Thread thread = new Thread(runnable, "test-paper-completion");
        thread.setDaemon(true);
        return thread;
    });
    private final Logger logger = Logger.getAnonymousLogger();
    private Server server;
    private SimpleCommandMap commandMap;
    private CommandSender sender;
    private Plugin plugin;
    private boolean enabled = true;
    private Field serverField;
    private Object previousServer;

    @BeforeEach
    void setUp() throws Exception {
        logger.setUseParentHandlers(false);
        logger.addHandler(new Handler() {
            @Override public void publish(LogRecord record) { logs.add(record); }
            @Override public void flush() { }
            @Override public void close() { }
        });
        BukkitScheduler scheduler = proxy(BukkitScheduler.class, (obj, method, args) -> {
            if (method.getName().equals("runTask")) {
                tasks.add((Runnable) args[1]);
                return null;
            }
            throw new UnsupportedOperationException(method.toString());
        });
        PluginManager manager = proxy(PluginManager.class, (obj, method, args) -> {
            if (method.getName().equals("getPlugin")) {
                return null; // Command timings also look up the fallback namespace.
            }
            if (method.getName().equals("registerEvents")) {
                assertSame(mainThread, Thread.currentThread());
                bridges.add((AsyncTabCompletionBridge) args[0]);
                return null;
            }
            throw new UnsupportedOperationException(method.toString());
        });
        server = proxy(Server.class, (obj, method, args) -> switch (method.getName()) {
            case "isPrimaryThread" -> Thread.currentThread() == mainThread;
            case "getCommandMap" -> {
                assertSame(mainThread, Thread.currentThread());
                yield commandMap;
            }
            case "getScheduler" -> scheduler;
            case "getPluginManager" -> manager;
            case "getOnlinePlayers" -> List.of();
            case "getLogger" -> logger;
            case "suggestPlayerNamesWhenNullTabCompletions" -> true;
            default -> throw new UnsupportedOperationException(method.toString());
        });
        // Avoid Bukkit.setServer's logging through Paper's runtime-only build-info SPI.
        serverField = Bukkit.class.getDeclaredField("server");
        serverField.setAccessible(true);
        previousServer = serverField.get(null);
        serverField.set(null, server);
        commandMap = new SimpleCommandMap(server, new HashMap<>());
        plugin = proxy(Plugin.class, (obj, method, args) -> switch (method.getName()) {
            case "getServer" -> server;
            case "getLogger" -> logger;
            case "isEnabled" -> enabled;
            case "getName" -> "test";
            case "getPluginMeta" -> proxy(method.getReturnType(), (meta, metaMethod, metaArgs) -> "test");
            default -> throw new UnsupportedOperationException(method.toString());
        });
        sender = sender(CommandSender.class);
    }

    @AfterEach
    void tearDown() throws Exception {
        try {
            for (AsyncTabCompletionBridge bridge : List.copyOf(bridges)) {
                bridge.onPluginDisable(new PluginDisableEvent(plugin));
            }
            eventThread.shutdownNow();
            assertTrue(eventThread.awaitTermination(5, TimeUnit.SECONDS));
        } finally {
            if (serverField != null) {
                serverField.set(null, previousServer);
            }
        }
    }

    @Test
    void preparesOnMainThreadAndWaitsOnlyOnEventThread() throws Exception {
        CompletableFuture<List<String>> stage = new CompletableFuture<>();
        CommandRouter router = router(args -> {
            assertSame(mainThread, Thread.currentThread());
            assertEquals("a", args.getNext());
            return stage.minimalCompletionStage();
        });
        router.addCommandMapping(new TestSubCommand(), "alpha");
        register("root", router);

        Future<AsyncTabCompleteEvent> request = fire("/root a");
        runNextTask();
        assertFalse(request.isDone());
        stage.complete(List.of("zeta", "apple", "Apple"));
        AsyncTabCompleteEvent event = request.get(5, TimeUnit.SECONDS);
        assertTrue(event.isHandled());
        assertEquals(List.of("Apple", "alpha", "apple"), event.getCompletions());
        assertTrue(logs.isEmpty());
    }

    @Test
    void nullStagePreservesSynchronousPathAndItsNullResult() throws Exception {
        AtomicInteger calls = new AtomicInteger();
        CommandRouter router = new CommandRouter();
        router.addCommandMapping(new TestSubCommand() {
            @Override public Collection<String> onTabComplete(CommandSender sender, Command command, String alias, ArgsParser args) {
                calls.incrementAndGet();
                return null;
            }
        });
        PluginCommand command = register("root", router);
        Future<AsyncTabCompleteEvent> request = fire("root ");
        runNextTask();
        assertFalse(request.get(5, TimeUnit.SECONDS).isHandled());
        assertEquals(0, calls.get());
        assertNull(router.onTabComplete(sender, command, "root", new String[] {""}));
        assertEquals(1, calls.get());
        router.addCommandMapping(new TestSubCommand(), "child");
        assertEquals(List.of("child"), router.onTabComplete(sender, command, "root", new String[] {""}));
    }

    @Test
    void emptyAsyncResultIsHandledAndStillIncludesChildren() throws Exception {
        CommandRouter router = router(args -> CompletableFuture.completedFuture(List.of()));
        register("root", router);
        assertEquals(List.of(), complete("root ").getCompletions());
        router.addCommandMapping(new TestSubCommand(), "child");
        assertEquals(List.of("child"), complete("root ").getCompletions());
    }

    @Test
    void syncAndAsyncSubcommandsCanBeMixed() throws Exception {
        CommandRouter router = new CommandRouter();
        router.addCommandMapping(new TestSubCommand(), "sync");
        router.addCommandMapping(new TestSubCommand() {
            @Override public CompletionStage<? extends Collection<String>> onTabCompleteAsync(CommandSender sender, Command command, String alias, ArgsParser args) {
                return CompletableFuture.completedFuture(List.of("value"));
            }
        }, "async");
        register("root", router);
        assertFalse(complete("root sync ").isHandled());
        assertEquals(List.of("value"), complete("root async ").getCompletions());
    }

    @Test
    void permissionAvailabilityAndVisibilityChecksStayOnMainThread() throws Exception {
        CommandRouter router = router(args -> CompletableFuture.completedFuture(List.of()));
        router.addCommandMapping(new TestSubCommand(), "allowed");
        router.addCommandMapping(new TestSubCommand() {
            @Override public boolean isVisible(CommandSender sender) {
                assertSame(mainThread, Thread.currentThread());
                return false;
            }
        }, "hidden");
        router.addCommandMapping(new TestSubCommand() {
            @Override public boolean isAvailable(CommandSender sender) {
                assertSame(mainThread, Thread.currentThread());
                return false;
            }
        }, "unavailable");
        router.addCommandMapping(new TestSubCommand() {
            @Override public String getRequiredPermission() { return "denied"; }
        }, "forbidden");
        permissions.put("denied", false);
        router.addCommandMapping(new TestSubCommand() {
            @Override public boolean requiresPlayer() { return true; }
        }, "player");
        register("root", router);
        assertEquals(List.of("allowed"), complete("root ").getCompletions());
    }

    @Test
    void unavailableExecutorNeverStartsAsyncWork() throws Exception {
        CommandRouter router = new CommandRouter();
        router.addCommandMapping(new TestSubCommand() {
            @Override public boolean isAvailable(CommandSender sender) { return false; }
            @Override public CompletionStage<? extends Collection<String>> onTabCompleteAsync(CommandSender sender, Command command, String alias, ArgsParser args) {
                fail("Unavailable command must not start its provider");
                return null;
            }
        });
        register("root", router);
        assertFalse(complete("root ").isHandled());
    }

    @Test
    void rootPermissionIsCheckedBeforeProvider() throws Exception {
        CommandRouter router = router(args -> { throw new AssertionError("Provider must not run"); });
        register("root", router).setPermission("denied");
        permissions.put("denied", false);
        AsyncTabCompleteEvent event = complete("root ");
        assertTrue(event.isHandled());
        assertTrue(event.getCompletions().isEmpty());
        assertTrue(logs.isEmpty());
    }

    @Test
    void aliasesNamespacesAndTrailingEmptyArgumentsArePreserved() throws Exception {
        CommandRouter router = new CommandRouter();
        router.addCommandMapping(new TestSubCommand() {
            @Override public CompletionStage<? extends Collection<String>> onTabCompleteAsync(CommandSender sender, Command command, String alias, ArgsParser args) {
                assertEquals("test:r", alias);
                assertArrayEquals(new String[] {"", ""}, args.toArray());
                return CompletableFuture.completedFuture(List.of("value"));
            }
        }, "parent", "leaf");
        router.addAlias("l", "parent", "leaf");
        register("root", router, "r");
        assertEquals(List.of("value"), complete("/test:r parent l  ").getCompletions());
    }

    @Test
    void ignoresForeignChatCancelledHandledAndRootOnlyRequests() throws Exception {
        register("root", router(args -> { throw new AssertionError("Provider must not run"); }));
        List<AsyncTabCompleteEvent> events = new ArrayList<>();
        for (String input : List.of("other ", "/root", "/", " root ")) {
            events.add(event(input));
        }
        events.add(new AsyncTabCompleteEvent(sender, "root ", false, null));
        AsyncTabCompleteEvent cancelled = event("root ");
        cancelled.setCancelled(true);
        events.add(cancelled);
        AsyncTabCompleteEvent handled = event("root ");
        handled.setHandled(true);
        events.add(handled);
        AsyncTabCompleteEvent populated = event("root ");
        populated.setCompletions(List.of("external"));
        events.add(populated);
        for (AsyncTabCompleteEvent event : events) {
            assertSame(event, fire(event).get(5, TimeUnit.SECONDS));
        }
        assertTrue(tasks.isEmpty());
        assertEquals(List.of("external"), populated.getCompletions());
    }

    @Test
    void replacedCommandOrCompleterTakesPrecedence() throws Exception {
        PluginCommand command = register("root", router(args -> { throw new AssertionError("Old provider must not run"); }));
        command.setTabCompleter((sender, cmd, alias, args) -> List.of("external"));
        assertFalse(complete("root ").isHandled());
        commandMap.getKnownCommands().put("root", new Command("replacement") {
            @Override public boolean execute(CommandSender sender, String label, String[] args) { return true; }
        });
        assertFalse(complete("root ").isHandled());
        assertTrue(logs.isEmpty());
    }

    @Test
    void thrownPreparationFailurePreservesChildrenAndLogsContext() throws Exception {
        CommandRouter router = router(args -> { throw new IllegalStateException("broken provider"); });
        router.addCommandMapping(new TestSubCommand(), "child");
        register("root", router);
        assertEquals(List.of("child"), complete("/root ").getCompletions());
        assertEquals(1, logs.size());
        assertTrue(logs.getFirst().getMessage().contains("/root "));
        assertTrue(logs.getFirst().getThrown() instanceof IllegalStateException);
    }

    @Test
    void failedCancelledAndInvalidResultsPreserveChildren() throws Exception {
        List<CompletableFuture<Collection<String>>> stages = List.of(
                CompletableFuture.failedFuture(new IllegalStateException("failed")),
                new CompletableFuture<>(),
                CompletableFuture.completedFuture(null),
                CompletableFuture.completedFuture(Arrays.asList("value", null)));
        stages.get(1).cancel(false);
        for (CompletableFuture<Collection<String>> stage : stages) {
            CommandRouter router = router(args -> stage);
            router.addCommandMapping(new TestSubCommand(), "child");
            register("root", router);
            AsyncTabCompleteEvent event = complete("root ");
            assertTrue(event.isHandled());
            assertEquals(List.of("child"), event.getCompletions());
        }
        assertEquals(stages.size(), logs.size());
    }

    @Test
    void disableReleasesPendingStageWithoutCancellingProvider() throws Exception {
        CompletableFuture<List<String>> stage = new CompletableFuture<>();
        register("root", router(args -> stage));
        Future<AsyncTabCompleteEvent> request = fire("root ");
        runNextTask();
        assertFalse(request.isDone());
        disable();
        AsyncTabCompleteEvent event = request.get(5, TimeUnit.SECONDS);
        assertTrue(event.isHandled());
        assertTrue(event.getCompletions().isEmpty());
        assertFalse(stage.isDone());
        stage.complete(List.of("late"));
        assertTrue(event.getCompletions().isEmpty());
        assertTrue(logs.isEmpty());
    }

    @Test
    void disableAlsoReleasesRequestsBeforeMainThreadPreparation() throws Exception {
        register("root", router(args -> { throw new AssertionError("Provider must not run"); }));
        Future<AsyncTabCompleteEvent> request = fire("root ");
        Runnable preparation = tasks.poll(5, TimeUnit.SECONDS);
        assertNotNull(preparation);
        disable();
        assertTrue(request.get(5, TimeUnit.SECONDS).isHandled());
        preparation.run();
        assertTrue(logs.isEmpty());
    }

    @Test
    void registersOneBridgePerPluginForMultipleCommandsAndRepeatedBindings() throws Exception {
        CommandRouter router = router(args -> CompletableFuture.completedFuture(List.of("value")));
        PluginCommand first = register("first", router);
        register("second", router);
        router.registerAsyncTabCompletion(plugin, first);
        assertEquals(1, bridges.size());
        assertEquals(List.of("value"), complete("first ").getCompletions());
        assertEquals(List.of("value"), complete("test:second ").getCompletions());
    }

    @Test
    void constructorAndLateExplicitRegistrationBothWork() throws Exception {
        enabled = false;
        PluginCommand command = pluginCommand("root");
        CommandRouter router = new CommandRouter(command);
        router.addCommandMapping(new TestSubCommand() {
            @Override public CompletionStage<? extends Collection<String>> onTabCompleteAsync(CommandSender sender, Command command, String alias, ArgsParser args) {
                return CompletableFuture.completedFuture(List.of("value"));
            }
        });
        assertTrue(bridges.isEmpty());
        assertSame(router, command.getExecutor());
        assertSame(router, command.getTabCompleter());
        assertThrows(IllegalPluginAccessException.class, () -> router.registerAsyncTabCompletion(plugin, command));
        enabled = true;
        router.registerAsyncTabCompletion(plugin, command);
        assertEquals(List.of("value"), complete("root ").getCompletions());
        PluginCommand other = pluginCommand("other");
        new CommandRouter(other);
        assertEquals(1, bridges.size());
    }

    @Test
    void explicitRegistrationDoesNotChangeHandlersAndRequiresMainThread() throws Exception {
        PluginCommand command = pluginCommand("root");
        CommandRouter router = router(args -> CompletableFuture.completedFuture(List.of("value")));
        Object executor = command.getExecutor();
        router.registerAsyncTabCompletion(plugin, command);
        assertSame(executor, command.getExecutor());
        assertNull(command.getTabCompleter());
        assertFalse(complete("root ").isHandled());
        Future<?> registration = eventThread.submit(() -> router.registerAsyncTabCompletion(plugin, command));
        java.util.concurrent.ExecutionException ex = assertThrows(java.util.concurrent.ExecutionException.class, () -> registration.get(5, TimeUnit.SECONDS));
        assertTrue(ex.getCause() instanceof IllegalStateException);
    }

    @Test
    void constructorDoesNotInvokeOverridableRegistrationMethods() throws Exception {
        PluginCommand command = pluginCommand("root");
        CommandRouter router = new CommandRouter(command) {
            @Override public void addPluginCommand(PluginCommand command) { fail("Virtual call from constructor"); }
            @Override public void registerAsyncTabCompletion(Plugin plugin, Command command) { fail("Virtual call from constructor"); }
        };
        assertSame(router, command.getTabCompleter());
        assertEquals(1, bridges.size());
    }

    @Test
    void disablingOnePluginDoesNotReleaseAnotherPluginsRequest() throws Exception {
        register("first", new CommandRouter());
        Plugin other = proxy(Plugin.class, (obj, method, args) -> switch (method.getName()) {
            case "getServer" -> server;
            case "getLogger" -> logger;
            case "isEnabled" -> true;
            case "getPluginMeta" -> proxy(method.getReturnType(), (meta, metaMethod, metaArgs) -> "other");
            default -> throw new UnsupportedOperationException(method.toString());
        });
        CompletableFuture<List<String>> stage = new CompletableFuture<>();
        CommandUtil.registerCommand(other, "second", router(args -> stage));
        assertEquals(2, bridges.size());
        Future<AsyncTabCompleteEvent> request = fire("second ");
        runNextTask();
        disable();
        assertFalse(request.isDone());
        stage.complete(List.of("value"));
        assertEquals(List.of("value"), request.get(5, TimeUnit.SECONDS).getCompletions());
        for (AsyncTabCompletionBridge bridge : bridges) {
            bridge.onPluginDisable(new PluginDisableEvent(other));
        }
    }

    @Test
    void dynamicallyRegisteredCommandsIncludeNamespaceOnlyCollisions() throws Exception {
        commandMap.register("other", new Command("root") {
            @Override public boolean execute(CommandSender sender, String label, String[] args) { return true; }
        });
        CommandRouter router = router(args -> CompletableFuture.completedFuture(List.of("value")));
        assertNull(CommandUtil.registerCommand(plugin, "root", List.of("r"), router, false));
        assertEquals(List.of("value"), complete("test:root ").getCompletions());
        assertEquals(List.of("value"), complete("r ").getCompletions());
        assertEquals(List.of("value"), complete("test:r ").getCompletions());
        assertFalse(fire("root ").get(5, TimeUnit.SECONDS).isHandled());
    }

    @Test
    void dynamicCommandsWithAnotherCompleterAreNotBoundToRouterExecutor() {
        CommandRouter router = new CommandRouter();
        assertNotNull(CommandUtil.registerCommand(plugin, "root", router, (sender, command, alias, args) -> List.of("external")));
        assertTrue(bridges.isEmpty());
    }

    @Test
    void playerSubcommandDelegatesToPlayerHookAndRejectsOtherSenders() {
        Player player = sender(Player.class);
        AtomicInteger calls = new AtomicInteger();
        PlayerSubCommand subCommand = new PlayerSubCommand() {
            @Override public boolean onCommand(Player sender, Command command, String alias, String commandString, ArgsParser args) { return true; }
            @Override public CompletionStage<? extends Collection<String>> onTabCompleteAsync(Player sender, Command command, String alias, ArgsParser args) {
                calls.incrementAndGet();
                assertSame(player, sender);
                return CompletableFuture.completedFuture(List.of("value"));
            }
        };
        assertEquals(List.of("value"), subCommand.onTabCompleteAsync((CommandSender) player, null, "root", new ArgsParser(new String[] {""})).toCompletableFuture().join());
        assertTrue(subCommand.onTabCompleteAsync(sender, null, "root", new ArgsParser(new String[] {""})).toCompletableFuture().join().isEmpty());
        assertEquals(1, calls.get());
    }

    private CommandRouter router(Function<ArgsParser, CompletionStage<? extends Collection<String>>> provider) {
        CommandRouter router = new CommandRouter();
        router.addCommandMapping(new TestSubCommand() {
            @Override public CompletionStage<? extends Collection<String>> onTabCompleteAsync(CommandSender sender, Command command, String alias, ArgsParser args) {
                return provider.apply(args);
            }
        });
        return router;
    }

    private PluginCommand register(String name, CommandRouter router, String... aliases) throws Exception {
        PluginCommand command = pluginCommand(name, aliases);
        router.addPluginCommand(command);
        return command;
    }

    private PluginCommand pluginCommand(String name, String... aliases) throws Exception {
        var constructor = PluginCommand.class.getDeclaredConstructor(String.class, Plugin.class);
        constructor.setAccessible(true);
        PluginCommand command = constructor.newInstance(name, plugin);
        command.setAliases(new ArrayList<>(List.of(aliases)));
        commandMap.register("test", command);
        return command;
    }

    private AsyncTabCompleteEvent event(String input) {
        return new AsyncTabCompleteEvent(sender, input, true, null);
    }

    private Future<AsyncTabCompleteEvent> fire(String input) {
        return fire(event(input));
    }

    private Future<AsyncTabCompleteEvent> fire(AsyncTabCompleteEvent event) {
        List<AsyncTabCompletionBridge> listeners = List.copyOf(bridges);
        return eventThread.submit(() -> {
            for (AsyncTabCompletionBridge bridge : listeners) {
                bridge.onAsyncTabComplete(event);
            }
            return event;
        });
    }

    private AsyncTabCompleteEvent complete(String input) throws Exception {
        Future<AsyncTabCompleteEvent> request = fire(input);
        runNextTask();
        return request.get(5, TimeUnit.SECONDS);
    }

    private void runNextTask() throws Exception {
        Runnable task = tasks.poll(5, TimeUnit.SECONDS);
        assertNotNull(task, "Expected main-thread preparation");
        task.run();
    }

    private void disable() {
        for (AsyncTabCompletionBridge bridge : List.copyOf(bridges)) {
            bridge.onPluginDisable(new PluginDisableEvent(plugin));
        }
        enabled = false;
    }

    private <T extends CommandSender> T sender(Class<T> type) {
        return proxy(type, (obj, method, args) -> switch (method.getName()) {
            case "hasPermission" -> {
                assertSame(mainThread, Thread.currentThread());
                yield permissions.getOrDefault(args[0].toString(), true);
            }
            case "getServer" -> server;
            case "getName" -> "TestSender";
            case "getUniqueId" -> new UUID(0, 1);
            case "isOp" -> false;
            case "getEffectivePermissions" -> Set.of();
            default -> throw new UnsupportedOperationException(method.toString());
        });
    }

    @SuppressWarnings("unchecked")
    private static <T> T proxy(Class<T> type, InvocationHandler handler) {
        return (T) Proxy.newProxyInstance(type.getClassLoader(), new Class<?>[] {type}, (obj, method, args) -> {
            if (method.getDeclaringClass() == Object.class) {
                return switch (method.getName()) {
                    case "equals" -> obj == args[0];
                    case "hashCode" -> System.identityHashCode(obj);
                    case "toString" -> "test-" + type.getSimpleName();
                    default -> throw new UnsupportedOperationException(method.toString());
                };
            }
            return handler.invoke(obj, method, args);
        });
    }

    private static class TestSubCommand extends SubCommand {
        @Override public boolean onCommand(CommandSender sender, Command command, String alias, String commandString, ArgsParser args) { return true; }
    }
}
