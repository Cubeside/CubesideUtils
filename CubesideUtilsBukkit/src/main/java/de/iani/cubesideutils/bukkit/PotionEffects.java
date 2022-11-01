package de.iani.cubesideutils.bukkit;

import de.iani.cubesideutils.StringUtil;
import de.iani.cubesideutils.bukkit.plugin.CubesideUtilsBukkit;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class PotionEffects {
    private PotionEffects() {
        throw new UnsupportedOperationException("No instance for you, Sir!");
        // prevents instances
    }

    private static final Map<PotionEffectType, Integer> typeToMax;
    static {
        typeToMax = new HashMap<>();

        addEffectType(PotionEffectType.ABSORPTION, 4);
        addEffectType(PotionEffectType.BAD_OMEN, 5);
        addEffectType(PotionEffectType.BLINDNESS, 1);
        addEffectType(PotionEffectType.CONDUIT_POWER, 1);
        addEffectType(PotionEffectType.CONFUSION, 1);
        addEffectType(PotionEffectType.DAMAGE_RESISTANCE, 2);
        addEffectType(PotionEffectType.DARKNESS, 1);
        addEffectType(PotionEffectType.DOLPHINS_GRACE, 1);
        addEffectType(PotionEffectType.FAST_DIGGING, 2);
        addEffectType(PotionEffectType.FIRE_RESISTANCE, 1);
        addEffectType(PotionEffectType.GLOWING, 1);
        addEffectType(PotionEffectType.HARM, 2);
        addEffectType(PotionEffectType.HEAL, 2);
        addEffectType(PotionEffectType.HEALTH_BOOST, 0);
        addEffectType(PotionEffectType.HERO_OF_THE_VILLAGE, 5);
        addEffectType(PotionEffectType.HUNGER, 1);
        addEffectType(PotionEffectType.INCREASE_DAMAGE, 2);
        addEffectType(PotionEffectType.INVISIBILITY, 1);
        addEffectType(PotionEffectType.JUMP, 2);
        addEffectType(PotionEffectType.LEVITATION, 1);
        addEffectType(PotionEffectType.LUCK, 0);
        addEffectType(PotionEffectType.NIGHT_VISION, 1);
        addEffectType(PotionEffectType.POISON, 2);
        addEffectType(PotionEffectType.REGENERATION, 2);
        addEffectType(PotionEffectType.SATURATION, 0);
        addEffectType(PotionEffectType.SLOW, 4);
        addEffectType(PotionEffectType.SLOW_DIGGING, 3);
        addEffectType(PotionEffectType.SLOW_FALLING, 1);
        addEffectType(PotionEffectType.SPEED, 2);
        addEffectType(PotionEffectType.UNLUCK, 0);
        addEffectType(PotionEffectType.WATER_BREATHING, 1);
        addEffectType(PotionEffectType.WEAKNESS, 2);
        addEffectType(PotionEffectType.WITHER, 2);
    }

    private static void addEffectType(PotionEffectType type, int maxAmplifier) {
        typeToMax.put(type, maxAmplifier - 1);
    }

    public static int getMaxAmplifier(PotionEffectType type) {
        int res = typeToMax.getOrDefault(type, -1);
        if (res == -1) {
            CubesideUtilsBukkit.getInstance().getLogger().log(Level.SEVERE, "No maximum level known for PotionEffectType " + type);
            res = 0;
        }
        return res;
    }

    public static String toNiceString(PotionEffect effect) {
        String result = StringUtil.capitalizeFirstLetter(effect.getType().getName(), true);
        if (effect.getAmplifier() != 0 || getMaxAmplifier(effect.getType()) != 0) {
            result += " " + StringUtil.toRomanNumber(effect.getAmplifier() + 1);
        }
        if (effect.isAmbient() || !effect.hasParticles() || !effect.hasIcon()) {
            result += " (";
            boolean first = true;
            if (effect.isAmbient()) {
                if (!first) {
                    result += ", ";
                }
                first = false;
                result += "ambient";
            }
            if (!effect.hasParticles()) {
                if (!first) {
                    result += ", ";
                }
                first = false;
                result += "no particles";
            }
            if (!effect.hasIcon()) {
                if (!first) {
                    result += ", ";
                }
                first = false;
                result += "no icon";
            }
            result += ")";
        }
        if (!effect.getType().isInstant()) {
            result += " for " + (effect.getDuration() == 0 ? "0 s" : StringUtil.formatTimespan(effect.getDuration() * 50, "d", "h", "m", "s", " ", " "));
        }
        return result;
    }

}
