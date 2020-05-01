package de.iani.cubesideutils.bukkit;

import de.iani.cubesideutils.ChatUtil;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
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

        @Override
        public void sendMessage(BaseComponent[] message) {
            original.sendMessage(message);
        }
    }

    public static interface BukkitSendable extends Sendable<CommandSender> {

        public Sendable<MessageReceiver> toGenericSendable();

    }

    public static class StringMsg implements BukkitSendable {

        public final String message;

        public StringMsg(String message) {
            this.message = message;
        }

        @Override
        public void send(CommandSender recipient) {
            recipient.sendMessage(this.message);
        }

        @Override
        public Sendable<MessageReceiver> toGenericSendable() {
            return new ChatUtil.StringMsg(message);
        }
    }

    public static class ComponentMsg implements BukkitSendable {

        public final BaseComponent[] message;

        public ComponentMsg(BaseComponent[] message) {
            this.message = message;
        }

        @Override
        public void send(CommandSender recipient) {
            recipient.sendMessage(this.message);
        }

        @Override
        public Sendable<MessageReceiver> toGenericSendable() {
            return new ChatUtil.ComponentMsg(message);
        }
    }

    public static List<BukkitSendable> stringToBukkitSendableList(List<String> messages) {
        List<BukkitSendable> result = new ArrayList<>(messages.size());
        for (String msg : messages) {
            result.add(new StringMsg(msg));
        }
        return result;
    }

    public static List<BukkitSendable> bcToBukkitSendableList(List<BaseComponent[]> messages) {
        List<BukkitSendable> result = new ArrayList<>(messages.size());
        for (BaseComponent[] msg : messages) {
            result.add(new ComponentMsg(msg));
        }
        return result;
    }

    private static List<Sendable<MessageReceiver>> convertSendableList(List<? extends BukkitSendable> messages) {
        return messages.stream().map(BukkitSendable::toGenericSendable).collect(Collectors.toCollection(ArrayList::new));
    }

    public static void sendMessagesPaged(CommandSender recipient, List<? extends BukkitSendable> messages, int page, String name, String openPageCommandPrefix) {
        sendMessagesPaged(recipient, messages, page, name, openPageCommandPrefix, "");
    }

    public static void sendMessagesPaged(CommandSender recipient, List<? extends BukkitSendable> messages, int page, String name, String openPageCommandPrefix, String pluginPrefix) {
        sendMessagesPaged(recipient, messages, page, name, openPageCommandPrefix, pluginPrefix, ChatColor.GREEN, ChatColor.GOLD);
    }

    public static void sendMessagesPaged(CommandSender recipient, List<? extends BukkitSendable> messages, int page, String name, String openPageCommandPrefix, String pluginPrefix, ChatColor normalColor, ChatColor warningColor) {
        ChatUtil.sendMessagesPaged((MessageReceiver) new CommandSenderWrapper(recipient), (List<? extends Sendable<MessageReceiver>>) convertSendableList(messages), page, name, openPageCommandPrefix, pluginPrefix, normalColor, warningColor);
    }

    public static void sendMessageToPlayers(String seeMsgPermission, String message) {
        if (seeMsgPermission.isEmpty() || Bukkit.getConsoleSender().hasPermission(seeMsgPermission)) {
            Bukkit.getConsoleSender().sendMessage(message);
        }
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (seeMsgPermission.isEmpty() || player.hasPermission(seeMsgPermission)) {
                player.sendMessage(message);
            }
        }
    }

}
