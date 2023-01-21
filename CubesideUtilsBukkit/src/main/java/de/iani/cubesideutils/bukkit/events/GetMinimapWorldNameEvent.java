package de.iani.cubesideutils.bukkit.events;

import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;

public class GetMinimapWorldNameEvent extends PlayerEvent {
    private static final HandlerList handlers = new HandlerList();

    private String serverName;
    private String worldName;

    public GetMinimapWorldNameEvent(Player player, String serverName, String worldName) {
        super(player);
        this.serverName = serverName;
        this.worldName = worldName;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public String getServerName() {
        return serverName;
    }

    public void setServerName(String serverName) {
        this.serverName = serverName;
    }

    public String getWorldName() {
        return worldName;
    }

    public void setWorldName(String worldName) {
        this.worldName = worldName;
    }
}
