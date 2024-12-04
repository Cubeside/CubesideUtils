package de.iani.cubesideutils.velocity.commands.exceptions;

import com.velocitypowered.api.command.Command;
import com.velocitypowered.api.command.CommandSource;
import de.iani.cubesideutils.velocity.commands.CommandRouter;
import de.iani.cubesideutils.velocity.commands.SubCommand;

public class NoPermissionException extends SubCommandException {

    private static final long serialVersionUID = 426296281527518966L;

    private String permission;

    public NoPermissionException(CommandRouter router, CommandSource sender, Command command, String alias, SubCommand subCommand, String[] args, String permission, String message) {
        super(router, sender, command, alias, subCommand, args, message);

        this.permission = permission;
    }

    public NoPermissionException(CommandRouter router, CommandSource sender, Command command, String alias, SubCommand subCommand, String[] args, String permission) {
        this(router, sender, command, alias, subCommand, args, permission, "No permission!");
    }

    public NoPermissionException(CommandRouter router, CommandSource sender, Command command, String alias, SubCommand subCommand, String[] args) {
        this(router, sender, command, alias, subCommand, args, null, "No permission!");
    }

    public String getPermission() {
        return permission;
    }

}
