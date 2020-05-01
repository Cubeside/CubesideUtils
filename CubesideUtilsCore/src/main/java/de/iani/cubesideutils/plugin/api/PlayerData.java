package de.iani.cubesideutils.plugin.api;

import java.util.UUID;

public interface PlayerData {

    public UUID getPlayerId();

    public boolean isOnlineHere();

    public long getFirstJoin();

    public void setFirstJoinAndLastJoinAndSeen(long value);

    public long getLastJoin();

    public void setLastJoinAndSeen(long value);

    public long getLastSeen();

    public void setLastSeen(long lastSeen);

    public boolean isGloballyAfk();

    public String getRank();

    public String getRankPrefix();

}