package de.iani.cubesideutils.velocity.commands;

import com.google.common.base.Preconditions;
import com.velocitypowered.api.command.Command;
import com.velocitypowered.api.command.CommandManager;
import com.velocitypowered.api.command.CommandMeta;
import java.util.List;

public class CommandUtil {
    private CommandUtil() {
        throw new UnsupportedOperationException();
    }

    public static void registerCommand(Object plugin, CommandManager commandManager, String commandName, Command command) {
        registerCommand(plugin, commandManager, commandName, List.of(), command);
    }

    public static void registerCommand(Object plugin, CommandManager commandManager, String commandName, List<String> aliases, Command command) {
        Preconditions.checkNotNull(plugin, "plugin");
        Preconditions.checkNotNull(commandManager, "commandManager");
        Preconditions.checkNotNull(commandName, "command");
        Preconditions.checkNotNull(aliases, "aliases");
        Preconditions.checkNotNull(command, "command");

        CommandMeta commandMeta = commandManager.metaBuilder(commandName).aliases(aliases.toArray(String[]::new)).plugin(plugin).build();
        commandManager.register(commandMeta, command);
    }
}
