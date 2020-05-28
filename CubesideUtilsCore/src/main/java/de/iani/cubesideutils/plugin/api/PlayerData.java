package de.iani.cubesideutils.plugin.api;

import java.util.UUID;

public interface PlayerData {

    public static final int MAX_CUSTOM_DATA_KEY_LENGTH = 127;

    public UUID getPlayerId();

    public String getCustomData(String key);

    public String setCustomData(String key, String value);

    public String removeCustomData(String key);

    public boolean isOnlineHere();

    public long getFirstJoin();

    public long getLastJoin();

    public long getLastSeen();

    public boolean isGloballyAfk();

    public String getRank();

    public String getRankPrefix();

    public int getRankPriority();

}