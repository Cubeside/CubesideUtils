package de.iani.cubesideutils.velocity.commands.exceptions;

import com.velocitypowered.api.command.Command;
import com.velocitypowered.api.command.CommandSource;
import de.iani.cubesideutils.velocity.commands.CommandRouter;
import de.iani.cubesideutils.velocity.commands.SubCommand;

public class IllegalSyntaxException extends SubCommandException {

    private static final long serialVersionUID = -9098781538062386012L;

    public IllegalSyntaxException(CommandRouter router, CommandSource sender, Command command, String alias, SubCommand subCommand, String[] args, String message, Throwable cause) {
        super(router, sender, command, alias, subCommand, args, message, cause);
    }

    public IllegalSyntaxException(CommandRouter router, CommandSource sender, Command command, String alias, SubCommand subCommand, String[] args, String message) {
        super(router, sender, command, alias, subCommand, args, message);
    }

    public IllegalSyntaxException(CommandRouter router, CommandSource sender, Command command, String alias, SubCommand subCommand, String[] args, Throwable cause) {
        super(router, sender, command, alias, subCommand, args, cause);
    }

    public IllegalSyntaxException(CommandRouter router, CommandSource sender, Command command, String alias, SubCommand subCommand, String[] args) {
        super(router, sender, command, alias, subCommand, args);
    }

}
