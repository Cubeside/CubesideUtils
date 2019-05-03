package de.iani.cubesideutils.plugin.database;

import de.iani.cubesideutils.plugin.PlayerData;
import de.inani.cubesidesecurity.SecurityPlayer;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class Database {

    // TODO: isOnline = insertIfMissing?
    public PlayerData getPlayerData(UUID playerId, boolean isOnline, boolean insertIfMissing) throws SQLException {
        return this.connection.runCommands((connection, sqlConnection) -> {
            PreparedStatement smt = sqlConnection.getOrCreateStatement(this.getPlayerDataString);
            smt.setString(1, playerId.toString());
            ResultSet rs = smt.executeQuery();

            if (!rs.first()) {
                rs.close();
                if (!insertIfMissing) {
                    return null;
                }

                SecurityPlayer result = SecurityPlayer.createNew(playerId);
                PreparedStatement insertSmt = sqlConnection.getOrCreateStatement(this.addPlayerDataString);
                insertSmt.setString(1, result.getPlayerId().toString());
                insertSmt.executeUpdate();
                return result;
            }

            PlayerData result = new PlayerData(playerId, /*TODO*/);
            rs.close();
            return result;
        });
    }

}
