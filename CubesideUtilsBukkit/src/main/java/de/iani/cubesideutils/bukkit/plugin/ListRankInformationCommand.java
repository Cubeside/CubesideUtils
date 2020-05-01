package de.iani.cubesideutils.bukkit.plugin;

import de.iani.cubesideutils.StringUtil;
import de.iani.cubesideutils.bukkit.ChatUtilBukkit;
import de.iani.cubesideutils.bukkit.ChatUtilBukkit.BukkitSendable;
import de.iani.cubesideutils.bukkit.ChatUtilBukkit.StringMsg;
import de.iani.cubesideutils.bukkit.commands.SubCommand;
import de.iani.cubesideutils.commands.ArgsParser;
import java.util.List;
import java.util.stream.Collectors;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

public class ListRankInformationCommand extends SubCommand {

    public static final String COMMAND_PATH = "list";
    public static final String FULL_COMMAND = CubesideUtilsBukkit.RANKS_COMMAND + " " + COMMAND_PATH;

    public ListRankInformationCommand() {

    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String alias, String commandString, ArgsParser args) {
        int page = 0;
        if (args.hasNext()) {
            page = args.getNext(0) - 1;
        }

        List<BukkitSendable> msgs = CubesideUtilsBukkit.getInstance().getRanks().stream().map(rank -> {
            int priority = CubesideUtilsBukkit.getInstance().getPriority(rank);
            String permission = CubesideUtilsBukkit.getInstance().getPermission(rank);
            String prefix = CubesideUtilsBukkit.getInstance().getPrefix(rank);

            StringBuilder info = new StringBuilder(rank).append(": ");
            info.append("priority ").append(priority).append(", ");
            info.append("permission ").append(permission == null ? "-" : permission).append(", ");
            info.append("prefix ").append(StringUtil.revertColors(prefix));

            return new StringMsg(info.toString());
        }).collect(Collectors.toList());
        ChatUtilBukkit.sendMessagesPaged(sender, msgs, page, "Rank Information", FULL_COMMAND);
        return true;
    }

}
