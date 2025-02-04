package de.iani.cubesideutils.bukkit.events;

import com.google.common.base.Preconditions;
import de.iani.cubesideutils.bukkit.commands.CommandActionFlag;
import de.iani.cubesideutils.bukkit.commands.CommandUtil;
import de.iani.cubesideutils.commands.ArgsParser;
import java.util.Arrays;
import java.util.EnumMap;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.kyori.adventure.util.TriState;
import org.bukkit.command.Command;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.Plugin;

public class CommandActionFlagsCheckEvent extends Event {
    private static final HandlerList handlers = new HandlerList();

    private final String commandLine;
    private String[] commandArgs;
    private final EnumMap<CommandActionFlag, TriState> actionFlags;
    private final @Nullable Command command;
    private final @Nullable Plugin plugin;

    public CommandActionFlagsCheckEvent(@Nonnull String commandLine) {
        this.commandLine = commandLine;
        this.command = CommandUtil.getCommandFromCommandLine(commandLine);
        this.plugin = command == null ? null : CommandUtil.getOwningPlugin(command);
        this.actionFlags = new EnumMap<>(CommandActionFlag.class);
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public void setActionFlag(@Nonnull CommandActionFlag flag, @Nonnull TriState state) {
        Preconditions.checkNotNull(flag, "flag");
        Preconditions.checkNotNull(state, "state");
        actionFlags.put(flag, state);
    }

    public void setActionFlag(@Nonnull CommandActionFlag flag, boolean state) {
        Preconditions.checkNotNull(flag, "flag");
        actionFlags.put(flag, TriState.byBoolean(state));
    }

    public @Nonnull TriState getActionFlag(@Nonnull CommandActionFlag flag) {
        Preconditions.checkNotNull(flag, "flag");
        return actionFlags.getOrDefault(flag, TriState.NOT_SET);
    }

    public boolean getActionFlagUnknownToTrue(@Nonnull CommandActionFlag flag) {
        Preconditions.checkNotNull(flag, "flag");
        return actionFlags.getOrDefault(flag, TriState.NOT_SET).toBooleanOrElse(true);
    }

    public @Nonnull String getCommandLine() {
        return commandLine;
    }

    /**
     * @return the command that is executed for the given commandLine. May be null if unknown or not matching any command
     */
    public @Nullable Command getCommand() {
        return command;
    }

    /**
     * @return the plugin that owns the command, null if not known or internal command
     */
    public @Nullable Plugin getOwningPlugin() {
        return plugin;
    }

    /**
     * @return a new ArgsParser containing the commands arguments
     */
    public @Nonnull ArgsParser getCommandArguments() {
        if (commandArgs == null) {
            String[] splitLine = org.apache.commons.lang3.StringUtils.split(commandLine, ' '); // same split method as paper uses for consistency
            commandArgs = splitLine.length > 1 ? Arrays.copyOfRange(splitLine, 1, splitLine.length) : new String[0];
        }
        return new ArgsParser(commandArgs);
    }
}
