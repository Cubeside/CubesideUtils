package de.iani.cubesideutils.bukkit.plugin;

import de.cubeside.connection.ConnectionAPI;
import de.cubeside.connection.GlobalClientPlugin;
import de.cubeside.connection.GlobalPlayer;
import de.cubeside.connection.GlobalServer;
import de.iani.cubesideutils.bukkit.ChatUtilBukkit;
import de.iani.cubesideutils.bukkit.commands.CommandRouter;
import de.iani.cubesideutils.bukkit.conditions.HasCustomPlayerDataValueCondition;
import de.iani.cubesideutils.bukkit.conditions.HasPermissionCondition;
import de.iani.cubesideutils.bukkit.plugin.api.PlayerDataBukkit;
import de.iani.cubesideutils.bukkit.plugin.api.UtilsApiBukkit;
import de.iani.cubesideutils.bukkit.plugin.api.events.PlayerOptionsRetrievedEvent;
import de.iani.cubesideutils.bukkit.plugin.commands.ChangeRankInformationCommand;
import de.iani.cubesideutils.bukkit.plugin.commands.ListRankInformationCommand;
import de.iani.cubesideutils.bukkit.plugin.commands.PlayerOptionsCommand;
import de.iani.cubesideutils.bukkit.serialization.GlobalLocationWrapper;
import de.iani.cubesideutils.bukkit.serialization.SerializableComponent;
import de.iani.cubesideutils.bukkit.serialization.SerializablePair;
import de.iani.cubesideutils.bukkit.serialization.SerializableTriple;
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
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class CubesideUtilsBukkit extends CubesideUtils implements UtilsApiBukkit {

    public static final String DISPLAY_NAME_PROPERTY_PREFIX = "worldDisplayName:";
    public static final String RANKS_COMMAND = "ranks";
    public static final String PLAYEROPTIONS_COMMAND = "playeroptions";

    private static volatile CubesideUtilsBukkit instance = null;

    public static CubesideUtilsBukkit getInstance() {
        return instance;
    }

    static {
        StringSerialization.register(GlobalLocationWrapper.SERIALIZATION_TYPE, GlobalLocationWrapper::deserialize);
        StringSerialization.register(HasPermissionCondition.SERIALIZATION_TYPE, HasPermissionCondition::deserialize);
        StringSerialization.register(HasCustomPlayerDataValueCondition.SERIALIZATION_TYPE, HasCustomPlayerDataValueCondition::deserialize);

        // trigger registration of serializable classes
        new SerializablePair<>(null, null);
        new SerializableTriple<>(null, null, null);
        new SerializableComponent(new TextComponent());
    }

    private UtilsPluginBukkit plugin;

    private UtilsDatabaseBukkit database;
    private PlayerDataCache playerDataCache;
    private PlayerUUIDCacheWrapper playerUUIDCache;
    private GlobalClientPlugin globalClientPlugin;
    private UtilsGlobalDataHelperBukkit globalDataHelper;

    private InventoryInputManagerImpl inventoryInputManager;
    private PlayerReconfigurationPhaseHelper reconfigurationPhaseHelper;

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

        if (this.plugin.getServer().getPluginManager().isPluginEnabled("PlayerUUIDCache")) {
            this.playerUUIDCache = new PlayerUUIDCacheWrapper(this.plugin);
        }
        if (this.plugin.getServer().getPluginManager().isPluginEnabled("GlobalClient")) {
            this.globalClientPlugin = JavaPlugin.getPlugin(GlobalClientPlugin.class);
            this.globalDataHelper = new UtilsGlobalDataHelperBukkit(this.plugin);
        }

        this.inventoryInputManager = new InventoryInputManagerImpl();
        this.reconfigurationPhaseHelper = new PlayerReconfigurationPhaseHelper();

        this.database.registerRealServer();

        CommandRouter ranksCommand = new CommandRouter(this.plugin.getCommand(RANKS_COMMAND));
        ranksCommand.addCommandMapping(new ListRankInformationCommand(), ListRankInformationCommand.COMMAND_PATH);
        ranksCommand.addCommandMapping(new ChangeRankInformationCommand(true), ChangeRankInformationCommand.SET_COMMAND_PATH);
        ranksCommand.addCommandMapping(new ChangeRankInformationCommand(false), ChangeRankInformationCommand.REMOVE_COMMAND_PATH);

        CommandRouter playeroptionsCommand = new CommandRouter(this.plugin.getCommand(PLAYEROPTIONS_COMMAND));
        playeroptionsCommand.addCommandMapping(new PlayerOptionsCommand(this));

        updateRankInformation();
    }

    @Override
    protected void shutdownServer() {
        Bukkit.getServer().shutdown();
    }

    public UtilsPluginBukkit getPlugin() {
        return plugin;
    }

    public OfflinePlayer getCachedOfflinePlayer(String name) {
        OfflinePlayer p = this.playerUUIDCache != null ? this.playerUUIDCache.getPlayer(name) : plugin.getServer().getOfflinePlayerIfCached(name);
        return p == null || p.getName() == null ? null : p;
    }

    public OfflinePlayer getCachedOfflinePlayer(UUID uuid) {
        OfflinePlayer p = this.playerUUIDCache != null ? this.playerUUIDCache.getPlayer(uuid) : plugin.getServer().getOfflinePlayer(uuid);
        return p == null || p.getName() == null ? null : p;
    }

    public GlobalClientPlugin getGlobalClientPlugin() {
        return this.globalClientPlugin;
    }

    @Override
    protected ConnectionAPI getConnectionApi() {
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
    public InventoryInputManagerImpl getInventoryInputManager() {
        return this.inventoryInputManager;
    }

    public PlayerReconfigurationPhaseHelper getReconfigurationPhaseHelper() {
        return this.reconfigurationPhaseHelper;
    }

    @Override
    public OnlinePlayerDataImpl getPlayerData(Player player) {
        return getPlayerData(player.getUniqueId()).getOnlineData();
    }

    @Override
    public PlayerDataImplBukkit getPlayerData(OfflinePlayer player) {
        return getPlayerData(player.getUniqueId());
    }

    @Override
    public PlayerDataImplBukkit getPlayerData(UUID playerId) {
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
    public void sendMessageToPlayersAllServers(Condition<? super Player> seeMsgCondition, String message) {
        ChatUtilBukkit.sendMessageToPlayers(seeMsgCondition, message);
        this.globalDataHelper.sendData(MessageType.SEND_MESSAGE, seeMsgCondition == null ? NullWrapper.INSTANCE : seeMsgCondition, false, message);
    }

    @Override
    public void sendMessageToPlayersAllServers(Condition<? super Player> seeMsgCondition, BaseComponent... message) {
        ChatUtilBukkit.sendMessageToPlayers(seeMsgCondition, message);
        this.globalDataHelper.sendData(MessageType.SEND_MESSAGE, seeMsgCondition == null ? NullWrapper.INSTANCE : seeMsgCondition, true, message);
    }

    @Override
    public void sendPlayerOptions(CommandSender sender, OfflinePlayer player) {
        PlayerOptionsRetrievedEvent event = new PlayerOptionsRetrievedEvent(sender, player);
        Bukkit.getPluginManager().callEvent(event);
        if (event.isCancelled()) {
            return;
        }
        BaseComponent[] options = event.getOptions();
        if (options.length > 0) {
            PlayerDataBukkit playerData = getPlayerData(player);
            sender.sendMessage(new TextComponent("  "), new TextComponent(TextComponent.fromLegacyText(playerData.getRankPrefix() + player.getName())), new TextComponent(": "), new TextComponent(options));
        }
    }

    @Override
    public void doAfterReconfigurationPhase(Player player, List<Consumer<? super Player>> actions) {
        this.reconfigurationPhaseHelper.doActions(player, actions);
    }

    @Override
    public void doAfterReconfigurationPhase(Player player, Consumer<? super Player> action) {
        this.reconfigurationPhaseHelper.doAction(player, action);
    }

}
