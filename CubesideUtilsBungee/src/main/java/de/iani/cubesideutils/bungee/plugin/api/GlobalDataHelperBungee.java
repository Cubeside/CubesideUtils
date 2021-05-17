package de.iani.cubesideutils.bungee.plugin.api;

import de.cubeside.connection.event.GlobalDataEvent;
import de.iani.cubesideutils.plugin.GlobalDataHelperImpl;
import java.io.DataInputStream;
import java.io.IOException;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.event.EventHandler;

public abstract class GlobalDataHelperBungee<T extends Enum<T>> extends GlobalDataHelperImpl<T> implements Listener {

    public GlobalDataHelperBungee(Class<T> messageTypeClass, String channel, Plugin plugin) {
        super(messageTypeClass, channel);

        ProxyServer.getInstance().getPluginManager().registerListener(plugin, this);
    }

    @EventHandler
    public void onGlobalDataEvent(GlobalDataEvent event) throws IOException {
        if (!event.getChannel().equals(getChannel())) {
            return;
        }

        DataInputStream data = new DataInputStream(event.getData());
        T messageType = fromOrdinal(data.readInt());
        handleMessage(messageType, event.getSource(), data);
    }

}
