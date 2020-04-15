package de.iani.cubesideutils.commands.exceptions;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

public class NoPermissionForPathException extends CommandRouterException {

    private static final long serialVersionUID = 1295353884134111903L;

    public NoPermissionForPathException(CommandSender sender, Command command, String alias, String[] args, String message) {
        super(sender, command, alias, args, message);
    }

    public NoPermissionForPathException(CommandSender sender, Command command, String alias, String[] args) {
        this(sender, command, alias, args, "No permission!");
    }

}
