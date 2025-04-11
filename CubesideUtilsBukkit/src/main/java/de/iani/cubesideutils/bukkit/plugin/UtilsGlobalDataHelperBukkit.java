package de.iani.cubesideutils.bukkit.plugin;

import de.cubeside.connection.GlobalServer;
import de.iani.cubesideutils.bukkit.ChatUtilBukkit;
import de.iani.cubesideutils.bukkit.plugin.api.GlobalDataHelperBukkit;
import de.iani.cubesideutils.bukkit.sound.SoundSequence;
import de.iani.cubesideutils.conditions.Condition;
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
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;

public class UtilsGlobalDataHelperBukkit extends GlobalDataHelperBukkit<MessageType> implements UtilsGlobalDataHelper {

    private Map<MessageType, Set<BiConsumer<GlobalServer, DataInputStream>>> handlers;

    public UtilsGlobalDataHelperBukkit(UtilsPluginBukkit plugin) {
        super(MessageType.class, GLOBAL_DATA_CHANNEL, plugin);

        this.handlers = new EnumMap<>(MessageType.class);
    }

    @Override
    public void registerHandler(MessageType type, BiConsumer<GlobalServer, DataInputStream> consumer) {
        handlers.computeIfAbsent(type, t -> new LinkedHashSet<>()).add((source, data) -> {
            try {
                consumer.accept(source, data);
            } catch (Exception | StackOverflowError e) {
                CubesideUtils.getInstance().getLogger().log(Level.SEVERE, "Exception trying to call handler for MessageType " + type, e);
            }
        });
    }

    @Override
    protected void handleMessage(MessageType messageType, GlobalServer source, DataInputStream data) throws IOException {
        this.handlers.getOrDefault(messageType, Set.of()).forEach(handler -> handler.accept(source, data));

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
            case SEND_MESSAGE: {
                Condition<? super Player> seeMsgCondition = readStringSerializable(data);
                boolean componentMsg = data.readBoolean();
                if (componentMsg) {
                    Component message = readAdventureComponent(data);
                    ChatUtilBukkit.sendMessageToPlayers(seeMsgCondition, message);
                } else {
                    String message = data.readUTF();
                    ChatUtilBukkit.sendMessageToPlayers(seeMsgCondition, message);
                }
                break;
            }
            case SOUND_SEQUENCE: {
                Condition<? super Player> hearSoundCondition = readStringSerializable(data);
                SoundSequence sequence = readStringSerializable(data);
                sequence.playToAll(hearSoundCondition, plugin);
                break;
            }
            default:
                break;
        }
    }

}
