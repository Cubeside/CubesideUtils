package de.iani.cubesideutils.bukkit.events;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.bukkit.command.Command;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class DetectCommandForLabelEvent extends Event {
    private static final HandlerList handlers = new HandlerList();

    private final @Nonnull String commandLine;
    private final @Nonnull String commandLabel;
    private @Nullable Command command;

    public DetectCommandForLabelEvent(@Nonnull String commandLine, @Nonnull String commandLabel, @Nullable Command command) {
        this.commandLine = commandLine;
        this.commandLabel = commandLabel;
        this.command = command;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public @Nonnull String getCommandLine() {
        return commandLine;
    }

    public @Nonnull String getCommandLabel() {
        return commandLabel;
    }

    public @Nullable Command getCommand() {
        return command;
    }

    public void setCommand(@Nullable Command command) {
        this.command = command;
    }
}
