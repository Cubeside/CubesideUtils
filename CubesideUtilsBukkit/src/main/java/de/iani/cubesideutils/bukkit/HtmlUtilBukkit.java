package de.iani.cubesideutils.bukkit;

import java.util.Arrays;
import java.util.List;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.format.TextDecoration.State;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;

public class HtmlUtilBukkit {
    public final static String COLOR_BLACK_HEX = "#000000";

    public static String colorToHex(int rgb) {
        String color = Integer.toHexString(rgb & 0x00ffffff);
        return COLOR_BLACK_HEX.substring(0, 7 - color.length()) + color;
    }

    public static String escapeHtml(String text) {
        StringBuilder sb = new StringBuilder();
        escapeHtml(sb, text);
        return sb.toString();
    }

    public static void escapeHtml(StringBuilder out, String text) {
        int len = text.length();
        for (int i = 0; i < len; i++) {
            char c = text.charAt(i);
            switch (c) {
                case '&':
                    out.append("&amp;");
                    break;
                case '<':
                    out.append("&lt;");
                    break;
                case '>':
                    out.append("&gt;");
                    break;
                case '\'':
                    out.append("&apos;");
                    break;
                case '"':
                    out.append("&quot;");
                    break;
                default:
                    out.append(c);
            }
        }
    }

    public static String toHTML(String message) {
        return toHTML(LegacyComponentSerializer.legacySection().deserialize(message));
    }

    public static String toHTML(Component... message) {
        StringBuilder stringBuilder = new StringBuilder();
        toHTML(stringBuilder, State.FALSE, State.FALSE, Arrays.asList(message));
        return stringBuilder.toString();
    }

    private static void toHTML(StringBuilder stringBuilder, State parentStrikethrough, State parentUnderlined, List<Component> message) {
        for (Component component : message) {
            if (component instanceof TextComponent textComponent) {
                TextColor color = component.style().color();
                Integer colorRGB = color == null ? null : ((color.red() << 16) | (color.green() << 8) | color.blue());
                State bold = component.style().decoration(TextDecoration.BOLD);
                State italic = component.style().decoration(TextDecoration.ITALIC);
                State strikethrough = component.style().decoration(TextDecoration.STRIKETHROUGH);
                State underlined = component.style().decoration(TextDecoration.UNDERLINED);
                boolean anyFormat = colorRGB != null || bold != State.NOT_SET || italic != State.NOT_SET || strikethrough != State.NOT_SET || underlined != State.NOT_SET;
                if (anyFormat) {
                    stringBuilder.append("<span style='");
                    if (colorRGB != null) {
                        stringBuilder.append("color:").append(colorToHex(colorRGB)).append(";");
                    }
                    if (bold != State.NOT_SET) {
                        stringBuilder.append("font-weight:").append(bold == State.TRUE ? "bold" : "normal").append(";");
                    }
                    if (italic != State.NOT_SET) {
                        stringBuilder.append("font-style:").append(italic == State.TRUE ? "italic" : "normal").append(";");
                    }
                    if (strikethrough != State.NOT_SET || underlined != State.NOT_SET) {
                        if (strikethrough == State.NOT_SET) {
                            strikethrough = parentStrikethrough;
                        }
                        if (underlined == State.NOT_SET) {
                            underlined = parentUnderlined;
                        }
                        stringBuilder.append("text-decoration:");
                        if (strikethrough == State.TRUE) {
                            stringBuilder.append("line-through");
                        }
                        if (underlined == State.TRUE) {
                            if (strikethrough == State.TRUE) {
                                stringBuilder.append(" ");
                            }
                            stringBuilder.append("underline");
                        } else if (strikethrough == State.FALSE) {
                            stringBuilder.append("none");
                        }
                        stringBuilder.append(";");
                    }
                    stringBuilder.append("'>");
                }

                escapeHtml(stringBuilder, textComponent.content());
                if (!textComponent.children().isEmpty()) {
                    toHTML(stringBuilder, strikethrough != null ? strikethrough : parentStrikethrough, underlined != null ? underlined : parentUnderlined, textComponent.children());
                }

                if (anyFormat) {
                    stringBuilder.append("</span>");
                }
            } else {
                toHTML(stringBuilder, parentStrikethrough, parentUnderlined, List.of(LegacyComponentSerializer.legacySection().deserialize(LegacyComponentSerializer.legacySection().serialize(component))));
            }
        }
    }
}
