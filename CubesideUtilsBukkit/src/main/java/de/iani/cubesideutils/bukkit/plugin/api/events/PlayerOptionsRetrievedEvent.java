package de.iani.cubesideutils.bukkit.plugin.api.events;

import de.iani.cubesideutils.Pair;
import java.util.Arrays;
import java.util.Objects;
import java.util.TreeSet;
import java.util.stream.Collectors;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
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
    private TreeSet<Pair<Integer, BaseComponent[]>> options;

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

    public void addOptions(int priority, BaseComponent... options) {
        this.options.add(new Pair<>(priority, new BaseComponent[] { new TextComponent(options), new TextComponent(" ") }));
    }

    public BaseComponent[] getOptions() {
        return this.options.stream().map(Pair::second).flatMap(Arrays::stream).collect(Collectors.toList()).toArray(new BaseComponent[0]);
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
