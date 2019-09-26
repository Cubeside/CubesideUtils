package de.iani.cubesideutils.commands;

import de.iani.cubesideutils.Pair;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map.Entry;
import org.bukkit.ChatColor;
import org.bukkit.command.BlockCommandSender;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.entity.minecart.CommandMinecart;
import org.bukkit.util.StringUtil;

public class CommandRouter extends AbstractCommandRouter<SubCommand> implements CommandExecutor, TabCompleter {

    public static final String UNKNOWN_COMMAND_MESSAGE = "Unknown command. Type \"/help\" for help.";

    public CommandRouter(PluginCommand command) {
        command.setExecutor(this);
        command.setTabCompleter(this);
    }

    public void addPluginCommand(PluginCommand command) {
        command.setExecutor(this);
        command.setTabCompleter(this);
    }

    // untested!
    @Override
    public SubCommand getSubCommand(String path) {
        String[] args = path.split(" ");
        return matchCommandMap(args).first.executor;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        Pair<AbstractCommandRouter<SubCommand>.CommandMap, Integer> commandMapAndArg = matchCommandMap(args, 1);
        CommandMap currentMap = commandMapAndArg.first;
        int nr = commandMapAndArg.second;

        String partial = args.length > 0 ? args[args.length - 1] : "";
        Collection<String> options = null;
        List<String> optionsList = null;
        // get tabcomplete options from command
        if (currentMap.executor != null) {
            options = Collections.emptyList();
            if (currentMap.executor.getRequiredPermission() == null || sender.hasPermission(currentMap.executor.getRequiredPermission())) {
                if (sender instanceof Player || !currentMap.executor.requiresPlayer()) {
                    options = currentMap.executor.onTabComplete(sender, command, alias, new ArgsParser(args, nr));
                }
            }
        }
        // get tabcomplete options from subcommands
        if (currentMap.subCommands != null) {
            for (Entry<String, CommandMap> e : currentMap.subCommands.entrySet()) {
                String key = e.getKey();
                if (StringUtil.startsWithIgnoreCase(key, partial)) {
                    CommandMap subcmd = e.getValue();
                    if (subcmd.executor == null || subcmd.executor.getRequiredPermission() == null || sender.hasPermission(subcmd.executor.getRequiredPermission())) {
                        if (sender instanceof Player || subcmd.executor == null || !subcmd.executor.requiresPlayer()) {
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
        if (options != null) {
            optionsList = StringUtil.copyPartialMatches(partial, options, new ArrayList<String>());
            Collections.sort(optionsList);
        }
        return optionsList;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String alias, String[] args) {
        Pair<AbstractCommandRouter<SubCommand>.CommandMap, Integer> commandMapAndArg = matchCommandMap(args);
        CommandMap currentMap = commandMapAndArg.first;
        int nr = commandMapAndArg.second;

        // execute this?
        SubCommand toExecute = currentMap.executor;
        if (toExecute != null) {
            if (toExecute.allowsCommandBlock() || !(sender instanceof BlockCommandSender || sender instanceof CommandMinecart)) {
                if (!toExecute.requiresPlayer() || sender instanceof Player) {
                    if ((toExecute.getRequiredPermission() == null || sender.hasPermission(toExecute.getRequiredPermission())) && toExecute.isAvailable(sender)) {
                        return toExecute.onCommand(sender, command, alias, getCommandString(alias, currentMap), new ArgsParser(args, nr));
                    } else {
                        sender.sendMessage(ChatColor.RED + "No permission!");
                    }
                } else {
                    sender.sendMessage(ChatColor.RED + "This command can only be executed by players!");
                }
            } else {
                sender.sendMessage(ChatColor.RED + "This command is not allowed for CommandBlocks!");
            }
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
                    sender.sendMessage(prefix + key + " ...");
                } else {
                    if ((subcmd.executor.getRequiredPermission() == null || sender.hasPermission(subcmd.executor.getRequiredPermission())) && subcmd.executor.isAvailable(sender)) {
                        if (sender instanceof Player || !subcmd.executor.requiresPlayer()) {
                            sender.sendMessage(prefix + key + " " + subcmd.executor.getUsage(sender));
                        }
                    }
                }
            }
        } else {

        }
    }
}
