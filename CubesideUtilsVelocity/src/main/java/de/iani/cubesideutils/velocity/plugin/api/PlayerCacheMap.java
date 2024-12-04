package de.iani.cubesideutils.velocity.plugin.api;

import com.velocitypowered.api.event.PostOrder;
import com.velocitypowered.api.event.ResultedEvent;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.DisconnectEvent;
import com.velocitypowered.api.event.connection.LoginEvent;
import com.velocitypowered.api.proxy.Player;
import de.iani.cubesideutils.collections.AdvancedCacheMap;
import de.iani.cubesideutils.plugin.CubesideUtils;
import java.util.Objects;
import java.util.UUID;
import java.util.logging.Level;

import de.iani.cubesideutils.velocity.plugin.CubesideUtilsVelocity;
import net.kyori.adventure.text.Component;

public abstract class PlayerCacheMap<V, D> extends AdvancedCacheMap<UUID, V, D> {

    public static final long BETWEEN_LOGIN_AND_JOIN_TIMEOUT = 5 * 60 * 20L; // 5 minutes in ticks

    private static final long serialVersionUID = 9162727527421482928L;

    public static class LoadingPlayerDataFailedException extends Exception {

        private static final long serialVersionUID = 4824440510991755719L;

        private String kickMessage;

        public LoadingPlayerDataFailedException(String kickMessage) {
            super();
            this.kickMessage = Objects.requireNonNull(kickMessage);
        }

        public LoadingPlayerDataFailedException(String kickMessage, String message, Throwable cause) {
            super(message, cause);
            this.kickMessage = Objects.requireNonNull(kickMessage);
        }

        public LoadingPlayerDataFailedException(String kickMessage, String message) {
            super(message);
            this.kickMessage = Objects.requireNonNull(kickMessage);
        }

        public LoadingPlayerDataFailedException(String kickMessage, Throwable cause) {
            super(cause);
            this.kickMessage = Objects.requireNonNull(kickMessage);
        }

        public String getKickMessage() {
            return kickMessage;
        }

    }

    private String valueLoggingName;

    protected PlayerCacheMap(int maxSoftCacheSize, D defaultData, String valueLoggingName) {
        super(maxSoftCacheSize, defaultData);

        this.valueLoggingName = Objects.requireNonNull(valueLoggingName);
        CubesideUtilsVelocity.getInstance().getServer().getEventManager().register(CubesideUtilsVelocity.getInstance().getPlugin(), this);
    }

    @Subscribe(order = PostOrder.LAST)
    public void earlyOnLoginEvent(LoginEvent event) {
        if (!event.getResult().isAllowed()) {
            return;
        }

        UUID playerId = event.getPlayer().getUniqueId();
        if (playerId == null) {
            event.setResult(ResultedEvent.ComponentResult.denied(Component.text("Account-ID unbekannt." + "\nAccount-ID unknown.")));
            return;
        }

        playerStartsLoggingIn(playerId);
        V value;
        try {
            value = loadOnLogin(playerId);
        } catch (LoadingPlayerDataFailedException e) {
            CubesideUtils.getInstance().getLogger().log(Level.SEVERE, "Could not load " + this.valueLoggingName + " for player " + playerId + ".", e);
            CubesideUtils.getInstance().getLogger().log(Level.SEVERE, "Denying join for player " + playerId + " because of an internal error.");
            event.setResult(ResultedEvent.ComponentResult.denied(Component.text(e.kickMessage)));
            return;
        }
        if (value != null) {
            this.addToHardCache(playerId, value);
        }
        playerDataLoadedOnLogin(playerId, value);
    }

    @Subscribe(order = PostOrder.LAST)
    public void lateOnLoginEvent(LoginEvent event) {
        UUID playerId = event.getPlayer().getUniqueId();

        playerFinishsLoggingIn(playerId);
        if (!event.getResult().isAllowed()) {
            V value = this.removeFromHardCache(playerId);
            this.playerDataUnloadedOnSuccesslessLogin(playerId, value);
            return;
        }
    }

    @Subscribe(order = PostOrder.LAST)
    public void lateOnPlayerQuitEvent(DisconnectEvent event) {
        Player player = event.getPlayer();
        playerQuitting(player);
        UUID playerId = player.getUniqueId();
        V value = removeFromHardCache(playerId);
        playerDataUnloadedOnQuit(player, value);
    }

    @Override
    protected boolean checkKey(Object key) {
        return key instanceof UUID;
    }

    protected void playerStartsLoggingIn(UUID playerId) {

    }

    protected abstract V loadOnLogin(UUID playerId) throws LoadingPlayerDataFailedException;

    protected void playerDataLoadedOnLogin(UUID playerId, V value) {

    }

    protected void playerFinishsLoggingIn(UUID playerId) {

    }

    protected void playerDataUnloadedOnSuccesslessLogin(UUID playerId, V value) {

    }

    protected void playerQuitting(Player player) {

    }

    protected void playerDataUnloadedOnQuit(Player player, V value) {

    }

}
