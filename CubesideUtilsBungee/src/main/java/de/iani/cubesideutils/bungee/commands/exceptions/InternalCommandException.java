package de.iani.cubesideutils.bungee.commands.exceptions;

import de.iani.cubesideutils.bungee.commands.CommandRouter;
import de.iani.cubesideutils.bungee.commands.SubCommand;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.plugin.Command;

public class InternalCommandException extends SubCommandException {

    private static final long serialVersionUID = -6856078921802113528L;

    public InternalCommandException(CommandRouter router, CommandSender sender, Command command, String alias, SubCommand subCommand, String[] args, String message, Throwable cause) {
        super(router, sender, command, alias, subCommand, args, message, cause);
    }

    public InternalCommandException(CommandRouter router, CommandSender sender, Command command, String alias, SubCommand subCommand, String[] args, Throwable cause) {
        this(router, sender, command, alias, subCommand, args, null, cause);
    }

}
