package de.iani.cubesideutils.items;

import java.util.Collections;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.Map;
import java.util.Set;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;

public class ItemGroups {
    private ItemGroups() {
        throw new UnsupportedOperationException("No instance for you, Sir!");
        // really prevents instances
    }

    private static final EnumSet<Material> AIRS_INTERNAL = EnumSet.noneOf(Material.class);
    public static final Set<Material> AIRS = Collections.unmodifiableSet(AIRS_INTERNAL);

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

    // wood-made
    private static final EnumSet<Material> LOGS_INTERNAL = EnumSet.noneOf(Material.class);
    private static final EnumSet<Material> LEAVES_INTERNAL = EnumSet.noneOf(Material.class);
    private static final EnumSet<Material> PLANKS_INTERNAL = EnumSet.noneOf(Material.class);
    private static final EnumSet<Material> SAPLINGS_INTERNAL = EnumSet.noneOf(Material.class);
    private static final EnumSet<Material> WOODEN_SLABS_INTERNAL = EnumSet.noneOf(Material.class);
    private static final EnumSet<Material> WOODEN_STAIRS_INTERNAL = EnumSet.noneOf(Material.class);
    private static final EnumSet<Material> WOODEN_FENCES_INTERNAL = EnumSet.noneOf(Material.class);
    private static final EnumSet<Material> WOODEN_FENCE_GATES_INTERNAL = EnumSet.noneOf(Material.class);
    private static final EnumSet<Material> WOODEN_DOORS_INTERNAL = EnumSet.noneOf(Material.class);
    private static final EnumSet<Material> WOODEN_TRAPDOORS_INTERNAL = EnumSet.noneOf(Material.class);
    private static final EnumSet<Material> WOODEN_BUTTONS_INTERNAL = EnumSet.noneOf(Material.class);
    private static final EnumSet<Material> WOODEN_PRESSURE_PLATES_INTERNAL = EnumSet.noneOf(Material.class);
    private static final EnumSet<Material> BOATS_INTERNAL = EnumSet.noneOf(Material.class);

    public static final Set<Material> LOGS = Collections.unmodifiableSet(LOGS_INTERNAL);
    public static final Set<Material> LEAVES = Collections.unmodifiableSet(LEAVES_INTERNAL);
    public static final Set<Material> PLANKS = Collections.unmodifiableSet(PLANKS_INTERNAL);
    public static final Set<Material> SAPLINGS = Collections.unmodifiableSet(SAPLINGS_INTERNAL);
    public static final Set<Material> WOODEN_SLABLS = Collections.unmodifiableSet(WOODEN_SLABS_INTERNAL);
    public static final Set<Material> WOODEN_STAIRS = Collections.unmodifiableSet(WOODEN_STAIRS_INTERNAL);
    public static final Set<Material> WOODEN_FENCES = Collections.unmodifiableSet(WOODEN_FENCES_INTERNAL);
    public static final Set<Material> WOODEN_FENCE_GATES = Collections.unmodifiableSet(WOODEN_FENCE_GATES_INTERNAL);
    public static final Set<Material> WOODEN_DOORS = Collections.unmodifiableSet(WOODEN_DOORS_INTERNAL);
    public static final Set<Material> WOODEN_TRAPDOORS = Collections.unmodifiableSet(WOODEN_TRAPDOORS_INTERNAL);
    public static final Set<Material> WOODEN_BUTTONS = Collections.unmodifiableSet(WOODEN_BUTTONS_INTERNAL);
    public static final Set<Material> WOODEN_PRESSURE_PLATES = Collections.unmodifiableSet(WOODEN_PRESSURE_PLATES_INTERNAL);
    public static final Set<Material> BOATS = Collections.unmodifiableSet(BOATS_INTERNAL);

    // redstone stuff
    private static final EnumSet<Material> FENCE_GATES_INTERNAL = EnumSet.noneOf(Material.class);
    private static final EnumSet<Material> DOORS_INTERNAL = EnumSet.noneOf(Material.class);
    private static final EnumSet<Material> TRAPDOORS_INTERNAL = EnumSet.noneOf(Material.class);
    private static final EnumSet<Material> BUTTONS_INTERNAL = EnumSet.noneOf(Material.class);
    private static final EnumSet<Material> PRESSURE_PLATES_INTERNAL = EnumSet.noneOf(Material.class);

    public static final Set<Material> FENCE_GATES = Collections.unmodifiableSet(FENCE_GATES_INTERNAL);
    public static final Set<Material> DOORS = Collections.unmodifiableSet(DOORS_INTERNAL);
    public static final Set<Material> TRAPDOORS = Collections.unmodifiableSet(TRAPDOORS_INTERNAL);
    public static final Set<Material> BUTTONS = Collections.unmodifiableSet(BUTTONS_INTERNAL);
    public static final Set<Material> PRESSURE_PLATES = Collections.unmodifiableSet(PRESSURE_PLATES_INTERNAL);

    private static final EnumSet<Material> DOUBLE_BLOCK_PLANTS_INTERNAL = EnumSet.noneOf(Material.class);
    public static final Set<Material> DOUBLE_BLOCK_PLANTS = Collections.unmodifiableSet(DOUBLE_BLOCK_PLANTS_INTERNAL);

    private static final EnumSet<Material> SINGLE_BLOCK_PLANTS_INTERNAL = EnumSet.noneOf(Material.class);
    public static final Set<Material> SINGLE_BLOCK_PLANTS = Collections.unmodifiableSet(SINGLE_BLOCK_PLANTS_INTERNAL);

    private static final EnumSet<Material> PLANTS_INTERNAL = EnumSet.noneOf(Material.class);
    public static final Set<Material> PLANTS = Collections.unmodifiableSet(PLANTS_INTERNAL);

    private static final EnumSet<Material> CROPS_INTERNAL = EnumSet.noneOf(Material.class);
    public static final Set<Material> CROPS = Collections.unmodifiableSet(CROPS_INTERNAL);

    private static final EnumSet<Material> FISHES_INTERNAL = EnumSet.noneOf(Material.class);
    public static final Set<Material> FISHES = Collections.unmodifiableSet(FISHES_INTERNAL);

    private static final EnumSet<Material> FISH_BUCKETS_INTERNAL = EnumSet.noneOf(Material.class);
    public static final Set<Material> FISH_BUCKETS = Collections.unmodifiableSet(FISH_BUCKETS_INTERNAL);

    private static final EnumSet<Material> POTTED_PLANTS_INTERNAL = EnumSet.noneOf(Material.class);
    public static final Set<Material> POTTED_PLANTS = Collections.unmodifiableSet(POTTED_PLANTS_INTERNAL);

    private static final EnumSet<Material> MUSIC_DISCS_INTERNAL = EnumSet.noneOf(Material.class);
    public static final Set<Material> MUSIC_DISCS = Collections.unmodifiableSet(MUSIC_DISCS_INTERNAL);

    private static final EnumMap<Material, EntityType> SPAWN_EGGS_INTERNAL = new EnumMap<>(Material.class);
    public static final Map<Material, EntityType> SPAWN_EGGS_MAP = Collections.unmodifiableMap(SPAWN_EGGS_INTERNAL);
    public static final Set<Material> SPAWN_EGGS = SPAWN_EGGS_MAP.keySet();

    private static final EnumMap<DyeColor, Material> DYE_COLOR_TO_DYE = new EnumMap<>(DyeColor.class);
    private static final EnumMap<DyeColor, Material> DYE_COLOR_TO_WOOL = new EnumMap<>(DyeColor.class);
    private static final EnumMap<DyeColor, Material> DYE_COLOR_TO_CONCRETE = new EnumMap<>(DyeColor.class);
    private static final EnumMap<DyeColor, Material> DYE_COLOR_TO_CONCRETE_POWDER = new EnumMap<>(DyeColor.class);
    private static final EnumMap<DyeColor, Material> DYE_COLOR_TO_TERRACOTTA = new EnumMap<>(DyeColor.class);
    private static final EnumMap<DyeColor, Material> DYE_COLOR_TO_GLAZED_TERRACOTTA = new EnumMap<>(DyeColor.class);
    private static final EnumMap<DyeColor, Material> DYE_COLOR_TO_STAINED_GLASS = new EnumMap<>(DyeColor.class);
    private static final EnumMap<DyeColor, Material> DYE_COLOR_TO_STAINED_GLASS_PANE = new EnumMap<>(DyeColor.class);
    private static final EnumMap<DyeColor, Material> DYE_COLOR_TO_BED = new EnumMap<>(DyeColor.class);
    private static final EnumMap<DyeColor, Material> DYE_COLOR_TO_SHULKER_BOX = new EnumMap<>(DyeColor.class);
    private static final EnumMap<Material, DyeColor> MATERIAL_TO_DYE_COLOR = new EnumMap<>(Material.class);

    private static final EnumSet<Material> SIGNS_INTERNAL = EnumSet.noneOf(Material.class);
    public static final Set<Material> SIGNS = Collections.unmodifiableSet(SIGNS_INTERNAL);
    private static final EnumSet<Material> WALL_SIGNS_INTERNAL = EnumSet.noneOf(Material.class);
    public static final Set<Material> WALL_SIGNS = Collections.unmodifiableSet(WALL_SIGNS_INTERNAL);
    private static final EnumSet<Material> ALL_SIGNS_INTERNAL = EnumSet.noneOf(Material.class);
    public static final Set<Material> ALL_SIGNS = Collections.unmodifiableSet(ALL_SIGNS_INTERNAL);

    static {
        AIRS_INTERNAL.add(Material.AIR);
        AIRS_INTERNAL.add(Material.CAVE_AIR);
        AIRS_INTERNAL.add(Material.VOID_AIR);

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

        SPAWN_EGGS_INTERNAL.put(Material.CAT_SPAWN_EGG, EntityType.CAT);
        SPAWN_EGGS_INTERNAL.put(Material.FOX_SPAWN_EGG, EntityType.FOX);
        SPAWN_EGGS_INTERNAL.put(Material.PANDA_SPAWN_EGG, EntityType.PANDA);
        SPAWN_EGGS_INTERNAL.put(Material.RAVAGER_SPAWN_EGG, EntityType.RAVAGER);
        SPAWN_EGGS_INTERNAL.put(Material.PILLAGER_SPAWN_EGG, EntityType.PILLAGER);
        SPAWN_EGGS_INTERNAL.put(Material.TRADER_LLAMA_SPAWN_EGG, EntityType.TRADER_LLAMA);
        SPAWN_EGGS_INTERNAL.put(Material.WANDERING_TRADER_SPAWN_EGG, EntityType.WANDERING_TRADER);

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
                } else if (name.endsWith("_LOG") || name.equals("_WOOD")) {
                    LOGS_INTERNAL.add(m);
                } else if (name.endsWith("_LEAVES")) {
                    LEAVES_INTERNAL.add(m);
                } else if (name.endsWith("_PLANKS")) {
                    PLANKS_INTERNAL.add(m);
                } else if (name.endsWith("_SAPLINGS")) {
                    SAPLINGS_INTERNAL.add(m);
                } else if (name.endsWith("OAK_SLAB") || name.equals("SPRUCE_SLAB") || name.equals("BIRCH_SLAB") || name.equals("JUNGLE_SLAB") || name.equals("ACACIA_SLAB")) {
                    WOODEN_SLABS_INTERNAL.add(m);
                } else if (name.endsWith("OAK_STAIRS") || name.equals("SPRUCE_STAIRS") || name.equals("BIRCH_STAIRS") || name.equals("JUNGLE_STAIRS") || name.equals("ACACIA_STAIRS")) {
                    WOODEN_STAIRS_INTERNAL.add(m);
                } else if (name.endsWith("OAK_FENCE") || name.equals("SPRUCE_FENCE") || name.equals("BIRCH_FENCE") || name.equals("JUNGLE_FENCE") || name.equals("ACACIA_FENCE")) {
                    WOODEN_FENCES_INTERNAL.add(m);
                } else if (name.endsWith("_FENCE_GATE")) {
                    if (name.endsWith("OAK_FENCE_GATE") || name.equals("SPRUCE_FENCE_GATE") || name.equals("BIRCH_FENCE_GATE") || name.equals("JUNGLE_FENCE_GATE") || name.equals("ACACIA_FENCE_GATE")) {
                        WOODEN_FENCE_GATES_INTERNAL.add(m);
                    }
                    FENCE_GATES_INTERNAL.add(m);
                } else if (name.endsWith("_DOOR")) {
                    if (name.endsWith("OAK_DOOR") || name.equals("SPRUCE_DOOR") || name.equals("BIRCH_DOOR") || name.equals("JUNGLE_DOOR") || name.equals("ACACIA_DOOR")) {
                        WOODEN_DOORS_INTERNAL.add(m);
                    }
                    DOORS_INTERNAL.add(m);
                } else if (name.endsWith("_TRAPDOOR")) {
                    if (name.endsWith("OAK_TRAPDOOR") || name.equals("SPRUCE_TRAPDOOR") || name.equals("BIRCH_TRAPDOOR") || name.equals("JUNGLE_TRAPDOOR") || name.equals("ACACIA_TRAPDOOR")) {
                        WOODEN_TRAPDOORS_INTERNAL.add(m);
                    }
                    TRAPDOORS_INTERNAL.add(m);
                } else if (name.endsWith("_BUTTON")) {
                    if (name.endsWith("OAK_BUTTON") || name.equals("SPRUCE_BUTTON") || name.equals("BIRCH_BUTTON") || name.equals("JUNGLE_BUTTON") || name.equals("ACACIA_BUTTON")) {
                        WOODEN_BUTTONS_INTERNAL.add(m);
                    }
                    BUTTONS_INTERNAL.add(m);
                } else if (name.endsWith("_PRESSURE_PLATE")) {
                    if (name.endsWith("OAK_PRESSURE_PLATE") || name.equals("SPRUCE_PRESSURE_PLATE") || name.equals("BIRCH_PRESSURE_PLATE") || name.equals("JUNGLE_PRESSURE_PLATE") || name.equals("ACACIA_PRESSURE_PLATE")) {
                        WOODEN_PRESSURE_PLATES_INTERNAL.add(m);
                    }
                    PRESSURE_PLATES_INTERNAL.add(m);
                } else if (name.endsWith("_WALL_SIGN")) {
                    WALL_SIGNS_INTERNAL.add(m);
                } else if (name.endsWith("_SIGN")) {
                    SIGNS_INTERNAL.add(m);
                } else if (name.endsWith("_BOAT")) {
                    BOATS_INTERNAL.add(m);
                } else if (name.endsWith("_SPAWN_EGG")) {
                    if (!SPAWN_EGGS_INTERNAL.containsKey(m)) {
                        SPAWN_EGGS_INTERNAL.put(m, EntityType.UNKNOWN);
                    }
                } else if (name.startsWith("POTTED_")) {
                    POTTED_PLANTS_INTERNAL.add(m);
                } else if (name.startsWith("MUSIC_DISC_")) {
                    MUSIC_DISCS_INTERNAL.add(m);
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
        SINGLE_BLOCK_PLANTS_INTERNAL.add(Material.SWEET_BERRY_BUSH);
        SINGLE_BLOCK_PLANTS_INTERNAL.addAll(CROPS_INTERNAL);

        PLANTS_INTERNAL.addAll(SINGLE_BLOCK_PLANTS_INTERNAL);
        PLANTS_INTERNAL.addAll(DOUBLE_BLOCK_PLANTS_INTERNAL);

        FISHES_INTERNAL.add(Material.COD);
        FISHES_INTERNAL.add(Material.SALMON);
        FISHES_INTERNAL.add(Material.PUFFERFISH);
        FISHES_INTERNAL.add(Material.TROPICAL_FISH);

        FISH_BUCKETS_INTERNAL.add(Material.COD_BUCKET);
        FISH_BUCKETS_INTERNAL.add(Material.SALMON_BUCKET);
        FISH_BUCKETS_INTERNAL.add(Material.PUFFERFISH_BUCKET);
        FISH_BUCKETS_INTERNAL.add(Material.TROPICAL_FISH_BUCKET);

        DYE_COLOR_TO_DYE.put(DyeColor.WHITE, Material.BONE_MEAL);
        DYE_COLOR_TO_DYE.put(DyeColor.ORANGE, Material.ORANGE_DYE);
        DYE_COLOR_TO_DYE.put(DyeColor.MAGENTA, Material.MAGENTA_DYE);
        DYE_COLOR_TO_DYE.put(DyeColor.LIGHT_BLUE, Material.LIGHT_BLUE_DYE);
        DYE_COLOR_TO_DYE.put(DyeColor.YELLOW, Material.YELLOW_DYE);
        DYE_COLOR_TO_DYE.put(DyeColor.LIME, Material.LIME_DYE);
        DYE_COLOR_TO_DYE.put(DyeColor.PINK, Material.PINK_DYE);
        DYE_COLOR_TO_DYE.put(DyeColor.GRAY, Material.GRAY_DYE);
        DYE_COLOR_TO_DYE.put(DyeColor.LIGHT_GRAY, Material.LIGHT_GRAY_DYE);
        DYE_COLOR_TO_DYE.put(DyeColor.CYAN, Material.CYAN_DYE);
        DYE_COLOR_TO_DYE.put(DyeColor.PURPLE, Material.PURPLE_DYE);
        DYE_COLOR_TO_DYE.put(DyeColor.BLUE, Material.LAPIS_LAZULI);
        DYE_COLOR_TO_DYE.put(DyeColor.BROWN, Material.COCOA_BEANS);
        DYE_COLOR_TO_DYE.put(DyeColor.GREEN, Material.GREEN_DYE);
        DYE_COLOR_TO_DYE.put(DyeColor.RED, Material.RED_DYE);
        DYE_COLOR_TO_DYE.put(DyeColor.BLACK, Material.INK_SAC);
        DYE_COLOR_TO_DYE.forEach((key, value) -> MATERIAL_TO_DYE_COLOR.put(value, key));

        DYE_COLOR_TO_WOOL.put(DyeColor.WHITE, Material.WHITE_WOOL);
        DYE_COLOR_TO_WOOL.put(DyeColor.ORANGE, Material.ORANGE_WOOL);
        DYE_COLOR_TO_WOOL.put(DyeColor.MAGENTA, Material.MAGENTA_WOOL);
        DYE_COLOR_TO_WOOL.put(DyeColor.LIGHT_BLUE, Material.LIGHT_BLUE_WOOL);
        DYE_COLOR_TO_WOOL.put(DyeColor.YELLOW, Material.YELLOW_WOOL);
        DYE_COLOR_TO_WOOL.put(DyeColor.LIME, Material.LIME_WOOL);
        DYE_COLOR_TO_WOOL.put(DyeColor.PINK, Material.PINK_WOOL);
        DYE_COLOR_TO_WOOL.put(DyeColor.GRAY, Material.GRAY_WOOL);
        DYE_COLOR_TO_WOOL.put(DyeColor.LIGHT_GRAY, Material.LIGHT_GRAY_WOOL);
        DYE_COLOR_TO_WOOL.put(DyeColor.CYAN, Material.CYAN_WOOL);
        DYE_COLOR_TO_WOOL.put(DyeColor.PURPLE, Material.PURPLE_WOOL);
        DYE_COLOR_TO_WOOL.put(DyeColor.BLUE, Material.BLUE_WOOL);
        DYE_COLOR_TO_WOOL.put(DyeColor.BROWN, Material.BROWN_WOOL);
        DYE_COLOR_TO_WOOL.put(DyeColor.GREEN, Material.GREEN_WOOL);
        DYE_COLOR_TO_WOOL.put(DyeColor.RED, Material.RED_WOOL);
        DYE_COLOR_TO_WOOL.put(DyeColor.BLACK, Material.BLACK_WOOL);
        DYE_COLOR_TO_WOOL.forEach((key, value) -> MATERIAL_TO_DYE_COLOR.put(value, key));

        DYE_COLOR_TO_CONCRETE.put(DyeColor.WHITE, Material.WHITE_CONCRETE);
        DYE_COLOR_TO_CONCRETE.put(DyeColor.ORANGE, Material.ORANGE_CONCRETE);
        DYE_COLOR_TO_CONCRETE.put(DyeColor.MAGENTA, Material.MAGENTA_CONCRETE);
        DYE_COLOR_TO_CONCRETE.put(DyeColor.LIGHT_BLUE, Material.LIGHT_BLUE_CONCRETE);
        DYE_COLOR_TO_CONCRETE.put(DyeColor.YELLOW, Material.YELLOW_CONCRETE);
        DYE_COLOR_TO_CONCRETE.put(DyeColor.LIME, Material.LIME_CONCRETE);
        DYE_COLOR_TO_CONCRETE.put(DyeColor.PINK, Material.PINK_CONCRETE);
        DYE_COLOR_TO_CONCRETE.put(DyeColor.GRAY, Material.GRAY_CONCRETE);
        DYE_COLOR_TO_CONCRETE.put(DyeColor.LIGHT_GRAY, Material.LIGHT_GRAY_CONCRETE);
        DYE_COLOR_TO_CONCRETE.put(DyeColor.CYAN, Material.CYAN_CONCRETE);
        DYE_COLOR_TO_CONCRETE.put(DyeColor.PURPLE, Material.PURPLE_CONCRETE);
        DYE_COLOR_TO_CONCRETE.put(DyeColor.BLUE, Material.BLUE_CONCRETE);
        DYE_COLOR_TO_CONCRETE.put(DyeColor.BROWN, Material.BROWN_CONCRETE);
        DYE_COLOR_TO_CONCRETE.put(DyeColor.GREEN, Material.GREEN_CONCRETE);
        DYE_COLOR_TO_CONCRETE.put(DyeColor.RED, Material.RED_CONCRETE);
        DYE_COLOR_TO_CONCRETE.put(DyeColor.BLACK, Material.BLACK_CONCRETE);
        DYE_COLOR_TO_CONCRETE.forEach((key, value) -> MATERIAL_TO_DYE_COLOR.put(value, key));

        DYE_COLOR_TO_CONCRETE_POWDER.put(DyeColor.WHITE, Material.WHITE_CONCRETE_POWDER);
        DYE_COLOR_TO_CONCRETE_POWDER.put(DyeColor.ORANGE, Material.ORANGE_CONCRETE_POWDER);
        DYE_COLOR_TO_CONCRETE_POWDER.put(DyeColor.MAGENTA, Material.MAGENTA_CONCRETE_POWDER);
        DYE_COLOR_TO_CONCRETE_POWDER.put(DyeColor.LIGHT_BLUE, Material.LIGHT_BLUE_CONCRETE_POWDER);
        DYE_COLOR_TO_CONCRETE_POWDER.put(DyeColor.YELLOW, Material.YELLOW_CONCRETE_POWDER);
        DYE_COLOR_TO_CONCRETE_POWDER.put(DyeColor.LIME, Material.LIME_CONCRETE_POWDER);
        DYE_COLOR_TO_CONCRETE_POWDER.put(DyeColor.PINK, Material.PINK_CONCRETE_POWDER);
        DYE_COLOR_TO_CONCRETE_POWDER.put(DyeColor.GRAY, Material.GRAY_CONCRETE_POWDER);
        DYE_COLOR_TO_CONCRETE_POWDER.put(DyeColor.LIGHT_GRAY, Material.LIGHT_GRAY_CONCRETE_POWDER);
        DYE_COLOR_TO_CONCRETE_POWDER.put(DyeColor.CYAN, Material.CYAN_CONCRETE_POWDER);
        DYE_COLOR_TO_CONCRETE_POWDER.put(DyeColor.PURPLE, Material.PURPLE_CONCRETE_POWDER);
        DYE_COLOR_TO_CONCRETE_POWDER.put(DyeColor.BLUE, Material.BLUE_CONCRETE_POWDER);
        DYE_COLOR_TO_CONCRETE_POWDER.put(DyeColor.BROWN, Material.BROWN_CONCRETE_POWDER);
        DYE_COLOR_TO_CONCRETE_POWDER.put(DyeColor.GREEN, Material.GREEN_CONCRETE_POWDER);
        DYE_COLOR_TO_CONCRETE_POWDER.put(DyeColor.RED, Material.RED_CONCRETE_POWDER);
        DYE_COLOR_TO_CONCRETE_POWDER.put(DyeColor.BLACK, Material.BLACK_CONCRETE_POWDER);
        DYE_COLOR_TO_CONCRETE_POWDER.forEach((key, value) -> MATERIAL_TO_DYE_COLOR.put(value, key));

        DYE_COLOR_TO_TERRACOTTA.put(DyeColor.WHITE, Material.WHITE_TERRACOTTA);
        DYE_COLOR_TO_TERRACOTTA.put(DyeColor.ORANGE, Material.ORANGE_TERRACOTTA);
        DYE_COLOR_TO_TERRACOTTA.put(DyeColor.MAGENTA, Material.MAGENTA_TERRACOTTA);
        DYE_COLOR_TO_TERRACOTTA.put(DyeColor.LIGHT_BLUE, Material.LIGHT_BLUE_TERRACOTTA);
        DYE_COLOR_TO_TERRACOTTA.put(DyeColor.YELLOW, Material.YELLOW_TERRACOTTA);
        DYE_COLOR_TO_TERRACOTTA.put(DyeColor.LIME, Material.LIME_TERRACOTTA);
        DYE_COLOR_TO_TERRACOTTA.put(DyeColor.PINK, Material.PINK_TERRACOTTA);
        DYE_COLOR_TO_TERRACOTTA.put(DyeColor.GRAY, Material.GRAY_TERRACOTTA);
        DYE_COLOR_TO_TERRACOTTA.put(DyeColor.LIGHT_GRAY, Material.LIGHT_GRAY_TERRACOTTA);
        DYE_COLOR_TO_TERRACOTTA.put(DyeColor.CYAN, Material.CYAN_TERRACOTTA);
        DYE_COLOR_TO_TERRACOTTA.put(DyeColor.PURPLE, Material.PURPLE_TERRACOTTA);
        DYE_COLOR_TO_TERRACOTTA.put(DyeColor.BLUE, Material.BLUE_TERRACOTTA);
        DYE_COLOR_TO_TERRACOTTA.put(DyeColor.BROWN, Material.BROWN_TERRACOTTA);
        DYE_COLOR_TO_TERRACOTTA.put(DyeColor.GREEN, Material.GREEN_TERRACOTTA);
        DYE_COLOR_TO_TERRACOTTA.put(DyeColor.RED, Material.RED_TERRACOTTA);
        DYE_COLOR_TO_TERRACOTTA.put(DyeColor.BLACK, Material.BLACK_TERRACOTTA);
        DYE_COLOR_TO_TERRACOTTA.forEach((key, value) -> MATERIAL_TO_DYE_COLOR.put(value, key));

        DYE_COLOR_TO_GLAZED_TERRACOTTA.put(DyeColor.WHITE, Material.WHITE_GLAZED_TERRACOTTA);
        DYE_COLOR_TO_GLAZED_TERRACOTTA.put(DyeColor.ORANGE, Material.ORANGE_GLAZED_TERRACOTTA);
        DYE_COLOR_TO_GLAZED_TERRACOTTA.put(DyeColor.MAGENTA, Material.MAGENTA_GLAZED_TERRACOTTA);
        DYE_COLOR_TO_GLAZED_TERRACOTTA.put(DyeColor.LIGHT_BLUE, Material.LIGHT_BLUE_GLAZED_TERRACOTTA);
        DYE_COLOR_TO_GLAZED_TERRACOTTA.put(DyeColor.YELLOW, Material.YELLOW_GLAZED_TERRACOTTA);
        DYE_COLOR_TO_GLAZED_TERRACOTTA.put(DyeColor.LIME, Material.LIME_GLAZED_TERRACOTTA);
        DYE_COLOR_TO_GLAZED_TERRACOTTA.put(DyeColor.PINK, Material.PINK_GLAZED_TERRACOTTA);
        DYE_COLOR_TO_GLAZED_TERRACOTTA.put(DyeColor.GRAY, Material.GRAY_GLAZED_TERRACOTTA);
        DYE_COLOR_TO_GLAZED_TERRACOTTA.put(DyeColor.LIGHT_GRAY, Material.LIGHT_GRAY_GLAZED_TERRACOTTA);
        DYE_COLOR_TO_GLAZED_TERRACOTTA.put(DyeColor.CYAN, Material.CYAN_GLAZED_TERRACOTTA);
        DYE_COLOR_TO_GLAZED_TERRACOTTA.put(DyeColor.PURPLE, Material.PURPLE_GLAZED_TERRACOTTA);
        DYE_COLOR_TO_GLAZED_TERRACOTTA.put(DyeColor.BLUE, Material.BLUE_GLAZED_TERRACOTTA);
        DYE_COLOR_TO_GLAZED_TERRACOTTA.put(DyeColor.BROWN, Material.BROWN_GLAZED_TERRACOTTA);
        DYE_COLOR_TO_GLAZED_TERRACOTTA.put(DyeColor.GREEN, Material.GREEN_GLAZED_TERRACOTTA);
        DYE_COLOR_TO_GLAZED_TERRACOTTA.put(DyeColor.RED, Material.RED_GLAZED_TERRACOTTA);
        DYE_COLOR_TO_GLAZED_TERRACOTTA.put(DyeColor.BLACK, Material.BLACK_GLAZED_TERRACOTTA);
        DYE_COLOR_TO_GLAZED_TERRACOTTA.forEach((key, value) -> MATERIAL_TO_DYE_COLOR.put(value, key));

        DYE_COLOR_TO_STAINED_GLASS.put(DyeColor.WHITE, Material.WHITE_STAINED_GLASS);
        DYE_COLOR_TO_STAINED_GLASS.put(DyeColor.ORANGE, Material.ORANGE_STAINED_GLASS);
        DYE_COLOR_TO_STAINED_GLASS.put(DyeColor.MAGENTA, Material.MAGENTA_STAINED_GLASS);
        DYE_COLOR_TO_STAINED_GLASS.put(DyeColor.LIGHT_BLUE, Material.LIGHT_BLUE_STAINED_GLASS);
        DYE_COLOR_TO_STAINED_GLASS.put(DyeColor.YELLOW, Material.YELLOW_STAINED_GLASS);
        DYE_COLOR_TO_STAINED_GLASS.put(DyeColor.LIME, Material.LIME_STAINED_GLASS);
        DYE_COLOR_TO_STAINED_GLASS.put(DyeColor.PINK, Material.PINK_STAINED_GLASS);
        DYE_COLOR_TO_STAINED_GLASS.put(DyeColor.GRAY, Material.GRAY_STAINED_GLASS);
        DYE_COLOR_TO_STAINED_GLASS.put(DyeColor.LIGHT_GRAY, Material.LIGHT_GRAY_STAINED_GLASS);
        DYE_COLOR_TO_STAINED_GLASS.put(DyeColor.CYAN, Material.CYAN_STAINED_GLASS);
        DYE_COLOR_TO_STAINED_GLASS.put(DyeColor.PURPLE, Material.PURPLE_STAINED_GLASS);
        DYE_COLOR_TO_STAINED_GLASS.put(DyeColor.BLUE, Material.BLUE_STAINED_GLASS);
        DYE_COLOR_TO_STAINED_GLASS.put(DyeColor.BROWN, Material.BROWN_STAINED_GLASS);
        DYE_COLOR_TO_STAINED_GLASS.put(DyeColor.GREEN, Material.GREEN_STAINED_GLASS);
        DYE_COLOR_TO_STAINED_GLASS.put(DyeColor.RED, Material.RED_STAINED_GLASS);
        DYE_COLOR_TO_STAINED_GLASS.put(DyeColor.BLACK, Material.BLACK_STAINED_GLASS);
        DYE_COLOR_TO_STAINED_GLASS.forEach((key, value) -> MATERIAL_TO_DYE_COLOR.put(value, key));

        DYE_COLOR_TO_STAINED_GLASS_PANE.put(DyeColor.WHITE, Material.WHITE_STAINED_GLASS_PANE);
        DYE_COLOR_TO_STAINED_GLASS_PANE.put(DyeColor.ORANGE, Material.ORANGE_STAINED_GLASS_PANE);
        DYE_COLOR_TO_STAINED_GLASS_PANE.put(DyeColor.MAGENTA, Material.MAGENTA_STAINED_GLASS_PANE);
        DYE_COLOR_TO_STAINED_GLASS_PANE.put(DyeColor.LIGHT_BLUE, Material.LIGHT_BLUE_STAINED_GLASS_PANE);
        DYE_COLOR_TO_STAINED_GLASS_PANE.put(DyeColor.YELLOW, Material.YELLOW_STAINED_GLASS_PANE);
        DYE_COLOR_TO_STAINED_GLASS_PANE.put(DyeColor.LIME, Material.LIME_STAINED_GLASS_PANE);
        DYE_COLOR_TO_STAINED_GLASS_PANE.put(DyeColor.PINK, Material.PINK_STAINED_GLASS_PANE);
        DYE_COLOR_TO_STAINED_GLASS_PANE.put(DyeColor.GRAY, Material.GRAY_STAINED_GLASS_PANE);
        DYE_COLOR_TO_STAINED_GLASS_PANE.put(DyeColor.LIGHT_GRAY, Material.LIGHT_GRAY_STAINED_GLASS_PANE);
        DYE_COLOR_TO_STAINED_GLASS_PANE.put(DyeColor.CYAN, Material.CYAN_STAINED_GLASS_PANE);
        DYE_COLOR_TO_STAINED_GLASS_PANE.put(DyeColor.PURPLE, Material.PURPLE_STAINED_GLASS_PANE);
        DYE_COLOR_TO_STAINED_GLASS_PANE.put(DyeColor.BLUE, Material.BLUE_STAINED_GLASS_PANE);
        DYE_COLOR_TO_STAINED_GLASS_PANE.put(DyeColor.BROWN, Material.BROWN_STAINED_GLASS_PANE);
        DYE_COLOR_TO_STAINED_GLASS_PANE.put(DyeColor.GREEN, Material.GREEN_STAINED_GLASS_PANE);
        DYE_COLOR_TO_STAINED_GLASS_PANE.put(DyeColor.RED, Material.RED_STAINED_GLASS_PANE);
        DYE_COLOR_TO_STAINED_GLASS_PANE.put(DyeColor.BLACK, Material.BLACK_STAINED_GLASS_PANE);
        DYE_COLOR_TO_STAINED_GLASS_PANE.forEach((key, value) -> MATERIAL_TO_DYE_COLOR.put(value, key));

        DYE_COLOR_TO_BED.put(DyeColor.WHITE, Material.WHITE_BED);
        DYE_COLOR_TO_BED.put(DyeColor.ORANGE, Material.ORANGE_BED);
        DYE_COLOR_TO_BED.put(DyeColor.MAGENTA, Material.MAGENTA_BED);
        DYE_COLOR_TO_BED.put(DyeColor.LIGHT_BLUE, Material.LIGHT_BLUE_BED);
        DYE_COLOR_TO_BED.put(DyeColor.YELLOW, Material.YELLOW_BED);
        DYE_COLOR_TO_BED.put(DyeColor.LIME, Material.LIME_BED);
        DYE_COLOR_TO_BED.put(DyeColor.PINK, Material.PINK_BED);
        DYE_COLOR_TO_BED.put(DyeColor.GRAY, Material.GRAY_BED);
        DYE_COLOR_TO_BED.put(DyeColor.LIGHT_GRAY, Material.LIGHT_GRAY_BED);
        DYE_COLOR_TO_BED.put(DyeColor.CYAN, Material.CYAN_BED);
        DYE_COLOR_TO_BED.put(DyeColor.PURPLE, Material.PURPLE_BED);
        DYE_COLOR_TO_BED.put(DyeColor.BLUE, Material.BLUE_BED);
        DYE_COLOR_TO_BED.put(DyeColor.BROWN, Material.BROWN_BED);
        DYE_COLOR_TO_BED.put(DyeColor.GREEN, Material.GREEN_BED);
        DYE_COLOR_TO_BED.put(DyeColor.RED, Material.RED_BED);
        DYE_COLOR_TO_BED.put(DyeColor.BLACK, Material.BLACK_BED);
        DYE_COLOR_TO_BED.forEach((key, value) -> MATERIAL_TO_DYE_COLOR.put(value, key));

        DYE_COLOR_TO_SHULKER_BOX.put(DyeColor.WHITE, Material.WHITE_SHULKER_BOX);
        DYE_COLOR_TO_SHULKER_BOX.put(DyeColor.ORANGE, Material.ORANGE_SHULKER_BOX);
        DYE_COLOR_TO_SHULKER_BOX.put(DyeColor.MAGENTA, Material.MAGENTA_SHULKER_BOX);
        DYE_COLOR_TO_SHULKER_BOX.put(DyeColor.LIGHT_BLUE, Material.LIGHT_BLUE_SHULKER_BOX);
        DYE_COLOR_TO_SHULKER_BOX.put(DyeColor.YELLOW, Material.YELLOW_SHULKER_BOX);
        DYE_COLOR_TO_SHULKER_BOX.put(DyeColor.LIME, Material.LIME_SHULKER_BOX);
        DYE_COLOR_TO_SHULKER_BOX.put(DyeColor.PINK, Material.PINK_SHULKER_BOX);
        DYE_COLOR_TO_SHULKER_BOX.put(DyeColor.GRAY, Material.GRAY_SHULKER_BOX);
        DYE_COLOR_TO_SHULKER_BOX.put(DyeColor.LIGHT_GRAY, Material.LIGHT_GRAY_SHULKER_BOX);
        DYE_COLOR_TO_SHULKER_BOX.put(DyeColor.CYAN, Material.CYAN_SHULKER_BOX);
        DYE_COLOR_TO_SHULKER_BOX.put(DyeColor.PURPLE, Material.PURPLE_SHULKER_BOX);
        DYE_COLOR_TO_SHULKER_BOX.put(DyeColor.BLUE, Material.BLUE_SHULKER_BOX);
        DYE_COLOR_TO_SHULKER_BOX.put(DyeColor.BROWN, Material.BROWN_SHULKER_BOX);
        DYE_COLOR_TO_SHULKER_BOX.put(DyeColor.GREEN, Material.GREEN_SHULKER_BOX);
        DYE_COLOR_TO_SHULKER_BOX.put(DyeColor.RED, Material.RED_SHULKER_BOX);
        DYE_COLOR_TO_SHULKER_BOX.put(DyeColor.BLACK, Material.BLACK_SHULKER_BOX);
        DYE_COLOR_TO_SHULKER_BOX.forEach((key, value) -> MATERIAL_TO_DYE_COLOR.put(value, key));

        ALL_SIGNS_INTERNAL.addAll(SIGNS_INTERNAL);
        ALL_SIGNS_INTERNAL.addAll(WALL_SIGNS_INTERNAL);
    }

    public static boolean isEmpty(ItemStack item) {
        return item == null || isAir(item.getType());
    }

    public static boolean isAir(Material m) {
        return AIRS_INTERNAL.contains(m);
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

    public static boolean isLog(Material m) {
        return LOGS_INTERNAL.contains(m);
    }

    public static boolean isLeaves(Material m) {
        return LEAVES_INTERNAL.contains(m);
    }

    public static boolean isPlanks(Material m) {
        return PLANKS_INTERNAL.contains(m);
    }

    public static boolean isSapling(Material m) {
        return SAPLINGS_INTERNAL.contains(m);
    }

    public static boolean isWoodenSlab(Material m) {
        return WOODEN_SLABS_INTERNAL.contains(m);
    }

    public static boolean isWoodenStairs(Material m) {
        return WOODEN_STAIRS_INTERNAL.contains(m);
    }

    public static boolean isWoodenFence(Material m) {
        return WOODEN_FENCES_INTERNAL.contains(m);
    }

    public static boolean isWoodenFenceGate(Material m) {
        return WOODEN_FENCE_GATES_INTERNAL.contains(m);
    }

    public static boolean isWoodenDoor(Material m) {
        return WOODEN_DOORS_INTERNAL.contains(m);
    }

    public static boolean isWoodenTrapdoor(Material m) {
        return WOODEN_TRAPDOORS_INTERNAL.contains(m);
    }

    public static boolean isWoodenButton(Material m) {
        return WOODEN_BUTTONS_INTERNAL.contains(m);
    }

    public static boolean isWoodenPressurePlate(Material m) {
        return WOODEN_PRESSURE_PLATES_INTERNAL.contains(m);
    }

    public static boolean isBoat(Material m) {
        return BOATS_INTERNAL.contains(m);
    }

    public static boolean isFenceGate(Material m) {
        return FENCE_GATES_INTERNAL.contains(m);
    }

    public static boolean isDoor(Material m) {
        return DOORS_INTERNAL.contains(m);
    }

    public static boolean isTrapdoor(Material m) {
        return TRAPDOORS_INTERNAL.contains(m);
    }

    public static boolean isButton(Material m) {
        return BUTTONS_INTERNAL.contains(m);
    }

    public static boolean isPressurePlate(Material m) {
        return PRESSURE_PLATES_INTERNAL.contains(m);
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

    public static boolean isFish(Material m) {
        return FISHES_INTERNAL.contains(m);
    }

    public static boolean isFishBucket(Material m) {
        return FISH_BUCKETS_INTERNAL.contains(m);
    }

    public static boolean isPottedPlant(Material m) {
        return POTTED_PLANTS_INTERNAL.contains(m);
    }

    public static boolean isMusicDisc(Material type) {
        return MUSIC_DISCS_INTERNAL.contains(type);
    }

    public static Material getDyeForDyeColor(DyeColor color) {
        return DYE_COLOR_TO_DYE.get(color);
    }

    public static Material getWoolForDyeColor(DyeColor color) {
        return DYE_COLOR_TO_WOOL.get(color);
    }

    public static Material getConcreteForDyeColor(DyeColor color) {
        return DYE_COLOR_TO_CONCRETE.get(color);
    }

    public static Material getConcretePowderForDyeColor(DyeColor color) {
        return DYE_COLOR_TO_CONCRETE_POWDER.get(color);
    }

    public static Material getTerracottaForDyeColor(DyeColor color) {
        return DYE_COLOR_TO_TERRACOTTA.get(color);
    }

    public static Material getGlazedTerracottaForDyeColor(DyeColor color) {
        return DYE_COLOR_TO_GLAZED_TERRACOTTA.get(color);
    }

    public static Material getStainedGlassForDyeColor(DyeColor color) {
        return DYE_COLOR_TO_STAINED_GLASS.get(color);
    }

    public static Material getStainedGlassPaneForDyeColor(DyeColor color) {
        return DYE_COLOR_TO_STAINED_GLASS_PANE.get(color);
    }

    public static Material getBedForDyeColor(DyeColor color) {
        return DYE_COLOR_TO_BED.get(color);
    }

    public static Material getShulkerBoxForDyeColor(DyeColor color) {
        return DYE_COLOR_TO_SHULKER_BOX.get(color);
    }

    public static DyeColor getDyeColorFor(Material colorable) {
        return MATERIAL_TO_DYE_COLOR.get(colorable);
    }
}
