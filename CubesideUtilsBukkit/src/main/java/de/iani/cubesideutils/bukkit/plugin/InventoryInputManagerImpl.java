package de.iani.cubesideutils.bukkit.plugin;

import de.iani.cubesideutils.bukkit.plugin.api.InventoryInputManager;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class InventoryInputManagerImpl implements InventoryInputManager, Listener {

    private static record InventoryInputData(Consumer<ItemStack[]> callback, Consumer<InterruptCause> interruptHandler, Inventory inventory) {}

    private CubesideUtilsBukkit core;

    private Map<Player, InventoryInputData> pendingInputs;

    public InventoryInputManagerImpl() {
        this.core = CubesideUtilsBukkit.getInstance();
        this.pendingInputs = new HashMap<>();
        Bukkit.getPluginManager().registerEvents(this, this.core.getPlugin());
    }

    @Override
    public void requestInventoryInput(Player player, Consumer<ItemStack[]> callback, Consumer<InterruptCause> interruptHandler, ItemStack[] defaultContent) {
        Inventory inventory = Bukkit.createInventory(player, defaultContent.length);
        pendingInputs.put(player, new InventoryInputData(callback, interruptHandler, inventory));
        player.openInventory(inventory);
    }

    @Override
    public void cancelInventoryInput(Player player) {
        InventoryInputData data = pendingInputs.remove(player);
        if (player == null) {
            return; // Exception?
        }

        data.interruptHandler().accept(InterruptCause.CANCELLED);
    }

    @EventHandler
    public void onInventoryCloseEvent(InventoryCloseEvent event) {
        InventoryInputData data = pendingInputs.remove(event.getPlayer());
        if (data == null) {
            return;
        }

        if (!data.inventory().equals(event.getView().getTopInventory())) {
            data.interruptHandler.accept(InterruptCause.OTHER_INVENTORY_CLOSED);
            return;
        }

        data.callback.accept(data.inventory().getContents());
    }

    @EventHandler
    public void onPlayerQuitEvent(PlayerQuitEvent event) {
        InventoryInputData data = pendingInputs.remove(event.getPlayer());
        if (data == null) {
            return;
        }

        data.interruptHandler().accept(InterruptCause.DISCONNECTED);
    }

}
