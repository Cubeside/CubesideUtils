package de.iani.cubesideutils.velocity.plugin;

import de.iani.cubesideutils.plugin.UtilsDatabase;
import de.iani.cubesideutils.velocity.sql.SQLConfigVelocity;

import java.sql.SQLException;
import java.util.UUID;

public class UtilsDatabaseVelocity extends UtilsDatabase<PlayerDataImplVelocity> {

    public UtilsDatabaseVelocity(SQLConfigVelocity sqlConfig) throws SQLException {
        super(sqlConfig);
    }

    @Override
    public PlayerDataImplVelocity getPlayerData(UUID playerId, boolean isOnline, boolean insertIfMissing, long lastAction, boolean manuallySetAfk) throws SQLException {
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

        return new PlayerDataImplVelocity(playerId, firstJoin, lastJoin, lastSeen, afk, lastName, rank);
    }

}
