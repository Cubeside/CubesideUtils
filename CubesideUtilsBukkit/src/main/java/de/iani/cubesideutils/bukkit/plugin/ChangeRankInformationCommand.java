package de.iani.cubesideutils.bukkit.plugin;

import de.iani.cubesideutils.StringUtil;
import de.iani.cubesideutils.bukkit.commands.SubCommand;
import de.iani.cubesideutils.commands.ArgsParser;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Collections;
import java.util.logging.Level;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

class ChangeRankInformationCommand extends SubCommand {

    static final String PERMISSION = "cubesideutils.changerankinformation";
    static final String SET_COMMAND_PATH = "set";
    static final String REMOVE_COMMAND_PATH = "remove";

    private final boolean set;

    ChangeRankInformationCommand(boolean set) {
        this.set = set;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String alias, String commandString, ArgsParser args) {
        if (!args.hasNext()) {
            return false;
        }
        String rank = args.next();

        if (!set) {
            try {
                if (CubesideUtilsBukkit.getInstance().removeRankInformation(rank)) {
                    sender.sendMessage(ChatColor.GREEN + "Rank removed.");
                    return true;
                } else {
                    sender.sendMessage(ChatColor.RED + "Unknown rank.");
                    return true;
                }
            } catch (SQLException e) {
                CubesideUtilsBukkit.getInstance().getLogger().log(Level.SEVERE, "Exception trying to remove rank information for rank " + rank + ".", e);
                sender.sendMessage(ChatColor.RED + "Ein interner Fehler ist aufgetreten.");
                return true;
            }
        }

        if (!args.hasNext()) {
            return false;
        }

        int priority;
        try {
            priority = Integer.parseInt(args.next());
        } catch (NumberFormatException e) {
            sender.sendMessage(ChatColor.RED + "Priority must be an integer.");
            return true;
        }

        if (!args.hasNext()) {
            return false;
        }

        String prefix = StringUtil.convertColors(args.getNext(""));
        String permission = args.next();
        permission = permission.equals("-") ? null : permission;

        try {
            CubesideUtilsBukkit.getInstance().setRankInformation(rank, priority, permission, prefix);
        } catch (SQLException e) {
            CubesideUtilsBukkit.getInstance().getLogger().log(Level.SEVERE, "Exception trying to set rank information for rank " + rank + ".", e);
            sender.sendMessage(ChatColor.RED + "Ein interner Fehler ist aufgetreten.");
            return true;
        }

        sender.sendMessage(ChatColor.GREEN + "Rank information set.");
        return true;
    }

    @Override
    public Collection<String> onTabComplete(CommandSender sender, Command command, String alias, ArgsParser args) {
        args.getNext(null);
        if (!args.hasNext()) {
            return CubesideUtilsBukkit.getInstance().getRanks();
        }
        return Collections.emptyList();
    }

    @Override
    public String getUsage() {
        return set ? "<rank> <piority> <permission | -> [prefix]" : "<rank>";
    }

    public String getPermission() {
        return PERMISSION;
    }

}
