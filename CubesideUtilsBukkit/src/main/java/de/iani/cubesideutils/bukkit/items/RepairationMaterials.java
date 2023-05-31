package de.iani.cubesideutils.bukkit.items;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import org.bukkit.Material;

public class RepairationMaterials {

    private RepairationMaterials() {
        throw new UnsupportedOperationException("No instance for you, Sir!");
        // prevents instances
    }

    private static final Map<Material, Material> repairationMaterialsInternal = new HashMap<>();
    public static final Map<Material, Material> REPAIRATION_MATERIALS = Collections.unmodifiableMap(repairationMaterialsInternal);
    public static final Set<Material> REPAIRABLE_MATERIALS = Collections.unmodifiableSet(repairationMaterialsInternal.keySet());

    static {
        String[] toolSuffixes = new String[] { "AXE", "HOE", "PICKAXE", "SHOVEL", "SWORD" };
        String[] armorSuffixes = new String[] { "HELMET", "CHESTPLATE", "LEGGINGS", "BOOTS" };

        for (String s : toolSuffixes) {
            repairationMaterialsInternal.put(Material.valueOf("WOODEN_" + s), Material.OAK_PLANKS);
            repairationMaterialsInternal.put(Material.valueOf("STONE_" + s), Material.COBBLESTONE);
            repairationMaterialsInternal.put(Material.valueOf("GOLDEN_" + s), Material.GOLD_INGOT);
            repairationMaterialsInternal.put(Material.valueOf("IRON_" + s), Material.IRON_INGOT);
            repairationMaterialsInternal.put(Material.valueOf("DIAMOND_" + s), Material.DIAMOND);
            repairationMaterialsInternal.put(Material.valueOf("NETHERITE_" + s), Material.NETHERITE_INGOT);
        }

        for (String s : armorSuffixes) {
            repairationMaterialsInternal.put(Material.valueOf("LEATHER_" + s), Material.LEATHER);
            repairationMaterialsInternal.put(Material.valueOf("GOLDEN_" + s), Material.GOLD_INGOT);
            repairationMaterialsInternal.put(Material.valueOf("CHAINMAIL_" + s), Material.IRON_INGOT);
            repairationMaterialsInternal.put(Material.valueOf("IRON_" + s), Material.IRON_INGOT);
            repairationMaterialsInternal.put(Material.valueOf("DIAMOND_" + s), Material.DIAMOND);
            repairationMaterialsInternal.put(Material.valueOf("NETHERITE_" + s), Material.NETHERITE_INGOT);
        }

        repairationMaterialsInternal.put(Material.ELYTRA, Material.PHANTOM_MEMBRANE);

        for (Material mat : Material.values()) {
            if (mat.isLegacy()) {
                continue;
            }
            if (repairationMaterialsInternal.containsKey(mat)) {
                continue;
            }
            if (mat.getMaxDurability() <= 0) {
                continue;
            }

            repairationMaterialsInternal.put(mat, mat);
        }
    }

    public static boolean isRepairable(Material material) {
        return REPAIRABLE_MATERIALS.contains(material);
    }

    public static Material getRepairationMaterial(Material material) {
        return REPAIRATION_MATERIALS.get(material);
    }

    public static boolean isRepairableBy(Material material, Material by) {
        if (!isRepairable(material)) {
            return false;
        }
        if (material == by) {
            return true;
        }

        Material repairation = getRepairationMaterial(material);
        if (repairation == by) {
            return true;
        }
        if (repairation == Material.OAK_PLANKS) {
            return ItemGroups.isPlanks(by);
        }

        return false;
    }

}
