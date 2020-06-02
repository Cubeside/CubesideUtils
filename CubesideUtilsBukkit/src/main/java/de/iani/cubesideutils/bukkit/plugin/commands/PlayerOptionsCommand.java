package de.iani.cubesideutils.bukkit.plugin.commands;

import de.iani.cubesideutils.bukkit.commands.SubCommand;
import de.iani.cubesideutils.bukkit.plugin.CubesideUtilsBukkit;
import de.iani.cubesideutils.commands.ArgsParser;
import java.util.UUID;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

public class PlayerOptionsCommand extends SubCommand {

    private CubesideUtilsBukkit cubesideUtilsBukkit;

    public PlayerOptionsCommand(CubesideUtilsBukkit cubesideUtilsBukkit) {
        this.cubesideUtilsBukkit = cubesideUtilsBukkit;
    }

    @Override
    public String getUsage() {
        return "<playerid>";
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String alias, String commandString, ArgsParser args) {
        if (args.remaining() != 1) {
            return false;
        }
        UUID playerid;
        try {
            playerid = UUID.fromString(args.getNext());
        } catch (IllegalArgumentException e) {
            sender.sendMessage("The playerid must be a valid UUID.");
            return true;
        }
        cubesideUtilsBukkit.sendPlayerOptions(sender, cubesideUtilsBukkit.getPlugin().getServer().getOfflinePlayer(playerid));
        return true;
    }

}
