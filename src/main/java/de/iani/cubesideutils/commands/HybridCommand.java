package de.iani.cubesideutils.commands;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.util.StringUtil;

public abstract class HybridCommand extends SubCommand implements CommandExecutor, TabCompleter {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String alias, String[] args) {
        if (this.getRequiredPermission() != null && !sender.hasPermission(getRequiredPermission())) {
            sender.sendMessage(ChatColor.RED + "No permission!");
            return true;
        }
        return onCommand(sender, command, alias, "/" + alias, new ArgsParser(args));
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (this.getRequiredPermission() != null && !sender.hasPermission(getRequiredPermission())) {
            return Collections.emptyList();
        }
        Collection<String> options = onTabComplete(sender, command, alias, new ArgsParser(args));
        if (options == null) {
            return null;
        }

        List<String> result = StringUtil.copyPartialMatches(args.length > 0 ? args[args.length - 1] : "", options, new ArrayList<String>());
        Collections.sort(result);
        return result;
    }

}
