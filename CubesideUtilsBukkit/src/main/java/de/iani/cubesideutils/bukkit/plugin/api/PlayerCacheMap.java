package de.iani.cubesideutils.bukkit.plugin.api;

import de.iani.cubesideutils.bukkit.plugin.CubesideUtilsBukkit;
import de.iani.cubesideutils.collections.AdvancedCacheMap;
import de.iani.cubesideutils.plugin.CubesideUtils;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.logging.Level;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerLoginEvent.Result;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitScheduler;

public abstract class PlayerCacheMap<V, D> extends AdvancedCacheMap<UUID, V, D> implements Listener {

    public static final long BETWEEN_LOGIN_AND_JOIN_TIMEOUT = 5 * 60 * 20L; // 5 minutes in ticks

    private static final long serialVersionUID = 9162727527421482928L;

    public static class LoadingPlayerDataFailedException extends Exception {

        private static final long serialVersionUID = 4824440510991755719L;

        private String kickMessage;

        public LoadingPlayerDataFailedException(String kickMessage) {
            super();
            this.kickMessage = Objects.requireNonNull(kickMessage);
        }

        public LoadingPlayerDataFailedException(String kickMessage, String message, Throwable cause) {
            super(message, cause);
            this.kickMessage = Objects.requireNonNull(kickMessage);
        }

        public LoadingPlayerDataFailedException(String kickMessage, String message) {
            super(message);
            this.kickMessage = Objects.requireNonNull(kickMessage);
        }

        public LoadingPlayerDataFailedException(String kickMessage, Throwable cause) {
            super(cause);
            this.kickMessage = Objects.requireNonNull(kickMessage);
        }

        public String getKickMessage() {
            return kickMessage;
        }

    }

    private String valueLoggingName;
    private Map<UUID, Integer> playersAwaitingJoin;

    protected PlayerCacheMap(int maxSoftCacheSize, D defaultData, String valueLoggingName) {
        super(maxSoftCacheSize, defaultData);

        this.valueLoggingName = Objects.requireNonNull(valueLoggingName);
        this.playersAwaitingJoin = new HashMap<>();
        Bukkit.getPluginManager().registerEvents(this, CubesideUtilsBukkit.getInstance().getPlugin());
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void earlyOnPlayerLoginEvent(PlayerLoginEvent event) {
        Player player = event.getPlayer();
        playerStartsLoggingIn(player);
        UUID playerId = player.getUniqueId();
        V value;
        try {
            value = loadOnLogin(player);
        } catch (LoadingPlayerDataFailedException e) {
            CubesideUtils.getInstance().getLogger().log(Level.SEVERE, "Could not load " + this.valueLoggingName + " for player " + playerId + ".", e);
            CubesideUtils.getInstance().getLogger().log(Level.SEVERE, "Denying join for player " + playerId + " because of an internal error.");
            event.disallow(Result.KICK_OTHER, LegacyComponentSerializer.legacySection().deserialize(e.getKickMessage()));
            return;
        }
        if (value != null) {
            this.addToHardCache(playerId, value);
        }
        playerDataLoadedOnLogin(player, value);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void lateOnPlayerLoginEvent(PlayerLoginEvent event) {
        Player player = event.getPlayer();
        UUID playerId = player.getUniqueId();
        playerFinishsLoggingIn(player);
        if (event.getResult() != PlayerLoginEvent.Result.ALLOWED) {
            V value = this.removeFromHardCache(playerId);
            this.playerDataUnloadedOnSuccesslessLogin(player, value);
            return;
        }

        BukkitScheduler scheduler = Bukkit.getScheduler();
        Integer oldTaskId = this.playersAwaitingJoin.put(playerId, scheduler.scheduleSyncDelayedTask(CubesideUtilsBukkit.getInstance().getPlugin(), () -> playerDidntJoinInternal(playerId), BETWEEN_LOGIN_AND_JOIN_TIMEOUT));
        if (oldTaskId != null) {
            scheduler.cancelTask(oldTaskId);
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerJoinEvent(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        UUID playerId = player.getUniqueId();
        Integer taskId = this.playersAwaitingJoin.remove(playerId);
        if (taskId != null) {
            Bukkit.getScheduler().cancelTask(taskId);
        } else {
            playerJoinedAfterTimeout(player);
        }
    }

    private void playerDidntJoinInternal(UUID playerId) {
        Integer taskId = this.playersAwaitingJoin.remove(playerId);
        if (taskId == null) {
            return;
        }

        playerDidntJoin(playerId);
        V value = removeFromHardCache(playerId);
        playerDataUnloadedDidntJoin(playerId, value);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void lateOnPlayerQuitEvent(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        playerQuitting(player);
        UUID playerId = player.getUniqueId();
        V value = removeFromHardCache(playerId);
        playerDataUnloadedOnQuit(player, value);
    }

    @Override
    protected boolean checkKey(Object key) {
        return key instanceof UUID;
    }

    protected void playerStartsLoggingIn(Player player) {

    }

    protected abstract V loadOnLogin(Player player) throws LoadingPlayerDataFailedException;

    protected void playerDataLoadedOnLogin(Player player, V value) {

    }

    protected void playerFinishsLoggingIn(Player player) {

    }

    protected void playerDataUnloadedOnSuccesslessLogin(Player player, V value) {

    }

    protected void playerDidntJoin(UUID playerId) {

    }

    protected void playerDataUnloadedDidntJoin(UUID playerId, V value) {

    }

    protected void playerJoinedAfterTimeout(Player player) {
        Bukkit.getScheduler().runTask(CubesideUtilsBukkit.getInstance().getPlugin(), () -> player.kick(Component.text("Timeout between login and join."), PlayerKickEvent.Cause.TIMEOUT));
    }

    protected void playerQuitting(Player player) {

    }

    protected void playerDataUnloadedOnQuit(Player player, V value) {

    }

}
