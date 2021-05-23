package de.iani.cubesideutils.bungee.commands.exceptions;

import de.iani.cubesideutils.bungee.commands.CommandRouter;
import java.util.Objects;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.plugin.Command;

public abstract class CommandRouterException extends Exception {

    private static final long serialVersionUID = 3550234682652991485L;

    private CommandRouter router;
    private CommandSender sender;
    private Command command;
    private String alias;
    private String[] args;

    public CommandRouterException(CommandRouter router, CommandSender sender, Command command, String alias, String[] args, String message, Throwable cause) {
        super(message, cause);
        init(router, sender, command, alias, args);
    }

    public CommandRouterException(CommandRouter router, CommandSender sender, Command command, String alias, String[] args, String message) {
        super(message);
        init(router, sender, command, alias, args);
    }

    public CommandRouterException(CommandRouter router, CommandSender sender, Command command, String alias, String[] args, Throwable cause) {
        super(cause);
        init(router, sender, command, alias, args);
    }

    public CommandRouterException(CommandRouter router, CommandSender sender, Command command, String alias, String[] args) {
        super();
        init(router, sender, command, alias, args);
    }

    private void init(CommandRouter router, CommandSender sender, Command command, String alias, String[] args) {
        this.router = router;
        this.sender = Objects.requireNonNull(sender);
        this.command = Objects.requireNonNull(command);
        this.alias = Objects.requireNonNull(alias);
        this.args = args.clone();
    }

    public CommandRouter getRouter() {
        return router;
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
