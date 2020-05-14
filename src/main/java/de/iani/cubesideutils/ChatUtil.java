package de.iani.cubesideutils;

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.IntSupplier;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import org.bukkit.command.CommandSender;

public class ChatUtil {
    private ChatUtil() {
        throw new UnsupportedOperationException("No instance for you, Sir!");
        // prevents instances
    }

    public static final Map<org.bukkit.ChatColor, Integer> CHATCOLOR_TO_RGB;
    static {
        EnumMap<org.bukkit.ChatColor, Integer> chatcolorToColor = new EnumMap<>(org.bukkit.ChatColor.class);
        chatcolorToColor.put(org.bukkit.ChatColor.BLACK, 0x000000);
        chatcolorToColor.put(org.bukkit.ChatColor.DARK_BLUE, 0x0000AA);
        chatcolorToColor.put(org.bukkit.ChatColor.DARK_GREEN, 0x00AA00);
        chatcolorToColor.put(org.bukkit.ChatColor.DARK_AQUA, 0x00AAAA);
        chatcolorToColor.put(org.bukkit.ChatColor.DARK_RED, 0xAA0000);
        chatcolorToColor.put(org.bukkit.ChatColor.DARK_PURPLE, 0xAA00AA);
        chatcolorToColor.put(org.bukkit.ChatColor.GOLD, 0xFFAA00);
        chatcolorToColor.put(org.bukkit.ChatColor.GRAY, 0xAAAAAA);
        chatcolorToColor.put(org.bukkit.ChatColor.DARK_GRAY, 0x555555);
        chatcolorToColor.put(org.bukkit.ChatColor.BLUE, 0x5555FF);
        chatcolorToColor.put(org.bukkit.ChatColor.GREEN, 0x55FF55);
        chatcolorToColor.put(org.bukkit.ChatColor.AQUA, 0x55FFFF);
        chatcolorToColor.put(org.bukkit.ChatColor.RED, 0xFF5555);
        chatcolorToColor.put(org.bukkit.ChatColor.LIGHT_PURPLE, 0xFF55FF);
        chatcolorToColor.put(org.bukkit.ChatColor.YELLOW, 0xFFFF55);
        chatcolorToColor.put(org.bukkit.ChatColor.WHITE, 0xFFFFFF);

        CHATCOLOR_TO_RGB = Collections.unmodifiableMap(chatcolorToColor);
    }

    public static interface Sendable {

        public void send(CommandSender recipient);
    }

    public static class StringMsg implements Sendable {

        public final String msg;

        public StringMsg(String msg) {
            this.msg = msg;
        }

        @Override
        public void send(CommandSender recipient) {
            recipient.sendMessage(this.msg);
        }
    }

    public static class ComponentMsg implements Sendable {

        public final BaseComponent[] msg;

        public ComponentMsg(BaseComponent[] msg) {
            this.msg = msg;
        }

        @Override
        public void send(CommandSender recipient) {
            recipient.sendMessage(this.msg);
        }
    }

    public static class CachedSendableList<T extends Sendable> extends AbstractList<T> {

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

    public static List<Sendable> stringToSendableList(List<String> msges) {
        ArrayList<Sendable> result = new ArrayList<>(msges.size());
        for (String msg : msges) {
            result.add(new StringMsg(msg));
        }
        return result;
    }

    public static List<Sendable> bcToSendableList(List<BaseComponent[]> msges) {
        ArrayList<Sendable> result = new ArrayList<>(msges.size());
        for (BaseComponent[] msg : msges) {
            result.add(new ComponentMsg(msg));
        }
        return result;
    }

    public static final int PAGE_LENGTH = 10;

    public static void sendMessagesPaged(CommandSender recipient, List<? extends Sendable> messages, int page, String name, String openPageCommandPrefix) {
        sendMessagesPaged(recipient, messages, page, name, openPageCommandPrefix, "");
    }

    public static void sendMessagesPaged(CommandSender recipient, List<? extends Sendable> messages, int page, String name, String openPageCommandPrefix, String pluginPrefix) {
        sendMessagesPaged(recipient, messages, page, name, openPageCommandPrefix, pluginPrefix, ChatColor.GREEN, ChatColor.GOLD);
    }

    public static void sendMessagesPaged(CommandSender recipient, List<? extends Sendable> messages, int page, String name, String openPageCommandPrefix, String pluginPrefix, ChatColor normalColor, ChatColor warningColor) {
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

    public static void sendMessage(CommandSender sender, String pluginPrefix, String colors, Object message, Object... messageParts) {
        if (messageParts.length == 0) {
            sender.sendMessage(pluginPrefix + " " + (colors == null ? "" : colors) + message);
        } else {
            StringBuilder builder = new StringBuilder(pluginPrefix).append(" ").append(colors == null ? "" : colors).append(message);
            for (Object s : messageParts) {
                if (colors != null) {
                    builder.append(ChatColor.RESET).append(colors);
                }
                builder.append(Objects.toString(s));
            }
            sender.sendMessage(builder.toString());
        }
    }

    public static Integer toRGB(org.bukkit.ChatColor color) {
        return CHATCOLOR_TO_RGB.get(color);
    }
}
