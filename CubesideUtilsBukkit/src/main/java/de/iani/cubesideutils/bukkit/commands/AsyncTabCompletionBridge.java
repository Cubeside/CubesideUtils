package de.iani.cubesideutils.bukkit.commands;

import com.destroystokyo.paper.event.server.AsyncTabCompleteEvent;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.Server;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.plugin.IllegalPluginAccessException;
import org.bukkit.plugin.Plugin;

/** Bridges Paper's event thread to main-thread routing and asynchronous providers. */
final class AsyncTabCompletionBridge implements Listener {
    // Accessed only on the main thread, like the routers' command maps.
    private static final Map<Plugin, AsyncTabCompletionBridge> BRIDGES = new IdentityHashMap<>();

    private final Plugin plugin;
    private final Server server;
    private final Logger logger;
    private final Map<Command, CommandRouter> routers = new IdentityHashMap<>();
    private final Set<Request> pending = ConcurrentHashMap.newKeySet();
    private volatile Set<String> labels = Set.of();
    private volatile boolean active = true;

    private AsyncTabCompletionBridge(Plugin plugin) {
        this.plugin = plugin;
        this.server = plugin.getServer();
        this.logger = plugin.getLogger();
    }

    static void register(Plugin plugin, Command command, CommandRouter router) {
        Objects.requireNonNull(plugin, "plugin");
        Objects.requireNonNull(command, "command");
        Objects.requireNonNull(router, "router");
        Server server = plugin.getServer();
        if (!server.isPrimaryThread()) {
            throw new IllegalStateException("Async tab completion must be registered on the main thread");
        }
        if (!plugin.isEnabled()) {
            throw new IllegalPluginAccessException("Async tab completion requires an enabled plugin");
        }
        AsyncTabCompletionBridge bridge = BRIDGES.get(plugin);
        if (bridge == null) {
            bridge = new AsyncTabCompletionBridge(plugin);
            server.getPluginManager().registerEvents(bridge, plugin);
            BRIDGES.put(plugin, bridge);
        }
        bridge.routers.put(command, router);
        Set<String> labels = new HashSet<>();
        for (Map.Entry<String, Command> entry : server.getCommandMap().getKnownCommands().entrySet()) {
            if (bridge.routers.containsKey(entry.getValue())) {
                labels.add(entry.getKey().toLowerCase(Locale.ROOT));
            }
        }
        bridge.labels = Set.copyOf(labels);
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onAsyncTabComplete(AsyncTabCompleteEvent event) {
        if (!active || !event.isCommand() || event.isCancelled() || event.isHandled() || server.isPrimaryThread()) {
            return;
        }
        String buffer = event.getBuffer();
        int start = buffer.startsWith("/") ? 1 : 0;
        int space = buffer.indexOf(' ', start);
        if (space <= start) {
            return;
        }
        String alias = buffer.substring(start, space);
        if (!labels.contains(alias.toLowerCase(Locale.ROOT))) {
            return;
        }
        String[] args = buffer.substring(space + 1).split(" ", -1);
        String partial = args[args.length - 1];
        Request request = new Request();
        pending.add(request);
        try {
            if (!active) {
                request.result.complete(List.of());
            } else {
                CommandSender sender = event.getSender();
                server.getScheduler().runTask(plugin, () -> prepare(request, sender, alias, args));
            }

            List<String> result;
            try {
                Collection<String> options = request.result.get();
                if (options == null) {
                    return; // Preserve the complete existing synchronous path.
                }
                result = CommandRouter.finishTabCompletion(partial, options, request.subCommands);
            } catch (InterruptedException ex) {
                Thread.currentThread().interrupt();
                result = CommandRouter.finishTabCompletion(partial, List.of(), request.subCommands);
            } catch (ExecutionException | RuntimeException ex) {
                logFailure(buffer, ex);
                result = CommandRouter.finishTabCompletion(partial, List.of(), request.subCommands);
            }
            event.setCompletions(active ? result : List.of());
            event.setHandled(true);
        } catch (RuntimeException ex) {
            // Scheduling can race with plugin shutdown.
            logFailure(buffer, ex);
            event.setCompletions(List.of());
            event.setHandled(true);
        } finally {
            request.result.complete(List.of());
            pending.remove(request);
        }
    }

    private void prepare(Request request, CommandSender sender, String alias, String[] args) {
        if (!active || request.result.isDone()) {
            request.result.complete(List.of());
            return;
        }
        List<String> subCommands = new ArrayList<>();
        try {
            Command command = server.getCommandMap().getCommand(alias);
            CommandRouter router = routers.get(command);
            if (router == null || !isCurrentCompleter(command, router)) {
                request.result.complete(null);
                return;
            }
            if (!command.testPermissionSilent(sender)) {
                request.result.complete(List.of());
                return;
            }
            CompletionStage<? extends Collection<String>> stage = router.prepareAsyncTabCompletion(sender, command, alias, args, subCommands);
            request.subCommands = List.copyOf(subCommands);
            if (stage == null) {
                request.result.complete(null);
            } else if (!request.result.isDone()) {
                // This callback retains no sender, event or bridge. A provider
                // may outlive the plugin, but can never mutate the Paper event.
                stage.whenComplete(request::complete);
            }
        } catch (Throwable failure) {
            request.subCommands = List.copyOf(subCommands);
            request.result.completeExceptionally(failure);
        }
    }

    private static boolean isCurrentCompleter(Command command, CommandRouter router) {
        if (command instanceof PluginCommand pluginCommand) {
            return pluginCommand.getTabCompleter() == router
                    || (pluginCommand.getTabCompleter() == null && pluginCommand.getExecutor() == router);
        }
        return true; // Other commands are explicitly bound, or bound by CommandUtil.
    }

    private void logFailure(String buffer, Throwable failure) {
        if (!active) {
            return;
        }
        while ((failure instanceof ExecutionException || failure instanceof CompletionException) && failure.getCause() != null) {
            failure = failure.getCause();
        }
        logger.log(Level.WARNING, "Exception during async tab completion for '" + buffer + "'", failure);
    }

    @EventHandler
    public void onPluginDisable(PluginDisableEvent event) {
        if (event.getPlugin() != plugin) {
            return;
        }
        active = false;
        labels = Set.of();
        for (Request request : pending) {
            request.result.complete(List.of());
        }
        pending.clear();
        routers.clear();
        BRIDGES.remove(plugin);
        HandlerList.unregisterAll(this);
    }

    private static final class Request {
        private final CompletableFuture<Collection<String>> result = new CompletableFuture<>();
        private volatile List<String> subCommands = List.of();

        private void complete(Collection<String> options, Throwable failure) {
            if (result.isDone()) {
                return;
            }
            if (failure != null) {
                result.completeExceptionally(failure);
            } else if (options == null) {
                result.completeExceptionally(new NullPointerException("Async tab completion returned a null collection"));
            } else {
                result.complete(options);
            }
        }
    }
}
