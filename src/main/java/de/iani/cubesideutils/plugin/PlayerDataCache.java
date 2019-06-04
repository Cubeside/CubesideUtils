package de.iani.cubesideutils.plugin;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import org.bukkit.Bukkit;
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

    public PlayerDataCache() {
        // Accesses may be asynchronous
        this.lock = new ReentrantReadWriteLock();
        this.onlinePlayers = new HashMap<>();

        Bukkit.getPluginManager().registerEvents(this, UtilsPlugin.getInstance());
    }

    public void invalidate(UUID playerId) {
        // MAY NOT HAVE READ LOCK
        this.lock.writeLock().lock();
        try {
            boolean isOnline = false;
            PlayerData data = this.onlinePlayers.remove(playerId);
            if (data != null) {
                isOnline = true;
            } else {
                data = super.remove(playerId);
            }

            if (isOnline) {
                try {
                    data = UtilsPlugin.getInstance().getDatabase().getOnlinePlayerData(playerId, false);
                } catch (SQLException e) {
                    // TODO: handle
                    return;
                }
                if (data == null) {
                    // TODO: handle
                } else {
                    this.onlinePlayers.put(playerId, (OnlinePlayerData) data);
                }
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
        UUID playerId = event.getPlayer().getUniqueId();
        this.lock.writeLock().lock();
        try {
            invalidate(playerId);
            OnlinePlayerData data;
            try {
                data = UtilsPlugin.getInstance().getDatabase().getOnlinePlayerData(playerId, true);
            } catch (SQLException e) {
                // TODO: handle
                // TODO: disallow?
                return;
            }
            this.onlinePlayers.put(playerId, data);
        } finally {
            this.lock.writeLock().unlock();
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void lateOnPlayerLoginEvent(PlayerLoginEvent event) {
        if (event.getResult() != PlayerLoginEvent.Result.ALLOWED) {
            left(event.getPlayer().getUniqueId());
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void lateOnPlayerQuitEvent(PlayerQuitEvent event) {
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

}
