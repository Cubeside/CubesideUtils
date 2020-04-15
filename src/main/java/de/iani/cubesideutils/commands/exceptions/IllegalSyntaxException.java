package de.iani.cubesideutils.commands.exceptions;

import de.iani.cubesideutils.commands.SubCommand;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

public class IllegalSyntaxException extends SubCommandException {

    private static final long serialVersionUID = -9098781538062386012L;

    public IllegalSyntaxException(CommandSender sender, Command command, String alias, SubCommand subCommand, String[] args, String message, Throwable cause) {
        super(sender, command, alias, subCommand, args, message, cause);
    }

    public IllegalSyntaxException(CommandSender sender, Command command, String alias, SubCommand subCommand, String[] args, String message) {
        super(sender, command, alias, subCommand, args, message);
    }

    public IllegalSyntaxException(CommandSender sender, Command command, String alias, SubCommand subCommand, String[] args, Throwable cause) {
        super(sender, command, alias, subCommand, args, cause);
    }

    public IllegalSyntaxException(CommandSender sender, Command command, String alias, SubCommand subCommand, String[] args) {
        super(sender, command, alias, subCommand, args);
    }

}
