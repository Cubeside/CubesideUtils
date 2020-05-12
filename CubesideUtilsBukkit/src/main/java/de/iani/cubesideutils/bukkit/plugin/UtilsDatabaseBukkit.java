package de.iani.cubesideutils.bukkit.plugin;

import de.iani.cubesideutils.Triple;
import de.iani.cubesideutils.plugin.UtilsDatabase;
import de.iani.cubesideutils.sql.SQLConfig;
import java.sql.SQLException;
import java.util.UUID;

public class UtilsDatabaseBukkit extends UtilsDatabase<PlayerDataImplBukkit> {

    public UtilsDatabaseBukkit(SQLConfig sqlConfig) throws SQLException {
        super(sqlConfig);
    }

    public OnlinePlayerDataImpl getOnlinePlayerData(UUID playerId, boolean insertIfMissing, long lastAction, boolean manuallySetAfk) throws SQLException {
        return (OnlinePlayerDataImpl) getPlayerData(playerId, true, insertIfMissing, lastAction, manuallySetAfk);
    }

    @Override
    public PlayerDataImplBukkit getPlayerData(UUID playerId, boolean isOnline, boolean insertIfMissing, long lastAction, boolean manuallySetAfk) throws SQLException {
        Triple<Triple<Long, Long, Long>, Boolean, String> data = getPlayerDataData(playerId, isOnline, insertIfMissing, lastAction, manuallySetAfk);
        if (data == null) {
            return null;
        }

        long firstJoin = data.first.first;
        long lastJoin = data.first.second;
        long lastSeen = data.first.third;
        boolean afk = data.second;
        String rank = data.third;

        return isOnline ? new OnlinePlayerDataImpl(playerId, firstJoin, lastJoin, lastSeen, afk, lastAction, manuallySetAfk, rank) : new PlayerDataImplBukkit(playerId, firstJoin, lastJoin, lastSeen, afk, rank);
    }

}
