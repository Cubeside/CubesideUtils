package de.iani.cubesideutils.velocity;

public class ChatUtilVelocity {
    /*private ChatUtilsVelocity() {
        throw new UnsupportedOperationException("No instance for you, Sir!");
        // prevents instances
    }

    public static class CommandSenderWrapper implements MessageReceiver {
        private final CommandSource original;

        public CommandSenderWrapper(CommandSource original) {
            this.original = original;
        }

        @Override
        @SuppressWarnings("deprecation")
        public void sendMessage(String message) {
            original.sendMessage(message);
        }

        @Override
        public void sendMessage(BaseComponent... message) {
            original.sendMessage(message);
        }
    }

    public static interface BungeeSendable extends Sendable<CommandSource> {

        public Sendable<MessageReceiver> toGenericSendable();

    }

    public static class StringMsg implements BungeeSendable {

        public final String message;

        public StringMsg(String message) {
            this.message = message;
        }

        @Override
        @SuppressWarnings("deprecation")
        public void send(CommandSource recipient) {
            recipient.sendMessage(this.message);
        }

        @Override
        public Sendable<MessageReceiver> toGenericSendable() {
            return new ChatUtil.StringMsg(message);
        }
    }

    public static class ComponentMsg implements BungeeSendable {

        public final BaseComponent[] message;

        public ComponentMsg(BaseComponent... message) {
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

    public static List<Sendable<CommandSource>> stringToSendableList(List<String> messages) {
        List<Sendable<CommandSource>> result = new ArrayList<>(messages.size());
        for (String msg : messages) {
            result.add(new StringMsg(msg));
        }
        return result;
    }

    public static List<Sendable<CommandSource>> bcToSendableList(List<BaseComponent[]> messages) {
        List<Sendable<CommandSource>> result = new ArrayList<>(messages.size());
        for (BaseComponent[] msg : messages) {
            result.add(new ComponentMsg(msg));
        }
        return result;
    }

    private static List<Sendable<MessageReceiver>> convertSendableList(List<? extends BungeeSendable> messages) {
        return new AbstractList<>() {

            @Override
            public Sendable<MessageReceiver> get(int index) {
                return messages.get(index).toGenericSendable();
            }

            @Override
            public int size() {
                return messages.size();
            }

        };
    }

    protected static void sendMessagesPaged(CommandSource recipient, List<? extends BungeeSendable> messages, int page, String name, String openPageCommandPrefix) {
        sendMessagesPaged(recipient, messages, page, name, openPageCommandPrefix, "");
    }

    protected static void sendMessagesPaged(CommandSource recipient, List<? extends BungeeSendable> messages, int page, String name, String openPageCommandPrefix, String pluginPrefix) {
        sendMessagesPaged(recipient, messages, page, name, openPageCommandPrefix, pluginPrefix, ChatColor.GREEN, ChatColor.GOLD);
    }

    protected static void sendMessagesPaged(CommandSource recipient, List<? extends BungeeSendable> messages, int page, String name, String openPageCommandPrefix, String pluginPrefix, ChatColor normalColor, ChatColor warningColor) {
        ChatUtil.sendMessagesPaged(new CommandSenderWrapper(recipient), convertSendableList(messages), page, name, openPageCommandPrefix, pluginPrefix, normalColor, warningColor);
    }*/

}
