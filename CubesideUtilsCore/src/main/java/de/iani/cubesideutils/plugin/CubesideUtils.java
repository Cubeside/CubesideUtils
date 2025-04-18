package de.iani.cubesideutils.plugin;

import de.cubeside.connection.ConnectionAPI;
import de.cubeside.connection.GlobalPlayer;
import de.iani.cubesideutils.Triple;
import de.iani.cubesideutils.conditions.BinaryCombinedCondition;
import de.iani.cubesideutils.conditions.ConstantCondition;
import de.iani.cubesideutils.conditions.NegatedCondition;
import de.iani.cubesideutils.plugin.UtilsGlobalDataHelper.MessageType;
import de.iani.cubesideutils.plugin.api.PasswordHandler;
import de.iani.cubesideutils.plugin.api.PlayerData;
import de.iani.cubesideutils.plugin.api.UtilsApi;
import de.iani.cubesideutils.serialization.StringSerialization;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.logging.Level;
import java.util.logging.Logger;

public abstract class CubesideUtils implements UtilsApi {

    private static volatile CubesideUtils instance = null;

    public static CubesideUtils getInstance() {
        return instance;
    }

    static {
        StringSerialization.register(BinaryCombinedCondition.SERIALIZATION_TYPE, BinaryCombinedCondition::deserialize);
        StringSerialization.register(ConstantCondition.SERIALIZATION_TYPE, ConstantCondition::deserialize);
        StringSerialization.register(NegatedCondition.SERIALIZATION_TYPE, NegatedCondition::deserialize);
    }

    private GeneralDataCache generalDataCache;

    private Map<String, PasswordHandlerImpl> passwordHandlers;
    private Map<String, OtpHandlerImpl> otpHandlers;

    private ReadWriteLock rankLock;
    private List<String> ranks;
    private Map<String, Triple<Integer, String, String>> rankPermissionsAndPrefixes;

    private Map<String, Boolean> cachedRealServers;

    public CubesideUtils() {
        synchronized (CubesideUtils.class) {
            if (instance != null) {
                throw new IllegalStateException("Only one instance permitted.");
            }
            instance = this;
        }

        this.passwordHandlers = new LinkedHashMap<>();
        this.otpHandlers = new LinkedHashMap<>();

        this.rankLock = new ReentrantReadWriteLock();
        this.ranks = Collections.emptyList();
        this.rankPermissionsAndPrefixes = Collections.emptyMap();

        this.cachedRealServers = Collections.synchronizedMap(new HashMap<>());
    }

    public void onEnable() {
        try {
            this.generalDataCache = new GeneralDataCache();

            onEnableInternal();

            updateRankInformation();
        } catch (Throwable e) {
            getLogger().log(Level.SEVERE, "Could not initilize CubesideUtils plugin.", e);
            shutdownServer();
        }
    }

    protected void onEnableInternal() throws Throwable {
    }

    protected abstract void shutdownServer();

    public abstract Logger getLogger();

    protected abstract ConnectionAPI getConnectionApi();

    public abstract UtilsDatabase<?> getDatabase();

    public abstract UtilsGlobalDataHelper getGlobalDataHelper();

    public GeneralDataCache getGeneralDataCache() {
        return this.generalDataCache;
    }

    @Override
    public String getGeneralData(String key) throws SQLException {
        return this.generalDataCache.get(key);
    }

    @Override
    public void setGeneralData(String key, String value) throws SQLException {
        this.generalDataCache.set(key, value);
    }

    @Override
    public PasswordHandler getPasswordHandler(String key) {
        return this.passwordHandlers.computeIfAbsent(key, k -> new PasswordHandlerImpl(k));
    }

    @Override
    public OtpHandler getOtpHandler(String key) {
        return this.otpHandlers.computeIfAbsent(key, k -> new OtpHandlerImpl(k));
    }

    @Override
    public boolean removePasswordKey(String key) throws SQLException {
        this.passwordHandlers.remove(key);
        return getDatabase().removePasswordKey(key);
    }

    @Override
    public int countActivePlayers(long lastSeenSince, long firstJoinUntil) throws SQLException {
        if (lastSeenSince > System.currentTimeMillis()) {
            return 0;
        }

        int result = getDatabase().countActivePlayers(lastSeenSince, firstJoinUntil);
        for (GlobalPlayer player : getGlobalDataHelper().getOnlinePlayers()) {
            PlayerData data = getPlayerData(player.getUniqueId());
            if (data.getLastSeen() < lastSeenSince && data.getFirstJoin() <= firstJoinUntil) {
                result++;
            }
        }

        return result;
    }

    @Override
    public List<String> getRanks() {
        this.rankLock.readLock().lock();

        try {
            return this.ranks;
        } finally {
            this.rankLock.readLock().unlock();
        }
    }

    @Override
    public String getDefaultRank() {
        this.rankLock.readLock().lock();

        try {
            if (this.ranks.isEmpty()) {
                return null;
            }

            return this.ranks.get(this.ranks.size() - 1);
        } finally {
            this.rankLock.readLock().unlock();
        }
    }

    @Override
    public int getPriority(String rank) {
        this.rankLock.readLock().lock();

        try {
            return this.rankPermissionsAndPrefixes.get(rank).first;
        } finally {
            this.rankLock.readLock().unlock();
        }
    }

    @Override
    public String getPermission(String rank) {
        this.rankLock.readLock().lock();

        try {
            return this.rankPermissionsAndPrefixes.get(rank).second;
        } finally {
            this.rankLock.readLock().unlock();
        }
    }

    @Override
    public String getPrefix(String rank) {
        this.rankLock.readLock().lock();

        try {
            return this.rankPermissionsAndPrefixes.get(rank).third;
        } finally {
            this.rankLock.readLock().unlock();
        }
    }

    public boolean isRank(String rank) {
        this.rankLock.readLock().lock();

        try {
            return this.rankPermissionsAndPrefixes.containsKey(rank);
        } finally {
            this.rankLock.readLock().unlock();
        }
    }

    @Override
    public void setRankInformation(String rank, int priority, String permission, String prefix) throws SQLException {
        getDatabase().setRankInformation(rank, priority, permission, prefix);
        getGlobalDataHelper().sendData(MessageType.RANK_INFORMATION_CHANGED);
        updateRankInformation();
    }

    @Override
    public boolean removeRankInformation(String rank) throws SQLException {
        if (!getDatabase().removeRankInformation(rank)) {
            return false;
        }
        getGlobalDataHelper().sendData(MessageType.RANK_INFORMATION_CHANGED);
        updateRankInformation();

        return true;
    }

    @Override
    public void updateRankInformation() {
        this.rankLock.writeLock().lock();

        try {
            this.rankPermissionsAndPrefixes = Collections.unmodifiableMap(getDatabase().getRankInformation());
            this.ranks = Collections.unmodifiableList(new ArrayList<>(rankPermissionsAndPrefixes.keySet()));
        } catch (SQLException e) {
            getLogger().log(Level.SEVERE, "Could not get rank information from database.", e);
        } finally {
            this.rankLock.writeLock().unlock();
        }

        for (PlayerDataImpl data : getLoadedPlayerData()) {
            data.checkRank();
        }
    }

    protected abstract Iterable<? extends PlayerDataImpl> getLoadedPlayerData();

    @Override
    public Map<String, Boolean> getCachedRealServers() {
        return this.cachedRealServers;
    }

}