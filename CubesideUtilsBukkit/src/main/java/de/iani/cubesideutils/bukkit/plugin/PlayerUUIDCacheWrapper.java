package de.iani.cubesideutils.bukkit.plugin;

import de.iani.playerUUIDCache.PlayerUUIDCache;
import java.util.UUID;
import org.bukkit.OfflinePlayer;
import org.bukkit.plugin.Plugin;

public class PlayerUUIDCacheWrapper {
    private PlayerUUIDCache playerUUIDCache;

    public PlayerUUIDCacheWrapper(Plugin plugin) {
        playerUUIDCache = (PlayerUUIDCache) (plugin.getServer().getPluginManager().getPlugin("PlayerUUIDCache"));
    }

    public OfflinePlayer getPlayer(String name) {
        return playerUUIDCache.getPlayer(name);
    }

    public OfflinePlayer getPlayer(UUID uuid) {
        return playerUUIDCache.getPlayer(uuid);
    }
}
