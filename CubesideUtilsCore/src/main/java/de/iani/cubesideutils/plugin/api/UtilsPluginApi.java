package de.iani.cubesideutils.plugin.api;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public interface UtilsPluginApi {

    public String getGeneralData(String key) throws SQLException;

    public void setGeneralData(String key, String value) throws SQLException;

    public List<String> getRanks();

    public String getDefaultRank();

    public int getPriority(String rank);

    public String getPermission(String rank);

    public String getPrefix(String rank);

    public void setRankInformation(String rank, int priority, String permission, String prefix) throws SQLException;

    public boolean removeRankInformation(String rank) throws SQLException;

    public void updateRankInformation();

    public Map<String, Boolean> getCachedRealServers();

    public void sendMessageToPlayersAllServers(String seeMsgPermission, String message);

}