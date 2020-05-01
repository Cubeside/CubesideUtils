package de.iani.cubesideutils.plugin;

import de.iani.cubesideutils.plugin.UtilsGlobalDataHelper.MessageType;
import de.iani.cubesideutils.plugin.api.GlobalDataHelper;

public interface UtilsGlobalDataHelper extends GlobalDataHelper<MessageType> {
    public static final String GLOBAL_DATA_CHANNEL = "UtilsPlugin";

    public static enum MessageType {
        RANK_INFORMATION_CHANGED,
        GENERAL_DATA_CHANGED,
        PLAYER_DATA_CHANGED,
        SEND_MESSAGE;
    }
}
