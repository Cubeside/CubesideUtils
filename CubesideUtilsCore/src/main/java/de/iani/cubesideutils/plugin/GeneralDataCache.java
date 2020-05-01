package de.iani.cubesideutils.plugin;

import de.iani.cubesideutils.plugin.UtilsGlobalDataHelper.MessageType;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class GeneralDataCache {

    private CubesideUtils core;
    private Map<String, String> cache;

    public GeneralDataCache() {
        this.core = CubesideUtils.getInstance();
        this.cache = new HashMap<>();
    }

    public synchronized String get(String key) throws SQLException {
        try {
            return cache.computeIfAbsent(key, k -> {
                try {
                    return core.getDatabase().getGeneralData(k);
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            });
        } catch (RuntimeException e) {
            if (e.getCause() instanceof SQLException) {
                throw (SQLException) e.getCause();
            } else {
                throw e;
            }
        }
    }

    public synchronized void set(String key, String value) throws SQLException {
        core.getDatabase().setGeneralData(key, value);
        core.getGlobalDataHelper().sendData(MessageType.GENERAL_DATA_CHANGED, key);
        cache.put(key, value);
    }

    public synchronized void invalidate(String key) {
        cache.remove(key);
    }

}
