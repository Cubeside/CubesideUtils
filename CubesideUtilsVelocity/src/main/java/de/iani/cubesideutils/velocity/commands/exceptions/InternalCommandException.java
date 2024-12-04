package de.iani.cubesideutils.velocity.commands.exceptions;

import com.velocitypowered.api.command.Command;
import com.velocitypowered.api.command.CommandSource;
import de.iani.cubesideutils.velocity.commands.CommandRouter;
import de.iani.cubesideutils.velocity.commands.SubCommand;

public class InternalCommandException extends SubCommandException {

    private static final long serialVersionUID = -6856078921802113528L;

    public InternalCommandException(CommandRouter router, CommandSource sender, Command command, String alias, SubCommand subCommand, String[] args, String message, Throwable cause) {
        super(router, sender, command, alias, subCommand, args, message, cause);
    }

    public InternalCommandException(CommandRouter router, CommandSource sender, Command command, String alias, SubCommand subCommand, String[] args, Throwable cause) {
        this(router, sender, command, alias, subCommand, args, null, cause);
    }

}
