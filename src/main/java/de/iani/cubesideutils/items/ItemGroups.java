package de.iani.cubesideutils.items;

import java.util.Collections;
import java.util.EnumSet;
import java.util.Set;

import org.bukkit.DyeColor;
import org.bukkit.Material;

public class ItemGroups {
    private ItemGroups() {
        // prevents instances
    }

    // colorable
    private static final EnumSet<Material> CONCRETE_POWDER_INTERNAL = EnumSet.noneOf(Material.class);
    private static final EnumSet<Material> CONCRETE_INTERNAL = EnumSet.noneOf(Material.class);
    private static final EnumSet<Material> WOOL_INTERNAL = EnumSet.noneOf(Material.class);
    private static final EnumSet<Material> STAINED_GLASS_INTERNAL = EnumSet.noneOf(Material.class);
    private static final EnumSet<Material> STAINED_GLASS_PANE_INTERNAL = EnumSet.noneOf(Material.class);
    private static final EnumSet<Material> SHULKER_BOX_INTERNAL = EnumSet.noneOf(Material.class);
    private static final EnumSet<Material> BED_INTERNAL = EnumSet.noneOf(Material.class);
    private static final EnumSet<Material> CARPET_INTERNAL = EnumSet.noneOf(Material.class);
    private static final EnumSet<Material> TERRACOTTA_INTERNAL = EnumSet.noneOf(Material.class);
    private static final EnumSet<Material> GLAZED_TERRACOTTA_INTERNAL = EnumSet.noneOf(Material.class);

    public static final Set<Material> CONCRETE_POWDER = Collections.unmodifiableSet(CONCRETE_POWDER_INTERNAL);
    public static final Set<Material> CONCRETE = Collections.unmodifiableSet(CONCRETE_INTERNAL);
    public static final Set<Material> WOOL = Collections.unmodifiableSet(WOOL_INTERNAL);
    public static final Set<Material> STAINED_GLASS = Collections.unmodifiableSet(STAINED_GLASS_INTERNAL);
    public static final Set<Material> STAINED_GLASS_PANE = Collections.unmodifiableSet(STAINED_GLASS_PANE_INTERNAL);
    public static final Set<Material> SHULKER_BOX = Collections.unmodifiableSet(SHULKER_BOX_INTERNAL);
    public static final Set<Material> BED = Collections.unmodifiableSet(BED_INTERNAL);
    public static final Set<Material> CARPET = Collections.unmodifiableSet(CARPET_INTERNAL);
    public static final Set<Material> TERRACOTTA = Collections.unmodifiableSet(TERRACOTTA_INTERNAL);
    public static final Set<Material> GLAZED_TERRACOTTA = Collections.unmodifiableSet(GLAZED_TERRACOTTA_INTERNAL);

    private static final EnumSet<Material> SPAWN_EGGS_INTERNAL = EnumSet.noneOf(Material.class);
    public static final Set<Material> SPAWN_EGGS = Collections.unmodifiableSet(SPAWN_EGGS_INTERNAL);

    private static final EnumSet<Material> DOUBLE_BLOCK_PLANTS_INTERNAL = EnumSet.noneOf(Material.class);
    public static final Set<Material> DOUBLE_BLOCK_PLANTS = Collections.unmodifiableSet(DOUBLE_BLOCK_PLANTS_INTERNAL);

    private static final EnumSet<Material> SINGLE_BLOCK_PLANTS_INTERNAL = EnumSet.noneOf(Material.class);
    public static final Set<Material> SINGLE_BLOCK_PLANTS = Collections.unmodifiableSet(SINGLE_BLOCK_PLANTS_INTERNAL);

    private static final EnumSet<Material> PLANTS_INTERNAL = EnumSet.noneOf(Material.class);
    public static final Set<Material> PLANTS = Collections.unmodifiableSet(PLANTS_INTERNAL);

    static {
        for (Material m : Material.values()) {
            String name = m.name();
            if (!name.startsWith("LEGACY_")) {
                if (name.endsWith("_CONCRETE_POWDER")) {
                    CONCRETE_POWDER_INTERNAL.add(m);
                } else if (name.endsWith("_CONCRETE")) {
                    CONCRETE_INTERNAL.add(m);
                } else if (name.endsWith("_WOOL")) {
                    WOOL_INTERNAL.add(m);
                } else if (name.endsWith("_STAINED_GLASS")) {
                    STAINED_GLASS_INTERNAL.add(m);
                } else if (name.endsWith("_STAINED_GLASS_PANE")) {
                    STAINED_GLASS_PANE_INTERNAL.add(m);
                } else if (name.endsWith("SHULKER_BOX")) {
                    SHULKER_BOX_INTERNAL.add(m);
                } else if (name.endsWith("_BED")) {
                    BED_INTERNAL.add(m);
                } else if (name.endsWith("_CARPET")) {
                    CARPET_INTERNAL.add(m);
                } else if (name.endsWith("_TERRACOTTA")) {
                    TERRACOTTA_INTERNAL.add(m);
                } else if (name.endsWith("_GLAZED_TERRACOTTA")) {
                    GLAZED_TERRACOTTA_INTERNAL.add(m);
                } else if (name.endsWith("_SPAWN_EGG")) {
                    SPAWN_EGGS_INTERNAL.add(m);
                }
            }
        }
        DOUBLE_BLOCK_PLANTS_INTERNAL.add(Material.LARGE_FERN);
        DOUBLE_BLOCK_PLANTS_INTERNAL.add(Material.TALL_GRASS);
        DOUBLE_BLOCK_PLANTS_INTERNAL.add(Material.ROSE_BUSH);
        DOUBLE_BLOCK_PLANTS_INTERNAL.add(Material.LILAC);
        DOUBLE_BLOCK_PLANTS_INTERNAL.add(Material.SUNFLOWER);
        DOUBLE_BLOCK_PLANTS_INTERNAL.add(Material.PEONY);

        SINGLE_BLOCK_PLANTS_INTERNAL.add(Material.POPPY);
        SINGLE_BLOCK_PLANTS_INTERNAL.add(Material.DANDELION);
        SINGLE_BLOCK_PLANTS_INTERNAL.add(Material.BLUE_ORCHID);
        SINGLE_BLOCK_PLANTS_INTERNAL.add(Material.ALLIUM);
        SINGLE_BLOCK_PLANTS_INTERNAL.add(Material.AZURE_BLUET);
        SINGLE_BLOCK_PLANTS_INTERNAL.add(Material.RED_TULIP);
        SINGLE_BLOCK_PLANTS_INTERNAL.add(Material.WHITE_TULIP);
        SINGLE_BLOCK_PLANTS_INTERNAL.add(Material.ORANGE_TULIP);
        SINGLE_BLOCK_PLANTS_INTERNAL.add(Material.PINK_TULIP);
        SINGLE_BLOCK_PLANTS_INTERNAL.add(Material.OXEYE_DAISY);
        SINGLE_BLOCK_PLANTS_INTERNAL.add(Material.GRASS);
        SINGLE_BLOCK_PLANTS_INTERNAL.add(Material.FERN);

        SINGLE_BLOCK_PLANTS_INTERNAL.add(Material.DEAD_BUSH);
        SINGLE_BLOCK_PLANTS_INTERNAL.add(Material.NETHER_WART);
        SINGLE_BLOCK_PLANTS_INTERNAL.add(Material.WHEAT);
        SINGLE_BLOCK_PLANTS_INTERNAL.add(Material.CARROTS);
        SINGLE_BLOCK_PLANTS_INTERNAL.add(Material.POTATOES);
        SINGLE_BLOCK_PLANTS_INTERNAL.add(Material.BEETROOTS);
        SINGLE_BLOCK_PLANTS_INTERNAL.add(Material.PUMPKIN_STEM);
        SINGLE_BLOCK_PLANTS_INTERNAL.add(Material.MELON_STEM);

        PLANTS_INTERNAL.addAll(SINGLE_BLOCK_PLANTS_INTERNAL);
        PLANTS_INTERNAL.addAll(DOUBLE_BLOCK_PLANTS_INTERNAL);
    }

    public static boolean isConcretePowder(Material m) {
        return CONCRETE_POWDER_INTERNAL.contains(m);
    }

    public static boolean isConcrete(Material m) {
        return CONCRETE_INTERNAL.contains(m);
    }

    public static boolean isWool(Material m) {
        return WOOL_INTERNAL.contains(m);
    }

    public static boolean isStainedGlass(Material m) {
        return STAINED_GLASS_INTERNAL.contains(m);
    }

    public static boolean isStainedGlassPane(Material m) {
        return STAINED_GLASS_PANE_INTERNAL.contains(m);
    }

    public static boolean isShulkerBox(Material m) {
        return SHULKER_BOX_INTERNAL.contains(m);
    }

    public static boolean isBed(Material m) {
        return BED_INTERNAL.contains(m);
    }

    public static boolean isCarpet(Material m) {
        return CARPET_INTERNAL.contains(m);
    }

    public static boolean isTerracotta(Material m) {
        return TERRACOTTA_INTERNAL.contains(m);
    }

    public static boolean isGlazedTerracotta(Material m) {
        return GLAZED_TERRACOTTA_INTERNAL.contains(m);
    }

    public static boolean isSpawnEgg(Material m) {
        return SPAWN_EGGS_INTERNAL.contains(m);
    }

    public static boolean isDoubleBlockPlant(Material m) {
        return DOUBLE_BLOCK_PLANTS_INTERNAL.contains(m);
    }

    public static boolean isSingleBlockPlant(Material m) {
        return SINGLE_BLOCK_PLANTS_INTERNAL.contains(m);
    }

    public static boolean isPlant(Material m) {
        return PLANTS_INTERNAL.contains(m);
    }

    public static Material getWoolForDyeColor(DyeColor color) {
        switch (color) {
            case WHITE:
                return Material.WHITE_WOOL;
            case ORANGE:
                return Material.ORANGE_WOOL;
            case MAGENTA:
                return Material.MAGENTA_WOOL;
            case LIGHT_BLUE:
                return Material.LIGHT_BLUE_WOOL;
            case YELLOW:
                return Material.YELLOW_WOOL;
            case LIME:
                return Material.LIME_WOOL;
            case PINK:
                return Material.PINK_WOOL;
            case GRAY:
                return Material.GRAY_WOOL;
            case SILVER:
                return Material.LIGHT_GRAY_WOOL;
            case CYAN:
                return Material.CYAN_WOOL;
            case PURPLE:
                return Material.PURPLE_WOOL;
            case BLUE:
                return Material.BLUE_WOOL;
            case BROWN:
                return Material.BROWN_WOOL;
            case GREEN:
                return Material.GREEN_WOOL;
            case RED:
                return Material.RED_WOOL;
            case BLACK:
                return Material.BLACK_WOOL;
        }
        throw new IllegalArgumentException("Dye " + color + " unknown");
    }

    public static Material getConcreteForDyeColor(DyeColor color) {
        switch (color) {
            case WHITE:
                return Material.WHITE_CONCRETE;
            case ORANGE:
                return Material.ORANGE_CONCRETE;
            case MAGENTA:
                return Material.MAGENTA_CONCRETE;
            case LIGHT_BLUE:
                return Material.LIGHT_BLUE_CONCRETE;
            case YELLOW:
                return Material.YELLOW_CONCRETE;
            case LIME:
                return Material.LIME_CONCRETE;
            case PINK:
                return Material.PINK_CONCRETE;
            case GRAY:
                return Material.GRAY_CONCRETE;
            case SILVER:
                return Material.LIGHT_GRAY_CONCRETE;
            case CYAN:
                return Material.CYAN_CONCRETE;
            case PURPLE:
                return Material.PURPLE_CONCRETE;
            case BLUE:
                return Material.BLUE_CONCRETE;
            case BROWN:
                return Material.BROWN_CONCRETE;
            case GREEN:
                return Material.GREEN_CONCRETE;
            case RED:
                return Material.RED_CONCRETE;
            case BLACK:
                return Material.BLACK_CONCRETE;
        }
        throw new IllegalArgumentException("Dye " + color + " unknown");
    }
}
