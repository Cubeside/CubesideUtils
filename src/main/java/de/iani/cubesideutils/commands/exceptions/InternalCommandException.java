package de.iani.cubesideutils.commands.exceptions;

import de.iani.cubesideutils.commands.SubCommand;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

public class InternalCommandException extends SubCommandException {

    private static final long serialVersionUID = 3290684802913534615L;

    public InternalCommandException(CommandSender sender, Command command, String alias, SubCommand subCommand, String[] args, Throwable cause) {
        super(sender, command, alias, subCommand, args, cause);
    }

}
