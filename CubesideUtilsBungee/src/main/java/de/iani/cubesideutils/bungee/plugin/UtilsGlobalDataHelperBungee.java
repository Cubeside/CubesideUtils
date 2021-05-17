package de.iani.cubesideutils.bungee.plugin;

import de.cubeside.connection.GlobalServer;
import de.iani.cubesideutils.bungee.plugin.api.GlobalDataHelperBungee;
import de.iani.cubesideutils.plugin.PlayerDataImpl;
import de.iani.cubesideutils.plugin.UtilsGlobalDataHelper;
import de.iani.cubesideutils.plugin.UtilsGlobalDataHelper.MessageType;
import java.io.DataInputStream;
import java.io.IOException;

public class UtilsGlobalDataHelperBungee extends GlobalDataHelperBungee<MessageType> implements UtilsGlobalDataHelper {

    public UtilsGlobalDataHelperBungee(UtilsPluginBungee plugin) {
        super(MessageType.class, GLOBAL_DATA_CHANNEL, plugin);
    }

    @Override
    protected void handleMessage(MessageType messageType, GlobalServer source, DataInputStream data) throws IOException {
        switch (messageType) {
            case RANK_INFORMATION_CHANGED:
                CubesideUtilsBungee.getInstance().updateRankInformation();
                break;
            case GENERAL_DATA_CHANGED:
                CubesideUtilsBungee.getInstance().getGeneralDataCache().invalidate(data.readUTF());
                break;
            case PLAYER_DATA_CHANGED:
                CubesideUtilsBungee.getInstance().getPlayerDataCache().invalidate(readUUID(data));
                break;
            case CUSTOM_PLAYER_DATA_CHANGED:
                PlayerDataImpl pData = CubesideUtilsBungee.getInstance().getPlayerDataCache().get(readUUID(data), false, false);
                if (pData != null) {
                    pData.customDataChanged();
                }
                break;
            case SEND_MESSAGE:
                // ignore
                break;
            default:
                break;
        }
    }

}
