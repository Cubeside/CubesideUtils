package de.iani.cubesideutils.plugin;

import de.cubeside.connection.ConnectionAPI;
import de.cubeside.connection.GlobalClientPlugin;
import de.cubeside.connection.GlobalPlayer;
import de.cubeside.connection.GlobalServer;
import de.cubeside.connection.PlayerMessageAPI;
import de.cubeside.connection.PlayerPropertiesAPI;
import de.iani.cubesideutils.FunctionUtil;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.md_5.bungee.api.chat.BaseComponent;
import org.bukkit.OfflinePlayer;

public class GlobalDataBundle implements ConnectionAPI, PlayerMessageAPI, PlayerPropertiesAPI {

    private ConnectionAPI connectionApi;
    private PlayerMessageAPI playerMsgApi;
    private PlayerPropertiesAPI playerPropertiesApi;

    public GlobalDataBundle() {
        GlobalClientPlugin globalClientPlugin = UtilsPlugin.getInstance().getGlobalClientPlugin();
        this.connectionApi = globalClientPlugin.getConnectionAPI();
        this.playerMsgApi = globalClientPlugin.getMessageAPI();
        this.playerPropertiesApi = globalClientPlugin.getPlayerPropertiesAPI();
    }

    public GlobalPlayer getPlayer(OfflinePlayer player) {
        return this.connectionApi.getPlayer(player.getUniqueId());
    }

    @Override
    public GlobalPlayer getPlayer(UUID playerId) {
        return this.connectionApi.getPlayer(playerId);
    }

    @Override
    public GlobalPlayer getPlayer(String name) {
        return this.connectionApi.getPlayer(name);
    }

    @Override
    public Collection<GlobalPlayer> getPlayers() {
        return this.connectionApi.getPlayers();
    }

    @Override
    public Collection<GlobalServer> getServers() {
        return this.connectionApi.getServers();
    }

    @Override
    public GlobalServer getServer(String serverName) {
        return this.connectionApi.getServer(serverName);
    }

    @Override
    public GlobalServer getThisServer() {
        return this.connectionApi.getThisServer();
    }

    public String getThisServerName() {
        return this.connectionApi.getThisServer().getName();
    }

    public void sendMessage(OfflinePlayer player, String message) {
        sendMessage(getPlayer(player), message);
    }

    public void sendMessage(UUID playerId, String message) {
        sendMessage(getPlayer(playerId), message);
    }

    public void sendMessage(String playerName, String message) {
        sendMessage(getPlayer(playerName), message);
    }

    @Override
    public void sendMessage(GlobalPlayer player, String message) {
        this.playerMsgApi.sendMessage(player, message);
    }

    public void sendMessage(OfflinePlayer player, BaseComponent... message) {
        sendMessage(getPlayer(player), message);
    }

    public void sendMessage(UUID playerId, BaseComponent... message) {
        sendMessage(getPlayer(playerId), message);
    }

    public void sendMessage(String playerName, BaseComponent... message) {
        sendMessage(getPlayer(playerName), message);
    }

    @Override
    public void sendMessage(GlobalPlayer player, BaseComponent... message) {
        this.playerMsgApi.sendMessage(player, message);
    }

    public void sendActionBarMessage(OfflinePlayer player, String message) {
        sendActionBarMessage(getPlayer(player), message);
    }

    public void sendActionBarMessage(UUID playerId, String message) {
        sendActionBarMessage(getPlayer(playerId), message);
    }

    public void sendActionBarMessage(String playerName, String message) {
        sendActionBarMessage(getPlayer(playerName), message);
    }

    @Override
    public void sendActionBarMessage(GlobalPlayer player, String message) {
        this.playerMsgApi.sendActionBarMessage(player, message);
    }

    public void sendTitleBarMessage(OfflinePlayer player, String title, String subtitle, int fadeInTicks, int durationTicks, int fadeOutTicks) {
        sendTitleBarMessage(getPlayer(player), title, subtitle, fadeInTicks, durationTicks, fadeOutTicks);
    }

    public void sendTitleBarMessage(UUID playerId, String title, String subtitle, int fadeInTicks, int durationTicks, int fadeOutTicks) {
        sendTitleBarMessage(getPlayer(playerId), title, subtitle, fadeInTicks, durationTicks, fadeOutTicks);
    }

    public void sendTitleBarMessage(String playerName, String title, String subtitle, int fadeInTicks, int durationTicks, int fadeOutTicks) {
        sendTitleBarMessage(getPlayer(playerName), title, subtitle, fadeInTicks, durationTicks, fadeOutTicks);
    }

    @Override
    public void sendTitleBarMessage(GlobalPlayer player, String title, String subtitle, int fadeInTicks, int durationTicks, int fadeOutTicks) {
        this.playerMsgApi.sendTitleBarMessage(player, title, subtitle, fadeInTicks, durationTicks, fadeOutTicks);
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

    public boolean isReal(GlobalServer server) {
        return isReal(server.getName());
    }

    public boolean isReal(String serverName) {
        Map<String, Boolean> cached = UtilsPlugin.getInstance().getCachedRealServers();
        Boolean result = cached.get(serverName);
        if (result != null) {
            return result;
        }

        cached.values().removeIf(Boolean::booleanValue);
        Set<String> realServers;
        try {
            realServers = UtilsPlugin.getInstance().getDatabase().getRealServers();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        for (String real : realServers) {
            cached.put(real, true);
        }

        return cached.computeIfAbsent(serverName, name -> false);
    }

    public List<GlobalServer> getServers(OfflinePlayer player) {
        return getServers(player, false);
    }

    public List<GlobalServer> getServers(OfflinePlayer player, boolean includeNonReals) {
        return getServers(getPlayer(player), includeNonReals);
    }

    public List<GlobalServer> getServers(UUID playerId) {
        return getServers(playerId, false);
    }

    public List<GlobalServer> getServers(UUID playerId, boolean includeNonReals) {
        return getServers(getPlayer(playerId), includeNonReals);
    }

    public List<GlobalServer> getServers(String playerName) {
        return getServers(playerName, false);
    }

    public List<GlobalServer> getServers(String playerName, boolean includeNonReals) {
        return getServers(getPlayer(playerName), includeNonReals);
    }

    public List<GlobalServer> getServers(GlobalPlayer gPlayer) {
        return getServers(gPlayer, false);
    }

    public List<GlobalServer> getServers(GlobalPlayer gPlayer, boolean includeNonReals) {
        if (gPlayer == null) {
            return Collections.emptyList();
        }

        List<GlobalServer> result = gPlayer.getCurrentServers();
        if (includeNonReals) {
            return result;
        }

        try {
            result.removeIf(FunctionUtil.negate(this::isReal));
        } catch (UnsupportedOperationException e) {
            result = new ArrayList<>(result);
            result.removeIf(FunctionUtil.negate(this::isReal));
        }
        return result;
    }

    public boolean isOnAnyServer(OfflinePlayer player) {
        return isOnAnyServer(player, false);
    }

    public boolean isOnAnyServer(OfflinePlayer player, boolean includeNonReals) {
        return isOnAnyServer(getPlayer(player), includeNonReals);
    }

    public boolean isOnAnyServer(UUID playerId) {
        return isOnAnyServer(playerId, false);
    }

    public boolean isOnAnyServer(UUID playerId, boolean includeNonReals) {
        return isOnAnyServer(getPlayer(playerId), includeNonReals);
    }

    public boolean isOnAnyServer(String playerName) {
        return isOnAnyServer(playerName, false);
    }

    public boolean isOnAnyServer(String playerName, boolean includeNonReals) {
        return isOnAnyServer(getPlayer(playerName), includeNonReals);
    }

    public boolean isOnAnyServer(GlobalPlayer gPlayer) {
        return isOnAnyServer(gPlayer, false);
    }

    public boolean isOnAnyServer(GlobalPlayer gPlayer, boolean includeNonReals) {
        if (gPlayer == null) {
            return false;
        }
        if (includeNonReals) {
            return gPlayer.isOnAnyServer();
        }
        return gPlayer.getCurrentServers().stream().anyMatch(this::isReal);
    }

    public Collection<GlobalPlayer> getOnlinePlayers() {
        return getOnlinePlayers(false);
    }

    public Collection<GlobalPlayer> getOnlinePlayers(boolean includeNonReals) {
        Collection<GlobalPlayer> result = getPlayers();
        if (!includeNonReals) {
            result = result.stream().filter(this::isOnAnyServer).collect(Collectors.toList());
        }
        return result;
    }

    public Set<String> getOnlinePlayerNames() {
        return getOnlinePlayerNames(false);
    }

    public Set<String> getOnlinePlayerNames(boolean includeNonReals) {
        Stream<GlobalPlayer> stream = getPlayers().stream();
        if (!includeNonReals) {
            stream = stream.filter(this::isOnAnyServer);
        }
        return stream.map(GlobalPlayer::getName).collect(Collectors.toSet());
    }

    @Override
    public void sendData(String channel, byte[] data) {
        this.connectionApi.sendData(channel, data);
    }

}
