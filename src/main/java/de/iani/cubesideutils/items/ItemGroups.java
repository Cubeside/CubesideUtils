package de.iani.cubesideutils.items;

import java.util.Collections;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.Map;
import java.util.Set;

import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;

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

    private static final EnumSet<Material> DOUBLE_BLOCK_PLANTS_INTERNAL = EnumSet.noneOf(Material.class);
    public static final Set<Material> DOUBLE_BLOCK_PLANTS = Collections.unmodifiableSet(DOUBLE_BLOCK_PLANTS_INTERNAL);

    private static final EnumSet<Material> SINGLE_BLOCK_PLANTS_INTERNAL = EnumSet.noneOf(Material.class);
    public static final Set<Material> SINGLE_BLOCK_PLANTS = Collections.unmodifiableSet(SINGLE_BLOCK_PLANTS_INTERNAL);

    private static final EnumSet<Material> PLANTS_INTERNAL = EnumSet.noneOf(Material.class);
    public static final Set<Material> PLANTS = Collections.unmodifiableSet(PLANTS_INTERNAL);

    private static final EnumSet<Material> CROPS_INTERNAL = EnumSet.noneOf(Material.class);
    public static final Set<Material> CROPS = Collections.unmodifiableSet(CROPS_INTERNAL);

    private static final EnumMap<Material, EntityType> SPAWN_EGGS_INTERNAL = new EnumMap<>(Material.class);
    public static final Map<Material, EntityType> SPAWN_EGGS_MAP = Collections.unmodifiableMap(SPAWN_EGGS_INTERNAL);
    public static final Set<Material> SPAWN_EGGS = SPAWN_EGGS_MAP.keySet();

    static {
        SPAWN_EGGS_INTERNAL.put(Material.BAT_SPAWN_EGG, EntityType.BAT);
        SPAWN_EGGS_INTERNAL.put(Material.BLAZE_SPAWN_EGG, EntityType.BLAZE);
        SPAWN_EGGS_INTERNAL.put(Material.CAVE_SPIDER_SPAWN_EGG, EntityType.CAVE_SPIDER);
        SPAWN_EGGS_INTERNAL.put(Material.CHICKEN_SPAWN_EGG, EntityType.CHICKEN);
        SPAWN_EGGS_INTERNAL.put(Material.COD_SPAWN_EGG, EntityType.COD);
        SPAWN_EGGS_INTERNAL.put(Material.COW_SPAWN_EGG, EntityType.COW);
        SPAWN_EGGS_INTERNAL.put(Material.CREEPER_SPAWN_EGG, EntityType.CREEPER);
        SPAWN_EGGS_INTERNAL.put(Material.DOLPHIN_SPAWN_EGG, EntityType.DOLPHIN);
        SPAWN_EGGS_INTERNAL.put(Material.DONKEY_SPAWN_EGG, EntityType.DONKEY);
        SPAWN_EGGS_INTERNAL.put(Material.DROWNED_SPAWN_EGG, EntityType.DROWNED);
        SPAWN_EGGS_INTERNAL.put(Material.ELDER_GUARDIAN_SPAWN_EGG, EntityType.ELDER_GUARDIAN);
        SPAWN_EGGS_INTERNAL.put(Material.ENDERMAN_SPAWN_EGG, EntityType.ENDERMAN);
        SPAWN_EGGS_INTERNAL.put(Material.ENDERMITE_SPAWN_EGG, EntityType.ENDERMITE);
        SPAWN_EGGS_INTERNAL.put(Material.EVOKER_SPAWN_EGG, EntityType.EVOKER);
        SPAWN_EGGS_INTERNAL.put(Material.GHAST_SPAWN_EGG, EntityType.GHAST);
        SPAWN_EGGS_INTERNAL.put(Material.GUARDIAN_SPAWN_EGG, EntityType.GUARDIAN);
        SPAWN_EGGS_INTERNAL.put(Material.HORSE_SPAWN_EGG, EntityType.HORSE);
        SPAWN_EGGS_INTERNAL.put(Material.HUSK_SPAWN_EGG, EntityType.HUSK);
        SPAWN_EGGS_INTERNAL.put(Material.LLAMA_SPAWN_EGG, EntityType.LLAMA);
        SPAWN_EGGS_INTERNAL.put(Material.MAGMA_CUBE_SPAWN_EGG, EntityType.MAGMA_CUBE);
        SPAWN_EGGS_INTERNAL.put(Material.MOOSHROOM_SPAWN_EGG, EntityType.MUSHROOM_COW);
        SPAWN_EGGS_INTERNAL.put(Material.MULE_SPAWN_EGG, EntityType.MULE);
        SPAWN_EGGS_INTERNAL.put(Material.OCELOT_SPAWN_EGG, EntityType.OCELOT);
        SPAWN_EGGS_INTERNAL.put(Material.PARROT_SPAWN_EGG, EntityType.PARROT);
        SPAWN_EGGS_INTERNAL.put(Material.PHANTOM_SPAWN_EGG, EntityType.PHANTOM);
        SPAWN_EGGS_INTERNAL.put(Material.PIG_SPAWN_EGG, EntityType.PIG);
        SPAWN_EGGS_INTERNAL.put(Material.POLAR_BEAR_SPAWN_EGG, EntityType.POLAR_BEAR);
        SPAWN_EGGS_INTERNAL.put(Material.PUFFERFISH_SPAWN_EGG, EntityType.PUFFERFISH);
        SPAWN_EGGS_INTERNAL.put(Material.RABBIT_SPAWN_EGG, EntityType.RABBIT);
        SPAWN_EGGS_INTERNAL.put(Material.SALMON_SPAWN_EGG, EntityType.SALMON);
        SPAWN_EGGS_INTERNAL.put(Material.SHEEP_SPAWN_EGG, EntityType.SHEEP);
        SPAWN_EGGS_INTERNAL.put(Material.SHULKER_SPAWN_EGG, EntityType.SHULKER);
        SPAWN_EGGS_INTERNAL.put(Material.SILVERFISH_SPAWN_EGG, EntityType.SILVERFISH);
        SPAWN_EGGS_INTERNAL.put(Material.SKELETON_HORSE_SPAWN_EGG, EntityType.SKELETON_HORSE);
        SPAWN_EGGS_INTERNAL.put(Material.SKELETON_SPAWN_EGG, EntityType.SKELETON);
        SPAWN_EGGS_INTERNAL.put(Material.SLIME_SPAWN_EGG, EntityType.SLIME);
        SPAWN_EGGS_INTERNAL.put(Material.SPIDER_SPAWN_EGG, EntityType.SPIDER);
        SPAWN_EGGS_INTERNAL.put(Material.SQUID_SPAWN_EGG, EntityType.SQUID);
        SPAWN_EGGS_INTERNAL.put(Material.STRAY_SPAWN_EGG, EntityType.STRAY);
        SPAWN_EGGS_INTERNAL.put(Material.TROPICAL_FISH_SPAWN_EGG, EntityType.TROPICAL_FISH);
        SPAWN_EGGS_INTERNAL.put(Material.TURTLE_SPAWN_EGG, EntityType.TURTLE);
        SPAWN_EGGS_INTERNAL.put(Material.VEX_SPAWN_EGG, EntityType.VEX);
        SPAWN_EGGS_INTERNAL.put(Material.VILLAGER_SPAWN_EGG, EntityType.VILLAGER);
        SPAWN_EGGS_INTERNAL.put(Material.VINDICATOR_SPAWN_EGG, EntityType.VINDICATOR);
        SPAWN_EGGS_INTERNAL.put(Material.WITCH_SPAWN_EGG, EntityType.WITCH);
        SPAWN_EGGS_INTERNAL.put(Material.WITHER_SKELETON_SPAWN_EGG, EntityType.WITHER_SKELETON);
        SPAWN_EGGS_INTERNAL.put(Material.WOLF_SPAWN_EGG, EntityType.WOLF);
        SPAWN_EGGS_INTERNAL.put(Material.ZOMBIE_HORSE_SPAWN_EGG, EntityType.ZOMBIE_HORSE);
        SPAWN_EGGS_INTERNAL.put(Material.ZOMBIE_PIGMAN_SPAWN_EGG, EntityType.PIG_ZOMBIE);
        SPAWN_EGGS_INTERNAL.put(Material.ZOMBIE_SPAWN_EGG, EntityType.ZOMBIE);
        SPAWN_EGGS_INTERNAL.put(Material.ZOMBIE_VILLAGER_SPAWN_EGG, EntityType.ZOMBIE_VILLAGER);

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
                    if (!SPAWN_EGGS_INTERNAL.containsKey(m)) {
                        SPAWN_EGGS_INTERNAL.put(m, EntityType.UNKNOWN);
                    }
                }
            }
        }
        DOUBLE_BLOCK_PLANTS_INTERNAL.add(Material.LARGE_FERN);
        DOUBLE_BLOCK_PLANTS_INTERNAL.add(Material.TALL_GRASS);
        DOUBLE_BLOCK_PLANTS_INTERNAL.add(Material.ROSE_BUSH);
        DOUBLE_BLOCK_PLANTS_INTERNAL.add(Material.LILAC);
        DOUBLE_BLOCK_PLANTS_INTERNAL.add(Material.SUNFLOWER);
        DOUBLE_BLOCK_PLANTS_INTERNAL.add(Material.PEONY);

        CROPS_INTERNAL.add(Material.NETHER_WART);
        CROPS_INTERNAL.add(Material.WHEAT);
        CROPS_INTERNAL.add(Material.CARROTS);
        CROPS_INTERNAL.add(Material.POTATOES);
        CROPS_INTERNAL.add(Material.BEETROOTS);
        CROPS_INTERNAL.add(Material.PUMPKIN_STEM);
        CROPS_INTERNAL.add(Material.MELON_STEM);

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
        SINGLE_BLOCK_PLANTS_INTERNAL.addAll(CROPS_INTERNAL);

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
        return SPAWN_EGGS_INTERNAL.containsKey(m);
    }

    public static EntityType getSpawnedTypeForSpawnEgg(Material m) {
        return SPAWN_EGGS_INTERNAL.get(m);
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

    public static boolean isCrop(Material m) {
        return CROPS_INTERNAL.contains(m);
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
            case LIGHT_GRAY:
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
            case LIGHT_GRAY:
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
