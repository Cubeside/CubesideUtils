package de.iani.cubesideutils;

import static net.kyori.adventure.text.Component.empty;
import static net.kyori.adventure.text.Component.space;

import de.iani.cubesideutils.ChatUtilAdventure.Sendable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.chat.ComponentSerializer;

public class ChatUtil {
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

    public static interface MessageReceiver extends ChatUtilAdventure.MessageReceiver {

        @Deprecated
        public default void sendMessage(BaseComponent... message) {
            sendMessage(convertBaseComponents(message));
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
    public static List<Sendable<MessageReceiver>> bcToSendableList(List<BaseComponent[]> messages) {
        List<Sendable<MessageReceiver>> result = new ArrayList<>(messages.size());
        for (BaseComponent[] msg : messages) {
            result.add(new BaseComponentMsg(msg));
        }
        return result;
    }

    @Deprecated
    public static <T extends MessageReceiver> void sendMessagesPaged(T recipient, List<? extends Sendable<? super T>> messages, int page, String name, String openPageCommandPrefix, String pluginPrefix) {
        sendMessagesPaged(recipient, messages, page, name, openPageCommandPrefix, pluginPrefix, ChatColor.GREEN, ChatColor.GOLD);
    }

    @Deprecated
    public static <T extends MessageReceiver> void sendMessagesPaged(T recipient, List<? extends Sendable<? super T>> messages, int page, String name, String openPageCommandPrefix, String pluginPrefix, ChatColor normalColor, ChatColor warningColor) {
        sendMessagesPaged(recipient, messages, page, new ComponentBuilder(name).create(), openPageCommandPrefix, pluginPrefix, normalColor, warningColor);
    }

    @Deprecated
    public static <T extends MessageReceiver> void sendMessagesPaged(T recipient, List<? extends Sendable<? super T>> messages, int page, BaseComponent[] name, String openPageCommandPrefix) {
        sendMessagesPaged(recipient, messages, page, name, openPageCommandPrefix, "");
    }

    @Deprecated
    public static <T extends MessageReceiver> void sendMessagesPaged(T recipient, List<? extends Sendable<? super T>> messages, int page, BaseComponent[] name, String openPageCommandPrefix, String pluginPrefix) {
        sendMessagesPaged(recipient, messages, page, name, openPageCommandPrefix, pluginPrefix, ChatColor.GREEN, ChatColor.GOLD);
    }

    @Deprecated
    public static <T extends MessageReceiver> void sendMessagesPaged(T recipient, List<? extends Sendable<? super T>> messages, int page, BaseComponent[] name, String openPageCommandPrefix, String pluginPrefix, ChatColor normalColor, ChatColor warningColor) {
        // CubesideUtils.getInstance().getLogger().log(Level.WARNING, "Outdatet call to sendMessagesPaged.", new Throwable());
        ChatUtilAdventure.sendMessagesPaged(recipient, messages, page, convertBaseComponents(name), openPageCommandPrefix, ChatUtilAdventure.convertLegacy(pluginPrefix), ChatUtilAdventure.convertStyle(normalColor.toString()), ChatUtilAdventure.convertStyle(warningColor.toString()));
    }

    @Deprecated
    public static void sendMessage(MessageReceiver receiver, String pluginPrefix, String colors, Object... messageParts) {
        // CubesideUtils.getInstance().getLogger().log(Level.WARNING, "Outdatet call to sendMessagesPaged.", new Throwable());
        sendMessage(receiver, ChatUtilAdventure.convertLegacy(pluginPrefix), ChatUtilAdventure.convertStyle(colors), messageParts);
    }

    public static void sendMessage(MessageReceiver receiver, Component pluginPrefix, Style style, Object... messageParts) {
        Component result = empty();
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
                }
                result = result.append(ComponentUtilAdventure.getLegacyComponentSerializer().deserialize(stringObject));
            }
        }

        if (outdated > 0) {
            // CubesideUtils.getInstance().getLogger().log(Level.WARNING, "Outdatet call to sendMessage, flags: " + outdated + ".", new Throwable());
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
