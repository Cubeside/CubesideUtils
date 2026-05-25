package de.iani.cubesideutils.bukkit.items;

import de.iani.cubesideutils.ComponentUtilAdventure;
import de.iani.cubesideutils.StringUtil;
import de.iani.cubesideutils.collections.LinkedGeneralHashMap;
import io.papermc.paper.registry.RegistryAccess;
import io.papermc.paper.registry.RegistryKey;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.ItemMeta;

public class Enchantments {

    private Enchantments() {
        throw new UnsupportedOperationException("No instance for you, Sir!");
        // prevents instances
    }

    private static final Map<String, Enchantment> DESCRIPTION_TO_ENCHANTMENT;
    private static final Map<String, Enchantment> DESCRIPTION_WITH_UNDERSCORES_TO_ENCHANTMENT;
    static {
        Map<String, Enchantment> descriptionToEnchantment = new LinkedGeneralHashMap<>(StringUtil.CASE_IGNORING_HASHER, StringUtil.CASE_IGNORING_EQUALITY);
        Map<String, Enchantment> descriptionWithUnderscoresToEnchantment = new LinkedGeneralHashMap<>(StringUtil.CASE_IGNORING_HASHER, StringUtil.CASE_IGNORING_EQUALITY);
        for (Enchantment enchantment : RegistryAccess.registryAccess().getRegistry(RegistryKey.ENCHANTMENT)) {
            descriptionToEnchantment.put(ComponentUtilAdventure.plainText(enchantment.description()), enchantment);
            descriptionWithUnderscoresToEnchantment.put(ComponentUtilAdventure.plainText(enchantment.description()).replace(' ', '_'), enchantment);
        }
        DESCRIPTION_TO_ENCHANTMENT = Collections.unmodifiableMap(descriptionToEnchantment);
        DESCRIPTION_WITH_UNDERSCORES_TO_ENCHANTMENT = Collections.unmodifiableMap(descriptionWithUnderscoresToEnchantment);
    }

    @SuppressWarnings("deprecation")
    public static Enchantment matchEnchantment(String arg) {
        Enchantment result = getByKey(arg);
        if (result != null) {
            return result;
        }

        result = getByKey(arg.toLowerCase());
        if (result != null) {
            return result;
        }

        result = DESCRIPTION_WITH_UNDERSCORES_TO_ENCHANTMENT.get(arg);
        if (result != null) {
            return result;
        }

        result = DESCRIPTION_TO_ENCHANTMENT.get(arg);
        if (result != null) {
            return result;
        }

        result = EnchantmentNames.getByName(arg);
        if (result != null) {
            return result;
        }

        result = Enchantment.getByName(arg.toUpperCase());
        if (result != null) {
            return result;
        }

        return null;
    }

    public static Enchantment getByKey(String key) {
        return RegistryAccess.registryAccess().getRegistry(RegistryKey.ENCHANTMENT).get(NamespacedKey.fromString(key));
    }

    public static Enchantment getByKey(NamespacedKey key) {
        return RegistryAccess.registryAccess().getRegistry(RegistryKey.ENCHANTMENT).get(key);
    }

    public static Map<Enchantment, Integer> getEnchants(ItemMeta meta) {
        return meta instanceof EnchantmentStorageMeta ? ((EnchantmentStorageMeta) meta).getStoredEnchants() : meta.getEnchants();
    }

    public static int getEnchantLevel(ItemMeta meta, Enchantment ench) {
        return meta instanceof EnchantmentStorageMeta ? ((EnchantmentStorageMeta) meta).getStoredEnchantLevel(ench) : meta.getEnchantLevel(ench);
    }

    public static boolean hasEnchant(ItemMeta meta, Enchantment ench) {
        return meta instanceof EnchantmentStorageMeta ? ((EnchantmentStorageMeta) meta).hasStoredEnchant(ench) : meta.hasEnchant(ench);
    }

    public static boolean hasConflictingEnchant(ItemMeta meta, Enchantment ench) {
        return meta instanceof EnchantmentStorageMeta ? ((EnchantmentStorageMeta) meta).hasConflictingStoredEnchant(ench) : meta.hasConflictingEnchant(ench);
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
        for (Enchantment ench : new ArrayList<>(getEnchants(meta).keySet())) {
            removeEnchant(meta, ench);
        }
    }

}
