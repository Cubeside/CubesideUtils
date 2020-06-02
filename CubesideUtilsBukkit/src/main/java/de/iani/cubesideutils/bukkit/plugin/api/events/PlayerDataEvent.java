package de.iani.cubesideutils.bukkit.plugin.api.events;

import de.iani.cubesideutils.bukkit.plugin.api.PlayerDataBukkit;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public abstract class PlayerDataEvent extends Event {

    private static final HandlerList handlers = new HandlerList();

    public static HandlerList getHandlerList() {
        return handlers;
    }

    private PlayerDataBukkit data;

    public PlayerDataEvent(PlayerDataBukkit data) {
        this.data = data;
    }

    public PlayerDataBukkit getPlayerData() {
        return data;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

}
