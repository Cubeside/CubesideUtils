package de.iani.cubesideutils.velocity.commands;

import com.google.common.base.Preconditions;
import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.command.SimpleCommand;
import java.util.List;

public class CommandRouterCommand implements SimpleCommand {
    private final CommandRouter router;

    public CommandRouterCommand(CommandRouter router) {
        this.router = Preconditions.checkNotNull(router, "router");

    }

    @Override
    public boolean hasPermission(Invocation invocation) {
        return router.canExecuteAnySubCommand(invocation.source());
    }

    @Override
    public void execute(Invocation invocation) {
        CommandSource sender = invocation.source();
        String alias = invocation.alias();
        String[] args = invocation.arguments();

        router.onCommand(sender, this, alias, args);
    }

    @Override
    public List<String> suggest(Invocation invocation) {
        CommandSource sender = invocation.source();
        String alias = invocation.alias();
        String[] args = invocation.arguments();

        return router.onTabComplete(sender, this, alias, args);
    }
}
