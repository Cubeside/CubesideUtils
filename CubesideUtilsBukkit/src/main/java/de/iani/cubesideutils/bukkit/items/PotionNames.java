package de.iani.cubesideutils.bukkit.items;

import de.iani.cubesideutils.StringUtil;
import java.util.HashMap;
import java.util.Map;
import org.bukkit.potion.PotionType;

public class PotionNames {
    private PotionNames() {
        throw new UnsupportedOperationException("No instance for you, Sir!");
        // prevents instances
    }

    private static final Map<PotionType, String> potionToName;
    static {
        potionToName = new HashMap<>();

        addPotion(PotionType.AWKWARD, "Awkward Potion");
        addPotion(PotionType.FIRE_RESISTANCE, "Potion of Fire Resistance");
        addPotion(PotionType.HARMING, "Potion of Harming");
        addPotion(PotionType.HEALING, "Potion of Healing");
        addPotion(PotionType.INVISIBILITY, "Potion of Invisibility");
        addPotion(PotionType.LEAPING, "Potion of Leaping");
        addPotion(PotionType.LUCK, "Potion of Luck");
        addPotion(PotionType.MUNDANE, "Mundane Potion");
        addPotion(PotionType.NIGHT_VISION, "Potion of Night Vision");
        addPotion(PotionType.POISON, "Potion of Poison");
        addPotion(PotionType.REGENERATION, "Potion of Regeneration");
        addPotion(PotionType.SLOWNESS, "Potion of Slowness");
        addPotion(PotionType.SWIFTNESS, "Potion of Swiftness");
        addPotion(PotionType.STRENGTH, "Potion of Strength");
        addPotion(PotionType.THICK, "Thick Potion");
        addPotion(PotionType.WATER, "Water Bottle");
        addPotion(PotionType.WATER_BREATHING, "Potion of Water Breathing");
        addPotion(PotionType.WEAKNESS, "Potion of Weakness");
        addPotion(PotionType.TURTLE_MASTER, "Potion of the Turtle Master");
        addPotion(PotionType.SLOW_FALLING, "Potion of Slow Falling");
    }

    private static void addPotion(PotionType potion, String name) {
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
