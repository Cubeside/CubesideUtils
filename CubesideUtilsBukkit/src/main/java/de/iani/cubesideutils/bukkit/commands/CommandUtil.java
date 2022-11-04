package de.iani.cubesideutils.bukkit.commands;

import com.google.common.base.Preconditions;
import de.iani.cubesideutils.collections.IteratorUtil;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.SimpleCommandMap;
import org.bukkit.command.TabCompleter;
import org.bukkit.plugin.Plugin;

public class CommandUtil {
    private CommandUtil() {
        throw new UnsupportedOperationException("No instance for you, Sir!");
        // prevents instances
    }

    public static final TabCompleter EMPTY_TAB_COMPLETER = (sender, command, alias, args) -> Collections.emptyList();

    /**
     * This method has to be called after commands are added / removed in the servers command map.
     * It recyncs the tab completions for the commands.
     */
    public static void resyncCommandTabCompletions() {
        Server server = Bukkit.getServer();
        try {
            Method syncCommandsMethod = server.getClass().getDeclaredMethod("syncCommands");
            syncCommandsMethod.setAccessible(true);
            syncCommandsMethod.invoke(server);
        } catch (Exception e) {
            server.getLogger().log(Level.SEVERE, "Could not resync commands", e);
        }
    }

    public static boolean registerCommand(Plugin plugin, String command, CommandRouter commandRouter) {
        return registerCommand(plugin, command, commandRouter, false);
    }

    public static boolean registerCommand(Plugin plugin, String command, CommandRouter commandRouter, boolean replaceExisting) {
        return registerCommand(plugin, command, Collections.emptyList(), commandRouter, replaceExisting);
    }

    public static boolean registerCommand(Plugin plugin, String command, List<String> aliases, CommandRouter commandRouter) {
        return registerCommand(plugin, command, aliases, commandRouter, false);
    }

    public static boolean registerCommand(Plugin plugin, String command, List<String> aliases, CommandRouter commandRouter, boolean replaceExisting) {
        return registerCommand(plugin, command, aliases, commandRouter, commandRouter, replaceExisting);
    }

    public static boolean registerCommand(Plugin plugin, String command, List<String> aliases, CommandRouter commandRouter, String permission, boolean replaceExisting) {
        return registerCommand(plugin, command, aliases, commandRouter, commandRouter, permission, replaceExisting);
    }

    public static boolean registerCommand(Plugin plugin, String command, HybridCommand hybridCommand) {
        return registerCommand(plugin, command, hybridCommand, false);
    }

    public static boolean registerCommand(Plugin plugin, String command, HybridCommand hybridCommand, boolean replaceExisting) {
        return registerCommand(plugin, command, Collections.emptyList(), hybridCommand, replaceExisting);
    }

    public static boolean registerCommand(Plugin plugin, String command, List<String> aliases, HybridCommand hybridCommand) {
        return registerCommand(plugin, command, aliases, hybridCommand, false);
    }

    public static boolean registerCommand(Plugin plugin, String command, List<String> aliases, HybridCommand hybridCommand, boolean replaceExisting) {
        return registerCommand(plugin, command, aliases, hybridCommand, hybridCommand, replaceExisting);
    }

    public static boolean registerCommand(Plugin plugin, String command, CommandExecutor executor, TabCompleter completer) {
        return registerCommand(plugin, command, executor, completer, false);
    }

    public static boolean registerCommand(Plugin plugin, String command, CommandExecutor executor, TabCompleter completer, boolean replaceExisting) {
        return registerCommand(plugin, command, Collections.emptyList(), executor, completer, replaceExisting);
    }

    public static boolean registerCommand(Plugin plugin, String command, List<String> aliases, CommandExecutor executor, TabCompleter completer) {
        return registerCommand(plugin, command, aliases, executor, completer, false);
    }

    public static boolean registerCommand(Plugin plugin, String command, List<String> aliases, CommandExecutor executor, TabCompleter completer, boolean replaceExisting) {
        return registerCommand(plugin, command, aliases, executor, completer, null, replaceExisting);
    }

    public static boolean registerCommand(Plugin plugin, String command, List<String> aliases, CommandExecutor executor, TabCompleter completer, String permission, boolean replaceExisting) {
        Preconditions.checkNotNull(plugin, "plugin");
        Preconditions.checkNotNull(command, "command");
        Preconditions.checkNotNull(aliases, "aliases");
        Preconditions.checkNotNull(executor, "executor");

        SimpleCommandMap commandMap = (SimpleCommandMap) Bukkit.getCommandMap();
        if (replaceExisting) {
            for (String alias : IteratorUtil.concat(Collections.singleton(command), aliases)) {
                if (commandMap.getKnownCommands().remove(alias) != null) {
                    plugin.getLogger().log(Level.INFO, "Replacing command /" + alias + ".");
                }
            }
        }

        Command cmd = new DynamicPluginCommand(plugin, command) {
            @Override
            public boolean execute(CommandSender sender, String commandLabel, String[] args) {
                return executor.onCommand(sender, this, commandLabel, args);
            }

            @Override
            public List<String> tabComplete(CommandSender sender, String alias, String[] args) throws IllegalArgumentException {
                return completer == null ? super.tabComplete(sender, alias, args) : completer.onTabComplete(sender, this, alias, args);
            }
        }.setAliases(new ArrayList<>(aliases));
        if (permission != null) {
            cmd.setPermission(permission);
        }

        boolean result = commandMap.register(plugin.getDescription().getName(), cmd);
        resyncCommandTabCompletions();

        return result;
    }
}
