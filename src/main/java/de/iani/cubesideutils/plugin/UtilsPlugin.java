package de.iani.cubesideutils.plugin;

import org.bukkit.plugin.java.JavaPlugin;

public class UtilsPlugin extends JavaPlugin {

    public static final long AFK_THRESHOLD = 2L * 60L * 1000L;

    private static UtilsPlugin instance = null;

    public static synchronized UtilsPlugin getInstance() {
        return instance;
    }

    private PlayerDataCache playerDataCache;

    public UtilsPlugin() {
        synchronized (UtilsPlugin.class) {
            if (instance != null) {
                throw new IllegalStateException("Only one instance permitted.");
            }
            instance = this;
        }

        this.playerDataCache = new PlayerDataCache();
    }

    public PlayerDataCache getPlayerDataCache() {
        return this.playerDataCache;
    }

    public String getDefaultRank() {
        return getConfig().getString("defaultRank");
    }

}
