package de.iani.cubesideutils.bungee.plugin.api;

import de.iani.cubesideutils.bungee.plugin.CubesideUtilsBungee;
import de.iani.cubesideutils.plugin.api.UtilsApi;
import java.util.UUID;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public interface UtilsApiBungee extends UtilsApi {

    public static UtilsApiBungee getInstance() {
        return CubesideUtilsBungee.getInstance();
    }

    public abstract PlayerDataBungee getPlayerData(ProxiedPlayer player);

    public abstract PlayerDataBungee getPlayerData(UUID playerId);

}
