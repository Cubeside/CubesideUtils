package de.iani.cubesideutils.bukkit.plugin;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;

public class PlayerReconfigurationPhaseHelper implements Listener {

    private Map<UUID, List<Consumer<? super Player>>> delayedActionsForPlayersInReconfigurationPhase;

    public PlayerReconfigurationPhaseHelper() {
        this.delayedActionsForPlayersInReconfigurationPhase = new ConcurrentHashMap<>();
        Bukkit.getPluginManager().registerEvents(this, CubesideUtilsBukkit.getInstance().getPlugin());
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void playerStartsLoggingIn(AsyncPlayerPreLoginEvent event) {
        delayedActionsForPlayersInReconfigurationPhase.put(event.getUniqueId(), Collections.synchronizedList(new ArrayList<>()));
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void playerFinishesLoggingIn(PlayerLoginEvent event) {
        if (event.getResult() != PlayerLoginEvent.Result.ALLOWED) {
            delayedActionsForPlayersInReconfigurationPhase.remove(event.getPlayer().getUniqueId());
        }
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void playerJoins(PlayerJoinEvent event) {
        Bukkit.getScheduler().runTaskLater(CubesideUtilsBukkit.getInstance().getPlugin(), () -> runDelayedActions(event.getPlayer()), 1);
    }

    private void runDelayedActions(Player player) {
        List<Consumer<? super Player>> delayedActions = delayedActionsForPlayersInReconfigurationPhase.remove(player.getUniqueId());
        if (!player.isOnline()) {
            return;
        }

        for (Consumer<? super Player> action : delayedActions) {
            action.accept(player);
        }
    }

    public void doActions(Player player, List<Consumer<? super Player>> actions) {
        List<Consumer<? super Player>> delayed = delayedActionsForPlayersInReconfigurationPhase.get(player.getUniqueId());
        if (delayed == null && player.isOnline()) {
            actions.forEach(a -> a.accept(player));
        } else if (delayed != null) {
            delayed.addAll(actions);
        }
    }

    public void doAction(Player player, Consumer<? super Player> action) {
        doActions(player, List.of(action));
    }

}
