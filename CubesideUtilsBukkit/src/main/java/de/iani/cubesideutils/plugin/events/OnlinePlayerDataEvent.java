package de.iani.cubesideutils.plugin.events;

import de.iani.cubesideutils.plugin.OnlinePlayerData;
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
