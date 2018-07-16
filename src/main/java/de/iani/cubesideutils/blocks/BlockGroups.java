package de.iani.cubesideutils.blocks;

import java.util.Collections;
import java.util.EnumSet;
import java.util.Set;

import org.bukkit.Material;

public class BlockGroups {
    private BlockGroups() {
        // prevents instances
    }

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
                }
            }
        }
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
}
