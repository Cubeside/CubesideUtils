package de.iani.cubesideutils.bukkit.plugin.api;

import org.bukkit.entity.Player;

public interface OnlinePlayerData extends PlayerDataBukkit {

    public Player getPlayer();

    public long getLastAction();

    public boolean isLocallyAfk();

    public boolean isManuallySetAfk();

    public void manuallySetAfk(boolean messagePlayer);

    public void checkRank();

}