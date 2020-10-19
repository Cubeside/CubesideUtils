package de.iani.cubesideutils.bukkit.plugin.commands;

import de.iani.cubesideutils.bukkit.commands.SubCommand;
import de.iani.cubesideutils.bukkit.plugin.CubesideUtilsBukkit;
import de.iani.cubesideutils.bukkit.plugin.api.PlayerDataBukkit;
import de.iani.cubesideutils.commands.ArgsParser;
import java.util.UUID;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

public class PlayerOptionsCommand extends SubCommand {

    public static final String COMMAND = "playeroptions";

    private CubesideUtilsBukkit cubesideUtilsBukkit;

    public PlayerOptionsCommand(CubesideUtilsBukkit cubesideUtilsBukkit) {
        this.cubesideUtilsBukkit = cubesideUtilsBukkit;
    }

    @Override
    public String getUsage() {
        return "<playerId>";
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String alias, String commandString, ArgsParser args) {
        if (args.remaining() != 1) {
            return false;
        }
        UUID playerId;
        try {
            playerId = UUID.fromString(args.getNext());
        } catch (IllegalArgumentException e) {
            sender.sendMessage("The playerid must be a valid UUID.");
            return true;
        }
        PlayerDataBukkit data = cubesideUtilsBukkit.getPlayerData(playerId);
        cubesideUtilsBukkit.sendPlayerOptions(sender, data.getOfflinePlayer());
        return true;
    }

}
