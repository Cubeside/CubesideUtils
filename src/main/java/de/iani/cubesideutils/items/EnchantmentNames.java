package de.iani.cubesideutils.items;

import de.iani.cubesideutils.StringUtil;
import de.iani.cubesideutils.StringUtil.BungeeStatics;
import java.util.HashMap;
import java.util.Map.Entry;
import org.bukkit.ChatColor;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;

public class EnchantmentNames {
    private EnchantmentNames() {
        throw new UnsupportedOperationException("No instance for you, Sir!");
        // prevents instances
    }

    private static final HashMap<Enchantment, String> enchantmentToName;
    static {
        enchantmentToName = new HashMap<>();

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

    public static Enchantment getByName(String name) {
        name = name.toLowerCase();

        Enchantment result = null;
        try {
            result = Enchantment.getByKey(NamespacedKey.minecraft(name));
        } catch (Exception e) {
            // ignore
        }
        if (result != null) {
            return result;
        }

        name = StringUtil.SPACES_AND_UNDERSCORES_PATTERN.matcher(name).replaceAll("");
        for (Entry<Enchantment, String> pair : enchantmentToName.entrySet()) {
            String simpleName = BungeeStatics.COLOR_CODES_PATTERN.matcher(pair.getValue()).replaceAll("");
            simpleName = StringUtil.SPACES_AND_UNDERSCORES_PATTERN.matcher(simpleName).replaceAll("");
            if (simpleName.equalsIgnoreCase(name)) {
                return pair.getKey();
            }
        }

        return null;
    }
}
