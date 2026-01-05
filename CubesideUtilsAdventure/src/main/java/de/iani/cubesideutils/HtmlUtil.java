package de.iani.cubesideutils;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.format.TextDecoration.State;

public class HtmlUtil {
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
        return toHTML(ComponentUtilAdventure.fromLegacy(message));
    }

    public static String toHTML(Component message) {
        StringBuilder stringBuilder = new StringBuilder();
        toHTML(stringBuilder, false, false, message);
        return stringBuilder.toString();
    }

    private static Boolean stateToBoolean(TextDecoration.State state) {
        return state == null || state == TextDecoration.State.NOT_SET ? null : state == State.TRUE;
    }

    private static void toHTML(StringBuilder stringBuilder, boolean parentStrikethrough, boolean parentUnderlined, Component message) {
        TextComponent component = null;
        if (message instanceof TextComponent textComponent) {
            component = textComponent;
        } else {
            component = ComponentUtilAdventure.fromLegacy(ComponentUtilAdventure.toLegacy(message));
        }

        TextColor color = message.color();
        String colorRgbHex = color == null ? null : color.asHexString();
        Boolean bold = stateToBoolean(component.decoration(TextDecoration.BOLD));
        Boolean italic = stateToBoolean(component.decoration(TextDecoration.ITALIC));
        Boolean strikethrough = stateToBoolean(component.decoration(TextDecoration.STRIKETHROUGH));
        Boolean underlined = stateToBoolean(component.decoration(TextDecoration.UNDERLINED));
        boolean anyFormat = colorRgbHex != null || bold != null || italic != null || strikethrough != null || underlined != null;
        if (anyFormat) {
            stringBuilder.append("<span style='");
            if (colorRgbHex != null) {
                stringBuilder.append("color:").append(colorRgbHex).append(";");
            }
            if (bold != null) {
                stringBuilder.append("font-weight:").append(bold ? "bold" : "normal").append(";");
            }
            if (italic != null) {
                stringBuilder.append("font-style:").append(italic ? "italic" : "normal").append(";");
            }
            if (strikethrough != null || underlined != null) {
                if (strikethrough == null) {
                    strikethrough = parentStrikethrough;
                }
                if (underlined == null) {
                    underlined = parentUnderlined;
                }
                stringBuilder.append("text-decoration:");
                if (strikethrough) {
                    stringBuilder.append("line-through");
                }
                if (underlined) {
                    if (strikethrough) {
                        stringBuilder.append(" ");
                    }
                    stringBuilder.append("underline");
                } else if (!strikethrough) {
                    stringBuilder.append("none");
                }
                stringBuilder.append(";");
            }
            stringBuilder.append("'>");
        }

        escapeHtml(stringBuilder, component.content());
        if (!component.children().isEmpty()) {
            for (Component child : component.children()) {
                toHTML(stringBuilder, strikethrough != null ? strikethrough : parentStrikethrough, underlined != null ? underlined : parentUnderlined, child);
            }
        }

        if (anyFormat) {
            stringBuilder.append("</span>");
        }
    }
}
