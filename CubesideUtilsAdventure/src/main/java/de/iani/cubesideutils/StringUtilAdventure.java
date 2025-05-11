package de.iani.cubesideutils;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.NavigableMap;
import java.util.Set;
import java.util.TreeMap;
import java.util.function.BiPredicate;
import java.util.function.ToIntFunction;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;

public class StringUtilAdventure {
    private StringUtilAdventure() {
        throw new UnsupportedOperationException("No instance for you, Sir!");
        // prevents instances
    }

    public static final Pattern COLOR_CHAR_PATTERN = Pattern.compile("\\" + LegacyComponentSerializer.SECTION_CHAR);
    public static final Pattern COLOR_CODES_PATTERN = Pattern.compile("\\" + LegacyComponentSerializer.SECTION_CHAR + "([0-9a-fk-or]|(x(" + LegacyComponentSerializer.SECTION_CHAR + "[0-9a-f]){6}))", Pattern.CASE_INSENSITIVE);

    public static final ToIntFunction<String> CASE_IGNORING_HASHER = StringUtilCore.CASE_IGNORING_HASHER;

    public static final BiPredicate<String, String> CASE_IGNORING_EQUALITY = StringUtilCore.CASE_IGNORING_EQUALITY;

    public static final ToIntFunction<String> CASE_AND_COLORS_IGNORING_HASHER = s -> {
        if (s == null) {
            return 0;
        }

        return CASE_IGNORING_HASHER.applyAsInt(stripColors(s, LegacyComponentSerializer.SECTION_CHAR));
    };

    public static final BiPredicate<String, String> CASE_AND_COLORS_IGNORING_EQUALITY = (s1, s2) -> {
        if (s1 == null || s2 == null) {
            return s1 == null && s2 == null;
        }

        s1 = stripColors(s1, LegacyComponentSerializer.SECTION_CHAR);
        s2 = stripColors(s2, LegacyComponentSerializer.SECTION_CHAR);
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

    public static boolean startsWithIgnoreCase(String arg, String prefix) {
        if (arg.length() < prefix.length()) {
            return false;
        }
        return arg.regionMatches(true, 0, prefix, 0, prefix.length());
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

    public static String indent(int indention) {
        return repeat(" ", indention);
    }

    @Deprecated
    public static String mcIndent(int indention) {
        return indent(indention);
    }

    public static boolean containsWord(String string, String word) {
        return Pattern.compile("\\b" + Pattern.quote(word) + "\\b").matcher(string).find();
    }

    public static String exceptionToString(Throwable e) {
        StringWriter sw = new StringWriter();
        e.printStackTrace(new PrintWriter(sw));
        return sw.toString();
    }

    public static String convertColors(String text) {
        return parseColors(text, '&', false);
    }

    public static String stripColors(String text) {
        return stripColors(text, '&');
    }

    public static String stripColors(String text, char colorChar) {
        return parseColors(text, colorChar, true);
    }

    private static String parseColors(String text, char colorChar, boolean remove) {
        if (text == null) {
            return null;
        }
        char[] asArray = text.toCharArray();
        StringBuilder builder = null;
        int len = text.length();
        for (int i = 0; i < len; i++) {
            char current = text.charAt(i);
            if (current == colorChar && i + 1 < len) {
                char next = text.charAt(i + 1);
                // if next is a "&" skip next char
                // if its a color char replace the "&"
                char candidate = Character.toLowerCase(next);
                if (('0' <= candidate && candidate <= '9') || ('a' <= candidate && candidate <= 'f') || ('k' <= candidate && candidate <= 'o') || candidate == colorChar || candidate == 'x') {
                    if (builder == null) {
                        builder = new StringBuilder();
                        builder.append(text, 0, i);
                    }
                    i++;
                    if (candidate != colorChar) {
                        if (candidate == 'x') {
                            TextColor hex;
                            if (colorChar == LegacyComponentSerializer.SECTION_CHAR) {
                                hex = parseHexColorConverted(asArray, i + 1);
                            } else {
                                hex = parseHexColor(text, i + 1);
                            }
                            if (hex == null) {
                                builder.append(current).append(next);
                            } else {
                                if (!remove) {
                                    builder.append(hex);
                                }
                                i += colorChar == LegacyComponentSerializer.SECTION_CHAR ? 12 : 6;
                            }
                        } else {
                            if (!remove) {
                                builder.append(LegacyComponentSerializer.SECTION_CHAR).append(next);
                            }
                        }
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

    public static TextColor parseHexColor(String text, int startIndex) {
        if (text.length() - startIndex < 6) {
            return null;
        }
        StringBuilder hexString = new StringBuilder("#");
        for (int i = 0; i < 6; i++) {
            char c = Character.toLowerCase(text.charAt(i + startIndex));
            if ((c < '0' || c > '9') && (c < 'a' || c > 'f')) {
                return null;
            }
            hexString.append(c);
        }
        return TextColor.fromCSSHexString(hexString.toString());
    }

    private static final Pattern PIPE_PATTERN = Pattern.compile("( |\\A)\\|( |\\Z)");
    private static final Pattern REMOVE_PIPE_PATTERN = Pattern.compile("\\|([^\\|])");

    public static Pair<String, String> splitAtPipe(String args) {
        Matcher matcher = PIPE_PATTERN.matcher(args);
        if (!matcher.find()) {
            return null;
        }

        String first = args.substring(0, matcher.start());
        String second = (matcher.end() >= args.length()) ? "" : args.substring(matcher.end());

        first = REMOVE_PIPE_PATTERN.matcher(first).replaceAll("$1");
        second = REMOVE_PIPE_PATTERN.matcher(second).replaceAll("$1");

        return new Pair<>(first, second);
    }

    public static String revertColors(String converted) {
        if (converted == null) {
            return null;
        }

        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < converted.length(); i++) {
            char c = converted.charAt(i);
            if (c == LegacyComponentSerializer.SECTION_CHAR) {
                if (converted.length() > i + 1 && converted.charAt(i + 1) == 'x') {
                    if (i + 14 > converted.length()) {
                        builder.append("&");
                        continue;
                    }
                    String hexString = converted.substring(i, i + 14);
                    if (!COLOR_CODES_PATTERN.matcher(hexString).matches()) {
                        builder.append("&");
                        continue;
                    }
                    builder.append("&").append(COLOR_CHAR_PATTERN.matcher(hexString).replaceAll(""));
                    i += 13;
                    continue;
                }
                builder.append("&");
            } else if (c == '&') {
                builder.append("&&");
            } else {
                builder.append(c);
            }
        }

        return builder.toString();
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

    public static String revertEscaped(String text) {
        text = text.replaceAll("\\\\", "\\\\\\\\");
        text = text.replaceAll("\\n", "\\\\n");
        return text;
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

    // preserveColorCodes without ignoreForLength set to COLOR_CODES_PATTERN may lead to strange results
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

        Style style = null;
        int ignoreColorsUntil = 0;
        int currentPrefixLength = 0;

        for (; index < chars.length;) {
            char current = chars[index];
            if (current == '\n') {
                index++;
                lastBreak = index;
                result.add(currentBuilder.toString());

                currentBuilder = new StringBuilder();
                if (preserveColorCodes) {
                    currentPrefixLength = addColorCodes(currentBuilder, style);
                }
                continue;
            } else if (current == ' ') {
                lastBlank = index;
            } else if (preserveColorCodes && current == LegacyComponentSerializer.SECTION_CHAR && index + 1 < chars.length && index >= ignoreColorsUntil) {
                char next = chars[index + 1];
                Style sty = null;

                if (next == 'r') {
                    style = null;
                } else if (next == 'x') {
                    sty = Style.style(parseHexColorConverted(chars, index + 2));
                    if (sty != null) {
                        ignoreColorsUntil = index + 2 + 12;
                    }
                    style = null;
                } else if (ComponentUtilAdventure.COLOR_CHARS.containsKey(next)) {
                    sty = Style.style(ComponentUtilAdventure.COLOR_CHARS.get(next));
                    style = null;
                } else if (ComponentUtilAdventure.DECORATION_CHARS.containsKey(next)) {
                    sty = Style.style(ComponentUtilAdventure.DECORATION_CHARS.get(next));
                }
                if (sty != null) {
                    style = (style == null ? sty : style.merge(sty));
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
                        currentPrefixLength = addColorCodes(currentBuilder, style);
                    }

                    index = lastBlank + 1;
                    lastBreak = index;
                    continue;
                } else if (forceLineBreak && index != lastBreak) {
                    currentBuilder.delete(currentBuilder.length() - 1, currentBuilder.length());

                    result.add(currentBuilder.toString());
                    currentBuilder = new StringBuilder();

                    if (preserveColorCodes) {
                        currentPrefixLength = addColorCodes(currentBuilder, style);
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

    private static TextColor parseHexColorConverted(char[] text, int startIndex) {
        if (text.length - startIndex < 12) {
            return null;
        }
        StringBuilder hexString = new StringBuilder("#");
        for (int i = 0; i < 6; i++) {
            if (text[(2 * i) + startIndex] != LegacyComponentSerializer.SECTION_CHAR) {
                return null;
            }
            char c = Character.toLowerCase(text[(2 * i) + 1 + startIndex]);
            if ((c < '0' || c > '9') && (c < 'a' || c > 'f')) {
                return null;
            }
            hexString.append(c);
        }
        return TextColor.fromCSSHexString(hexString.toString());
    }

    private static boolean tooLong(String string, int limit, Pattern ignoreForLength) {
        string = ignoreForLength == null ? string : ignoreForLength.matcher(string).replaceAll("");
        return string.length() > limit;
    }

    private static int addColorCodes(StringBuilder builder, Style style) {
        int length = 0;
        if (style == null) {
            return length;
        }
        String styleString = ComponentUtilAdventure.getLegacyComponentSerializer().serialize(Component.empty().style(style));
        builder.append(styleString);
        length += styleString.length();
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
        if (arg == 0) {
            return "0";
        }
        if (arg < 0) {
            return "-" + toRomanNumber(-arg);
        }
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

    public static String formatTimespanClassic(long ms) {
        return formatTimespan(ChronoUtil.roundTimespan(ms, ChronoUnit.SECONDS), "", "", "", "", ":", ":", false, true);
    }

    public static long parseTimespan(String arg) {
        return parseTimespan(arg, "d", "h", "m", "s", "");
    }

    public static long parseTimespan(String arg, String d, String h, String m, String s, String delimiter) {
        return parseTimespan(arg, d, h, m, s, delimiter, delimiter);
    }

    public static long parseTimespan(String arg, String d, String h, String m, String s, String delimiter1, String delimiter2) {
        String delimiterExpression = delimiter1.isEmpty() && delimiter2.isEmpty() ? "(\\Z??)" : ("((\\Z)" + (!delimiter1.isEmpty() ? "|(" + Pattern.quote(delimiter1) + ")" : "") + (!delimiter2.isEmpty() ? "|(" + Pattern.quote(delimiter2) + ")" : "") + ")");
        String dayExpression = "(?<days>(\\d+))" + Pattern.quote(d) + delimiterExpression;
        String hourExpression = "(?<hours>(\\d+))" + Pattern.quote(h) + delimiterExpression;
        String minuteExpression = "(?<minutes>(\\d+))" + Pattern.quote(m) + delimiterExpression;
        String secondExpression = "(?<seconds>(\\d+(\\.\\d+)?))" + Pattern.quote(s) + delimiterExpression;

        Pattern sanityPattern = Pattern.compile("\\A((" + dayExpression + ")?(" + hourExpression + ")?(" + minuteExpression + ")?(" + secondExpression + ")?)*\\Z", Pattern.CASE_INSENSITIVE);
        if (!sanityPattern.matcher(arg).matches()) {
            throw new IllegalArgumentException("Not a timespan.");
        }

        Pattern dayPattern = Pattern.compile(dayExpression, Pattern.CASE_INSENSITIVE);
        Pattern hourPattern = Pattern.compile(hourExpression, Pattern.CASE_INSENSITIVE);
        Pattern minutePattern = Pattern.compile(minuteExpression, Pattern.CASE_INSENSITIVE);
        Pattern secondPattern = Pattern.compile(secondExpression, Pattern.CASE_INSENSITIVE);

        long result = 0;

        Matcher dayMatcher = dayPattern.matcher(arg);
        while (dayMatcher.find()) {
            result += Long.parseLong(dayMatcher.group("days")) * 24L * 60L * 60L * 1000L;
        }

        Matcher hourMatcher = hourPattern.matcher(arg);
        while (hourMatcher.find()) {
            result += Long.parseLong(hourMatcher.group("hours")) * 60L * 60L * 1000L;
        }

        Matcher minuteMatcher = minutePattern.matcher(arg);
        while (minuteMatcher.find()) {
            result += Long.parseLong(minuteMatcher.group("minutes")) * 60L * 1000L;
        }

        Matcher secondMatcher = secondPattern.matcher(arg);
        while (secondMatcher.find()) {
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

    public static synchronized String formatDate(long date, boolean displayTime) {
        return formatDate(new Date(date), displayTime);
    }

    public static synchronized String formatDate(Date date, boolean displayTime) {
        return formatDate(date, displayTime, displayTime);
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
        arg = arg.replaceAll("_", " ");

        String[] args = arg.split(" ");
        if (args.length > 2) {
            throw new IllegalArgumentException("Only one space/underscore allowed.");
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

    public static String flip(String s) {
        char[] data = s.toCharArray();
        de.iani.cubesideutils.collections.ArrayUtils.flip(data);
        return String.valueOf(data);
    }

    public static String tryPlural(String of) {
        String lower = of.toLowerCase();
        if (lower.endsWith("sheep")) {
            return of;
        }
        if (lower.endsWith("us")) {
            return of.substring(0, of.length() - 2) + "i";
        }
        if (lower.endsWith("s")) {
            return of;
        }
        if (lower.endsWith("sh") || lower.endsWith("ch") || lower.endsWith("is") || lower.endsWith("x") || lower.endsWith("z") || lower.endsWith("o")) {
            return of + "es";
        }
        if (lower.endsWith("f") || lower.endsWith("fe")) {
            return of.substring(0, of.lastIndexOf('f')) + "ves";
        }
        if (lower.endsWith("y") && (lower.length() >= 2 && CharacterUtil.isConsonant(lower.charAt(lower.length() - 2)))) {
            return of.substring(0, of.length() - 1) + "ies";
        }
        if (lower.endsWith("man")) {
            return of.substring(0, of.length() - 2) + "en";
        }
        return of + "s";
    }

}
