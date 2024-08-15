package de.iani.cubesideutils.plugin;

import de.cubeside.connection.ConnectionAPI;
import de.cubeside.connection.GlobalPlayer;
import de.cubeside.connection.GlobalServer;
import de.iani.cubesideutils.ComponentUtil;
import de.iani.cubesideutils.FunctionUtil;
import de.iani.cubesideutils.plugin.api.GlobalDataHelper;
import de.iani.cubesideutils.serialization.StringSerializable;
import de.iani.cubesideutils.serialization.StringSerialization;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.logging.Level;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;

public abstract class GlobalDataHelperImpl<T extends Enum<T>> implements GlobalDataHelper<T> {

    private final ConnectionAPI connectionAPI;

    private final String channel;
    private Class<T> messageTypeClass;
    private final T[] messageTypes;

    public GlobalDataHelperImpl(Class<T> messageTypeClass, String channel) {
        this.connectionAPI = CubesideUtils.getInstance().getConnectionApi();
        this.channel = channel;
        this.messageTypeClass = messageTypeClass;
        this.messageTypes = messageTypeClass.getEnumConstants();
    }

    @Override
    public String getChannel() {
        return this.channel;
    }

    @Override
    public GlobalPlayer getPlayer(String name) {
        return this.connectionAPI.getPlayer(name);
    }

    @Override
    public GlobalPlayer getPlayer(UUID uuid) {
        return this.connectionAPI.getPlayer(uuid);
    }

    @Override
    public Collection<GlobalPlayer> getPlayers() {
        return this.connectionAPI.getPlayers();
    }

    @Override
    public GlobalServer getServer(String name) {
        return this.connectionAPI.getServer(name);
    }

    @Override
    public Collection<GlobalServer> getServers() {
        return this.connectionAPI.getServers();
    }

    @Override
    public GlobalServer getThisServer() {
        return this.connectionAPI.getThisServer();
    }

    @Override
    public String getThisServerName() {
        return getThisServer().getName();
    }

    @Override
    public boolean isReal(GlobalServer server) {
        return isReal(server.getName());
    }

    @Override
    public boolean isReal(String serverName) {
        Map<String, Boolean> cached = CubesideUtils.getInstance().getCachedRealServers();
        Boolean result = cached.get(serverName);
        if (result != null) {
            return result;
        }

        cached.values().removeIf(Boolean::booleanValue);
        Set<String> realServers;
        try {
            realServers = CubesideUtils.getInstance().getDatabase().getRealServers();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        for (String real : realServers) {
            cached.put(real, true);
        }

        return cached.computeIfAbsent(serverName, name -> false);
    }

    @Override
    public List<GlobalServer> getServers(UUID playerId) {
        return getServers(playerId, false);
    }

    @Override
    public List<GlobalServer> getServers(UUID playerId, boolean includeNonReals) {
        return getServers(getPlayer(playerId), includeNonReals);
    }

    @Override
    public List<GlobalServer> getServers(String playerName) {
        return getServers(playerName, false);
    }

    @Override
    public List<GlobalServer> getServers(String playerName, boolean includeNonReals) {
        return getServers(getPlayer(playerName), includeNonReals);
    }

    @Override
    public List<GlobalServer> getServers(GlobalPlayer gPlayer) {
        return getServers(gPlayer, false);
    }

    @Override
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

    @Override
    public boolean isOnAnyServer(UUID playerId) {
        return isOnAnyServer(playerId, false);
    }

    @Override
    public boolean isOnAnyServer(UUID playerId, boolean includeNonReals) {
        return isOnAnyServer(getPlayer(playerId), includeNonReals);
    }

    @Override
    public boolean isOnAnyServer(String playerName) {
        return isOnAnyServer(playerName, false);
    }

    @Override
    public boolean isOnAnyServer(String playerName, boolean includeNonReals) {
        return isOnAnyServer(getPlayer(playerName), includeNonReals);
    }

    @Override
    public boolean isOnAnyServer(GlobalPlayer gPlayer) {
        return isOnAnyServer(gPlayer, false);
    }

    @Override
    public boolean isOnAnyServer(GlobalPlayer gPlayer, boolean includeNonReals) {
        if (gPlayer == null) {
            return false;
        }
        if (includeNonReals) {
            return gPlayer.isOnAnyServer();
        }
        return gPlayer.getCurrentServers().stream().anyMatch(this::isReal);
    }

    @Override
    public Collection<GlobalPlayer> getOnlinePlayers() {
        return getOnlinePlayers(false);
    }

    @Override
    public Collection<GlobalPlayer> getOnlinePlayers(boolean includeNonReals) {
        Collection<GlobalPlayer> result = getPlayers();
        if (!includeNonReals) {
            result = result.stream().filter(this::isOnAnyServer).collect(Collectors.toList());
        }
        return result;
    }

    @Override
    public Set<String> getOnlinePlayerNames() {
        return getOnlinePlayerNames(false);
    }

    @Override
    public Set<String> getOnlinePlayerNames(boolean includeNonReals) {
        Stream<GlobalPlayer> stream = getPlayers().stream();
        if (!includeNonReals) {
            stream = stream.filter(this::isOnAnyServer);
        }
        return stream.map(GlobalPlayer::getName).collect(Collectors.toSet());
    }

    @Override
    public void sendData(String channel, byte[] data, boolean sendToRestricted) {
        this.connectionAPI.sendData(channel, data, sendToRestricted);
    }

    @Override
    public void sendData(String channel, byte[] data) {
        this.connectionAPI.sendData(channel, data);
    }

    // Equivalent to broadcastData(true, messageType, data);
    @Override
    public void sendData(T messageType, Object... data) {
        sendData(true, messageType, data);
    }

    @Override
    public void sendData(boolean sendToRestricted, T messageType, Object... data) {
        Collection<GlobalServer> servers;
        if (sendToRestricted) {
            servers = new HashSet<>(getServers());
            servers.remove(getThisServer());
        } else {
            servers = null;
        }
        sendData(servers, messageType, data);
    }

    @Override
    public void sendData(GlobalServer server, T messageType, Object... data) {
        sendData(server == null ? null : Collections.singleton(server), messageType, data);
    }

    @Override
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
                sendData(channel, msgarry, false);
            } else {
                for (GlobalServer server : servers) {
                    server.sendData(channel, msgarry);
                }
            }
        } catch (IOException e) {
            CubesideUtils.getInstance().getLogger().log(Level.SEVERE, "IOException trying to send GlobalDataMessage!", e);
            return;
        }
    }

    protected void sendMsgPart(DataOutputStream msgout, Object msg) throws IOException {
        if (msg == null) {
            throw new NullPointerException();
        }

        // complex stuff
        if (msg instanceof UUID) {
            long first = ((UUID) msg).getMostSignificantBits();
            long second = ((UUID) msg).getLeastSignificantBits();
            msgout.writeLong(first);
            msgout.writeLong(second);
        } else if (msg instanceof StringSerializable) {
            StringSerializable serializable = (StringSerializable) msg;
            msgout.writeUTF(serializable.getSerializationType());
            msgout.writeUTF(serializable.serializeToString());
        } else if (msg instanceof BaseComponent) {
            msgout.writeUTF(ComponentUtil.serializeComponent((BaseComponent) msg));
        } else if (msg instanceof BaseComponent[]) {
            BaseComponent[] bc = (BaseComponent[]) msg;
            if (bc.length == 1) {
                sendMsgPart(msgout, bc[0]);
            } else {
                msgout.writeUTF(ComponentUtil.serializeComponent(new TextComponent(bc)));
            }
        } else
        // simple stuff
        if (msg instanceof String) {
            msgout.writeUTF((String) msg);
        } else if (msg instanceof Byte) {
            msgout.writeByte((Byte) msg);
        } else if (msg instanceof Short) {
            msgout.writeShort((Short) msg);
        } else if (msg instanceof Integer) {
            msgout.writeInt((Integer) msg);
        } else if (msg instanceof Long) {
            msgout.writeLong((Long) msg);
        } else if (msg instanceof Float) {
            msgout.writeFloat((Float) msg);
        } else if (msg instanceof Double) {
            msgout.writeDouble((Double) msg);
        } else if (msg instanceof Boolean) {
            msgout.writeBoolean((Boolean) msg);
        } else if (msg instanceof Character) {
            msgout.writeChar((Character) msg);
        } else {
            throw new IllegalArgumentException("Unsendable data object of type " + msg.getClass().getName() + ".");
        }
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

    protected BaseComponent readComponent(DataInputStream msgin) throws IOException {
        String serialized = msgin.readUTF();
        try {
            return ComponentUtil.deserializeComponent(serialized);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

    protected T fromOrdinal(int ordinal) {
        return ordinal < messageTypes.length ? messageTypes[ordinal] : null;
    }

    public Class<T> getMessageTypeClass() {
        return messageTypeClass;
    }

    protected abstract void handleMessage(T messageType, GlobalServer source, DataInputStream data) throws IOException;

}
