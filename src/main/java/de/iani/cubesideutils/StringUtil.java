package de.iani.cubesideutils;

import org.bukkit.Location;

public class StringUtil {
    /**
     * Capitalize the first letter of every word, lowercase everything else.
     *
     * @param s
     * @param replaceUnderscores
     *            If underscores should be replaced by spaces
     * @return
     */
    public static String capitalizeFirstLetter(String s, boolean replaceUnderscores) {
        char[] cap = s.toCharArray();
        boolean lastSpace = true;
        for (int i = 0; i < cap.length; i++) {
            char c = cap[i];
            if (c == '_') {
                if (replaceUnderscores) {
                    c = ' ';
                }
                lastSpace = true;
            } else if (c >= '0' && c <= '9' || c == '(' || c == ')') {
                lastSpace = true;
            } else {
                if (lastSpace) {
                    c = Character.toUpperCase(c);
                } else {
                    c = Character.toLowerCase(c);
                }
                lastSpace = false;
            }
            cap[i] = c;
        }
        return new String(cap);
    }

    public static String formatBlockLocationWithoutWorld(Location loc) {
        return loc.getBlockX() + "," + loc.getBlockY() + "," + loc.getBlockZ();
    }
}
