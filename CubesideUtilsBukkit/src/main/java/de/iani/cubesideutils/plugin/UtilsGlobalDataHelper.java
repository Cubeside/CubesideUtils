package de.iani.cubesideutils.plugin;

import de.iani.cubesideutils.bukkit.BukkitChatUtil;
import java.io.DataInputStream;
import java.io.IOException;

class UtilsGlobalDataHelper extends GlobalDataHelper<MessageType> {

    public static final String GLOBAL_DATA_CHANNEL = "UtilsPlugin";

    UtilsGlobalDataHelper(UtilsPlugin plugin) {
        super(MessageType.class, GLOBAL_DATA_CHANNEL, plugin);
    }

    @Override
    protected void handleMessage(MessageType messageType, DataInputStream data) throws IOException {
        switch (messageType) {
            case RANK_INFORMATION_CHANGED:
                UtilsPlugin.getInstance().updateRankInformation();
                break;
            case GENERAL_DATA_CHANGED:
                UtilsPlugin.getInstance().getGeneralDataCache().invalidate(data.readUTF());
                break;
            case PLAYER_DATA_CHANGED:
                UtilsPlugin.getInstance().getPlayerDataCache().invalidate(readUUID(data));
                break;
            case SEND_MESSAGE:
                String permission = data.readUTF();
                String message = data.readUTF();
                BukkitChatUtil.sendMessageToPlayers(permission, message);
                break;
            default:
                break;
        }
    }

}
