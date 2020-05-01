package de.iani.cubesideutils.bukkit.plugin;

import de.iani.cubesideutils.StringUtil;
import de.iani.cubesideutils.bukkit.BukkitChatUtil;
import de.iani.cubesideutils.bukkit.BukkitChatUtil.BukkitSendable;
import de.iani.cubesideutils.bukkit.BukkitChatUtil.StringMsg;
import de.iani.cubesideutils.bukkit.commands.SubCommand;
import de.iani.cubesideutils.commands.ArgsParser;
import java.util.List;
import java.util.stream.Collectors;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

class ListRankInformationCommand extends SubCommand {

    final static String COMMAND_PATH = "list";
    final static String FULL_COMMAND = CubesideUtilsBukkit.RANKS_COMMAND + " " + COMMAND_PATH;

    ListRankInformationCommand() {

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
        BukkitChatUtil.sendMessagesPaged(sender, msgs, page, "Rank Information", FULL_COMMAND);
        return true;
    }

}
