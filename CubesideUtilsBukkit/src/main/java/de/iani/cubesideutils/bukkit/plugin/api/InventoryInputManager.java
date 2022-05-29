package de.iani.cubesideutils.bukkit.plugin.api;

import java.util.function.Consumer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public interface InventoryInputManager {
    public enum InterruptCause {
        CANCELLED,
        DISCONNECTED,
        OTHER_INVENTORY_CLOSED;
    }

    public abstract void requestInventoryInput(Player player, Consumer<ItemStack[]> callback, Consumer<InterruptCause> interruptHandler, ItemStack[] defaultContent);

    public abstract void cancelInventoryInput(Player player);
}
