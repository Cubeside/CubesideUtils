package de.iani.cubesideutils.commands;

import java.util.Collection;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

public abstract class SubCommand {
    public boolean requiresPlayer() {
        return false;
    }

    public boolean allowsCommandBlock() {
        return false;
    }

    public String getRequiredPermission() {
        return null;
    }

    public boolean isAvailable(CommandSender sender) {
        return true;
    }

    public abstract boolean onCommand(CommandSender sender, Command command, String alias, String commandString, ArgsParser args);

    public Collection<String> onTabComplete(CommandSender sender, Command command, String alias, ArgsParser args) {
        return null;
    }

    public String getUsage() {
        return "";
    }
}
