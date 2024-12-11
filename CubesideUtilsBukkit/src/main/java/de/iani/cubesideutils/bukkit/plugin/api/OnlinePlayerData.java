package de.iani.cubesideutils.bukkit.plugin.api;

import de.iani.cubesideutils.bukkit.plugin.AnvilGUI;
import org.bukkit.entity.Player;

public interface OnlinePlayerData extends PlayerDataBukkit {

    public Player getPlayer();

    public long getLastAction();

    public boolean isLocallyAfk();

    public boolean isManuallySetAfk();

    public void manuallySetAfk(boolean messagePlayer);

    public void checkRank();

    public String getHostName();

    public AnvilGUI getOpenAnvilGUI();
}