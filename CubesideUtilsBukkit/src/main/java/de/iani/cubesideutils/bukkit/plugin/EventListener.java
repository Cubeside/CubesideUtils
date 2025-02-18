package de.iani.cubesideutils.bukkit.plugin;

import com.destroystokyo.paper.event.inventory.PrepareResultEvent;
import com.google.common.base.Objects;
import de.cubeside.connection.GlobalPlayer;
import de.cubeside.connection.event.GlobalPlayerJoinedEvent;
import de.iani.cubesideutils.bukkit.events.PlayerMadeUnknownActionEvent;
import de.iani.cubesideutils.bukkit.plugin.api.events.PlayerPermissionsChangedEvent;
import de.iani.cubesideutils.plugin.PlayerDataImpl;
import io.papermc.paper.event.player.AsyncChatEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerEditBookEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerLoginEvent.Result;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.bukkit.inventory.AnvilInventory;
import org.bukkit.inventory.view.AnvilView;

public final class EventListener implements Listener {

    private CubesideUtilsBukkit core;

    public EventListener() {
        this.core = CubesideUtilsBukkit.getInstance();
        Bukkit.getPluginManager().registerEvents(this, this.core.getPlugin());
    }

    private void madeAction(Player player) {
        if (!player.isOnline()) {
            return;
        }
        PlayerDataImplBukkit data = core.getPlayerDataCache().get(player.getUniqueId());
        if (!data.isOnlineHere()) {
            return;
        }
        OnlinePlayerDataImpl onlineData = data.getOnlineData();
        if (onlineData == null) {
            return; // events can occure after PlayerQuitEvent
        }
        onlineData.madeAction();
    }

    // Update rank

    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerLoginEvent(PlayerLoginEvent event) {
        if (event.getResult() != Result.ALLOWED) {
            return;
        }
        core.getPlayerData(event.getPlayer()).checkRank();
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerLoginEarly(PlayerLoginEvent event) {
        String hostName = event.getHostname();
        int split = hostName.indexOf('\0');
        if (split >= 0) {
            hostName = hostName.substring(0, split);
        }
        core.getPlayerData(event.getPlayer()).setHostName(hostName);
    }

    @EventHandler
    public void onPlayerPermissionsChanged(PlayerPermissionsChangedEvent event) {
        Player player = Bukkit.getPlayer(event.getPlayerId());
        if (player == null) {
            return;
        }
        core.getPlayerData(player).checkRank();
    }

    // First/last join

    @EventHandler(priority = EventPriority.LOWEST)
    public void onGlobalPlayerJoinedEvent(GlobalPlayerJoinedEvent event) {
        if (!event.hasJustJoinedTheNetwork()) {
            return;
        }

        GlobalPlayer player = event.getPlayer();
        PlayerDataImpl data = core.getPlayerDataCache().get(player.getUniqueId(), true, true);
        if (data.getFirstJoin() == 0) {
            data.setNameAndFirstJoinAndLastJoinAndSeen(System.currentTimeMillis(), player.getName());
        } else {
            data.setNameAndLastJoinAndSeen(System.currentTimeMillis(), player.getName());
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

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerQuitEvent(PlayerQuitEvent event) {
        // On monitor, GlobalPlayer may no longer be available.
        UtilsGlobalDataHelperBukkit globalData = core.getGlobalDataHelper();
        globalData.setPropertyValue(event.getPlayer(), CubesideUtilsBukkit.DISPLAY_NAME_PROPERTY_PREFIX + globalData.getThisServerName(), null);

        core.getPlayerData(event.getPlayer()).setLastSeen(System.currentTimeMillis());
    }

    // AFK

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = false)
    public void onAsynchPlayerChatEvent(AsyncChatEvent event) {
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

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerMadeAnyAction(PlayerMadeUnknownActionEvent event) {
        madeAction(event.getPlayer());
    }

    @EventHandler()
    public void onPlayerInventoryClick(InventoryClickEvent e) {
        if (e.getWhoClicked() instanceof Player player) {
            AnvilGUI anvil = core.getPlayerData(player).getOpenAnvilGUI();
            if (anvil != null && Objects.equal(anvil.getOpenInventory(), e.getView())) {
                e.setCancelled(true);
                if (e.getClickedInventory() instanceof AnvilInventory) {
                    anvil.onInventoryClick(e.getSlot(), e.getCurrentItem(), e.isShiftClick());
                }
            }
        }
    }

    @EventHandler()
    public void onPlayerPrepareResult(PrepareResultEvent e) {
        if (e.getView() instanceof AnvilView) {
            for (HumanEntity viewer : e.getViewers()) {
                if (viewer instanceof Player player) {
                    AnvilGUI anvil = core.getPlayerData(player).getOpenAnvilGUI();
                    if (anvil != null && Objects.equal(anvil.getOpenInventory(), e.getView())) {
                        anvil.onUpdateAnvil(e);
                    }
                }
            }
        }
    }

    @EventHandler()
    public void onPlayerCloseInventory(InventoryCloseEvent e) {
        if (e.getView() instanceof AnvilView && e.getPlayer() instanceof Player player) {
            AnvilGUI anvil = core.getPlayerData(player).getOpenAnvilGUI();
            if (anvil != null && Objects.equal(anvil.getOpenInventory(), e.getView())) {
                anvil.onInventoryClose();
            }
        }
    }
}
