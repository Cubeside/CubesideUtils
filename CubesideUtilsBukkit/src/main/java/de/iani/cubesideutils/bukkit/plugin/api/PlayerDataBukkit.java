package de.iani.cubesideutils.bukkit.plugin.api;

import de.iani.cubesideutils.plugin.api.PlayerData;
import org.bukkit.OfflinePlayer;

public interface PlayerDataBukkit extends PlayerData {

    public OnlinePlayerData getOnlineData();

    public OfflinePlayer getOfflinePlayer();

}
