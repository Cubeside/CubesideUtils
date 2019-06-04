package de.iani.cubesideutils.plugin;

import de.cubeside.connection.ConnectionAPI;
import de.cubeside.connection.GlobalServer;
import java.util.Collection;
import java.util.Set;
import java.util.UUID;

public class OnlinePlayerData extends PlayerData {

    private long lastAction;
    private boolean locallyAfk;

    public OnlinePlayerData(UUID playerId, boolean afk, long lastAction, String rank) {
        super(playerId, afk, rank);

        this.lastAction = lastAction;
    }

    synchronized void quit() {
        setLocallyAfkInternal(true);
    }

    public synchronized void checkAfk() {
        if (this.isLocallyAfk()) {
            return;
        }

        if (System.currentTimeMillis() - this.lastAction < UtilsPlugin.AFK_THRESHOLD) {
            return;
        }

        setLocallyAfk(true);
    }

    public synchronized boolean isLocallyAfk() {
        return this.locallyAfk;
    }

    public synchronized void setLocallyAfk(boolean afk) {
        if (!setLocallyAfkInternal(afk)) {
            return;
        }

        // TODO: tell player
    }

    private synchronized boolean setLocallyAfkInternal(boolean afk) {
        if (this.locallyAfk == afk) {
            return false;
        }

        this.locallyAfk = afk;
        UtilsPlugin.getInstance().getDatabase().addLocallyAfk(this.getPlayerId());
        checkGloballyAfk();
        return true;
    }

    private void checkGloballyAfk() {
        if (this.locallyAfk && isGloballyAfk()) {
            return;
        }

        if (!this.locallyAfk && !isGloballyAfk()) {
            return;
        }

        if (!this.locallyAfk /* && isGloballyAfk() */) {
            setGloballyAfkInternal(false);
            return;
        }

        ConnectionAPI connectionApi = UtilsPlugin.getInstance().getConnectionAPI();
        Collection<GlobalServer> servers = connectionApi.getPlayer(getPlayerId()).getCurrentServers();
        assert servers.contains(connectionApi.getThisServer());

        if (servers.size() == 1) {
            setGloballyAfkInternal(true);
            return;
        }

        Set<String> afkServers = UtilsPlugin.getInstance().getDatabase().getAfkServers(getPlayerId());
        for (GlobalServer server : servers) {
            if (!afkServers.contains(server.getName())) {
                return;
            }
        }

        setGloballyAfkInternal(true);
        return;
    }

    public synchronized void madeAction() {
        this.lastAction = System.currentTimeMillis();

        if (isLocallyAfk()) {
            setLocallyAfk(false);
        }
    }

}
