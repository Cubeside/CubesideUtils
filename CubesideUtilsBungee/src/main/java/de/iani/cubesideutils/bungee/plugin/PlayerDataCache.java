package de.iani.cubesideutils.bungee.plugin;

import de.iani.cubesideutils.Pair;
import de.iani.cubesideutils.bungee.plugin.api.PlayerCacheMap;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.UUID;
import java.util.logging.Level;
import net.md_5.bungee.api.plugin.Listener;

public class PlayerDataCache extends PlayerCacheMap<PlayerDataImplBungee, Pair<Boolean, Boolean>> implements Listener {

    private static final long serialVersionUID = -8879364238741140063L;
    private static final int MAX_SOFT_CACHE_SIZE = 16;

    private UUID currentlyLoggingInPlayerId;

    public PlayerDataCache() {
        super(MAX_SOFT_CACHE_SIZE, new Pair<>(true, false), "PlayerData");
    }

    public UUID getCurrentlyLoggingInPlayer() {
        return this.currentlyLoggingInPlayerId;
    }

    public Collection<PlayerDataImplBungee> loadedData() {
        return Collections.unmodifiableCollection(new ArrayList<>(values()));
    }

    public PlayerDataImplBungee get(Object key, boolean queryDatabase, boolean createIfMissing) {
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
    protected PlayerDataImplBungee load(UUID key, Pair<Boolean, Boolean> data) {
        try {
            return CubesideUtilsBungee.getInstance().getDatabase().getPlayerData(key, data.second);
        } catch (SQLException e) {
            CubesideUtilsBungee.getInstance().getLogger().log(Level.SEVERE, "Exception trying to query database for PlayerData.", e);
            return null;
        }
    }

    @Override
    public void invalidate(UUID key) {
        super.invalidate(key);
    }

    @Override
    protected PlayerDataImplBungee getReplacement(UUID key, PlayerDataImplBungee uncached) {
        try {
            return CubesideUtilsBungee.getInstance().getDatabase().getPlayerData(key, true);
        } catch (SQLException e) {
            CubesideUtilsBungee.getInstance().getLogger().log(Level.SEVERE, "Exception trying to load OnlinePlayerData for " + key + " from database.");
            return null;
        }
    }

    @Override
    protected void playerStartsLoggingIn(UUID playerId) {
        this.currentlyLoggingInPlayerId = playerId;
    }

    @Override
    protected PlayerDataImplBungee loadOnLogin(UUID playerId) throws LoadingPlayerDataFailedException {
        try {
            return CubesideUtilsBungee.getInstance().getDatabase().getPlayerData(playerId, true);
        } catch (SQLException e) {
            CubesideUtilsBungee.getInstance().getLogger().log(Level.SEVERE, "Exception trying to load OnlinePlayerData for " + playerId + " from database.");
            // TODO: disallow, i.e. LoadingPlayerDataFailedException?
            return null;
        }
    }

    @Override
    protected void playerFinishsLoggingIn(UUID playerId) {
        this.currentlyLoggingInPlayerId = null;
    }

}
