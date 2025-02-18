package de.iani.cubesideutils.bukkit.plugin;

import de.iani.cubesideutils.Pair;
import de.iani.cubesideutils.bukkit.plugin.api.PlayerCacheMap;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.UUID;
import java.util.logging.Level;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

public class PlayerDataCache extends PlayerCacheMap<PlayerDataImplBukkit, Pair<Boolean, Boolean>> implements Listener {

    private static final long serialVersionUID = -8879364238741140063L;
    private static final int MAX_SOFT_CACHE_SIZE = 16;

    private Player currentlyLoggingInPlayer;

    public PlayerDataCache() {
        super(MAX_SOFT_CACHE_SIZE, new Pair<>(true, false), "PlayerData");
    }

    public Player getCurrentlyLoggingInPlayer() {
        return this.currentlyLoggingInPlayer;
    }

    public OnlinePlayerDataImpl getOnline(UUID key) {
        return (OnlinePlayerDataImpl) getFromHardCache(key);
    }

    public Collection<PlayerDataImplBukkit> loadedData() {
        return Collections.unmodifiableCollection(new ArrayList<>(values()));
    }

    public PlayerDataImplBukkit get(Object key, boolean queryDatabase, boolean createIfMissing) {
        return get(key, new Pair<>(queryDatabase, createIfMissing));
    }

    @Override
    protected void checkData(Pair<Boolean, Boolean> data) {
        if (data == null || data.first == null || data.second == null) {
            throw new NullPointerException();
        }
        if (!data.first && data.second) {
            throw new IllegalArgumentException("can only createIfMissing if queryDatabase");
        }
    }

    @Override
    protected boolean shouldLoadIntoCache(UUID key, Pair<Boolean, Boolean> data) {
        return data.first;
    }

    @Override
    protected PlayerDataImplBukkit load(UUID key, Pair<Boolean, Boolean> data) {
        try {
            return CubesideUtilsBukkit.getInstance().getDatabase().getPlayerData(key, data.second);
        } catch (SQLException e) {
            CubesideUtilsBukkit.getInstance().getLogger().log(Level.SEVERE, "Exception trying to query database for PlayerData.", e);
            return null;
        }
    }

    @Override
    public void invalidate(UUID key) {
        super.invalidate(key);
    }

    @Override
    protected PlayerDataImplBukkit getReplacement(UUID key, PlayerDataImplBukkit uncached) {
        OnlinePlayerDataImpl online = (OnlinePlayerDataImpl) uncached;
        long lastAction = online.getOnlineData().getLastAction();
        boolean manuallySetAfk = online.getOnlineData().isManuallySetAfk();
        try {
            return CubesideUtilsBukkit.getInstance().getDatabase().getOnlinePlayerData(key, true, lastAction, manuallySetAfk);
        } catch (SQLException e) {
            CubesideUtilsBukkit.getInstance().getLogger().log(Level.SEVERE, "Exception trying to load OnlinePlayerData for " + key + " from database.");
            return null;
        }
    }

    @Override
    protected void replaced(UUID key, PlayerDataImplBukkit uncached, PlayerDataImplBukkit replacement) {
        if (replacement != null) {
            ((OnlinePlayerDataImpl) replacement).checkAfk(false);
        }
    }

    @Override
    protected void playerStartsLoggingIn(Player player) {
        this.currentlyLoggingInPlayer = player;
    }

    @Override
    protected PlayerDataImplBukkit loadOnLogin(Player player) throws LoadingPlayerDataFailedException {
        try {
            return CubesideUtilsBukkit.getInstance().getDatabase().getOnlinePlayerData(player.getUniqueId(), true, System.currentTimeMillis(), false);
        } catch (SQLException e) {
            CubesideUtilsBukkit.getInstance().getLogger().log(Level.SEVERE, "Exception trying to load OnlinePlayerData for " + player.getUniqueId() + " from database.");
            // TODO: disallow, i.e. LoadingPlayerDataFailedException?
            return null;
        }
    }

    @Override
    protected void playerDataLoadedOnLogin(Player player, PlayerDataImplBukkit value) {
        ((OnlinePlayerDataImpl) value).setLocallyAfk(false, false);
    }

    @Override
    protected void playerFinishsLoggingIn(Player player) {
        this.currentlyLoggingInPlayer = null;
    }

    @Override
    protected void playerDataUnloadedOnSuccesslessLogin(Player player, PlayerDataImplBukkit value) {
        ((OnlinePlayerDataImpl) value).quit();
    }

    @Override
    protected void playerJoinedAfterTimeout(Player player) {
        try {
            addToHardCache(player.getUniqueId(), loadOnLogin(player));
        } catch (LoadingPlayerDataFailedException e) {
            player.kick(Component.text(e.getKickMessage()));
        }
    }

    @Override
    protected void playerDataUnloadedOnQuit(Player player, PlayerDataImplBukkit value) {
        ((OnlinePlayerDataImpl) value).quit();
    }

}
