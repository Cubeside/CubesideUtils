package de.iani.cubesideutils.items;

import java.util.ArrayList;
import java.util.Map;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.ItemMeta;

public class Enchantments {

    private Enchantments() {
        throw new UnsupportedOperationException("No instance for you, Sir!");
        // prevents instances
    }

    public static Map<Enchantment, Integer> getEnchants(ItemMeta meta) {
        return meta instanceof EnchantmentStorageMeta
                ? ((EnchantmentStorageMeta) meta).getStoredEnchants()
                : meta.getEnchants();
    }

    public static int getEnchantLevel(ItemMeta meta, Enchantment ench) {
        return meta instanceof EnchantmentStorageMeta
                ? ((EnchantmentStorageMeta) meta).getStoredEnchantLevel(ench)
                : meta.getEnchantLevel(ench);
    }

    public static boolean hasEnchant(ItemMeta meta, Enchantment ench) {
        return meta instanceof EnchantmentStorageMeta
                ? ((EnchantmentStorageMeta) meta).hasStoredEnchant(ench)
                : meta.hasEnchant(ench);
    }

    public static boolean hasConflictingEnchant(ItemMeta meta, Enchantment ench) {
        return meta instanceof EnchantmentStorageMeta
                ? ((EnchantmentStorageMeta) meta).hasConflictingStoredEnchant(ench)
                : meta.hasConflictingEnchant(ench);
    }


    public static void addEnchant(ItemMeta meta, Enchantment ench, int level, boolean ignoreLevelRestriction) {
        if (meta instanceof EnchantmentStorageMeta) {
            ((EnchantmentStorageMeta) meta).addStoredEnchant(ench, level, ignoreLevelRestriction);
        } else {
            meta.addEnchant(ench, level, ignoreLevelRestriction);
        }
    }

    public static void removeEnchant(ItemMeta meta, Enchantment ench) {
        if (meta instanceof EnchantmentStorageMeta) {
            ((EnchantmentStorageMeta) meta).removeStoredEnchant(ench);
        } else {
            meta.removeEnchant(ench);
        }
    }

    public static void clearEnchants(ItemMeta meta) {
        for (Enchantment ench: new ArrayList<>(getEnchants(meta).keySet())) {
            removeEnchant(meta, ench);
        }
    }

}
