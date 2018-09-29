package de.iani.cubesideutils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.NavigableMap;
import java.util.Set;
import java.util.TreeMap;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.DyeColor;
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

    // Line breaking

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

    // Safe file names

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

    // Roman numbers

    private static final NavigableMap<Integer, String> ROMAN_NUMBER_MAP;

    static {
        NavigableMap<Integer, String> romanNumberMap = new TreeMap<>();
        romanNumberMap.put(1000, "M");
        romanNumberMap.put(900, "CM");
        romanNumberMap.put(500, "D");
        romanNumberMap.put(400, "CD");
        romanNumberMap.put(100, "C");
        romanNumberMap.put(90, "XC");
        romanNumberMap.put(50, "L");
        romanNumberMap.put(40, "XL");
        romanNumberMap.put(10, "X");
        romanNumberMap.put(9, "IX");
        romanNumberMap.put(5, "V");
        romanNumberMap.put(4, "IV");
        romanNumberMap.put(1, "I");
        ROMAN_NUMBER_MAP = Collections.unmodifiableNavigableMap(romanNumberMap);
    }

    public static String toRomanNumber(int arg) {
        int i = ROMAN_NUMBER_MAP.floorKey(arg);
        if (arg == i) {
            return ROMAN_NUMBER_MAP.get(arg);
        }
        return ROMAN_NUMBER_MAP.get(i) + toRomanNumber(arg - i);
    }

    // Date and time formatting

    public static final String DATE_FORMAT_STRING = "dd.MM.yyyy";
    public static final String TIME_FORMAT_STRING = "HH:mm";
    public static final String TIME_SECONDS_FORMAT_STRING = "HH:mm:ss";
    public static final String DATE_AND_TIME_FORMAT_STRING = "dd.MM.yyyy HH:mm";
    public static final String DATE_AND_TIME_SECONDS_FORMAT_STRING = "dd.MM.yyyy HH:mm:ss";

    private static final DateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT_STRING);
    private static final DateFormat timeFormat = new SimpleDateFormat(TIME_FORMAT_STRING);
    private static final DateFormat timeSecondsFormat =
            new SimpleDateFormat(TIME_SECONDS_FORMAT_STRING);

    public static String formatTimespan(long ms) {
        return formatTimespan(ms, "d", "h", "m", "s", "", "");
    }

    public static String formatTimespan(long ms, String d, String h, String m, String s,
            String delimiter, String lastDelimiter) {
        return formatTimespan(ms, d, h, m, s, delimiter, lastDelimiter, true);
    }

    public static String formatTimespan(long ms, String d, String h, String m, String s,
            String delimiter, String lastDelimiter, boolean dropAllLowerIfZero) {
        return formatTimespan(ms, d, h, m, s, delimiter, lastDelimiter, dropAllLowerIfZero, false);
    }

    public static String formatTimespan(long ms, String d, String h, String m, String s,
            String delimiter, String lastDelimiter, boolean dropAllLowerIfZero,
            boolean forceMinutesAndTwoDigitsForTime) {
        long days = ms / (1000L * 60L * 60L * 24L);
        ms -= days * (1000L * 60L * 60L * 24L);
        long hours = ms / (1000L * 60L * 60L);
        ms -= hours * (1000L * 60L * 60L);
        long minutes = ms / (1000L * 60L);
        ms -= minutes * (1000L * 60L);
        long seconds = ms / 1000L;
        ms -= seconds * 1000L;
        double lessThanSeconds = (ms / 1000.0);

        StringBuilder builder = new StringBuilder();
        boolean first = true;
        boolean allNext = false;

        if (days != 0) {
            first = false;
            allNext = !dropAllLowerIfZero;

            builder.append(days);
            builder.append(d);
        }
        if (allNext || hours != 0) {
            if (!first) {
                if (allNext || minutes != 0 || seconds != 0 || lessThanSeconds != 0) {
                    builder.append(delimiter);
                } else {
                    builder.append(lastDelimiter);
                }
            }

            first = false;
            allNext = !dropAllLowerIfZero;

            if (forceMinutesAndTwoDigitsForTime && hours < 10) {
                builder.append('0');
            }
            builder.append(hours);
            builder.append(h);
        }
        if (allNext || forceMinutesAndTwoDigitsForTime || minutes != 0) {
            if (!first) {
                if (allNext || seconds != 0 || lessThanSeconds != 0) {
                    builder.append(delimiter);
                } else {
                    builder.append(lastDelimiter);
                }
            }

            first = false;
            allNext = !dropAllLowerIfZero;

            if (forceMinutesAndTwoDigitsForTime && minutes < 10) {
                builder.append('0');
            }
            builder.append(minutes);
            builder.append(m);
        }
        if (allNext || seconds != 0 || lessThanSeconds != 0) {
            if (!first) {
                builder.append(lastDelimiter);
            }

            first = false;
            allNext = !dropAllLowerIfZero;

            if (forceMinutesAndTwoDigitsForTime && seconds < 10) {
                builder.append('0');
            }
            builder.append(seconds);
            if (lessThanSeconds != 0) {
                builder.append(".");
                String lessThanSecondsString = "" + lessThanSeconds;
                lessThanSecondsString =
                        lessThanSecondsString.substring(lessThanSecondsString.indexOf('.') + 1);
                builder.append(lessThanSecondsString);
            }
            builder.append(s);
        }

        String result = builder.toString().trim();
        if (!result.equals("")) {
            return result;
        }

        return ("0" + s).trim();
    }

    public static synchronized String formatDate(long date) {
        return formatDate(new Date(date));
    }

    public static synchronized String formatDate(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);

        String result = dateFormat.format(date);

        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);
        int second = calendar.get(Calendar.SECOND);
        if (hour == 0 && minute == 0 && second == 0) {
            return result;
        }

        result += " " + (second == 0 ? timeFormat.format(date) : timeSecondsFormat.format(date))
                + " Uhr";
        return result;
    }

    // Colors

    private static final Map<Color, String> CONSTANT_COLORS;

    static {
        Map<Color, String> constantColors = new LinkedHashMap<>();
        constantColors.put(Color.AQUA, "aqua");
        constantColors.put(Color.BLACK, "black");
        constantColors.put(Color.BLUE, "blue");
        constantColors.put(Color.FUCHSIA, "fuchsia");
        constantColors.put(Color.GRAY, "gray");
        constantColors.put(Color.GREEN, "greeen");
        constantColors.put(Color.LIME, "lime");
        constantColors.put(Color.MAROON, "maroon");
        constantColors.put(Color.NAVY, "navy");
        constantColors.put(Color.OLIVE, "olive");
        constantColors.put(Color.ORANGE, "orange");
        constantColors.put(Color.PURPLE, "purple");
        constantColors.put(Color.RED, "Aqua");
        constantColors.put(Color.SILVER, "red");
        constantColors.put(Color.TEAL, "teal");
        constantColors.put(Color.WHITE, "white");
        constantColors.put(Color.YELLOW, "yellow");

        for (DyeColor dc : DyeColor.values()) {
            constantColors.put(dc.getColor(),
                    dc.name().replaceAll(Pattern.quote("_"), " ").toLowerCase());
        }

        CONSTANT_COLORS = Collections.unmodifiableMap(constantColors);
    }

    public static String toNiceString(Color color) {
        if (CONSTANT_COLORS.containsKey(color)) {
            return CONSTANT_COLORS.get(color);
        }

        double lowestDiff = Double.MAX_VALUE;
        String bestMatch = null;

        for (Color other : CONSTANT_COLORS.keySet()) {
            double diff = diff(color, other);
            if (diff < lowestDiff) {
                lowestDiff = diff;
                bestMatch = CONSTANT_COLORS.get(other);
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
        return Math.sqrt(
                Math.pow(c1.getRed() - c2.getRed(), 2) + Math.pow(c1.getBlue() - c2.getBlue(), 2)
                        + Math.pow(c1.getGreen() - c2.getGreen(), 2));
    }
}
