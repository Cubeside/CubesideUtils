package de.iani.cubesideutils.plugin;

import de.iani.cubesideutils.Pair;
import de.iani.cubesideutils.Triple;
import de.iani.cubesideutils.plugin.api.PasswordHandler;
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

public abstract class UtilsDatabase<T extends PlayerDataImpl> {

    private SQLConnection connection;
    private String tablePrefix;

    private String generalDataTableName;
    private String realServersTableName;
    private String playerDataTableName;
    private String afkPlayersTableName;
    private String ranksTableName;
    private String passwordsTableName;

    private String getGeneralDataQuery;
    private String setGeneralDataQuery;

    private String getPasswordHashQuery;
    private String setPasswordHashQuery;
    private String removePasswordHashQuery;
    private String removePasswordKeyQuery;

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

    public UtilsDatabase(SQLConfig sqlConfig) throws SQLException {
        this.tablePrefix = sqlConfig.getTablePrefix();
        this.connection = new MySQLConnection(sqlConfig.getHost(), sqlConfig.getDatabase(), sqlConfig.getUser(), sqlConfig.getPassword());

        this.generalDataTableName = this.tablePrefix + "_generalData";
        this.realServersTableName = this.tablePrefix + "_realServers";
        this.playerDataTableName = this.tablePrefix + "_playerData";
        this.afkPlayersTableName = this.tablePrefix + "_afkPlayers";
        this.ranksTableName = this.tablePrefix + "_ranks";
        this.passwordsTableName = this.tablePrefix + "_passwords";

        this.getGeneralDataQuery = "SELECT value FROM `" + this.generalDataTableName + "` WHERE `key` = ?";
        this.setGeneralDataQuery = "INSERT INTO `" + this.generalDataTableName + "` (`key`, value) VALUES (?, ?) ON DUPLICATE KEY UPDATE value = ?";

        this.getPasswordHashQuery = "SELECT salt, hash FROM `" + this.passwordsTableName + "` WHERE `key` = ? AND holderId = ?";
        this.setPasswordHashQuery = "INSERT INTO `" + this.passwordsTableName + "` (`key`, holderId, salt, hash) VALUES (?, ?, ?, ?) ON DUPLICATE KEY UPDATE salt = ?, hash = ?";
        this.removePasswordHashQuery = "REMOVE FROM `" + this.passwordsTableName + "` WHERE `key` = ? AND holderId = ?";
        this.removePasswordKeyQuery = "REMOVE FROM `" + this.passwordsTableName + "` WHERE `key` = ?";

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
            if (!sqlConnection.hasTable(this.passwordsTableName)) {
                Statement smt = connection.createStatement();
                smt.executeUpdate("CREATE TABLE `" + this.passwordsTableName + "` (" + "`key` VARCHAR(" + PasswordHandler.MAX_KEY_LENGTH + "), " + "holderId CHAR(36), " + "salt BINARY(" + PasswordHandlerImpl.SALT_LENGTH + "), " + "hash BINARY(" + PasswordHandlerImpl.HASH_LENGTH + "), "
                        + "PRIMARY KEY (`key`, holderId)" + ") ENGINE = innodb");
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

    public Pair<byte[], byte[]> getPasswordEntry(String key, UUID holderId) throws SQLException {
        return this.connection.runCommands((connection, sqlConnection) -> {
            PreparedStatement smt = sqlConnection.getOrCreateStatement(this.getPasswordHashQuery);
            smt.setString(1, key);
            smt.setString(2, holderId.toString());
            ResultSet rs = smt.executeQuery();

            Pair<byte[], byte[]> result = rs.first() ? new Pair<>(rs.getBytes(1), rs.getBytes(2)) : null;

            rs.close();
            return result;
        });
    }

    public void setPasswordHash(String key, UUID holderId, byte[] salt, byte[] hash) throws SQLException {
        this.connection.runCommands((connection, sqlConnection) -> {
            PreparedStatement smt = sqlConnection.getOrCreateStatement(this.setPasswordHashQuery);
            smt.setString(1, key);
            smt.setString(2, holderId.toString());
            smt.setBytes(3, salt);
            smt.setBytes(4, hash);
            smt.setBytes(5, salt);
            smt.setBytes(6, hash);
            smt.executeUpdate();
            return null;
        });
    }

    public boolean removePassword(String key, UUID holderId) throws SQLException {
        return this.connection.runCommands((connection, sqlConnection) -> {
            PreparedStatement smt = sqlConnection.getOrCreateStatement(this.removePasswordHashQuery);
            smt.setString(1, key);
            smt.setString(2, holderId.toString());
            return smt.executeUpdate() > 0;
        });
    }

    public boolean removePasswordKey(String key) throws SQLException {
        return this.connection.runCommands((connection, sqlConnection) -> {
            PreparedStatement smt = sqlConnection.getOrCreateStatement(this.removePasswordKeyQuery);
            smt.setString(1, key);
            return smt.executeUpdate() > 0;
        });
    }

    public void registerRealServer() throws SQLException {
        this.connection.runCommands((connection, sqlConnection) -> {
            PreparedStatement smt = sqlConnection.getOrCreateStatement(this.addRealServerQuery);
            smt.setString(1, CubesideUtils.getInstance().getGlobalDataHelper().getThisServerName());
            smt.executeUpdate();
            return null;
        });
    }

    public void removeRealServer() throws SQLException {
        this.connection.runCommands((connection, sqlConnection) -> {
            PreparedStatement smt = sqlConnection.getOrCreateStatement(this.removeRealServerQuery);
            smt.setString(1, CubesideUtils.getInstance().getGlobalDataHelper().getThisServerName());
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

    public T getPlayerData(UUID playerId, boolean insertIfMissing) throws SQLException {
        return getPlayerData(playerId, false, insertIfMissing, 0, false);
    }

    public abstract T getPlayerData(UUID playerId, boolean isOnline, boolean insertIfMissing, long lastAction, boolean manuallySetAfk) throws SQLException;

    protected Triple<Triple<Long, Long, Long>, Boolean, String> getPlayerDataData(UUID playerId, boolean isOnline, boolean insertIfMissing, long lastAction, boolean manuallySetAfk) throws SQLException {
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
                return getPlayerDataData(playerId, isOnline, insertIfMissing, lastAction, manuallySetAfk);
            }

            long firstJoin = rs.getLong(1);
            long lastJoin = rs.getLong(2);
            long lastSeen = rs.getLong(3);
            boolean afk = rs.getBoolean(4);
            String rank = rs.getString(5);
            Triple<Triple<Long, Long, Long>, Boolean, String> result = new Triple<>(new Triple<>(firstJoin, lastJoin, lastSeen), afk, rank);

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
            smt.setString(2, CubesideUtils.getInstance().getGlobalDataHelper().getThisServer().getName());
            smt.executeUpdate();
            return null;
        });
    }

    private void removeLocallyAfk(UUID playerId) throws SQLException {
        this.connection.runCommands((connection, sqlConnection) -> {
            PreparedStatement smt = sqlConnection.getOrCreateStatement(this.removeAfkServerQuery);
            smt.setString(1, playerId.toString());
            smt.setString(2, CubesideUtils.getInstance().getGlobalDataHelper().getThisServer().getName());
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
