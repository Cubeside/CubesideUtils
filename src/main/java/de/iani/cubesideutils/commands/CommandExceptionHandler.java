package de.iani.cubesideutils.commands;

import de.iani.cubesideutils.commands.exceptions.IllegalSyntaxException;
import de.iani.cubesideutils.commands.exceptions.InternalCommandException;
import de.iani.cubesideutils.commands.exceptions.NoPermissionException;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.command.CommandSender;

public interface CommandExceptionHandler {

    public default void handleInternalException(InternalCommandException thrown) {
        CommandSender sender = thrown.getSender();
        sender.sendMessage(ChatColor.RED + "An internal error occured.");

        Throwable cause = thrown.getCause();
        if (cause instanceof Error) {
            throw (Error) cause;
        } else if (cause instanceof RuntimeException) {
            throw (RuntimeException) cause;
        } else {
            throw new RuntimeException(cause);
        }
    }

    public default void handleNoPermission(NoPermissionException thrown) {
        CommandSender sender = thrown.getSender();
        sender.sendMessage(ChatColor.RED + thrown.getMessage());
    }

    public default void handleIllegalSyntax(IllegalSyntaxException thrown) {
        CommandSender sender = thrown.getSender();
        SubCommand subCommand = thrown.getSubCommand();
        sender.sendMessage(subCommand.getUsage(sender));
    }

}
