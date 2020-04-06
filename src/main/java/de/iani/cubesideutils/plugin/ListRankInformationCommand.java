package de.iani.cubesideutils.plugin;

import de.iani.cubesideutils.ChatUtil;
import de.iani.cubesideutils.ChatUtil.Sendable;
import de.iani.cubesideutils.ChatUtil.StringMsg;
import de.iani.cubesideutils.StringUtil;
import de.iani.cubesideutils.commands.ArgsParser;
import de.iani.cubesideutils.commands.SubCommand;
import java.util.List;
import java.util.stream.Collectors;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

class ListRankInformationCommand extends SubCommand {

    final static String COMMAND_PATH = "list";
    final static String FULL_COMMAND = UtilsPlugin.RANKS_COMMAND + " " + COMMAND_PATH;

    ListRankInformationCommand() {

    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String alias, String commandString, ArgsParser args) {
        int page = 0;
        if (args.hasNext()) {
            page = args.getNext(0) - 1;
        }

        List<Sendable> msgs = UtilsPlugin.getInstance().getRanks().stream().map(rank -> {
            String permission = UtilsPlugin.getInstance().getPermission(rank);
            String prefix = UtilsPlugin.getInstance().getPrefix(rank);

            StringBuilder info = new StringBuilder(rank).append(": ");
            info.append("permission ").append(permission == null ? "-" : permission).append(", ");
            info.append("prefix ").append(StringUtil.revertColors(prefix));

            return new StringMsg(info.toString());
        }).collect(Collectors.toList());
        ChatUtil.sendMessagesPaged(sender, msgs, page, "Rank Information", FULL_COMMAND);
        return true;
    }

}
