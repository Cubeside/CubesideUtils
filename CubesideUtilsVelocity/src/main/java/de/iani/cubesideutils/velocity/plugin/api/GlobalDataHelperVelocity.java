package de.iani.cubesideutils.velocity.plugin.api;

import com.velocitypowered.api.event.Subscribe;
import de.cubeside.connection.event.GlobalDataEvent;
import de.iani.cubesideutils.velocity.plugin.CubesideUtilsVelocity;
import de.iani.cubesideutils.velocity.plugin.GlobalDataHelperImpl;
import java.io.DataInputStream;
import java.io.IOException;
import java.util.function.Consumer;
import org.slf4j.Logger;

public abstract class GlobalDataHelperVelocity<T extends Enum<T>> extends GlobalDataHelperImpl<T> {

    private final Consumer<String> warningLogger;

    private GlobalDataHelperVelocity(Class<T> messageTypeClass, String channel, Object plugin,
            Consumer<String> logger) {
        super(messageTypeClass, channel);
        this.warningLogger = logger;

        CubesideUtilsVelocity.getInstance().getServer().getEventManager().register(plugin, this);
    }

    public GlobalDataHelperVelocity(Class<T> messageTypeClass, String channel, Object plugin, Logger logger) {
        this(messageTypeClass, channel, plugin, logger::warn);
    }

    public GlobalDataHelperVelocity(Class<T> messageTypeClass, String channel, Object plugin,
            java.util.logging.Logger logger) {
        this(messageTypeClass, channel, plugin, s -> logger.warning(s));
    }

    @Subscribe
    public void onGlobalDataEvent(GlobalDataEvent event) throws IOException {
        if (!event.getChannel().equals(getChannel())) {
            return;
        }

        DataInputStream data = new DataInputStream(event.getData());
        int messageTypeId = data.readInt();
        T messageType = fromOrdinal(messageTypeId);
        if (messageType == null) {
            this.warningLogger.accept(
                    "Unknown data type for DataHelper " + getMessageTypeClass().getName() + ": " + messageTypeId);
        } else {
            handleMessage(messageType, event.getSource(), data);
        }
    }
}
