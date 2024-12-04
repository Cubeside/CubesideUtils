package de.iani.cubesideutils.velocity.plugin.api;

import com.velocitypowered.api.proxy.Player;
import de.iani.cubesideutils.plugin.api.UtilsApi;
import java.util.UUID;

import de.iani.cubesideutils.velocity.plugin.CubesideUtilsVelocity;

public interface UtilsApiVelocity extends UtilsApi {

    public static UtilsApiVelocity getInstance() {
        return CubesideUtilsVelocity.getInstance();
    }

    public abstract PlayerDataVelocity getPlayerData(Player player);

    public abstract PlayerDataVelocity getPlayerData(UUID playerId);

}
