package de.iani.cubesideutils.bukkit.plugin;

import org.bukkit.plugin.java.JavaPlugin;

public class UtilsPluginBukkit extends JavaPlugin {

    private CubesideUtilsBukkit core;

    public UtilsPluginBukkit() {
        this.core = new CubesideUtilsBukkit(this);
    }

    @Override
    public void onEnable() {
        core.onEnable();
    }

}
