package de.iani.cubesideutils.bukkit.plugin.api.events;

import de.iani.cubesideutils.bukkit.plugin.OnlinePlayerDataImpl;
import org.bukkit.event.Cancellable;

public class LocalAfkStateChangeEvent extends OnlinePlayerDataEvent implements Cancellable {

    private boolean nowAfk;
    private boolean cancelled;

    public LocalAfkStateChangeEvent(OnlinePlayerDataImpl data, boolean nowAfk) {
        super(data);

        this.nowAfk = nowAfk;
        this.cancelled = false;
    }

    public boolean isNowAfk() {
        return nowAfk;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancel) {
        this.cancelled = cancel;
    }

}
