package de.iani.cubesideutils.bukkit.inventory;

import de.iani.cubesideutils.bukkit.plugin.CubesideUtilsBukkit;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;

public class WindowManager implements Listener {

    private static final WindowManager INSTANCE = new WindowManager();
    private final Map<UUID, Window> openWindows;
    private final CubesideUtilsBukkit core;

    public WindowManager() {
        this.core = CubesideUtilsBukkit.getInstance();
        this.openWindows = new HashMap<>();
        Bukkit.getPluginManager().registerEvents(this, this.core.getPlugin());
    }

    public void registerOpenWindow(Window window) {
        Window alreadyOpen = this.openWindows.put(window.getPlayer().getUniqueId(), window);
        if (alreadyOpen != null) {
            this.core.getPlugin().getLogger().log(Level.WARNING, "Window opened without the last one being closed.");
        }
    }

    @EventHandler
    public void onInventoryClickEvent(InventoryClickEvent event) {
        Window window = this.openWindows.get(event.getWhoClicked().getUniqueId());
        if (window == null) {
            return;
        }

        try {
            window.onItemClicked(event);
        } catch (Exception e) {
            // For security reasons.
            event.setCancelled(true);
            throw e;
        }
    }

    @EventHandler
    public void onInventoryDragEvent(InventoryDragEvent event) {
        Window window = this.openWindows.get(event.getWhoClicked().getUniqueId());
        if (window == null) {
            return;
        }

        try {
            window.onItemDraged(event);
        } catch (Exception e) {
            // For security reasons.
            event.setCancelled(true);
            throw e;
        }
    }

    @EventHandler
    public void onInventoryClosedEvent(InventoryCloseEvent event) {
        Window window = this.openWindows.get(event.getPlayer().getUniqueId());
        if (window == null) {
            return;
        }

        window.closed();
        window.onInventoryClose(event);
        this.openWindows.remove(event.getPlayer().getUniqueId());
    }

    public static WindowManager getInstance() {
        return INSTANCE;
    }
}