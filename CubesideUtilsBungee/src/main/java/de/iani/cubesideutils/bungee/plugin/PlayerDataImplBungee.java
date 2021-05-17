package de.iani.cubesideutils.bungee.plugin;

import de.iani.cubesideutils.bungee.plugin.api.PlayerDataBungee;
import de.iani.cubesideutils.plugin.PlayerDataImpl;
import java.util.UUID;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public class PlayerDataImplBungee extends PlayerDataImpl implements PlayerDataBungee {

    protected PlayerDataImplBungee(UUID playerId, long firstJoin, long lastJoin, long lastSeen, boolean afk, String lastName, String rank) {
        super(playerId, firstJoin, lastJoin, lastSeen, afk, lastName, rank);
    }

    @Override
    public boolean isOnlineHere() {
        ProxiedPlayer player = getPlayer();
        return player != null && player.isConnected();
    }

    @Override
    public ProxiedPlayer getPlayer() {
        return ProxyServer.getInstance().getPlayer(getPlayerId());
    }

}
