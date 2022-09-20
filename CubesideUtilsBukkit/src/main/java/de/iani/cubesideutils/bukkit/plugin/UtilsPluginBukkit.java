package de.iani.cubesideutils.bukkit.plugin;

import de.iani.cubesideutils.bukkit.world.EmptyChunkGenerator;
import org.bukkit.generator.ChunkGenerator;
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

    @Override
    public ChunkGenerator getDefaultWorldGenerator(String worldName, String id) {
        // used by some plugins to check if a worldgenerator is available
        if (worldName == null || worldName.length() == 0 || worldName.equals("test")) {
            return new EmptyChunkGenerator();
        }
        return new EmptyChunkGenerator();
    }
}
