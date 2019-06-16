package de.iani.cubesideutils.plugin;

import de.iani.cubesideutils.sql.SQLConfig;
import de.iani.cubesideutils.sql.SQLConnection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class Database {

    private SQLConnection connection;
    private String tablePrefix;

    private String playerDataTableName;
    private String afkPlayersTableName;

    private String getPlayerDataQuery = "SELECT afk, rank FROM `" + this.playerDataTableName + "` WHERE playerId = ?";
    private String addPlayerDataQuery = "INSERT INTO `" + this.playerDataTableName + "` (playerId, afk, rank) VALUES (?, 0, NULL)";
    private String savePlayerDataQuery = "INSERT INTO `" + this.playerDataTableName + "` (playerId, afk, rank) VALUES (?, ?, ?) ON DUPLICATE KEY UPDATE afk = ?, rank = ?";

    private String getAfkServersQuery = "SELECT server FROM `" + this.afkPlayersTableName + "` WHERE player = ?";
    private String addAfkServerQuery = "INSERT IGNORE INTO `" + this.afkPlayersTableName + "` (player, server) VALUES (?, ?)";
    private String removeAfkServerQuery = "DELETE FROM `" + this.afkPlayersTableName + "` WHERE player = ? AND server = ?";

    Database() throws SQLException {
        UtilsPlugin plugin = UtilsPlugin.getInstance();
        SQLConfig sqlconf = plugin.getSQLConfig();

        this.tablePrefix = sqlconf.getTablePrefix();
        this.connection = new SQLConnection(sqlconf.getHost(), sqlconf.getDatabase(), sqlconf.getUser(), sqlconf.getPassword());

        this.playerDataTableName = this.tablePrefix + "_playerData";
        this.afkPlayersTableName = this.tablePrefix + "_afkPlayers";

        createMissingTables();
    }

    private void createMissingTables() throws SQLException {
        this.connection.runCommands((connection, sqlConnection) -> {
            if (!sqlConnection.hasTable(this.playerDataTableName)) {
                Statement smt = connection.createStatement();
                smt.executeUpdate("CREATE TABLE `" + this.playerDataTableName + "` (" + "`playerId` CHAR(36), " + "`afk` BIT NOT NULL, " + "`rank` VARCHAR(64), " + "PRIMARY KEY (`playerId`)) " + "ENGINE = innodb");
                smt.close();
            }
            if (!sqlConnection.hasTable(this.afkPlayersTableName)) {
                Statement smt = connection.createStatement();
                smt.executeUpdate("CREATE TABLE `" + this.afkPlayersTableName + "` (" + "`player` CHAR(36), " + "`server` VARCHAR(64), " + "PRIMARY KEY (`player`, `server`) " + "FOREIGN KEY (`player`) REFERENCES `" + this.playerDataTableName + "` (`playerId`) ON UPDATE CASCADE ON DELETE CASCADE) "
                        + "ENGINE = innodb");
                smt.close();
            }
            return null;
        });
    }

    public PlayerData getPlayerData(UUID playerId, boolean insertIfMissing) throws SQLException {
        return getPlayerData(playerId, false, insertIfMissing);
    }

    public OnlinePlayerData getOnlinePlayerData(UUID playerId, boolean insertIfMissing) throws SQLException {
        return (OnlinePlayerData) getPlayerData(playerId, true, insertIfMissing);
    }

    // TODO: isOnline = insertIfMissing?
    private PlayerData getPlayerData(UUID playerId, boolean isOnline, boolean insertIfMissing) throws SQLException {
        return this.connection.runCommands((connection, sqlConnection) -> {
            PreparedStatement smt = sqlConnection.getOrCreateStatement(this.getPlayerDataQuery);
            smt.setString(1, playerId.toString());
            ResultSet rs = smt.executeQuery();

            if (!rs.first()) {
                rs.close();
                if (!insertIfMissing) {
                    return null;
                }

                PreparedStatement insertSmt = sqlConnection.getOrCreateStatement(this.addPlayerDataQuery);
                insertSmt.setString(1, playerId.toString());
                insertSmt.executeUpdate();
                return getPlayerData(playerId, isOnline, insertIfMissing);
            }

            boolean afk = rs.getBoolean(1);
            String rank = rs.getString(2);
            PlayerData result = isOnline ? new OnlinePlayerData(playerId, afk, rank) : new PlayerData(playerId, afk, rank);

            rs.close();
            return result;
        });
    }

    public void savePlayerData(PlayerData data) throws SQLException {
        this.connection.runCommands((connection, sqlConnection) -> {
            PreparedStatement smt = sqlConnection.getOrCreateStatement(savePlayerDataQuery);
            smt.setString(1, data.getPlayerId().toString());
            smt.setBoolean(2, data.isGloballyAfk());
            smt.setString(3, data.getRank());
            smt.setBoolean(4, data.isGloballyAfk());
            smt.setString(5, data.getRank());
            smt.executeUpdate();
            return null;
        });
    }

    public Set<String> getAfkServers(UUID playerId) throws SQLException {
        return this.connection.runCommands((connection, sqlConnection) -> {
            PreparedStatement smt = sqlConnection.getOrCreateStatement(this.getAfkServersQuery);
            smt.setString(1, playerId.toString());
            ResultSet rs = smt.executeQuery();

            Set<String> result = new HashSet<>();
            while (rs.next()) {
                result.add(rs.getString(1));
            }

            rs.close();
            return result;
        });
    }

    public void setLocallyAfk(UUID playerId, boolean afk) throws SQLException {
        if (afk) {
            addLocallyAfk(playerId);
        } else {
            removeLocallyAfk(playerId);
        }
    }

    private void addLocallyAfk(UUID playerId) throws SQLException {
        this.connection.runCommands((connection, sqlConnection) -> {
            PreparedStatement smt = sqlConnection.getOrCreateStatement(this.addAfkServerQuery);
            smt.setString(1, playerId.toString());
            smt.setString(2, UtilsPlugin.getInstance().getConnectionAPI().getThisServer().getName());
            smt.executeUpdate();
            return null;
        });
    }

    private void removeLocallyAfk(UUID playerId) throws SQLException {
        this.connection.runCommands((connection, sqlConnection) -> {
            PreparedStatement smt = sqlConnection.getOrCreateStatement(this.removeAfkServerQuery);
            smt.setString(1, playerId.toString());
            smt.setString(2, UtilsPlugin.getInstance().getConnectionAPI().getThisServer().getName());
            smt.executeUpdate();
            return null;
        });
    }

}
