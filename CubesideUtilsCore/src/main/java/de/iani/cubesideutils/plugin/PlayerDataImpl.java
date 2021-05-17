package de.iani.cubesideutils.plugin;

import de.iani.cubesideutils.plugin.UtilsGlobalDataHelper.MessageType;
import de.iani.cubesideutils.plugin.api.PlayerData;
import java.sql.SQLException;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.logging.Level;

public abstract class PlayerDataImpl implements PlayerData {

    private UUID playerId;

    private Map<String, String> customData;

    private long firstJoin;
    private long lastJoin;
    private long lastSeen;

    private boolean afk;

    private String lastName;
    private String rank;

    protected PlayerDataImpl(UUID playerId, long firstJoin, long lastJoin, long lastSeen, boolean afk, String lastName, String rank) {
        this.playerId = Objects.requireNonNull(playerId);
        this.firstJoin = firstJoin;
        this.lastJoin = lastJoin;
        this.lastSeen = lastSeen;
        this.afk = afk;
        this.lastName = lastName;
        this.rank = rank;

        postConstruction();
    }

    protected void postConstruction() {
        checkRank();
    }

    @Override
    public UUID getPlayerId() {
        return this.playerId;
    }

    @Override
    public synchronized String getCustomData(String key) {
        ensureCustomDataPresent();
        return this.customData.get(key);
    }

    @Override
    public synchronized String setCustomData(String key, String value) {
        if (key.length() > PlayerData.MAX_CUSTOM_DATA_KEY_LENGTH) {
            throw new IllegalArgumentException("key is too long");
        }

        ensureCustomDataPresent();
        String result = this.customData.put(key, value);
        try {
            CubesideUtils.getInstance().getDatabase().setCustomPlayerData(this.playerId, key, value);
        } catch (SQLException e) {
            CubesideUtils.getInstance().getLogger().log(Level.SEVERE, "Exception trying to save customData for player " + this.playerId + " in database.", e);
            return result;
        }
        notifyCustomDataChanged();
        return result;
    }

    @Override
    public synchronized String removeCustomData(String key) {
        ensureCustomDataPresent();
        String result = this.customData.remove(key);
        try {
            CubesideUtils.getInstance().getDatabase().removeCustomPlayerData(this.playerId, key);
        } catch (SQLException e) {
            CubesideUtils.getInstance().getLogger().log(Level.SEVERE, "Exception trying to save customData for player " + this.playerId + " in database.", e);
            return result;
        }
        notifyCustomDataChanged();
        return result;
    }

    private void ensureCustomDataPresent() {
        if (this.customData == null) {
            try {
                this.customData = CubesideUtils.getInstance().getDatabase().getCustomPlayerData(this.playerId);
            } catch (SQLException e) {
                CubesideUtils.getInstance().getLogger().log(Level.SEVERE, "Exception trying to get customData for player " + this.playerId + " from database.", e);
                return; // NPE incoming...
            }
        }
    }

    private void notifyCustomDataChanged() {
        CubesideUtils.getInstance().getGlobalDataHelper().sendData(MessageType.CUSTOM_PLAYER_DATA_CHANGED, this.playerId);
    }

    public synchronized void customDataChanged() {
        this.customData = null;
    }

    @Override
    public synchronized long getFirstJoin() {
        return this.firstJoin;
    }

    public synchronized void setNameAndFirstJoinAndLastJoinAndSeen(long value, String name) {
        if (this.firstJoin != 0) {
            throw new IllegalStateException("player already had a first join");
        }

        this.firstJoin = value;
        this.lastJoin = value;
        this.lastSeen = value;
        this.lastName = name;

        try {
            CubesideUtils.getInstance().getDatabase().setPlayerNameAndFirstJoinAndLastJoinAndSeen(this.playerId, value, name);
        } catch (SQLException e) {
            CubesideUtils.getInstance().getLogger().log(Level.SEVERE, "Exception trying to save name, firstJoin, lastJoin and lastSeen values for player " + this.playerId + " in database.", e);
            return;
        }
        notifyChanges();
    }

    @Override
    public synchronized long getLastJoin() {
        return this.lastJoin;
    }

    public synchronized void setNameAndLastJoinAndSeen(long value, String name) {
        if (this.firstJoin == 0) {
            throw new IllegalStateException("player had no first join yet");
        }

        this.lastJoin = value;
        this.lastSeen = value;
        this.lastName = name;

        try {
            CubesideUtils.getInstance().getDatabase().setPlayerNameAndLastJoinAndSeen(this.playerId, value, name);
        } catch (SQLException e) {
            CubesideUtils.getInstance().getLogger().log(Level.SEVERE, "Exception trying to save name, lastJoin and lastSeen values for player " + this.playerId + " in database.", e);
            return;
        }
        notifyChanges();
    }

    @Override
    public synchronized long getLastSeen() {
        return this.lastSeen;
    }

    public synchronized void setLastSeen(long lastSeen) {
        if (this.firstJoin == 0) {
            throw new IllegalStateException("player had no first join yet");
        }

        this.lastSeen = lastSeen;
        try {
            CubesideUtils.getInstance().getDatabase().setPlayerLastSeen(this.playerId, lastSeen);
        } catch (SQLException e) {
            CubesideUtils.getInstance().getLogger().log(Level.SEVERE, "Exception trying to save lastSeen value for player " + this.playerId + " in database.", e);
            return;
        }
        notifyChanges();
    }

    @Override
    public synchronized boolean isGloballyAfk() {
        return this.afk;
    }

    protected synchronized void setGloballyAfk(boolean afk) {
        this.afk = afk;
    }

    @Override
    public synchronized String getLastName() {
        return this.lastName;
    }

    @Override
    public synchronized String getRank() {
        return this.rank;
    }

    @Override
    public synchronized String getRankPrefix() {
        return this.rank == null ? null : CubesideUtils.getInstance().getPrefix(this.rank);
    }

    @Override
    public synchronized int getRankPriority() {
        return this.rank == null ? 0 : CubesideUtils.getInstance().getPriority(this.rank);
    }

    public synchronized void setRank(String rank) {
        if (Objects.equals(this.rank, rank)) {
            return;
        }

        this.rank = rank;
        try {
            CubesideUtils.getInstance().getDatabase().setRank(this.playerId, rank);
        } catch (SQLException e) {
            CubesideUtils.getInstance().getLogger().log(Level.SEVERE, "Exception trying to save rank for player " + this.playerId + " in database.", e);
            return;
        }
        notifyChanges();
    }

    public void checkRank() {
        // Overwritten in OnlinePlayer

        if (this.rank != null && CubesideUtils.getInstance().isRank(this.rank)) {
            return;
        }

        setRank(CubesideUtils.getInstance().getDefaultRank());
    }

    public synchronized void notifyChanges() {
        CubesideUtils.getInstance().getGlobalDataHelper().sendData(MessageType.PLAYER_DATA_CHANGED, this.playerId);
    }

}
