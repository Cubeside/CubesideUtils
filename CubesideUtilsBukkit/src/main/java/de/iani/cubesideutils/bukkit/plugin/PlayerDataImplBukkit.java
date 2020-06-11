package de.iani.cubesideutils.bukkit.plugin;

import de.iani.cubesideutils.bukkit.plugin.api.OnlinePlayerData;
import de.iani.cubesideutils.bukkit.plugin.api.PlayerDataBukkit;
import de.iani.cubesideutils.plugin.PlayerDataImpl;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import org.bukkit.Bukkit;
import org.bukkit.permissions.Permissible;

public class PlayerDataImplBukkit extends PlayerDataImpl implements PlayerDataBukkit {

    public PlayerDataImplBukkit(UUID playerId, long firstJoin, long lastJoin, long lastSeen, boolean afk, String rank) {
        super(playerId, firstJoin, lastJoin, lastSeen, afk, rank);
    }

    @Override
    public boolean isOnlineHere() {
        if (Bukkit.isPrimaryThread()) {
            return Bukkit.getPlayer(getPlayerId()) != null;
        }

        try {
            return Bukkit.getScheduler().callSyncMethod(CubesideUtilsBukkit.getInstance().getPlugin(), () -> (Bukkit.getPlayer(getPlayerId()) != null)).get();
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public OnlinePlayerData getOnlineData() {
        if (this instanceof OnlinePlayerData) {
            return (OnlinePlayerData) this;
        }

        if (!isOnlineHere()) {
            throw new IllegalStateException("The player isn't online.");
        }

        return CubesideUtilsBukkit.getInstance().getPlayerDataCache().getOnline(getPlayerId());
    }

    public void checkRank(Permissible player) {
        String rank = null;
        for (String possible : CubesideUtilsBukkit.getInstance().getRanks()) {
            String permission = CubesideUtilsBukkit.getInstance().getPermission(possible);
            if (permission == null || player.hasPermission(permission)) {
                rank = possible;
                break;
            }
        }

        setRank(rank);
    }

}
