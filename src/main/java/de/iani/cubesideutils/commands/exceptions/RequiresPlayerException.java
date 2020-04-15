package de.iani.cubesideutils.commands.exceptions;

import de.iani.cubesideutils.commands.SubCommand;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

public class RequiresPlayerException extends SubCommandException {

    private static final long serialVersionUID = -4621194287775434508L;

    public RequiresPlayerException(CommandSender sender, Command command, String alias, SubCommand subCommand, String[] args, String message) {
        super(sender, command, alias, subCommand, args, message);
    }

    public RequiresPlayerException(CommandSender sender, Command command, String alias, SubCommand subCommand, String[] args) {
        this(sender, command, alias, subCommand, args, "This command can only be executed by players!");
    }

}
