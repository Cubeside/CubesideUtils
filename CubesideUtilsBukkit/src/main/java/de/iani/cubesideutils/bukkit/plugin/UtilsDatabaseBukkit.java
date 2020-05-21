package de.iani.cubesideutils.bukkit.plugin;

import de.iani.cubesideutils.Pair;
import de.iani.cubesideutils.Triple;
import de.iani.cubesideutils.plugin.UtilsDatabase;
import de.iani.cubesideutils.sql.SQLConfig;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.bukkit.BanEntry;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.Statistic;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

public class UtilsDatabaseBukkit extends UtilsDatabase<PlayerDataImplBukkit> {

    private static class CachedOfflinePlayer implements OfflinePlayer {

        private UUID playerId;
        private String name;

        private OfflinePlayer bukkitOfflinePlayer;

        public CachedOfflinePlayer(UUID playerId, String name) {
            this.playerId = playerId;
            this.name = name;
        }

        private OfflinePlayer getBukkitOfflinePlayer() {
            if (bukkitOfflinePlayer == null) {
                bukkitOfflinePlayer = Bukkit.getOfflinePlayer(playerId);
            }
            return bukkitOfflinePlayer;
        }

        @Override
        public boolean isOp() {
            return getBukkitOfflinePlayer().isOp();
        }

        @Override
        public void setOp(boolean value) {
            getBukkitOfflinePlayer().setOp(value);
        }

        @Override
        public boolean isOnline() {
            return getBukkitOfflinePlayer().isOnline();
        }

        @Override
        public String getName() {
            return name;
        }

        @Override
        public Map<String, Object> serialize() {
            return getBukkitOfflinePlayer().serialize();
        }

        @Override
        public UUID getUniqueId() {
            return getBukkitOfflinePlayer().getUniqueId();
        }

        @Override
        public boolean isBanned() {
            return getBukkitOfflinePlayer().isBanned();
        }

        @Override
        public BanEntry banPlayer(String reason) {
            return getBukkitOfflinePlayer().banPlayer(reason);
        }

        @Override
        public BanEntry banPlayer(String reason, String source) {
            return getBukkitOfflinePlayer().banPlayer(reason, source);
        }

        @Override
        public BanEntry banPlayer(String reason, Date expires) {
            return getBukkitOfflinePlayer().banPlayer(reason, expires);
        }

        @Override
        public BanEntry banPlayer(String reason, Date expires, String source) {
            return getBukkitOfflinePlayer().banPlayer(reason, expires, source);
        }

        @Override
        public BanEntry banPlayer(String reason, Date expires, String source, boolean kickIfOnline) {
            return getBukkitOfflinePlayer().banPlayer(reason, expires, source, kickIfOnline);
        }

        @Override
        public boolean isWhitelisted() {
            return getBukkitOfflinePlayer().isWhitelisted();
        }

        @Override
        public void setWhitelisted(boolean value) {
            getBukkitOfflinePlayer().setWhitelisted(value);
        }

        @Override
        public Player getPlayer() {
            return getBukkitOfflinePlayer().getPlayer();
        }

        @Override
        public long getFirstPlayed() {
            return getBukkitOfflinePlayer().getFirstPlayed();
        }

        @Deprecated
        @Override
        public long getLastPlayed() {
            return getBukkitOfflinePlayer().getLastPlayed();
        }

        @Override
        public boolean hasPlayedBefore() {
            return getBukkitOfflinePlayer().hasPlayedBefore();
        }

        @Override
        public Location getBedSpawnLocation() {
            return getBukkitOfflinePlayer().getBedSpawnLocation();
        }

        @Override
        public long getLastLogin() {
            return getBukkitOfflinePlayer().getLastLogin();
        }

        @Override
        public long getLastSeen() {
            return getBukkitOfflinePlayer().getLastSeen();
        }

        @Override
        public void incrementStatistic(Statistic statistic) throws IllegalArgumentException {
            getBukkitOfflinePlayer().incrementStatistic(statistic);
        }

        @Override
        public void decrementStatistic(Statistic statistic) throws IllegalArgumentException {
            getBukkitOfflinePlayer().decrementStatistic(statistic);
        }

        @Override
        public void incrementStatistic(Statistic statistic, int amount) throws IllegalArgumentException {
            getBukkitOfflinePlayer().incrementStatistic(statistic, amount);
        }

        @Override
        public void decrementStatistic(Statistic statistic, int amount) throws IllegalArgumentException {
            getBukkitOfflinePlayer().decrementStatistic(statistic, amount);
        }

        @Override
        public void setStatistic(Statistic statistic, int newValue) throws IllegalArgumentException {
            getBukkitOfflinePlayer().setStatistic(statistic, newValue);
        }

        @Override
        public int getStatistic(Statistic statistic) throws IllegalArgumentException {
            return getBukkitOfflinePlayer().getStatistic(statistic);
        }

        @Override
        public void incrementStatistic(Statistic statistic, Material material) throws IllegalArgumentException {
            getBukkitOfflinePlayer().incrementStatistic(statistic, material);
        }

        @Override
        public void decrementStatistic(Statistic statistic, Material material) throws IllegalArgumentException {
            getBukkitOfflinePlayer().decrementStatistic(statistic, material);
        }

        @Override
        public int getStatistic(Statistic statistic, Material material) throws IllegalArgumentException {
            return getBukkitOfflinePlayer().getStatistic(statistic, material);
        }

        @Override
        public void incrementStatistic(Statistic statistic, Material material, int amount) throws IllegalArgumentException {
            getBukkitOfflinePlayer().incrementStatistic(statistic, material, amount);
        }

        @Override
        public void decrementStatistic(Statistic statistic, Material material, int amount) throws IllegalArgumentException {
            getBukkitOfflinePlayer().decrementStatistic(statistic, material, amount);
        }

        @Override
        public void setStatistic(Statistic statistic, Material material, int newValue) throws IllegalArgumentException {
            getBukkitOfflinePlayer().setStatistic(statistic, material, newValue);
        }

        @Override
        public void incrementStatistic(Statistic statistic, EntityType entityType) throws IllegalArgumentException {
            getBukkitOfflinePlayer().incrementStatistic(statistic, entityType);
        }

        @Override
        public void decrementStatistic(Statistic statistic, EntityType entityType) throws IllegalArgumentException {
            getBukkitOfflinePlayer().decrementStatistic(statistic, entityType);
        }

        @Override
        public int getStatistic(Statistic statistic, EntityType entityType) throws IllegalArgumentException {
            return getBukkitOfflinePlayer().getStatistic(statistic, entityType);
        }

        @Override
        public void incrementStatistic(Statistic statistic, EntityType entityType, int amount) throws IllegalArgumentException {
            getBukkitOfflinePlayer().incrementStatistic(statistic, entityType, amount);
        }

        @Override
        public void decrementStatistic(Statistic statistic, EntityType entityType, int amount) {
            getBukkitOfflinePlayer().decrementStatistic(statistic, entityType, amount);
        }

        @Override
        public void setStatistic(Statistic statistic, EntityType entityType, int newValue) {
            getBukkitOfflinePlayer().setStatistic(statistic, entityType, newValue);
        }
    }

    public UtilsDatabaseBukkit(SQLConfig sqlConfig) throws SQLException {
        super(sqlConfig);
    }

    public OnlinePlayerDataImpl getOnlinePlayerData(UUID playerId, boolean insertIfMissing, long lastAction, boolean manuallySetAfk) throws SQLException {
        return (OnlinePlayerDataImpl) getPlayerData(playerId, true, insertIfMissing, lastAction, manuallySetAfk);
    }

    @Override
    public PlayerDataImplBukkit getPlayerData(UUID playerId, boolean isOnline, boolean insertIfMissing, long lastAction, boolean manuallySetAfk) throws SQLException {
        Triple<Triple<Long, Long, Long>, Boolean, String> data = getPlayerDataData(playerId, isOnline, insertIfMissing, lastAction, manuallySetAfk);
        if (data == null) {
            return null;
        }

        long firstJoin = data.first.first;
        long lastJoin = data.first.second;
        long lastSeen = data.first.third;
        boolean afk = data.second;
        String rank = data.third;

        return isOnline ? new OnlinePlayerDataImpl(playerId, firstJoin, lastJoin, lastSeen, afk, lastAction, manuallySetAfk, rank) : new PlayerDataImplBukkit(playerId, firstJoin, lastJoin, lastSeen, afk, rank);
    }

    public List<OfflinePlayer> searchPlayersByPartialName(String partialName) throws SQLException {
        List<Pair<UUID, String>> idNamePairs = getPlayerIdsByPartialName(partialName);
        List<OfflinePlayer> result = new ArrayList<>(idNamePairs.size());

        for (Pair<UUID, String> pair : idNamePairs) {
            OfflinePlayer player = new CachedOfflinePlayer(pair.first, pair.second);
            result.add(player);
        }

        return result;
    }

}
