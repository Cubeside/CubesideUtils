package de.iani.cubesideutils.bungee.plugin;

import de.iani.cubesideutils.bungee.sql.SQLConfigBungee;
import de.iani.cubesideutils.plugin.UtilsDatabase;
import java.sql.SQLException;
import java.util.UUID;

public class UtilsDatabaseBungee extends UtilsDatabase<PlayerDataImplBungee> {

    public UtilsDatabaseBungee(SQLConfigBungee sqlConfig) throws SQLException {
        super(sqlConfig);
    }

    @Override
    public PlayerDataImplBungee getPlayerData(UUID playerId, boolean isOnline, boolean insertIfMissing, long lastAction, boolean manuallySetAfk) throws SQLException {
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

        return new PlayerDataImplBungee(playerId, firstJoin, lastJoin, lastSeen, afk, lastName, rank);
    }

}
