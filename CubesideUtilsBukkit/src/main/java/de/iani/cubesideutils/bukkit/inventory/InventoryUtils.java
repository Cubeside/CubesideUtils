package de.iani.cubesideutils.bukkit.inventory;

import java.util.Arrays;
import java.util.Map;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class InventoryUtils {

    public static boolean canPlayerInventoryFitItems(Player player, ItemStack[] items) {
        ItemStack[] playerInv = player.getInventory().getContents();
        playerInv = Arrays.copyOf(playerInv, 36);
        Inventory clonedPlayerInventory = Bukkit.createInventory(null, 36);
        clonedPlayerInventory.setContents(playerInv);
        Map<Integer, ItemStack> leftover = clonedPlayerInventory.addItem(items);
        return leftover.isEmpty();
    }
}
