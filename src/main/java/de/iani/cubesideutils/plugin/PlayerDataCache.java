package de.iani.cubesideutils.plugin;

import java.util.UUID;
import org.bukkit.OfflinePlayer;

public class PlayerDataCache {

    public PlayerData getData(UUID playerId) {
        // TODO;
        return null;
    }

    public PlayerData getData(OfflinePlayer player) {
        return getData(player.getUniqueId());
    }

}
