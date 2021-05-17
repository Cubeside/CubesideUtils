package de.iani.cubesideutils.bungee.plugin.api;

import de.iani.cubesideutils.plugin.api.PlayerData;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public interface PlayerDataBungee extends PlayerData {

    public ProxiedPlayer getPlayer();

}