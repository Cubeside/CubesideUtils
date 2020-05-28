package de.iani.cubesideutils.bukkit.plugin;

import de.iani.cubesideutils.bukkit.ChatUtilBukkit;
import de.iani.cubesideutils.bukkit.plugin.api.GlobalDataHelperBukkit;
import de.iani.cubesideutils.plugin.PlayerDataImpl;
import de.iani.cubesideutils.plugin.UtilsGlobalDataHelper;
import de.iani.cubesideutils.plugin.UtilsGlobalDataHelper.MessageType;
import java.io.DataInputStream;
import java.io.IOException;

public class UtilsGlobalDataHelperBukkit extends GlobalDataHelperBukkit<MessageType> implements UtilsGlobalDataHelper {

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
            case CUSTOM_PLAYER_DATA_CHANGED:
                PlayerDataImpl pData = CubesideUtilsBukkit.getInstance().getPlayerDataCache().get(readUUID(data), false, false);
                if (pData != null) {
                    pData.customDataChanged();
                }
                break;
            case SEND_MESSAGE:
                String permission = data.readUTF();
                String message = data.readUTF();
                ChatUtilBukkit.sendMessageToPlayers(permission, message);
                break;
            default:
                break;
        }
    }

}
