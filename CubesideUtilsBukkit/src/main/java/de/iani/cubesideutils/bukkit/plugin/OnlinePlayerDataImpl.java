package de.iani.cubesideutils.bukkit.plugin;

import de.cubeside.connection.GlobalServer;
import de.iani.cubesideutils.bukkit.plugin.api.OnlinePlayerData;
import de.iani.cubesideutils.bukkit.plugin.api.events.GlobalAfkStateChangeEvent;
import de.iani.cubesideutils.bukkit.plugin.api.events.LocalAfkStateChangeEvent;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class OnlinePlayerDataImpl extends PlayerDataImplBukkit implements OnlinePlayerData {

    private long lastLocalAction;
    private boolean manuallySetAfk;
    private boolean locallyAfk;

    public OnlinePlayerDataImpl(UUID playerId, long firstJoin, long lastJoin, long lastSeen, boolean afk, long lastAction, boolean manuallySetAfk, String rank) {
        super(playerId, firstJoin, lastJoin, lastSeen, afk, rank);

        this.lastLocalAction = lastAction;
        this.manuallySetAfk = manuallySetAfk;
        this.locallyAfk = afk;
    }

    @Override
    protected void postConstruction() {
        // no checkRank, done later in playerLoginEvent
    }

    public synchronized void quit() {
        this.setLocallyAfk(false, false);
        Bukkit.getScheduler().scheduleSyncDelayedTask(CubesideUtilsBukkit.getInstance().getPlugin(), this::notifyChanges);
    }

    @Override
    public Player getPlayer() {
        Player result = Bukkit.getPlayer(getPlayerId());
        if (result == null) {
            result = CubesideUtilsBukkit.getInstance().getPlayerDataCache().getCurrentlyLoggingInPlayer(); // TODO: hä?
            if (result == null || !result.getUniqueId().equals(getPlayerId())) {
                return null;
            }
        }
        return result;
    }

    @Override
    public synchronized long getLastAction() {
        return this.lastLocalAction;
    }

    public synchronized void madeAction() {
        this.lastLocalAction = System.currentTimeMillis();
        this.manuallySetAfk = false;
        checkAfk(true);
    }

    public synchronized void checkAfk(boolean messagePlayer) {
        boolean afk = System.currentTimeMillis() >= this.lastLocalAction + AfkManager.AFK_THRESHOLD;
        if (afk == isLocallyAfk() || (!afk && manuallySetAfk)) {
            return;
        }

        if (Bukkit.isPrimaryThread()) {
            setLocallyAfk(afk, messagePlayer);
        } else {
            Bukkit.getScheduler().runTask(CubesideUtilsBukkit.getInstance().getPlugin(), () -> setLocallyAfk(afk, messagePlayer));
        }
    }

    @Override
    public synchronized boolean isLocallyAfk() {
        return this.locallyAfk;
    }

    @Override
    public synchronized boolean isManuallySetAfk() {
        return this.manuallySetAfk;
    }

    @Override
    public synchronized void manuallySetAfk(boolean messagePlayer) {
        this.manuallySetAfk = true;
        setLocallyAfk(true, messagePlayer);
    }

    public synchronized void setLocallyAfk(boolean afk, boolean messagePlayer) {
        if (!Bukkit.isPrimaryThread()) {
            throw new IllegalStateException("May only be invoked on the bukkit primary thread.");
        }

        if (this.locallyAfk == afk) {
            return;
        }

        LocalAfkStateChangeEvent event = new LocalAfkStateChangeEvent(this, afk);
        Bukkit.getPluginManager().callEvent(event);
        if (event.isCancelled()) {
            return;
        }

        this.locallyAfk = afk;
        if (!afk) {
            this.manuallySetAfk = false;
        }

        try {
            CubesideUtilsBukkit.getInstance().getDatabase().setLocallyAfk(this.getPlayerId(), afk);
        } catch (SQLException e) {
            CubesideUtilsBukkit.getInstance().getLogger().log(Level.SEVERE, "Could not save AFK-status in database.", e);
        }
        checkGloballyAfk();

        if (messagePlayer) {
            getPlayer().sendMessage(ChatColor.GRAY + "* Du bist nun" + (this.locallyAfk ? "" : " nicht mehr") + " abwesend.");
        }
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

        UtilsGlobalDataHelperBukkit globalHelper = CubesideUtilsBukkit.getInstance().getGlobalDataHelper();
        Collection<GlobalServer> servers = globalHelper.getServers(getPlayerId());
        assert servers.contains(globalHelper.getThisServer());

        if (servers.size() == 1) {
            setGloballyAfkInternal(true);
            return;
        }

        Set<String> afkServers;
        try {
            afkServers = CubesideUtilsBukkit.getInstance().getDatabase().getAfkServers(getPlayerId());
        } catch (SQLException e) {
            CubesideUtilsBukkit.getInstance().getLogger().log(Level.SEVERE, "Could not load AFK-status from database.", e);
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

    private void setGloballyAfkInternal(boolean afk) {
        if (afk == isGloballyAfk()) {
            return;
        }

        GlobalAfkStateChangeEvent event = new GlobalAfkStateChangeEvent(this, afk);
        if (Bukkit.isPrimaryThread()) {
            Bukkit.getPluginManager().callEvent(event);
        } else {
            try {
                Bukkit.getScheduler().callSyncMethod(CubesideUtilsBukkit.getInstance().getPlugin(), () -> event.callEvent()).get();
            } catch (InterruptedException | ExecutionException e) {
                throw new RuntimeException(e);
            }
        }

        setGloballyAfk(afk);

        try {
            CubesideUtilsBukkit.getInstance().getDatabase().setGloballyAfk(getPlayerId(), afk);
        } catch (SQLException e) {
            CubesideUtilsBukkit.getInstance().getLogger().log(Level.SEVERE, "Exception trying to save afk value for player " + getPlayerId() + " in database.", e);
            return;
        }

        notifyChanges();
    }

    @Override
    public void checkRank() {
        String rank = null;
        for (String possible : CubesideUtilsBukkit.getInstance().getRanks()) {
            String permission = CubesideUtilsBukkit.getInstance().getPermission(possible);
            if (permission == null || getPlayer().hasPermission(permission)) {
                rank = possible;
                break;
            }
        }

        setRank(rank);
    }

}
