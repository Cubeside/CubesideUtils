package de.iani.cubesideutils.bukkit.inventory;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;

public interface Window {

    Player getPlayer();

    Inventory getInventory();

    Window getParent();

    InventoryView getView();

    void open();

    default void closed() {

    }

    default void onItemClicked(InventoryClickEvent event) {
        event.setCancelled(true);
    }

    default void onItemDraged(InventoryDragEvent event) {
        event.setCancelled(true);
    }

    default void onInventoryClose(InventoryCloseEvent event) {
    }

}