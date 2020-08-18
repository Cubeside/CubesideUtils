package de.iani.cubesideutils.bukkit;

import de.iani.cubesideutils.StringUtil;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder.FormatRetention;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.TranslatableComponent;
import net.md_5.bungee.api.chat.hover.content.Content;
import net.md_5.bungee.api.chat.hover.content.Text;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionType;

public class ComponentUtils {
    private ComponentUtils() {
        throw new UnsupportedOperationException("No instance for you, Sir!");
        // prevents instances
    }

    public static BaseComponent createTranslatableComponentFor(Material m) {
        String key = m.getKey().getKey();
        if (key.startsWith("music_disc_") || key.endsWith("_banner_pattern")) {
            return new TextComponent(new TranslatableComponent("item.minecraft." + key), new TextComponent(": "), new TranslatableComponent("item.minecraft." + key + ".desc"));
        }
        return new TranslatableComponent((m.isBlock() ? "block.minecraft." : "item.minecraft.") + key);
    }

    public static BaseComponent createTranslatableComponentFor(ItemStack stack) {
        Material m = stack.getType();
        if (m == Material.TIPPED_ARROW || m == Material.POTION || m == Material.SPLASH_POTION || m == Material.LINGERING_POTION) {
            ItemMeta meta = stack.getItemMeta();
            if (meta instanceof PotionMeta) {
                String key = m.getKey().getKey();
                PotionType type = ((PotionMeta) meta).getBasePotionData().getType();
                return new TranslatableComponent("item.minecraft." + key + ".effect." + getInternalPotionName(type));
            }
        }
        String key = m.getKey().getKey();
        if (key.startsWith("music_disc_")) {
            return new TextComponent(new TranslatableComponent("item.minecraft." + key), new TextComponent(": "), new TranslatableComponent("item.minecraft." + key + ".desc"));
        }
        return new TranslatableComponent((m.isBlock() ? "block.minecraft." : "item.minecraft.") + key);
    }

    public static BaseComponent createTranslatableComponentFor(EntityType t) {
        return new TranslatableComponent("entity.minecraft." + t.getKey().getKey());
    }

    public static BaseComponent createTranslatableComponentFor(Entity e) {
        return createTranslatableComponentFor(e.getType());
    }

    private static String getInternalPotionName(PotionType t) {
        switch (t) {
            case UNCRAFTABLE:
                return "empty";
            case JUMP:
                return "leaping";
            case SPEED:
                return "swiftness";
            case INSTANT_HEAL:
                return "healing";
            case INSTANT_DAMAGE:
                return "harming";
            case REGEN:
                return "regeneration";
            default:
                return t.name().toLowerCase();
        }
    }

    /* DO NOT USE ANYTHING BELOW HERE! UNTESTED! */

    private static final Pattern ESCAPED_LEFT_BRACE_PATTERN = Pattern.compile("\\\\\\{");
    private static final Pattern ESCAPED_RIGHT_BRACE_PATTERN = Pattern.compile("\\\\\\}");

    public static BaseComponent convertEscaped(String text) throws ParseException {
        return convertEscaped(text, 0, text.length());
    }

    public static BaseComponent convertEscaped(String text, int from, int to) throws ParseException {
        List<BaseComponent> components = new ArrayList<>();

        StringBuilder currentBuilder = new StringBuilder();
        TextComponent currentComponent = new TextComponent();
        for (int i = from; i < to; i++) {
            char current = text.charAt(i);
            switch (current) {
                case '&':
                    if (i + 1 >= to) {
                        throw new ParseException("unexpected end of string", i + 1);
                    }
                    char next = text.charAt(i + 1);
                    // if next is a "&", append "&" and skip next
                    if (next == '&') {
                        currentBuilder.append('&');
                        i++;
                        continue;
                    }

                    // try to parse the color code
                    if (ChatColor.getByChar(next) != null || next == 'x') {
                        ChatColor color;
                        if (next == 'x') {
                            if (i + 2 + 6 > to) {
                                throw new ParseException("illegal hex code", i);
                            }
                            color = StringUtil.parseHexColor(text, i + 2);
                            if (color == null) {
                                throw new ParseException("illegal hex code", i);
                            }
                            i += 7;
                        } else {
                            color = ChatColor.getByChar(next);
                            i += 1;
                        }

                        currentComponent.addExtra(currentBuilder.toString());
                        components.add(currentComponent);

                        currentComponent = currentComponent.duplicate();
                        currentComponent.setExtra(Collections.emptyList());
                        currentBuilder = new StringBuilder();

                        if (color == ChatColor.BOLD) {
                            currentComponent.setBold(true);
                        } else if (color == ChatColor.ITALIC) {
                            currentComponent.setItalic(true);
                        } else if (color == ChatColor.MAGIC) {
                            currentComponent.setObfuscated(true);
                        } else if (color == ChatColor.STRIKETHROUGH) {
                            currentComponent.setStrikethrough(true);
                        } else if (color == ChatColor.UNDERLINE) {
                            currentComponent.setUnderlined(true);
                        } else {
                            // component.setBold(false);
                            // component.setItalic(false);
                            // component.setObfuscated(false);
                            // component.setStrikethrough(false);
                            // component.setUnderlined(false);
                            currentComponent.setColor(color);
                        }
                        continue;
                    }

                    throw new ParseException("character after '&' was neither a color char nor '&' or 'x'", i);

                case '\\':
                    if (i + 1 >= to) {
                        throw new ParseException("unexpected end of string", i + 1);
                    }
                    next = text.charAt(i + 1);

                    if (next == '\\' || next == '&' || next == '{' || next == '}') {
                        currentBuilder.append(next);
                        i++;
                        continue;
                    }

                    if (next == 'n') {
                        currentBuilder.append('\n');
                        continue;
                    }
                    if (next == 't') {
                        currentBuilder.append('\t');
                        continue;
                    }

                    if (next == 'r') {
                        if (i + 2 >= to) {
                            throw new ParseException("unexpected end of string", to);
                        }
                        char resetType = text.charAt(i + 2);
                        if (resetType != 'a' && resetType != 'e' && resetType != 'f') {
                            throw new ParseException("unknown reset type " + resetType, i + 2);
                        }

                        currentComponent.addExtra(currentBuilder.toString());
                        components.add(currentComponent);

                        TextComponent newComponent = new TextComponent();
                        newComponent.copyFormatting(currentComponent, resetType == 'a' ? FormatRetention.NONE : resetType == 'e' ? FormatRetention.FORMATTING : FormatRetention.EVENTS, true);

                        currentComponent = newComponent;
                        currentBuilder = new StringBuilder();
                        continue;
                    }

                    if (next == 'h') {
                        if (i + 3 >= to) {
                            throw new ParseException("unexpected end of string", to);
                        }
                        char actionType = text.charAt(i + 2);
                        if (actionType != 't' && actionType != 'i' && actionType != 'e') {
                            throw new ParseException("unknown action type " + actionType, i + 2);
                        }
                        if (text.charAt(i + 3) != '{') {
                            throw new ParseException("expected {", i + 3);
                        }

                        int contentStartIndex = i + 4;
                        int contentEndIndex = findMatchingRightBrace(text, i + 3, to);
                        if (contentEndIndex < 0) {
                            throw new ParseException("unmatched left brace", i + 3);
                        }

                        HoverEvent.Action action;
                        Content[] content;

                        if (actionType == 't') {
                            action = HoverEvent.Action.SHOW_TEXT;
                            content = new Content[] { new Text(new BaseComponent[] { convertEscaped(text, contentStartIndex, contentEndIndex) }) };
                        } else {
                            throw new ParseException("action types i and e are currently not implemented", i + 2);
                        }

                        currentComponent.addExtra(currentBuilder.toString());
                        components.add(currentComponent);

                        currentComponent = currentComponent.duplicate();
                        currentComponent.setExtra(Collections.emptyList());
                        currentComponent.setHoverEvent(new HoverEvent(action, content));
                        currentBuilder = new StringBuilder();
                    }

                    if (next == 'c') {
                        if (i + 3 >= to) {
                            throw new ParseException("unexpected end of string", to);
                        }
                        char actionType = text.charAt(i + 2);

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
                            throw new ParseException("action type f is rejected by clients", i + 2);
                        } else {
                            throw new ParseException("unknown action type " + actionType, i + 2);
                        }

                        if (text.charAt(i + 3) != '{') {
                            throw new ParseException("expected {", i + 3);
                        }

                        int contentStartIndex = i + 4;
                        int contentEndIndex = findMatchingRightBrace(text, i + 3, to);
                        if (contentEndIndex < 0) {
                            throw new ParseException("unmatched left brace", i + 3);
                        }

                        String value = text.substring(contentEndIndex, contentStartIndex);
                        value = ESCAPED_LEFT_BRACE_PATTERN.matcher(value).replaceAll("{");
                        value = ESCAPED_RIGHT_BRACE_PATTERN.matcher(value).replaceAll("}");

                        currentComponent.addExtra(currentBuilder.toString());
                        components.add(currentComponent);

                        currentComponent = currentComponent.duplicate();
                        currentComponent.setExtra(Collections.emptyList());
                        currentComponent.setClickEvent(new ClickEvent(action, value));
                        currentBuilder = new StringBuilder();
                    }

                    // TODO \l(ocalized) \s(coreboad) \e(ntity name) \k(eybind)

                    break;

                case '{':
                    int closingIndex = findMatchingRightBrace(text, i + 1, to);
                    if (closingIndex < 0) {
                        throw new ParseException("unmatched left brace", i);
                    }

                    currentComponent.addExtra(currentBuilder.toString());
                    components.add(currentComponent);

                    currentComponent = currentComponent.duplicate();
                    currentComponent.setExtra(Collections.emptyList());
                    currentBuilder = new StringBuilder();

                    BaseComponent subComponent = convertEscaped(text, i + 2, closingIndex);
                    currentComponent.addExtra(subComponent);
                    i = closingIndex;
                    continue;

                case '}':
                    throw new ParseException("unmatched right brace", i);

                default:
                    currentBuilder.append(current);
                    continue;
            }
        }

        currentComponent.addExtra(currentBuilder.toString());
        components.add(currentComponent);

        return new TextComponent(components.toArray(new BaseComponent[components.size()]));
    }

    private static int findMatchingRightBrace(String text, int leftBraceIndex, int endIndex) {
        return findMatchingRightBrace(text, leftBraceIndex, endIndex, false);
    }

    private static int findMatchingRightBrace(String text, int leftBraceIndex, int endIndex, boolean ignoreLeftBraces) {
        int depth = ignoreLeftBraces ? 1 : 0;
        for (int i = leftBraceIndex; i < endIndex; i++) {
            switch (text.charAt(i)) {
                case '\\':
                    i++;
                    break;
                case '{':
                    depth += ignoreLeftBraces ? 0 : 1;
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
        return -1;
    }
}
