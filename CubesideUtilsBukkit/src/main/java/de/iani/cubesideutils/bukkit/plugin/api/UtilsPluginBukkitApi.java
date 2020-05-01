package de.iani.cubesideutils.bukkit.plugin.api;

import de.iani.cubesideutils.bukkit.plugin.CubesideUtilsBukkit;
import de.iani.cubesideutils.plugin.api.UtilsPluginApi;
import java.util.Set;
import java.util.UUID;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

public interface UtilsPluginBukkitApi extends UtilsPluginApi {

    public static UtilsPluginBukkitApi getInstance() {
        return CubesideUtilsBukkit.getInstance();
    }

    public OnlinePlayerData getPlayerData(Player player);

    public PlayerDataBukkit getPlayerData(OfflinePlayer player);

    public PlayerDataBukkit getPlayerData(UUID playerId);

    public String getWorldDisplayName(LivingEntity entity);

    public String getWorldDisplayName(World world);

    public String getWorldDisplayName(String worldName);

    public Set<String> getWorldDisplayNames(OfflinePlayer player);

    public Set<String> getWorldDisplayNames(UUID playerId);

}