package de.iani.cubesideutils.plugin;

import de.cubeside.connection.ConnectionAPI;
import de.cubeside.connection.GlobalClientPlugin;
import de.cubeside.connection.PlayerMessageAPI;
import de.iani.cubesideutils.Pair;
import de.iani.cubesideutils.sql.SQLConfig;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.logging.Level;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class UtilsPlugin extends JavaPlugin {

    public static final long AFK_THRESHOLD = 2L * 60L * 1000L;

    public static final String MODIFY_RANKS_PERMISSION = "cubesideutils.modify_ranks";

    private static UtilsPlugin instance = null;

    public static synchronized UtilsPlugin getInstance() {
        return instance;
    }

    private Database database;
    private PlayerDataCache playerDataCache;
    private AfkManager afkManager;
    private ConnectionAPI connectionApi;
    private PlayerMessageAPI playerMsgApi;
    private UtilsGlobalDataHelper globalDataHelper;

    private ReadWriteLock rankLock;
    private List<String> ranks;
    private Map<String, Pair<String, String>> rankPermissionsAndPrefixes;

    public UtilsPlugin() {
        synchronized (UtilsPlugin.class) {
            if (instance != null) {
                throw new IllegalStateException("Only one instance permitted.");
            }
            instance = this;
        }

        this.rankLock = new ReentrantReadWriteLock();
        this.ranks = Collections.emptyList();
        this.rankPermissionsAndPrefixes = Collections.emptyMap();
    }

    @Override
    public void onEnable() {
        try {
            saveDefaultConfig();

            this.database = new Database();
            this.playerDataCache = new PlayerDataCache();
            this.afkManager = new AfkManager();

            GlobalClientPlugin connectionPlugin = JavaPlugin.getPlugin(GlobalClientPlugin.class);
            this.connectionApi = connectionPlugin.getConnectionAPI();
            this.playerMsgApi = connectionPlugin.getMessageAPI();
            this.globalDataHelper = new UtilsGlobalDataHelper(this);
        } catch (Throwable e) {
            getLogger().log(Level.SEVERE, "Could not initilize CubesideUtils plugin.", e);
            Bukkit.getServer().shutdown();
        }

        updateRankInformation();
    }

    SQLConfig getSQLConfig() {
        return new SQLConfig(getConfig().getConfigurationSection("database"));
    }

    Database getDatabase() {
        return this.database;
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

    UtilsGlobalDataHelper getGlobalDataHelper() {
        return this.globalDataHelper;
    }

    public OnlinePlayerData getPlayerData(Player player) {
        return getPlayerData(player.getUniqueId()).getOnlineData();
    }

    public PlayerData getPlayerData(OfflinePlayer player) {
        return getPlayerData(player.getUniqueId());
    }

    public PlayerData getPlayerData(UUID playerId) {
        return this.playerDataCache.get(playerId);
    }

    @Deprecated
    public PlayerData getPlayerDataTemp(UUID playerId, boolean insertIfMissing) {
        return this.playerDataCache.get(playerId, true);
    }

    public List<String> getRanks() {
        this.rankLock.readLock().lock();

        try {
            return this.ranks;
        } finally {
            this.rankLock.readLock().unlock();
        }
    }

    String getDefaultRank() {
        this.rankLock.readLock().lock();

        try {
            if (this.ranks.isEmpty()) {
                return null;
            }

            return this.ranks.get(this.ranks.size() - 1);
        } finally {
            this.rankLock.readLock().unlock();
        }
    }

    public String getPermission(String rank) {
        this.rankLock.readLock().lock();

        try {
            return this.rankPermissionsAndPrefixes.get(rank).first;
        } finally {
            this.rankLock.readLock().unlock();
        }
    }

    public String getPrefix(String rank) {
        this.rankLock.readLock().lock();

        try {
            return this.rankPermissionsAndPrefixes.get(rank).second;
        } finally {
            this.rankLock.readLock().unlock();
        }
    }

    void updateRankInformation() {
        this.rankLock.writeLock().lock();

        try {
            this.rankPermissionsAndPrefixes = Collections.unmodifiableMap(this.database.getRankInformation());
            this.ranks = Collections.unmodifiableList(new ArrayList<>(rankPermissionsAndPrefixes.keySet()));
        } catch (SQLException e) {
            getLogger().log(Level.SEVERE, "Could not get rank information from database.", e);
        } finally {
            this.rankLock.writeLock().unlock();
        }

        for (PlayerData data : this.playerDataCache.loadedData()) {
            data.checkRank();
        }
    }

}
