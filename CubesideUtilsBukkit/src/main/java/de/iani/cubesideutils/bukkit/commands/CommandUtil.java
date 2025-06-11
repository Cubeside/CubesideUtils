package de.iani.cubesideutils.bukkit.commands;

import com.google.common.base.Preconditions;
import de.iani.cubesideutils.bukkit.events.CommandActionFlagsCheckEvent;
import de.iani.cubesideutils.bukkit.events.DetectCommandForLabelEvent;
import de.iani.cubesideutils.collections.IteratorUtil;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.PluginIdentifiableCommand;
import org.bukkit.command.SimpleCommandMap;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.PluginClassLoader;

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
        for (Player player : Bukkit.getServer().getOnlinePlayers()) {
            player.updateCommands();
        }
    }

    public static DynamicPluginCommand registerCommand(Plugin plugin, String command, CommandRouter commandRouter) {
        return registerCommand(plugin, command, commandRouter, false);
    }

    public static DynamicPluginCommand registerCommand(Plugin plugin, String command, CommandRouter commandRouter, boolean replaceExisting) {
        return registerCommand(plugin, command, Collections.emptyList(), commandRouter, replaceExisting);
    }

    public static DynamicPluginCommand registerCommand(Plugin plugin, String command, List<String> aliases, CommandRouter commandRouter) {
        return registerCommand(plugin, command, aliases, commandRouter, false);
    }

    public static DynamicPluginCommand registerCommand(Plugin plugin, String command, List<String> aliases, CommandRouter commandRouter, boolean replaceExisting) {
        return registerCommand(plugin, command, aliases, commandRouter, commandRouter, replaceExisting);
    }

    public static DynamicPluginCommand registerCommand(Plugin plugin, String command, List<String> aliases, CommandRouter commandRouter, String permission, boolean replaceExisting) {
        return registerCommand(plugin, command, aliases, commandRouter, commandRouter, permission, replaceExisting);
    }

    public static DynamicPluginCommand registerCommand(Plugin plugin, String command, HybridCommand hybridCommand) {
        return registerCommand(plugin, command, hybridCommand, false);
    }

    public static DynamicPluginCommand registerCommand(Plugin plugin, String command, HybridCommand hybridCommand, boolean replaceExisting) {
        return registerCommand(plugin, command, Collections.emptyList(), hybridCommand, replaceExisting);
    }

    public static DynamicPluginCommand registerCommand(Plugin plugin, String command, List<String> aliases, HybridCommand hybridCommand) {
        return registerCommand(plugin, command, aliases, hybridCommand, false);
    }

    public static DynamicPluginCommand registerCommand(Plugin plugin, String command, List<String> aliases, HybridCommand hybridCommand, boolean replaceExisting) {
        return registerCommand(plugin, command, aliases, hybridCommand, hybridCommand, hybridCommand.getRequiredPermission(), replaceExisting);
    }

    public static DynamicPluginCommand registerCommand(Plugin plugin, String command, CommandExecutor executor, TabCompleter completer) {
        return registerCommand(plugin, command, executor, completer, false);
    }

    public static DynamicPluginCommand registerCommand(Plugin plugin, String command, CommandExecutor executor, TabCompleter completer, boolean replaceExisting) {
        return registerCommand(plugin, command, Collections.emptyList(), executor, completer, replaceExisting);
    }

    public static DynamicPluginCommand registerCommand(Plugin plugin, String command, List<String> aliases, CommandExecutor executor, TabCompleter completer) {
        return registerCommand(plugin, command, aliases, executor, completer, false);
    }

    public static DynamicPluginCommand registerCommand(Plugin plugin, String command, List<String> aliases, CommandExecutor executor, TabCompleter completer, boolean replaceExisting) {
        return registerCommand(plugin, command, aliases, executor, completer, null, replaceExisting);
    }

    public static DynamicPluginCommand registerCommand(Plugin plugin, String command, List<String> aliases, CommandExecutor executor, TabCompleter completer, String permission, boolean replaceExisting) {
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

        DynamicPluginCommand cmd = new DynamicPluginCommand(plugin, command) {
            @Override
            public boolean execute(CommandSender sender, String commandLabel, String[] args) {
                return executor.onCommand(sender, this, commandLabel, args);
            }

            @Override
            public List<String> tabComplete(CommandSender sender, String alias, String[] args) throws IllegalArgumentException {
                return completer == null ? super.tabComplete(sender, alias, args) : completer.onTabComplete(sender, this, alias, args);
            }
        };
        cmd.setAliases(new ArrayList<>(aliases));
        if (permission != null) {
            cmd.setPermission(permission);
        }

        boolean result = commandMap.register(plugin.getPluginMeta().getName(), cmd);
        resyncCommandTabCompletions();

        return result ? cmd : null;
    }

    /**
     *
     * @param command
     *            a command, including the starting slash
     * @param forbiddenFlags
     *            a set of flags to test against
     * @return true if the given command will definitely not violate the forbiddenFlags, false otherwise
     */
    public static boolean isCommandSafe(String command, Set<CommandActionFlag> forbiddenFlags) {
        CommandActionFlagsCheckEvent event = new CommandActionFlagsCheckEvent(command);
        event.callEvent();
        for (CommandActionFlag f : forbiddenFlags) {
            if (event.getActionFlagUnknownToTrue(f)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Gets the command that is called with the given commandLine. the commandLine should include the beginning slash.
     *
     * @param commandLine
     *            the full command line as sent from a player or the console
     * @return the called command or null if no command is mapped
     */
    public static Command getCommandFromCommandLine(String commandLine) {
        String commandLineTrimmed = commandLine.substring(1).trim();
        int space = commandLineTrimmed.indexOf(" ");
        String mainCommand = (space < 0 ? commandLineTrimmed : commandLineTrimmed.substring(0, space));
        Command command = Bukkit.getCommandMap().getCommand(mainCommand);
        DetectCommandForLabelEvent event = new DetectCommandForLabelEvent(commandLine, mainCommand, command);
        event.callEvent();
        return event.getCommand();
    }

    /**
     * Gets the plugin that owns a command. may return null for builtin commands or when the plugin is not kown
     *
     * @param command
     *            a command
     * @return the plugin that owns the command if known, or null
     */
    public static Plugin getOwningPlugin(Command command) {
        if (command == null) {
            return null;
        }
        Plugin plugin = null;
        if (command instanceof PluginIdentifiableCommand pluginCommand) {
            plugin = pluginCommand.getPlugin();
        } else if (command instanceof PluginCommand pluginCommand) {
            plugin = pluginCommand.getPlugin();
        } else if (command instanceof DynamicPluginCommand pluginCommand) {
            plugin = pluginCommand.getPlugin();
        } else if (command.getClass().getClassLoader() instanceof PluginClassLoader pluginClassLoader && pluginClassLoader.getPlugin() != null) {
            plugin = pluginClassLoader.getPlugin();
        }
        return plugin;
    }
}
