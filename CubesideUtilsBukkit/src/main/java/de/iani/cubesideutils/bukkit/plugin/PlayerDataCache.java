package de.iani.cubesideutils.bukkit.plugin;

import de.iani.cubesideutils.bukkit.plugin.api.OnlinePlayerData;
import de.iani.cubesideutils.collections.IteratorUtil;
import de.iani.cubesideutils.plugin.PlayerDataImpl;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.logging.Level;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerDataCache extends LinkedHashMap<UUID, PlayerDataImplBukkit> implements Listener {

    private static final long serialVersionUID = 2279901989917386890L;
    private static final int MAX_SIZE = 16;

    private Map<UUID, OnlinePlayerDataImpl> onlinePlayers;

    private ReadWriteLock lock;

    private Player currentlyLoggingInPlayer;

    public PlayerDataCache() {
        // Accesses may be asynchronous
        this.lock = new ReentrantReadWriteLock();
        this.onlinePlayers = new HashMap<>();

        Bukkit.getPluginManager().registerEvents(this, CubesideUtilsBukkit.getInstance().getPlugin());
    }

    public Player getCurrentlyLoggingInPlayer() {
        return this.currentlyLoggingInPlayer;
    }

    public void invalidate(UUID playerId) {
        // MAY NOT HAVE READ LOCK
        this.lock.writeLock().lock();
        try {
            boolean isOnline = false;
            long lastAction = 0;
            boolean manuallySetAfk = false;
            PlayerDataImplBukkit data = this.onlinePlayers.remove(playerId);
            if (data != null) {
                isOnline = true;
                lastAction = data.getOnlineData().getLastAction();
                manuallySetAfk = data.getOnlineData().isManuallySetAfk();
            } else {
                data = super.remove(playerId);
            }

            if (isOnline) {
                try {
                    data = CubesideUtilsBukkit.getInstance().getDatabase().getOnlinePlayerData(playerId, true, lastAction, manuallySetAfk);
                } catch (SQLException e) {
                    CubesideUtilsBukkit.getInstance().getLogger().log(Level.SEVERE, "Exception trying to load OnlinePlayerData for " + playerId + " from database.");
                    return;
                }
                this.onlinePlayers.put(playerId, (OnlinePlayerDataImpl) data);
                data.getOnlineData().checkAfk(false);
            }
        } finally {
            this.lock.writeLock().unlock();
        }
    }

    private void left(UUID playerId) {
        this.lock.writeLock().lock();
        OnlinePlayerDataImpl data = this.onlinePlayers.remove(playerId);
        data.quit();
        this.lock.writeLock().unlock();
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void earlyOnPlayerLoginEvent(PlayerLoginEvent event) {
        this.currentlyLoggingInPlayer = event.getPlayer();
        UUID playerId = event.getPlayer().getUniqueId();
        this.lock.writeLock().lock();
        try {
            invalidate(playerId);
            OnlinePlayerDataImpl data;
            try {
                data = CubesideUtilsBukkit.getInstance().getDatabase().getOnlinePlayerData(playerId, true, System.currentTimeMillis(), false);
            } catch (SQLException e) {
                CubesideUtilsBukkit.getInstance().getLogger().log(Level.SEVERE, "Exception trying to load OnlinePlayerData for " + playerId + " from database.");
                // TODO: disallow?
                return;
            }
            this.onlinePlayers.put(playerId, data);
            data.setLocallyAfk(false, false);
        } finally {
            this.lock.writeLock().unlock();
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void lateOnPlayerLoginEvent(PlayerLoginEvent event) {
        this.currentlyLoggingInPlayer = null;
        if (event.getResult() != PlayerLoginEvent.Result.ALLOWED) {
            left(event.getPlayer().getUniqueId());
            return;
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void lateOnPlayerQuitEvent(PlayerQuitEvent event) {
        PlayerDataImpl data = get(event.getPlayer().getUniqueId());
        data.setLastSeen(System.currentTimeMillis());

        // Other plugins may need the data on quit.
        left(event.getPlayer().getUniqueId());
    }

    // May be accessed asynchronously.
    @Override
    public PlayerDataImplBukkit get(Object key) {
        return get(key, true, false);
    }

    // May be accessed asynchronously.
    public PlayerDataImplBukkit get(Object key, boolean queryDatabase, boolean createIfMissing) {
        if (!queryDatabase && createIfMissing) {
            throw new IllegalArgumentException("can only createIfMissing if queryDatabase");
        }

        if (!(key instanceof UUID)) {
            return null;
        }

        this.lock.readLock().lock();
        try {
            PlayerDataImplBukkit result = this.onlinePlayers.get(key);
            if (result != null) {
                return result;
            }

            result = super.get(key);
            if (result != null || !queryDatabase) {
                return result;
            }

            // unlock read to be allowed to lock write
            this.lock.readLock().unlock();
            this.lock.writeLock().lock();

            try {
                // relock read to unlock in outer finally
                this.lock.readLock().lock();

                // might have changed during temporary unlock
                result = this.onlinePlayers.get(key);
                if (result != null) {
                    return result;
                }

                return super.computeIfAbsent((UUID) key, k -> {
                    PlayerDataImplBukkit data;
                    try {
                        data = CubesideUtilsBukkit.getInstance().getDatabase().getPlayerData(k, createIfMissing);
                    } catch (SQLException e) {
                        CubesideUtilsBukkit.getInstance().getLogger().log(Level.SEVERE, "Exception trying to query database for PlayerData.", e);
                        return null;
                    }
                    return data;
                });
            } finally {
                this.lock.writeLock().unlock();
            }
        } finally {
            this.lock.readLock().unlock();
        }
    }

    public OnlinePlayerData getOnline(UUID key) {
        return onlinePlayers.get(key);
    }

    // May be accessed asynchronously.
    @Override
    public boolean containsKey(Object key) {
        this.lock.readLock().lock();
        try {
            return super.containsKey(key) || this.onlinePlayers.containsKey(key);
        } finally {
            this.lock.readLock().unlock();
        }
    }

    @Override
    public PlayerDataImplBukkit put(UUID key, PlayerDataImplBukkit value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public PlayerDataImplBukkit remove(Object key) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void clear() {
        throw new UnsupportedOperationException();
    }

    @Override
    protected boolean removeEldestEntry(java.util.Map.Entry<UUID, PlayerDataImplBukkit> eldest) {
        return size() >= MAX_SIZE;
    }

    public Iterable<PlayerDataImplBukkit> loadedData() {
        return IteratorUtil.concatUnmodifiable(this.onlinePlayers.values(), values());
    }

}
