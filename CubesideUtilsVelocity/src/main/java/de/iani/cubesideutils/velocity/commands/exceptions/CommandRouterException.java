package de.iani.cubesideutils.velocity.commands.exceptions;

import com.velocitypowered.api.command.Command;
import com.velocitypowered.api.command.CommandSource;

import java.util.Objects;

import de.iani.cubesideutils.velocity.commands.CommandRouter;

public abstract class CommandRouterException extends Exception {

    private static final long serialVersionUID = 3550234682652991485L;

    private CommandRouter router;
    private CommandSource sender;
    private Command command;
    private String alias;
    private String[] args;

    public CommandRouterException(CommandRouter router, CommandSource sender, Command command, String alias, String[] args, String message, Throwable cause) {
        super(message, cause);
        init(router, sender, command, alias, args);
    }

    public CommandRouterException(CommandRouter router, CommandSource sender, Command command, String alias, String[] args, String message) {
        super(message);
        init(router, sender, command, alias, args);
    }

    public CommandRouterException(CommandRouter router, CommandSource sender, Command command, String alias, String[] args, Throwable cause) {
        super(cause);
        init(router, sender, command, alias, args);
    }

    public CommandRouterException(CommandRouter router, CommandSource sender, Command command, String alias, String[] args) {
        super();
        init(router, sender, command, alias, args);
    }

    private void init(CommandRouter router, CommandSource sender, Command command, String alias, String[] args) {
        this.router = router;
        this.sender = Objects.requireNonNull(sender);
        this.command = Objects.requireNonNull(command);
        this.alias = Objects.requireNonNull(alias);
        this.args = args.clone();
    }

    public CommandRouter getRouter() {
        return router;
    }

    public CommandSource getSender() {
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
