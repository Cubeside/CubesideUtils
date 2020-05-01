package de.iani.cubesideutils.bukkit.plugin;

import de.iani.cubesideutils.bukkit.BukkitChatUtil;
import de.iani.cubesideutils.bukkit.plugin.api.BukkitGlobalDataHelper;
import de.iani.cubesideutils.plugin.UtilsGlobalDataHelper;
import de.iani.cubesideutils.plugin.UtilsGlobalDataHelper.MessageType;
import java.io.DataInputStream;
import java.io.IOException;

public class UtilsGlobalDataHelperBukkit extends BukkitGlobalDataHelper<MessageType> implements UtilsGlobalDataHelper {

    public UtilsGlobalDataHelperBukkit(UtilsPluginBukkit plugin) {
        super(MessageType.class, GLOBAL_DATA_CHANNEL, plugin);
    }

    @Override
    protected void handleMessage(MessageType messageType, DataInputStream data) throws IOException {
        switch (messageType) {
            case RANK_INFORMATION_CHANGED:
                CubesideUtilsBukkit.getInstance().updateRankInformation();
                break;
            case GENERAL_DATA_CHANGED:
                CubesideUtilsBukkit.getInstance().getGeneralDataCache().invalidate(data.readUTF());
                break;
            case PLAYER_DATA_CHANGED:
                CubesideUtilsBukkit.getInstance().getPlayerDataCache().invalidate(readUUID(data));
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
