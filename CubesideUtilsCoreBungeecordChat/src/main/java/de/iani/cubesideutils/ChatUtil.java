package de.iani.cubesideutils;

import static net.kyori.adventure.text.Component.empty;
import static net.kyori.adventure.text.Component.space;
import static net.kyori.adventure.text.Component.text;

import de.iani.cubesideutils.plugin.CubesideUtils;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.IntSupplier;
import java.util.logging.Level;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.chat.ComponentSerializer;

public abstract class ChatUtil {
    protected ChatUtil() {
        throw new UnsupportedOperationException("No instance for you, Sir!");
        // prevents instances
    }

    private static final Map<ChatColor, Integer> CHATCOLOR_TO_RGB;
    static {
        Map<ChatColor, Integer> chatcolorToColor = new HashMap<>();
        chatcolorToColor.put(ChatColor.BLACK, 0x000000);
        chatcolorToColor.put(ChatColor.DARK_BLUE, 0x0000AA);
        chatcolorToColor.put(ChatColor.DARK_GREEN, 0x00AA00);
        chatcolorToColor.put(ChatColor.DARK_AQUA, 0x00AAAA);
        chatcolorToColor.put(ChatColor.DARK_RED, 0xAA0000);
        chatcolorToColor.put(ChatColor.DARK_PURPLE, 0xAA00AA);
        chatcolorToColor.put(ChatColor.GOLD, 0xFFAA00);
        chatcolorToColor.put(ChatColor.GRAY, 0xAAAAAA);
        chatcolorToColor.put(ChatColor.DARK_GRAY, 0x555555);
        chatcolorToColor.put(ChatColor.BLUE, 0x5555FF);
        chatcolorToColor.put(ChatColor.GREEN, 0x55FF55);
        chatcolorToColor.put(ChatColor.AQUA, 0x55FFFF);
        chatcolorToColor.put(ChatColor.RED, 0xFF5555);
        chatcolorToColor.put(ChatColor.LIGHT_PURPLE, 0xFF55FF);
        chatcolorToColor.put(ChatColor.YELLOW, 0xFFFF55);
        chatcolorToColor.put(ChatColor.WHITE, 0xFFFFFF);

        CHATCOLOR_TO_RGB = Collections.unmodifiableMap(chatcolorToColor);
    }

    public static interface MessageReceiver {

        public void sendMessage(String message);

        public void sendMessage(Component message);

        @Deprecated
        public default void sendMessage(BaseComponent... message) {
            sendMessage(convertBaseComponents(message));
        }
    }

    public static interface Sendable<T> extends Consumer<T> {

        public void send(T recipient);

        @Override
        public default void accept(T t) {
            send(t);
        }
    }

    public record StringMsg(String message) implements Sendable<MessageReceiver> {

        @Override
        public void send(MessageReceiver recipient) {
            recipient.sendMessage(this.message);
        }
    }

    public record AdventureComponentMsg(Component message) implements Sendable<MessageReceiver> {

        @Override
        public void send(MessageReceiver recipient) {
            recipient.sendMessage(this.message);
        }

    }

    @Deprecated
    public static class BaseComponentMsg implements Sendable<MessageReceiver> {

        public final BaseComponent[] message;

        public BaseComponentMsg(BaseComponent... message) {
            this.message = message;
        }

        public BaseComponentMsg(BaseComponent message) {
            this.message = new BaseComponent[] { message };
        }

        @Override
        public void send(MessageReceiver recipient) {
            recipient.sendMessage(this.message);
        }
    }

    @Deprecated
    public static Component convertBaseComponent(BaseComponent bc) {
        return GsonComponentSerializer.gson().deserialize(ComponentSerializer.toString(bc));
    }

    @Deprecated
    public static Component convertBaseComponents(BaseComponent... bc) {
        return GsonComponentSerializer.gson().deserialize(ComponentSerializer.toString(bc));
    }

    @Deprecated
    public static Component convertLegacy(String s) {
        return LegacyComponentSerializer.legacySection().deserialize(s);
    }

    @Deprecated
    public static Style convertStyle(String colorString) {
        return convertLegacy(colorString).style();
    }

    public static List<Sendable<MessageReceiver>> stringToSendableList(List<String> messages) {
        List<Sendable<MessageReceiver>> result = new ArrayList<>(messages.size());
        for (String msg : messages) {
            result.add(new StringMsg(msg));
        }
        return result;
    }

    public static List<Sendable<MessageReceiver>> componentToSendableList(List<Component> messages) {
        List<Sendable<MessageReceiver>> result = new ArrayList<>(messages.size());
        for (Component msg : messages) {
            result.add(new AdventureComponentMsg(msg));
        }
        return result;
    }

    @Deprecated
    public static List<Sendable<MessageReceiver>> bcToSendableList(List<BaseComponent[]> messages) {
        List<Sendable<MessageReceiver>> result = new ArrayList<>(messages.size());
        for (BaseComponent[] msg : messages) {
            result.add(new BaseComponentMsg(msg));
        }
        return result;
    }

    public static class CachedSendableList<T extends Sendable<S>, S> extends AbstractList<T> {

        private IntSupplier sizeGetter;
        private BiFunction<Integer, Integer, List<T>> listFiller;

        private int cacheSize;
        private int cacheStartIndex;
        private List<T> cache;

        public CachedSendableList(IntSupplier sizeGetter, BiFunction<Integer, Integer, List<T>> listFiller, int cacheSize) {
            this.sizeGetter = sizeGetter;
            this.listFiller = listFiller;
            this.cacheSize = cacheSize;
        }

        public CachedSendableList(IntSupplier sizeGetter, BiFunction<Integer, Integer, List<T>> listFiller) {
            this(sizeGetter, listFiller, PAGE_LENGTH);
        }

        @Override
        public T get(int index) {
            int transformedIndex = index - this.cacheStartIndex;
            if (this.cache == null || transformedIndex < 0 || transformedIndex >= this.cacheSize) {
                this.cache = this.listFiller.apply(index, this.cacheSize);
                this.cacheStartIndex = index;
                transformedIndex = 0;
            }
            return this.cache.get(transformedIndex);
        }

        @Override
        public int size() {
            return this.sizeGetter.getAsInt();
        }

    }

    public static class AbortPageSendException extends RuntimeException {

        private static final long serialVersionUID = -2300346467867854669L;

        public AbortPageSendException() {
            super();
        }

        public AbortPageSendException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
            super(message, cause, enableSuppression, writableStackTrace);
        }

        public AbortPageSendException(String message, Throwable cause) {
            super(message, cause);
        }

        public AbortPageSendException(String message) {
            super(message);
        }

        public AbortPageSendException(Throwable cause) {
            super(cause);
        }

    }

    public static final int PAGE_LENGTH = 10;

    public static <T extends MessageReceiver> void sendMessagesPaged(T recipient, List<? extends Sendable<T>> messages, int page, String name, String openPageCommandPrefix) {
        sendMessagesPaged(recipient, messages, page, Component.text(name), openPageCommandPrefix);
    }

    @Deprecated
    public static <T extends MessageReceiver> void sendMessagesPaged(T recipient, List<? extends Sendable<T>> messages, int page, String name, String openPageCommandPrefix, String pluginPrefix) {
        sendMessagesPaged(recipient, messages, page, name, openPageCommandPrefix, pluginPrefix, ChatColor.GREEN, ChatColor.GOLD);
    }

    @Deprecated
    public static <T extends MessageReceiver> void sendMessagesPaged(T recipient, List<? extends Sendable<T>> messages, int page, String name, String openPageCommandPrefix, String pluginPrefix, ChatColor normalColor, ChatColor warningColor) {
        sendMessagesPaged(recipient, messages, page, new ComponentBuilder(name).create(), openPageCommandPrefix, pluginPrefix, normalColor, warningColor);
    }

    @Deprecated
    public static <T extends MessageReceiver> void sendMessagesPaged(T recipient, List<? extends Sendable<T>> messages, int page, BaseComponent[] name, String openPageCommandPrefix) {
        sendMessagesPaged(recipient, messages, page, name, openPageCommandPrefix, "");
    }

    @Deprecated
    public static <T extends MessageReceiver> void sendMessagesPaged(T recipient, List<? extends Sendable<T>> messages, int page, BaseComponent[] name, String openPageCommandPrefix, String pluginPrefix) {
        sendMessagesPaged(recipient, messages, page, name, openPageCommandPrefix, pluginPrefix, ChatColor.GREEN, ChatColor.GOLD);
    }

    @Deprecated
    public static <T extends MessageReceiver> void sendMessagesPaged(T recipient, List<? extends Sendable<T>> messages, int page, BaseComponent[] name, String openPageCommandPrefix, String pluginPrefix, ChatColor normalColor, ChatColor warningColor) {
        //CubesideUtils.getInstance().getLogger().log(Level.WARNING, "Outdatet call to sendMessagesPaged.", new Throwable());
        sendMessagesPaged(recipient, messages, page, convertBaseComponents(name), openPageCommandPrefix, convertLegacy(pluginPrefix), convertStyle(normalColor.toString()), convertStyle(warningColor.toString()));
    }

    public static <T extends MessageReceiver> void sendMessagesPaged(T recipient, List<? extends Sendable<T>> messages, int page, Component name, String openPageCommandPrefix) {
        sendMessagesPaged(recipient, messages, page, name, openPageCommandPrefix, null);
    }

    public static <T extends MessageReceiver> void sendMessagesPaged(T recipient, List<? extends Sendable<T>> messages, int page, Component name, String openPageCommandPrefix, Component pluginPrefix) {
        sendMessagesPaged(recipient, messages, page, name, openPageCommandPrefix, pluginPrefix, Style.style(NamedTextColor.GREEN), Style.style(NamedTextColor.GOLD));
    }

    public static <T extends MessageReceiver> void sendMessagesPaged(T recipient, List<? extends Sendable<T>> messages, int page, Component name, String openPageCommandPrefix, Component pluginPrefix, Style normalStyle, Style warningStyle) {
        try {
            if (page < 0) {
                sendMessage(recipient, pluginPrefix, warningStyle, "Bitte gib die Seitenzahl als positive ganze Zahl an.");
                return;
            }

            Component prefixComponent = pluginPrefix == null ? empty() : pluginPrefix.append(space());

            int listSize = messages.size();
            int numPages = (int) Math.ceil(listSize / (double) PAGE_LENGTH);
            if (page >= numPages && page > 0) {
                Component result = empty().style(warningStyle).append(prefixComponent).append(name).append(text(" hat keine Seite " + (page + 1)));
                recipient.sendMessage(result);
                return;
            }

            if (!openPageCommandPrefix.startsWith("/")) {
                openPageCommandPrefix = "/" + openPageCommandPrefix;
            }

            if (numPages > 1) {
                Component result = empty().style(normalStyle).append(prefixComponent).append(name).append(text(" (Seite " + (page + 1) + "/" + numPages + "):"));
                recipient.sendMessage(result);
            } else {
                Component result = empty().style(normalStyle).append(prefixComponent).append(name).append(text(":"));
                recipient.sendMessage(result);
            }

            if (listSize == 0) {
                recipient.sendMessage(text(" -- keine --").color(NamedTextColor.GRAY));
            }

            int index = page * PAGE_LENGTH;
            for (int i = 0; i < PAGE_LENGTH && index < listSize;) {
                messages.get(index).send(recipient);

                i++;
                index++;
            }

            if (numPages > 1) {
                sendMessage(recipient, pluginPrefix, normalStyle, "Seite x anzeigen: ", openPageCommandPrefix, " x");

                Component prevComponent = text(" << vorherige");
                if (page > 0) {
                    prevComponent = prevComponent.color(NamedTextColor.BLUE);

                    HoverEvent<Component> he = HoverEvent.showText(text("Seite " + page + " anzeigen"));
                    ClickEvent ce = ClickEvent.runCommand(openPageCommandPrefix + " " + page);

                    prevComponent = prevComponent.hoverEvent(he).clickEvent(ce);
                } else {
                    prevComponent = prevComponent.color(NamedTextColor.GRAY);

                    HoverEvent<Component> he = HoverEvent.showText(text("Bereits auf Seite 1"));

                    prevComponent = prevComponent.hoverEvent(he);
                }

                Component nextComponent = text("nächste >>");

                if (page + 1 < numPages) {
                    nextComponent = nextComponent.color(NamedTextColor.BLUE);

                    HoverEvent<Component> he = HoverEvent.showText(text("Seite " + (page + 2) + " anzeigen"));
                    ClickEvent ce = ClickEvent.runCommand(openPageCommandPrefix + " " + (page + 2));

                    nextComponent = nextComponent.hoverEvent(he).clickEvent(ce);
                } else {
                    nextComponent = nextComponent.color(NamedTextColor.GRAY);

                    HoverEvent<Component> he = HoverEvent.showText(text("Bereits auf Seite " + numPages));

                    nextComponent = nextComponent.hoverEvent(he);
                }

                Component result = empty().append(pluginPrefix).append(prevComponent).append(text("   ")).append(nextComponent);
                recipient.sendMessage(result);
            }
        } catch (AbortPageSendException e) {
            return;
        }
    }

    @Deprecated
    public static void sendMessage(MessageReceiver receiver, String pluginPrefix, String colors, Object... messageParts) {
        //CubesideUtils.getInstance().getLogger().log(Level.WARNING, "Outdatet call to sendMessagesPaged.", new Throwable());
        sendMessage(receiver, convertLegacy(pluginPrefix), convertStyle(colors), messageParts);
    }

    public static void sendMessage(MessageReceiver receiver, Component pluginPrefix, Style style, Object... messageParts) {
        Component result = empty().style(style);
        if (pluginPrefix != null) {
            result = result.append(pluginPrefix).append(space());
        }

        if (style != null) {
            result = result.style(style);
        }

        int outdated = 0;
        for (Object s : messageParts) {
            if (s instanceof BaseComponent[] bc) {
                result = result.append(convertBaseComponents(bc));
                outdated |= (1 << 0);
            } else if (s instanceof BaseComponent bc) {
                result = result.append(convertBaseComponent(bc));
                outdated |= (1 << 1);
            } else if (s instanceof Component cmp) {
                result = result.append(cmp);
            } else {
                String stringObject = Objects.toString(s);
                if (stringObject.contains("§")) {
                    outdated |= (1 << 2);
                    result = result.append(LegacyComponentSerializer.legacySection().deserialize(stringObject));
                } else {
                    result = result.append(text(stringObject));
                }
            }
        }

        if (outdated > 0) {
            //CubesideUtils.getInstance().getLogger().log(Level.WARNING, "Outdatet call to sendMessage, flags: " + outdated + ".", new Throwable());
        }

        receiver.sendMessage(result);
    }

    @Deprecated
    public static Integer toRGB(ChatColor color) {
        String colorString = color.toString();
        if (colorString != null && colorString.length() > 2 && colorString.charAt(1) == 'x') {
            return Integer.parseInt(colorString.substring(2).replace(String.valueOf(ChatColor.COLOR_CHAR), ""), 16);
        }
        return CHATCOLOR_TO_RGB.get(color);
    }
}
