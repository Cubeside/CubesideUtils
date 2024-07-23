package de.iani.cubesideutils.bukkit.items;

import de.iani.cubesideutils.StringUtil;
import io.papermc.paper.registry.RegistryAccess;
import io.papermc.paper.registry.RegistryKey;
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

        addEnchantment(Enchantment.POWER, "Power");
        addEnchantment(Enchantment.FLAME, "Flame");
        addEnchantment(Enchantment.INFINITY, "Infinity");
        addEnchantment(Enchantment.PUNCH, "Punch");
        addEnchantment(Enchantment.BINDING_CURSE, ChatColor.RED + "Curse of Binding");
        addEnchantment(Enchantment.SHARPNESS, "Sharpness");
        addEnchantment(Enchantment.BANE_OF_ARTHROPODS, "Bane of Anthropods");
        addEnchantment(Enchantment.SMITE, "Smite");
        addEnchantment(Enchantment.EFFICIENCY, "Efficiency");
        addEnchantment(Enchantment.UNBREAKING, "Unbreaking");
        addEnchantment(Enchantment.FORTUNE, "Fortune");
        addEnchantment(Enchantment.LOOTING, "Looting");
        addEnchantment(Enchantment.LUCK_OF_THE_SEA, "Luck of the Sea");
        addEnchantment(Enchantment.RESPIRATION, "Respiration");
        addEnchantment(Enchantment.PROTECTION, "Protection");
        addEnchantment(Enchantment.BLAST_PROTECTION, "Blast Protection");
        addEnchantment(Enchantment.FEATHER_FALLING, "Feather Falling");
        addEnchantment(Enchantment.FIRE_PROTECTION, "Fire Protection");
        addEnchantment(Enchantment.PROJECTILE_PROTECTION, "Projectile Protection");
        addEnchantment(Enchantment.VANISHING_CURSE, ChatColor.RED + "Curse of Vanishing");
        addEnchantment(Enchantment.AQUA_AFFINITY, "Aqua Affinity");
        addEnchantment(Enchantment.LOYALTY, "Loyalty");
        addEnchantment(Enchantment.IMPALING, "Impaling");
        addEnchantment(Enchantment.RIPTIDE, "Riptide");
        addEnchantment(Enchantment.CHANNELING, "Channeling");
        addEnchantment(Enchantment.SOUL_SPEED, "Soul Speed");
        addEnchantment(Enchantment.SWIFT_SNEAK, "Swift Sneak");
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
            result = RegistryAccess.registryAccess().getRegistry(RegistryKey.ENCHANTMENT).get(NamespacedKey.minecraft(name));
        } catch (Exception e) {
            // ignore
        }
        if (result != null) {
            return result;
        }

        name = StringUtil.SPACES_AND_UNDERSCORES_PATTERN.matcher(name).replaceAll("");
        for (Entry<Enchantment, String> pair : enchantmentToName.entrySet()) {
            String simpleName = StringUtil.COLOR_CODES_PATTERN.matcher(pair.getValue()).replaceAll("");
            simpleName = StringUtil.SPACES_AND_UNDERSCORES_PATTERN.matcher(simpleName).replaceAll("");
            if (simpleName.equalsIgnoreCase(name)) {
                return pair.getKey();
            }
        }

        return null;
    }

}
