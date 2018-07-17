package de.iani.cubesideutils.items;

import java.util.HashMap;

import org.bukkit.ChatColor;
import org.bukkit.enchantments.Enchantment;

import de.iani.cubesideutils.StringUtil;

public class EnchantmentNames {
    private static final HashMap<Enchantment, String> enchantmentToName;
    static {
        enchantmentToName = new HashMap<Enchantment, String>();

        addEnchantment(Enchantment.ARROW_DAMAGE, "Power");
        addEnchantment(Enchantment.ARROW_FIRE, "Flame");
        addEnchantment(Enchantment.ARROW_INFINITE, "Infinity");
        addEnchantment(Enchantment.ARROW_KNOCKBACK, "Punch");
        addEnchantment(Enchantment.BINDING_CURSE, ChatColor.RED + "Curse of Binding");
        addEnchantment(Enchantment.DAMAGE_ALL, "Sharpness");
        addEnchantment(Enchantment.DAMAGE_ARTHROPODS, "Bane of Anthropods");
        addEnchantment(Enchantment.DAMAGE_UNDEAD, "Smite");
        addEnchantment(Enchantment.DIG_SPEED, "Efficiency");
        addEnchantment(Enchantment.DURABILITY, "Unbreaking");
        addEnchantment(Enchantment.LOOT_BONUS_BLOCKS, "Fortune");
        addEnchantment(Enchantment.LOOT_BONUS_MOBS, "Looting");
        addEnchantment(Enchantment.LUCK, "Luck of the Sea");
        addEnchantment(Enchantment.OXYGEN, "Respiration");
        addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, "Protection");
        addEnchantment(Enchantment.PROTECTION_EXPLOSIONS, "Blast Protection");
        addEnchantment(Enchantment.PROTECTION_FALL, "Feather Falling");
        addEnchantment(Enchantment.PROTECTION_FIRE, "Fire Protection");
        addEnchantment(Enchantment.PROTECTION_PROJECTILE, "Projectile Protection");
        addEnchantment(Enchantment.VANISHING_CURSE, ChatColor.RED + "Curse of Vanishing");
        addEnchantment(Enchantment.WATER_WORKER, "Aqua Affinity");
        addEnchantment(Enchantment.LOYALTY, "Loyalty");
        addEnchantment(Enchantment.IMPALING, "Impaling");
        addEnchantment(Enchantment.RIPTIDE, "Riptide");
        addEnchantment(Enchantment.CHANNELING, "Channeling");
    }

    private static void addEnchantment(Enchantment enchantment, String name) {
        enchantmentToName.put(enchantment, name);
    }

    public static String getName(Enchantment enchantment) {
        if (enchantment == null) {
            return null;
        }
        String name = enchantmentToName.get(enchantment);
        if (name != null) {
            return name;
        }
        return StringUtil.capitalizeFirstLetter(enchantment.getKey().getKey(), true);
    }
}
