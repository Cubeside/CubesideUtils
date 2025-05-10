package de.iani.cubesideutils;

import static net.kyori.adventure.text.Component.empty;
import static net.kyori.adventure.text.Component.space;
import static net.kyori.adventure.text.Component.text;

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.IntSupplier;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.Style;

public abstract class ChatUtilAdventure {
    protected ChatUtilAdventure() {
        throw new UnsupportedOperationException("No instance for you, Sir!");
        // prevents instances
    }

    public static interface MessageReceiver {

        public void sendMessage(String message);

        public void sendMessage(Component message);
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
    public static Component convertLegacy(String s) {
        return ComponentUtil.getLegacyComponentSerializer().deserialize(s);
    }

    @Deprecated
    public static Style convertStyle(String colorString) {
        return colorString == null ? Style.empty() : convertLegacy(colorString).style();
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

    public static <T extends MessageReceiver> void sendMessagesPaged(T recipient, List<? extends Sendable<? super T>> messages, int page, String name, String openPageCommandPrefix) {
        sendMessagesPaged(recipient, messages, page, Component.text(name), openPageCommandPrefix);
    }

    public static <T extends MessageReceiver> void sendMessagesPaged(T recipient, List<? extends Sendable<? super T>> messages, int page, Component name, String openPageCommandPrefix) {
        sendMessagesPaged(recipient, messages, page, name, openPageCommandPrefix, null);
    }

    public static <T extends MessageReceiver> void sendMessagesPaged(T recipient, List<? extends Sendable<? super T>> messages, int page, Component name, String openPageCommandPrefix, Component pluginPrefix) {
        sendMessagesPaged(recipient, messages, page, name, openPageCommandPrefix, pluginPrefix, Style.style(NamedTextColor.GREEN), Style.style(NamedTextColor.GOLD));
    }

    public static <T extends MessageReceiver> void sendMessagesPaged(T recipient, List<? extends Sendable<? super T>> messages, int page, Component name, String openPageCommandPrefix, Component pluginPrefix, Style normalStyle, Style warningStyle) {
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

                Component result = empty().append(prefixComponent).append(prevComponent).append(text("   ")).append(nextComponent);
                recipient.sendMessage(result);
            }
        } catch (AbortPageSendException e) {
            return;
        }
    }

    public static void sendMessage(MessageReceiver receiver, Component pluginPrefix, Style style, Object... messageParts) {
        Component result = empty().style(style);
        if (pluginPrefix != null) {
            result = result.append(pluginPrefix).append(space());
        }

        if (style != null) {
            result = result.style(style);
        }

        for (Object s : messageParts) {
            if (s instanceof Component cmp) {
                result = result.append(cmp);
            } else {
                String stringObject = Objects.toString(s);
                result = result.append(ComponentUtil.getLegacyComponentSerializer().deserialize(stringObject));
            }
        }

        receiver.sendMessage(result);
    }
}
