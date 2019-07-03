package de.iani.cubesideutils.plugin;

import java.util.LinkedHashSet;
import java.util.Set;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

class AfkManager implements Listener {

    private static final int AFK_CHECK_BINS = 50;

    private UtilsPlugin plugin;

    private Set<Player>[] onlinePlayers;
    private int currentTick;

    @SuppressWarnings("unchecked")
    public AfkManager() {
        this.plugin = UtilsPlugin.getInstance();

        this.onlinePlayers = new Set[AFK_CHECK_BINS];
        for (int i = 0; i < AFK_CHECK_BINS; i++) {
            onlinePlayers[i] = new LinkedHashSet<>();
        }

        Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, this::tick, 10, 1);
    }

    public void onPlayerJoinEvent(PlayerJoinEvent event) {
        onlinePlayers[Math.floorMod(event.getPlayer().getUniqueId().hashCode(), AFK_CHECK_BINS)].add(event.getPlayer());
    }

    public void onPlayerQuitEvent(PlayerQuitEvent event) {
        onlinePlayers[Math.floorMod(event.getPlayer().getUniqueId().hashCode(), AFK_CHECK_BINS)].remove(event.getPlayer());
    }

    private void tick() {
        for (Player player : onlinePlayers[currentTick % AFK_CHECK_BINS]) {
            OnlinePlayerData data = plugin.getPlayerData(player);
            data.checkAfk();
        }

        currentTick++;
    }

}
