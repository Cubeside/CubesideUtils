package de.iani.cubesideutils.bukkit.plugin;

import de.cubeside.connection.event.GlobalPlayerJoinedEvent;
import de.iani.cubesideutils.plugin.api.PlayerData;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerEditBookEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;

public class EventListener implements Listener {

    private CubesideUtilsBukkit core;

    public EventListener() {
        this.core = CubesideUtilsBukkit.getInstance();
        Bukkit.getPluginManager().registerEvents(this, this.core.getPlugin());
    }

    private void madeAction(Player player) {
        PlayerDataImplBukkit data = core.getPlayerDataCache().get(player.getUniqueId());
        ((OnlinePlayerDataImpl) data.getOnlineData()).madeAction();
    }

    // First/last join

    @EventHandler(priority = EventPriority.LOWEST)
    public void onGlobalPlayerJoinedEvent(GlobalPlayerJoinedEvent event) {
        if (!event.hasJustJoinedTheNetwork()) {
            return;
        }

        PlayerData data = core.getPlayerDataCache().get(event.getPlayer().getUniqueId(), true);
        if (data.getFirstJoin() == 0) {
            data.setFirstJoinAndLastJoinAndSeen(System.currentTimeMillis());
        } else {
            data.setLastJoinAndSeen(System.currentTimeMillis());
        }
    }

    // WorldDisplayName

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerJoinEvent(PlayerJoinEvent event) {
        UtilsGlobalDataHelperBukkit globalData = core.getGlobalDataHelper();
        globalData.setPropertyValue(event.getPlayer(), CubesideUtilsBukkit.DISPLAY_NAME_PROPERTY_PREFIX + globalData.getThisServerName(), core.getWorldDisplayName(event.getPlayer()));
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerChangedWorldEvent(PlayerChangedWorldEvent event) {
        UtilsGlobalDataHelperBukkit globalData = core.getGlobalDataHelper();
        globalData.setPropertyValue(event.getPlayer(), CubesideUtilsBukkit.DISPLAY_NAME_PROPERTY_PREFIX + globalData.getThisServerName(), core.getWorldDisplayName(event.getPlayer()));
    }

    // On monitor, GlobalPlayer may no longer be available.
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerQuitEvent(PlayerQuitEvent event) {
        UtilsGlobalDataHelperBukkit globalData = core.getGlobalDataHelper();
        globalData.setPropertyValue(event.getPlayer(), CubesideUtilsBukkit.DISPLAY_NAME_PROPERTY_PREFIX + globalData.getThisServerName(), null);
    }

    // AFK

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = false)
    public void onAsynchPlayerChatEvent(AsyncPlayerChatEvent event) {
        madeAction(event.getPlayer());
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = false)
    public void onPlayerCommandPreprocessEvent(PlayerCommandPreprocessEvent event) {
        madeAction(event.getPlayer());
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = false)
    public void PlayerPlayerDropItemEvent(PlayerDropItemEvent event) {
        madeAction(event.getPlayer());
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = false)
    public void onPlayerEditBookEvent(PlayerEditBookEvent event) {
        madeAction(event.getPlayer());
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = false)
    public void onPlayerInteractEvent(PlayerInteractEvent event) {
        madeAction(event.getPlayer());
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = false)
    public void onPlayerInteractEntityEvent(PlayerInteractEntityEvent event) {
        madeAction(event.getPlayer());
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = false)
    public void onPlayerInteractAtEntityEvent(PlayerInteractAtEntityEvent event) {
        madeAction(event.getPlayer());
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = false)
    public void onPlayerItemHeldEvent(PlayerItemHeldEvent event) {
        madeAction(event.getPlayer());
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = false)
    public void onPlayerMoveEvent(PlayerMoveEvent event) {
        madeAction(event.getPlayer());
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = false)
    public void onPlayerSwapHandItemsEvent(PlayerSwapHandItemsEvent event) {
        madeAction(event.getPlayer());
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = false)
    public void onInventoryClickEvent(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player)) {
            return;
        }
        madeAction((Player) event.getWhoClicked());
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = false)
    public void onInventoryCloseEvent(InventoryCloseEvent event) {
        if (!(event.getPlayer() instanceof Player)) {
            return;
        }
        madeAction((Player) event.getPlayer());
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = false)
    public void onInventoryOpenEvent(InventoryOpenEvent event) {
        if (!(event.getPlayer() instanceof Player)) {
            return;
        }
        madeAction((Player) event.getPlayer());
    }

}
