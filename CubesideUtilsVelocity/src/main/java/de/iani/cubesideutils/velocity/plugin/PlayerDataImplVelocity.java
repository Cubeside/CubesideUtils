package de.iani.cubesideutils.velocity.plugin;

import com.velocitypowered.api.proxy.Player;
import de.iani.cubesideutils.plugin.PlayerDataImpl;

import java.util.Optional;
import java.util.UUID;

import de.iani.cubesideutils.velocity.plugin.api.PlayerDataVelocity;

public class PlayerDataImplVelocity extends PlayerDataImpl implements PlayerDataVelocity {

    protected PlayerDataImplVelocity(UUID playerId, long firstJoin, long lastJoin, long lastSeen, boolean afk, String lastName, String rank) {
        super(playerId, firstJoin, lastJoin, lastSeen, afk, lastName, rank);
    }

    @Override
    public boolean isOnlineHere() {
        Optional<Player> player = getPlayer();
        return player.isPresent(); //player != null && player.isConnected() wird vermutlich nicht mehr gebraucht?
    }

    @Override
    public Optional<Player> getPlayer() {
        return CubesideUtilsVelocity.getInstance().getServer().getPlayer(getPlayerId());
    }

}
