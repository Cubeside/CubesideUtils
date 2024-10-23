package de.iani.cubesideutils.velocity.plugin;

import com.google.inject.Inject;
import com.velocitypowered.api.plugin.Dependency;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import de.cubeside.connection.ConnectionAPI;
import de.cubeside.connection.GlobalClientPlugin;
import de.iani.cubesideutils.bungee.plugin.api.UtilsApiBungee;
import de.iani.cubesideutils.bungee.sql.SQLConfigBungee;
import de.iani.cubesideutils.plugin.CubesideUtils;
import de.iani.cubesideutils.plugin.PlayerDataImpl;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;
import java.util.logging.Logger;

import de.iani.cubesideutils.plugin.api.UtilsApi;
import de.iani.cubesideutils.velocity.plugin.api.UtilsApiVelocity;
import de.iani.cubesideutils.velocity.sql.SQLConfigVelocity;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;
import org.spongepowered.configurate.CommentedConfigurationNode;


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
            saveDefaultConfig();
            this.config = ConfigurationProvider.getProvider(YamlConfiguration.class).load(new File(this.plugin.getDataFolder(), "config.yml"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        this.database = new UtilsDatabaseVelocity(new SQLConfigVelocity(this.config.getSection("database")));
        this.playerDataCache = new PlayerDataCache();

        this.globalClientPlugin = (GlobalClientPlugin) ProxyServer.getInstance().getPluginManager().getPlugin("GlobalClient");
        this.globalDataHelper = new UtilsGlobalDataHelperVelocity(this.plugin);

        updateRankInformation();
    }

    private void saveDefaultConfig() throws IOException {
        File config = new File(this.plugin.getDataFolder(), "config.yml");
        if (config.exists()) {
            return;
        }
        InputStream defaultConfig = getClass().getClassLoader().getResourceAsStream("config.yml");
        this.plugin.getDataFolder().mkdirs();
        Files.copy(defaultConfig, config.toPath());
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

}
