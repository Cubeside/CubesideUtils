package de.iani.cubesideutils;

import java.awt.Color;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder.FormatRetention;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.ItemTag;
import net.md_5.bungee.api.chat.KeybindComponent;
import net.md_5.bungee.api.chat.ScoreComponent;
import net.md_5.bungee.api.chat.SelectorComponent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.TranslatableComponent;
import net.md_5.bungee.api.chat.hover.content.Content;
import net.md_5.bungee.api.chat.hover.content.Entity;
import net.md_5.bungee.api.chat.hover.content.Item;
import net.md_5.bungee.api.chat.hover.content.Text;

public class ComponentUtil {
    // public static void main(String args[]) throws ParseException {
    // String start = "&4Dies &list &5ein Test.";
    // String next = start;
    // String current;
    // do {
    // current = next;
    // next = serializeComponent(deserializeComponent(current));
    // } while (!current.equals(next));
    // System.out.println(current);
    //
    // start = "&4&lDies {ist &oein &5Test} der &2Components.";
    // next = start;
    // do {
    // current = next;
    // next = serializeComponent(deserializeComponent(current));
    // } while (!current.equals(next));
    // System.out.println(current);
    // }
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

    private ComponentUtil() {
        throw new UnsupportedOperationException("No instance for you, Sir!");
        // prevents instances
    }

    public static LegacyComponentSerializer getLegacyComponentSerializer() {
        return LEGACY_COMPONENT_SERIALIZER;
    }

    public static final boolean isColor(ChatColor color) {
        return color != ChatColor.BOLD && color != ChatColor.ITALIC && color != ChatColor.MAGIC && color != ChatColor.RESET && color != ChatColor.STRIKETHROUGH && color != ChatColor.UNDERLINE;
    }

    public static BaseComponent deserializeComponent(String text) throws ParseException {
        return convertEscaped(text);
    }

    public static BaseComponent deserializeComponent(String text, int from, int to) throws ParseException {
        return convertEscaped(text, from, to);
    }

    public static BaseComponent convertEscaped(String text) throws ParseException {
        return convertEscaped(text, 0, text.length());
    }

    public static BaseComponent convertEscaped(String text, int from, int to) throws ParseException {
        return new EscapedConverter(text, from, to).convert();
    }

    private static class EscapedConverter {

        private String text;
        private int to;

        private List<BaseComponent> components;

        private int index;
        private StringBuilder currentBuilder;
        private TextComponent currentComponent;

        EscapedConverter(String text, int from, int to) {
            this.text = Objects.requireNonNull(text);
            this.to = to;

            this.components = new ArrayList<>();
            this.index = from;
            this.currentBuilder = new StringBuilder();
            this.currentComponent = new TextComponent();
        }

        BaseComponent convert() throws ParseException {
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
                    if (ChatColor.getByChar(next) != null || next == 'x') {
                        ChatColor color;
                        if (next == 'x') {
                            if (this.index + 2 + 6 > this.to) {
                                throw new ParseException("illegal hex code", this.index);
                            }
                            color = StringUtil.parseHexColor(this.text, this.index + 2);
                            if (color == null) {
                                throw new ParseException("illegal hex code", this.index);
                            }
                            this.index += 7;
                        } else {
                            color = ChatColor.getByChar(next);
                            this.index += 1;
                        }

                        finishComponent(isColor(color) ? FormatRetention.EVENTS : FormatRetention.ALL);

                        if (color == ChatColor.BOLD) {
                            this.currentComponent.setBold(true);
                        } else if (color == ChatColor.ITALIC) {
                            this.currentComponent.setItalic(true);
                        } else if (color == ChatColor.MAGIC) {
                            this.currentComponent.setObfuscated(true);
                        } else if (color == ChatColor.STRIKETHROUGH) {
                            this.currentComponent.setStrikethrough(true);
                        } else if (color == ChatColor.UNDERLINE) {
                            this.currentComponent.setUnderlined(true);
                        } else if (color == ChatColor.RESET) {
                            this.currentComponent.setReset(true);
                        } else if (color.getColor() != null) {
                            this.currentComponent.setColor(color);
                        } else {
                            throw new RuntimeException("Unexpected ChatColor: " + color);
                        }
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

                        HoverEvent.Action action;
                        Content[] content;

                        if (actionType == 't') {
                            action = HoverEvent.Action.SHOW_TEXT;
                            content = new Content[] { new Text(new BaseComponent[] { convertEscaped(this.text, contentStartIndex, contentEndIndex) }) };
                        } else if (actionType == 'i') {
                            action = HoverEvent.Action.SHOW_ITEM;

                            String[] itemStrings = this.text.substring(contentStartIndex, contentEndIndex).split("\\,", 3);
                            String itemId = itemStrings[0];
                            int itemCount;
                            try {
                                itemCount = itemStrings.length < 2 || itemStrings[1].isEmpty() ? 1 : Integer.parseInt(itemStrings[1]);
                            } catch (NumberFormatException e) {
                                throw new ParseException("illegal item count " + itemStrings[1], this.index + 3 + itemStrings[0].length() + 1);
                            }
                            ItemTag tag = itemStrings.length < 3 || itemStrings[2].isEmpty() ? null : ItemTag.ofNbt(convertEscapedString(itemStrings[2]));

                            content = new Content[] { new Item(itemId, itemCount, tag) };
                            // TODO: allow multiple items in one component?
                        } else if (actionType == 'e') {
                            action = HoverEvent.Action.SHOW_ENTITY;

                            int nameStartIndex = this.text.substring(contentStartIndex, contentEndIndex).indexOf('{') + contentStartIndex;
                            String[] entityStrings = this.text.substring(contentStartIndex, nameStartIndex < 0 ? contentEndIndex : nameStartIndex).split("\\,", 2);
                            String entityType = entityStrings[0];
                            UUID entityId;
                            try {
                                entityId = entityStrings.length < 2 || entityStrings[1].isEmpty() ? UUID.randomUUID() : UUID.fromString(entityStrings[1]);
                            } catch (IllegalArgumentException e) {
                                throw new ParseException("illegal entity id " + entityStrings[1], this.index + 3 + entityStrings[0].length() + 1);
                            }
                            BaseComponent entityName = nameStartIndex < contentStartIndex ? null : convertEscaped(this.text, nameStartIndex + 1, findMatchingRightBrace(nameStartIndex, contentEndIndex));

                            content = new Content[] { new Entity(entityType, entityId.toString(), entityName) };
                            // TODO: allow multiple items in one component?
                        } else {
                            assert false;
                            throw new ParseException("unknown action type " + actionType, this.index + 2);
                        }

                        finishComponent();
                        this.currentComponent.setHoverEvent(new HoverEvent(action, content));
                        this.index = contentEndIndex;
                        continue;
                    }

                    // click event
                    if (next == 'c') {
                        char actionType = charAtOrException(this.index + 2);

                        ClickEvent.Action action;
                        if (actionType == 'r') {
                            action = ClickEvent.Action.RUN_COMMAND;
                        } else if (actionType == 's') {
                            action = ClickEvent.Action.SUGGEST_COMMAND;
                        } else if (actionType == 'c') {
                            action = ClickEvent.Action.COPY_TO_CLIPBOARD;
                        } else if (actionType == 'p') {
                            action = ClickEvent.Action.CHANGE_PAGE;
                        } else if (actionType == 'u') {
                            action = ClickEvent.Action.OPEN_URL;
                        } else if (actionType == 'f') {
                            throw new ParseException("action type f is rejected by clients", this.index + 2);
                        } else {
                            throw new ParseException("unknown action type " + actionType, this.index + 2);
                        }

                        if (charAtOrException(this.index + 3) != '{') {
                            throw new ParseException("expected {", this.index + 3);
                        }

                        int contentStartIndex = this.index + 4;
                        int contentEndIndex = findMatchingRightBrace(this.index + 3, this.to);

                        String value = convertEscapedString(contentStartIndex, contentEndIndex);

                        finishComponent();
                        this.currentComponent.setClickEvent(new ClickEvent(action, value));
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
                        this.currentComponent.setInsertion(insertion);
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
                        String fontString = this.text.substring(fontStartIndex, fontEndIndex);

                        finishComponent();
                        this.currentComponent.setFont(fontString);
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
                        if (translationKeyEndIndex < 0) {
                            translationKeyEndIndex = contentEndIndex;
                        } else {
                            translationKeyEndIndex += contentStartIndex;
                        }

                        String translationKey = this.text.substring(contentStartIndex, translationKeyEndIndex);

                        List<BaseComponent> translationExtras = new ArrayList<>();
                        for (int blockStartIndex = translationKeyEndIndex; blockStartIndex < contentEndIndex;) {
                            if (charAtOrException(blockStartIndex) != '{') {
                                throw new ParseException("expected {", blockStartIndex);
                            }

                            int blockEndIndex = findMatchingRightBrace(blockStartIndex, contentEndIndex);
                            assert blockEndIndex >= 0;

                            translationExtras.add(convertEscaped(this.text, blockStartIndex + 1, blockEndIndex));
                            blockStartIndex = blockEndIndex + 1;
                        }

                        finishComponent();
                        this.currentComponent.addExtra(new TranslatableComponent(translationKey, translationExtras.toArray(new Object[translationExtras.size()])));
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

                        String[] scoreStrings = this.text.substring(contentStartIndex, contentEndIndex).split("\\,", 3);
                        if (scoreStrings.length < 2) {
                            throw new ParseException("missing objective name", contentEndIndex);
                        }

                        // TODO: escaping/conversion needed?
                        String name = scoreStrings[0];
                        String objective = scoreStrings[1];
                        String value = scoreStrings.length < 3 ? "" : scoreStrings[2];

                        finishComponent();
                        this.currentComponent.addExtra(new ScoreComponent(name, objective, value));
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
                        this.currentComponent.addExtra(new SelectorComponent(selector));
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
                        this.currentComponent.addExtra(new KeybindComponent(keybind));
                        this.index = contentEndIndex;
                        continue;
                    }

                    throw new ParseException("unknown control sequence \\" + next, this.index);
                }

                // plain block
                if (current == '{') {
                    int closingIndex = findMatchingRightBrace(this.index, this.to);
                    finishComponent();

                    BaseComponent subComponent = convertEscaped(this.text, this.index + 1, closingIndex);
                    this.currentComponent.addExtra(subComponent);
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
            this.currentComponent.addExtra(this.currentBuilder.toString());
            this.components.add(this.currentComponent);

            return new TextComponent(this.components.toArray(new BaseComponent[this.components.size()]));
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
            this.currentComponent.addExtra(this.currentBuilder.toString());
            this.components.add(this.currentComponent);

            TextComponent newComponent = new TextComponent();
            newComponent.copyFormatting(this.currentComponent, retention, true);
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

        private String convertEscapedString(String text) throws ParseException {
            return convertEscapedString(text, 0, text.length());
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
                        result.append(ChatColor.COLOR_CHAR);
                        continue;
                    }
                    char next = text.charAt(i + 1);

                    if (next == '&') {
                        result.append('&');
                        i++;
                    } else {
                        result.append(ChatColor.COLOR_CHAR);
                    }
                } else {
                    result.append(curr);
                }
            }
            return result.toString();
        }
    }

    private static final Map<ChatColor, String> CONSTANT_CHAT_COLORS;

    static {
        Map<ChatColor, String> constantChatColors = new LinkedHashMap<>();
        for (char c : "0123456789abcdef".toCharArray()) {
            constantChatColors.put(ChatColor.getByChar(c), "&" + c);
        }
        CONSTANT_CHAT_COLORS = Collections.unmodifiableMap(constantChatColors);
    }

    public static String serializeComponent(BaseComponent component) {
        return serializeComponent(component, new StringBuilder()).toString();
    }

    private static StringBuilder serializeComponent(BaseComponent component, StringBuilder builder) {
        serializeColor(component, builder);
        serializeFormatting(component, builder);
        serializeEvents(component, builder);

        if (component instanceof TextComponent) {
            serializeTextComponent((TextComponent) component, builder);
        } else if (component instanceof TranslatableComponent) {
            serializeTranslatableComponent((TranslatableComponent) component, builder);
        } else if (component instanceof ScoreComponent) {
            serializeScoreComponent((ScoreComponent) component, builder);
        } else if (component instanceof SelectorComponent) {
            serializeSelectorComponent((SelectorComponent) component, builder);
        } else if (component instanceof KeybindComponent) {
            serializeKeybindComponent((KeybindComponent) component, builder);
        } else {
            throw new IllegalArgumentException("unsupported component type " + component.getClass().getName());
        }

        serializeExtraComponents(component, builder);
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

    private static void serializeColor(BaseComponent component, StringBuilder builder) {
        ChatColor color = component.getColorRaw();
        if (color == null) {
            return;
        }

        String colorString = CONSTANT_CHAT_COLORS.get(color);
        if (colorString != null) {
            builder.append(colorString);
            return;
        }

        builder.append("&x");
        appendHexColorString(color.getColor(), builder);
    }

    private static void appendHexColorString(Color color, StringBuilder builder) {
        String hexString = Integer.toHexString(color.getRGB());
        for (int i = 0; i + hexString.length() < 6; i++) {
            builder.append('0');
        }
        builder.append(hexString);
    }

    private static void serializeFormatting(BaseComponent component, StringBuilder builder) {
        if (component.isObfuscatedRaw() == Boolean.TRUE) {
            builder.append("&k");
        }
        if (component.isBoldRaw() == Boolean.TRUE) {
            builder.append("&l");
        }
        if (component.isStrikethroughRaw() == Boolean.TRUE) {
            builder.append("&m");
        }
        if (component.isUnderlinedRaw() == Boolean.TRUE) {
            builder.append("&n");
        }
        if (component.isItalicRaw() == Boolean.TRUE) {
            builder.append("&o");
        }

        if (component.getFontRaw() != null) {
            builder.append("\\f{").append(component.getFontRaw()).append('}');
        }
    }

    private static void serializeEvents(BaseComponent component, StringBuilder builder) {
        HoverEvent he = component.getHoverEvent();
        if (he != null) {
            builder.append("\\h");
            List<Content> hoverContents = he.getContents();
            if (hoverContents.size() != 1) {
                throw new IllegalArgumentException("HoverEvents with more than one content object are not supported.");
            }
            switch (he.getAction()) {
                case SHOW_TEXT:
                    builder.append("t{");
                    Text text = (Text) hoverContents.get(0);
                    if (text.getValue() instanceof String) {
                        escapeString((String) text.getValue(), builder);
                    } else if (text.getValue() instanceof BaseComponent[]) {
                        serializeComponent(new TextComponent((BaseComponent[]) text.getValue()));
                    } else {
                        throw new ClassCastException("Expected HoverEvent Text value to be instance of String or BaseComponent[].");
                    }
                    builder.append('}');
                    break;
                case SHOW_ITEM:
                    builder.append("i{");
                    Item item = (Item) hoverContents.get(0);
                    builder.append(item.getId()).append(',').append(item.getCount()).append(',');
                    escapeString(item.getTag().getNbt(), builder);
                    builder.append('}');
                    break;
                case SHOW_ENTITY:
                    builder.append("e{");
                    Entity entity = (Entity) hoverContents.get(0);
                    builder.append(entity.getType()).append(',').append(entity.getId()).append(',');
                    serializeComponent(entity.getName(), builder);
                    builder.append('}');
                    break;
                default:
                    throw new IllegalArgumentException("HoverEvent Action " + he.getAction() + " is not supported.");
            }
        }

        ClickEvent ce = component.getClickEvent();
        if (ce != null) {
            builder.append("\\c");
            switch (ce.getAction()) {
                case RUN_COMMAND:
                    builder.append('r');
                    break;
                case SUGGEST_COMMAND:
                    builder.append('s');
                    break;
                case COPY_TO_CLIPBOARD:
                    builder.append('c');
                    break;
                case CHANGE_PAGE:
                    builder.append('p');
                    break;
                case OPEN_URL:
                    builder.append('u');
                    break;
                case OPEN_FILE:
                    throw new IllegalArgumentException("ClickEvent Action OPEN_FILE is rejectet by clients and not supported.");
            }
            builder.append('{');
            escapeString(ce.getValue(), builder);
            builder.append('}');
        }

        String insertion = component.getInsertion();
        if (insertion != null) {
            builder.append("\\i{");
            escapeString(insertion, builder);
            builder.append('}');
        }
    }

    private static void serializeExtraComponents(BaseComponent component, StringBuilder builder) {
        List<BaseComponent> extra = component.getExtra();
        if (extra == null) {
            return;
        }

        extra = new ArrayList<>(extra);
        extra.removeIf(bc -> bc.toPlainText().isEmpty());
        if (extra.isEmpty()) {
            return;
        }

        for (BaseComponent bc : extra) {
            if (extra.size() != 1) {
                builder.append('{');
            }
            serializeComponent(bc, builder);
            if (extra.size() != 1) {
                builder.append('}');
            }
        }
    }

    private static void serializeTextComponent(TextComponent componet, StringBuilder builder) {
        escapeString(componet.getText(), builder);
    }

    private static void serializeTranslatableComponent(TranslatableComponent component, StringBuilder builder) {
        builder.append("\\t{");
        builder.append(component.getTranslate());

        for (BaseComponent with : component.getWith()) {
            builder.append('{');
            serializeComponent(with, builder);
            builder.append('}');
        }

        builder.append('}');
    }

    private static void serializeScoreComponent(ScoreComponent component, StringBuilder builder) {
        builder.append("\\s{");
        builder.append(component.getName()).append(',');
        builder.append(component.getObjective()).append(',');
        builder.append(component.getValue());
        builder.append('}');
    }

    private static void serializeSelectorComponent(SelectorComponent component, StringBuilder builder) {
        builder.append("\\@{");
        builder.append(component.getSelector());
        builder.append('}');
    }

    private static void serializeKeybindComponent(KeybindComponent component, StringBuilder builder) {
        builder.append("\\k{");
        builder.append(component.getKeybind());
        builder.append('}');
    }

    public static <T extends BaseComponent> T setColor(T component, ChatColor color) {
        component.setColor(color);
        return component;
    }

    public static TextComponent setColor(String text, ChatColor color) {
        TextComponent component = new TextComponent(text);
        component.setColor(color);
        return component;
    }

    public static <T extends BaseComponent> T setBold(T component, Boolean bold) {
        component.setBold(bold);
        return component;
    }

    public static <T extends BaseComponent> T setItalic(T component, Boolean italic) {
        component.setItalic(italic);
        return component;
    }

    public static <T extends BaseComponent> T setStrikethrough(T component, Boolean strikethrough) {
        component.setStrikethrough(strikethrough);
        return component;
    }

    public static <T extends BaseComponent> T setUnderlined(T component, Boolean underlined) {
        component.setUnderlined(underlined);
        return component;
    }

    public static <T extends BaseComponent> T setObfuscated(T component, Boolean obfuscated) {
        component.setObfuscated(obfuscated);
        return component;
    }

    public static <T extends BaseComponent> T addClickEvent(T component, ClickEvent event) {
        component.setClickEvent(event);
        return component;
    }

    public static <T extends BaseComponent> T addHoverEvent(T component, HoverEvent event) {
        component.setHoverEvent(event);
        return component;
    }

    public static <T extends BaseComponent> T addExtra(T component, BaseComponent extra) {
        component.addExtra(extra);
        return component;
    }

    public static <T extends BaseComponent> T addExtra(T component, String extra) {
        component.addExtra(extra);
        return component;
    }
}
