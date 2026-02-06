package de.iani.cubesideutils.bukkit;

import de.cubeside.connection.GlobalPlayer;
import de.iani.cubesideutils.ChatUtil;
import de.iani.cubesideutils.ChatUtilAdventure;
import de.iani.cubesideutils.ChatUtilAdventure.Sendable;
import de.iani.cubesideutils.bukkit.plugin.CubesideUtilsBukkit;
import de.iani.cubesideutils.conditions.Condition;
import de.iani.cubesideutils.plugin.CubesideUtils;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ChatUtilBukkit extends ChatUtil {
    private ChatUtilBukkit() {
        super(); // guaranteed to fail
    }

    public static class CommandSenderWrapper implements MessageReceiver {
        private final CommandSender original;

        public CommandSenderWrapper(CommandSender original) {
            this.original = original;
        }

        @Override
        public void sendMessage(String message) {
            original.sendMessage(message);
        }

        @Deprecated
        @Override
        public void sendMessage(BaseComponent... message) {
            try {
                original.sendMessage(message);
            } catch (NoSuchMethodError e) {
                // spigot compatibility
                original.sendMessage(BaseComponent.toLegacyText(message));
            }
        }

        @Override
        public void sendMessage(Component message) {
            original.sendMessage(message);
        }
    }

    public static class GlobalPlayerWrapper implements MessageReceiver {
        private GlobalPlayer gPlayer;

        public GlobalPlayerWrapper(GlobalPlayer gPlayer) {
            this.gPlayer = Objects.requireNonNull(gPlayer);
        }

        public GlobalPlayerWrapper(UUID playerId) {
            this.gPlayer = CubesideUtils.getInstance().getGlobalDataHelper().getPlayer(playerId);
            if (this.gPlayer == null) {
                throw new IllegalArgumentException("player not found");
            }
        }

        @Deprecated
        @Override
        public void sendMessage(String message) {
            sendMessage(LegacyComponentSerializer.legacySection().deserialize(message));
        }

        @Override
        public void sendMessage(Component message) {
            CubesideUtilsBukkit.getInstance().getGlobalDataHelper().sendMessage(gPlayer, message);
        }

    }

    public static interface BukkitSendable extends Sendable<CommandSender> {

        public Sendable<ChatUtilAdventure.MessageReceiver> toGenericSendable();

    }

    public static class StringMsg implements BukkitSendable {

        public final String message;

        public StringMsg(String message) {
            this.message = message;
        }

        @Override
        public void send(CommandSender recipient) {
            new CommandSenderWrapper(recipient).sendMessage(message);
        }

        @Override
        public Sendable<ChatUtilAdventure.MessageReceiver> toGenericSendable() {
            return new ChatUtilAdventure.StringMsg(message);
        }
    }

    @Deprecated
    public static class ComponentMsg implements BukkitSendable {

        @Deprecated
        public final BaseComponent[] message;

        @Deprecated
        public ComponentMsg(BaseComponent[] message) {
            this.message = message;
        }

        @Deprecated
        public ComponentMsg(BaseComponent message) {
            this.message = new BaseComponent[] { message };
        }

        @Deprecated
        @Override
        public void send(CommandSender recipient) {
            new CommandSenderWrapper(recipient).sendMessage(message);
        }

        @Deprecated
        @Override
        public Sendable<ChatUtilAdventure.MessageReceiver> toGenericSendable() {
            return new ChatUtilAdventure.AdventureComponentMsg(ChatUtil.convertBaseComponents(message));
        }
    }

    public static class AdventureComponentMsg implements BukkitSendable {

        public final Component message;

        public AdventureComponentMsg(Component message) {
            this.message = message;
        }

        @Override
        public void send(CommandSender recipient) {
            recipient.sendMessage(message);
        }

        @Override
        public Sendable<ChatUtilAdventure.MessageReceiver> toGenericSendable() {
            return new ChatUtilAdventure.AdventureComponentMsg(message);
        }
    }

    public static List<BukkitSendable> stringToBukkitSendableList(List<String> messages) {
        List<BukkitSendable> result = new ArrayList<>(messages.size());
        for (String msg : messages) {
            result.add(new StringMsg(msg));
        }
        return result;
    }

    public static List<BukkitSendable> componentToBukkitSendableList(List<Component> messages) {
        List<BukkitSendable> result = new ArrayList<>(messages.size());
        for (Component msg : messages) {
            result.add(new AdventureComponentMsg(msg));
        }
        return result;
    }

    private static List<Sendable<ChatUtilAdventure.MessageReceiver>> convertSendableList(List<? extends BukkitSendable> messages) {
        return new AbstractList<>() {

            @Override
            public Sendable<ChatUtilAdventure.MessageReceiver> get(int index) {
                return messages.get(index).toGenericSendable();
            }

            @Override
            public int size() {
                return messages.size();
            }

        };
    }

    public static void sendMessagesPaged(CommandSender recipient, List<? extends BukkitSendable> messages, int page, String name, String openPageCommandPrefix) {
        sendMessagesPaged(recipient, messages, page, Component.text(name), openPageCommandPrefix);
    }

    public static void sendMessagesPaged(CommandSender recipient, List<? extends BukkitSendable> messages, int page, Component name, String openPageCommandPrefix) {
        sendMessagesPaged(recipient, messages, page, name, openPageCommandPrefix, null);
    }

    public static void sendMessagesPaged(CommandSender recipient, List<? extends BukkitSendable> messages, int page, Component name, String openPageCommandPrefix, Component pluginPrefix) {
        sendMessagesPaged(recipient, messages, page, name, openPageCommandPrefix, pluginPrefix, Style.style(NamedTextColor.GREEN), Style.style(NamedTextColor.GOLD));
    }

    public static void sendMessagesPaged(CommandSender recipient, List<? extends BukkitSendable> messages, int page, Component name, String openPageCommandPrefix, Component pluginPrefix, Style normalStyle, Style warningStyle) {
        ChatUtilAdventure.sendMessagesPaged(new CommandSenderWrapper(recipient), convertSendableList(messages), page, name, openPageCommandPrefix, pluginPrefix, normalStyle, warningStyle);
    }

    @Deprecated
    public static void sendMessagesPaged(CommandSender recipient, List<? extends BukkitSendable> messages, int page, String name, String openPageCommandPrefix, String pluginPrefix) {
        sendMessagesPaged(recipient, messages, page, name, openPageCommandPrefix, pluginPrefix, ChatColor.GREEN, ChatColor.GOLD);
    }

    @Deprecated
    public static void sendMessagesPaged(CommandSender recipient, List<? extends BukkitSendable> messages, int page, String name, String openPageCommandPrefix, String pluginPrefix, ChatColor normalColor, ChatColor warningColor) {
        ChatUtil.sendMessagesPaged(new CommandSenderWrapper(recipient), convertSendableList(messages), page, name, openPageCommandPrefix, pluginPrefix, normalColor, warningColor);
    }

    @Deprecated
    public static void sendMessagesPaged(CommandSender recipient, List<? extends BukkitSendable> messages, int page, BaseComponent name, String openPageCommandPrefix, String pluginPrefix, ChatColor normalColor, ChatColor warningColor) {
        ChatUtil.sendMessagesPaged(new CommandSenderWrapper(recipient), convertSendableList(messages), page, new BaseComponent[] { name }, openPageCommandPrefix, pluginPrefix, normalColor, warningColor);
    }

    public static void sendMessageToPlayers(Condition<? super Player> seeMsgCondition, String message) {
        sendMessageToPlayers(seeMsgCondition, new StringMsg(message));
    }

    @Deprecated
    public static void sendMessageToPlayers(Condition<? super Player> seeMsgCondition, BaseComponent... message) {
        sendMessageToPlayers(seeMsgCondition, new ComponentMsg(message));
    }

    public static void sendMessageToPlayers(Condition<? super Player> seeMsgCondition, Component message) {
        sendMessageToPlayers(seeMsgCondition, new AdventureComponentMsg(message));
    }

    public static void sendMessageToPlayers(Condition<? super Player> seeMsgCondition, BukkitSendable message) {
        message.send(Bukkit.getConsoleSender());
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (seeMsgCondition == null || seeMsgCondition.test(player)) {
                message.send(player);
            }
        }
    }

    public static void sendMessage(CommandSender receiver, Component pluginPrefix, Style style, Object... messageParts) {
        ChatUtil.sendMessage(new CommandSenderWrapper(receiver), pluginPrefix, style, messageParts);
    }

    public static void sendMessage(UUID playerId, Component pluginPrefix, Style style, Object... messageParts) {
        ChatUtil.sendMessage(new GlobalPlayerWrapper(playerId), pluginPrefix, style, messageParts);
    }

    @Deprecated
    public static void sendMessage(CommandSender receiver, String pluginPrefix, String colors, Object... messageParts) {
        ChatUtil.sendMessage(new CommandSenderWrapper(receiver), pluginPrefix, colors, messageParts);
    }

    @Deprecated
    public static void sendMessage(UUID playerId, String pluginPrefix, String colors, Object... messageParts) {
        ChatUtil.sendMessage(new GlobalPlayerWrapper(playerId), pluginPrefix, colors, messageParts);
    }

    @Deprecated
    public static Integer toRGB(org.bukkit.ChatColor color) {
        return ChatUtil.toRGB(color.asBungee());
    }
}
