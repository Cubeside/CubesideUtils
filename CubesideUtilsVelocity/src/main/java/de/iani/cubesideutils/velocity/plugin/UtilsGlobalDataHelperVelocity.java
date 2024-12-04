package de.iani.cubesideutils.velocity.plugin;

import de.cubeside.connection.GlobalServer;
import de.iani.cubesideutils.plugin.PlayerDataImpl;
import de.iani.cubesideutils.plugin.UtilsGlobalDataHelper;
import de.iani.cubesideutils.plugin.UtilsGlobalDataHelper.MessageType;
import de.iani.cubesideutils.velocity.plugin.api.GlobalDataHelperVelocity;

import java.io.DataInputStream;
import java.io.IOException;

public class UtilsGlobalDataHelperVelocity extends GlobalDataHelperVelocity<MessageType> implements UtilsGlobalDataHelper {

    public UtilsGlobalDataHelperVelocity(CubesideUtilsVelocity plugin) {
        super(MessageType.class, GLOBAL_DATA_CHANNEL, plugin);
    }

    @Override
    protected void handleMessage(MessageType messageType, GlobalServer source, DataInputStream data) throws IOException {
        switch (messageType) {
            case RANK_INFORMATION_CHANGED:
                CubesideUtilsVelocity.getInstance().updateRankInformation();
                break;
            case GENERAL_DATA_CHANGED:
                CubesideUtilsVelocity.getInstance().getGeneralDataCache().invalidate(data.readUTF());
                break;
            case PLAYER_DATA_CHANGED:
                CubesideUtilsVelocity.getInstance().getPlayerDataCache().invalidate(readUUID(data));
                break;
            case CUSTOM_PLAYER_DATA_CHANGED:
                PlayerDataImpl pData = CubesideUtilsVelocity.getInstance().getPlayerDataCache().get(readUUID(data), false, false);
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
