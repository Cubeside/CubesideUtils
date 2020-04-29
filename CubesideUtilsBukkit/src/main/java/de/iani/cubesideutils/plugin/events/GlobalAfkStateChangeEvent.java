package de.iani.cubesideutils.plugin.events;

import de.iani.cubesideutils.plugin.PlayerData;

public class GlobalAfkStateChangeEvent extends PlayerDataEvent {

    private boolean nowAfk;

    public GlobalAfkStateChangeEvent(PlayerData data, boolean nowAfk) {
        super(data);

        this.nowAfk = nowAfk;
    }

    public boolean isNowAfk() {
        return nowAfk;
    }

}
