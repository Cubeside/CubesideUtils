package de.iani.cubesideutils.bukkit.plugin;

import de.iani.cubesideutils.plugin.GeneralDataCache;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public interface UtilsPluginApi {

    GeneralDataCache getGeneralDataCache();

    String getGeneralData(String key) throws SQLException;

    void setGeneralData(String key, String value) throws SQLException;

    List<String> getRanks();

    String getDefaultRank();

    int getPriority(String rank);

    String getPermission(String rank);

    String getPrefix(String rank);

    void setRankInformation(String rank, int priority, String permission, String prefix) throws SQLException;

    boolean removeRankInformation(String rank) throws SQLException;

    void updateRankInformation();

    Map<String, Boolean> getCachedRealServers();

    void sendMessageToPlayersAllServers(String seeMsgPermission, String message);

}