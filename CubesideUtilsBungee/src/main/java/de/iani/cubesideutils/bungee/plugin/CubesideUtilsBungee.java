package de.iani.cubesideutils.bungee.plugin;

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
import java.util.UUID;
import java.util.logging.Logger;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;

public class CubesideUtilsBungee extends CubesideUtils implements UtilsApiBungee {
    private static volatile CubesideUtilsBungee instance = null;

    public static CubesideUtilsBungee getInstance() {
        return instance;
    }

    private UtilsPluginBungee plugin;

    private Configuration config;
    private UtilsDatabaseBungee database;
    private PlayerDataCache playerDataCache;
    private GlobalClientPlugin globalClientPlugin;
    private UtilsGlobalDataHelperBungee globalDataHelper;

    public CubesideUtilsBungee(UtilsPluginBungee plugin) {
        synchronized (CubesideUtilsBungee.class) {
            if (instance != null) {
                throw new IllegalStateException("Only one instance permitted.");
            }
            instance = this;
        }

        this.plugin = plugin;
    }

    @Override
    protected void onEnableInternal() throws Throwable {
        try {
            saveDefaultConfig();
            this.config = ConfigurationProvider.getProvider(YamlConfiguration.class).load(new File(this.plugin.getDataFolder(), "config.yml"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        this.database = new UtilsDatabaseBungee(new SQLConfigBungee(this.config.getSection("database")));
        this.playerDataCache = new PlayerDataCache();

        this.globalClientPlugin = (GlobalClientPlugin) ProxyServer.getInstance().getPluginManager().getPlugin("GlobalClient");
        this.globalDataHelper = new UtilsGlobalDataHelperBungee(this.plugin);

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
        ProxyServer.getInstance().stop();
    }

    public UtilsPluginBungee getPlugin() {
        return this.plugin;
    }

    public GlobalClientPlugin getGlobalClientPlugin() {
        return this.globalClientPlugin;
    }

    @Override
    public ConnectionAPI getConnectionApi() {
        return this.globalClientPlugin.getConnectionAPI();
    }

    @Override
    public UtilsDatabaseBungee getDatabase() {
        return this.database;
    }

    @Override
    public Logger getLogger() {
        return this.plugin.getLogger();
    }

    @Override
    public File getDataFolder() {
        return this.plugin.getDataFolder();
    }

    @Override
    public String getMinecraftVersion() {
        throw new UnsupportedOperationException(); // FIXME
    }

    @Override
    public UtilsGlobalDataHelperBungee getGlobalDataHelper() {
        return this.globalDataHelper;
    }

    public PlayerDataCache getPlayerDataCache() {
        return this.playerDataCache;
    }

    @Override
    public PlayerDataImplBungee getPlayerData(ProxiedPlayer player) {
        return getPlayerData(player.getUniqueId());
    }

    @Override
    public PlayerDataImplBungee getPlayerData(UUID playerId) {
        return this.playerDataCache.get(playerId);
    }

    @Override
    protected Iterable<? extends PlayerDataImpl> getLoadedPlayerData() {
        return this.playerDataCache.loadedData();
    }

}
