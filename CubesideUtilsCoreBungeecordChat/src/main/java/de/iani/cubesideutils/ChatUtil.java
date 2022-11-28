package de.iani.cubesideutils;

import de.iani.cubesideutils.plugin.CubesideUtils;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.IntSupplier;
import java.util.logging.Level;
import java.util.stream.Stream;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Text;

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

        public void sendMessage(BaseComponent... message);
    }

    public static interface Sendable<T> {

        public void send(T recipient);
    }

    public static class StringMsg implements Sendable<MessageReceiver> {

        public final String message;

        public StringMsg(String message) {
            this.message = message;
        }

        @Override
        public void send(MessageReceiver recipient) {
            recipient.sendMessage(this.message);
        }
    }

    public static class ComponentMsg implements Sendable<MessageReceiver> {

        public final BaseComponent[] message;

        public ComponentMsg(BaseComponent... message) {
            this.message = message;
        }

        public ComponentMsg(BaseComponent message) {
            this.message = new BaseComponent[] { message };
        }

        @Override
        public void send(MessageReceiver recipient) {
            recipient.sendMessage(this.message);
        }
    }

    public static List<Sendable<MessageReceiver>> stringToSendableList(List<String> messages) {
        List<Sendable<MessageReceiver>> result = new ArrayList<>(messages.size());
        for (String msg : messages) {
            result.add(new StringMsg(msg));
        }
        return result;
    }

    public static List<Sendable<MessageReceiver>> bcToSendableList(List<BaseComponent[]> messages) {
        List<Sendable<MessageReceiver>> result = new ArrayList<>(messages.size());
        for (BaseComponent[] msg : messages) {
            result.add(new ComponentMsg(msg));
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
        sendMessagesPaged(recipient, messages, page, name, openPageCommandPrefix, "");
    }

    public static <T extends MessageReceiver> void sendMessagesPaged(T recipient, List<? extends Sendable<T>> messages, int page, String name, String openPageCommandPrefix, String pluginPrefix) {
        sendMessagesPaged(recipient, messages, page, name, openPageCommandPrefix, pluginPrefix, ChatColor.GREEN, ChatColor.GOLD);
    }

    public static <T extends MessageReceiver> void sendMessagesPaged(T recipient, List<? extends Sendable<T>> messages, int page, String name, String openPageCommandPrefix, String pluginPrefix, ChatColor normalColor, ChatColor warningColor) {
        sendMessagesPaged(recipient, messages, page, new ComponentBuilder(name).create(), openPageCommandPrefix, pluginPrefix, normalColor, warningColor);
    }

    public static <T extends MessageReceiver> void sendMessagesPaged(T recipient, List<? extends Sendable<T>> messages, int page, BaseComponent[] name, String openPageCommandPrefix) {
        sendMessagesPaged(recipient, messages, page, name, openPageCommandPrefix, "");
    }

    public static <T extends MessageReceiver> void sendMessagesPaged(T recipient, List<? extends Sendable<T>> messages, int page, BaseComponent[] name, String openPageCommandPrefix, String pluginPrefix) {
        sendMessagesPaged(recipient, messages, page, name, openPageCommandPrefix, pluginPrefix, ChatColor.GREEN, ChatColor.GOLD);
    }

    public static <T extends MessageReceiver> void sendMessagesPaged(T recipient, List<? extends Sendable<T>> messages, int page, BaseComponent[] name, String openPageCommandPrefix, String pluginPrefix, ChatColor normalColor, ChatColor warningColor) {
        try {
            if (page < 0) {
                sendMessage(recipient, pluginPrefix, warningColor.toString(), "Bitte gib die Seitenzahl als positive ganze Zahl an.");
                return;
            }

            TextComponent prefixComponent = new TextComponent(pluginPrefix.isEmpty() ? "" : (pluginPrefix + " "));
            TextComponent nameComponent = new TextComponent(name);

            int listSize = messages.size();
            int numPages = (int) Math.ceil(listSize / (double) PAGE_LENGTH);
            if (page >= numPages && page > 0) {
                ComponentBuilder builder = new ComponentBuilder(pluginPrefix);
                if (!pluginPrefix.isEmpty()) {
                    builder.append(" ");
                }
                builder.append(nameComponent).color(warningColor).append(" hat keine Seite ").append(String.valueOf(page + 1));
                recipient.sendMessage(builder.create());
                return;
            }

            if (!openPageCommandPrefix.startsWith("/")) {
                openPageCommandPrefix = "/" + openPageCommandPrefix;
            }

            if (numPages > 1) {
                ComponentBuilder builder = new ComponentBuilder(prefixComponent);
                builder.append(nameComponent).color(normalColor).append(" (Seite ").append(String.valueOf(page + 1)).append("/").append(String.valueOf(numPages)).append("):");
                recipient.sendMessage(builder.create());
            } else {
                ComponentBuilder builder = new ComponentBuilder(prefixComponent);
                builder.append(nameComponent).color(normalColor).append(":");
                recipient.sendMessage(builder.create());
            }

            if (listSize == 0) {
                recipient.sendMessage(ChatColor.GRAY + " -- keine --");
            }

            int index = page * PAGE_LENGTH;
            for (int i = 0; i < PAGE_LENGTH && index < listSize;) {
                messages.get(index).send(recipient);

                i++;
                index++;
            }

            if (numPages > 1) {
                sendMessage(recipient, pluginPrefix, normalColor.toString(), "Seite x anzeigen: ", openPageCommandPrefix, " x");
                ComponentBuilder builder = new ComponentBuilder(pluginPrefix).append(" << vorherige");
                if (page > 0) {
                    builder.color(ChatColor.BLUE);

                    HoverEvent he = new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(new ComponentBuilder("Seite " + page + " anzeigen").create()));
                    ClickEvent ce = new ClickEvent(ClickEvent.Action.RUN_COMMAND, openPageCommandPrefix + " " + page);

                    builder.event(he).event(ce);
                } else {
                    builder.color(ChatColor.GRAY);

                    HoverEvent he = new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(new ComponentBuilder("Bereits auf Seite 1").create()));

                    builder.event(he);
                }

                builder.append("   ").reset().append("nächste >>");

                if (page + 1 < numPages) {
                    builder.color(ChatColor.BLUE);

                    HoverEvent he = new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(new ComponentBuilder("Seite " + (page + 2) + " anzeigen").create()));
                    ClickEvent ce = new ClickEvent(ClickEvent.Action.RUN_COMMAND, openPageCommandPrefix + " " + (page + 2));

                    builder.event(he).event(ce);
                } else {
                    builder.color(ChatColor.GRAY);

                    HoverEvent he = new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(new ComponentBuilder("Bereits auf Seite " + numPages).create()));

                    builder.event(he);
                }

                recipient.sendMessage(builder.create());
            }
        } catch (AbortPageSendException e) {
            return;
        }
    }

    public static void sendMessage(MessageReceiver receiver, String pluginPrefix, String colors, Object... messageParts) {
        TextComponent builder = new TextComponent(pluginPrefix);
        if (!pluginPrefix.isEmpty()) {
            builder.addExtra(" ");
        }

        if (colors != null && !colors.isEmpty()) {
            BaseComponent[] colorComponents = TextComponent.fromLegacyText(colors);
            if (colorComponents.length == 1) {
                builder.copyFormatting(colorComponents[0]);
            } else {
                CubesideUtils.getInstance().getLogger().log(Level.WARNING, "Outdatet call to sendMessage (colors-String).", new Throwable());
            }
        }

        if (Arrays.stream(messageParts).anyMatch(o -> o != null && o.getClass().isArray())) {
            CubesideUtils.getInstance().getLogger().log(Level.WARNING, "Outdatet call to sendMessage.", new Throwable());
            messageParts = Arrays.stream(messageParts).flatMap(o -> (o != null && o.getClass().isArray()) ? arrayStream(o) : Stream.of(o)).toArray();
        }

        for (Object s : messageParts) {
            if (s instanceof BaseComponent[] bc) {
                builder.addExtra(new TextComponent(bc));
            } else if (s instanceof BaseComponent bc) {
                builder.addExtra(bc);
            } else {
                builder.addExtra(Objects.toString(s));
            }
        }
        receiver.sendMessage(builder);
    }

    @SuppressWarnings("unchecked")
    private static <T> Stream<T> arrayStream(Object array) {
        return Arrays.stream((T[]) array);
    }

    public static Integer toRGB(ChatColor color) {
        String colorString = color.toString();
        if (colorString != null && colorString.length() > 2 && colorString.charAt(1) == 'x') {
            return Integer.parseInt(colorString.substring(2).replace(String.valueOf(ChatColor.COLOR_CHAR), ""), 16);
        }
        return CHATCOLOR_TO_RGB.get(color);
    }
}
