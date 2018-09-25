package de.iani.cubesideutils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import org.bukkit.ChatColor;
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

    public static String convertColors(String text) {
        if (text == null) {
            return null;
        }
        StringBuilder builder = null;
        int len = text.length();
        for (int i = 0; i < len; i++) {
            char current = text.charAt(i);
            if (current == '&' && i + 1 < len) {
                char next = text.charAt(i + 1);
                // if next is a "&" skip next char
                // if its a color char replace the "&"
                if (ChatColor.getByChar(next) != null || next == '&') {
                    if (builder == null) {
                        builder = new StringBuilder();
                        builder.append(text, 0, i);
                    }
                    i++;
                    if (next != '&') {
                        builder.append(ChatColor.COLOR_CHAR).append(next);
                        continue;
                    }
                }
            }
            if (builder != null) {
                builder.append(current);
            }
        }
        return builder == null ? text : builder.toString();
    }

    public static final Pattern MATCH_COLOR_CODES =
            Pattern.compile(
                    "\\" + ChatColor.COLOR_CHAR + "["
                            + Arrays.stream(ChatColor.values()).map(ChatColor::getChar)
                                    .map(String::valueOf).collect(Collectors.joining())
                            + "]",
                    Pattern.CASE_INSENSITIVE);

    public static List<String> breakLinesForMinecraft(String text, int lineLength) {
        return breakLines(text, lineLength, MATCH_COLOR_CODES);
    }

    public static List<String> breakLinesForMinecraft(String text, int lineLength, boolean forceLineBreak) {
        return breakLines(text, lineLength, MATCH_COLOR_CODES, forceLineBreak);
    }

    public static List<String> breakLines(String text, int lineLength, Pattern ignoreForLength) {
        return breakLines(text, lineLength, ignoreForLength, false);
    }

    public static List<String> breakLines(String text, int lineLength, Pattern ignoreForLength,
            boolean forceLineBreak) {
        List<String> result = new ArrayList<>();

        StringBuilder currentBuilder = new StringBuilder();
        char[] chars = text.toCharArray();
        int index = 0;
        int lastBlank = -1;
        int lastBreak = 0;

        for (; index < chars.length;) {
            char current = chars[index];
            if (current == ' ') {
                lastBlank = index;
            }

            currentBuilder.append(current);
            if (tooLong(currentBuilder.toString(), lineLength, ignoreForLength)) {
                if (lastBlank > lastBreak) {
                    int start = lastBlank - lastBreak;
                    int end = currentBuilder.length();
                    currentBuilder.delete(start, end);

                    result.add(currentBuilder.toString());
                    currentBuilder = new StringBuilder();

                    index = lastBlank + 1;
                    lastBreak = index;
                    continue;
                } else if (forceLineBreak) {
                    currentBuilder.delete(currentBuilder.length() - 1, currentBuilder.length());

                    result.add(currentBuilder.toString());
                    currentBuilder = new StringBuilder();

                    lastBreak = index;
                    continue;
                }
            }

            index++;
        }

        if (currentBuilder.length() > 0) {
            result.add(currentBuilder.toString());
        }

        return result;
    }

    private static boolean tooLong(String string, int limit, Pattern ignoreForLength) {
        string = ignoreForLength == null ? string : ignoreForLength.matcher(string).replaceAll("");
        return string.length() > limit;
    }

    private static final Set<Character> CHARS_ILLEGAL_IN_FILENAME;

    static {
        Set<Character> illegals = new HashSet<>();
        for (char c = 0x00; c <= 0x1F; c++) {
            illegals.add(c);
        }
        for (char c : new char[] {'|', '\\', '/', '?', '!', '*', '+', '%', '<', '>', '"', ':', ';',
                ',', '.', '=', '[', ']', '@', (char) 0x7F}) {
            illegals.add(c);
        }
        CHARS_ILLEGAL_IN_FILENAME = Collections.unmodifiableSet(illegals);
    }

    public static boolean isSafeFilename(String name) {
        for (int i = 0; i < name.length(); i++) {
            char c = name.charAt(i);
            if (CHARS_ILLEGAL_IN_FILENAME.contains(c)) {
                return false;
            }
        }
        return true;
    }

    public static boolean isLegalCommandName(String name) {
        return !name.contains(" ");
    }
}
