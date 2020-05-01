package de.iani.cubesideutils.bukkit;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;
import org.bukkit.Color;
import org.bukkit.DyeColor;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;

public class StringUtilBukkit {
    private StringUtilBukkit() {
        throw new UnsupportedOperationException("No instance for you, Sir!");
        // prevents instances
    }

    public static String formatBlockLocationWithoutWorld(Location loc) {
        return loc.getBlockX() + "," + loc.getBlockY() + "," + loc.getBlockZ();
    }

    // Colors

    private static final Map<Color, String> CONSTANT_COLOR_NAMES;
    private static final Map<String, Color> CONSTANT_COLORS_BY_NAMES;

    static {
        Map<Color, String> constantColorNames = new LinkedHashMap<>();
        Map<String, Color> constantColorsByNames = new LinkedHashMap<>();

        registerColor(constantColorNames, constantColorsByNames, Color.AQUA, "aqua");
        registerColor(constantColorNames, constantColorsByNames, Color.BLACK, "black");
        registerColor(constantColorNames, constantColorsByNames, Color.BLUE, "blue");
        registerColor(constantColorNames, constantColorsByNames, Color.FUCHSIA, "fuchsia");
        registerColor(constantColorNames, constantColorsByNames, Color.GRAY, "gray");
        registerColor(constantColorNames, constantColorsByNames, Color.GREEN, "greeen");
        registerColor(constantColorNames, constantColorsByNames, Color.LIME, "lime");
        registerColor(constantColorNames, constantColorsByNames, Color.MAROON, "maroon");
        registerColor(constantColorNames, constantColorsByNames, Color.NAVY, "navy");
        registerColor(constantColorNames, constantColorsByNames, Color.OLIVE, "olive");
        registerColor(constantColorNames, constantColorsByNames, Color.ORANGE, "orange");
        registerColor(constantColorNames, constantColorsByNames, Color.PURPLE, "purple");
        registerColor(constantColorNames, constantColorsByNames, Color.RED, "red");
        registerColor(constantColorNames, constantColorsByNames, Color.SILVER, "silver");
        registerColor(constantColorNames, constantColorsByNames, Color.TEAL, "teal");
        registerColor(constantColorNames, constantColorsByNames, Color.WHITE, "white");
        registerColor(constantColorNames, constantColorsByNames, Color.YELLOW, "yellow");

        for (DyeColor dc : DyeColor.values()) {
            registerColor(constantColorNames, constantColorsByNames, dc.getColor(), dc.name().replaceAll(Pattern.quote("_"), " ").toLowerCase());
        }

        CONSTANT_COLOR_NAMES = Collections.unmodifiableMap(constantColorNames);
        CONSTANT_COLORS_BY_NAMES = Collections.unmodifiableMap(constantColorsByNames);
    }

    private static void registerColor(Map<Color, String> colorToName, Map<String, Color> nameToColor, Color color, String name) {
        colorToName.put(color, name);
        nameToColor.put(name, color);
    }

    public static Set<Color> getConstantColors() {
        return CONSTANT_COLOR_NAMES.keySet();
    }

    public static Color getConstantColor(String name) {
        return CONSTANT_COLORS_BY_NAMES.get(name.toLowerCase());
    }

    public static String getConstantColorName(Color color) {
        return CONSTANT_COLOR_NAMES.get(color);
    }

    public static String toNiceString(Color color) {
        if (CONSTANT_COLOR_NAMES.containsKey(color)) {
            return CONSTANT_COLOR_NAMES.get(color);
        }

        double lowestDiff = Double.MAX_VALUE;
        String bestMatch = null;

        for (Color other : CONSTANT_COLOR_NAMES.keySet()) {
            double diff = diff(color, other);
            if (diff < lowestDiff) {
                lowestDiff = diff;
                bestMatch = CONSTANT_COLOR_NAMES.get(other);
            }
        }

        String hexString = Integer.toHexString(color.asRGB()).toUpperCase();
        int zerosMissing = 6 - hexString.length();

        StringBuilder builder = new StringBuilder("roughly ");
        builder.append(bestMatch).append(" (#");
        for (int i = 0; i < zerosMissing; i++) {
            builder.append('0');
        }
        builder.append(hexString).append(")");

        return builder.toString();
    }

    private static double diff(Color c1, Color c2) {
        return Math.sqrt(Math.pow(c1.getRed() - c2.getRed(), 2) + Math.pow(c1.getBlue() - c2.getBlue(), 2) + Math.pow(c1.getGreen() - c2.getGreen(), 2));
    }

    private static final Pattern UNDERSCORE_AND_MINUS_PATTERN = Pattern.compile("[\\_\\-]");

    public static EntityType matchEntityType(String arg) {
        arg = arg.toUpperCase();
        try {
            return EntityType.valueOf(arg);
        } catch (IllegalArgumentException e) {
            // ignore
        }

        arg = UNDERSCORE_AND_MINUS_PATTERN.matcher(arg).replaceAll("");
        for (EntityType type : EntityType.values()) {
            if (UNDERSCORE_AND_MINUS_PATTERN.matcher(type.name().toUpperCase()).replaceAll("").equals(arg)) {
                return type;
            }
            if (UNDERSCORE_AND_MINUS_PATTERN.matcher(type.toString().toUpperCase()).replaceAll("").equals(arg)) {
                return type;
            }
        }

        return null;
    }

}
