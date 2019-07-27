package de.iani.cubesideutils.plugin;

import de.iani.cubesideutils.plugin.events.GlobalAfkStateChangeEvent;
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

    PlayerData(UUID playerId, long firstJoin, long lastJoin, long lastSeen, boolean afk, String rank) {
        this.playerId = Objects.requireNonNull(playerId);
        this.firstJoin = firstJoin;
        this.lastJoin = lastJoin;
        this.lastSeen = lastSeen;
        this.afk = afk;
        this.rank = rank;

        checkRank();
    }

    public UUID getPlayerId() {
        return this.playerId;
    }

    public boolean isOnlineHere() {
        if (Bukkit.isPrimaryThread()) {
            return Bukkit.getPlayer(this.playerId) != null;
        }

        try {
            return Bukkit.getScheduler().callSyncMethod(UtilsPlugin.getInstance(), () -> (Bukkit.getPlayer(this.playerId) != null)).get();
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
            UtilsPlugin.getInstance().getDatabase().setPlayerFirstJoinAndLastJoinAndSeen(this.playerId, value);
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
            UtilsPlugin.getInstance().getDatabase().setPlayerLastJoinAndSeen(this.playerId, value);
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
            UtilsPlugin.getInstance().getDatabase().setPlayerLastSeen(this.playerId, lastSeen);
        } catch (SQLException e) {
            UtilsPlugin.getInstance().getLogger().log(Level.SEVERE, "Exception trying to save lastSeen value for player " + this.playerId + " in database.", e);
            return;
        }
        notifyChanges();
    }

    public synchronized boolean isGloballyAfk() {
        return this.afk;
    }

    synchronized void setGloballyAfkInternal(boolean afk) {
        if (this.afk == afk) {
            return;
        }

        GlobalAfkStateChangeEvent event = new GlobalAfkStateChangeEvent(this, afk);
        if (Bukkit.isPrimaryThread()) {
            Bukkit.getPluginManager().callEvent(event);
        } else {
            try {
                Bukkit.getScheduler().callSyncMethod(UtilsPlugin.getInstance(), () -> event.callEvent()).get();
            } catch (InterruptedException | ExecutionException e) {
                throw new RuntimeException(e);
            }
        }

        this.afk = afk;
        try {
            UtilsPlugin.getInstance().getDatabase().setGloballyAfk(this.playerId, afk);
        } catch (SQLException e) {
            UtilsPlugin.getInstance().getLogger().log(Level.SEVERE, "Exception trying to save afk value for player " + this.playerId + " in database.", e);
            return;
        }
        notifyChanges();
    }

    public synchronized String getRank() {
        return this.rank;
    }

    public synchronized String getRankPrefix() {
        return this.rank == null ? null : UtilsPlugin.getInstance().getPrefix(this.rank);
    }

    synchronized void setRank(String rank) {
        if (Objects.equals(this.rank, rank)) {
            return;
        }

        this.rank = rank;
        try {
            UtilsPlugin.getInstance().getDatabase().setRank(this.playerId, rank);
        } catch (SQLException e) {
            UtilsPlugin.getInstance().getLogger().log(Level.SEVERE, "Exception trying to save rank for player " + this.playerId + " in database.", e);
            return;
        }
        notifyChanges();
    }

    void checkRank() {
        // Overwritten in OnlinePlayer

        if (this.rank != null && UtilsPlugin.getInstance().getPermission(this.rank) != null) {
            return;
        }

        setRank(UtilsPlugin.getInstance().getDefaultRank());
    }

    synchronized void notifyChanges() {
        UtilsPlugin.getInstance().getGlobalDataHelper().sendData(MessageType.PLAYER_DATA_CHANGED, this.playerId);
    }

}
