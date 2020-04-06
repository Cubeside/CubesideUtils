package de.iani.cubesideutils.plugin;

import de.iani.cubesideutils.Triple;
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

    private String generalDataTableName;
    private String realServersTableName;
    private String playerDataTableName;
    private String afkPlayersTableName;
    private String ranksTableName;

    private String getGeneralDataQuery;
    private String setGeneralDataQuery;

    private String addRealServerQuery;
    private String removeRealServerQuery;
    private String getRealServersQuery;

    private String getPlayerDataQuery;
    private String addPlayerDataQuery;
    private String setPlayerFirstJoinAndLastJoinAndSeenQuery;
    private String setPlayerLastJoinAndSeenQuery;
    private String setPlayerLastSeenQuery;
    private String setPlayerAfkQuery;
    private String setPlayerRankQuery;

    private String getAfkServersQuery;
    private String addAfkServerQuery;
    private String removeAfkServerQuery;

    private String getRankInformationQuery;
    private String setRankInformationQuery;
    private String removeRankInformationQuery;

    Database() throws SQLException {
        UtilsPlugin plugin = UtilsPlugin.getInstance();
        SQLConfig sqlconf = plugin.getSQLConfig();

        this.tablePrefix = sqlconf.getTablePrefix();
        this.connection = new MySQLConnection(sqlconf.getHost(), sqlconf.getDatabase(), sqlconf.getUser(), sqlconf.getPassword());

        this.generalDataTableName = this.tablePrefix + "_generalData";
        this.realServersTableName = this.tablePrefix + "_realServers";
        this.playerDataTableName = this.tablePrefix + "_playerData";
        this.afkPlayersTableName = this.tablePrefix + "_afkPlayers";
        this.ranksTableName = this.tablePrefix + "_ranks";

        this.getGeneralDataQuery = "SELECT value FROM `" + this.generalDataTableName + "` WHERE `key` = ?";
        this.setGeneralDataQuery = "INSERT INTO `" + this.generalDataTableName + "` (`key`, value) VALUES (?, ?) ON DUPLICATE KEY UPDATE value = ?";

        this.addRealServerQuery = "INSERT IGNORE INTO `" + this.realServersTableName + "` (server) VALUES (?)";
        this.removeRealServerQuery = "DELETE FROM `" + this.realServersTableName + "` WHERE server = ?";
        this.getRealServersQuery = "SELECT (server) FROM `" + this.realServersTableName + "`";

        this.getPlayerDataQuery = "SELECT firstJoin, lastJoin, lastSeen, afk, `rank` FROM `" + this.playerDataTableName + "` WHERE playerId = ?";
        this.addPlayerDataQuery = "INSERT INTO `" + this.playerDataTableName + "` (playerId, afk, `rank`) VALUES (?, 0, NULL)";
        this.setPlayerFirstJoinAndLastJoinAndSeenQuery = "UPDATE `" + this.playerDataTableName + "` SET firstJoin = ?, lastJoin = ?, lastSeen = ? WHERE playerId = ?";
        this.setPlayerLastJoinAndSeenQuery = "UPDATE `" + this.playerDataTableName + "` SET lastJoin = ?, lastSeen = ? WHERE playerId = ?";
        this.setPlayerLastSeenQuery = "UPDATE `" + this.playerDataTableName + "` SET lastSeen = ? WHERE playerId = ?";
        this.setPlayerAfkQuery = "UPDATE `" + this.playerDataTableName + "` SET afk = ? WHERE playerId = ?";
        this.setPlayerRankQuery = "UPDATE `" + this.playerDataTableName + "` SET `rank` = ? WHERE playerId = ?";

        this.getAfkServersQuery = "SELECT server FROM `" + this.afkPlayersTableName + "` WHERE player = ?";
        this.addAfkServerQuery = "INSERT IGNORE INTO `" + this.afkPlayersTableName + "` (player, server) VALUES (?, ?)";
        this.removeAfkServerQuery = "DELETE FROM `" + this.afkPlayersTableName + "` WHERE player = ? AND server = ?";

        this.getRankInformationQuery = "SELECT `rank`, priority, permission, prefix FROM `" + this.ranksTableName + "` ORDER BY priority DESC";
        this.setRankInformationQuery = "INSERT INTO `" + this.ranksTableName + "` (`rank`, priority, permission, prefix) VALUES (?, ?, ?, ?) ON DUPLICATE KEY UPDATE priority = ?, permission = ?, prefix = ?";
        this.removeRankInformationQuery = "DELETE FROM `" + this.ranksTableName + "` WHERE `rank` = ?";

        createMissingTables();
    }

    private void createMissingTables() throws SQLException {
        this.connection.runCommands((connection, sqlConnection) -> {
            if (!sqlConnection.hasTable(this.generalDataTableName)) {
                Statement smt = connection.createStatement();
                smt.executeUpdate("CREATE TABLE `" + this.generalDataTableName + "` (" + "`key` VARCHAR(128), " + "value MEDIUMTEXT, " + "PRIMARY KEY (`key`)) " + "ENGINE = innodb");
                smt.close();
            }
            if (!sqlConnection.hasTable(this.realServersTableName)) {
                Statement smt = connection.createStatement();
                smt.executeUpdate("CREATE TABLE `" + this.realServersTableName + "` (" + "server VARCHAR(64), " + "PRIMARY KEY (server)) " + "ENGINE = innodb");
                smt.close();
            }
            if (!sqlConnection.hasTable(this.playerDataTableName)) {
                Statement smt = connection.createStatement();
                smt.executeUpdate("CREATE TABLE `" + this.playerDataTableName + "` (" + "playerId CHAR(36), " + "firstJoin BIGINT NOT NULL DEFAULT 0, " + "lastJoin BIGINT NOT NULL DEFAULT 0, " + "lastSeen BIGINT NOT NULL DEFAULT 0, " + "afk BIT NOT NULL, " + "`rank` VARCHAR(64), "
                        + "PRIMARY KEY (playerId)) " + "ENGINE = innodb");
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
                smt.executeUpdate("CREATE TABLE `" + this.ranksTableName + "` (" + "`rank` VARCHAR(64), " + "priority INT, " + "permission TINYTEXT, " + "prefix TINYTEXT, " + "PRIMARY KEY (`rank`) " + ") ENGINE = innodb");
                smt.close();
            }
            return null;
        });
    }

    public String getGeneralData(String key) throws SQLException {
        return this.connection.runCommands((connection, sqlConnection) -> {
            PreparedStatement smt = sqlConnection.getOrCreateStatement(this.getGeneralDataQuery);
            smt.setString(1, key);

            ResultSet rs = smt.executeQuery();
            String result = rs.next() ? rs.getString(1) : null;
            rs.close();
            return result;
        });
    }

    public void setGeneralData(String key, String value) throws SQLException {
        this.connection.runCommands((connection, sqlConnection) -> {
            PreparedStatement smt = sqlConnection.getOrCreateStatement(this.setGeneralDataQuery);
            smt.setString(1, key);
            smt.setString(2, value);
            smt.setString(3, value);
            smt.executeUpdate();
            return null;
        });
    }

    public void registerRealServer() throws SQLException {
        this.connection.runCommands((connection, sqlConnection) -> {
            PreparedStatement smt = sqlConnection.getOrCreateStatement(this.addRealServerQuery);
            smt.setString(1, UtilsPlugin.getInstance().getGlobalDataHelper().getThisServerName());
            smt.executeUpdate();
            return null;
        });
    }

    public void removeRealServer() throws SQLException {
        this.connection.runCommands((connection, sqlConnection) -> {
            PreparedStatement smt = sqlConnection.getOrCreateStatement(this.removeRealServerQuery);
            smt.setString(1, UtilsPlugin.getInstance().getGlobalDataHelper().getThisServerName());
            smt.executeUpdate();
            return null;
        });
    }

    public Set<String> getRealServers() throws SQLException {
        return this.connection.runCommands((connection, sqlConnection) -> {
            PreparedStatement smt = sqlConnection.getOrCreateStatement(this.getRealServersQuery);
            ResultSet rs = smt.executeQuery();

            Set<String> result = new HashSet<>();
            while (rs.next()) {
                result.add(rs.getString(1));
            }

            rs.close();
            return result;
        });
    }

    public PlayerData getPlayerData(UUID playerId, boolean insertIfMissing) throws SQLException {
        return getPlayerData(playerId, false, insertIfMissing, 0);
    }

    public OnlinePlayerData getOnlinePlayerData(UUID playerId, boolean insertIfMissing, long lastAction) throws SQLException {
        return (OnlinePlayerData) getPlayerData(playerId, true, insertIfMissing, lastAction);
    }

    private PlayerData getPlayerData(UUID playerId, boolean isOnline, boolean insertIfMissing, long lastAction) throws SQLException {
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
                return getPlayerData(playerId, isOnline, insertIfMissing, lastAction);
            }

            long firstJoin = rs.getLong(1);
            long lastJoin = rs.getLong(2);
            long lastSeen = rs.getLong(3);
            boolean afk = rs.getBoolean(4);
            String rank = rs.getString(5);
            PlayerData result = isOnline ? new OnlinePlayerData(playerId, firstJoin, lastJoin, lastSeen, afk, lastAction, rank) : new PlayerData(playerId, firstJoin, lastJoin, lastSeen, afk, rank);

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
            smt.setBoolean(1, afk);
            smt.setString(2, playerId.toString());
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
            smt.setString(2, UtilsPlugin.getInstance().getGlobalDataHelper().getThisServer().getName());
            smt.executeUpdate();
            return null;
        });
    }

    private void removeLocallyAfk(UUID playerId) throws SQLException {
        this.connection.runCommands((connection, sqlConnection) -> {
            PreparedStatement smt = sqlConnection.getOrCreateStatement(this.removeAfkServerQuery);
            smt.setString(1, playerId.toString());
            smt.setString(2, UtilsPlugin.getInstance().getGlobalDataHelper().getThisServer().getName());
            smt.executeUpdate();
            return null;
        });
    }

    public void setRank(UUID playerId, String rank) throws SQLException {
        this.connection.runCommands((connection, sqlConnection) -> {
            PreparedStatement smt = sqlConnection.getOrCreateStatement(this.setPlayerRankQuery);
            smt.setString(1, rank);
            smt.setString(2, playerId.toString());
            smt.executeUpdate();
            return null;
        });
    }

    public Map<String, Triple<Integer, String, String>> getRankInformation() throws SQLException {
        return this.connection.runCommands((connection, sqlConnection) -> {
            PreparedStatement smt = sqlConnection.getOrCreateStatement(this.getRankInformationQuery);
            ResultSet rs = smt.executeQuery();

            Map<String, Triple<Integer, String, String>> result = new LinkedHashMap<>();
            while (rs.next()) {
                result.put(rs.getString(1), new Triple<>(rs.getInt(2), rs.getString(3), rs.getString(4)));
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

    public boolean removeRankInformation(String rank) throws SQLException {
        return this.connection.runCommands((connection, sqlConnection) -> {
            PreparedStatement smt = sqlConnection.getOrCreateStatement(this.removeRankInformationQuery);
            smt.setString(1, rank);
            return smt.executeUpdate() > 0;
        });
    }

}
