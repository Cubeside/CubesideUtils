package de.iani.cubesideutils.plugin;

import java.util.Objects;
import java.util.UUID;

public class PlayerData {

    private UUID playerId;

    private boolean afk;
    private long lastAction;

    private String rank;

    public PlayerData(UUID playerId) {
        this(playerId, false, UtilsPlugin.getInstance().getDefaultRank());
    }

    public PlayerData(UUID playerId, boolean afk, String rank) {
        this.playerId = Objects.requireNonNull(playerId);
        this.afk = afk;
        this.rank = Objects.requireNonNull(rank);
    }

    public UUID getPlayerId() {
        return this.playerId;
    }

    // TODO: what about player on two servers, afk at one? save lastAction in DB, but only on afk-change?
    public synchronized boolean isAfk() {
        return this.afk;
    }

    public synchronized void checkAfk() {
        if (this.afk) {
            return;
        }

        if (System.currentTimeMillis() - this.lastAction < UtilsPlugin.AFK_THRESHOLD) {
            this.afk = true;
            saveChanges();
        }
    }

    public synchronized void madeAction() {
        this.lastAction = System.currentTimeMillis();

        if (!this.afk) {
            return;
        }

        this.afk = false;
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

    private void saveChanges() {
        // TODO: save in database
        // TODO: send message to other servers
    }

}
