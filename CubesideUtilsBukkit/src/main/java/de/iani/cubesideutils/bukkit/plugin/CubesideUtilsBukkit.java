package de.iani.cubesideutils.bukkit.plugin;

import de.cubeside.connection.ConnectionAPI;
import de.cubeside.connection.GlobalClientPlugin;
import de.cubeside.connection.GlobalPlayer;
import de.cubeside.connection.GlobalServer;
import de.iani.cubesideutils.bukkit.ChatUtilBukkit;
import de.iani.cubesideutils.bukkit.commands.CommandRouter;
import de.iani.cubesideutils.bukkit.conditions.HasCustomPlayerDataValueCondition;
import de.iani.cubesideutils.bukkit.conditions.HasPermissionCondition;
import de.iani.cubesideutils.bukkit.plugin.api.OnlinePlayerData;
import de.iani.cubesideutils.bukkit.plugin.api.PlayerDataBukkit;
import de.iani.cubesideutils.bukkit.plugin.api.UtilsApiBukkit;
import de.iani.cubesideutils.bukkit.plugin.commands.ChangeRankInformationCommand;
import de.iani.cubesideutils.bukkit.plugin.commands.ListRankInformationCommand;
import de.iani.cubesideutils.bukkit.serialization.GlobalLocationWrapper;
import de.iani.cubesideutils.bukkit.sql.SQLConfigBukkit;
import de.iani.cubesideutils.conditions.Condition;
import de.iani.cubesideutils.plugin.CubesideUtils;
import de.iani.cubesideutils.plugin.PlayerDataImpl;
import de.iani.cubesideutils.plugin.UtilsGlobalDataHelper.MessageType;
import de.iani.cubesideutils.serialization.NullWrapper;
import de.iani.cubesideutils.serialization.StringSerialization;
import java.sql.SQLException;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class CubesideUtilsBukkit extends CubesideUtils implements UtilsApiBukkit {

    public static final String DISPLAY_NAME_PROPERTY_PREFIX = "worldDisplayName:";
    public static final String RANKS_COMMAND = "ranks";

    private static volatile CubesideUtilsBukkit instance = null;

    public static CubesideUtilsBukkit getInstance() {
        return instance;
    }

    static {
        StringSerialization.register(GlobalLocationWrapper.SERIALIZATION_TYPE, GlobalLocationWrapper::deserialize);
        StringSerialization.register(HasPermissionCondition.SERIALIZATION_TYPE, HasPermissionCondition::deserialize);
        StringSerialization.register(HasCustomPlayerDataValueCondition.SERIALIZATION_TYPE, HasCustomPlayerDataValueCondition::deserialize);
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
        this.plugin.saveDefaultConfig();
        FileConfiguration config = this.plugin.getConfig();

        this.defaultDisplayName = config.getString("defaultDisplayName");
        this.worldDisplayNames = new LinkedHashMap<>();
        ConfigurationSection displayNamesSection = config.getConfigurationSection("worldDisplayNames");
        for (String worldName : displayNamesSection.getKeys(false)) {
            this.worldDisplayNames.put(worldName, displayNamesSection.getString(worldName));
        }

        this.database = new UtilsDatabaseBukkit(new SQLConfigBukkit(config.getConfigurationSection("database")));
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

    @Override
    protected Iterable<? extends PlayerDataImpl> getLoadedPlayerData() {
        return this.playerDataCache.loadedData();
    }

    @Override
    public List<OfflinePlayer> searchPlayersByPartialName(String partialName) {
        try {
            return getDatabase().searchPlayersByPartialName(partialName);
        } catch (SQLException e) {
            getLogger().log(Level.SEVERE, "Exception while trying to query database.", e);
            return Collections.emptyList();
        }
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
        sendMessageToPlayersAllServers(seeMsgPermission == null ? null : new HasPermissionCondition(seeMsgPermission), message);
    }

    @Override
    public void sendMessageToPlayersAllServers(Condition<? super Player> seeMsgCondition, String message) {
        ChatUtilBukkit.sendMessageToPlayers(seeMsgCondition, message);
        this.globalDataHelper.sendData(MessageType.SEND_MESSAGE, seeMsgCondition == null ? NullWrapper.instance : seeMsgCondition, message);
    }

}
