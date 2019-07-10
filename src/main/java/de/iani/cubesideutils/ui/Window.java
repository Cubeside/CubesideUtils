package de.iani.cubesideutils.ui;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

public abstract class Window<T extends Inventory> implements InventoryHolder {

    private Set<Player> viewers;
    private Set<Player> unmodifiableViewers;

    Window() {
        this.viewers = new HashSet<>();
    }

    @Override
    public abstract T getInventory();

    public Set<Player> getViewers() {
        if (this.unmodifiableViewers == null) {
            this.unmodifiableViewers = Collections.unmodifiableSet(this.viewers);
        }
        return this.unmodifiableViewers;
    }

    protected void update() {
        for (Player viewer : viewers) {
            viewer.updateInventory();
        }
    }

}
