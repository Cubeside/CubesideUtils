package de.iani.cubesideutils.plugin;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.chat.ComponentSerializer;

public abstract class GlobalDataHelperImpl<T extends Enum<T>> extends GlobalDataHelperBaseImpl<T> {
    public GlobalDataHelperImpl(Class<T> messageTypeClass, String channel) {
        super(messageTypeClass, channel);
    }

    @Override
    protected void sendMsgPart(DataOutputStream msgout, Object msg) throws IOException {
        if (msg == null) {
            throw new NullPointerException();
        }

        if (msg instanceof BaseComponent component) {
            msgout.writeUTF(ComponentSerializer.toString(component));
        } else if (msg instanceof BaseComponent[] components) {
            msgout.writeUTF(ComponentSerializer.toString(components));
        } else {
            super.sendMsgPart(msgout, msg);
        }
    }

    protected BaseComponent[] readBaseComponent(DataInputStream msgin) throws IOException {
        return ComponentSerializer.parse(msgin.readUTF());
    }
}
