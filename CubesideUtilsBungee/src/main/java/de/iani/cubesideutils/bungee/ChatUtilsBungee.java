package de.iani.cubesideutils.bungee;

import de.iani.cubesideutils.ChatUtil;
import de.iani.cubesideutils.ChatUtil.MessageReceiver;
import de.iani.cubesideutils.ChatUtil.Sendable;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.BaseComponent;

public class ChatUtilsBungee {
    private ChatUtilsBungee() {
        throw new UnsupportedOperationException("No instance for you, Sir!");
        // prevents instances
    }

    public static class CommandSenderWrapper implements MessageReceiver {
        private final CommandSender original;

        public CommandSenderWrapper(CommandSender original) {
            this.original = original;
        }

        @Override
        @SuppressWarnings("deprecation")
        public void sendMessage(String message) {
            original.sendMessage(message);
        }

        @Override
        public void sendMessage(BaseComponent[] message) {
            original.sendMessage(message);
        }
    }

    public static interface BungeeSendable extends Sendable<CommandSender> {

        public Sendable<MessageReceiver> toGenericSendable();

    }

    public static class StringMsg implements BungeeSendable {

        public final String message;

        public StringMsg(String message) {
            this.message = message;
        }

        @Override
        @SuppressWarnings("deprecation")
        public void send(CommandSender recipient) {
            recipient.sendMessage(this.message);
        }

        @Override
        public Sendable<MessageReceiver> toGenericSendable() {
            return new ChatUtil.StringMsg(message);
        }
    }

    public static class ComponentMsg implements BungeeSendable {

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

    public static List<Sendable<CommandSender>> stringToSendableList(List<String> messages) {
        List<Sendable<CommandSender>> result = new ArrayList<>(messages.size());
        for (String msg : messages) {
            result.add(new StringMsg(msg));
        }
        return result;
    }

    public static List<Sendable<CommandSender>> bcToSendableList(List<BaseComponent[]> messages) {
        List<Sendable<CommandSender>> result = new ArrayList<>(messages.size());
        for (BaseComponent[] msg : messages) {
            result.add(new ComponentMsg(msg));
        }
        return result;
    }

    private static List<Sendable<MessageReceiver>> convertSendableList(List<? extends BungeeSendable> messages) {
        return messages.stream().map(BungeeSendable::toGenericSendable).collect(Collectors.toCollection(ArrayList::new));
    }

    protected static void sendMessagesPaged(CommandSender recipient, List<? extends BungeeSendable> messages, int page, String name, String openPageCommandPrefix) {
        sendMessagesPaged(recipient, messages, page, name, openPageCommandPrefix, "");
    }

    protected static void sendMessagesPaged(CommandSender recipient, List<? extends BungeeSendable> messages, int page, String name, String openPageCommandPrefix, String pluginPrefix) {
        sendMessagesPaged(recipient, messages, page, name, openPageCommandPrefix, pluginPrefix, ChatColor.GREEN, ChatColor.GOLD);
    }

    protected static void sendMessagesPaged(CommandSender recipient, List<? extends BungeeSendable> messages, int page, String name, String openPageCommandPrefix, String pluginPrefix, ChatColor normalColor, ChatColor warningColor) {
        ChatUtil.sendMessagesPaged(new CommandSenderWrapper(recipient), convertSendableList(messages), page, name, openPageCommandPrefix, pluginPrefix, normalColor, warningColor);
    }

    public static void sendMessageToPlayersAllServers(String seeMsgPermission, String message) {
        // UtilsPlugin.getInstance().sendMessageToPlayersAllServers(seeMsgPermission, message); // TODO
    }

}
