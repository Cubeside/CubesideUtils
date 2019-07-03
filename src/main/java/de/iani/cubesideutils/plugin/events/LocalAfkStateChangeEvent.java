package de.iani.cubesideutils.plugin.events;

import de.iani.cubesideutils.plugin.OnlinePlayerData;
import org.bukkit.event.Cancellable;

public class LocalAfkStateChangeEvent extends OnlinePlayerDataEvent implements Cancellable {

    private boolean nowAfk;
    private boolean cancelled;

    public LocalAfkStateChangeEvent(OnlinePlayerData data, boolean nowAfk) {
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
