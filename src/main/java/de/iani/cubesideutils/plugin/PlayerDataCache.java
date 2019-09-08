package de.iani.cubesideutils.plugin;

import de.iani.cubesideutils.collections.IteratorUtil;
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

class PlayerDataCache extends LinkedHashMap<UUID, PlayerData> implements Listener {

    private static final long serialVersionUID = 2279901989917386890L;
    private static final int MAX_SIZE = 16;

    private Map<UUID, OnlinePlayerData> onlinePlayers;

    private ReadWriteLock lock;

    private Player currentlyLoggingInPlayer;

    public PlayerDataCache() {
        // Accesses may be asynchronous
        this.lock = new ReentrantReadWriteLock();
        this.onlinePlayers = new HashMap<>();

        Bukkit.getPluginManager().registerEvents(this, UtilsPlugin.getInstance());
    }

    Player getCurrentlyLoggingInPlayer() {
        return this.currentlyLoggingInPlayer;
    }

    public void invalidate(UUID playerId) {
        // MAY NOT HAVE READ LOCK
        this.lock.writeLock().lock();
        try {
            boolean isOnline = false;
            long lastAction = 0;
            PlayerData data = this.onlinePlayers.remove(playerId);
            if (data != null) {
                isOnline = true;
                lastAction = data.getOnlineData().getLastAction();
            } else {
                data = super.remove(playerId);
            }

            if (isOnline) {
                try {
                    data = UtilsPlugin.getInstance().getDatabase().getOnlinePlayerData(playerId, true, lastAction);
                } catch (SQLException e) {
                    UtilsPlugin.getInstance().getLogger().log(Level.SEVERE, "Exception trying to load OnlinePlayerData for " + playerId + " from database.");
                    return;
                }
                this.onlinePlayers.put(playerId, (OnlinePlayerData) data);
                data.getOnlineData().checkAfk(false);
            }
        } finally {
            this.lock.writeLock().unlock();
        }
    }

    private void left(UUID playerId) {
        this.lock.writeLock().lock();
        OnlinePlayerData data = this.onlinePlayers.remove(playerId);
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
            OnlinePlayerData data;
            try {
                data = UtilsPlugin.getInstance().getDatabase().getOnlinePlayerData(playerId, true, System.currentTimeMillis());
            } catch (SQLException e) {
                UtilsPlugin.getInstance().getLogger().log(Level.SEVERE, "Exception trying to load OnlinePlayerData for " + playerId + " from database.");
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
        PlayerData data = get(event.getPlayer().getUniqueId());
        data.setLastSeen(System.currentTimeMillis());

        // Other plugins may need the data on quit.
        left(event.getPlayer().getUniqueId());
    }

    // May be accessed asynchronously.
    @Override
    public PlayerData get(Object key) {
        return get(key, false);
    }

    // May be accessed asynchronously.
    public PlayerData get(Object key, boolean createIfMissing) {
        if (!(key instanceof UUID)) {
            return null;
        }

        this.lock.readLock().lock();
        try {
            PlayerData result = this.onlinePlayers.get(key);
            if (result != null) {
                return result;
            }

            result = super.get(key);
            if (result != null) {
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
                    PlayerData data;
                    try {
                        data = UtilsPlugin.getInstance().getDatabase().getPlayerData(k, createIfMissing);
                    } catch (SQLException e) {
                        // TODO: handle
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
    public PlayerData put(UUID key, PlayerData value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public PlayerData remove(Object key) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void clear() {
        throw new UnsupportedOperationException();
    }

    @Override
    protected boolean removeEldestEntry(java.util.Map.Entry<UUID, PlayerData> eldest) {
        return size() >= MAX_SIZE;
    }

    Iterable<PlayerData> loadedData() {
        return IteratorUtil.concatUnmodifiable(this.onlinePlayers.values(), values());
    }

}
