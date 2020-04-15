package de.iani.cubesideutils.commands.exceptions;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

public abstract class CommandRouterException extends Exception {

    private static final long serialVersionUID = 3550234682652991485L;

    private CommandSender sender;
    private Command command;
    private String alias;
    private String[] args;

    public CommandRouterException(CommandSender sender, Command command, String alias, String[] args, String message, Throwable cause) {
        super(message, cause);
        init(sender, command, alias, args);
    }

    public CommandRouterException(CommandSender sender, Command command, String alias, String[] args, String message) {
        super(message);
        init(sender, command, alias, args);
    }

    public CommandRouterException(CommandSender sender, Command command, String alias, String[] args, Throwable cause) {
        super(cause);
        init(sender, command, alias, args);
    }

    public CommandRouterException(CommandSender sender, Command command, String alias, String[] args) {
        super();
        init(sender, command, alias, args);
    }

    private void init(CommandSender sender, Command command, String alias, String[] args) {
        this.sender = sender;
        this.command = command;
        this.alias = alias;
        this.args = args.clone();
    }

    public CommandSender getSender() {
        return sender;
    }

    public Command getCommand() {
        return command;
    }

    public String getAlias() {
        return alias;
    }

    public String[] getArgs() {
        return args.clone();
    }

}
