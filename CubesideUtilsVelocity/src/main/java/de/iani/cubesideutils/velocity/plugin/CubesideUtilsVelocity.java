package de.iani.cubesideutils.velocity.plugin;

import com.velocitypowered.api.plugin.annotation.DataDirectory;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import de.cubeside.connection.ConnectionAPI;
import de.cubeside.connection.GlobalClientPlugin;
import de.iani.cubesideutils.plugin.CubesideUtils;
import de.iani.cubesideutils.plugin.PlayerDataImpl;
import de.iani.cubesideutils.velocity.plugin.api.UtilsApiVelocity;
import de.iani.cubesideutils.velocity.sql.SQLConfigVelocity;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;
import org.spongepowered.configurate.CommentedConfigurationNode;
import org.spongepowered.configurate.yaml.YamlConfigurationLoader;

public class CubesideUtilsVelocity extends CubesideUtils implements UtilsApiVelocity {
    private static volatile CubesideUtilsVelocity instance = null;

    public static CubesideUtilsVelocity getInstance() {
        return instance;
    }

    private UtilsPluginVelocity plugin;

    private final ProxyServer server;
    private final org.slf4j.Logger logger;
    private final Path dataDirectory;
    private CommentedConfigurationNode configuration;
    private UtilsDatabaseVelocity database;
    private PlayerDataCache playerDataCache;
    private GlobalClientPlugin globalClientPlugin;
    private UtilsGlobalDataHelperVelocity globalDataHelper;

    public CubesideUtilsVelocity(UtilsPluginVelocity plugin, ProxyServer server, org.slf4j.Logger logger, @DataDirectory Path dataDirectory) {
        synchronized (CubesideUtilsVelocity.class) {
            if (instance != null) {
                throw new IllegalStateException("Only one instance permitted.");
            }
            instance = this;
        }

        this.plugin = plugin;
        this.server = server;
        this.logger = logger;
        this.dataDirectory = dataDirectory;
    }

    @Override
    protected void onEnableInternal() throws Throwable {

        try {
            Path configFile = dataDirectory.resolve("config.yml");
            if (Files.notExists(dataDirectory)) {
                Files.createDirectory(dataDirectory);
                if (Files.notExists(configFile)) {
                    try (InputStream stream = this.getClass().getClassLoader().getResourceAsStream("config.yml")) {
                        Files.copy(stream, configFile);
                    }
                }
            }

            YamlConfigurationLoader loader = YamlConfigurationLoader.builder().path(configFile).build();
            configuration = loader.load();
        } catch (IOException e) {
            logger.error("Error while loading config", e);
        }

        this.database = new UtilsDatabaseVelocity(new SQLConfigVelocity(this.configuration.node("database")));
        this.playerDataCache = new PlayerDataCache();

        this.globalClientPlugin = (GlobalClientPlugin) server.getPluginManager().getPlugin("globalconnectionvelocity").orElseThrow().getInstance().orElseThrow();

        server.getScheduler().buildTask(plugin, () -> { // TODO ob das so gut ist das zu verzögern.
            this.globalDataHelper = new UtilsGlobalDataHelperVelocity(this);
            updateRankInformation();
        }).delay(0L, TimeUnit.SECONDS);
    }

    @Override
    protected void shutdownServer() {
        server.shutdown();
    }

    public UtilsPluginVelocity getPlugin() {
        return this.plugin;
    }

    public ProxyServer getServer() {
        return server;
    }

    public GlobalClientPlugin getGlobalClientPlugin() {
        return this.globalClientPlugin;
    }

    @Override
    public ConnectionAPI getConnectionApi() {
        return this.globalClientPlugin.getConnectionAPI();
    }

    @Override
    public UtilsDatabaseVelocity getDatabase() {
        return this.database;
    }

    @Override
    public Logger getLogger() {
        return Logger.getLogger("CubesideUtils");
    }

    @Override
    public File getDataFolder() {
        throw new UnsupportedOperationException(); // FIXME
    }

    @Override
    public String getMinecraftVersion() {
        throw new UnsupportedOperationException(); // FIXME
    }

    @Override
    public UtilsGlobalDataHelperVelocity getGlobalDataHelper() {
        return this.globalDataHelper;
    }

    public PlayerDataCache getPlayerDataCache() {
        return this.playerDataCache;
    }

    @Override
    public PlayerDataImplVelocity getPlayerData(Player player) {
        return getPlayerData(player.getUniqueId());
    }

    @Override
    public PlayerDataImplVelocity getPlayerData(UUID playerId) {
        return this.playerDataCache.get(playerId);
    }

    @Override
    protected Iterable<? extends PlayerDataImpl> getLoadedPlayerData() {
        return this.playerDataCache.loadedData();
    }

    @Override
    public ClassLoader getServerClassLoader() {
        return ProxyServer.class.getClassLoader();
    }
}
