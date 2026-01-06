package de.iani.cubesideutils;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import net.kyori.adventure.key.InvalidKeyException;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentLike;
import net.kyori.adventure.text.JoinConfiguration;
import net.kyori.adventure.text.KeybindComponent;
import net.kyori.adventure.text.ObjectComponent;
import net.kyori.adventure.text.ScoreComponent;
import net.kyori.adventure.text.SelectorComponent;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.TextReplacementConfig;
import net.kyori.adventure.text.TranslatableComponent;
import net.kyori.adventure.text.TranslationArgument;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.ClickEvent.Payload;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.event.HoverEvent.ShowEntity;
import net.kyori.adventure.text.event.HoverEvent.ShowItem;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.format.Style.Merge;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.format.TextDecoration.State;
import net.kyori.adventure.text.object.ObjectContents;
import net.kyori.adventure.text.object.PlayerHeadObjectContents;
import net.kyori.adventure.text.object.PlayerHeadObjectContents.ProfileProperty;
import net.kyori.adventure.text.object.SpriteObjectContents;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;

public class ComponentUtilAdventure {

    private static final LegacyComponentSerializer LEGACY_COMPONENT_SERIALIZER;
    static {
        LegacyComponentSerializer ser = null;
        try {
            ser = LegacyComponentSerializer.builder().character(LegacyComponentSerializer.SECTION_CHAR).extractUrls().useUnusualXRepeatedCharacterHexFormat().build();
        } catch (Throwable t) {
            ser = null;
        }
        LEGACY_COMPONENT_SERIALIZER = ser;
    }

    private static final PlainTextComponentSerializer PLAIN_TEXT_COMPONENT_SERIALIZER = PlainTextComponentSerializer.plainText();

    public static final Comparator<Component> TEXT_ONLY_ORDER = (c1, c2) -> plainText(c1).compareTo(plainText(c2));
    public static final Comparator<Component> TEXT_ONLY_CASE_INSENSITIVE_ORDER = (c1, c2) -> String.CASE_INSENSITIVE_ORDER.compare(plainText(c1), plainText(c2));

    public static final Map<Character, NamedTextColor> COLOR_CHARS;
    public static final Map<NamedTextColor, Character> COLOR_CHARS_INVERSE;
    static {
        Map<Character, NamedTextColor> colorChars = new LinkedHashMap<>();
        colorChars.put('0', NamedTextColor.BLACK);
        colorChars.put('1', NamedTextColor.DARK_BLUE);
        colorChars.put('2', NamedTextColor.DARK_GREEN);
        colorChars.put('3', NamedTextColor.DARK_AQUA);
        colorChars.put('4', NamedTextColor.DARK_RED);
        colorChars.put('5', NamedTextColor.DARK_PURPLE);
        colorChars.put('6', NamedTextColor.GOLD);
        colorChars.put('7', NamedTextColor.GRAY);
        colorChars.put('8', NamedTextColor.DARK_GRAY);
        colorChars.put('9', NamedTextColor.BLUE);
        colorChars.put('a', NamedTextColor.GREEN);
        colorChars.put('b', NamedTextColor.AQUA);
        colorChars.put('c', NamedTextColor.RED);
        colorChars.put('d', NamedTextColor.LIGHT_PURPLE);
        colorChars.put('e', NamedTextColor.YELLOW);
        colorChars.put('f', NamedTextColor.WHITE);
        COLOR_CHARS = Collections.unmodifiableMap(colorChars);

        Map<NamedTextColor, Character> colorCharsInverse = new LinkedHashMap<>();
        for (Entry<Character, NamedTextColor> entry : COLOR_CHARS.entrySet()) {
            colorCharsInverse.put(entry.getValue(), entry.getKey());
        }
        COLOR_CHARS_INVERSE = Collections.unmodifiableMap(colorCharsInverse);
    }

    public static final Map<Character, TextDecoration> DECORATION_CHARS;
    public static final Map<TextDecoration, Character> DECORATION_CHARS_INVERSE;
    static {
        Map<Character, TextDecoration> decorationChars = new LinkedHashMap<>();
        decorationChars.put('k', TextDecoration.OBFUSCATED);
        decorationChars.put('l', TextDecoration.BOLD);
        decorationChars.put('m', TextDecoration.STRIKETHROUGH);
        decorationChars.put('n', TextDecoration.UNDERLINED);
        decorationChars.put('o', TextDecoration.ITALIC);
        DECORATION_CHARS = Collections.unmodifiableMap(decorationChars);

        Map<TextDecoration, Character> decorationCharsInverse = new LinkedHashMap<>();
        for (Entry<Character, TextDecoration> entry : DECORATION_CHARS.entrySet()) {
            decorationCharsInverse.put(entry.getValue(), entry.getKey());
        }
        DECORATION_CHARS_INVERSE = Collections.unmodifiableMap(decorationCharsInverse);
    }

    private enum FormatRetention {
        NONE(Merge.merges()),
        FORMATTING(Merge.merges(Merge.COLOR, Merge.DECORATIONS, Merge.FONT)),
        EVENTS(Merge.merges(Merge.EVENTS, Merge.INSERTION)),
        ALL(Merge.all());

        public final Set<Merge> MERGES;

        private FormatRetention(Set<Merge> merges) {
            this.MERGES = merges;
        }
    }

    private ComponentUtilAdventure() {
        throw new UnsupportedOperationException("No instance for you, Sir!");
        // prevents instances
    }

    public static LegacyComponentSerializer getLegacyComponentSerializer() {
        return LEGACY_COMPONENT_SERIALIZER;
    }

    public static PlainTextComponentSerializer getPlainTextComponentSerializer() {
        return PLAIN_TEXT_COMPONENT_SERIALIZER;
    }

    public static String toLegacy(Component c) {
        return LEGACY_COMPONENT_SERIALIZER.serialize(c);
    }

    public static TextComponent fromLegacy(String s) {
        return LEGACY_COMPONENT_SERIALIZER.deserialize(s);
    }

    public static String plainText(Component c) {
        return PLAIN_TEXT_COMPONENT_SERIALIZER.serialize(c);
    }

    public TextComponent fromPlainText(String s) {
        return PLAIN_TEXT_COMPONENT_SERIALIZER.deserialize(s);
    }

    public static Component convertEscaped(String text) throws ParseException {
        return convertEscaped(text, 0, text.length());
    }

    public static Component convertEscaped(String text, int from, int to) throws ParseException {
        return new EscapedConverter(text, from, to).convert();
    }

    private static class EscapedConverter {

        private String text;
        private int to;

        private List<Component> components;

        private int index;
        private StringBuilder currentBuilder;
        private Component currentComponent;

        EscapedConverter(String text, int from, int to) {
            this.text = Objects.requireNonNull(text);
            this.to = to;

            this.components = new ArrayList<>();
            this.index = from;
            this.currentBuilder = new StringBuilder();
            this.currentComponent = Component.empty();
        }

        Component convert() throws ParseException {
            for (; this.index < this.to; this.index++) {
                char current = this.text.charAt(this.index);

                // color code (or escaped &)
                if (current == '&') {
                    char next = charAtOrException(this.index + 1);
                    // if next is a "&", append "&" and skip next
                    if (next == '&') {
                        this.currentBuilder.append('&');
                        this.index++;
                        continue;
                    }

                    // try to parse the color code
                    if (COLOR_CHARS.containsKey(next) || next == 'x') {
                        TextColor color;
                        if (next == 'x') {
                            if (this.index + 2 + 6 > this.to) {
                                throw new ParseException("illegal hex code", this.index);
                            }
                            color = StringUtilAdventure.parseHexColor(this.text, this.index + 2);
                            if (color == null) {
                                throw new ParseException("illegal hex code", this.index);
                            }
                            this.index += 7;
                        } else {
                            color = COLOR_CHARS.get(next);
                            this.index += 1;
                        }

                        finishComponent(FormatRetention.EVENTS);
                        this.currentComponent = this.currentComponent.color(color);
                        continue;
                    } else if (DECORATION_CHARS.containsKey(next)) {
                        TextDecoration deco = DECORATION_CHARS.get(next);
                        finishComponent(FormatRetention.ALL);
                        this.currentComponent = this.currentComponent.decorate(deco);
                        this.index += 1;
                        continue;
                    } else if (next == 'r') {
                        finishComponent(FormatRetention.EVENTS);
                        this.index += 1;
                        continue;
                    }

                    throw new ParseException("unknown color code &" + next, this.index);

                }

                // control sequence
                if (current == '\\') {
                    char next = charAtOrException(this.index + 1);

                    // escaped character
                    if (next == '\\' || next == '&' || next == '{' || next == '}') {
                        this.currentBuilder.append(next);
                        this.index++;
                        continue;
                    }

                    // newline
                    if (next == 'n') {
                        this.currentBuilder.append('\n');
                        this.index++;
                        continue;
                    }

                    // reset
                    if (next == 'r') {
                        char resetType = charAtOrException(this.index + 2);
                        // a = all, e = events, f = formatting
                        if (resetType != 'a' && resetType != 'e' && resetType != 'f') {
                            throw new ParseException("unknown reset type " + resetType, this.index + 2);
                        }

                        finishComponent(resetType == 'a' ? FormatRetention.NONE : resetType == 'e' ? FormatRetention.FORMATTING : FormatRetention.EVENTS);
                        this.index += 2;
                        continue;
                    }

                    // hover event
                    if (next == 'h') {
                        char actionType = charAtOrException(this.index + 2);
                        // t = text, i = item, e = entity
                        if (actionType != 't' && actionType != 'i' && actionType != 'e') {
                            throw new ParseException("unknown action type " + actionType, this.index + 2);
                        }
                        if (charAtOrException(this.index + 3) != '{') {
                            throw new ParseException("expected {", this.index + 3);
                        }

                        int contentStartIndex = this.index + 4;
                        int contentEndIndex = findMatchingRightBrace(this.index + 3, this.to);

                        HoverEvent<?> event;

                        if (actionType == 't') {
                            event = HoverEvent.showText(convertEscaped(this.text, contentStartIndex, contentEndIndex));
                        } else if (actionType == 'i') {

                            String[] itemStrings = this.text.substring(contentStartIndex, contentEndIndex).split("\\,", 3);
                            Key itemKey = Key.key(itemStrings[0].toLowerCase());
                            int itemCount;
                            try {
                                itemCount = itemStrings.length < 2 || itemStrings[1].isEmpty() ? 1 : Integer.parseInt(itemStrings[1]);
                            } catch (NumberFormatException e) {
                                throw new ParseException("illegal item count " + itemStrings[1], this.index + 3 + itemStrings[0].length() + 1);
                            }

                            event = HoverEvent.showItem(itemKey, itemCount);
                            // TODO: allow multiple items in one component?
                        } else if (actionType == 'e') {
                            int nameStartIndex = this.text.substring(contentStartIndex, contentEndIndex).indexOf('{') + contentStartIndex;
                            String[] entityStrings = this.text.substring(contentStartIndex, nameStartIndex < 0 ? contentEndIndex : nameStartIndex).split("\\,", 2);
                            Key entityKey = Key.key(entityStrings[0].toLowerCase());
                            UUID entityId;
                            try {
                                entityId = entityStrings.length < 2 || entityStrings[1].isEmpty() ? UUID.randomUUID() : UUID.fromString(entityStrings[1]);
                            } catch (IllegalArgumentException e) {
                                throw new ParseException("illegal entity id " + entityStrings[1], this.index + 3 + entityStrings[0].length() + 1);
                            }
                            Component entityName = nameStartIndex < contentStartIndex ? null : convertEscaped(this.text, nameStartIndex + 1, findMatchingRightBrace(nameStartIndex, contentEndIndex));

                            event = HoverEvent.showEntity(entityKey, entityId, entityName);
                            // TODO: allow multiple items in one component?
                        } else {
                            assert false;
                            throw new ParseException("unknown action type " + actionType, this.index + 2);
                        }

                        finishComponent();
                        this.currentComponent = this.currentComponent.hoverEvent(event);
                        this.index = contentEndIndex;
                        continue;
                    }

                    // click event
                    if (next == 'c') {
                        char actionType = charAtOrException(this.index + 2);

                        if (charAtOrException(this.index + 3) != '{') {
                            throw new ParseException("expected {", this.index + 3);
                        }

                        int contentStartIndex = this.index + 4;
                        int contentEndIndex = findMatchingRightBrace(this.index + 3, this.to);

                        String value = convertEscapedString(contentStartIndex, contentEndIndex);

                        ClickEvent event;
                        if (actionType == 'r') {
                            event = ClickEvent.runCommand(value);
                        } else if (actionType == 's') {
                            event = ClickEvent.suggestCommand(value);
                        } else if (actionType == 'c') {
                            event = ClickEvent.copyToClipboard(value);
                        } else if (actionType == 'p') {
                            try {
                                event = ClickEvent.changePage(Integer.parseInt(value));
                            } catch (NumberFormatException e) {
                                throw new ParseException("invalid page number", contentStartIndex);
                            }
                        } else if (actionType == 'u') {
                            event = ClickEvent.openUrl(value);
                        } else if (actionType == 'f') {
                            throw new ParseException("action type f is rejected by clients", this.index + 2);
                        } else {
                            throw new ParseException("unknown action type " + actionType, this.index + 2);
                        }

                        finishComponent();
                        this.currentComponent = this.currentComponent.clickEvent(event);
                        this.index = contentEndIndex;
                        continue;
                    }

                    // insertion
                    if (next == 'i') {
                        if (charAtOrException(this.index + 2) != '{') {
                            throw new ParseException("expected {", this.index + 2);
                        }

                        int contentStartIndex = this.index + 3;
                        int contentEndIndex = findMatchingRightBrace(this.index + 2, this.to);

                        String insertion = convertEscapedString(contentStartIndex, contentEndIndex);

                        finishComponent();
                        this.currentComponent = this.currentComponent.insertion(insertion);
                        this.index = contentEndIndex;
                        continue;
                    }

                    // font
                    if (next == 'f') {
                        if (charAtOrException(this.index + 2) != '{') {
                            throw new ParseException("expected {", this.index + 2);
                        }

                        int fontStartIndex = this.index + 3;
                        int fontEndIndex = findMatchingRightBrace(this.index + 2, this.to);
                        Key fontKey = Key.key(this.text.substring(fontStartIndex, fontEndIndex).toLowerCase());

                        finishComponent();
                        this.currentComponent = this.currentComponent.font(fontKey);
                        this.index = fontEndIndex;
                        continue;
                    }

                    // translated component
                    if (next == 't') {
                        if (charAtOrException(this.index + 2) != '{') {
                            throw new ParseException("expected {", this.index + 2);
                        }

                        int contentStartIndex = this.index + 3;
                        int contentEndIndex = findMatchingRightBrace(this.index + 2, this.to);

                        int translationKeyEndIndex = this.text.substring(contentStartIndex, contentEndIndex).indexOf('{');
                        boolean hasFallback = charAtOrException(translationKeyEndIndex + contentStartIndex - 1) == ',';
                        if (hasFallback) {
                            translationKeyEndIndex--;
                        }
                        if (translationKeyEndIndex < 0) {
                            translationKeyEndIndex = contentEndIndex;
                        } else {
                            translationKeyEndIndex += contentStartIndex;
                        }

                        String translationKey = this.text.substring(contentStartIndex, translationKeyEndIndex);

                        int startOfExtras;
                        String fallback;
                        if (hasFallback) {
                            int fallbackEndIndex = findMatchingRightBrace(translationKeyEndIndex + 1, contentEndIndex);
                            fallback = convertEscapedString(translationKeyEndIndex + 2, fallbackEndIndex);
                            startOfExtras = fallbackEndIndex + 1;
                        } else {
                            fallback = null;
                            startOfExtras = translationKeyEndIndex;
                        }

                        List<Component> translationExtras = new ArrayList<>();
                        for (int blockStartIndex = startOfExtras; blockStartIndex < contentEndIndex;) {
                            if (charAtOrException(blockStartIndex) != '{') {
                                throw new ParseException("expected {", blockStartIndex);
                            }

                            int blockEndIndex = findMatchingRightBrace(blockStartIndex, contentEndIndex);
                            assert blockEndIndex >= 0;

                            translationExtras.add(convertEscaped(this.text, blockStartIndex + 1, blockEndIndex));
                            blockStartIndex = blockEndIndex + 1;
                        }

                        finishComponent();
                        this.currentComponent = this.currentComponent.append(Component.translatable(translationKey, fallback, translationExtras));
                        this.index = contentEndIndex;
                        continue;
                    }

                    // score component
                    if (next == 's') {
                        if (charAtOrException(this.index + 2) != '{') {
                            throw new ParseException("expected {", this.index + 2);
                        }

                        int contentStartIndex = this.index + 3;
                        int contentEndIndex = findMatchingRightBrace(this.index + 2, this.to);

                        String[] scoreStrings = this.text.substring(contentStartIndex, contentEndIndex).split("\\,", 2);
                        if (scoreStrings.length < 2) {
                            throw new ParseException("missing objective name", contentEndIndex);
                        }

                        // TODO: escaping/conversion needed?
                        String name = scoreStrings[0];
                        String objective = scoreStrings[1];

                        finishComponent();
                        this.currentComponent = this.currentComponent.append(Component.score(name, objective));
                        this.index = contentEndIndex;
                        continue;
                    }

                    // selector component
                    if (next == '@') {
                        if (charAtOrException(this.index + 2) != '{') {
                            throw new ParseException("expected {", this.index + 2);
                        }

                        int contentStartIndex = this.index + 3;
                        int contentEndIndex = findMatchingRightBrace(this.index + 2, this.to);

                        // TODO: escaping/conversion needed?
                        String selector = this.text.substring(contentStartIndex, contentEndIndex);

                        finishComponent();
                        this.currentComponent = this.currentComponent.append(Component.selector(selector));
                        this.index = contentEndIndex;
                        continue;
                    }

                    // keybind component
                    if (next == 'k') {
                        if (charAtOrException(this.index + 2) != '{') {
                            throw new ParseException("expected {", this.index + 2);
                        }

                        int contentStartIndex = this.index + 3;
                        int contentEndIndex = findMatchingRightBrace(this.index + 2, this.to);

                        // TODO: escaping/conversion needed?
                        String keybind = this.text.substring(contentStartIndex, contentEndIndex);

                        finishComponent();
                        this.currentComponent = this.currentComponent.append(Component.keybind(keybind));
                        this.index = contentEndIndex;
                        continue;
                    }

                    if (next == 'o') {
                        char contentsTypeChar = charAtOrException(this.index + 2);

                        if (charAtOrException(this.index + 3) != '{') {
                            throw new ParseException("expected {", this.index + 3);
                        }

                        int contentStartIndex = this.index + 4;
                        int contentEndIndex = findMatchingRightBrace(this.index + 3, this.to);

                        if (contentsTypeChar == 'p') {
                            if (charAtOrException(contentStartIndex) != '{') {
                                throw new ParseException("expected {", contentStartIndex);
                            }

                            boolean hat = false;
                            UUID id = null;
                            String name = null;
                            List<ProfileProperty> profileProperties = new ArrayList<>();
                            Key texture = null;

                            int endOfFirstBlock = findMatchingRightBrace(contentStartIndex, contentEndIndex);
                            String[] firstBlock = convertEscapedString(contentStartIndex + 1, endOfFirstBlock).split(",", 3);
                            for (String s : firstBlock) {
                                if ("hat".equalsIgnoreCase(s)) {
                                    hat = true;
                                    continue;
                                }
                                try {
                                    id = UUID.fromString(s);
                                    continue;
                                } catch (IllegalArgumentException e) {
                                    // ignore
                                }
                                if (name != null) {
                                    throw new ParseException("duplicate name, or invalid other property", contentStartIndex + 1);
                                }
                                name = s;
                            }

                            if (charAtOrException(endOfFirstBlock + 1) == '{') {
                                int endOfSecondBlock = findMatchingRightBrace(endOfFirstBlock + 1, contentEndIndex);
                                String[] secondBlock = convertEscapedString(endOfFirstBlock + 2, endOfSecondBlock).split(",");
                                for (String s : secondBlock) {
                                    if (s.isEmpty()) {
                                        continue;
                                    }
                                    String[] ppParts = s.split("=", 2);
                                    if (ppParts.length != 2) {
                                        throw new ParseException("profile property string must have two parts separated by =", endOfFirstBlock + 2);
                                    }
                                    profileProperties.add(PlayerHeadObjectContents.property(ppParts[0], ppParts[1]));
                                }

                                if (charAtOrException(endOfSecondBlock + 1) == '{') {
                                    int endOfThirdBlock = findMatchingRightBrace(endOfSecondBlock + 1, contentEndIndex);
                                    String thirdBlock = convertEscapedString(endOfSecondBlock + 2, endOfThirdBlock);
                                    try {
                                        texture = Key.key(thirdBlock);
                                    } catch (InvalidKeyException e) {
                                        throw new ParseException("invalid key", endOfSecondBlock + 2);
                                    }
                                }
                            }

                            finishComponent();
                            this.currentComponent = this.currentComponent.append(Component.object(ObjectContents.playerHead().hat(hat).id(id).name(name).profileProperties(profileProperties).texture(texture).build()));
                            this.index = contentEndIndex;
                            continue;
                        } else if (contentsTypeChar == 's') {
                            String[] parts = convertEscapedString(contentStartIndex, contentEndIndex).split(",", 2);
                            SpriteObjectContents contents;
                            if (parts.length == 1) {
                                try {
                                    contents = ObjectContents.sprite(Key.key(parts[0]));
                                } catch (InvalidKeyException e) {
                                    throw new ParseException("invalid key", contentStartIndex);
                                }
                            } else if (parts.length == 2) {
                                try {
                                    contents = ObjectContents.sprite(Key.key(parts[0]), Key.key(parts[1]));
                                } catch (InvalidKeyException e) {
                                    throw new ParseException("invalid key", contentStartIndex);
                                }
                            } else {
                                throw new ParseException("expected one or two keys, separated by comma", contentStartIndex);
                            }

                            finishComponent();
                            this.currentComponent = this.currentComponent.append(Component.object(contents));
                            this.index = contentEndIndex;
                            continue;
                        } else {
                            throw new ParseException("unknown object component contents type " + contentsTypeChar, this.index + 1);
                        }
                    }

                    throw new ParseException("unknown control sequence \\" + next, this.index);
                }

                // plain block
                if (current == '{') {
                    int closingIndex = findMatchingRightBrace(this.index, this.to);
                    finishComponent();

                    Component subComponent = convertEscaped(this.text, this.index + 1, closingIndex);
                    this.currentComponent = this.currentComponent.append(subComponent);
                    this.index = closingIndex;
                    continue;
                }

                // unmatched right brace
                if (current == '}') {
                    throw new ParseException("unmatched right brace", this.index);
                }

                // normal character
                this.currentBuilder.append(current);
                continue;
            }

            // finish last component
            this.currentComponent = this.currentComponent.append(Component.text(this.currentBuilder.toString()));
            this.components.add(this.currentComponent);

            return Component.join(JoinConfiguration.noSeparators(), this.components).compact();
        }

        private char charAtOrException(int i) throws ParseException {
            if (i >= this.to) {
                throw new ParseException("unexpected end of block", this.to);
            }
            return this.text.charAt(i);
        }

        private void finishComponent() {
            finishComponent(FormatRetention.ALL);
        }

        private void finishComponent(FormatRetention retention) {
            this.currentComponent = this.currentComponent.append(Component.text(this.currentBuilder.toString()));
            this.components.add(this.currentComponent);

            Component newComponent = Component.empty();
            newComponent = newComponent.mergeStyle(this.currentComponent, retention.MERGES);
            this.currentComponent = newComponent;

            this.currentBuilder = new StringBuilder();
        }

        private int findMatchingRightBrace(int leftBraceIndex, int endIndex) throws ParseException {
            int depth = 0;
            for (int i = leftBraceIndex; i < endIndex; i++) {
                switch (this.text.charAt(i)) {
                    case '\\':
                        i++;
                        break;
                    case '{':
                        depth += 1;
                        break;
                    case '}':
                        depth--;
                        break;
                    default:
                        continue;
                }
                if (depth == 0) {
                    return i;
                }
            }
            throw new ParseException("unmatched left brace", leftBraceIndex);
        }

        private String convertEscapedString(int from, int to) throws ParseException {
            return convertEscapedString(this.text, from, to);
        }

        private String convertEscapedString(String text, int from, int to) throws ParseException {
            StringBuilder result = new StringBuilder();
            for (int i = from; i < to; i++) {
                char curr = text.charAt(i);
                if (curr == '\\') {
                    if (i + 1 >= to) {
                        throw new ParseException("unexpected end of block", to);
                    }
                    char next = text.charAt(i + 1);

                    if (next == '\\' || next == '&' || next == '{' || next == '}') {
                        result.append(next);
                    } else if (next == 'n') {
                        result.append('\n');
                    } else {
                        throw new ParseException("unknown simple control sequence \\" + next, i);
                    }
                    i++;
                } else if (curr == '&') {
                    if (i + 1 >= to) {
                        result.append(LegacyComponentSerializer.SECTION_CHAR);
                        continue;
                    }
                    char next = text.charAt(i + 1);

                    if (next == '&') {
                        result.append('&');
                        i++;
                    } else {
                        result.append(LegacyComponentSerializer.SECTION_CHAR);
                    }
                } else {
                    result.append(curr);
                }
            }
            return result.toString();
        }
    }

    public static String revertEscaped(Component component) {
        return revertEscaped(component.compact(), new StringBuilder()).toString();
    }

    private static StringBuilder revertEscaped(Component component, StringBuilder builder) {
        serializeColor(component, builder);
        serializeFormatting(component, builder);
        serializeEvents(component, builder);

        if (component instanceof TextComponent c) {
            serializeTextComponent(c, builder);
        } else if (component instanceof TranslatableComponent c) {
            serializeTranslatableComponent(c, builder);
        } else if (component instanceof ScoreComponent c) {
            serializeScoreComponent(c, builder);
        } else if (component instanceof SelectorComponent c) {
            serializeSelectorComponent(c, builder);
        } else if (component instanceof KeybindComponent c) {
            serializeKeybindComponent(c, builder);
        } else if (component instanceof ObjectComponent c) {
            serializeObjectComponent(c, builder);
        } else {
            throw new IllegalArgumentException("unsupported component type " + component.getClass().getName());
        }

        serializeChildren(component, builder);
        return builder;
    }

    private static void escapeString(String raw, StringBuilder builder) {
        for (int i = 0; i < raw.length(); i++) {
            char c = raw.charAt(i);
            switch (c) {
                case '\n':
                    builder.append("\\n");
                    break;
                case '\\':
                case '&':
                case '{':
                case '}':
                    builder.append('\\');
                    //$FALL-THROUGH$
                default:
                    builder.append(c);
            }
        }
    }

    private static void serializeColor(Component component, StringBuilder builder) {
        TextColor color = component.color();
        if (color == null) {
            return;
        }

        NamedTextColor named = NamedTextColor.namedColor(color.value());
        if (named != null) {
            builder.append("&" + COLOR_CHARS_INVERSE.get(named));
            return;
        }

        builder.append("&x");
        appendHexColorString(color, builder);
    }

    private static void appendHexColorString(TextColor color, StringBuilder builder) {
        String hexString = Integer.toHexString(color.value());
        for (int i = 0; i + hexString.length() < 6; i++) {
            builder.append('0');
        }
        builder.append(hexString);
    }

    private static void serializeFormatting(Component component, StringBuilder builder) {
        boolean containsFalse = component.style().decorations().values().contains(State.FALSE);
        if (containsFalse) {
            throw new IllegalArgumentException("can't deal with explicitely false decorations");
        }

        for (Entry<TextDecoration, State> deco : component.style().decorations().entrySet()) {
            if (deco.getValue() == State.NOT_SET) {
                continue;
            }
            builder.append("&" + DECORATION_CHARS_INVERSE.get(deco.getKey()));
        }

        if (component.font() != null) {
            builder.append("\\f{").append(component.font().asMinimalString()).append('}');
        }
    }

    private static void serializeEvents(Component component, StringBuilder builder) {
        HoverEvent<?> he = component.hoverEvent();
        if (he != null) {
            builder.append("\\h");
            HoverEvent.Action<?> action = he.action();
            Object value = he.value();
            if (action == HoverEvent.Action.SHOW_TEXT) {
                builder.append("t{");
                if (!(value instanceof ComponentLike cLike)) {
                    throw new ClassCastException("Expected HoverEvent SHOW_TEXT value to be instance of ComponentLike.");
                }
                revertEscaped(cLike.asComponent(), builder);
                builder.append('}');
            } else if (action == HoverEvent.Action.SHOW_ITEM) {
                builder.append("i{");
                if (!(value instanceof ShowItem item)) {
                    throw new ClassCastException("Expected HoverEvent SHOW_ITEM value to be instance of ShowItem.");
                }
                builder.append(item.item().asMinimalString()).append(',').append(item.count());
                builder.append('}');
            } else if (action == HoverEvent.Action.SHOW_ENTITY) {
                builder.append("e{");
                if (!(value instanceof ShowEntity entity)) {
                    throw new ClassCastException("Expected HoverEvent SHOW_ENTITY value to be instance of ShowEntity.");
                }
                builder.append(entity.type().asMinimalString()).append(',').append(entity.id().toString()).append(',');
                revertEscaped(entity.name(), builder);
                builder.append('}');
            } else {
                throw new IllegalArgumentException("HoverEvent Action " + action + " is not supported.");
            }
        }

        ClickEvent ce = component.clickEvent();
        if (ce != null) {
            builder.append("\\c");
            ClickEvent.Action action = ce.action();
            if (action == ClickEvent.Action.RUN_COMMAND) {
                builder.append('r');
            } else if (action == ClickEvent.Action.SUGGEST_COMMAND) {
                builder.append('s');
            } else if (action == ClickEvent.Action.COPY_TO_CLIPBOARD) {
                builder.append('c');
            } else if (action == ClickEvent.Action.CHANGE_PAGE) {
                builder.append('p');
            } else if (action == ClickEvent.Action.OPEN_URL) {
                builder.append('u');
            } else if (action == ClickEvent.Action.OPEN_FILE) {
                throw new IllegalArgumentException("ClickEvent Action OPEN_FILE is rejectet by clients and not supported.");
            }
            builder.append('{');
            if (ce.payload() instanceof Payload.Text payload) {
                escapeString(payload.value(), builder);
            } else if (ce.payload() instanceof Payload.Int payload) {
                builder.append(payload.integer());
            } else {
                throw new IllegalArgumentException("Unsupported ClickEvent payload type " + ce.payload().getClass());
            }
            builder.append('}');
        }

        String insertion = component.insertion();
        if (insertion != null) {
            builder.append("\\i{");
            escapeString(component.insertion(), builder);
            builder.append('}');
        }
    }

    private static void serializeChildren(Component component, StringBuilder builder) {
        List<Component> children = component.children();
        if (children == null) {
            return;
        }

        children = children.stream().filter(Component.IS_NOT_EMPTY).toList();
        if (children.isEmpty()) {
            return;
        }

        for (Component child : children) {
            if (children.size() != 1) {
                builder.append('{');
            }
            revertEscaped(child, builder);
            if (children.size() != 1) {
                builder.append('}');
            }
        }
    }

    private static void serializeTextComponent(TextComponent componet, StringBuilder builder) {
        escapeString(componet.content(), builder);
    }

    private static void serializeTranslatableComponent(TranslatableComponent component, StringBuilder builder) {
        builder.append("\\t{");
        builder.append(component.key());
        if (component.fallback() != null) {
            builder.append(",{");
            escapeString(component.fallback(), builder);
            builder.append('}');
        }

        for (TranslationArgument argument : component.arguments()) {
            builder.append('{');
            revertEscaped(argument.asComponent(), builder);
            builder.append('}');
        }

        builder.append('}');
    }

    private static void serializeScoreComponent(ScoreComponent component, StringBuilder builder) {
        builder.append("\\s{");
        builder.append(component.name()).append(',');
        builder.append(component.objective());
        builder.append('}');
    }

    private static void serializeSelectorComponent(SelectorComponent component, StringBuilder builder) {
        builder.append("\\@{");
        builder.append(component.pattern());
        builder.append('}');
    }

    private static void serializeKeybindComponent(KeybindComponent component, StringBuilder builder) {
        builder.append("\\k{");
        builder.append(component.keybind());
        builder.append('}');
    }

    private static void serializeObjectComponent(ObjectComponent component, StringBuilder builder) {
        builder.append("\\o");
        if (component.contents() instanceof PlayerHeadObjectContents contents) {
            builder.append("p{");
            List<String> parts = new ArrayList<>();
            if (contents.hat()) {
                parts.add("hat");
            }
            if (contents.id() != null) {
                parts.add(contents.id().toString());
            }
            if (contents.name() != null) {
                parts.add(contents.name());
            }
            builder.append('{').append(parts.stream().collect(Collectors.joining(","))).append('}');
            builder.append('{').append(contents.profileProperties().stream().map(pp -> pp.name() + "=" + pp.value()).collect(Collectors.joining(","))).append('}');
            if (contents.texture() != null) {
                builder.append('{').append(contents.texture().asMinimalString()).append('}');
            }
        } else if (component.contents() instanceof SpriteObjectContents contents) {
            builder.append("s{");
            builder.append(contents.atlas().asMinimalString()).append(',').append(contents.sprite().asMinimalString());
        } else {
            throw new IllegalArgumentException("Unknown type of object component contents: " + component.contents().getClass());
        }
        builder.append('}');
    }

    public static List<Component> splitBySpaces(Component component) {
        SpaceSplitter splitter = new SpaceSplitter();
        splitter.accept(component, Style.empty());
        return splitter.finish();
    }

    private static final class SpaceSplitter {
        private final List<Component> out = new ArrayList<>();
        private Component current = Component.empty();
        private boolean hasContent = false;

        void accept(Component c, Style parentStyle) {
            Style effective = mergeInherited(parentStyle, c.style());

            if (c instanceof TextComponent tc) {
                String content = tc.content();
                if (!content.isEmpty()) {
                    appendText(content, effective);
                }
                for (Component child : c.children()) {
                    accept(child, effective);
                }
                return;
            }

            // Non-text components (e.g., translatable/keybind/etc.) are appended atomically.
            // Their own children are preserved inside them; we don't traverse to avoid duplication.
            appendAtomic(c.style(effective));
        }

        private void appendText(String s, Style style) {
            int start = 0;
            for (int i = 0; i < s.length(); i++) {
                if (s.charAt(i) != ' ') {
                    continue;
                }

                // text part before the space
                if (i > start) {
                    appendAtomic(Component.text(s.substring(start, i)).style(style));
                }
                // the space itself ends the token (and is included in the token)
                appendAtomic(Component.text(" ").style(style));
                flushToken();

                start = i + 1;
            }

            // tail (no trailing space)
            if (start < s.length()) {
                appendAtomic(Component.text(s.substring(start)).style(style));
            }
        }

        private void appendAtomic(Component c) {
            this.current = this.current.append(c);
            this.hasContent = true;
        }

        private void flushToken() {
            if (this.hasContent) {
                this.out.add(this.current);
                this.current = Component.empty();
                this.hasContent = false;
            }
        }

        List<Component> finish() {
            flushToken();
            return this.out;
        }

        private static Style mergeInherited(Style parent, Style self) {
            // Apply parent values where the child has none (so child overrides parent).
            return self.merge(parent, Style.Merge.Strategy.IF_ABSENT_ON_TARGET);
        }
    }

    public static Component replacePattern(Component component, Pattern pattern, Component replacement) {
        return component.replaceText(TextReplacementConfig.builder().match(pattern).replacement(replacement).build());
    }

}
