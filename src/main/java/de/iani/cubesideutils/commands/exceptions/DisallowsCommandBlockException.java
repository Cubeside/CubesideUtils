package de.iani.cubesideutils.commands.exceptions;

import de.iani.cubesideutils.commands.SubCommand;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

public class DisallowsCommandBlockException extends SubCommandException {

    private static final long serialVersionUID = -679571981171996226L;

    public DisallowsCommandBlockException(CommandSender sender, Command command, String alias, SubCommand subCommand, String[] args, String message) {
        super(sender, command, alias, subCommand, args, message);
    }

    public DisallowsCommandBlockException(CommandSender sender, Command command, String alias, SubCommand subCommand, String[] args) {
        this(sender, command, alias, subCommand, args, "This command is not allowed for CommandBlocks!");
    }

}
