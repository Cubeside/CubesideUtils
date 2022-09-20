package de.iani.cubesideutils.bukkit.plugin;

import de.iani.cubesideutils.Triple;
import de.iani.cubesideutils.plugin.UtilsDatabase;
import de.iani.cubesideutils.sql.SQLConfig;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.bukkit.OfflinePlayer;

public class UtilsDatabaseBukkit extends UtilsDatabase<PlayerDataImplBukkit> {

    public UtilsDatabaseBukkit(SQLConfig sqlConfig) throws SQLException {
        super(sqlConfig);
    }

    public OnlinePlayerDataImpl getOnlinePlayerData(UUID playerId, boolean insertIfMissing, long lastAction, boolean manuallySetAfk) throws SQLException {
        return (OnlinePlayerDataImpl) getPlayerData(playerId, true, insertIfMissing, lastAction, manuallySetAfk);
    }

    @Override
    public PlayerDataImplBukkit getPlayerData(UUID playerId, boolean isOnline, boolean insertIfMissing, long lastAction, boolean manuallySetAfk) throws SQLException {
        PlayerDataStruct data = getPlayerDataData(playerId, isOnline, insertIfMissing, lastAction, manuallySetAfk);
        if (data == null) {
            return null;
        }

        long firstJoin = data.firstJoin;
        long lastJoin = data.lastJoin;
        long lastSeen = data.lastSeen;
        boolean afk = data.afk;
        String lastName = data.lastName;
        String rank = data.rank;

        return isOnline ? new OnlinePlayerDataImpl(playerId, firstJoin, lastJoin, lastSeen, afk, lastAction, manuallySetAfk, lastName, rank) : new PlayerDataImplBukkit(playerId, firstJoin, lastJoin, lastSeen, afk, lastName, rank);
    }

    public List<OfflinePlayer> searchPlayersByPartialName(String partialName) throws SQLException {
        List<Triple<UUID, String, Long>> idNamePairs = getPlayerIdsByPartialName(partialName);
        List<OfflinePlayer> result = new ArrayList<>(idNamePairs.size());

        for (Triple<UUID, String, Long> pair : idNamePairs) {
            OfflinePlayer player = new CachedOfflinePlayer(pair.first, pair.second, pair.third);
            result.add(player);
        }

        return result;
    }

}
