package de.iani.cubesideutils.velocity.commands;

import com.velocitypowered.api.command.CommandSource;
import de.iani.cubesideutils.velocity.commands.exceptions.IllegalSyntaxException;
import de.iani.cubesideutils.velocity.commands.exceptions.InternalCommandException;
import de.iani.cubesideutils.velocity.commands.exceptions.NoPermissionException;
import de.iani.cubesideutils.velocity.commands.exceptions.NoPermissionForPathException;
import de.iani.cubesideutils.velocity.commands.exceptions.RequiresPlayerException;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

public interface CommandExceptionHandler {

    public static final CommandExceptionHandler DEFAULT_HANDLER = new CommandExceptionHandler() {
    };

    public default boolean handleRequiresPlayer(RequiresPlayerException thrown) {
        CommandSource sender = thrown.getSender();
        sender.sendMessage(Component.text(thrown.getMessage(), getErrorMessagePrefix()));
        return true;
    }

    public default boolean handleNoPermission(NoPermissionException thrown) {
        CommandSource sender = thrown.getSender();
        sender.sendMessage(Component.text(thrown.getMessage(), getErrorMessagePrefix()));
        return true;
    }

    public default boolean handleNoPermissionForPath(NoPermissionForPathException thrown) {
        CommandSource sender = thrown.getSender();
        sender.sendMessage(Component.text(thrown.getMessage(), getErrorMessagePrefix()));
        return true;
    }

    public default boolean handleIllegalSyntax(IllegalSyntaxException thrown) {
        CommandRouter router = thrown.getRouter();
        CommandSource sender = thrown.getSender();
        String alias = thrown.getAlias();
        String[] args = thrown.getArgs();
        router.showHelp(sender, alias, args);
        return true;
    }

    public default boolean handleInternalException(InternalCommandException thrown) {
        if (thrown.getMessage() != null) {
            CommandSource sender = thrown.getSender();
            sender.sendMessage(Component.text(thrown.getMessage(), getErrorMessagePrefix()));
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

    public default NamedTextColor getErrorMessagePrefix() {
        return NamedTextColor.RED;
    }

    public default Component getHelpMessagePrefix() {
        return Component.empty();
    }
}
