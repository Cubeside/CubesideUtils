package de.iani.cubesideutils.bukkit.commands.exceptions;

import de.iani.cubesideutils.bukkit.commands.CommandRouter;
import de.iani.cubesideutils.bukkit.commands.SubCommand;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

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
