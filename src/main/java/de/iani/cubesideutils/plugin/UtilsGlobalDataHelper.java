package de.iani.cubesideutils.plugin;

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
            case PLAYER_DATA_CHANGED:
                UtilsPlugin.getInstance().getPlayerDataCache().invalidate(readUUID(data));
                break;
            default:
                break;
        }
    }

}
