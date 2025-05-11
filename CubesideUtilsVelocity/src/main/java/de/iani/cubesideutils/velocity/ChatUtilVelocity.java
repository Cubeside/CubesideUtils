package de.iani.cubesideutils.velocity;

import com.velocitypowered.api.command.CommandSource;
import de.iani.cubesideutils.ChatUtilAdventure;
import de.iani.cubesideutils.ChatUtilAdventure.MessageReceiver;
import de.iani.cubesideutils.ChatUtilAdventure.Sendable;
import de.iani.cubesideutils.ComponentUtil;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.List;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.Style;

public class ChatUtilVelocity {

    private ChatUtilVelocity() {
        throw new UnsupportedOperationException("No instance for you, Sir!");
        // prevents instances
    }

    public static class CommandSourceWrapper implements MessageReceiver {

        private final CommandSource original;

        public CommandSourceWrapper(CommandSource original) {
            this.original = original;
        }

        @Override
        public void sendMessage(String message) {
            this.original.sendMessage(ComponentUtil.getLegacyComponentSerializer().deserialize(message));
        }

        @Override
        public void sendMessage(Component message) {
            sendMessage(message);
        }

    }

    public static interface VelocitySendable extends Sendable<CommandSource> {

        public Sendable<ChatUtilAdventure.MessageReceiver> toGenericSendable();

    }

    public static class StringMsg implements VelocitySendable {

        public final String message;

        public StringMsg(String message) {
            this.message = message;
        }

        @Override
        public void send(CommandSource recipient) {
            recipient.sendMessage(ComponentUtil.getLegacyComponentSerializer().deserialize(this.message));
        }

        @Override
        public Sendable<ChatUtilAdventure.MessageReceiver> toGenericSendable() {
            return new ChatUtilAdventure.StringMsg(this.message);
        }
    }

    public static class AdventureComponentMsg implements VelocitySendable {

        public final Component message;

        public AdventureComponentMsg(Component message) {
            this.message = message;
        }

        @Override
        public void send(CommandSource recipient) {
            recipient.sendMessage(this.message);
        }

        @Override
        public Sendable<ChatUtilAdventure.MessageReceiver> toGenericSendable() {
            return new ChatUtilAdventure.AdventureComponentMsg(this.message);
        }
    }

    public static List<Sendable<CommandSource>> stringToSendableList(List<String> messages) {
        List<Sendable<CommandSource>> result = new ArrayList<>(messages.size());
        for (String msg : messages) {
            result.add(new StringMsg(msg));
        }
        return result;
    }

    public static List<Sendable<CommandSource>> bcToSendableList(List<Component> messages) {
        List<Sendable<CommandSource>> result = new ArrayList<>(messages.size());
        for (Component msg : messages) {
            result.add(new AdventureComponentMsg(msg));
        }
        return result;
    }

    private static List<Sendable<ChatUtilAdventure.MessageReceiver>> convertSendableList(
            List<? extends VelocitySendable> messages) {
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

    public static void sendMessagesPaged(CommandSource recipient, List<? extends VelocitySendable> messages, int page,
            Component name, String openPageCommandPrefix) {
        sendMessagesPaged(recipient, messages, page, name, openPageCommandPrefix, Component.empty());
    }

    public static void sendMessagesPaged(CommandSource recipient, List<? extends VelocitySendable> messages, int page,
            Component name, String openPageCommandPrefix, Component pluginPrefix) {
        sendMessagesPaged(recipient, messages, page, name, openPageCommandPrefix, pluginPrefix,
                Style.style(NamedTextColor.GREEN), Style.style(NamedTextColor.GOLD));
    }

    public static void sendMessagesPaged(CommandSource recipient, List<? extends VelocitySendable> messages, int page,
            Component name, String openPageCommandPrefix, Component pluginPrefix, Style normalStyle,
            Style warningStyle) {
        ChatUtilAdventure.sendMessagesPaged(new CommandSourceWrapper(recipient), convertSendableList(messages), page,
                name, openPageCommandPrefix, pluginPrefix, normalStyle, warningStyle);
    }

}
