package de.iani.cubesideutils;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.DateFormat;
import java.text.ParseException;
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
import java.util.function.BiPredicate;
import java.util.function.ToIntFunction;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.DyeColor;
import org.bukkit.Location;

public class StringUtil {
    private StringUtil() {
        throw new UnsupportedOperationException("No instance for you, Sir!");
        // prevents instances
    }

    private static class BukkitStatic {
        private BukkitStatic() {
            throw new UnsupportedOperationException("No instance for you, Sir!");
            // prevents instances
        }

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

    }

    public static final ToIntFunction<String> CASE_IGNORING_HASHER = s -> {
        if (s == null) {
            return 0;
        }

        int hash = 0;
        for (int i = 0; i < s.length(); i++) {
            int c = s.charAt(i);
            hash = 31 * hash + Character.toLowerCase(c);
        }
        return hash;
    };

    public static final BiPredicate<String, String> CASE_IGNORING_EQUALITY = (s1, s2) -> s1 == null ? s2 == null : s1.equalsIgnoreCase(s2);

    public static final ToIntFunction<String> CASE_AND_COLORS_IGNORING_HASHER = s -> {
        if (s == null) {
            return 0;
        }

        int hash = 0;
        for (int i = 0; i < s.length(); i++) {
            int c = s.charAt(i);
            if (c == ChatColor.COLOR_CHAR && i + 1 < s.length() && ChatColor.getByChar(s.charAt(i + 1)) != null) {
                i++;
                continue;
            }
            hash = 31 * hash + Character.toLowerCase(c);
        }
        return hash;
    };

    public static final BiPredicate<String, String> CASE_AND_COLORS_IGNORING_EQUALITY = (s1, s2) -> {
        if (s2 == null) {
            return false;
        }

        s1 = ChatColor.stripColor(s1);
        s2 = ChatColor.stripColor(s2);
        return s1.equalsIgnoreCase(s2);
    };

    public static final Set<String> TRUE_STRINGS;
    public static final Set<String> FALSE_STRINGS;

    static {
        Set<String> trueStrings = Collections.newSetFromMap(new TreeMap<>(String.CASE_INSENSITIVE_ORDER));
        trueStrings.addAll(Arrays.asList("1", "true", "t", "ja", "j", "wahr", "w"));
        TRUE_STRINGS = Collections.unmodifiableSet(trueStrings);

        Set<String> falseStrings = Collections.newSetFromMap(new TreeMap<>(String.CASE_INSENSITIVE_ORDER));
        falseStrings.addAll(Arrays.asList("0", "false", "f", "nein", "n", "falsch"));
        FALSE_STRINGS = Collections.unmodifiableSet(falseStrings);
    }

    public static final String MC_INDENTION = net.md_5.bungee.api.ChatColor.RESET + " ";

    public static final Pattern SPACES_AND_UNDERSCORES_PATTERN = Pattern.compile("[\\ \\_]");

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

    public static String replaceLast(String in, String sequence, String replacement) {
        int index = in.lastIndexOf(sequence);
        if (index < 0) {
            return in;
        }

        return in.substring(0, index) + replacement + in.substring(index + sequence.length(), in.length());
    }

    public static String repeat(String arg, int times) {
        if (times <= 1) {
            if (times == 1) {
                return arg;
            }
            return "";
        }
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < times; i++) {
            builder.append(arg);
        }
        return builder.toString();
    }

    public static String mcIndent(int indention) {
        return repeat(MC_INDENTION, indention);
    }

    public static String exceptionToString(Throwable e) {
        StringWriter sw = new StringWriter();
        e.printStackTrace(new PrintWriter(sw));
        return sw.toString();
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

    private static final Pattern PIPE_PATTERN = Pattern.compile(" \\| ");
    private static final Pattern REMOVE_PIPE_PATTERN = Pattern.compile("\\|([^\\|])");

    public static Pair<String, String> splitAtPipe(String args) {
        Matcher matcher = PIPE_PATTERN.matcher(args);
        if (!matcher.find()) {
            return null;
        }

        int splitIndex = matcher.start();
        String first = args.substring(0, splitIndex);
        String second = args.substring(splitIndex + 3);

        first = REMOVE_PIPE_PATTERN.matcher(first).replaceAll("$1");
        second = REMOVE_PIPE_PATTERN.matcher(second).replaceAll("$1");

        return new Pair<>(first, second);
    }

    public static final Pattern AND_PATTERN = Pattern.compile("\\&");
    public static final Pattern COLOR_CHAR_PATTERN = Pattern.compile("\\" + net.md_5.bungee.api.ChatColor.COLOR_CHAR);
    public static final Pattern COLOR_CODES_PATTERN = Pattern.compile("\\" + net.md_5.bungee.api.ChatColor.COLOR_CHAR + "[" + Arrays.stream(net.md_5.bungee.api.ChatColor.values()).map(Object::toString).map(s -> s.substring(1)).collect(Collectors.joining()) + "]", Pattern.CASE_INSENSITIVE);

    public static String revertColors(String converted) {
        if (converted == null) {
            return null;
        }
        return COLOR_CHAR_PATTERN.matcher(AND_PATTERN.matcher(converted).replaceAll("&&")).replaceAll("&");
    }

    private static final Pattern ESCAPE_CHARACTER_PATTERN = Pattern.compile("\\\\.");

    public static String convertEscaped(String text) {
        Matcher matcher = ESCAPE_CHARACTER_PATTERN.matcher(text);
        StringBuffer buffer = null;

        while (matcher.find()) {
            if (buffer == null) {
                buffer = new StringBuffer();
            }

            char escaped = matcher.group().charAt(1);
            String replacement;
            switch (escaped) {
                case '\\':
                    replacement = "\\\\";
                    break;
                case 'n':
                    replacement = "\n";
                    break;
                default:
                    replacement = String.valueOf(escaped);
            }

            matcher.appendReplacement(buffer, replacement);
        }

        if (buffer == null) {
            return text;
        }

        matcher.appendTail(buffer);
        return buffer.toString();
    }

    // Line breaking

    public static List<String> breakLinesForMinecraft(String text, int lineLength) {
        return breakLines(text, lineLength, COLOR_CODES_PATTERN);
    }

    public static List<String> breakLinesForMinecraft(String text, int lineLength, boolean forceLineBreak) {
        return breakLines(text, lineLength, COLOR_CODES_PATTERN, forceLineBreak);
    }

    public static List<String> breakLinesForMinecraft(String text, int lineLength, boolean forceLineBreak, boolean preserveColorCodes) {
        return breakLines(text, lineLength, COLOR_CODES_PATTERN, forceLineBreak, preserveColorCodes);
    }

    public static List<String> breakLines(String text, int lineLength, Pattern ignoreForLength) {
        return breakLines(text, lineLength, ignoreForLength, false);
    }

    public static List<String> breakLines(String text, int lineLength, Pattern ignoreForLength, boolean forceLineBreak) {
        return breakLines(text, lineLength, ignoreForLength, forceLineBreak, ignoreForLength == COLOR_CODES_PATTERN);
    }

    // preserveColorCodes without ignoreForLength set to MATCH_COLOR_CODES may lead to strange results
    public static List<String> breakLines(String text, int lineLength, Pattern ignoreForLength, boolean forceLineBreak, boolean preserveColorCodes) {
        if (lineLength <= 0) {
            throw new IllegalArgumentException("lineLength must be positive");
        }

        List<String> result = new ArrayList<>();

        StringBuilder currentBuilder = new StringBuilder();
        char[] chars = text.toCharArray();
        int index = 0;
        int lastBlank = -1;
        int lastBreak = 0;

        boolean magic = false;
        boolean bold = false;
        boolean strikethrough = false;
        boolean underline = false;
        boolean italic = false;
        ChatColor color = null;
        int currentPrefixLength = 0;

        for (; index < chars.length;) {
            char current = chars[index];
            if (current == '\n') {
                index++;
                lastBreak = index;
                result.add(currentBuilder.toString());

                currentBuilder = new StringBuilder();
                if (preserveColorCodes) {
                    currentPrefixLength = addColorCodes(currentBuilder, magic, bold, strikethrough, underline, italic, color);
                }
                continue;
            } else if (current == ' ') {
                lastBlank = index;
            } else if (preserveColorCodes && current == ChatColor.COLOR_CHAR && index + 1 < chars.length) {
                char next = chars[index + 1];
                ChatColor col = ChatColor.getByChar(next);
                if (col != null) {
                    switch (col) {
                        case MAGIC:
                            magic = true;
                            break;
                        case BOLD:
                            bold = true;
                            break;
                        case STRIKETHROUGH:
                            strikethrough = true;
                            break;
                        case UNDERLINE:
                            underline = true;
                            break;
                        case ITALIC:
                            italic = true;
                            break;
                        default:
                            color = col == ChatColor.RESET ? null : col;
                            magic = false;
                            bold = false;
                            strikethrough = false;
                            underline = false;
                            italic = false;
                    }
                }
            }

            currentBuilder.append(current);
            if (tooLong(currentBuilder.toString(), lineLength, ignoreForLength)) {
                if (lastBlank > lastBreak) {
                    int start = lastBlank + currentPrefixLength - lastBreak;
                    int end = currentBuilder.length();
                    currentBuilder.delete(start, end);

                    result.add(currentBuilder.toString());
                    currentBuilder = new StringBuilder();

                    if (preserveColorCodes) {
                        currentPrefixLength = addColorCodes(currentBuilder, magic, bold, strikethrough, underline, italic, color);
                    }

                    index = lastBlank + 1;
                    lastBreak = index;
                    continue;
                } else if (forceLineBreak && index != lastBreak) {
                    currentBuilder.delete(currentBuilder.length() - 1, currentBuilder.length());

                    result.add(currentBuilder.toString());
                    currentBuilder = new StringBuilder();

                    if (preserveColorCodes) {
                        currentPrefixLength = addColorCodes(currentBuilder, magic, bold, strikethrough, underline, italic, color);
                    }

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

    private static int addColorCodes(StringBuilder builder, boolean magic, boolean bold, boolean strikethrough, boolean underline, boolean italic, ChatColor color) {
        int length = 0;
        if (color != null) {
            builder.append(color);
            length += 2;
        }
        if (magic) {
            builder.append(ChatColor.MAGIC);
            length += 2;
        }
        if (bold) {
            builder.append(ChatColor.BOLD);
            length += 2;
        }
        if (strikethrough) {
            builder.append(ChatColor.STRIKETHROUGH);
            length += 2;
        }
        if (underline) {
            builder.append(ChatColor.UNDERLINE);
            length += 2;
        }
        if (italic) {
            builder.append(ChatColor.ITALIC);
            length += 2;
        }
        return length;
    }

    // Safe file names

    private static final Set<Character> CHARS_ILLEGAL_IN_FILENAME;

    static {
        Set<Character> illegals = new HashSet<>();
        for (char c = 0x00; c <= 0x1F; c++) {
            illegals.add(c);
        }
        for (char c : new char[] { '|', '\\', '/', '?', '!', '*', '+', '%', '<', '>', '"', ':', ';', ',', '.', '=', '[', ']', '@', (char) 0x7F }) {
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
    public static final String TIMESTAMP_FORMAT_STRING = "yyyy-MM-dd HH:mm:ss";

    private static final DateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT_STRING);
    // private static final DateFormat timeFormat = new SimpleDateFormat(TIME_FORMAT_STRING);
    // private static final DateFormat timeSecondsFormat = new SimpleDateFormat(TIME_SECONDS_FORMAT_STRING);
    private static final DateFormat dateAndTimeFormat = new SimpleDateFormat(DATE_AND_TIME_FORMAT_STRING);
    private static final DateFormat dateAndTimeSecondsFormat = new SimpleDateFormat(DATE_AND_TIME_SECONDS_FORMAT_STRING);
    private static final DateFormat timestampFormat = new SimpleDateFormat(TIMESTAMP_FORMAT_STRING);

    public static String formatTimespan(long ms) {
        return formatTimespan(ms, "d", "h", "m", "s", "", "");
    }

    public static String formatTimespan(long ms, String d, String h, String m, String s, String delimiter, String lastDelimiter) {
        return formatTimespan(ms, d, h, m, s, delimiter, lastDelimiter, true);
    }

    public static String formatTimespan(long ms, String d, String h, String m, String s, String delimiter, String lastDelimiter, boolean dropAllLowerIfZero) {
        return formatTimespan(ms, d, h, m, s, delimiter, lastDelimiter, dropAllLowerIfZero, false);
    }

    public static String formatTimespan(long ms, String d, String h, String m, String s, String delimiter, String lastDelimiter, boolean dropAllLowerIfZero, boolean forceMinutesAndTwoDigitsForTime) {
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
                lessThanSecondsString = lessThanSecondsString.substring(lessThanSecondsString.indexOf('.') + 1);
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

    public static long parseTimespan(String arg) {
        return parseTimespan(arg, "d", "h", "m", "s", "");
    }

    public static long parseTimespan(String arg, String d, String h, String m, String s, String delimiter) {
        return parseTimespan(arg, d, h, m, s, delimiter, delimiter);
    }

    public static long parseTimespan(String arg, String d, String h, String m, String s, String delimiter1, String delimiter2) {
        String delimiterExpression = delimiter1.isEmpty() && delimiter2.isEmpty() ? "(\\z?)" : ("((\\z)" + (!delimiter1.isEmpty() ? "|(" + Pattern.quote(delimiter1) + ")" : "") + (!delimiter2.isEmpty() ? "|(" + Pattern.quote(delimiter2) + ")" : "") + ")");
        Pattern dayPattern = Pattern.compile("(?<days>(\\d+))" + Pattern.quote(d) + delimiterExpression, Pattern.CASE_INSENSITIVE);
        Pattern hourPattern = Pattern.compile("(?<hours>(\\d+))" + Pattern.quote(h) + delimiterExpression, Pattern.CASE_INSENSITIVE);
        Pattern minutePattern = Pattern.compile("(?<minutes>(\\d+))" + Pattern.quote(m) + delimiterExpression, Pattern.CASE_INSENSITIVE);
        Pattern secondPattern = Pattern.compile("(?<seconds>(\\d+(\\.\\d+)?))" + Pattern.quote(s) + delimiterExpression, Pattern.CASE_INSENSITIVE);

        long result = 0;

        Matcher dayMatcher = dayPattern.matcher(arg);
        if (dayMatcher.find()) {
            result += Long.parseLong(dayMatcher.group("days")) * 24L * 60L * 60L * 1000L;
        }

        Matcher hourMatcher = hourPattern.matcher(arg);
        if (hourMatcher.find()) {
            result += Long.parseLong(hourMatcher.group("hours")) * 60L * 60L * 1000L;
        }

        Matcher minuteMatcher = minutePattern.matcher(arg);
        if (minuteMatcher.find()) {
            result += Long.parseLong(minuteMatcher.group("minutes")) * 60L * 1000L;
        }

        Matcher secondMatcher = secondPattern.matcher(arg);
        if (secondMatcher.find()) {
            result += (long) (Double.parseDouble(secondMatcher.group("seconds")) * 1000d);
        }

        return result;

    }

    public static synchronized String formatDate(long date) {
        return formatDate(new Date(date));
    }

    public static synchronized String formatDate(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);

        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);
        int second = calendar.get(Calendar.SECOND);
        if (hour == 0 && minute == 0 && second == 0) {
            return dateFormat.format(date);
        }

        return (second == 0 ? dateAndTimeFormat.format(date) : dateAndTimeSecondsFormat.format(date)) + " Uhr";
    }

    public static synchronized String formatDate(long date, boolean displayTime, boolean displaySeconds) {
        return formatDate(new Date(date), displayTime, displaySeconds);
    }

    public static synchronized String formatDate(Date date, boolean displayTime, boolean displaySeconds) {
        if (!displayTime) {
            return dateFormat.format(date);
        }
        if (!displaySeconds) {
            return dateAndTimeFormat.format(date) + " Uhr";
        }
        return dateAndTimeSecondsFormat.format(date) + " Uhr";
    }

    public static synchronized Date parseDate(String arg) {
        if (arg.isEmpty()) {
            throw new IllegalArgumentException("empty String");
        }

        String[] args = arg.split(" ");
        if (args.length > 2) {
            throw new IllegalArgumentException("Only one space allowed.");
        }

        try {
            if (args.length == 1) {
                return dateFormat.parse(arg);
            }

            String[] args2 = args[1].split("\\:");
            if (args2.length < 2 || args2.length > 3) {
                throw new IllegalArgumentException("Only one or two colons allowed in time block.");
            }

            if (args2.length == 2) {
                return dateAndTimeFormat.parse(arg);
            }

            return dateAndTimeSecondsFormat.parse(arg);
        } catch (ParseException e) {
            throw new IllegalArgumentException(e);
        }
    }

    public static synchronized String formatTimestamp(long date) {
        return formatTimestamp(new Date(date));
    }

    public static synchronized String formatTimestamp(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return timestampFormat.format(date);
    }

    // Colors

    public static Set<Color> getConstantColors() {
        return BukkitStatic.CONSTANT_COLOR_NAMES.keySet();
    }

    public static Color getConstantColor(String name) {
        return BukkitStatic.CONSTANT_COLORS_BY_NAMES.get(name.toLowerCase());
    }

    public static String getConstantColorName(Color color) {
        return BukkitStatic.CONSTANT_COLOR_NAMES.get(color);
    }

    public static String toNiceString(Color color) {
        if (BukkitStatic.CONSTANT_COLOR_NAMES.containsKey(color)) {
            return BukkitStatic.CONSTANT_COLOR_NAMES.get(color);
        }

        double lowestDiff = Double.MAX_VALUE;
        String bestMatch = null;

        for (Color other : BukkitStatic.CONSTANT_COLOR_NAMES.keySet()) {
            double diff = diff(color, other);
            if (diff < lowestDiff) {
                lowestDiff = diff;
                bestMatch = BukkitStatic.CONSTANT_COLOR_NAMES.get(other);
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

    public static String flip(String s) {
        char[] data = s.toCharArray();
        de.iani.cubesideutils.collections.ArrayUtils.flip(data);
        return String.valueOf(data);
    }

}
