package de.iani.cubesideutils.bukkit.items;

import io.papermc.paper.registry.RegistryAccess;
import io.papermc.paper.registry.RegistryKey;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.logging.Level;
import org.bukkit.Bukkit;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Villager;
import org.bukkit.inventory.ItemStack;

public class ItemGroups {

    private ItemGroups() {
        throw new UnsupportedOperationException("No instance for you, Sir!");
        // really prevents instances
    }

    private static final Set<Material> AIRS_INTERNAL = new HashSet<>();
    public static final Set<Material> AIRS = Collections.unmodifiableSet(AIRS_INTERNAL);

    // colorable
    private static final Set<Material> CONCRETE_POWDER_INTERNAL = new HashSet<>();
    private static final Set<Material> CONCRETE_INTERNAL = new HashSet<>();
    private static final Set<Material> WOOL_INTERNAL = new HashSet<>();
    private static final Set<Material> STAINED_GLASS_INTERNAL = new HashSet<>();
    private static final Set<Material> STAINED_GLASS_PANE_INTERNAL = new HashSet<>();
    private static final Set<Material> SHULKER_BOX_INTERNAL = new HashSet<>();
    private static final Set<Material> BED_INTERNAL = new HashSet<>();
    private static final Set<Material> CARPET_INTERNAL = new HashSet<>();
    private static final Set<Material> TERRACOTTA_INTERNAL = new HashSet<>();
    private static final Set<Material> GLAZED_TERRACOTTA_INTERNAL = new HashSet<>();
    private static final Set<Material> CANDLE_INTERNAL = new HashSet<>();
    private static final Set<Material> CANDLE_CAKE_INTERNAL = new HashSet<>();

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
    public static final Set<Material> CANDLE = Collections.unmodifiableSet(CANDLE_INTERNAL);
    public static final Set<Material> CANDLE_CAKE = Collections.unmodifiableSet(CANDLE_CAKE_INTERNAL);

    // wood-made
    private static final Set<Material> LOGS_INTERNAL = new HashSet<>();
    private static final Set<Material> LEAVES_INTERNAL = new HashSet<>();
    private static final Set<Material> PLANKS_INTERNAL = new HashSet<>();
    private static final Set<Material> SAPLINGS_INTERNAL = new HashSet<>();
    private static final Set<Material> WOODEN_SLABS_INTERNAL = new HashSet<>();
    private static final Set<Material> WOODEN_STAIRS_INTERNAL = new HashSet<>();
    private static final Set<Material> WOODEN_FENCES_INTERNAL = new HashSet<>();
    private static final Set<Material> WOODEN_FENCE_GATES_INTERNAL = new HashSet<>();
    private static final Set<Material> WOODEN_DOORS_INTERNAL = new HashSet<>();
    private static final Set<Material> WOODEN_TRAPDOORS_INTERNAL = new HashSet<>();
    private static final Set<Material> WOODEN_BUTTONS_INTERNAL = new HashSet<>();
    private static final Set<Material> WOODEN_PRESSURE_PLATES_INTERNAL = new HashSet<>();
    private static final Set<Material> BOATS_INTERNAL = new HashSet<>();

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
    private static final Set<Material> FENCE_GATES_INTERNAL = new HashSet<>();
    private static final Set<Material> DOORS_INTERNAL = new HashSet<>();
    private static final Set<Material> TRAPDOORS_INTERNAL = new HashSet<>();
    private static final Set<Material> BUTTONS_INTERNAL = new HashSet<>();
    private static final Set<Material> PRESSURE_PLATES_INTERNAL = new HashSet<>();

    public static final Set<Material> FENCE_GATES = Collections.unmodifiableSet(FENCE_GATES_INTERNAL);
    public static final Set<Material> DOORS = Collections.unmodifiableSet(DOORS_INTERNAL);
    public static final Set<Material> TRAPDOORS = Collections.unmodifiableSet(TRAPDOORS_INTERNAL);
    public static final Set<Material> BUTTONS = Collections.unmodifiableSet(BUTTONS_INTERNAL);
    public static final Set<Material> PRESSURE_PLATES = Collections.unmodifiableSet(PRESSURE_PLATES_INTERNAL);

    // weapons and tools
    private static final Set<Material> PICKAXES_INTERNAL = new HashSet<>();
    private static final Set<Material> AXES_INTERNAL = new HashSet<>();
    private static final Set<Material> SHOVELS_INTERNAL = new HashSet<>();
    private static final Set<Material> HOES_INTERNAL = new HashSet<>();
    private static final Set<Material> SWORDS_INTERNAL = new HashSet<>();
    private static final Set<Material> TOOLS_INTERNAL = new HashSet<>();
    private static final Set<Material> WEAPONS_INTERNAL = new HashSet<>();

    public static final Set<Material> PICKAXES = Collections.unmodifiableSet(PICKAXES_INTERNAL);
    public static final Set<Material> AXES = Collections.unmodifiableSet(AXES_INTERNAL);
    public static final Set<Material> SHOVELS = Collections.unmodifiableSet(SHOVELS_INTERNAL);
    public static final Set<Material> HOES = Collections.unmodifiableSet(HOES_INTERNAL);
    public static final Set<Material> SWORDS = Collections.unmodifiableSet(SWORDS_INTERNAL);
    public static final Set<Material> TOOLS = Collections.unmodifiableSet(TOOLS_INTERNAL);
    public static final Set<Material> WEAPONS = Collections.unmodifiableSet(WEAPONS_INTERNAL);

    private static final Set<Material> MULTI_BLOCK_PLANTS_INTERNAL = new HashSet<>();
    public static final Set<Material> MULTI_BLOCK_PLANTS = Collections.unmodifiableSet(MULTI_BLOCK_PLANTS_INTERNAL);

    private static final Set<Material> DOUBLE_BLOCK_PLANTS_INTERNAL = new HashSet<>();
    public static final Set<Material> DOUBLE_BLOCK_PLANTS = Collections.unmodifiableSet(DOUBLE_BLOCK_PLANTS_INTERNAL);

    private static final Set<Material> SINGLE_BLOCK_PLANTS_INTERNAL = new HashSet<>();
    public static final Set<Material> SINGLE_BLOCK_PLANTS = Collections.unmodifiableSet(SINGLE_BLOCK_PLANTS_INTERNAL);

    private static final Set<Material> PLANTS_INTERNAL = new HashSet<>();
    public static final Set<Material> PLANTS = Collections.unmodifiableSet(PLANTS_INTERNAL);

    private static final Set<Material> CROPS_INTERNAL = new HashSet<>();
    public static final Set<Material> CROPS = Collections.unmodifiableSet(CROPS_INTERNAL);

    private static final Set<Material> FISHES_INTERNAL = new HashSet<>();
    public static final Set<Material> FISHES = Collections.unmodifiableSet(FISHES_INTERNAL);

    private static final Set<Material> FISH_BUCKETS_INTERNAL = new HashSet<>();
    public static final Set<Material> FISH_BUCKETS = Collections.unmodifiableSet(FISH_BUCKETS_INTERNAL);

    private static final Set<Material> POTTED_PLANTS_INTERNAL = new HashSet<>();
    public static final Set<Material> POTTED_PLANTS = Collections.unmodifiableSet(POTTED_PLANTS_INTERNAL);

    private static final Set<Material> MUSIC_DISCS_INTERNAL = new HashSet<>();
    public static final Set<Material> MUSIC_DISCS = Collections.unmodifiableSet(MUSIC_DISCS_INTERNAL);

    private static final Set<Material> SKULLS_INTERNAL = new HashSet<>();
    public static final Set<Material> SKULLS = Collections.unmodifiableSet(SKULLS_INTERNAL);

    private static final HashMap<Material, EntityType> SPAWN_EGGS_INTERNAL = new HashMap<>();
    public static final Map<Material, EntityType> SPAWN_EGGS_MAP = Collections.unmodifiableMap(SPAWN_EGGS_INTERNAL);
    public static final Set<Material> SPAWN_EGGS = SPAWN_EGGS_MAP.keySet();

    private static final HashMap<EntityType, Material> SPAWNEGG_FOR_ENTITY_INTERNAL = new HashMap<>();
    public static final Map<EntityType, Material> SPAWNEGG_FOR_ENTITY = Collections.unmodifiableMap(SPAWNEGG_FOR_ENTITY_INTERNAL);

    private static final HashMap<Material, Villager.Profession> WORKSTATION_TO_VILLAGER_PROFESSION_INTERNAL = new HashMap<>();
    public static final Map<Material, Villager.Profession> WORKSTATION_TO_VILLAGER_PROFESSION = Collections.unmodifiableMap(WORKSTATION_TO_VILLAGER_PROFESSION_INTERNAL);

    private static final HashMap<Villager.Profession, Material> VILLAGER_PROFESSION_TO_WORKSTATION_INTERNAL = new HashMap<>();
    public static final Map<Villager.Profession, Material> VILLAGER_PROFESSION_TO_WORKSTATION = Collections.unmodifiableMap(VILLAGER_PROFESSION_TO_WORKSTATION_INTERNAL);

    private static final Map<DyeColor, Material> DYE_COLOR_TO_DYE = new HashMap<>();
    private static final Map<DyeColor, Material> DYE_COLOR_TO_WOOL = new HashMap<>();
    private static final Map<DyeColor, Material> DYE_COLOR_TO_CONCRETE = new HashMap<>();
    private static final Map<DyeColor, Material> DYE_COLOR_TO_CONCRETE_POWDER = new HashMap<>();
    private static final Map<DyeColor, Material> DYE_COLOR_TO_TERRACOTTA = new HashMap<>();
    private static final Map<DyeColor, Material> DYE_COLOR_TO_GLAZED_TERRACOTTA = new HashMap<>();
    private static final Map<DyeColor, Material> DYE_COLOR_TO_STAINED_GLASS = new HashMap<>();
    private static final Map<DyeColor, Material> DYE_COLOR_TO_STAINED_GLASS_PANE = new HashMap<>();
    private static final Map<DyeColor, Material> DYE_COLOR_TO_BED = new HashMap<>();
    private static final Map<DyeColor, Material> DYE_COLOR_TO_SHULKER_BOX = new HashMap<>();
    private static final Map<Material, DyeColor> MATERIAL_TO_DYE_COLOR = new HashMap<>();

    private static final Set<Material> DYES_INTERNAL = new HashSet<>();
    public static final Set<Material> DYES = Collections.unmodifiableSet(DYES_INTERNAL);

    private static final Set<Material> HANGING_SIGNS_INTERNAL = new HashSet<>();
    public static final Set<Material> HANGING_SIGNS = Collections.unmodifiableSet(HANGING_SIGNS_INTERNAL);
    private static final Set<Material> WALL_HANGING_SIGNS_INTERNAL = new HashSet<>();
    public static final Set<Material> WALL_HANGING_SIGNS = Collections.unmodifiableSet(WALL_HANGING_SIGNS_INTERNAL);

    private static final Set<Material> SIGNS_INTERNAL = new HashSet<>();
    public static final Set<Material> SIGNS = Collections.unmodifiableSet(SIGNS_INTERNAL);
    private static final Set<Material> WALL_SIGNS_INTERNAL = new HashSet<>();
    public static final Set<Material> WALL_SIGNS = Collections.unmodifiableSet(WALL_SIGNS_INTERNAL);
    private static final Set<Material> ALL_SIGNS_INTERNAL = new HashSet<>();
    public static final Set<Material> ALL_SIGNS = Collections.unmodifiableSet(ALL_SIGNS_INTERNAL);

    private static final Set<Material> CONTAINER_BLOCKS_INTERNAL = new HashSet<>();
    public static final Set<Material> CONTAINER_BLOCKS = Collections.unmodifiableSet(CONTAINER_BLOCKS_INTERNAL);

    private static final Set<Material> DYEABLE_ITEMS_INTERNAL = new HashSet<>();
    public static final Set<Material> DYEABLE_ITEMS = Collections.unmodifiableSet(DYEABLE_ITEMS_INTERNAL);
    private static final Collection<String> woodTypes;

    static {
        woodTypes = new ArrayList<>();
        woodTypes.add("OAK");
        woodTypes.add("SPRUCE");
        woodTypes.add("BIRCH");
        woodTypes.add("JUNGLE");
        woodTypes.add("DARK_OAK");
        woodTypes.add("ACACIA");
        woodTypes.add("CRIMSON");
        woodTypes.add("WARPED");
        woodTypes.add("MANGROVE");
        woodTypes.add("BAMBOO");
        woodTypes.add("CHERRY");

        AIRS_INTERNAL.add(Material.AIR);
        AIRS_INTERNAL.add(Material.CAVE_AIR);
        AIRS_INTERNAL.add(Material.VOID_AIR);

        for (EntityType e : RegistryAccess.registryAccess().getRegistry(RegistryKey.ENTITY_TYPE)) {
            NamespacedKey entityTypeKey = e.getKey();
            if (entityTypeKey != null) {
                NamespacedKey spawnEggKey = NamespacedKey.fromString(entityTypeKey.getNamespace() + ":" + entityTypeKey.getKey() + "_spawn_egg");
                Material spawnEgg = Registry.MATERIAL.get(spawnEggKey);
                if (spawnEgg != null) {
                    SPAWN_EGGS_INTERNAL.put(spawnEgg, e);
                }
            }
        }
        // SPAWN_EGGS_INTERNAL.put(Material.ZOMBIE_PIGMAN_SPAWN_EGG, EntityType.PIG_ZOMBIE); - removed in 1.16

        WORKSTATION_TO_VILLAGER_PROFESSION_INTERNAL.put(Material.BLAST_FURNACE, Villager.Profession.ARMORER);
        WORKSTATION_TO_VILLAGER_PROFESSION_INTERNAL.put(Material.SMOKER, Villager.Profession.BUTCHER);
        WORKSTATION_TO_VILLAGER_PROFESSION_INTERNAL.put(Material.CARTOGRAPHY_TABLE, Villager.Profession.CARTOGRAPHER);
        WORKSTATION_TO_VILLAGER_PROFESSION_INTERNAL.put(Material.BREWING_STAND, Villager.Profession.CLERIC);
        WORKSTATION_TO_VILLAGER_PROFESSION_INTERNAL.put(Material.COMPOSTER, Villager.Profession.FARMER);
        WORKSTATION_TO_VILLAGER_PROFESSION_INTERNAL.put(Material.BARREL, Villager.Profession.FISHERMAN);
        WORKSTATION_TO_VILLAGER_PROFESSION_INTERNAL.put(Material.CAULDRON, Villager.Profession.LEATHERWORKER);
        WORKSTATION_TO_VILLAGER_PROFESSION_INTERNAL.put(Material.STONECUTTER, Villager.Profession.MASON);
        WORKSTATION_TO_VILLAGER_PROFESSION_INTERNAL.put(Material.LOOM, Villager.Profession.SHEPHERD);
        WORKSTATION_TO_VILLAGER_PROFESSION_INTERNAL.put(Material.SMITHING_TABLE, Villager.Profession.TOOLSMITH);
        WORKSTATION_TO_VILLAGER_PROFESSION_INTERNAL.put(Material.GRINDSTONE, Villager.Profession.WEAPONSMITH);
        WORKSTATION_TO_VILLAGER_PROFESSION_INTERNAL.put(Material.FLETCHING_TABLE, Villager.Profession.FLETCHER);
        WORKSTATION_TO_VILLAGER_PROFESSION_INTERNAL.put(Material.LECTERN, Villager.Profession.LIBRARIAN);

        for (Entry<Material, Villager.Profession> e : WORKSTATION_TO_VILLAGER_PROFESSION_INTERNAL.entrySet()) {
            if (e.getValue() != null) {
                VILLAGER_PROFESSION_TO_WORKSTATION_INTERNAL.put(e.getValue(), e.getKey());
            }
        }

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
                } else if (name.endsWith("CANDLE")) {
                    CANDLE_INTERNAL.add(m);
                } else if (name.endsWith("CANDLE_CAKE")) {
                    CANDLE_CAKE_INTERNAL.add(m);
                } else if (name.endsWith("_LOG") || name.endsWith("_WOOD") || name.endsWith("_STEM") || name.endsWith("_HYPHAE")) {
                    LOGS_INTERNAL.add(m);
                } else if (name.endsWith("_LEAVES")) {
                    LEAVES_INTERNAL.add(m);
                } else if (name.endsWith("_PLANKS")) {
                    PLANKS_INTERNAL.add(m);
                } else if (name.endsWith("_SAPLING") && !name.startsWith("POTTED")) {
                    SAPLINGS_INTERNAL.add(m);
                } else if (name.endsWith("_SLAB") && isProbablyWood(m)) {
                    WOODEN_SLABS_INTERNAL.add(m);
                } else if (name.endsWith("_STAIRS") && isProbablyWood(m)) {
                    WOODEN_STAIRS_INTERNAL.add(m);
                } else if (name.endsWith("_FENCE") && isProbablyWood(m)) {
                    WOODEN_FENCES_INTERNAL.add(m);
                } else if (name.endsWith("_FENCE_GATE")) {
                    if (isProbablyWood(m)) {
                        WOODEN_FENCE_GATES_INTERNAL.add(m);
                    }
                    FENCE_GATES_INTERNAL.add(m);
                } else if (name.endsWith("_DOOR")) {
                    if (isProbablyWood(m)) {
                        WOODEN_DOORS_INTERNAL.add(m);
                    }
                    DOORS_INTERNAL.add(m);
                } else if (name.endsWith("_TRAPDOOR")) {
                    if (isProbablyWood(m)) {
                        WOODEN_TRAPDOORS_INTERNAL.add(m);
                    }
                    TRAPDOORS_INTERNAL.add(m);
                } else if (name.endsWith("_BUTTON")) {
                    if (isProbablyWood(m)) {
                        WOODEN_BUTTONS_INTERNAL.add(m);
                    }
                    BUTTONS_INTERNAL.add(m);
                } else if (name.endsWith("_PRESSURE_PLATE")) {
                    if (isProbablyWood(m)) {
                        WOODEN_PRESSURE_PLATES_INTERNAL.add(m);
                    }
                    PRESSURE_PLATES_INTERNAL.add(m);
                } else if (name.endsWith("_PICKAXE")) {
                    PICKAXES_INTERNAL.add(m);
                } else if (name.endsWith("_AXE")) {
                    AXES_INTERNAL.add(m);
                } else if (name.endsWith("_SHOVEL")) {
                    SHOVELS_INTERNAL.add(m);
                } else if (name.endsWith("_HOE")) {
                    HOES_INTERNAL.add(m);
                } else if (name.endsWith("_SWORD")) {
                    SWORDS_INTERNAL.add(m);
                } else if (name.endsWith("_WALL_SIGN")) {
                    WALL_SIGNS_INTERNAL.add(m);
                } else if (name.endsWith("_HANGING_SIGN")) {
                    HANGING_SIGNS_INTERNAL.add(m);
                } else if (name.endsWith("_WALL_HANGING_SIGN")) {
                    WALL_HANGING_SIGNS_INTERNAL.add(m);
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
                } else if (name.endsWith("_SKULL") || name.endsWith("_HEAD")) {
                    SKULLS_INTERNAL.add(m);
                }
            }

            for (Entry<Material, EntityType> e : SPAWN_EGGS_INTERNAL.entrySet()) {
                if (e.getValue() != null) {
                    SPAWNEGG_FOR_ENTITY_INTERNAL.put(e.getValue(), e.getKey());
                }
            }
        }

        // 1.19
        try {
            SAPLINGS_INTERNAL.add(Material.MANGROVE_PROPAGULE);
        } catch (NoSuchFieldError e) {
            Bukkit.getLogger().log(Level.INFO, "Some items could not be loaded into the ItemGroup");
        }

        TOOLS_INTERNAL.addAll(PICKAXES_INTERNAL);
        TOOLS_INTERNAL.addAll(AXES_INTERNAL);
        TOOLS_INTERNAL.addAll(SHOVELS_INTERNAL);
        TOOLS_INTERNAL.addAll(HOES_INTERNAL);
        TOOLS_INTERNAL.addAll(SWORDS_INTERNAL);
        TOOLS_INTERNAL.add(Material.FISHING_ROD);
        TOOLS_INTERNAL.add(Material.SHEARS);
        TOOLS_INTERNAL.add(Material.FLINT_AND_STEEL);
        // 1.19.4
        try {
            TOOLS_INTERNAL.add(Material.BRUSH);
        } catch (NoSuchFieldError e) {
            Bukkit.getLogger().log(Level.INFO, "Some items could not be loaded into the ItemGroup");
        }

        WEAPONS_INTERNAL.addAll(SWORDS_INTERNAL);
        WEAPONS_INTERNAL.addAll(AXES_INTERNAL);
        WEAPONS_INTERNAL.add(Material.TRIDENT);
        WEAPONS_INTERNAL.add(Material.BOW);
        WEAPONS_INTERNAL.add(Material.CROSSBOW);
        WEAPONS_INTERNAL.add(Material.SHIELD);

        MULTI_BLOCK_PLANTS_INTERNAL.add(Material.VINE);
        MULTI_BLOCK_PLANTS_INTERNAL.add(Material.SUGAR_CANE);
        MULTI_BLOCK_PLANTS_INTERNAL.add(Material.BAMBOO);
        MULTI_BLOCK_PLANTS_INTERNAL.add(Material.BAMBOO_SAPLING);
        MULTI_BLOCK_PLANTS_INTERNAL.add(Material.KELP_PLANT);
        MULTI_BLOCK_PLANTS_INTERNAL.add(Material.KELP);
        MULTI_BLOCK_PLANTS_INTERNAL.add(Material.TWISTING_VINES);
        MULTI_BLOCK_PLANTS_INTERNAL.add(Material.TWISTING_VINES_PLANT);
        MULTI_BLOCK_PLANTS_INTERNAL.add(Material.WEEPING_VINES);
        MULTI_BLOCK_PLANTS_INTERNAL.add(Material.WEEPING_VINES_PLANT);
        MULTI_BLOCK_PLANTS_INTERNAL.add(Material.CACTUS);
        try {
            MULTI_BLOCK_PLANTS_INTERNAL.add(Material.CAVE_VINES);
            MULTI_BLOCK_PLANTS_INTERNAL.add(Material.CAVE_VINES_PLANT);
            MULTI_BLOCK_PLANTS_INTERNAL.add(Material.BIG_DRIPLEAF);
            MULTI_BLOCK_PLANTS_INTERNAL.add(Material.BIG_DRIPLEAF_STEM);
        } catch (NoSuchFieldError e) {
            Bukkit.getLogger().log(Level.INFO, "Some items could not be loaded into the ItemGroup");
        }

        DOUBLE_BLOCK_PLANTS_INTERNAL.add(Material.LARGE_FERN);
        DOUBLE_BLOCK_PLANTS_INTERNAL.add(Material.TALL_GRASS);
        DOUBLE_BLOCK_PLANTS_INTERNAL.add(Material.ROSE_BUSH);
        DOUBLE_BLOCK_PLANTS_INTERNAL.add(Material.LILAC);
        DOUBLE_BLOCK_PLANTS_INTERNAL.add(Material.SUNFLOWER);
        DOUBLE_BLOCK_PLANTS_INTERNAL.add(Material.PEONY);
        DOUBLE_BLOCK_PLANTS_INTERNAL.add(Material.TALL_SEAGRASS);
        try {
            DOUBLE_BLOCK_PLANTS_INTERNAL.add(Material.SMALL_DRIPLEAF);
            DOUBLE_BLOCK_PLANTS_INTERNAL.add(Material.PITCHER_PLANT);
        } catch (NoSuchFieldError e) {
            Bukkit.getLogger().log(Level.INFO, "Some items could not be loaded into the ItemGroup");
        }

        CROPS_INTERNAL.add(Material.NETHER_WART);
        CROPS_INTERNAL.add(Material.WHEAT);
        CROPS_INTERNAL.add(Material.CARROTS);
        CROPS_INTERNAL.add(Material.POTATOES);
        CROPS_INTERNAL.add(Material.BEETROOTS);
        CROPS_INTERNAL.add(Material.PUMPKIN_STEM);
        CROPS_INTERNAL.add(Material.MELON_STEM);
        try {
            CROPS_INTERNAL.add(Material.TORCHFLOWER_CROP);
            CROPS_INTERNAL.add(Material.PITCHER_POD);
        } catch (NoSuchFieldError e) {
            Bukkit.getLogger().log(Level.INFO, "Some items could not be loaded into the ItemGroup");
        }

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
        try {
            SINGLE_BLOCK_PLANTS_INTERNAL.add(Material.SHORT_GRASS);
        } catch (NoSuchFieldError e) {
            SINGLE_BLOCK_PLANTS_INTERNAL.add(Material.valueOf("GRASS"));
        }
        SINGLE_BLOCK_PLANTS_INTERNAL.add(Material.FERN);
        SINGLE_BLOCK_PLANTS_INTERNAL.add(Material.DEAD_BUSH);
        SINGLE_BLOCK_PLANTS_INTERNAL.add(Material.SWEET_BERRY_BUSH);
        SINGLE_BLOCK_PLANTS_INTERNAL.add(Material.LILY_OF_THE_VALLEY);
        SINGLE_BLOCK_PLANTS_INTERNAL.add(Material.CORNFLOWER);
        SINGLE_BLOCK_PLANTS_INTERNAL.add(Material.WITHER_ROSE);
        SINGLE_BLOCK_PLANTS_INTERNAL.add(Material.SEAGRASS);
        SINGLE_BLOCK_PLANTS_INTERNAL.add(Material.RED_MUSHROOM);
        SINGLE_BLOCK_PLANTS_INTERNAL.add(Material.BROWN_MUSHROOM);
        SINGLE_BLOCK_PLANTS_INTERNAL.add(Material.CRIMSON_FUNGUS);
        SINGLE_BLOCK_PLANTS_INTERNAL.add(Material.WARPED_FUNGUS);
        SINGLE_BLOCK_PLANTS_INTERNAL.add(Material.CRIMSON_ROOTS);
        SINGLE_BLOCK_PLANTS_INTERNAL.add(Material.WARPED_ROOTS);
        SINGLE_BLOCK_PLANTS_INTERNAL.add(Material.NETHER_SPROUTS);
        try {
            SINGLE_BLOCK_PLANTS_INTERNAL.add(Material.AZALEA);
            SINGLE_BLOCK_PLANTS_INTERNAL.add(Material.FLOWERING_AZALEA);
            SINGLE_BLOCK_PLANTS_INTERNAL.add(Material.GLOW_LICHEN);
            SINGLE_BLOCK_PLANTS_INTERNAL.add(Material.MOSS_CARPET);
            SINGLE_BLOCK_PLANTS_INTERNAL.add(Material.MOSS_BLOCK);
            SINGLE_BLOCK_PLANTS_INTERNAL.add(Material.SPORE_BLOSSOM);
            SINGLE_BLOCK_PLANTS_INTERNAL.add(Material.HANGING_ROOTS);
            SINGLE_BLOCK_PLANTS_INTERNAL.add(Material.TORCHFLOWER);
            SINGLE_BLOCK_PLANTS_INTERNAL.add(Material.PINK_PETALS);
            SINGLE_BLOCK_PLANTS_INTERNAL.add(Material.PITCHER_CROP);
            SINGLE_BLOCK_PLANTS_INTERNAL.add(Material.GLOW_LICHEN);
            SINGLE_BLOCK_PLANTS_INTERNAL.add(Material.SCULK_VEIN);
            SINGLE_BLOCK_PLANTS_INTERNAL.add(Material.PALE_HANGING_MOSS);
            SINGLE_BLOCK_PLANTS_INTERNAL.add(Material.OPEN_EYEBLOSSOM);
            SINGLE_BLOCK_PLANTS_INTERNAL.add(Material.CLOSED_EYEBLOSSOM);
            SINGLE_BLOCK_PLANTS_INTERNAL.add(Material.BUSH);
            SINGLE_BLOCK_PLANTS_INTERNAL.add(Material.CACTUS_FLOWER);
            SINGLE_BLOCK_PLANTS_INTERNAL.add(Material.FIREFLY_BUSH);
            SINGLE_BLOCK_PLANTS_INTERNAL.add(Material.LEAF_LITTER);
            SINGLE_BLOCK_PLANTS_INTERNAL.add(Material.SHORT_DRY_GRASS);
            SINGLE_BLOCK_PLANTS_INTERNAL.add(Material.TALL_DRY_GRASS);
            SINGLE_BLOCK_PLANTS_INTERNAL.add(Material.WILDFLOWERS);
        } catch (NoSuchFieldError e) {
            Bukkit.getLogger().log(Level.INFO, "Some items could not be loaded into the ItemGroup");
        }
        SINGLE_BLOCK_PLANTS_INTERNAL.add(Material.LILY_PAD);
        SINGLE_BLOCK_PLANTS_INTERNAL.addAll(CROPS_INTERNAL);

        PLANTS_INTERNAL.addAll(SINGLE_BLOCK_PLANTS_INTERNAL);
        PLANTS_INTERNAL.addAll(DOUBLE_BLOCK_PLANTS_INTERNAL);
        PLANTS_INTERNAL.addAll(MULTI_BLOCK_PLANTS_INTERNAL);

        FISHES_INTERNAL.add(Material.COD);
        FISHES_INTERNAL.add(Material.SALMON);
        FISHES_INTERNAL.add(Material.PUFFERFISH);
        FISHES_INTERNAL.add(Material.TROPICAL_FISH);

        FISH_BUCKETS_INTERNAL.add(Material.COD_BUCKET);
        FISH_BUCKETS_INTERNAL.add(Material.SALMON_BUCKET);
        FISH_BUCKETS_INTERNAL.add(Material.PUFFERFISH_BUCKET);
        FISH_BUCKETS_INTERNAL.add(Material.TROPICAL_FISH_BUCKET);
        try {
            FISH_BUCKETS_INTERNAL.add(Material.AXOLOTL_BUCKET);
            FISH_BUCKETS_INTERNAL.add(Material.TADPOLE_BUCKET);
        } catch (NoSuchFieldError e) {
            Bukkit.getLogger().log(Level.INFO, "Some items could not be loaded into the ItemGroup");
        }

        DYE_COLOR_TO_DYE.put(DyeColor.WHITE, Material.WHITE_DYE);
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
        DYE_COLOR_TO_DYE.put(DyeColor.BLUE, Material.BLUE_DYE);
        DYE_COLOR_TO_DYE.put(DyeColor.BROWN, Material.BROWN_DYE);
        DYE_COLOR_TO_DYE.put(DyeColor.GREEN, Material.GREEN_DYE);
        DYE_COLOR_TO_DYE.put(DyeColor.RED, Material.RED_DYE);
        DYE_COLOR_TO_DYE.put(DyeColor.BLACK, Material.BLACK_DYE);
        DYE_COLOR_TO_DYE.forEach((key, value) -> MATERIAL_TO_DYE_COLOR.put(value, key));
        DYE_COLOR_TO_DYE.forEach((key, value) -> DYES_INTERNAL.add(value));

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
        ALL_SIGNS_INTERNAL.addAll(HANGING_SIGNS_INTERNAL);
        ALL_SIGNS_INTERNAL.addAll(WALL_HANGING_SIGNS_INTERNAL);

        CONTAINER_BLOCKS_INTERNAL.add(Material.CHEST);
        CONTAINER_BLOCKS_INTERNAL.add(Material.TRAPPED_CHEST);
        CONTAINER_BLOCKS_INTERNAL.add(Material.DISPENSER);
        CONTAINER_BLOCKS_INTERNAL.add(Material.DROPPER);
        CONTAINER_BLOCKS_INTERNAL.add(Material.HOPPER);
        CONTAINER_BLOCKS_INTERNAL.add(Material.BREWING_STAND);
        CONTAINER_BLOCKS_INTERNAL.add(Material.FURNACE);
        CONTAINER_BLOCKS_INTERNAL.addAll(SHULKER_BOX_INTERNAL);
        CONTAINER_BLOCKS_INTERNAL.add(Material.BARREL);
        CONTAINER_BLOCKS_INTERNAL.add(Material.BLAST_FURNACE);
        CONTAINER_BLOCKS_INTERNAL.add(Material.SMOKER);

        DYEABLE_ITEMS_INTERNAL.add(Material.LEATHER_HELMET);
        DYEABLE_ITEMS_INTERNAL.add(Material.LEATHER_CHESTPLATE);
        DYEABLE_ITEMS_INTERNAL.add(Material.LEATHER_LEGGINGS);
        DYEABLE_ITEMS_INTERNAL.add(Material.LEATHER_BOOTS);
        DYEABLE_ITEMS_INTERNAL.add(Material.LEATHER_HORSE_ARMOR);
        DYEABLE_ITEMS_INTERNAL.add(Material.WOLF_ARMOR);
    }

    /**
     * Returns true for all wood material, but might return true for Materials that are not wood, like CRIMSON_ROOTS
     */
    private static boolean isProbablyWood(Material m) {
        String name = m.name();
        for (String e : woodTypes) {
            if (name.startsWith(e)) {
                return true;
            }
        }
        return false;
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

    public static boolean isCandle(Material m) {
        return CANDLE_INTERNAL.contains(m);
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

    public static boolean isPickaxe(Material m) {
        return PICKAXES_INTERNAL.contains(m);
    }

    public static boolean isAxe(Material m) {
        return AXES_INTERNAL.contains(m);
    }

    public static boolean isShovel(Material m) {
        return SHOVELS_INTERNAL.contains(m);
    }

    public static boolean isHoe(Material m) {
        return HOES_INTERNAL.contains(m);
    }

    public static boolean isSword(Material m) {
        return SWORDS_INTERNAL.contains(m);
    }

    public static boolean isTool(Material m) {
        return TOOLS_INTERNAL.contains(m);
    }

    public static boolean isWeapon(Material m) {
        return WEAPONS_INTERNAL.contains(m);
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

    public static Material getSpawnEggForEntity(EntityType e) {
        return SPAWNEGG_FOR_ENTITY.get(e);
    }

    public static boolean isVillagerWorkstation(Material m) {
        return WORKSTATION_TO_VILLAGER_PROFESSION_INTERNAL.containsKey(m);
    }

    public static Villager.Profession getVillagerProfessionForWorkStation(Material m) {
        return WORKSTATION_TO_VILLAGER_PROFESSION_INTERNAL.get(m);
    }

    public static Material getWorkstationForVillagerProfession(Villager.Profession e) {
        return VILLAGER_PROFESSION_TO_WORKSTATION.get(e);
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

    public static boolean isSkull(Material type) {
        return SKULLS_INTERNAL.contains(type);
    }

    public static boolean isSign(Material m) {
        return ALL_SIGNS_INTERNAL.contains(m);
    }

    public static boolean isSignPost(Material m) {
        return SIGNS_INTERNAL.contains(m);
    }

    public static boolean isWallSign(Material m) {
        return WALL_SIGNS_INTERNAL.contains(m);
    }

    public static boolean isDye(Material m) {
        return DYES_INTERNAL.contains(m);
    }

    public static boolean isCandleCake(Material m) {
        return CANDLE_CAKE_INTERNAL.contains(m);
    }

    public static boolean isContainer(Material m) {
        return CONTAINER_BLOCKS_INTERNAL.contains(m);
    }

    public static boolean isDyeableItem(Material m) {
        return DYEABLE_ITEMS_INTERNAL.contains(m);
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
