package de.iani.cubesideutils.bukkit;

import de.cubeside.connection.util.GlobalLocation;
import de.iani.cubesideutils.bukkit.plugin.CubesideUtilsBukkit;
import de.iani.cubesideutils.commands.ArgsParser;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.regex.Pattern;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.DyeColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

public class StringUtilBukkit {
    private StringUtilBukkit() {
        throw new UnsupportedOperationException("No instance for you, Sir!");
        // prevents instances
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
        registerColor(constantColorNames, constantColorsByNames, Color.GREEN, "green");
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

    // Locations

    public static class ParseException extends Exception {

        private static final long serialVersionUID = 4329390475601897805L;

        private CommandSender sender;

        public ParseException(CommandSender sender, String msg) {
            super(msg);
            this.sender = Objects.requireNonNull(sender);
        }

        public void sendMessage(String pluginPrefix, ChatColor color) {
            ChatUtilBukkit.sendMessage(sender, pluginPrefix, color.toString(), getMessage());
        }
    }

    public static Location getLocation(CommandSender sender, ArgsParser args, boolean noPitchOrYaw, boolean roundToBlock) throws ParseException {
        return getSafeLocation(sender, args, noPitchOrYaw, roundToBlock).getLocation();
    }

    public static GlobalLocation getSafeLocation(CommandSender sender, ArgsParser args, boolean noPitchOrYaw, boolean roundToBlock) throws ParseException {
        GlobalLocation result = null;

        String world;
        String serverName;
        String thisServerName = CubesideUtilsBukkit.getInstance().getGlobalDataHelper().getThisServerName();
        if (args.remaining() < 4) {
            if (sender instanceof Player) {
                Player player = (Player) sender;
                if (args.remaining() == 3) {
                    world = player.getWorld().getName();
                    serverName = thisServerName;
                } else if (args.hasNext()) {
                    throw new ParseException(sender, "Bitte gib die x-, y- und z-Koordinate des Orts an.");
                } else {
                    result = new GlobalLocation(player.getLocation());
                    world = result.getWorld();
                    serverName = result.getServer();
                }
            } else {
                throw new ParseException(sender, "Bitte gib die Welt und die x-, y- und z-Koordinate des Orts an.");
            }
        } else {
            if (args.remaining() < 5) {
                serverName = thisServerName;
            } else {
                serverName = args.next();
            }
            world = args.getNext();
            if (serverName.equalsIgnoreCase(thisServerName) && Bukkit.getWorld(world) == null) {
                throw new ParseException(sender, "Welt " + world + " nicht gefunden.");
            }
        }

        if (result == null) {
            double x, y, z;
            float pitch = 0.0f, yaw = 0.0f;
            try {
                x = Double.parseDouble(args.getNext());
                y = Double.parseDouble(args.getNext());
                z = Double.parseDouble(args.getNext());
            } catch (NumberFormatException e) {
                throw new ParseException(sender, "Bitte gib die x- y- und z-Koordinate des Orts als Kommazahlen (mit . statt ,) an.");
            }
            if (!noPitchOrYaw && args.remaining() > 1) {
                if (args.remaining() < 2) {
                    throw new ParseException(sender, "Bitte gib entweder nur x, y und z oder x, y, z, pitch und yaw an.");
                }
                try {
                    pitch = Float.parseFloat(args.getNext());
                    yaw = Float.parseFloat(args.getNext());
                } catch (NumberFormatException e) {
                    throw new ParseException(sender, "Bitte gib pitch und yaw des Orts als Kommazahlen (mit . statt ,) an.");
                }
            }
            result = new GlobalLocation(serverName, world, x, y, z, pitch, yaw);
        }

        if (roundToBlock) {
            result = new GlobalLocation(result.getServer(), result.getWorld(), result.getBlockX(), result.getBlockY(), result.getBlockZ(), 0.0f, 0.0f);
        } else if (noPitchOrYaw) {
            result = new GlobalLocation(result.getServer(), result.getWorld(), result.getX(), result.getY(), result.getZ(), 0.0f, 0.0f);
        }

        return result;
    }

    public static GlobalLocation roundLocation(GlobalLocation loc, int digits) {
        String server = loc.getServer();
        String world = loc.getWorld();
        double x = loc.getX();
        double y = loc.getY();
        double z = loc.getZ();
        float yaw = loc.getYaw();
        float pitch = loc.getPitch();

        double factor = Math.pow(10, digits);
        x = Math.round(x * factor) / factor;
        y = Math.round(y * factor) / factor;
        z = Math.round(z * factor) / factor;
        yaw = (float) (Math.round(yaw * factor) / factor);
        pitch = (float) (Math.round(pitch * factor) / factor);

        return new GlobalLocation(server, world, x, y, z, yaw, pitch);
    }

    public static Location roundLocation(Location loc, int digits) {
        World world = loc.getWorld();
        double x = loc.getX();
        double y = loc.getY();
        double z = loc.getZ();
        float yaw = loc.getYaw();
        float pitch = loc.getPitch();

        double factor = Math.pow(10, digits);
        x = Math.round(x * factor) / factor;
        y = Math.round(y * factor) / factor;
        z = Math.round(z * factor) / factor;
        yaw = (float) (Math.round(yaw * factor) / factor);
        pitch = (float) (Math.round(pitch * factor) / factor);

        return new Location(world, x, y, z, yaw, pitch);
    }

    public static String formatBlockLocationWithoutWorld(Location loc) {
        return loc.getBlockX() + "," + loc.getBlockY() + "," + loc.getBlockZ();
    }

    public static String formatLocation(Location location) {
        return formatLocation(location, null);
    }

    public static String formatLocation(Location location, Double tolerance) {
        if (location == null) {
            return formatLocation(null, 0, 0, 0, 0, 0);
        }
        return formatLocation(null, location.getWorld().getName(), location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch(), tolerance);
    }

    public static String formatLocation(GlobalLocation location) {
        return formatLocation(location, null);
    }

    public static String formatLocation(GlobalLocation location, Double tolerance) {
        if (location == null) {
            return formatLocation(null, 0, 0, 0, 0, 0);
        }
        return formatLocation(location.getServer(), location.getWorld(), location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch(), tolerance);
    }

    public static String formatLocation(String world, double x, double y, double z, float yaw, float pitch) {
        return formatLocation(world, x, y, z, yaw, pitch, null);
    }

    public static String formatLocation(String world, double x, double y, double z, float yaw, float pitch, Double tolerance) {
        return formatLocation(null, world, x, y, z, yaw, pitch, tolerance);
    }

    public static String formatLocation(String server, String world, double x, double y, double z, float yaw, float pitch) {
        return formatLocation(world, x, y, z, yaw, pitch, null);
    }

    public static String formatLocation(String server, String world, double x, double y, double z, float yaw, float pitch, Double tolerance) {
        if (world == null) {
            return "NULL";
        } else {
            String result = "";
            if (server != null) {
                result += "Server: " + server + " ";
            }
            result += "Welt: " + world + " x: " + x + " y: " + y + " z: " + z;
            if (yaw != 0 || pitch != 0) {
                result += " Yaw: " + yaw + " Pitch: " + pitch;
            }
            if (tolerance != null) {
                result += " ±" + tolerance;
            }
            return result;
        }
    }

}
