package de.iani.cubesideutils.commands;

import de.iani.cubesideutils.commands.exceptions.DisallowsCommandBlockException;
import de.iani.cubesideutils.commands.exceptions.IllegalSyntaxException;
import de.iani.cubesideutils.commands.exceptions.InternalCommandException;
import de.iani.cubesideutils.commands.exceptions.NoPermissionException;
import de.iani.cubesideutils.commands.exceptions.NoPermissionForPathException;
import de.iani.cubesideutils.commands.exceptions.RequiresPlayerException;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.command.CommandSender;

public interface CommandExceptionHandler {

    public static final CommandExceptionHandler DEFAULT_HANDLER = new CommandExceptionHandler() {
    };

    public default boolean handleDisallowsCommandBlock(DisallowsCommandBlockException thrown) {
        CommandSender sender = thrown.getSender();
        sender.sendMessage(ChatColor.RED + thrown.getMessage());
        return true;
    }

    public default boolean handleRequiresPlayer(RequiresPlayerException thrown) {
        CommandSender sender = thrown.getSender();
        sender.sendMessage(ChatColor.RED + thrown.getMessage());
        return true;
    }

    public default boolean handleNoPermission(NoPermissionException thrown) {
        CommandSender sender = thrown.getSender();
        sender.sendMessage(ChatColor.RED + thrown.getMessage());
        return true;
    }

    public default boolean handleNoPermissionForPath(NoPermissionForPathException thrown) {
        CommandSender sender = thrown.getSender();
        sender.sendMessage(ChatColor.RED + thrown.getMessage());
        return true;
    }

    public default boolean handleIllegalSyntax(IllegalSyntaxException thrown) {
        CommandRouter router = thrown.getRouter();
        CommandSender sender = thrown.getSender();
        String alias = thrown.getAlias();
        String[] args = thrown.getArgs();
        router.showHelp(sender, alias, args);
        return true;
    }

    public default boolean handleInternalException(InternalCommandException thrown) {
        if (thrown.getMessage() != null) {
            CommandSender sender = thrown.getSender();
            sender.sendMessage(ChatColor.RED + thrown.getMessage());
        }

        Throwable cause = thrown.getCause();
        if (cause instanceof Error) {
            throw (Error) cause;
        } else if (cause instanceof RuntimeException) {
            throw (RuntimeException) cause;
        } else {
            throw new RuntimeException(cause);
        }
    }

}
