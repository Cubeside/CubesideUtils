package de.iani.cubesideutils.commands.exceptions;

import de.iani.cubesideutils.commands.SubCommand;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

public class InternalCommandException extends SubCommandException {

    private static final long serialVersionUID = -6856078921802113528L;

    public InternalCommandException(CommandSender sender, Command command, String alias, SubCommand subCommand, String[] args, String message, Throwable cause) {
        super(sender, command, alias, subCommand, args, message, cause);
    }

    public InternalCommandException(CommandSender sender, Command command, String alias, SubCommand subCommand, String[] args, Throwable cause) {
        this(sender, command, alias, subCommand, args, null, cause);
    }

}
