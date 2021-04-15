package de.iani.cubesideutils.plugin;

import de.iani.cubesideutils.Pair;
import de.iani.cubesideutils.Triple;
import de.iani.cubesideutils.plugin.api.PasswordHandler;
import de.iani.cubesideutils.plugin.api.PlayerData;
import de.iani.cubesideutils.plugin.api.UtilsApi;
import de.iani.cubesideutils.sql.MySQLConnection;
import de.iani.cubesideutils.sql.SQLConfig;
import de.iani.cubesideutils.sql.SQLConnection;
import de.iani.cubesideutils.sql.SQLUtil;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public abstract class UtilsDatabase<T extends PlayerDataImpl> {

    protected static class PlayerDataStruct {

        public final long firstJoin;
        public final long lastJoin;
        public final long lastSeen;
        public final boolean afk;
        public final String lastName;
        public final String rank;

        public PlayerDataStruct(long firstJoin, long lastJoin, long lastSeen, boolean afk, String lastName, String rank) {
            this.firstJoin = firstJoin;
            this.lastJoin = lastJoin;
            this.lastSeen = lastSeen;
            this.afk = afk;
            this.lastName = lastName;
            this.rank = rank;
        }

    }

    private SQLConnection connection;
    private String tablePrefix;

    private String generalDataTableName;
    private String realServersTableName;
    private String playerDataTableName;
    private String customPlayerDataTableName;
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
    private String setPlayerNameAndFirstJoinAndLastJoinAndSeenQuery;
    private String setPlayerNameAndLastJoinAndSeenQuery;
    private String setPlayerLastSeenQuery;
    private String setPlayerAfkQuery;
    private String setPlayerRankQuery;

    private String getCustomPlayerDataQuery;
    private String setCustomPlayerDataQuery;
    private String removeCustomPlayerDataQuery;

    private String getPlayerIdsByPartialNameQuery;

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
        this.customPlayerDataTableName = this.tablePrefix + "_customPlayerData";
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

        this.getPlayerDataQuery = "SELECT firstJoin, lastJoin, lastSeen, afk, name, `rank` FROM `" + this.playerDataTableName + "` WHERE playerId = ?";
        this.addPlayerDataQuery = "INSERT INTO `" + this.playerDataTableName + "` (playerId, name, afk, `rank`) VALUES (?, NULL, 0, NULL)";
        this.setPlayerNameAndFirstJoinAndLastJoinAndSeenQuery = "UPDATE `" + this.playerDataTableName + "` SET name = ?, firstJoin = ?, lastJoin = ?, lastSeen = ? WHERE playerId = ?";
        this.setPlayerNameAndLastJoinAndSeenQuery = "UPDATE `" + this.playerDataTableName + "` SET name = ?, lastJoin = ?, lastSeen = ? WHERE playerId = ?";
        this.setPlayerLastSeenQuery = "UPDATE `" + this.playerDataTableName + "` SET lastSeen = ? WHERE playerId = ?";
        this.setPlayerAfkQuery = "UPDATE `" + this.playerDataTableName + "` SET afk = ? WHERE playerId = ?";
        this.setPlayerRankQuery = "UPDATE `" + this.playerDataTableName + "` SET `rank` = ? WHERE playerId = ?";

        this.getCustomPlayerDataQuery = "SELECT `key`, `value` FROM `" + this.customPlayerDataTableName + "` WHERE playerId = ?";
        this.setCustomPlayerDataQuery = "INSERT INTO `" + this.customPlayerDataTableName + "` (playerId, `key`, `value`) VALUES (?, ?, ?) ON DUPLICATE KEY UPDATE `value` = ?";
        this.removeCustomPlayerDataQuery = "DELETE FROM `" + this.customPlayerDataTableName + "` WHERE playerId = ? AND `key` = ?";

        this.getPlayerIdsByPartialNameQuery = "SELECT playerId, name FROM `" + this.playerDataTableName + "` WHERE name LIKE ? ORDER BY lastSeen DESC";

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
                smt.executeUpdate("CREATE TABLE `" + this.generalDataTableName + "` (" + "`key` VARCHAR(" + UtilsApi.MAX_GENERAL_DATA_KEY_LENGTH + "), " + "value MEDIUMTEXT, " + "PRIMARY KEY (`key`)) " + "ENGINE = innodb");
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
                smt.executeUpdate("CREATE TABLE `" + this.playerDataTableName + "` (" + "playerId CHAR(36), " + "name VARCHAR(16), " + "firstJoin BIGINT NOT NULL DEFAULT 0, " + "lastJoin BIGINT NOT NULL DEFAULT 0, " + "lastSeen BIGINT NOT NULL DEFAULT 0, " + "afk BIT NOT NULL, "
                        + "`rank` VARCHAR(64), " + "PRIMARY KEY (playerId), " + "INDEX (name)" + ") ENGINE = innodb");
                smt.close();
            } else if (!sqlConnection.hasColumn(this.playerDataTableName, "name")) {
                Statement smt = connection.createStatement();
                smt.executeUpdate("ALTER TABLE `" + this.playerDataTableName + "` ADD name VARCHAR(16)");
                smt.close();
            }
            if (!sqlConnection.hasTable(this.customPlayerDataTableName)) {
                Statement smt = connection.createStatement();
                smt.executeUpdate("CREATE TABLE `" + this.customPlayerDataTableName + "` (" + "playerId CHAR(36), " + "`key` VARCHAR(" + PlayerData.MAX_CUSTOM_DATA_KEY_LENGTH + "), " + "value MEDIUMTEXT, " + "PRIMARY KEY (playerId, `key`), " + "INDEX (playerId)" + ") ENGINE = innodb");
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

            Pair<byte[], byte[]> result = rs.next() ? new Pair<>(rs.getBytes(1), rs.getBytes(2)) : null;

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

    protected PlayerDataStruct getPlayerDataData(UUID playerId, boolean isOnline, boolean insertIfMissing, long lastAction, boolean manuallySetAfk) throws SQLException {
        return this.connection.runCommands((connection, sqlConnection) -> {
            PreparedStatement smt = sqlConnection.getOrCreateStatement(this.getPlayerDataQuery);
            smt.setString(1, playerId.toString());
            ResultSet rs = smt.executeQuery();

            if (!rs.next()) {
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
            String lastName = rs.getString(5);
            String rank = rs.getString(6);
            PlayerDataStruct result = new PlayerDataStruct(firstJoin, lastJoin, lastSeen, afk, lastName, rank);

            rs.close();
            return result;
        });
    }

    public void setPlayerNameAndFirstJoinAndLastJoinAndSeen(UUID playerId, long value, String name) throws SQLException {
        this.connection.runCommands((connection, sqlConnection) -> {
            PreparedStatement smt = sqlConnection.getOrCreateStatement(this.setPlayerNameAndFirstJoinAndLastJoinAndSeenQuery);
            smt.setString(1, name);
            smt.setLong(2, value);
            smt.setLong(3, value);
            smt.setLong(4, value);
            smt.setString(5, playerId.toString());
            smt.executeUpdate();
            return null;
        });
    }

    public void setPlayerNameAndLastJoinAndSeen(UUID playerId, long value, String name) throws SQLException {
        this.connection.runCommands((connection, sqlConnection) -> {
            PreparedStatement smt = sqlConnection.getOrCreateStatement(this.setPlayerNameAndLastJoinAndSeenQuery);
            smt.setString(1, name);
            smt.setLong(2, value);
            smt.setLong(3, value);
            smt.setString(4, playerId.toString());
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

    public Map<String, String> getCustomPlayerData(UUID playerId) throws SQLException {
        return this.connection.runCommands((connection, sqlConnection) -> {
            PreparedStatement smt = sqlConnection.getOrCreateStatement(this.getCustomPlayerDataQuery);
            smt.setString(1, playerId.toString());
            ResultSet rs = smt.executeQuery();

            Map<String, String> result = new HashMap<>();
            while (rs.next()) {
                String key = rs.getString(1);
                String value = rs.getString(2);
                result.put(key, value);
            }
            rs.close();
            return result;
        });
    }

    public void setCustomPlayerData(UUID playerId, String key, String value) throws SQLException {
        this.connection.runCommands((connection, sqlConnection) -> {
            PreparedStatement smt = sqlConnection.getOrCreateStatement(this.setCustomPlayerDataQuery);
            smt.setString(1, playerId.toString());
            smt.setString(2, key);
            smt.setString(3, value);
            smt.setString(4, value);
            smt.executeUpdate();
            return null;
        });
    }

    public void removeCustomPlayerData(UUID playerId, String key) throws SQLException {
        this.connection.runCommands((connection, sqlConnection) -> {
            PreparedStatement smt = sqlConnection.getOrCreateStatement(this.removeCustomPlayerDataQuery);
            smt.setString(1, playerId.toString());
            smt.setString(2, key);
            smt.executeUpdate();
            return null;
        });
    }

    protected List<Pair<UUID, String>> getPlayerIdsByPartialName(String partialName) throws SQLException {
        return this.connection.runCommands((connection, sqlConnection) -> {
            PreparedStatement smt = sqlConnection.getOrCreateStatement(this.getPlayerIdsByPartialNameQuery);
            smt.setString(1, "%" + SQLUtil.escapeLike(partialName) + "%");
            ResultSet rs = smt.executeQuery();

            List<Pair<UUID, String>> result = new ArrayList<>();
            while (rs.next()) {
                UUID playerId = UUID.fromString(rs.getString(1));
                String name = rs.getString(2);
                result.add(new Pair<>(playerId, name));
            }
            rs.close();
            return result;
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

    @Deprecated
    public Set<UUID> getUsedUUIDs() throws SQLException {
        return this.connection.runCommands((connection, sqlConnection) -> {
            PreparedStatement smt = sqlConnection.getOrCreateStatement("SELECT playerId FROM `" + this.playerDataTableName + "`");
            ResultSet rs = smt.executeQuery();

            Set<UUID> result = new HashSet<>();
            while (rs.next()) {
                result.add(UUID.fromString(rs.getString(1)));
            }

            return result;
        });
    }

}
