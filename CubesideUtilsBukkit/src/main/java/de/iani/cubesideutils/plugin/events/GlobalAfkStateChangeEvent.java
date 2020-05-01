package de.iani.cubesideutils.plugin.events;

import de.iani.cubesideutils.bukkit.plugin.api.PlayerDataBukkit;

public class GlobalAfkStateChangeEvent extends PlayerDataEvent {

    private boolean nowAfk;

    public GlobalAfkStateChangeEvent(PlayerDataBukkit data, boolean nowAfk) {
        super(data);

        this.nowAfk = nowAfk;
    }

    public boolean isNowAfk() {
        return nowAfk;
    }

}
