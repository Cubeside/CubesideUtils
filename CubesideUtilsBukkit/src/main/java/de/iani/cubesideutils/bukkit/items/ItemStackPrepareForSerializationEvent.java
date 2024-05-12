package de.iani.cubesideutils.bukkit.items;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;

/**
 * Plugins can listen to this if they want prepare some ItemStacks for Serialization. The ItemStack may not be modified by itself but a new ItemStack must be created and set by using setStack().
 */
public class ItemStackPrepareForSerializationEvent extends Event {
    private static final HandlerList handlers = new HandlerList();

    private ItemStack stack;

    public ItemStackPrepareForSerializationEvent(ItemStack stack) {
        this.stack = stack;
    }

    /**
     * The ItemStack the is checked for being serialized. If a plugin wants to serialize a modified the stack a new ItemStack must be created and set by using setStack().
     *
     * <br>
     * <br>
     * <b>Never modify the returned itemstack!</b>
     *
     * @return the old ItemStack
     */
    public ItemStack getStack() {
        return stack;
    }

    public void setStack(ItemStack stack) {
        this.stack = stack;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }
}
