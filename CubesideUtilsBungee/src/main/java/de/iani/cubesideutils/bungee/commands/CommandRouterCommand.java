package de.iani.cubesideutils.bungee.commands;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.TabExecutor;

public class CommandRouterCommand extends Command implements TabExecutor {
    private CommandRouter router;

    public CommandRouterCommand(CommandRouter router, String name) {
        super(name);
        this.router = router;
    }

    public CommandRouterCommand(CommandRouter router, String name, String permission, String... aliases) {
        super(name, permission, aliases);
        this.router = router;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        router.onCommand(sender, this, getName(), args);
    }

    @Override
    public Iterable<String> onTabComplete(CommandSender sender, String[] args) {
        return router.onTabComplete(sender, this, getName(), args);
    }
}
