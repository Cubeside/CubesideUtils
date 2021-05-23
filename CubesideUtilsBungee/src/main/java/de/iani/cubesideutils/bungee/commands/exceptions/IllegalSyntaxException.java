package de.iani.cubesideutils.bungee.commands.exceptions;

import de.iani.cubesideutils.bungee.commands.CommandRouter;
import de.iani.cubesideutils.bungee.commands.SubCommand;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.plugin.Command;

public class IllegalSyntaxException extends SubCommandException {

    private static final long serialVersionUID = -9098781538062386012L;

    public IllegalSyntaxException(CommandRouter router, CommandSender sender, Command command, String alias, SubCommand subCommand, String[] args, String message, Throwable cause) {
        super(router, sender, command, alias, subCommand, args, message, cause);
    }

    public IllegalSyntaxException(CommandRouter router, CommandSender sender, Command command, String alias, SubCommand subCommand, String[] args, String message) {
        super(router, sender, command, alias, subCommand, args, message);
    }

    public IllegalSyntaxException(CommandRouter router, CommandSender sender, Command command, String alias, SubCommand subCommand, String[] args, Throwable cause) {
        super(router, sender, command, alias, subCommand, args, cause);
    }

    public IllegalSyntaxException(CommandRouter router, CommandSender sender, Command command, String alias, SubCommand subCommand, String[] args) {
        super(router, sender, command, alias, subCommand, args);
    }

}
