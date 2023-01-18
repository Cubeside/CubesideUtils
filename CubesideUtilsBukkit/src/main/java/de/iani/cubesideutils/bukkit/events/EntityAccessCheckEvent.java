package de.iani.cubesideutils.bukkit.events;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.bukkit.entity.Entity;
import org.bukkit.event.HandlerList;
import org.bukkit.event.entity.EntityEvent;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

public class EntityAccessCheckEvent extends EntityEvent {
    private static final HandlerList handlers = new HandlerList();

    private final Plugin caller;
    private List<Plugin> entityOwningPlugins;
    private List<Plugin> unmodifiableEntityOwningPlugins;

    public EntityAccessCheckEvent(@NotNull Entity entity, @NotNull Plugin caller) {
        super(entity);
        this.caller = caller;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public @NotNull Plugin getCaller() {
        return caller;
    }

    public boolean isProtected() {
        return entityOwningPlugins != null && !entityOwningPlugins.isEmpty();
    }

    public void addProtectingPlugin(Plugin plugin) {
        if (entityOwningPlugins == null) {
            entityOwningPlugins = new ArrayList<>();
        }
        entityOwningPlugins.add(plugin);
    }

    public @NotNull List<Plugin> getProtectingPlugins() {
        if (entityOwningPlugins == null || entityOwningPlugins.isEmpty()) {
            return List.of();
        }
        if (unmodifiableEntityOwningPlugins == null) {
            unmodifiableEntityOwningPlugins = Collections.unmodifiableList(entityOwningPlugins);
        }
        return unmodifiableEntityOwningPlugins;
    }
}
