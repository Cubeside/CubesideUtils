package de.iani.cubesideutils.velocity.commands.exceptions;

import com.velocitypowered.api.command.Command;
import com.velocitypowered.api.command.CommandSource;
import de.iani.cubesideutils.velocity.commands.CommandRouter;

public class NoPermissionForPathException extends CommandRouterException {

    private static final long serialVersionUID = 1295353884134111903L;

    public NoPermissionForPathException(CommandRouter router, CommandSource sender, Command command, String alias, String[] args, String message) {
        super(router, sender, command, alias, args, message);
    }

    public NoPermissionForPathException(CommandRouter router, CommandSource sender, Command command, String alias, String[] args) {
        this(router, sender, command, alias, args, "No permission!");
    }

}
