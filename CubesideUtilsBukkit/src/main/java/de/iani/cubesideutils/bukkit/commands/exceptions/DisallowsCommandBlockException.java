package de.iani.cubesideutils.bukkit.commands.exceptions;

import de.iani.cubesideutils.bukkit.commands.CommandRouter;
import de.iani.cubesideutils.bukkit.commands.SubCommand;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

public class DisallowsCommandBlockException extends SubCommandException {

    private static final long serialVersionUID = -679571981171996226L;

    public DisallowsCommandBlockException(CommandRouter router, CommandSender sender, Command command, String alias, SubCommand subCommand, String[] args, String message) {
        super(router, sender, command, alias, subCommand, args, message);
    }

    public DisallowsCommandBlockException(CommandRouter router, CommandSender sender, Command command, String alias, SubCommand subCommand, String[] args) {
        this(router, sender, command, alias, subCommand, args, "This command is not allowed for CommandBlocks!");
    }

}
