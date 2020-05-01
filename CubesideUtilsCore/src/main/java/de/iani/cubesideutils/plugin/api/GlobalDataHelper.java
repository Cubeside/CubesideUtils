package de.iani.cubesideutils.plugin.api;

import de.cubeside.connection.ConnectionAPI;
import de.cubeside.connection.GlobalPlayer;
import de.cubeside.connection.GlobalServer;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public interface GlobalDataHelper<T extends Enum<T>> extends ConnectionAPI {

    public String getChannel();

    public GlobalPlayer getPlayer(String name);

    public GlobalPlayer getPlayer(UUID uuid);

    public Collection<GlobalPlayer> getPlayers();

    public GlobalServer getServer(String name);

    public Collection<GlobalServer> getServers();

    public GlobalServer getThisServer();

    public String getThisServerName();

    public boolean isReal(GlobalServer server);

    public boolean isReal(String serverName);

    public List<GlobalServer> getServers(UUID playerId);

    public List<GlobalServer> getServers(UUID playerId, boolean includeNonReals);

    public List<GlobalServer> getServers(String playerName);

    public List<GlobalServer> getServers(String playerName, boolean includeNonReals);

    public List<GlobalServer> getServers(GlobalPlayer gPlayer);

    public List<GlobalServer> getServers(GlobalPlayer gPlayer, boolean includeNonReals);

    public boolean isOnAnyServer(UUID playerId);

    public boolean isOnAnyServer(UUID playerId, boolean includeNonReals);

    public boolean isOnAnyServer(String playerName);

    public boolean isOnAnyServer(String playerName, boolean includeNonReals);

    public boolean isOnAnyServer(GlobalPlayer gPlayer);

    public boolean isOnAnyServer(GlobalPlayer gPlayer, boolean includeNonReals);

    public Collection<GlobalPlayer> getOnlinePlayers();

    public Collection<GlobalPlayer> getOnlinePlayers(boolean includeNonReals);

    public Set<String> getOnlinePlayerNames();

    public Set<String> getOnlinePlayerNames(boolean includeNonReals);

    public void sendData(String channel, byte[] data, boolean sendToRestricted);

    public void sendData(String channel, byte[] data);

    // TODO FIX SEND TO RESTRICTED
    public void broadcastData(boolean sendToRestricted, T messageType, Object... data);

    // Equivalent to broadcastData(false, messageType, data);
    public void sendData(T messageType, Object... data);

    public void sendData(GlobalServer server, T messageType, Object... data);

    public void sendData(Collection<GlobalServer> servers, T messageType, Object... data);

}