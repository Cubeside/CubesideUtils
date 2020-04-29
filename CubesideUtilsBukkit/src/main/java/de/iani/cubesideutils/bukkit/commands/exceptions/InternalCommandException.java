package de.iani.cubesideutils.bukkit.commands.exceptions;

import de.iani.cubesideutils.bukkit.commands.CommandRouter;
import de.iani.cubesideutils.bukkit.commands.SubCommand;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

public class InternalCommandException extends SubCommandException {

    private static final long serialVersionUID = -6856078921802113528L;

    public InternalCommandException(CommandRouter router, CommandSender sender, Command command, String alias, SubCommand subCommand, String[] args, String message, Throwable cause) {
        super(router, sender, command, alias, subCommand, args, message, cause);
    }

    public InternalCommandException(CommandRouter router, CommandSender sender, Command command, String alias, SubCommand subCommand, String[] args, Throwable cause) {
        this(router, sender, command, alias, subCommand, args, null, cause);
    }

}
