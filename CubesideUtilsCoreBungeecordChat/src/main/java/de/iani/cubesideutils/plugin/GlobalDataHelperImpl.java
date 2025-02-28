package de.iani.cubesideutils.plugin;

import java.io.DataOutputStream;
import java.io.IOException;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
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
        } else if (msg instanceof BaseComponent[]) {
            BaseComponent[] bc = (BaseComponent[]) msg;
            if (bc.length == 1) {
                sendMsgPart(msgout, bc[0]);
            } else {
                msgout.writeUTF(ComponentSerializer.toString(new TextComponent(bc)));
            }
        } else {
            super.sendMsgPart(msgout, msg);
        }
    }
}
