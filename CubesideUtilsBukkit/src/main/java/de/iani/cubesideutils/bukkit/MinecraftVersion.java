package de.iani.cubesideutils.bukkit;

import java.util.logging.Level;
import org.bukkit.Bukkit;

public class MinecraftVersion {
    private MinecraftVersion() {
        throw new UnsupportedOperationException("No instance for you, Sir!");
        // prevents instances
    }

    public static final int MAJOR;
    public static final int MINOR;
    public static final int RELEASE;

    static {
        int majorVersion = -1;
        int minorVersion = -1;
        int releaseVersion = -1;
        try {
            String version = Bukkit.getVersion();
            int mcStart = version.indexOf("(MC: ");
            if (mcStart >= 0) {
                version = version.substring(mcStart + 5);
            }
            int mcEnd = version.indexOf(")");
            if (mcEnd >= 0) {
                version = version.substring(0, mcEnd);
            }
            String[] parts = version.split("\\.");
            majorVersion = parseSafeInt(parts[0]);
            minorVersion = 0;
            releaseVersion = 0;
            minorVersion = parseSafeInt(parts[1]);
            if (parts.length > 2) {
                releaseVersion = parseSafeInt(parts[2]);
            }
            // Bukkit.getLogger().info("Detected Minecraft Version: " + majorVersion + "." + minorVersion + "." + releaseVersion);
        } catch (Exception e) {
            Bukkit.getLogger().log(Level.SEVERE, "Could not parse minecraft version", e);
        }
        MAJOR = majorVersion;
        MINOR = minorVersion;
        RELEASE = releaseVersion;
    }

    private static int parseSafeInt(String s) {
        int len = s.length();
        if (len == 0) {
            return 0;
        }
        for (int i = 0; i < len; i++) {
            char c = s.charAt(i);
            if (c < '0' || c > '9') {
                if (i == 0) {
                    return 0;
                } else {
                    return Integer.parseInt(s.substring(0, i));
                }
            }
        }
        return Integer.parseInt(s);
    }

    public static boolean isAboveOrEqual(int major, int minor, int release) {
        return MAJOR > major || (MAJOR == major && (MINOR > minor || (MINOR == minor && RELEASE >= release)));
    }

    public static boolean isAbove(int major, int minor, int release) {
        return MAJOR > major || (MAJOR == major && (MINOR > minor || (MINOR == minor && RELEASE > release)));
    }

    public static boolean isEqual(int major, int minor, int release) {
        return MAJOR == major && MINOR == minor && RELEASE == release;
    }
}
