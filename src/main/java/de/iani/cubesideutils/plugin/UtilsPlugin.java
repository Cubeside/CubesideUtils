package de.iani.cubesideutils.plugin;

import de.cubeside.connection.ConnectionAPI;
import de.cubeside.connection.GlobalClientPlugin;
import de.cubeside.connection.PlayerMessageAPI;
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
    private ConnectionAPI connectionApi;
    private PlayerMessageAPI playerMsgApi;

    public UtilsPlugin() {
        synchronized (UtilsPlugin.class) {
            if (instance != null) {
                throw new IllegalStateException("Only one instance permitted.");
            }
            instance = this;
        }

        this.database = new Database();
        this.playerDataCache = new PlayerDataCache();

        GlobalClientPlugin connectionPlugin = JavaPlugin.getPlugin(GlobalClientPlugin.class);
        this.connectionApi = connectionPlugin.getConnectionAPI();
        this.playerMsgApi = connectionPlugin.getMessageAPI();
    }

    Database getDatabase() {
        return database;
    }

    PlayerDataCache getPlayerDataCache() {
        return this.playerDataCache;
    }

    ConnectionAPI getConnectionAPI() {
        return this.connectionApi;
    }

    PlayerMessageAPI getPlayerMsgApi() {
        return this.playerMsgApi;
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
