package de.iani.cubesideutils.bukkit.plugin;

import java.util.LinkedHashSet;
import java.util.Set;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class AfkManager implements Listener {

    private static final int AFK_CHECK_BINS = 50;

    private CubesideUtilsBukkit core;

    private Set<Player>[] onlinePlayers;
    private int currentTick;

    public static final long AFK_THRESHOLD = 10L * 60L * 1000L;

    @SuppressWarnings("unchecked")
    public AfkManager() {
        this.core = CubesideUtilsBukkit.getInstance();
        Bukkit.getPluginManager().registerEvents(this, this.core.getPlugin());

        this.onlinePlayers = new Set[AFK_CHECK_BINS];
        for (int i = 0; i < AFK_CHECK_BINS; i++) {
            onlinePlayers[i] = new LinkedHashSet<>();
        }

        Bukkit.getScheduler().scheduleSyncRepeatingTask(this.core.getPlugin(), this::tick, 10, 1);
    }

    @EventHandler
    public void onPlayerJoinEvent(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        onlinePlayers[Math.floorMod(event.getPlayer().getUniqueId().hashCode(), AFK_CHECK_BINS)].add(player);
        core.getPlayerData(player).checkAfk(false);
    }

    @EventHandler
    public void onPlayerQuitEvent(PlayerQuitEvent event) {
        onlinePlayers[Math.floorMod(event.getPlayer().getUniqueId().hashCode(), AFK_CHECK_BINS)].remove(event.getPlayer());
    }

    private void tick() {
        for (Player player : onlinePlayers[currentTick % AFK_CHECK_BINS]) {
            OnlinePlayerDataImpl data = (core.getPlayerData(player));
            data.checkAfk(true);
        }

        currentTick++;
    }

}
