package de.iani.cubesideutils.plugin;

import de.cubeside.connection.GlobalClientPlugin;
import de.cubeside.connection.GlobalPlayer;
import de.cubeside.connection.GlobalServer;
import de.iani.cubesideutils.Pair;
import de.iani.cubesideutils.sql.SQLConfig;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.logging.Level;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class UtilsPlugin extends JavaPlugin {

    public static final long AFK_THRESHOLD = 2L * 60L * 1000L;

    static final String DISPLAY_NAME_PROPERTY_PREFIX = "worldDisplayName:";

    private static UtilsPlugin instance = null;

    public static synchronized UtilsPlugin getInstance() {
        return instance;
    }

    private Database database;
    private PlayerDataCache playerDataCache;
    private EventListener eventListener;
    private AfkManager afkManager;
    private GlobalClientPlugin globalClientPlugin;
    private UtilsGlobalDataHelper globalDataHelper;
    private GlobalDataBundle globalDataBundle;

    private ReadWriteLock rankLock;
    private List<String> ranks;
    private Map<String, Pair<String, String>> rankPermissionsAndPrefixes;

    private String defaultDisplayName;
    private Map<String, String> worldDisplayNames;

    private Map<String, Boolean> cachedRealServers;

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

        this.cachedRealServers = Collections.synchronizedMap(new HashMap<>());
    }

    @Override
    public void onEnable() {
        try {
            saveDefaultConfig();

            this.defaultDisplayName = getConfig().getString("defaultDisplayName");
            this.worldDisplayNames = new LinkedHashMap<>();
            ConfigurationSection displayNamesSection = getConfig().getConfigurationSection("worldDisplayNames");
            for (String worldName : displayNamesSection.getKeys(false)) {
                this.worldDisplayNames.put(worldName, displayNamesSection.getString(worldName));
            }

            this.database = new Database();
            this.playerDataCache = new PlayerDataCache();
            this.eventListener = new EventListener();
            this.afkManager = new AfkManager();

            this.globalClientPlugin = JavaPlugin.getPlugin(GlobalClientPlugin.class);
            this.globalDataHelper = new UtilsGlobalDataHelper(this);
            this.globalDataBundle = new GlobalDataBundle();

            this.database.registerRealServer();
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

    GlobalClientPlugin getGlobalClientPlugin() {
        return this.globalClientPlugin;
    }

    UtilsGlobalDataHelper getGlobalDataHelper() {
        return this.globalDataHelper;
    }

    public GlobalDataBundle getGlobalDataBundle() {
        return this.globalDataBundle;
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

    public String getWorldDisplayName(LivingEntity entity) {
        return getWorldDisplayName(entity == null ? null : entity.getWorld());
    }

    public String getWorldDisplayName(World world) {
        return getWorldDisplayName(world == null ? null : world.getName());
    }

    public String getWorldDisplayName(String worldName) {
        return this.worldDisplayNames.getOrDefault(worldName, defaultDisplayName);
    }

    public Set<String> getWorldDisplayNames(OfflinePlayer player) {
        return getWorldDisplayNames(player.getUniqueId());
    }

    public Set<String> getWorldDisplayNames(UUID playerId) {
        Set<String> result = new HashSet<>();
        GlobalPlayer gPlayer = this.globalDataHelper.getPlayer(playerId);
        for (GlobalServer server : this.globalDataHelper.getServers(gPlayer)) {
            String displayName = this.globalDataHelper.getPropertyValue(gPlayer, DISPLAY_NAME_PROPERTY_PREFIX + server.getName());
            if (displayName != null) {
                result.add(displayName);
            }
        }
        return result;
    }

    Map<String, Boolean> getCachedRealServers() {
        return this.cachedRealServers;
    }

    public void sendMessageToPlayersAllServers(String seeMsgPermission, String message) {
        this.globalDataHelper.sendData(MessageType.SEND_MESSAGE, seeMsgPermission, message);
    }

}
