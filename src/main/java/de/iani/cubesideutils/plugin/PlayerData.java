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

    public boolean isOnline() {
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
        if (!isOnline()) {
            throw new IllegalStateException("The player isn't online.");
        }

        // TODO: create OnlinePlayerData, replace in caches, return (synchronized!)
        // TODO: OR OnlinePlayerData must already have been created on login, get from cache.
        return null;
    }

    public synchronized boolean isAfk() {
        return this.afk;
    }

    protected synchronized boolean setAfkInternal(boolean afk) {
        if (this.afk == afk) {
            return false;
        }

        this.afk = afk;
        saveChanges();
        return true;
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
        saveChanges(false);
    }

    protected synchronized void saveChanges(boolean soft) {
        // TODO: save in database
        // TODO: send message to other servers if not soft
    }

}
