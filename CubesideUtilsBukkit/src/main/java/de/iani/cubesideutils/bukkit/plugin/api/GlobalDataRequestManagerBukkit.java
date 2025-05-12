package de.iani.cubesideutils.bukkit.plugin.api;

import de.cubeside.connection.GlobalServer;
import de.cubeside.connection.util.GlobalLocation;
import de.iani.cubesideutils.Pair;
import de.iani.cubesideutils.adventure.plugin.GlobalDataHelperAdventureImpl;
import de.iani.cubesideutils.plugin.GlobalDataRequestManagerImpl;
import java.io.DataInputStream;
import java.io.IOException;
import org.bukkit.plugin.java.JavaPlugin;

public abstract class GlobalDataRequestManagerBukkit<T extends Enum<T>> extends GlobalDataRequestManagerImpl<T> {

    private static <T extends Enum<T>> Pair<GlobalDataHelperAdventureImpl<T>, Delegator<T>> createHelper(Class<T> messageTypeClass, String channel, JavaPlugin plugin) {
        Delegator<T> delegator = new Delegator<>();
        GlobalDataHelperBukkit<T> helper = new GlobalDataHelperBukkit<>(messageTypeClass, channel, plugin) {

            @Override
            protected void handleMessage(T messageType, GlobalServer source, DataInputStream data) throws IOException {
                delegator.handleMessage(messageType, source, data);
            }

        };

        return new Pair<>(helper, delegator);
    }

    public GlobalDataRequestManagerBukkit(Class<T> messageTypeClass, String channel, JavaPlugin plugin) {
        super(createHelper(messageTypeClass, channel, plugin));
    }

    @Override
    protected GlobalDataHelperBukkit<T> getHelper() {
        return (GlobalDataHelperBukkit<T>) super.getHelper();
    }

    protected GlobalLocation readGlobalLocation(DataInputStream msgin) throws IOException {
        return getHelper().readGlobalLocation(msgin);
    }

}
