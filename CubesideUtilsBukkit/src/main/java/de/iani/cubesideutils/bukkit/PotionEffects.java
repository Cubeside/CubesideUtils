package de.iani.cubesideutils.bukkit;

import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.Component.translatable;

import de.iani.cubesideutils.StringUtil;
import de.iani.cubesideutils.bukkit.plugin.CubesideUtilsBukkit;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import net.kyori.adventure.text.Component;
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
        addEffectType(PotionEffectType.NAUSEA, 1);
        addEffectType(PotionEffectType.RESISTANCE, 2);
        addEffectType(PotionEffectType.DARKNESS, 1);
        addEffectType(PotionEffectType.DOLPHINS_GRACE, 1);
        addEffectType(PotionEffectType.HASTE, 2);
        addEffectType(PotionEffectType.FIRE_RESISTANCE, 1);
        addEffectType(PotionEffectType.GLOWING, 1);
        addEffectType(PotionEffectType.INSTANT_DAMAGE, 2);
        addEffectType(PotionEffectType.INSTANT_HEALTH, 2);
        addEffectType(PotionEffectType.HEALTH_BOOST, 0);
        addEffectType(PotionEffectType.HERO_OF_THE_VILLAGE, 5);
        addEffectType(PotionEffectType.HUNGER, 1);
        addEffectType(PotionEffectType.STRENGTH, 2);
        addEffectType(PotionEffectType.INVISIBILITY, 1);
        addEffectType(PotionEffectType.JUMP_BOOST, 2);
        addEffectType(PotionEffectType.LEVITATION, 1);
        addEffectType(PotionEffectType.LUCK, 0);
        addEffectType(PotionEffectType.NIGHT_VISION, 1);
        addEffectType(PotionEffectType.POISON, 2);
        addEffectType(PotionEffectType.REGENERATION, 2);
        addEffectType(PotionEffectType.SATURATION, 0);
        addEffectType(PotionEffectType.SLOWNESS, 4);
        addEffectType(PotionEffectType.MINING_FATIGUE, 3);
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

    /**
     * @deprecated Needs component rewrite (effect.getType().translationKey())
     */
    @Deprecated
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

    /*
     * TODO: Translatable components for ambient, no particles, no icon, and duration?
     */
    public static Component toComponent(PotionEffect effect) {
        Component result = translatable(effect.getType().translationKey());

        // Amplifier (Roman numeral) when relevant
        if (effect.getAmplifier() != 0 || getMaxAmplifier(effect.getType()) != 0) {
            result = result.append(text(" " + StringUtil.toRomanNumber(effect.getAmplifier() + 1)));
        }

        // Flags
        if (effect.isAmbient() || !effect.hasParticles() || !effect.hasIcon()) {
            boolean first = true;
            StringBuilder flags = new StringBuilder(" (");
            if (effect.isAmbient()) {
                flags.append("ambient");
                first = false;
            }
            if (!effect.hasParticles()) {
                if (!first) {
                    flags.append(", ");
                }
                flags.append("no particles");
                first = false;
            }
            if (!effect.hasIcon()) {
                if (!first) {
                    flags.append(", ");
                }
                flags.append("no icon");
            }
            flags.append(")");
            result = result.append(text(flags.toString()));
        }

        // Duration (non-instant)
        if (!effect.getType().isInstant()) {
            String dur = (effect.getDuration() == 0) ? "0 s" : StringUtil.formatTimespan(effect.getDuration() * 50L, "d", "h", "m", "s", " ", " ");
            result = result.append(text(" for " + dur));
        }

        return result;
    }

}
