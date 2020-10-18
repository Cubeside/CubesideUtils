package de.iani.cubesideutils.bukkit.plugin.api.events;

import java.util.UUID;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class PlayerPermissionsChangedEvent extends Event {

    private static final HandlerList handlers = new HandlerList();

    public static HandlerList getHandlerList() {
        return handlers;
    }

    private UUID playerId;

    public PlayerPermissionsChangedEvent(UUID playerId) {
        this.playerId = playerId;
    }

    public UUID getPlayerId() {
        return playerId;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

}
