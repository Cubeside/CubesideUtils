package de.iani.cubesideutils.plugin.api;

import java.util.UUID;

public interface PlayerData {

    public UUID getPlayerId();

    public boolean isOnlineHere();

    public long getFirstJoin();

    public long getLastJoin();

    public long getLastSeen();

    public boolean isGloballyAfk();

    public String getRank();

    public String getRankPrefix();

}