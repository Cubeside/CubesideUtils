package de.iani.cubesideutils.plugin;

import de.iani.cubesideutils.plugin.ConnectionUtil.MessageType;
import java.sql.SQLException;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import org.bukkit.Bukkit;

public class PlayerData {

    private UUID playerId;

    private long firstJoin;
    private long lastJoin;
    private long lastSeen;

    private boolean afk;

    private String rank;

    public PlayerData(UUID playerId, long firstJoin, long lastJoin, long lastSeen, boolean afk, String rank) {
        this.playerId = Objects.requireNonNull(playerId);
        this.firstJoin = firstJoin;
        this.lastJoin = lastJoin;
        this.lastSeen = lastSeen;
        this.afk = afk;
        this.rank = Objects.requireNonNull(rank);
    }

    public UUID getPlayerId() {
        return this.playerId;
    }

    public boolean isOnlineHere() {
        if (Bukkit.isPrimaryThread()) {
            return Bukkit.getPlayer(playerId) != null;
        }

        try {
            return Bukkit.getScheduler().callSyncMethod(UtilsPlugin.getInstance(), () -> (Bukkit.getPlayer(playerId) != null)).get();
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

    public OnlinePlayerData getOnlineData() {
        if (this instanceof OnlinePlayerData) {
            return (OnlinePlayerData) this;
        }

        // TODO: possible that player is online and this is no OnlinePlayerData?
        if (!isOnlineHere()) {
            throw new IllegalStateException("The player isn't online.");
        }

        // TODO: should this happen?!
        return UtilsPlugin.getInstance().getPlayerDataCache().getOnline(this.playerId);
    }

    public synchronized long getFirstJoin() {
        return this.firstJoin;
    }

    public synchronized void setFirstJoinAndLastJoinAndSeen(long value) {
        if (this.firstJoin != 0) {
            throw new IllegalStateException("player already had a first join");
        }

        this.firstJoin = value;
        this.lastJoin = value;
        this.lastSeen = value;

        try {
            UtilsPlugin.getInstance().getDatabase().setPlayerFirstJoinAndLastJoinAndSeen(getPlayerId(), value);
        } catch (SQLException e) {
            UtilsPlugin.getInstance().getLogger().log(Level.SEVERE, "Exception trying to save firstJoin, lastJoin and lastSeen values for player " + this.playerId + " in database.", e);
            return;
        }
        notifyChanges();
    }

    public synchronized long getLastJoin() {
        return this.lastJoin;
    }

    public synchronized void setLastJoinAndSeen(long value) {
        if (this.firstJoin == 0) {
            throw new IllegalStateException("player had no first join yet");
        }

        this.lastJoin = value;
        this.lastSeen = value;
        try {
            UtilsPlugin.getInstance().getDatabase().setPlayerLastJoinAndSeen(getPlayerId(), value);
        } catch (SQLException e) {
            UtilsPlugin.getInstance().getLogger().log(Level.SEVERE, "Exception trying to save lastJoin and lastSeen values for player " + this.playerId + " in database.", e);
            return;
        }
        notifyChanges();
    }

    public synchronized long getLastSeen() {
        return this.lastSeen;
    }

    public synchronized void setLastSeen(long lastSeen) {
        if (this.firstJoin == 0) {
            throw new IllegalStateException("player had no first join yet");
        }

        this.lastSeen = lastSeen;
        try {
            UtilsPlugin.getInstance().getDatabase().setPlayerLastSeen(getPlayerId(), lastSeen);
        } catch (SQLException e) {
            UtilsPlugin.getInstance().getLogger().log(Level.SEVERE, "Exception trying to save lastSeen value for player " + this.playerId + " in database.", e);
            return;
        }
        notifyChanges();
    }

    public synchronized boolean isGloballyAfk() {
        return this.afk;
    }

    protected synchronized void setGloballyAfkInternal(boolean afk) {
        this.afk = afk;
        try {
            UtilsPlugin.getInstance().getDatabase().setGloballyAfk(playerId, afk);
        } catch (SQLException e) {
            UtilsPlugin.getInstance().getLogger().log(Level.SEVERE, "Exception trying to save afk value for player " + this.playerId + " in database.", e);
            return;
        }
        notifyChanges();
    }

    public synchronized String getRank() {
        return rank;
    }

    public synchronized void setRank(String rank) {
        if (this.rank.equals(Objects.requireNonNull(rank))) {
            return;
        }

        this.rank = rank;
        // TODO: database
        notifyChanges();
    }

    protected synchronized void notifyChanges() {
        ConnectionUtil.sendData(MessageType.PLAYER_DATA_CHANGED, playerId);
    }

}
