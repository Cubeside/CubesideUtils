package de.iani.cubesideutils.bukkit.events;

import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;

/**
 * Called from plugins when a player made an action that should avoid afk state or similar and is not causing a regular bukkit event.
 */
public class PlayerMadeUnknownActionEvent extends PlayerEvent {
    private static final HandlerList handlers = new HandlerList();
    private boolean guaranteedFromPlayer;

    public PlayerMadeUnknownActionEvent(Player player, boolean guaranteedFromPlayer) {
        super(player);
        this.guaranteedFromPlayer = guaranteedFromPlayer;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    /**
     * Checks if the action is somewhat guaranteed to be a manual player action.
     * For example moving is not a guaranteed player action, because the player could be pushed by something.
     * Pressing some
     */
    public boolean isGuaranteedFromPlayer() {
        return guaranteedFromPlayer;
    }
}
