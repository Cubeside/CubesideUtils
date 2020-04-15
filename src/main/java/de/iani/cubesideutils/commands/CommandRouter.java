package de.iani.cubesideutils.commands;

import de.iani.cubesideutils.Pair;
import de.iani.cubesideutils.commands.exceptions.DisallowsCommandBlockException;
import de.iani.cubesideutils.commands.exceptions.IllegalSyntaxException;
import de.iani.cubesideutils.commands.exceptions.InternalCommandException;
import de.iani.cubesideutils.commands.exceptions.NoPermissionException;
import de.iani.cubesideutils.commands.exceptions.NoPermissionForPathException;
import de.iani.cubesideutils.commands.exceptions.RequiresPlayerException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import org.bukkit.command.BlockCommandSender;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.entity.minecart.CommandMinecart;
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

    public CommandRouter(PluginCommand command, boolean caseInsensitive, CommandExceptionHandler exceptionHandler) {
        super(caseInsensitive);
        command.setExecutor(this);
        command.setTabCompleter(this);

        this.exceptionHandler = exceptionHandler;
    }

    public void addPluginCommand(PluginCommand command) {
        command.setExecutor(this);
        command.setTabCompleter(this);
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
        Collection<String> options = null;
        List<String> optionsList = null;
        // get tabcomplete options from command
        if (currentMap.executor != null) {
            options = Collections.emptyList();
            if (currentMap.executor.hasRequiredPermission(sender)) {
                if (sender instanceof Player || !currentMap.executor.requiresPlayer()) {
                    options = currentMap.executor.onTabComplete(sender, command, alias, new ArgsParser(args, nr));
                }
            }
        } else {
            options = Collections.emptyList();
        }
        // get tabcomplete options from subcommands
        if (nr == args.length - 1 && currentMap.subCommands != null) {
            for (Entry<String, CommandMap> e : currentMap.subCommands.entrySet()) {
                String key = e.getKey();
                if (StringUtil.startsWithIgnoreCase(key, partial)) {
                    CommandMap subcmd = e.getValue();
                    if (hasAnyPermission(sender, subcmd.requiredPermissions)) {
                        if (sender instanceof Player || subcmd.executor == null || !subcmd.executor.requiresPlayer()) {
                            if (isAnySubCommandAvailable(sender, subcmd)) {
                                if (optionsList == null) {
                                    optionsList = options == null ? new ArrayList<>() : new ArrayList<>(options);
                                    options = optionsList;
                                }
                                optionsList.add(key);
                            }
                        }
                    }
                }
            }
        }
        if (options != null) {
            optionsList = StringUtil.copyPartialMatches(partial, options, new ArrayList<String>());
            Collections.sort(optionsList);
        }
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
                    throw new DisallowsCommandBlockException(sender, command, alias, toExecute, args);
                }
                if (toExecute.requiresPlayer() && !(sender instanceof Player)) {
                    throw new RequiresPlayerException(sender, command, alias, toExecute, args);
                }
                if (!toExecute.hasRequiredPermission(sender) || !toExecute.isAvailable(sender)) {
                    throw new NoPermissionException(sender, command, alias, toExecute, args, toExecute.getRequiredPermission());
                }

                if (toExecute.onCommand(sender, command, alias, getCommandString(alias, currentMap), new ArgsParser(args, nr))) {
                    return true;
                } else {
                    throw new IllegalSyntaxException(sender, command, alias, toExecute, args);
                }
            } catch (DisallowsCommandBlockException e) {
                return exceptionHandler.handleDisallowsCommandBlock(e);
            } catch (RequiresPlayerException e) {
                return exceptionHandler.handleRequiresPlayer(e);
            } catch (NoPermissionException e) {
                return exceptionHandler.handleNoPermission(e);
            } catch (IllegalSyntaxException e) {
                return exceptionHandler.handleIllegalSyntax(e);
            } catch (Throwable t) {
                return exceptionHandler.handleInternalException(new InternalCommandException(sender, command, alias, toExecute, args, t));
            }
        }

        if (!hasAnyPermission(sender, currentMap.requiredPermissions) || !isAnySubCommandAvailable(sender, currentMap)) {
            return exceptionHandler.handleNoPermissionForPath(new NoPermissionForPathException(sender, command, alias, args));
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

    private void showHelp(CommandSender sender, String alias, CommandMap currentMap) {
        if (currentMap.subCommands != null) {
            String prefix = getCommandString(alias, currentMap);
            for (CommandMap subcmd : currentMap.subcommandsOrdered) {
                String key = subcmd.name;
                if (subcmd.executor == null) {
                    // hat weitere subcommands
                    if (hasAnyPermission(sender, subcmd.requiredPermissions) && isAnySubCommandAvailable(sender, subcmd)) {
                        sender.sendMessage(prefix + key + " ...");
                    }
                } else {
                    if (subcmd.executor.hasRequiredPermission(sender) && subcmd.executor.isAvailable(sender)) {
                        if (sender instanceof Player || !subcmd.executor.requiresPlayer()) {
                            sender.sendMessage(prefix + key + " " + subcmd.executor.getUsage(sender));
                        }
                    }
                }
            }
        } else if (currentMap.executor != null) {
            SubCommand executor = currentMap.executor;
            if (executor.hasRequiredPermission(sender) && executor.isAvailable(sender)) {
                String prefix = getCommandString(alias, currentMap);
                if (sender instanceof Player || !executor.requiresPlayer()) {
                    sender.sendMessage(prefix + executor.getUsage(sender));
                }
            }
        }
    }

    private boolean isAnySubCommandAvailable(CommandSender sender, CommandMap cmd) {
        if (cmd.executor != null && cmd.executor.isAvailable(sender)) {
            return true;
        }
        if (cmd.subcommandsOrdered != null) {
            for (CommandMap subcommand : cmd.subcommandsOrdered) {
                if (isAnySubCommandAvailable(sender, subcommand)) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    protected boolean hasAnyPermission(CommandSender handler, Set<String> permissions) {
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
