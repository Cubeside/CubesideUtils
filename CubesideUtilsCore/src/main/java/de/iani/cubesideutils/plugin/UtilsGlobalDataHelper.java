package de.iani.cubesideutils.plugin;

import de.cubeside.connection.GlobalServer;
import de.iani.cubesideutils.plugin.UtilsGlobalDataHelper.MessageType;
import de.iani.cubesideutils.plugin.api.GlobalDataHelper;
import java.io.DataInputStream;
import java.util.function.BiConsumer;

public interface UtilsGlobalDataHelper extends GlobalDataHelper<MessageType> {
    public static final String GLOBAL_DATA_CHANNEL = "UtilsPlugin";

    public static enum MessageType {
        RANK_INFORMATION_CHANGED,
        GENERAL_DATA_CHANGED,
        PLAYER_DATA_CHANGED,
        CUSTOM_PLAYER_DATA_CHANGED,
        SEND_MESSAGE,
        SOUND_SEQUENCE,
        OTP_CHANGED;
    }

    void registerHandler(MessageType type, BiConsumer<GlobalServer, DataInputStream> consumer);
}
