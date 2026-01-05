package de.iani.cubesideutils.bungee.plugin.api;

import de.iani.cubesideutils.bungee.plugin.CubesideUtilsBungee;
import de.iani.cubesideutils.collections.AdvancedCacheMap;
import de.iani.cubesideutils.plugin.CubesideUtils;
import java.util.Objects;
import java.util.UUID;
import java.util.logging.Level;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.LoginEvent;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.md_5.bungee.event.EventPriority;

public abstract class PlayerCacheMap<V, D> extends AdvancedCacheMap<UUID, V, D> implements Listener {

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
        ProxyServer.getInstance().getPluginManager().registerListener(CubesideUtilsBungee.getInstance().getPlugin(), this);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void earlyOnLoginEvent(LoginEvent event) {
        if (event.isCancelled()) {
            return;
        }

        UUID playerId = event.getConnection().getUniqueId();
        if (playerId == null) {
            event.setCancelled(true);
            event.setReason(new ComponentBuilder("Account-ID unbekannt." + "\nAccount-ID unknown.").build());
            return;
        }

        playerStartsLoggingIn(playerId);
        V value;
        try {
            value = loadOnLogin(playerId);
        } catch (LoadingPlayerDataFailedException e) {
            CubesideUtils.getInstance().getLogger().log(Level.SEVERE, "Could not load " + this.valueLoggingName + " for player " + playerId + ".", e);
            CubesideUtils.getInstance().getLogger().log(Level.SEVERE, "Denying join for player " + playerId + " because of an internal error.");
            event.setCancelled(true);
            event.setReason(new TextComponent(e.getKickMessage()));
            return;
        }
        if (value != null) {
            this.addToHardCache(playerId, value);
        }
        playerDataLoadedOnLogin(playerId, value);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void lateOnLoginEvent(LoginEvent event) {
        UUID playerId = event.getConnection().getUniqueId();

        playerFinishsLoggingIn(playerId);
        if (event.isCancelled()) {
            V value = this.removeFromHardCache(playerId);
            this.playerDataUnloadedOnSuccesslessLogin(playerId, value);
            return;
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void lateOnPlayerQuitEvent(PlayerDisconnectEvent event) {
        ProxiedPlayer player = event.getPlayer();
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

    protected void playerQuitting(ProxiedPlayer player) {

    }

    protected void playerDataUnloadedOnQuit(ProxiedPlayer player, V value) {

    }

}
