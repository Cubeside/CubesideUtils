package de.iani.cubesideutils.bungee.commands.exceptions;

import de.iani.cubesideutils.bungee.commands.CommandRouter;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.plugin.Command;

public class NoPermissionForPathException extends CommandRouterException {

    private static final long serialVersionUID = 1295353884134111903L;

    public NoPermissionForPathException(CommandRouter router, CommandSender sender, Command command, String alias, String[] args, String message) {
        super(router, sender, command, alias, args, message);
    }

    public NoPermissionForPathException(CommandRouter router, CommandSender sender, Command command, String alias, String[] args) {
        this(router, sender, command, alias, args, "No permission!");
    }

}
