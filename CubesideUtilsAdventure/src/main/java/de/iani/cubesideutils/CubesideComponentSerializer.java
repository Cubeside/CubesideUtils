package de.iani.cubesideutils;

import de.iani.cubesideutils.linkextractor.LinkExtractor;
import de.iani.cubesideutils.linkextractor.LinkSegment;
import de.iani.cubesideutils.linkextractor.PlainSegment;
import de.iani.cubesideutils.linkextractor.Segment;
import java.util.ArrayList;
import java.util.List;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentLike;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.ClickEvent.Action;
import net.kyori.adventure.text.flattener.ComponentFlattener;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.serializer.ComponentSerializer;
import net.kyori.adventure.text.serializer.json.JSONComponentSerializer;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.jetbrains.annotations.NotNull;

/**
 * A serializer that preserves links and supports the cubeside rgb format &xrrggbb and double ampersand && for escaped ampersands.
 * Converts from &-marked colors to components and back.
 */
public class CubesideComponentSerializer implements ComponentSerializer<Component, TextComponent, String> {
    private final static ComponentFlattener FLATTENER = ComponentFlattener.basic().toBuilder().mapper(TextComponent.class, c -> c.clickEvent() != null && c.clickEvent().action() == Action.OPEN_URL ? c.content() : c.content().replace("&", "&&")).build();
    private final static LegacyComponentSerializer SERIALIZER = LegacyComponentSerializer.builder().character('&').hexColors().hexCharacter('x').flattener(FLATTENER).build();
    private final static LegacyComponentSerializer SERIALIZER_NO_COLOR = LegacyComponentSerializer.builder().character('&').hexColors().hexCharacter('x').build();

    private final static CubesideComponentSerializer COLORS_AND_LINKS = new CubesideComponentSerializer(true, true);
    private final static CubesideComponentSerializer COLORS_AND_NO_LINKS = new CubesideComponentSerializer(true, false);
    private final static CubesideComponentSerializer NO_COLORS_AND_LINKS = new CubesideComponentSerializer(false, true);
    private final static CubesideComponentSerializer NO_COLORS_AND_NO_LINKS = new CubesideComponentSerializer(false, false);

    private final boolean allowColors;
    private final boolean convertLinks;

    private CubesideComponentSerializer(boolean allowColors, boolean convertLinks) {
        this.allowColors = allowColors;
        this.convertLinks = convertLinks;
    }

    public static CubesideComponentSerializer instance(boolean allowColors, boolean convertLinks) {
        if (allowColors) {
            return convertLinks ? COLORS_AND_LINKS : COLORS_AND_NO_LINKS;
        } else {
            return convertLinks ? NO_COLORS_AND_LINKS : NO_COLORS_AND_NO_LINKS;
        }
    }

    @Override
    public @NotNull TextComponent deserialize(@NotNull String input) {
        List<Segment> segments = convertLinks ? LinkExtractor.extractLinks(input) : List.of(new PlainSegment(input));
        ArrayList<ComponentBuilder> parts = new ArrayList<>();
        ComponentBuilder currentPart = null;
        Style currentStyle = Style.empty();
        StringBuilder builder = new StringBuilder();
        for (Segment segment : segments) {
            if (segment instanceof PlainSegment plain) {
                String text = plain.value();
                if (!allowColors) {
                    if (currentPart == null) {
                        currentPart = new ComponentBuilder(text);
                        parts.add(currentPart);
                    } else {
                        currentPart.children().add(new ComponentBuilder(text));
                    }
                } else {
                    // bis zum ersten farb/style-code direkt hinzufügen
                    // farbe: neuen node am toplevel anbauen
                    // style: neuen node untergeordnet
                    // reset: back zum toplevel
                    builder.setLength(0);
                    int len = text.length();
                    for (int i = 0; i < len; i++) {
                        char current = text.charAt(i);
                        if (current == '&' && i + 1 < len) {
                            char next = Character.toLowerCase(text.charAt(i + 1));
                            // if next is a "&" skip next char
                            // if its a color char replace the "&"
                            if (NamedChatColor.getByCode(next) != null || next == '&' || next == 'x' || next == '#') {
                                i++;
                                if (next != '&') {
                                    if (next == 'x' || next == '#') {
                                        Integer hex = StringUtilCore.parseHexColor(text, i + 1);
                                        if (hex == null) {
                                            builder.append(current).append(next); // not a parseable color so just keep the content
                                        } else {
                                            if (!builder.isEmpty()) {
                                                if (currentPart == null) {
                                                    currentPart = new ComponentBuilder(builder.toString(), currentStyle);
                                                    parts.add(currentPart);
                                                } else {
                                                    currentPart.children().add(new ComponentBuilder(builder.toString(), currentStyle));
                                                }
                                                builder.setLength(0);
                                            }
                                            currentPart = null;
                                            currentStyle = Style.style(TextColor.color(hex));
                                            i += 6;
                                        }
                                    } else if (next == 'r' || (next >= '0' && next <= '9') || (next >= 'a' && next <= 'f')) {
                                        if (!builder.isEmpty()) {
                                            if (currentPart == null) {
                                                currentPart = new ComponentBuilder(builder.toString(), currentStyle);
                                                parts.add(currentPart);
                                            } else {
                                                currentPart.children().add(new ComponentBuilder(builder.toString(), currentStyle));
                                            }
                                            builder.setLength(0);
                                        }
                                        currentPart = null;
                                        if (next == 'r') {
                                            currentStyle = Style.empty();
                                        } else {
                                            currentStyle = Style.style(ComponentUtilAdventure.COLOR_CHARS.get(next));
                                        }
                                    } else {
                                        TextDecoration deco = ComponentUtilAdventure.DECORATION_CHARS.get(next);
                                        if (deco == null) {
                                            builder.append(current).append(next); // not a parseable style so just keep the content
                                        } else {
                                            if (!builder.isEmpty()) {
                                                ComponentBuilder newPart = new ComponentBuilder(builder.toString(), currentStyle);
                                                if (currentPart == null) {
                                                    currentPart = newPart;
                                                    parts.add(currentPart);
                                                } else {
                                                    currentPart.children().add(newPart);
                                                    currentPart = newPart;
                                                }
                                                builder.setLength(0);

                                                currentStyle = Style.style(deco);
                                            } else {
                                                currentStyle = currentStyle.decorate(deco);
                                            }
                                        }
                                    }
                                    continue;
                                }
                            }
                        }
                        builder.append(current);
                    }
                    if (!builder.isEmpty() || !currentStyle.isEmpty()) {
                        ComponentBuilder newPart = new ComponentBuilder(builder.toString(), currentStyle);
                        if (currentPart == null) {
                            currentPart = newPart;
                            parts.add(currentPart);
                        } else {
                            currentPart.children().add(newPart);
                            currentPart = newPart;
                        }
                        builder.setLength(0);
                        currentStyle = Style.empty();
                    }
                }
            } else if (segment instanceof LinkSegment link) {
                String linkUrl = link.value();
                if (!linkUrl.regionMatches(true, 0, "https://", 0, 8) && !linkUrl.regionMatches(true, 0, "http://", 0, 7)) {
                    linkUrl = "https://" + linkUrl;
                }
                if (currentPart == null) {
                    currentPart = new ComponentBuilder("");
                    parts.add(currentPart);
                }
                currentPart.children().add(Component.text(link.value()).clickEvent(ClickEvent.openUrl(linkUrl)));
            }
        }
        if (parts.isEmpty()) {
            return Component.empty();
        } else if (parts.size() == 1) {
            return parts.get(0).asComponent();
        } else {
            if (parts.get(0).style.isEmpty()) {
                ComponentBuilder top = parts.remove(0);
                top.children().addAll(parts);
                return top.asComponent();
            } else {
                return Component.empty().children(parts);
            }
        }
    }

    @Override
    public @NotNull String serialize(@NotNull Component component) {
        return allowColors ? SERIALIZER.serialize(component) : SERIALIZER_NO_COLOR.serialize(component);
    }

    static record ComponentBuilder(String value, ArrayList<ComponentLike> children, Style style) implements ComponentLike {
        public ComponentBuilder(String value) {
            this(value, Style.empty());
        }

        public ComponentBuilder(String value, Style style) {
            this(value, new ArrayList<>(), style);
        }

        @Override
        public @NotNull TextComponent asComponent() {
            return Component.text(value).style(style).children(children);
        }
    }

    public static void main(String[] args) {
        // LegacyComponentSerializer ser = LegacyComponentSerializer.legacyAmpersand();
        JSONComponentSerializer ser = JSONComponentSerializer.json();
        CubesideComponentSerializer serC = CubesideComponentSerializer.instance(false, true);
        System.out.println(serC.serialize(serC.deserialize("&x123456&laa&af&khi!")));
        System.out.println(serC.serialize(serC.deserialize("&laa&af&khi!")));
        System.out.println(serC.serialize(serC.deserialize("&laa&af&khi! www.web.de&c a")));
        System.out.println(serC.serialize(serC.deserialize("&laa&af&k&&hi! &lhttps://www.web.de/a?foo=d&a=f &raa")));
    }
}
