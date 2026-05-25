package de.iani.cubesideutils.bukkit.events;

import org.bukkit.command.Command;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

public class DetectCommandForLabelEvent extends Event {
    private static final HandlerList handlers = new HandlerList();

    private final @NonNull String commandLine;
    private final @NonNull String commandLabel;
    private @Nullable Command command;

    public DetectCommandForLabelEvent(@NonNull String commandLine, @NonNull String commandLabel, @Nullable Command command) {
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

    public @NonNull String getCommandLine() {
        return commandLine;
    }

    public @NonNull String getCommandLabel() {
        return commandLabel;
    }

    public @Nullable Command getCommand() {
        return command;
    }

    public void setCommand(@Nullable Command command) {
        this.command = command;
    }
}
