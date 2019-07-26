package de.iani.cubesideutils.plugin;

import de.iani.cubesideutils.Pair;
import de.iani.cubesideutils.sql.MySQLConnection;
import de.iani.cubesideutils.sql.SQLConfig;
import de.iani.cubesideutils.sql.SQLConnection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

class Database {

    private SQLConnection connection;
    private String tablePrefix;

    private String playerDataTableName;
    private String afkPlayersTableName;
    private String ranksTableName;

    private String getPlayerDataQuery = "SELECT firstJoin, lastJoin, lastSeen, afk, rank FROM `" + this.playerDataTableName + "` WHERE playerId = ?";
    private String addPlayerDataQuery = "INSERT INTO `" + this.playerDataTableName + "` (playerId, afk, rank) VALUES (?, 0, NULL)";
    private String setPlayerFirstJoinAndLastJoinAndSeenQuery = "UPDATE `" + this.playerDataTableName + "` SET firstJoin = ?, lastJoin = ?, lastSeen = ? WHERE playerId = ?";
    private String setPlayerLastJoinAndSeenQuery = "UPDATE `" + this.playerDataTableName + "` SET lastJoin = ?, lastSeen = ? WHERE playerId = ?";
    private String setPlayerLastSeenQuery = "UPDATE `" + this.playerDataTableName + "` SET lastSeen = ? WHERE playerId = ?";
    private String setPlayerAfkQuery = "UPDATE `" + this.playerDataTableName + "` afk = ? WHERE playerId = ?";
    private String setPlayerRankQuery = "UPDATE `" + this.playerDataTableName + "` rank = ? WHERE playerId = ?";

    private String getAfkServersQuery = "SELECT server FROM `" + this.afkPlayersTableName + "` WHERE player = ?";
    private String addAfkServerQuery = "INSERT IGNORE INTO `" + this.afkPlayersTableName + "` (player, server) VALUES (?, ?)";
    private String removeAfkServerQuery = "DELETE FROM `" + this.afkPlayersTableName + "` WHERE player = ? AND server = ?";

    private String getRankInformationQuery = "SELECT rank, permission, prefix FROM `" + this.ranksTableName + "` ORDER BY priority DESC";
    private String setRankInformationQuery = "INSERT INTO `" + this.ranksTableName + "` rank, priority, permission, prefix VALUES (?, ?, ?, ?) ON DUPLICATE KEY UPDATE priority = ?, permission = ?, prefix = ?";
    private String removeRankInformationQuery = "DELETE FROM `" + this.ranksTableName + "` WHERE rank = ?";

    Database() throws SQLException {
        UtilsPlugin plugin = UtilsPlugin.getInstance();
        SQLConfig sqlconf = plugin.getSQLConfig();

        this.tablePrefix = sqlconf.getTablePrefix();
        this.connection = new MySQLConnection(sqlconf.getHost(), sqlconf.getDatabase(), sqlconf.getUser(), sqlconf.getPassword());

        this.playerDataTableName = this.tablePrefix + "_playerData";
        this.afkPlayersTableName = this.tablePrefix + "_afkPlayers";
        this.ranksTableName = this.tablePrefix + "_ranks";

        createMissingTables();
    }

    private void createMissingTables() throws SQLException {
        this.connection.runCommands((connection, sqlConnection) -> {
            if (!sqlConnection.hasTable(this.playerDataTableName)) {
                Statement smt = connection.createStatement();
                smt.executeUpdate("CREATE TABLE `" + this.playerDataTableName + "` (" + "playerId CHAR(36), " + "firstJoin BIGINT NOT NULL DEFAULT 0, " + "lastJoin BIGINT NOT NULL DEFAULT 0, " + "lastSeen BIGINT NOT NULL DEFAULT 0, " + "afk BIT NOT NULL, " + "rank VARCHAR(64), "
                        + "PRIMARY KEY (`playerId`)) " + "ENGINE = innodb");
                smt.close();
            }
            if (!sqlConnection.hasTable(this.afkPlayersTableName)) {
                Statement smt = connection.createStatement();
                smt.executeUpdate(
                        "CREATE TABLE `" + this.afkPlayersTableName + "` (" + "player CHAR(36), " + "server VARCHAR(64), " + "PRIMARY KEY (player, server), " + "FOREIGN KEY (player) REFERENCES `" + this.playerDataTableName + "` (playerId) ON UPDATE CASCADE ON DELETE CASCADE) " + "ENGINE = innodb");
                smt.close();
            }
            if (!sqlConnection.hasTable(this.ranksTableName)) {
                Statement smt = connection.createStatement();
                smt.executeUpdate("CREATE TABLE `" + this.ranksTableName + "` (" + "rank VARCHAR(64), " + "priority INT, " + "permission TINYTEXT, " + "prefix TINYTEXT, " + "PRIMARY KEY (rank), " + "FOREIGN KEY (player) REFERENCES `" + this.playerDataTableName
                        + "` (playerId) ON UPDATE CASCADE ON DELETE CASCADE) " + "ENGINE = innodb");
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

            long firstJoin = rs.getLong(1);
            long lastJoin = rs.getLong(2);
            long lastSeen = rs.getLong(3);
            boolean afk = rs.getBoolean(4);
            String rank = rs.getString(5);
            PlayerData result = isOnline ? new OnlinePlayerData(playerId, firstJoin, lastJoin, lastSeen, afk, rank) : new PlayerData(playerId, firstJoin, lastJoin, lastSeen, afk, rank);

            rs.close();
            return result;
        });
    }

    public void setPlayerFirstJoinAndLastJoinAndSeen(UUID playerId, long value) throws SQLException {
        this.connection.runCommands((connection, sqlConnection) -> {
            PreparedStatement smt = sqlConnection.getOrCreateStatement(this.setPlayerFirstJoinAndLastJoinAndSeenQuery);
            smt.setLong(1, value);
            smt.setLong(2, value);
            smt.setLong(3, value);
            smt.setString(4, playerId.toString());
            smt.executeUpdate();
            return null;
        });
    }

    public void setPlayerLastJoinAndSeen(UUID playerId, long value) throws SQLException {
        this.connection.runCommands((connection, sqlConnection) -> {
            PreparedStatement smt = sqlConnection.getOrCreateStatement(this.setPlayerLastJoinAndSeenQuery);
            smt.setLong(1, value);
            smt.setLong(2, value);
            smt.setString(3, playerId.toString());
            smt.executeUpdate();
            return null;
        });
    }

    public void setPlayerLastSeen(UUID playerId, long lastSeen) throws SQLException {
        this.connection.runCommands((connection, sqlConnection) -> {
            PreparedStatement smt = sqlConnection.getOrCreateStatement(this.setPlayerLastSeenQuery);
            smt.setLong(1, lastSeen);
            smt.setString(2, playerId.toString());
            smt.executeUpdate();
            return null;
        });
    }

    public void setGloballyAfk(UUID playerId, boolean afk) throws SQLException {
        this.connection.runCommands((connection, sqlConnection) -> {
            PreparedStatement smt = sqlConnection.getOrCreateStatement(this.setPlayerAfkQuery);
            smt.setString(1, playerId.toString());
            smt.setBoolean(2, afk);
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

    public void setRank(UUID playerId, String rank) throws SQLException {
        this.connection.runCommands((connection, sqlConnection) -> {
            PreparedStatement smt = sqlConnection.getOrCreateStatement(this.setPlayerRankQuery);
            smt.setString(1, playerId.toString());
            smt.setString(2, rank);
            smt.executeUpdate();
            return null;
        });
    }

    public Map<String, Pair<String, String>> getRankInformation() throws SQLException {
        return this.connection.runCommands((connection, sqlConnection) -> {
            PreparedStatement smt = sqlConnection.getOrCreateStatement(this.getRankInformationQuery);
            ResultSet rs = smt.executeQuery();

            Map<String, Pair<String, String>> result = new LinkedHashMap<>();
            while (rs.next()) {
                result.put(rs.getString(1), new Pair<>(rs.getString(2), rs.getString(3)));
            }

            return result;
        });
    }

    public void setRankInformation(String rank, int priority, String permission, String prefix) throws SQLException {
        this.connection.runCommands((connection, sqlConnection) -> {
            PreparedStatement smt = sqlConnection.getOrCreateStatement(this.setRankInformationQuery);
            smt.setString(1, rank);
            smt.setInt(2, priority);
            smt.setString(3, permission);
            smt.setString(4, prefix);
            smt.setInt(5, priority);
            smt.setString(6, permission);
            smt.setString(7, prefix);
            smt.executeUpdate();
            return null;
        });
    }

    public void removeRankInformation(String rank) throws SQLException {
        this.connection.runCommands((connection, sqlConnection) -> {
            PreparedStatement smt = sqlConnection.getOrCreateStatement(this.removeRankInformationQuery);
            smt.setString(1, rank);
            return null;
        });
    }

}
