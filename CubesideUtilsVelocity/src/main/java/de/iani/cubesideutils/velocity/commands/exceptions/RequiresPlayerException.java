package de.iani.cubesideutils.velocity.commands.exceptions;

import com.velocitypowered.api.command.Command;
import com.velocitypowered.api.command.CommandSource;
import de.iani.cubesideutils.velocity.commands.CommandRouter;
import de.iani.cubesideutils.velocity.commands.SubCommand;

public class RequiresPlayerException extends SubCommandException {

    private static final long serialVersionUID = -4621194287775434508L;

    public RequiresPlayerException(CommandRouter router, CommandSource sender, Command command, String alias, SubCommand subCommand, String[] args, String message) {
        super(router, sender, command, alias, subCommand, args, message);
    }

    public RequiresPlayerException(CommandRouter router, CommandSource sender, Command command, String alias, SubCommand subCommand, String[] args) {
        this(router, sender, command, alias, subCommand, args, "This command can only be executed by players!");
    }

}
