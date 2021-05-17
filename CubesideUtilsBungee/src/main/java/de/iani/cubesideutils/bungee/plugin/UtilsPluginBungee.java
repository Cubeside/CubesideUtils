package de.iani.cubesideutils.bungee.plugin;

import net.md_5.bungee.api.plugin.Plugin;

public class UtilsPluginBungee extends Plugin {
    private CubesideUtilsBungee core;

    public UtilsPluginBungee() {
        this.core = new CubesideUtilsBungee(this);
    }

    @Override
    public void onEnable() {
        core.onEnable();
    }
}
