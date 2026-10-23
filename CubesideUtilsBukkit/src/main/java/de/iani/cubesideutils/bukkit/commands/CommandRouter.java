package de.iani.cubesideutils.bukkit.commands;

import de.iani.cubesideutils.Pair;
import de.iani.cubesideutils.bukkit.commands.exceptions.DisallowsCommandBlockException;
import de.iani.cubesideutils.bukkit.commands.exceptions.IllegalSyntaxException;
import de.iani.cubesideutils.bukkit.commands.exceptions.InternalCommandException;
import de.iani.cubesideutils.bukkit.commands.exceptions.NoPermissionException;
import de.iani.cubesideutils.bukkit.commands.exceptions.NoPermissionForPathException;
import de.iani.cubesideutils.bukkit.commands.exceptions.RequiresPlayerException;
import de.iani.cubesideutils.commands.AbstractCommandRouter;
import de.iani.cubesideutils.commands.ArgsParser;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.CompletionStage;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.HoverEvent;
import org.bukkit.command.BlockCommandSender;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.entity.minecart.CommandMinecart;
import org.bukkit.plugin.Plugin;
import org.bukkit.util.StringUtil;

public class CommandRouter extends AbstractCommandRouter<SubCommand, CommandSender> implements CommandExecutor, TabCompleter {

    public static final String UNKNOWN_COMMAND_MESSAGE = "Unknown command. Type \"/help\" for help.";

    private CommandExceptionHandler exceptionHandler;

    public CommandRouter(PluginCommand command) {
        this(command, true);
    }

    public CommandRouter(PluginCommand command, boolean caseInsensitive) {
        this(command, caseInsensitive, CommandExceptionHandler.DEFAULT_HANDLER);
    }

    public CommandRouter(PluginCommand command, CommandExceptionHandler exceptionHandler) {
        this(command, true, exceptionHandler);
    }

    public CommandRouter(PluginCommand command, boolean caseInsensitive, CommandExceptionHandler exceptionHandler) {
        this(caseInsensitive, exceptionHandler);
        attachPluginCommand(command);
    }

    public CommandRouter() {
        this(true);
    }

    public CommandRouter(boolean caseInsensitive) {
        this(caseInsensitive, CommandExceptionHandler.DEFAULT_HANDLER);
    }

    public CommandRouter(CommandExceptionHandler exceptionHandler) {
        this(true, exceptionHandler);
    }

    public CommandRouter(boolean caseInsensitive, CommandExceptionHandler exceptionHandler) {
        super(caseInsensitive);

        this.exceptionHandler = Objects.requireNonNull(exceptionHandler);
    }

    public void addPluginCommand(PluginCommand command) {
        attachPluginCommand(command);
    }

    private void attachPluginCommand(PluginCommand command) {
        command.setExecutor(this);
        command.setTabCompleter(this);
        if (command.getPlugin().isEnabled()) {
            AsyncTabCompletionBridge.register(command.getPlugin(), command, this);
        }
    }

    /**
     * Registers Paper async completion for an already registered command without
     * changing its executor or tab completer. Call on the main thread while the
     * owning plugin is enabled. Repeating the registration also refreshes the
     * index of command labels and aliases without adding another listener.
     * <p>
     * Plugin-command constructors, {@link #addPluginCommand(PluginCommand)} and
     * {@link CommandUtil#registerCommand(Plugin, String, CommandRouter)} do this
     * automatically when the plugin is enabled. If the router was attached
     * earlier, call this method explicitly from {@code onEnable}.
     *
     * @param plugin the plugin owning the completion registration
     * @param command the command already present in Bukkit's command map
     */
    public void registerAsyncTabCompletion(Plugin plugin, Command command) {
        AsyncTabCompletionBridge.register(plugin, command, this);
    }

    // untested!
    public SubCommand getSubCommand(String path) {
        String[] args = path.split(" ");
        Pair<CommandMap, Integer> commandMapAndArg = matchCommandMap(null, args);
        CommandMap currentMap = commandMapAndArg.first;
        int nr = commandMapAndArg.second;
        return nr == args.length ? currentMap.executor : null;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        Pair<CommandMap, Integer> commandMapAndArg = matchCommandMap(sender, args, 1);
        CommandMap currentMap = commandMapAndArg.first;
        int nr = commandMapAndArg.second;

        String partial = args.length > 0 ? args[args.length - 1] : "";
        Collection<String> options = Collections.emptyList();
        // get tabcomplete options from command
        if (currentMap.executor != null) {
            if (currentMap.executor.isExecutable(sender)) {
                options = currentMap.executor.onTabComplete(sender, command, alias, new ArgsParser(args, nr));
            }
        }
        List<String> subCommandOptions = new ArrayList<>();
        collectSubCommandOptions(sender, currentMap, nr, args.length, partial, subCommandOptions);
        return finishTabCompletion(partial, options, subCommandOptions);
    }

    // Called only on the main thread. The caller retains already collected
    // subcommands if a permission hook or the completion provider throws.
    CompletionStage<? extends Collection<String>> prepareAsyncTabCompletion(CommandSender sender, Command command, String alias, String[] args, List<String> subCommandOptions) {
        Pair<CommandMap, Integer> commandMapAndArg = matchCommandMap(sender, args, 1);
        CommandMap currentMap = commandMapAndArg.first;
        int nr = commandMapAndArg.second;
        String partial = args.length > 0 ? args[args.length - 1] : "";
        collectSubCommandOptions(sender, currentMap, nr, args.length, partial, subCommandOptions);
        if (currentMap.executor != null && currentMap.executor.isExecutable(sender)) {
            return currentMap.executor.onTabCompleteAsync(sender, command, alias, new ArgsParser(args.clone(), nr));
        }
        return null;
    }

    private void collectSubCommandOptions(CommandSender sender, CommandMap currentMap, int nr, int argsLength, String partial, List<String> options) {
        // get tabcomplete options from subcommands
        if (nr == argsLength - 1 && currentMap.subCommands != null) {
            for (Entry<String, CommandMap> e : currentMap.subCommands.entrySet()) {
                String key = e.getKey();
                if (StringUtil.startsWithIgnoreCase(key, partial)) {
                    CommandMap subcmd = e.getValue();
                    if (isAnySubCommandDisplayable(sender, subcmd)) {
                        if (sender instanceof Player || subcmd.executor == null || !subcmd.executor.requiresPlayer()) {
                            options.add(key);
                        }
                    }
                }
            }
        }
    }

    static List<String> finishTabCompletion(String partial, Collection<String> options, List<String> subCommandOptions) {
        if (options == null && subCommandOptions.isEmpty()) {
            return null;
        }
        List<String> allOptions = options == null ? new ArrayList<>() : new ArrayList<>(options);
        allOptions.forEach(Objects::requireNonNull);
        allOptions.addAll(subCommandOptions);
        List<String> optionsList = StringUtil.copyPartialMatches(partial, allOptions, new ArrayList<>());
        Collections.sort(optionsList);
        return optionsList;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String alias, String[] args) {
        Pair<CommandMap, Integer> commandMapAndArg = matchCommandMap(sender, args);
        CommandMap currentMap = commandMapAndArg.first;
        int nr = commandMapAndArg.second;

        // execute this?
        SubCommand toExecute = currentMap.executor;
        if (toExecute != null) {
            try {
                if (!toExecute.allowsCommandBlock() && (sender instanceof BlockCommandSender || sender instanceof CommandMinecart)) {
                    throw new DisallowsCommandBlockException(this, sender, command, alias, toExecute, args);
                }
                if (toExecute.requiresPlayer() && !(sender instanceof Player)) {
                    throw new RequiresPlayerException(this, sender, command, alias, toExecute, args);
                }
                if (!toExecute.hasRequiredPermission(sender) || !toExecute.isAvailable(sender)) {
                    throw new NoPermissionException(this, sender, command, alias, toExecute, args, toExecute.getRequiredPermission());
                }

                if (toExecute.onCommand(sender, command, alias, getCommandString(alias, currentMap), new ArgsParser(args, nr))) {
                    return true;
                } else {
                    throw new IllegalSyntaxException(this, sender, command, alias, toExecute, args);
                }
            } catch (DisallowsCommandBlockException e) {
                return exceptionHandler.handleDisallowsCommandBlock(e);
            } catch (RequiresPlayerException e) {
                return exceptionHandler.handleRequiresPlayer(e);
            } catch (NoPermissionException e) {
                return exceptionHandler.handleNoPermission(e);
            } catch (IllegalSyntaxException e) {
                return exceptionHandler.handleIllegalSyntax(e);
            } catch (InternalCommandException e) {
                return exceptionHandler.handleInternalException(e);
            } catch (Throwable t) {
                return exceptionHandler.handleInternalException(new InternalCommandException(this, sender, command, alias, toExecute, args, t));
            }
        }

        if (!isAnySubCommandExecutable(sender, currentMap)) {
            return exceptionHandler.handleNoPermissionForPath(new NoPermissionForPathException(this, sender, command, alias, args));
        }
        // show valid cmds
        showHelp(sender, alias, currentMap);
        return true;
    }

    private String getCommandString(String alias, CommandMap currentMap) {
        StringBuilder prefixBuilder = new StringBuilder();
        prefixBuilder.append('/').append(alias).append(' ');
        ArrayList<CommandMap> hierarchy = new ArrayList<>();
        CommandMap map = currentMap;
        while (map != null) {
            hierarchy.add(map);
            map = map.parent;
        }
        for (int i = hierarchy.size() - 2; i >= 0; i--) {
            prefixBuilder.append(hierarchy.get(i).name).append(' ');
        }
        return prefixBuilder.toString();
    }

    public void showHelp(CommandSender sender, String alias, String[] args) {
        Pair<CommandMap, Integer> commandMapAndArg = matchCommandMap(sender, args);
        CommandMap currentMap = commandMapAndArg.first;
        showHelp(sender, alias, currentMap);
    }

    private void showHelp(CommandSender sender, String alias, CommandMap currentMap) {
        if (currentMap.subCommands != null) {
            String prefix = getCommandString(alias, currentMap);
            for (CommandMap subcmd : currentMap.subcommandsOrdered) {
                String key = subcmd.name;
                if (subcmd.executor == null) {
                    // hat weitere subcommands
                    if (isAnySubCommandDisplayable(sender, subcmd)) {
                        Component message = exceptionHandler.getHelpMessagePrefix();
                        Component syntax = Component.text(prefix + key + " ...");
                        message = message == null ? syntax : message.append(syntax);
                        sender.sendMessage(message);
                    }
                } else {
                    if (subcmd.executor.hasRequiredPermission(sender) && subcmd.executor.isAvailable(sender)) {
                        if (sender instanceof Player || !subcmd.executor.requiresPlayer()) {
                            Component message = exceptionHandler.getHelpMessagePrefix();
                            Component syntax = Component.text(prefix + key + " " + subcmd.executor.getUsage(sender));
                            Component description = subcmd.executor.getDescription();
                            if (description != null) {
                                syntax = syntax.hoverEvent(HoverEvent.showText(description));
                            }
                            message = message == null ? syntax : message.append(syntax);
                            sender.sendMessage(message);
                        }
                    }
                }
            }
        }
        if (currentMap.executor != null) {
            SubCommand executor = currentMap.executor;
            if (executor.hasRequiredPermission(sender) && executor.isAvailable(sender)) {
                String prefix = getCommandString(alias, currentMap);
                if (sender instanceof Player || !executor.requiresPlayer()) {
                    Component message = exceptionHandler.getHelpMessagePrefix();
                    Component syntax = Component.text(prefix + executor.getUsage(sender));
                    Component description = currentMap.executor.getDescription();
                    if (description != null) {
                        syntax = syntax.hoverEvent(HoverEvent.showText(description));
                    }
                    message = message == null ? syntax : message.append(syntax);
                    sender.sendMessage(message);
                }
            }
        }
    }

    private boolean isAnySubCommandExecutable(CommandSender sender, CommandMap cmd) {
        if (cmd.executor != null && cmd.executor.isExecutable(sender)) {
            return true;
        }
        if (cmd.subcommandsOrdered == null) {
            return false;
        }
        if (!hasAnyPermission(sender, cmd.requiredPermissions)) {
            return false;
        }
        for (CommandMap subcommand : cmd.subcommandsOrdered) {
            if (isAnySubCommandExecutable(sender, subcommand)) {
                return true;
            }
        }
        return false;
    }

    private boolean isAnySubCommandDisplayable(CommandSender sender, CommandMap cmd) {
        if (cmd.executor != null && cmd.executor.isDisplayable(sender)) {
            return true;
        }
        if (cmd.subcommandsOrdered == null) {
            return false;
        }
        if (!hasAnyPermission(sender, cmd.requiredPermissions)) {
            return false;
        }
        for (CommandMap subcommand : cmd.subcommandsOrdered) {
            if (isAnySubCommandDisplayable(sender, subcommand)) {
                return true;
            }
        }
        return false;
    }

    private boolean hasAnyPermission(CommandSender handler, Set<String> permissions) {
        if (permissions == null) {
            return true;
        }
        for (String permission : permissions) {
            if (handler.hasPermission(permission)) {
                return true;
            }
        }
        return false;
    }

}
