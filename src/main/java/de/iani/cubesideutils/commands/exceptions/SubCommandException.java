package de.iani.cubesideutils.commands.exceptions;

import de.iani.cubesideutils.commands.SubCommand;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

public class SubCommandException extends CommandRouterException {

    private static final long serialVersionUID = -6734610669148837489L;

    private SubCommand subCommand;

    public SubCommandException(CommandSender sender, Command command, String alias, SubCommand subCommand, String[] args, String message, Throwable cause) {
        super(sender, command, alias, args, message, cause);
        this.subCommand = subCommand;
    }

    public SubCommandException(CommandSender sender, Command command, String alias, SubCommand subCommand, String[] args, String message) {
        super(sender, command, alias, args, message);
        this.subCommand = subCommand;
    }

    public SubCommandException(CommandSender sender, Command command, String alias, SubCommand subCommand, String[] args, Throwable cause) {
        super(sender, command, alias, args, cause);
        this.subCommand = subCommand;
    }

    public SubCommandException(CommandSender sender, Command command, String alias, SubCommand subCommand, String[] args) {
        super(sender, command, alias, args);
        this.subCommand = subCommand;
    }

    public SubCommand getSubCommand() {
        return subCommand;
    }

}
