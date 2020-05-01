package de.iani.cubesideutils;

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.IntSupplier;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;

public abstract class ChatUtil {
    protected ChatUtil() {
        throw new UnsupportedOperationException("No instance for you, Sir!");
        // prevents instances
    }

    public static interface MessageReceiver {

        public void sendMessage(String message);

        public void sendMessage(BaseComponent[] message);
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

        public ComponentMsg(BaseComponent[] message) {
            this.message = message;
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
            int transformedIndex = index - cacheStartIndex;
            if (cache == null || transformedIndex < 0 || transformedIndex >= cacheSize) {
                cache = listFiller.apply(index, cacheSize);
                cacheStartIndex = index;
                transformedIndex = 0;
            }
            return cache.get(transformedIndex);
        }

        @Override
        public int size() {
            return sizeGetter.getAsInt();
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
        if (page < 0) {
            sendMessage(recipient, pluginPrefix, warningColor.toString(), "Bitte gib die Seitenzahl als positive ganze Zahl an.");
            return;
        }

        int listSize = messages.size();
        int numPages = (int) Math.ceil(listSize / (double) PAGE_LENGTH);
        if (page >= numPages && page > 0) {
            sendMessage(recipient, pluginPrefix, warningColor.toString(), name, " hat keine Seite ", (page + 1));
            return;
        }

        if (!openPageCommandPrefix.startsWith("/")) {
            openPageCommandPrefix = "/" + openPageCommandPrefix;
        }

        if (numPages > 1) {
            sendMessage(recipient, pluginPrefix, normalColor.toString(), name, " (Seite ", (page + 1), "/", numPages, "):");
        } else {
            sendMessage(recipient, pluginPrefix, normalColor.toString(), name, ":");
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

                HoverEvent he = new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("Seite " + page + " anzeigen").create());
                ClickEvent ce = new ClickEvent(ClickEvent.Action.RUN_COMMAND, openPageCommandPrefix + " " + page);

                builder.event(he).event(ce);
            } else {
                builder.color(ChatColor.GRAY);

                HoverEvent he = new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("Bereits auf Seite 1").create());

                builder.event(he);
            }

            builder.append("   ").reset().append("nächste >>");

            if (page + 1 < numPages) {
                builder.color(ChatColor.BLUE);

                HoverEvent he = new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("Seite " + (page + 2) + " anzeigen").create());
                ClickEvent ce = new ClickEvent(ClickEvent.Action.RUN_COMMAND, openPageCommandPrefix + " " + (page + 2));

                builder.event(he).event(ce);
            } else {
                builder.color(ChatColor.GRAY);

                HoverEvent he = new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("Bereits auf Seite " + numPages).create());

                builder.event(he);
            }

            recipient.sendMessage(builder.create());
        }
    }

    public static void sendMessage(MessageReceiver receiver, String pluginPrefix, String colors, Object message, Object... messageParts) {
        if (messageParts.length == 0) {
            receiver.sendMessage(pluginPrefix + " " + (colors == null ? "" : colors) + message);
        } else {
            StringBuilder builder = new StringBuilder(pluginPrefix).append(" ").append(colors == null ? "" : colors).append(message);
            for (Object s : messageParts) {
                if (colors != null) {
                    builder.append(ChatColor.RESET).append(colors);
                }
                builder.append(Objects.toString(s));
            }
            receiver.sendMessage(builder.toString());
        }
    }

}
