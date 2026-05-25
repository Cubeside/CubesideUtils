package de.iani.cubesideutils.bukkit.plugin.api;

import de.cubeside.connection.GlobalClientPlugin;
import de.cubeside.connection.GlobalPlayer;
import de.cubeside.connection.GlobalServer;
import de.cubeside.connection.PlayerMessageAPI;
import de.cubeside.connection.PlayerPropertiesAPI;
import de.cubeside.connection.event.GlobalDataEvent;
import de.cubeside.connection.util.GlobalLocation;
import de.iani.cubesideutils.bukkit.plugin.CubesideUtilsBukkit;
import de.iani.cubesideutils.bukkit.serialization.GlobalLocationWrapper;
import de.iani.cubesideutils.plugin.GlobalDataHelperImpl;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.json.JSONComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

public abstract class GlobalDataHelperBukkit<T extends Enum<T>> extends GlobalDataHelperImpl<T> implements PlayerMessageAPI, PlayerPropertiesAPI, Listener {

    private final PlayerMessageAPI playerMsgApi;
    private final PlayerPropertiesAPI playerPropertiesApi;
    protected final JavaPlugin plugin;

    public GlobalDataHelperBukkit(Class<T> messageTypeClass, String channel, JavaPlugin plugin) {
        super(messageTypeClass, channel);
        this.plugin = plugin;

        GlobalClientPlugin globalClientPlugin = CubesideUtilsBukkit.getInstance().getGlobalClientPlugin();
        this.playerMsgApi = globalClientPlugin.getMessageAPI();
        this.playerPropertiesApi = globalClientPlugin.getPlayerPropertiesAPI();

        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    public GlobalPlayer getPlayer(OfflinePlayer player) {
        return getPlayer(player.getUniqueId());
    }

    public void sendMessage(OfflinePlayer player, Component message) {
        sendMessage(getPlayer(player), message);
    }

    public void sendMessage(UUID playerId, Component message) {
        sendMessage(getPlayer(playerId), message);
    }

    public void sendMessage(String playerName, Component message) {
        sendMessage(getPlayer(playerName), message);
    }

    @Override
    public void sendMessage(GlobalPlayer player, Component message) {
        this.playerMsgApi.sendMessage(player, message);
    }

    @Override
    public void sendActionBarMessage(GlobalPlayer player, Component message) {
        this.playerMsgApi.sendActionBarMessage(player, message);
    }

    public void sendActionBarMessage(OfflinePlayer player, Component message) {
        sendActionBarMessage(getPlayer(player), message);
    }

    @Override
    public void sendTitleBarMessage(GlobalPlayer player, Component title, Component subtitle, int fadeInTicks, int durationTicks, int fadeOutTicks) {
        this.playerMsgApi.sendTitleBarMessage(player, title, subtitle, fadeInTicks, durationTicks, fadeOutTicks);
    }

    public void sendTitleBarMessage(OfflinePlayer player, Component title, Component subtitle, int fadeInTicks, int durationTicks, int fadeOutTicks) {
        sendTitleBarMessage(getPlayer(player), title, subtitle, fadeInTicks, durationTicks, fadeOutTicks);
    }

    public boolean hasProperty(OfflinePlayer player, String property) {
        return hasProperty(getPlayer(player), property);
    }

    public boolean hasProperty(UUID playerId, String property) {
        return hasProperty(getPlayer(playerId), property);
    }

    public boolean hasProperty(String playerName, String property) {
        return hasProperty(getPlayer(playerName), property);
    }

    @Override
    public boolean hasProperty(GlobalPlayer player, String property) {
        return playerPropertiesApi.hasProperty(player, property);
    }

    public String getPropertyValue(OfflinePlayer player, String property) {
        return getPropertyValue(getPlayer(player), property);
    }

    public String getPropertyValue(UUID playerId, String property) {
        return getPropertyValue(getPlayer(playerId), property);
    }

    public String getPropertyValue(String playerName, String property) {
        return getPropertyValue(getPlayer(playerName), property);
    }

    @Override
    public String getPropertyValue(GlobalPlayer player, String property) {
        return playerPropertiesApi.getPropertyValue(player, property);
    }

    public Map<String, String> getAllProperties(OfflinePlayer player) {
        return getAllProperties(getPlayer(player));
    }

    public Map<String, String> getAllProperties(UUID playerId) {
        return getAllProperties(getPlayer(playerId));
    }

    public Map<String, String> getAllProperties(String playerName) {
        return getAllProperties(getPlayer(playerName));
    }

    @Override
    public Map<String, String> getAllProperties(GlobalPlayer player) {
        return playerPropertiesApi.getAllProperties(player);
    }

    public void setPropertyValue(OfflinePlayer player, String property, String value) {
        setPropertyValue(getPlayer(player), property, value);
    }

    public void setPropertyValue(UUID playerId, String property, String value) {
        setPropertyValue(getPlayer(playerId), property, value);
    }

    public void setPropertyValue(String playerName, String property, String value) {
        setPropertyValue(getPlayer(playerName), property, value);
    }

    @Override
    public void setPropertyValue(GlobalPlayer player, String property, String value) {
        playerPropertiesApi.setPropertyValue(player, property, value);
    }

    public List<GlobalServer> getServers(OfflinePlayer player) {
        return getServers(player, false);
    }

    public List<GlobalServer> getServers(OfflinePlayer player, boolean includeNonReals) {
        return getServers(getPlayer(player), includeNonReals);
    }

    public boolean isOnAnyServer(OfflinePlayer player) {
        return isOnAnyServer(player, false);
    }

    public boolean isOnAnyServer(OfflinePlayer player, boolean includeNonReals) {
        return isOnAnyServer(getPlayer(player), includeNonReals);
    }

    @Override
    protected void sendMsgPart(DataOutputStream msgout, Object msg) throws IOException {
        if (msg instanceof GlobalLocation) {
            GlobalLocationWrapper wrapper = new GlobalLocationWrapper((GlobalLocation) msg);
            sendMsgPart(msgout, wrapper);
        } else {
            super.sendMsgPart(msgout, msg);
        }
    }

    protected GlobalLocation readGlobalLocation(DataInputStream msgin) throws IOException {
        GlobalLocationWrapper wrapper = readStringSerializable(msgin);
        return wrapper.original;
    }

    protected Component readAdventureComponent(DataInputStream msgin) throws IOException {
        String serialized = msgin.readUTF();
        return JSONComponentSerializer.json().deserialize(serialized);
    }

    @EventHandler
    public void onGlobalDataEvent(GlobalDataEvent event) throws IOException {
        if (!event.getChannel().equals(getChannel())) {
            return;
        }

        DataInputStream data = new DataInputStream(event.getData());
        int messageTypeId = data.readInt();
        T messageType = fromOrdinal(messageTypeId);
        if (messageType == null) {
            plugin.getLogger().log(Level.WARNING, "Unknown data type for DataHelper " + getMessageTypeClass().getName() + ": " + messageTypeId);
        } else {
            handleMessage(messageType, event.getSource(), data);
        }
    }

}
