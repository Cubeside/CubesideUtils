package de.iani.cubesideutils.plugin;

import de.cubeside.connection.ConnectionAPI;
import de.cubeside.connection.GlobalPlayer;
import de.cubeside.connection.GlobalServer;
import de.cubeside.connection.PlayerMessageAPI;
import de.cubeside.connection.event.GlobalDataEvent;
import de.cubeside.connection.util.GlobalLocation;
import de.iani.cubesideutils.serialization.GlobalLocationWrapper;
import de.iani.cubesideutils.serialization.StringSerializable;
import de.iani.cubesideutils.serialization.StringSerialization;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.UUID;
import java.util.logging.Level;
import java.util.stream.Collectors;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

public abstract class GlobalDataHelper<T extends Enum<T>> implements Listener {

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

    public Collection<GlobalServer> getServers(OfflinePlayer player) {
        GlobalPlayer gPlayer = this.connectionApi.getPlayer(player.getUniqueId());
        return gPlayer == null ? Collections.emptySet() : gPlayer.getCurrentServers();
    }

    public GlobalServer getServer(String serverName) {
        return this.connectionApi.getServer(serverName);
    }

    public String getThisServerName() {
        return this.connectionApi.getThisServer().getName();
    }

    public boolean isOnAnyServer(OfflinePlayer player) {
        GlobalPlayer gPlayer = this.connectionApi.getPlayer(player.getUniqueId());
        return gPlayer != null && gPlayer.isOnAnyServer();
    }

    public Collection<GlobalPlayer> getOnlinePlayers() {
        return this.connectionApi.getPlayers();
    }

    public Collection<String> getOnlinePlayerNames() {
        return getOnlinePlayers().stream().map(GlobalPlayer::getName).collect(Collectors.toList());
    }

    public void sendMessage(OfflinePlayer player, String message) {
        this.playerMsgApi.sendMessage(this.connectionApi.getPlayer(player.getUniqueId()), message);
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
                UtilsPlugin.getInstance().getConnectionAPI().sendData(channel, msgarry);
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
