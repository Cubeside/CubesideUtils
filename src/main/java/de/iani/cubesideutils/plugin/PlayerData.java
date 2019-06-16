package de.iani.cubesideutils.plugin;

import java.sql.SQLException;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import org.bukkit.Bukkit;

public class PlayerData {

    private UUID playerId;

    private boolean afk;

    private String rank;

    public PlayerData(UUID playerId, boolean afk, String rank) {
        this.playerId = Objects.requireNonNull(playerId);
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

    public synchronized boolean isGloballyAfk() {
        return this.afk;
    }

    protected synchronized void setGloballyAfkInternal(boolean afk) {
        this.afk = afk;
        saveChanges();
    }

    public synchronized String getRank() {
        return rank;
    }

    public synchronized void setRank(String rank) {
        if (this.rank.equals(Objects.requireNonNull(rank))) {
            return;
        }

        this.rank = rank;
        saveChanges();
    }

    protected synchronized void saveChanges() {
        try {
            UtilsPlugin.getInstance().getDatabase().savePlayerData(this);
        } catch (SQLException e) {
            UtilsPlugin.getInstance().getLogger().log(Level.SEVERE, "Could not save PlayerData to database.", e);
        }
    }

}
