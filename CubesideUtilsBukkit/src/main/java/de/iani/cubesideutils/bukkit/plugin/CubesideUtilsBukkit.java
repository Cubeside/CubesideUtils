package de.iani.cubesideutils.bukkit.plugin;

import de.cubeside.connection.ConnectionAPI;
import de.cubeside.connection.GlobalClientPlugin;
import de.cubeside.connection.GlobalPlayer;
import de.cubeside.connection.GlobalServer;
import de.iani.cubesideutils.bukkit.BukkitChatUtil;
import de.iani.cubesideutils.bukkit.commands.CommandRouter;
import de.iani.cubesideutils.bukkit.plugin.api.OnlinePlayerData;
import de.iani.cubesideutils.bukkit.plugin.api.PlayerDataBukkit;
import de.iani.cubesideutils.bukkit.plugin.api.UtilsPluginBukkitApi;
import de.iani.cubesideutils.bukkit.sql.BukkitSQLConfig;
import de.iani.cubesideutils.plugin.CubesideUtils;
import de.iani.cubesideutils.plugin.PlayerDataImpl;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.logging.Logger;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class CubesideUtilsBukkit extends CubesideUtils implements UtilsPluginBukkitApi {

    static final String DISPLAY_NAME_PROPERTY_PREFIX = "worldDisplayName:";
    static final String RANKS_COMMAND = "ranks";

    private static volatile CubesideUtilsBukkit instance = null;

    public static CubesideUtilsBukkit getInstance() {
        return instance;
    }

    private UtilsPluginBukkit plugin;

    private UtilsDatabaseBukkit database;
    private PlayerDataCache playerDataCache;
    private GlobalClientPlugin globalClientPlugin;
    private UtilsGlobalDataHelperBukkit globalDataHelper;

    private String defaultDisplayName;
    private Map<String, String> worldDisplayNames;

    public CubesideUtilsBukkit(UtilsPluginBukkit plugin) {
        synchronized (CubesideUtilsBukkit.class) {
            if (instance != null) {
                throw new IllegalStateException("Only one instance permitted.");
            }
            instance = this;
        }

        this.plugin = plugin;
    }

    @Override
    protected void onEnableInternal() throws Throwable {
        super.onEnableInternal();

        this.plugin.saveDefaultConfig();
        FileConfiguration config = this.plugin.getConfig();

        this.defaultDisplayName = config.getString("defaultDisplayName");
        this.worldDisplayNames = new LinkedHashMap<>();
        ConfigurationSection displayNamesSection = config.getConfigurationSection("worldDisplayNames");
        for (String worldName : displayNamesSection.getKeys(false)) {
            this.worldDisplayNames.put(worldName, displayNamesSection.getString(worldName));
        }

        this.database = new UtilsDatabaseBukkit(new BukkitSQLConfig(config.getConfigurationSection("database")));
        this.playerDataCache = new PlayerDataCache();
        new EventListener();
        new AfkManager();

        this.globalClientPlugin = JavaPlugin.getPlugin(GlobalClientPlugin.class);
        this.globalDataHelper = new UtilsGlobalDataHelperBukkit(this.plugin);

        this.database.registerRealServer();

        CommandRouter ranksCommand = new CommandRouter(this.plugin.getCommand(RANKS_COMMAND));
        ranksCommand.addCommandMapping(new ListRankInformationCommand(), ListRankInformationCommand.COMMAND_PATH);
        ranksCommand.addCommandMapping(new ChangeRankInformationCommand(true), ChangeRankInformationCommand.SET_COMMAND_PATH);
        ranksCommand.addCommandMapping(new ChangeRankInformationCommand(false), ChangeRankInformationCommand.REMOVE_COMMAND_PATH);

        updateRankInformation();
    }

    @Override
    protected void shutdownServer() {
        Bukkit.getServer().shutdown();
    }

    public UtilsPluginBukkit getPlugin() {
        return plugin;
    }

    public GlobalClientPlugin getGlobalClientPlugin() {
        return this.globalClientPlugin;
    }

    @Override
    public ConnectionAPI getConnectionApi() {
        return this.globalClientPlugin.getConnectionAPI();
    }

    @Override
    public UtilsDatabaseBukkit getDatabase() {
        return this.database;
    }

    @Override
    public Logger getLogger() {
        return this.plugin.getLogger();
    }

    public PlayerDataCache getPlayerDataCache() {
        return this.playerDataCache;
    }

    @Override
    public UtilsGlobalDataHelperBukkit getGlobalDataHelper() {
        return this.globalDataHelper;
    }

    @Override
    public OnlinePlayerData getPlayerData(Player player) {
        return getPlayerData(player.getUniqueId()).getOnlineData();
    }

    @Override
    public PlayerDataBukkit getPlayerData(OfflinePlayer player) {
        return getPlayerData(player.getUniqueId());
    }

    @Override
    public PlayerDataBukkit getPlayerData(UUID playerId) {
        return this.playerDataCache.get(playerId);
    }

    @Deprecated
    public PlayerDataBukkit getPlayerDataTemp(UUID playerId, boolean insertIfMissing) {
        return this.playerDataCache.get(playerId, true);
    }

    @Override
    protected Iterable<? extends PlayerDataImpl> getLoadedPlayerData() {
        return this.playerDataCache.loadedData();
    }

    @Override
    public String getWorldDisplayName(LivingEntity entity) {
        return getWorldDisplayName(entity == null ? null : entity.getWorld());
    }

    @Override
    public String getWorldDisplayName(World world) {
        return getWorldDisplayName(world == null ? null : world.getName());
    }

    @Override
    public String getWorldDisplayName(String worldName) {
        return this.worldDisplayNames.getOrDefault(worldName, defaultDisplayName);
    }

    @Override
    public Set<String> getWorldDisplayNames(OfflinePlayer player) {
        return getWorldDisplayNames(player.getUniqueId());
    }

    @Override
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

    @Override
    public void sendMessageToPlayersAllServers(String seeMsgPermission, String message) {
        BukkitChatUtil.sendMessageToPlayers(seeMsgPermission, message);
        sendMessageToPlayersAllServers(seeMsgPermission, message);
    }

}
