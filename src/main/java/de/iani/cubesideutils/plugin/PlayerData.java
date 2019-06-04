package de.iani.cubesideutils.plugin;

import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
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

        // TODO: possible that player is online of this is no OnlinePlayerData?
        if (!isOnlineHere()) {
            throw new IllegalStateException("The player isn't online.");
        }

        // TODO: should this happen?!
        return UtilsPlugin.getInstance().getPlayerDataCache().getOnline(this.playerId);
    }

    public synchronized boolean isGloballyAfk() {
        return this.afk; // More complex in OnlinePlayerData
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
        // TODO: save in database
    }

}
