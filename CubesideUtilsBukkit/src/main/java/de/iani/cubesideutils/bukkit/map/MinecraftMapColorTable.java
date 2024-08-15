package de.iani.cubesideutils.bukkit.map;

import de.iani.cubesideutils.image.IndexedColorTable;
import java.awt.Color;
import org.bukkit.map.MapPalette;

public class MinecraftMapColorTable implements IndexedColorTable {
    private static final MinecraftMapColorTable instance = new MinecraftMapColorTable();

    public static MinecraftMapColorTable getInstance() {
        return instance;
    }

    private MinecraftMapColorTable() {
    }

    @SuppressWarnings({ "removal" })
    @Override
    public int getNearestIndex(int rgb) {
        return MapPalette.matchColor(new Color(rgb, true));
    }

    @SuppressWarnings({ "removal" })
    @Override
    public int getRGBForIndex(int index) {
        try {
            return MapPalette.getColor((byte) index).getRGB();
        } catch (Exception e) {
            return 0;
        }
    }
}
