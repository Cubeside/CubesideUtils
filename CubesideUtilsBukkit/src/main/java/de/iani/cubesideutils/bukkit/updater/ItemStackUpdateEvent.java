package de.iani.cubesideutils.bukkit.updater;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;

/**
 * Plugins can listen to this if they may want to update ItemStacks. If the ItemStrack is modified setChanged must be called.
 */
public class ItemStackUpdateEvent extends Event {
    private static final HandlerList handlers = new HandlerList();

    private ItemStack stack;
    private boolean changed;

    public ItemStackUpdateEvent(ItemStack stack) {
        this.stack = stack;
    }

    public ItemStack getStack() {
        return stack;
    }

    public void setStack(ItemStack stack) {
        this.stack = stack;
        this.setChanged();
    }

    public boolean isChanged() {
        return changed;
    }

    public void setChanged() {
        this.changed = true;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }
}
