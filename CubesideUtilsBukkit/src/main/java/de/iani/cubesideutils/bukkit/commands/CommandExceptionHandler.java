package de.iani.cubesideutils.bukkit.commands;

import de.iani.cubesideutils.bukkit.commands.exceptions.CommandRouterException;
import de.iani.cubesideutils.bukkit.commands.exceptions.DisallowsCommandBlockException;
import de.iani.cubesideutils.bukkit.commands.exceptions.IllegalSyntaxException;
import de.iani.cubesideutils.bukkit.commands.exceptions.InternalCommandException;
import de.iani.cubesideutils.bukkit.commands.exceptions.NoPermissionException;
import de.iani.cubesideutils.bukkit.commands.exceptions.NoPermissionForPathException;
import de.iani.cubesideutils.bukkit.commands.exceptions.RequiresPlayerException;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.CommandSender;

public interface CommandExceptionHandler {

    public static final CommandExceptionHandler DEFAULT_HANDLER = new CommandExceptionHandler() {};

    public default boolean handleDisallowsCommandBlock(DisallowsCommandBlockException thrown) {
        sendExceptionMessage(thrown);
        return true;
    }

    public default boolean handleRequiresPlayer(RequiresPlayerException thrown) {
        sendExceptionMessage(thrown);
        return true;
    }

    public default boolean handleNoPermission(NoPermissionException thrown) {
        sendExceptionMessage(thrown);
        return true;
    }

    public default boolean handleNoPermissionForPath(NoPermissionForPathException thrown) {
        sendExceptionMessage(thrown);
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
            sendExceptionMessage(thrown);
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

    public default void sendExceptionMessage(CommandRouterException thrown) {
        CommandSender sender = thrown.getSender();
        Component prefix = getErrorMessagePrefix();
        Component message = Component.text(thrown.getMessage());
        sender.sendMessage(prefix == null ? message : prefix.append(message));
    }

    public default Component getErrorMessagePrefix() {
        return Component.empty().color(NamedTextColor.RED);
    }

    public default Component getHelpMessagePrefix() {
        return null;
    }
}
