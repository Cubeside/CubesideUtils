package de.iani.cubesideutils.plugin;

import java.util.UUID;

public class OnlinePlayerData extends PlayerData {

    private static final long SAVE_LAST_ACTION_THRESNHOLD = (long) (0.75 * UtilsPlugin.AFK_THRESHOLD);

    private long lastAction;
    private long lastSaved;

    public OnlinePlayerData(UUID playerId, boolean afk, long lastAction, String rank) {
        super(playerId, afk, rank);

        this.lastAction = lastAction;
        this.lastSaved = System.currentTimeMillis();
    }

    public synchronized void checkAfk() {
        if (this.isAfk()) {
            return;
        }

        if (System.currentTimeMillis() - this.lastAction < UtilsPlugin.AFK_THRESHOLD) {
            return;
        }

        // reaload from database

        if (System.currentTimeMillis() - this.lastAction < UtilsPlugin.AFK_THRESHOLD) {
            return;
        }

        setAfk(true);
    }

    public synchronized void setAfk(boolean afk) {
        if (!super.setAfkInternal(afk)) {
            return;
        }

        // tell player
    }

    public synchronized void madeAction() {
        this.lastAction = System.currentTimeMillis();

        if (isAfk()) {
            setAfk(false);
        } else if (System.currentTimeMillis() - this.lastSaved >= SAVE_LAST_ACTION_THRESNHOLD) {
            // TODO: instead check whether online on any other server first?
            saveChanges(true);
        }
    }

    @Override
    protected synchronized void saveChanges(boolean soft) {
        super.saveChanges(soft);
        this.lastSaved = System.currentTimeMillis();
    }

}
