package de.iani.cubesideutils.plugin;

import de.cubeside.connection.ConnectionAPI;
import de.cubeside.connection.GlobalPlayer;
import de.cubeside.connection.GlobalServer;
import de.cubeside.connection.PlayerMessageAPI;
import de.cubeside.connection.event.GlobalDataEvent;
import de.cubeside.connection.util.GlobalLocation;
import de.iani.cubesideutils.FunctionUtil;
import de.iani.cubesideutils.serialization.GlobalLocationWrapper;
import de.iani.cubesideutils.serialization.StringSerializable;
import de.iani.cubesideutils.serialization.StringSerialization;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.logging.Level;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.md_5.bungee.api.chat.BaseComponent;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

public abstract class GlobalDataHelper<T extends Enum<T>> implements ConnectionAPI, PlayerMessageAPI, Listener {

    private final String channel;
    private final T[] messageTypes;

    private ConnectionAPI connectionApi;
    private PlayerMessageAPI playerMsgApi;

    public GlobalDataHelper(Class<T> messageTypeClass, String channel, JavaPlugin plugin) {
        this.channel = channel;
        this.messageTypes = messageTypeClass.getEnumConstants();

        this.connectionApi = UtilsPlugin.getInstance().getConnectionAPI();
        this.playerMsgApi = UtilsPlugin.getInstance().getPlayerMsgApi();

        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    @Override
    public GlobalPlayer getPlayer(String name) {
        return this.connectionApi.getPlayer(name);
    }

    @Override
    public GlobalPlayer getPlayer(UUID playerId) {
        return this.connectionApi.getPlayer(playerId);
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
        sendMessage(player.getUniqueId(), message);
    }

    public void sendMessage(UUID playerId, String message) {
        sendMessage(this.connectionApi.getPlayer(playerId), message);
    }

    @Override
    public void sendMessage(GlobalPlayer player, String message) {
        this.playerMsgApi.sendMessage(player, message);
    }

    public void sendMessage(OfflinePlayer player, BaseComponent... message) {
        sendMessage(player.getUniqueId(), message);
    }

    public void sendMessage(UUID playerId, BaseComponent... message) {
        sendMessage(getPlayer(playerId), message);
    }

    @Override
    public void sendMessage(GlobalPlayer player, BaseComponent... message) {
        this.playerMsgApi.sendMessage(player, message);
    }

    public void sendActionBarMessage(OfflinePlayer player, String message) {
        sendActionBarMessage(player.getUniqueId(), message);
    }

    public void sendActionBarMessage(UUID playerId, String message) {
        sendActionBarMessage(getPlayer(playerId), message);
    }

    @Override
    public void sendActionBarMessage(GlobalPlayer player, String message) {
        this.playerMsgApi.sendActionBarMessage(player, message);
    }

    public void sendTitleBarMessage(OfflinePlayer player, String title, String subtitle, int fadeInTicks, int durationTicks, int fadeOutTicks) {
        sendTitleBarMessage(player.getUniqueId(), title, subtitle, fadeInTicks, durationTicks, fadeOutTicks);
    }

    public void sendTitleBarMessage(UUID playerId, String title, String subtitle, int fadeInTicks, int durationTicks, int fadeOutTicks) {
        sendTitleBarMessage(getPlayer(playerId), title, subtitle, fadeInTicks, durationTicks, fadeOutTicks);
    }

    @Override
    public void sendTitleBarMessage(GlobalPlayer player, String title, String subtitle, int fadeInTicks, int durationTicks, int fadeOutTicks) {
        this.playerMsgApi.sendTitleBarMessage(player, title, subtitle, fadeInTicks, durationTicks, fadeOutTicks);
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
        return getServers(player.getUniqueId(), includeNonReals);
    }

    public List<GlobalServer> getServers(UUID playerId) {
        return getServers(playerId, false);
    }

    public List<GlobalServer> getServers(UUID playerId, boolean includeNonReals) {
        return getServers(this.connectionApi.getPlayer(playerId), includeNonReals);
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
        return isOnAnyServer(player.getUniqueId(), includeNonReals);
    }

    public boolean isOnAnyServer(UUID playerId) {
        return isOnAnyServer(playerId, false);
    }

    public boolean isOnAnyServer(UUID playerId, boolean includeNonReals) {
        return isOnAnyServer(this.connectionApi.getPlayer(playerId), includeNonReals);
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

    public void sendData(T messageType, Object... data) {
        sendData((GlobalServer) null, messageType, data);
    }

    public void sendData(GlobalServer server, T messageType, Object... data) {
        sendData(server == null ? null : Collections.singleton(server), messageType, data);
    }

    public void sendData(Collection<GlobalServer> servers, T messageType, Object... data) {
        ByteArrayOutputStream msgbytes = new ByteArrayOutputStream();
        DataOutputStream msgout = new DataOutputStream(msgbytes);
        try {
            msgout.writeInt(messageType.ordinal());

            for (Object msg : data) {
                sendMsgPart(msgout, msg);
            }

            byte[] msgarry = msgbytes.toByteArray();
            if (servers == null) {
                sendData(channel, msgarry);
            } else {
                for (GlobalServer server : servers) {
                    server.sendData(channel, msgarry);
                }
            }
        } catch (IOException e) {
            UtilsPlugin.getInstance().getLogger().log(Level.SEVERE, "IOException trying to send GlobalDataMessage!", e);
            return;
        }
    }

    private void sendMsgPart(DataOutputStream msgout, Object msg) throws IOException {
        if (msg == null) {
            throw new NullPointerException();
        }

        if (msg instanceof UUID) {
            long first = ((UUID) msg).getMostSignificantBits();
            long second = ((UUID) msg).getLeastSignificantBits();
            msgout.writeLong(first);
            msgout.writeLong(second);
            return;
        }
        if (msg instanceof StringSerializable) {
            StringSerializable serializable = (StringSerializable) msg;
            msgout.writeUTF(serializable.getSerializationType());
            msgout.writeUTF(serializable.serializeToString());
            return;
        }
        if (msg instanceof GlobalLocation) {
            GlobalLocationWrapper wrapper = new GlobalLocationWrapper((GlobalLocation) msg);
            sendMsgPart(msgout, wrapper);
            return;
        }
        if (msg instanceof String) {
            msgout.writeUTF((String) msg);
            return;
        }
        if (msg instanceof Byte) {
            msgout.writeByte((Byte) msg);
            return;
        }
        if (msg instanceof Short) {
            msgout.writeShort((Short) msg);
            return;
        }
        if (msg instanceof Integer) {
            msgout.writeInt((Integer) msg);
            return;
        }
        if (msg instanceof Long) {
            msgout.writeLong((Long) msg);
            return;
        }
        if (msg instanceof Float) {
            msgout.writeFloat((Float) msg);
            return;
        }
        if (msg instanceof Double) {
            msgout.writeDouble((Double) msg);
            return;
        }
        if (msg instanceof Boolean) {
            msgout.writeBoolean((Boolean) msg);
            return;
        }
        if (msg instanceof Character) {
            msgout.writeChar((Character) msg);
            return;
        }

        throw new IllegalArgumentException("Unsendable data object of type " + msg.getClass().getName() + ".");
    }

    protected UUID readUUID(DataInputStream msgin) throws IOException {
        long first = msgin.readLong();
        long second = msgin.readLong();
        return new UUID(first, second);
    }

    protected <S extends StringSerializable> S readStringSerializable(DataInputStream msgin) throws IOException {
        String type = msgin.readUTF();
        String serialized = msgin.readUTF();
        return StringSerialization.deserialize(type, serialized);
    }

    protected GlobalLocation readGlobalLocation(DataInputStream msgin) throws IOException {
        GlobalLocationWrapper wrapper = readStringSerializable(msgin);
        return wrapper.original;
    }

    private T fromOrdinal(int ordinal) {
        return messageTypes[ordinal];
    }

    @EventHandler
    public void onGlobalDataEvent(GlobalDataEvent event) throws IOException {
        if (!event.getChannel().equals(channel)) {
            return;
        }

        DataInputStream data = new DataInputStream(event.getData());
        T messageType = fromOrdinal(data.readInt());
        handleMessage(messageType, data);
    }

    protected abstract void handleMessage(T messageType, DataInputStream data) throws IOException;

}
