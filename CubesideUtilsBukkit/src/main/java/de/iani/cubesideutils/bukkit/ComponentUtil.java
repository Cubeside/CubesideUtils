package de.iani.cubesideutils.bukkit;

import de.iani.cubesideutils.StringUtil;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
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

public class ComponentUtil {
    private ComponentUtil() {
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
            for (; index < to; index++) {
                char current = text.charAt(index);
                if (current == '&') {
                    char next = charAtOrException(index + 1);
                    // if next is a "&", append "&" and skip next
                    if (next == '&') {
                        currentBuilder.append('&');
                        index++;
                        continue;
                    }

                    // try to parse the color code
                    if (ChatColor.getByChar(next) != null || next == 'x') {
                        ChatColor color;
                        if (next == 'x') {
                            if (index + 2 + 6 > to) {
                                throw new ParseException("illegal hex code", index);
                            }
                            color = StringUtil.parseHexColor(text, index + 2);
                            if (color == null) {
                                throw new ParseException("illegal hex code", index);
                            }
                            index += 7;
                        } else {
                            color = ChatColor.getByChar(next);
                            index += 1;
                        }

                        finishComponent();

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

                    throw new ParseException("unknown color code &" + next, index);

                } else if (current == '\\') {
                    char next = charAtOrException(index + 1);

                    // escaped characters
                    if (next == '\\' || next == '&' || next == '{' || next == '}') {
                        currentBuilder.append(next);
                        index++;
                        continue;
                    }

                    // newline
                    if (next == 'n') {
                        currentBuilder.append('\n');
                        index++;
                        continue;
                    }

                    // reset
                    if (next == 'r') {
                        char resetType = charAtOrException(index + 2);
                        if (resetType != 'a' && resetType != 'e' && resetType != 'f') {
                            throw new ParseException("unknown reset type " + resetType, index + 2);
                        }

                        finishComponent(resetType == 'a' ? FormatRetention.NONE : resetType == 'e' ? FormatRetention.FORMATTING : FormatRetention.EVENTS);
                        index += 2;
                        continue;
                    }

                    // hover event
                    if (next == 'h') {
                        char actionType = charAtOrException(index + 2);
                        if (actionType != 't' && actionType != 'i' && actionType != 'e') {
                            throw new ParseException("unknown action type " + actionType, index + 2);
                        }
                        if (charAtOrException(index + 3) != '{') {
                            throw new ParseException("expected {", index + 3);
                        }

                        int contentStartIndex = index + 4;
                        int contentEndIndex = findMatchingRightBrace(text, index + 3, to);

                        HoverEvent.Action action;
                        Content[] content;

                        if (actionType == 't') {
                            action = HoverEvent.Action.SHOW_TEXT;
                            content = new Content[] { new Text(new BaseComponent[] { convertEscaped(text, contentStartIndex, contentEndIndex) }) };
                        } else {
                            throw new ParseException("action types i and e are currently not implemented", index + 2);
                        }

                        finishComponent();
                        currentComponent.setHoverEvent(new HoverEvent(action, content));
                        index = contentEndIndex;
                        continue;
                    }

                    // click event
                    if (next == 'c') {
                        char actionType = charAtOrException(index + 2);

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
                            throw new ParseException("action type f is rejected by clients", index + 2);
                        } else {
                            throw new ParseException("unknown action type " + actionType, index + 2);
                        }

                        if (charAtOrException(index + 3) != '{') {
                            throw new ParseException("expected {", index + 3);
                        }

                        int contentStartIndex = index + 4;
                        int contentEndIndex = findMatchingRightBrace(text, index + 3, to);

                        String value = convertEscapedString(contentStartIndex, contentEndIndex);

                        finishComponent();
                        currentComponent.setClickEvent(new ClickEvent(action, value));
                        index = contentEndIndex;
                        continue;
                    }

                    // translated component
                    if (next == 't') {
                        if (charAtOrException(index + 2) != '{') {
                            throw new ParseException("expected {", index + 2);
                        }

                        int contentStartIndex = index + 3;
                        int contentEndIndex = findMatchingRightBrace(text, index + 2, to);

                        int translationKeyEndIndex = text.substring(contentStartIndex, contentEndIndex).indexOf('{');
                        if (translationKeyEndIndex < 0) {
                            translationKeyEndIndex = contentEndIndex;
                        } else {
                            translationKeyEndIndex += contentStartIndex;
                        }

                        String translationKey = text.substring(contentStartIndex, translationKeyEndIndex);

                        List<BaseComponent> translationExtras = new ArrayList<>();
                        for (int blockStartIndex = translationKeyEndIndex; blockStartIndex < contentEndIndex;) {
                            if (charAtOrException(blockStartIndex) != '{') {
                                throw new ParseException("expected {", blockStartIndex);
                            }

                            int blockEndIndex = findMatchingRightBrace(text, blockStartIndex, contentEndIndex);
                            assert blockEndIndex >= 0;

                            translationExtras.add(convertEscaped(text, blockStartIndex + 1, blockEndIndex));
                            blockStartIndex = blockEndIndex + 1;
                        }

                        finishComponent();
                        currentComponent.addExtra(new TranslatableComponent(translationKey, translationExtras.toArray(new Object[translationExtras.size()])));
                        index = contentEndIndex;
                        continue;
                    }

                    // TODO \s(coreboad) \e(ntity name) \k(eybind)

                    throw new ParseException("unknown control sequence \\" + next, index);

                    // plain block
                } else if (current == '{') {
                    int closingIndex = findMatchingRightBrace(text, index, to);
                    finishComponent();

                    BaseComponent subComponent = convertEscaped(text, index + 1, closingIndex);
                    currentComponent.addExtra(subComponent);
                    index = closingIndex;
                    continue;

                    // unmatched right brace
                } else if (current == '}') {
                    throw new ParseException("unmatched right brace", index);

                    // normal character
                } else {
                    currentBuilder.append(current);
                    continue;
                }
            }

            // finish last component
            currentComponent.addExtra(currentBuilder.toString());
            components.add(currentComponent);

            return new TextComponent(components.toArray(new BaseComponent[components.size()]));
        }

        private char charAtOrException(int i) throws ParseException {
            if (i >= to) {
                throw new ParseException("unexpected end of block", to);
            }
            return text.charAt(i);
        }

        private void finishComponent() {
            finishComponent(FormatRetention.ALL);
        }

        private void finishComponent(FormatRetention retention) {
            currentComponent.addExtra(currentBuilder.toString());
            components.add(currentComponent);

            TextComponent newComponent = new TextComponent();
            newComponent.copyFormatting(currentComponent, retention, true);
            currentComponent = newComponent;

            currentBuilder = new StringBuilder();
        }

        private static int findMatchingRightBrace(String text, int leftBraceIndex, int endIndex) throws ParseException {
            int depth = 0;
            for (int i = leftBraceIndex; i < endIndex; i++) {
                switch (text.charAt(i)) {
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
            StringBuilder result = new StringBuilder();
            for (int i = from; i < to; i++) {
                char curr = text.charAt(i);
                if (curr == '\\') {
                    if (i + 1 >= to) {
                        throw new ParseException("unexpected end of block", to);
                    }
                    char next = text.charAt(i + 1);

                    if (next == '\\' || next == '{' || next == '}') {
                        result.append(next);
                    } else if (next == 'n') {
                        result.append('\n');
                    } else {
                        throw new ParseException("unknown simple control sequence \\" + next, i);
                    }
                    i++;
                } else {
                    result.append(curr);
                }
            }
            return result.toString();
        }
    }

}
