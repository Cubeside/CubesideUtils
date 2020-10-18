package de.iani.cubesideutils.bukkit.commands.exceptions;

import de.iani.cubesideutils.bukkit.commands.CommandRouter;
import de.iani.cubesideutils.bukkit.commands.SubCommand;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

public class NoPermissionException extends SubCommandException {

    private static final long serialVersionUID = 426296281527518966L;

    private String permission;

    public NoPermissionException(CommandRouter router, CommandSender sender, Command command, String alias, SubCommand subCommand, String[] args, String permission, String message) {
        super(router, sender, command, alias, subCommand, args, message);

        this.permission = permission;
    }

    public NoPermissionException(CommandRouter router, CommandSender sender, Command command, String alias, SubCommand subCommand, String[] args, String permission) {
        this(router, sender, command, alias, subCommand, args, permission, "No permission!");
    }

    public NoPermissionException(CommandRouter router, CommandSender sender, Command command, String alias, SubCommand subCommand, String[] args) {
        this(router, sender, command, alias, subCommand, args, null, "No permission!");
    }

    public String getPermission() {
        return permission;
    }

}
