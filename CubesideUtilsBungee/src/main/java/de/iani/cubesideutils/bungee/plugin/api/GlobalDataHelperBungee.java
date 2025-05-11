package de.iani.cubesideutils.bungee.plugin.api;

import de.cubeside.connection.event.GlobalDataEvent;
import de.iani.cubesideutils.adventure.plugin.GlobalDataHelperImpl;
import java.io.DataInputStream;
import java.io.IOException;
import java.util.logging.Level;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.event.EventHandler;

public abstract class GlobalDataHelperBungee<T extends Enum<T>> extends GlobalDataHelperImpl<T> implements Listener {
    private final Plugin plugin;

    public GlobalDataHelperBungee(Class<T> messageTypeClass, String channel, Plugin plugin) {
        super(messageTypeClass, channel);
        this.plugin = plugin;

        ProxyServer.getInstance().getPluginManager().registerListener(plugin, this);
    }

    @EventHandler
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
