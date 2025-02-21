package de.iani.cubesideutils.bukkit.plugin.api.events;

import de.iani.cubesideutils.Pair;
import java.util.Objects;
import java.util.TreeSet;
import net.kyori.adventure.text.Component;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class PlayerOptionsRetrievedEvent extends Event implements Cancellable {

    private static final HandlerList handlers = new HandlerList();

    public static HandlerList getHandlerList() {
        return handlers;
    }

    private CommandSender sender;
    private OfflinePlayer player;

    private boolean cancelled;
    private TreeSet<Pair<Integer, Component>> options;

    public PlayerOptionsRetrievedEvent(CommandSender sender, OfflinePlayer playerData) {
        this.sender = Objects.requireNonNull(sender);
        this.player = Objects.requireNonNull(playerData);

        this.cancelled = false;
        this.options = new TreeSet<>((p1, p2) -> Integer.compare(p1.first, p2.first));
    }

    public CommandSender getSender() {
        return this.sender;
    }

    public OfflinePlayer getPlayer() {
        return this.player;
    }

    public void addOptions(int priority, Component options) {
        this.options.add(new Pair<>(priority, options));
    }

    public Component getOptions() {
        Component main = Component.empty();
        for (Pair<Integer, Component> option : options) {
            if (!main.children().isEmpty()) {
                main = main.append(Component.space());
            }
            main = main.append(option.second);
        }
        return main;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

}
