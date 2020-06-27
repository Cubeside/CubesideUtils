package de.iani.cubesideutils;

import java.util.Arrays;
import java.util.List;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;

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
        return toHTML(TextComponent.fromLegacyText(message));
    }

    public static String toHTML(BaseComponent... message) {
        StringBuilder stringBuilder = new StringBuilder();
        toHTML(stringBuilder, false, false, Arrays.asList(message));
        return stringBuilder.toString();
    }

    private static void toHTML(StringBuilder stringBuilder, boolean parentStrikethrough, boolean parentUnderlined, List<BaseComponent> message) {
        for (BaseComponent component : message) {
            if (component instanceof TextComponent) {
                ChatColor color = component.getColorRaw();
                Integer colorRGB = color == null ? null : ChatUtil.toRGB(color);
                Boolean bold = component.isBoldRaw();
                Boolean italic = component.isItalicRaw();
                Boolean strikethrough = component.isStrikethroughRaw();
                Boolean underlined = component.isUnderlinedRaw();
                boolean anyFormat = colorRGB != null || bold != null || italic != null || strikethrough != null || underlined != null;
                if (anyFormat) {
                    stringBuilder.append("<span style='");
                    if (colorRGB != null) {
                        stringBuilder.append("color:").append(colorToHex(colorRGB)).append(";");
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

                escapeHtml(stringBuilder, ((TextComponent) component).getText());
                if (component.getExtra() != null && !component.getExtra().isEmpty()) {
                    toHTML(stringBuilder, strikethrough != null ? strikethrough : parentStrikethrough, underlined != null ? underlined : parentUnderlined, component.getExtra());
                }

                if (anyFormat) {
                    stringBuilder.append("</span>");
                }
            } else {
                toHTML(stringBuilder, parentStrikethrough, parentUnderlined, Arrays.asList(TextComponent.fromLegacyText(component.toLegacyText())));
            }
        }
    }
}
