package de.iani.cubesideutils.velocity.commands.exceptions;

import com.velocitypowered.api.command.Command;
import com.velocitypowered.api.command.CommandSource;
import de.iani.cubesideutils.velocity.commands.CommandRouter;
import de.iani.cubesideutils.velocity.commands.SubCommand;

public class SubCommandException extends CommandRouterException {

    private static final long serialVersionUID = -6734610669148837489L;

    private SubCommand subCommand;

    public SubCommandException(CommandRouter router, CommandSource sender, Command command, String alias, SubCommand subCommand, String[] args, String message, Throwable cause) {
        super(router, sender, command, alias, args, message, cause);
        this.subCommand = subCommand;
    }

    public SubCommandException(CommandRouter router, CommandSource sender, Command command, String alias, SubCommand subCommand, String[] args, String message) {
        super(router, sender, command, alias, args, message);
        this.subCommand = subCommand;
    }

    public SubCommandException(CommandRouter router, CommandSource sender, Command command, String alias, SubCommand subCommand, String[] args, Throwable cause) {
        super(router, sender, command, alias, args, cause);
        this.subCommand = subCommand;
    }

    public SubCommandException(CommandRouter router, CommandSource sender, Command command, String alias, SubCommand subCommand, String[] args) {
        super(router, sender, command, alias, args);
        this.subCommand = subCommand;
    }

    public SubCommand getSubCommand() {
        return subCommand;
    }

}
