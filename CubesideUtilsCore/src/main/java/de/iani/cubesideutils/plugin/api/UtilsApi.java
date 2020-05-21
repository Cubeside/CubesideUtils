package de.iani.cubesideutils.plugin.api;

import de.iani.cubesideutils.plugin.CubesideUtils;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public interface UtilsApi {

    public static UtilsApi getInstance() {
        return CubesideUtils.getInstance();
    }

    public abstract String getGeneralData(String key) throws SQLException;

    public abstract void setGeneralData(String key, String value) throws SQLException;

    public abstract PasswordHandler getPasswordHandler(String key);

    public abstract boolean removePasswordKey(String key) throws SQLException;

    public abstract List<String> getRanks();

    public abstract String getDefaultRank();

    public abstract int getPriority(String rank);

    public abstract String getPermission(String rank);

    public abstract String getPrefix(String rank);

    public abstract void setRankInformation(String rank, int priority, String permission, String prefix) throws SQLException;

    public abstract boolean removeRankInformation(String rank) throws SQLException;

    public abstract void updateRankInformation();

    public abstract Map<String, Boolean> getCachedRealServers();

    public abstract void sendMessageToPlayersAllServers(String seeMsgPermission, String message);

}