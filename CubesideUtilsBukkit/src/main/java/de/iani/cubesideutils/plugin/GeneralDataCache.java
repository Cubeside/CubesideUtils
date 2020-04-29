package de.iani.cubesideutils.plugin;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

class GeneralDataCache {

    private UtilsPlugin plugin;
    private Map<String, String> cache;

    GeneralDataCache() {
        this.plugin = UtilsPlugin.getInstance();
        this.cache = new HashMap<>();
    }

    synchronized String get(String key) throws SQLException {
        try {
            return cache.computeIfAbsent(key, k -> {
                try {
                    return plugin.getDatabase().getGeneralData(k);
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

    synchronized void set(String key, String value) throws SQLException {
        plugin.getDatabase().setGeneralData(key, value);
        plugin.getGlobalDataHelper().sendData(MessageType.GENERAL_DATA_CHANGED, key);
        cache.put(key, value);
    }

    synchronized void invalidate(String key) {
        cache.remove(key);
    }

}
