package de.iani.cubesideutils.bukkit.plugin.api.events;

import de.iani.cubesideutils.bukkit.plugin.api.OnlinePlayerData;
import org.bukkit.entity.Player;

public abstract class OnlinePlayerDataEvent extends PlayerDataEvent {

    public OnlinePlayerDataEvent(OnlinePlayerData data) {
        super(data);
    }

    @Override
    public OnlinePlayerData getPlayerData() {
        return super.getPlayerData().getOnlineData();
    }

    public Player getPlayer() {
        return getPlayerData().getPlayer();
    }

}
