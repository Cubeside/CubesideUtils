package de.iani.cubesideutils;

import java.util.ArrayList;
import java.util.List;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import org.bukkit.command.CommandSender;

public class ChatUtil {

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

    public static void sendMessagesPaged(CommandSender recipient, List<? extends Sendable> messages,
            int page, String name, String openPageCommandPrefix) {
        sendMessagesPaged(recipient, messages, page, name, openPageCommandPrefix, "");
    }

    public static void sendMessagesPaged(CommandSender recipient, List<? extends Sendable> messages,
            int page, String name, String openPageCommandPrefix, String pluginPrefix) {
        sendMessagesPaged(recipient, messages, page, name, openPageCommandPrefix, pluginPrefix, ChatColor.GREEN,
                ChatColor.GOLD);
    }

    public static void sendMessagesPaged(CommandSender recipient, List<? extends Sendable> messages,
            int page, String name, String openPageCommandPrefix, String pluginPrefix, ChatColor normalColor,
            ChatColor warningColor) {

        int numPages = (int) Math.ceil(messages.size() / (double) PAGE_LENGTH);
        if (page >= numPages) {
            sendMessage(recipient, pluginPrefix, warningColor.toString(), name, " hat keine Seite " + (page + 1));
            return;
        }

        if (numPages > 1) {
            sendMessage(recipient, pluginPrefix, normalColor.toString(), name, " (Seite " + (page + 1), "/" + numPages, "):");
        } else {
            sendMessage(recipient, pluginPrefix, normalColor.toString(), name, ":");
        }

        int index = page * PAGE_LENGTH;
        for (int i = 0; i < PAGE_LENGTH && index < messages.size();) {
            messages.get(index).send(recipient);

            i++;
            index++;
        }

        if (numPages > 1) {
            sendMessage(recipient, pluginPrefix, normalColor.toString(), "Seite x anzeigen: ", openPageCommandPrefix, " x");
            ComponentBuilder builder = new ComponentBuilder(pluginPrefix).append(" << vorherige");
            if (page > 0) {
                builder.color(ChatColor.BLUE);

                HoverEvent he = new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                        new ComponentBuilder("Seite " + page + " anzeigen").create());
                ClickEvent ce = new ClickEvent(ClickEvent.Action.RUN_COMMAND,
                        openPageCommandPrefix + " " + page);

                builder.event(he).event(ce);
            } else {
                builder.color(ChatColor.GRAY);

                HoverEvent he = new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                        new ComponentBuilder("Bereits auf Seite 1").create());

                builder.event(he);
            }

            builder.append("   ").reset().append("nächste >>");

            if (page + 1 < numPages) {
                builder.color(ChatColor.BLUE);

                HoverEvent he = new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                        new ComponentBuilder("Seite " + (page + 2) + " anzeigen").create());
                ClickEvent ce = new ClickEvent(ClickEvent.Action.RUN_COMMAND,
                        openPageCommandPrefix + " " + (page + 2));

                builder.event(he).event(ce);
            } else {
                builder.color(ChatColor.GRAY);

                HoverEvent he = new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                        new ComponentBuilder("Bereits auf Seite " + numPages).create());

                builder.event(he);
            }

            recipient.sendMessage(builder.create());
        }
    }

    public static void sendMessage(CommandSender sender, String pluginPrefix, String colors, String message,
            String... messageParts) {
        if (messageParts.length == 0) {
            sender.sendMessage(pluginPrefix + " " + colors + message);
        } else {
            StringBuilder builder = new StringBuilder(pluginPrefix).append(" ").append(colors).append(message);
            for (String s : messageParts) {
                builder.append(ChatColor.RESET).append(colors).append(s);
            }
            sender.sendMessage(builder.toString());
        }
    }

}
