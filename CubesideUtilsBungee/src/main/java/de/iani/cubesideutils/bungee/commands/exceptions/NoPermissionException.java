package de.iani.cubesideutils.bungee.commands.exceptions;

import de.iani.cubesideutils.bungee.commands.CommandRouter;
import de.iani.cubesideutils.bungee.commands.SubCommand;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.plugin.Command;

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
