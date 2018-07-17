package de.iani.cubesideutils.items;

import java.util.HashMap;

import org.bukkit.potion.PotionType;

import de.iani.cubesideutils.StringUtil;

public class PotionNames {
    private static final HashMap<PotionType, String> potionToName;
    static {
        potionToName = new HashMap<PotionType, String>();

        addEnchantment(PotionType.AWKWARD, "Awkward Potion");
        addEnchantment(PotionType.FIRE_RESISTANCE, "Potion of Fire Resistance");
        addEnchantment(PotionType.INSTANT_DAMAGE, "Potion of Harming");
        addEnchantment(PotionType.INSTANT_HEAL, "Potion of Healing");
        addEnchantment(PotionType.INVISIBILITY, "Potion of Invisibility");
        addEnchantment(PotionType.JUMP, "Potion of Leaping");
        addEnchantment(PotionType.LUCK, "Potion of Luck");
        addEnchantment(PotionType.MUNDANE, "Mundane Potion");
        addEnchantment(PotionType.NIGHT_VISION, "Potion of Night Vision");
        addEnchantment(PotionType.POISON, "Potion of Poison");
        addEnchantment(PotionType.REGEN, "Potion of Regeneration");
        addEnchantment(PotionType.SLOWNESS, "Potion of Slowness");
        addEnchantment(PotionType.SPEED, "Potion of Swiftness");
        addEnchantment(PotionType.STRENGTH, "Potion of Strength");
        addEnchantment(PotionType.THICK, "Thick Potion");
        addEnchantment(PotionType.UNCRAFTABLE, "Uncraftable Potion");
        addEnchantment(PotionType.WATER, "Water Bottle");
        addEnchantment(PotionType.WATER_BREATHING, "Potion of Water Breathing");
        addEnchantment(PotionType.WEAKNESS, "Potion of Weakness");
        addEnchantment(PotionType.TURTLE_MASTER, "Potion of the Turtle Master");
        addEnchantment(PotionType.SLOW_FALLING, "Potion of Slow Falling");
    }

    private static void addEnchantment(PotionType potion, String name) {
        potionToName.put(potion, name);
    }

    public static String getName(PotionType potion) {
        if (potion == null) {
            return null;
        }
        String name = potionToName.get(potion);
        if (name != null) {
            return name;
        }
        return StringUtil.capitalizeFirstLetter(potion.name(), true);
    }
}
