package de.iani.cubesideutils.plugin;

import de.cubeside.connection.ConnectionAPI;
import de.cubeside.connection.GlobalServer;
import de.iani.cubesideutils.plugin.events.LocalAfkStateChangeEvent;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Set;
import java.util.UUID;
import java.util.logging.Level;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class OnlinePlayerData extends PlayerData {

    private long lastAction;
    private boolean locallyAfk;

    OnlinePlayerData(UUID playerId, long firstJoin, long lastJoin, long lastSeen, boolean afk, long lastAction, String rank) {
        super(playerId, firstJoin, lastJoin, lastSeen, afk, rank);

        this.lastAction = lastAction;
    }

    OnlinePlayerData(UUID playerId, long firstJoin, long lastJoin, long lastSeen, boolean afk, String rank) {
        this(playerId, firstJoin, lastJoin, lastSeen, afk, System.currentTimeMillis(), rank);
    }

    synchronized void quit() {
        setLocallyAfkInternal(true);
    }

    public Player getPlayer() {
        return Bukkit.getPlayer(getPlayerId());
    }

    public synchronized long getLastAction() {
        return this.lastAction;
    }

    public synchronized void checkAfk() {
        if (this.isLocallyAfk()) {
            return;
        }

        if (System.currentTimeMillis() - this.lastAction < UtilsPlugin.AFK_THRESHOLD) {
            return;
        }

        if (Bukkit.isPrimaryThread()) {
            setLocallyAfk(true);
        } else {
            Bukkit.getScheduler().runTask(UtilsPlugin.getInstance(), () -> setLocallyAfk(true));
        }
    }

    public synchronized boolean isLocallyAfk() {
        return this.locallyAfk;
    }

    public synchronized void setLocallyAfk(boolean afk) {
        if (!setLocallyAfkInternal(afk)) {
            return;
        }

        Bukkit.getPlayer(getPlayerId()).sendMessage(ChatColor.GRAY + "* Du bist nun" + (this.locallyAfk ? "" : " nicht mehr") + " abwesend.");
    }

    private synchronized boolean setLocallyAfkInternal(boolean afk) {
        if (!Bukkit.isPrimaryThread()) {
            throw new IllegalStateException("May only be invoked on the bukkit primary thread.");
        }

        if (this.locallyAfk == afk) {
            return false;
        }

        LocalAfkStateChangeEvent event = new LocalAfkStateChangeEvent(this, afk);
        Bukkit.getPluginManager().callEvent(event);
        if (event.isCancelled()) {
            return false;
        }

        this.locallyAfk = afk;
        try {
            UtilsPlugin.getInstance().getDatabase().setLocallyAfk(this.getPlayerId(), afk);
        } catch (SQLException e) {
            UtilsPlugin.getInstance().getLogger().log(Level.SEVERE, "Could not save AFK-status in database.", e);
        }
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

        Set<String> afkServers;
        try {
            afkServers = UtilsPlugin.getInstance().getDatabase().getAfkServers(getPlayerId());
        } catch (SQLException e) {
            UtilsPlugin.getInstance().getLogger().log(Level.SEVERE, "Could not load AFK-status from database.", e);
            return;
        }
        for (GlobalServer server : servers) {
            if (!afkServers.contains(server.getName())) {
                return;
            }
        }

        setGloballyAfkInternal(true);
        return;
    }

    synchronized void madeAction() {
        this.lastAction = System.currentTimeMillis();

        if (isLocallyAfk()) {
            if (Bukkit.isPrimaryThread()) {
                setLocallyAfk(false);
            } else {
                Bukkit.getScheduler().runTask(UtilsPlugin.getInstance(), () -> setLocallyAfk(false));
            }
        }
    }

    @Override
    void checkRank() {
        String rank = null;
        for (String possible : UtilsPlugin.getInstance().getRanks()) {
            String permission = UtilsPlugin.getInstance().getPermission(possible);
            if (permission == null || getPlayer().hasPermission(permission)) {
                rank = possible;
                break;
            }
        }

        setRank(rank);
    }

}
