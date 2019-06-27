package de.iani.cubesideutils.plugin.events;

import de.iani.cubesideutils.plugin.PlayerData;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public abstract class PlayerDataEvent extends Event {

    private static final HandlerList handlers = new HandlerList();

    public static HandlerList getHandlerList() {
        return handlers;
    }

    private PlayerData data;

    public PlayerDataEvent(PlayerData data) {
        this.data = data;
    }

    public PlayerData getPlayerData() {
        return data;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

}
