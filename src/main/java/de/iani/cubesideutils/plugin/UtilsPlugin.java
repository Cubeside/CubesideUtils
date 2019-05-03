package de.iani.cubesideutils.plugin;

import de.iani.cubesideutils.plugin.database.Database;
import java.util.UUID;
import org.bukkit.OfflinePlayer;
import org.bukkit.plugin.java.JavaPlugin;

public class UtilsPlugin extends JavaPlugin {

    public static final long AFK_THRESHOLD = 2L * 60L * 1000L;

    private static UtilsPlugin instance = null;

    public static synchronized UtilsPlugin getInstance() {
        return instance;
    }

    private Database database;
    private PlayerDataCache playerDataCache;

    public UtilsPlugin() {
        synchronized (UtilsPlugin.class) {
            if (instance != null) {
                throw new IllegalStateException("Only one instance permitted.");
            }
            instance = this;
        }

        this.database = new Database();
        this.playerDataCache = new PlayerDataCache();
    }

    Database getDatabase() {
        return database;
    }

    PlayerDataCache getPlayerDataCache() {
        return this.playerDataCache;
    }

    public PlayerData getPlayerData(OfflinePlayer player) {
        return getPlayerData(player.getUniqueId());
    }

    public PlayerData getPlayerData(UUID playerId) {
        return this.playerDataCache.get(playerId);
    }

    public String getDefaultRank() {
        return getConfig().getString("defaultRank");
    }

}
