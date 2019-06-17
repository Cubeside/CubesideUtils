package de.iani.cubesideutils.plugin;

import de.cubeside.connection.GlobalServer;
import de.cubeside.connection.event.GlobalDataEvent;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.UUID;
import java.util.logging.Level;
import org.bukkit.event.EventHandler;

class ConnectionUtil {

    public enum MessageType {
        PLAYER_DATA_CHANGED;

        private static MessageType[] values;

        public static MessageType fromOrdinal(int ordinal) {
            return values[ordinal];
        }
    }

    public static void sendData(MessageType messageType, Object... data) {
        sendData((GlobalServer) null, messageType, data);
    }

    public static void sendData(GlobalServer server, MessageType messageType, Object... data) {
        sendData(server == null ? null : Collections.singleton(server), messageType, data);
    }

    public static void sendData(Collection<GlobalServer> servers, MessageType messageType, Object... data) {
        ByteArrayOutputStream msgbytes = new ByteArrayOutputStream();
        DataOutputStream msgout = new DataOutputStream(msgbytes);
        try {
            msgout.writeInt(messageType.ordinal());

            for (Object msg : data) {
                sendMsgPart(msgout, msg);
            }

            byte[] msgarry = msgbytes.toByteArray();
            if (servers == null) {
                UtilsPlugin.getInstance().getConnectionAPI().sendData(UtilsPlugin.GLOBAL_DATA_CHANNEL, msgarry);
            } else {
                for (GlobalServer server : servers) {
                    server.sendData(UtilsPlugin.GLOBAL_DATA_CHANNEL, msgarry);
                }
            }
        } catch (IOException e) {
            UtilsPlugin.getInstance().getLogger().log(Level.SEVERE, "IOException trying to send GlobalDataMessage!", e);
            return;
        }
    }

    private static void sendMsgPart(DataOutputStream msgout, Object msg) throws IOException {
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

    private static UUID readUUID(DataInputStream msgin) throws IOException {
        long first = msgin.readLong();
        long second = msgin.readLong();
        return new UUID(first, second);
    }

    @EventHandler
    public static void onGlobalDataEvent(GlobalDataEvent event) throws IOException {
        if (!event.getChannel().equals(UtilsPlugin.GLOBAL_DATA_CHANNEL)) {
            return;
        }

        DataInputStream data = new DataInputStream(event.getData());
        MessageType messageType = MessageType.fromOrdinal(data.readInt());

        switch (messageType) {
            case PLAYER_DATA_CHANGED:
                handlePlayerDataChanged(readUUID(data));
                break;
            // case SEND_MESSAGE:
            // String permission = data.readUTF();
            // String message = data.readUTF();
            // if (permission.isEmpty() || Bukkit.getConsoleSender().hasPermission(permission)) {
            // Bukkit.getConsoleSender().sendMessage(message);
            // }
            // for (Player player : Bukkit.getOnlinePlayers()) {
            // if (permission.isEmpty() || player.hasPermission(permission)) {
            // player.sendMessage(message);
            // }
            // }
            // break;
            default:
                break;
        }
    }

    private static void handlePlayerDataChanged(UUID readUUID) {
        // TODO Auto-generated method stub

    }

}
