package de.iani.cubesideutils.plugin;

import de.iani.cubesideutils.plugin.UtilsGlobalDataHelper.MessageType;
import de.iani.cubesideutils.plugin.api.PlayerData;
import java.sql.SQLException;
import java.util.Objects;
import java.util.UUID;
import java.util.logging.Level;

public abstract class PlayerDataImpl implements PlayerData {

    private UUID playerId;

    private long firstJoin;
    private long lastJoin;
    private long lastSeen;

    private boolean afk;

    private String rank;

    protected PlayerDataImpl(UUID playerId, long firstJoin, long lastJoin, long lastSeen, boolean afk, String rank) {
        this.playerId = Objects.requireNonNull(playerId);
        this.firstJoin = firstJoin;
        this.lastJoin = lastJoin;
        this.lastSeen = lastSeen;
        this.afk = afk;
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

        try {
            CubesideUtils.getInstance().getDatabase().setPlayerNameAndFirstJoinAndLastJoinAndSeen(this.playerId, value, name);
        } catch (SQLException e) {
            CubesideUtils.getInstance().getLogger().log(Level.SEVERE, "Exception trying to save firstJoin, lastJoin and lastSeen values for player " + this.playerId + " in database.", e);
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
        try {
            CubesideUtils.getInstance().getDatabase().setPlayerNameAndLastJoinAndSeen(this.playerId, value, name);
        } catch (SQLException e) {
            CubesideUtils.getInstance().getLogger().log(Level.SEVERE, "Exception trying to save lastJoin and lastSeen values for player " + this.playerId + " in database.", e);
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
    public synchronized String getRank() {
        return this.rank;
    }

    @Override
    public synchronized String getRankPrefix() {
        return this.rank == null ? null : CubesideUtils.getInstance().getPrefix(this.rank);
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

        if (this.rank != null && CubesideUtils.getInstance().getPermission(this.rank) != null) {
            return;
        }

        setRank(CubesideUtils.getInstance().getDefaultRank());
    }

    public synchronized void notifyChanges() {
        CubesideUtils.getInstance().getGlobalDataHelper().sendData(MessageType.PLAYER_DATA_CHANGED, this.playerId);
    }

}
