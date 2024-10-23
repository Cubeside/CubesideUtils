package de.iani.cubesideutils.velocity.plugin.api;

import com.velocitypowered.api.event.Subscribe;
import de.cubeside.connection.event.GlobalDataEvent;
import de.iani.cubesideutils.plugin.GlobalDataHelperImpl;
import java.io.DataInputStream;
import java.io.IOException;
import java.util.logging.Level;

import de.iani.cubesideutils.velocity.plugin.CubesideUtilsVelocity;

public abstract class GlobalDataHelperVelocity<T extends Enum<T>> extends GlobalDataHelperImpl<T> {
    private final CubesideUtilsVelocity plugin;

    public GlobalDataHelperVelocity(Class<T> messageTypeClass, String channel, CubesideUtilsVelocity plugin) {
        super(messageTypeClass, channel);
        this.plugin = plugin;

        plugin.getServer().getEventManager().register(plugin, this);
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
            plugin.getLogger().log(Level.WARNING, "Unknown data type for DataHelper " + getMessageTypeClass().getName() + ": " + messageTypeId);
        } else {
            handleMessage(messageType, event.getSource(), data);
        }
    }
}
