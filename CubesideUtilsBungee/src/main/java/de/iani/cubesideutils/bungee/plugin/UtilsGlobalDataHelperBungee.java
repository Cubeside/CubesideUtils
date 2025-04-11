package de.iani.cubesideutils.bungee.plugin;

import de.cubeside.connection.GlobalServer;
import de.iani.cubesideutils.bungee.plugin.api.GlobalDataHelperBungee;
import de.iani.cubesideutils.plugin.CubesideUtils;
import de.iani.cubesideutils.plugin.PlayerDataImpl;
import de.iani.cubesideutils.plugin.UtilsGlobalDataHelper;
import de.iani.cubesideutils.plugin.UtilsGlobalDataHelper.MessageType;
import java.io.DataInputStream;
import java.io.IOException;
import java.util.EnumMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.logging.Level;

public class UtilsGlobalDataHelperBungee extends GlobalDataHelperBungee<MessageType> implements UtilsGlobalDataHelper {

    private Map<MessageType, Set<BiConsumer<GlobalServer, DataInputStream>>> handlers;

    public UtilsGlobalDataHelperBungee(UtilsPluginBungee plugin) {
        super(MessageType.class, GLOBAL_DATA_CHANNEL, plugin);

        this.handlers = new EnumMap<>(MessageType.class);
    }

    @Override
    public void registerHandler(MessageType type, BiConsumer<GlobalServer, DataInputStream> consumer) {
        this.handlers.computeIfAbsent(type, t -> new LinkedHashSet<>()).add((source, data) -> {
            try {
                consumer.accept(source, data);
            } catch (Exception | StackOverflowError e) {
                CubesideUtils.getInstance().getLogger().log(Level.SEVERE, "Exception trying to call handler for MessageType " + type, e);
            }
        });
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
