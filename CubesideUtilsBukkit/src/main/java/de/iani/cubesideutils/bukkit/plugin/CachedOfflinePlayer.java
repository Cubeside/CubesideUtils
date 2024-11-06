package de.iani.cubesideutils.bukkit.plugin;

import com.destroystokyo.paper.profile.PlayerProfile;
import io.papermc.paper.persistence.PersistentDataContainerView;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;
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
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@SuppressWarnings("deprecation")
public class CachedOfflinePlayer implements OfflinePlayer {

    private UUID playerId;
    private String name;
    private long lastSeen;

    private OfflinePlayer bukkitOfflinePlayer;

    public CachedOfflinePlayer(UUID playerId, String name, long lastSeen) {
        this.playerId = playerId;
        this.name = name;
        this.lastSeen = lastSeen;
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
        return lastSeen >= 0 ? lastSeen : getBukkitOfflinePlayer().getLastSeen();
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

    @Override
    public @NotNull com.destroystokyo.paper.profile.PlayerProfile getPlayerProfile() {
        return Bukkit.createProfile(playerId, name);
    }

    @Override
    public @Nullable Location getLastDeathLocation() {
        return getBukkitOfflinePlayer().getLastDeathLocation();
    }

    @Override
    public boolean isConnected() {
        return getBukkitOfflinePlayer().isConnected();
    }

    @Override
    public <E extends BanEntry<? super PlayerProfile>> @Nullable E ban(@Nullable String reason, @Nullable Date expires, @Nullable String source) {
        return getBukkitOfflinePlayer().ban(reason, expires, source);
    }

    @Override
    public <E extends BanEntry<? super PlayerProfile>> @Nullable E ban(@Nullable String reason, @Nullable Instant expires, @Nullable String source) {
        return getBukkitOfflinePlayer().ban(reason, expires, source);
    }

    @Override
    public <E extends BanEntry<? super PlayerProfile>> @Nullable E ban(@Nullable String reason, @Nullable Duration duration, @Nullable String source) {
        return getBukkitOfflinePlayer().ban(reason, duration, source);
    }

    @Override
    public Location getRespawnLocation() {
        return getBukkitOfflinePlayer().getRespawnLocation();
    }

    @Override
    public Location getLocation() {
        return getBukkitOfflinePlayer().getLocation();
    }

    @Override
    public @NotNull PersistentDataContainerView getPersistentDataContainer() {
        return getBukkitOfflinePlayer().getPersistentDataContainer();
    }
}