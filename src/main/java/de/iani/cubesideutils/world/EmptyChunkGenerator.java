package de.iani.cubesideutils.world;

import java.util.Collections;
import java.util.List;
import java.util.Random;
import org.bukkit.World;
import org.bukkit.block.Biome;
import org.bukkit.generator.BlockPopulator;
import org.bukkit.generator.ChunkGenerator;

public class EmptyChunkGenerator extends ChunkGenerator {
    @Override
    public List<BlockPopulator> getDefaultPopulators(World world) {
        return Collections.emptyList();
    }

    @Override
    public ChunkData generateChunkData(World world, Random random, int x, int z, BiomeGrid biome) {
        int height = world.getMaxHeight();
        for (int xx = 0; xx < 16; xx += 4) {
            for (int zz = 0; zz < 16; zz += 4) {
                for (int yy = 0; yy < height; yy += 4) {
                    biome.setBiome(xx, yy, zz, Biome.PLAINS);
                }
            }
        }
        ChunkData chunk = createChunkData(world);
        return chunk;
    }
}
