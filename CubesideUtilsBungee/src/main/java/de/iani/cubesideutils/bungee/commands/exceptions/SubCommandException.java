package de.iani.cubesideutils.bungee.commands.exceptions;

import de.iani.cubesideutils.bungee.commands.CommandRouter;
import de.iani.cubesideutils.bungee.commands.SubCommand;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.plugin.Command;

public class SubCommandException extends CommandRouterException {

    private static final long serialVersionUID = -6734610669148837489L;

    private SubCommand subCommand;

    public SubCommandException(CommandRouter router, CommandSender sender, Command command, String alias, SubCommand subCommand, String[] args, String message, Throwable cause) {
        super(router, sender, command, alias, args, message, cause);
        this.subCommand = subCommand;
    }

    public SubCommandException(CommandRouter router, CommandSender sender, Command command, String alias, SubCommand subCommand, String[] args, String message) {
        super(router, sender, command, alias, args, message);
        this.subCommand = subCommand;
    }

    public SubCommandException(CommandRouter router, CommandSender sender, Command command, String alias, SubCommand subCommand, String[] args, Throwable cause) {
        super(router, sender, command, alias, args, cause);
        this.subCommand = subCommand;
    }

    public SubCommandException(CommandRouter router, CommandSender sender, Command command, String alias, SubCommand subCommand, String[] args) {
        super(router, sender, command, alias, args);
        this.subCommand = subCommand;
    }

    public SubCommand getSubCommand() {
        return subCommand;
    }

}
