package de.iani.cubesideutils.adventure.plugin;

import de.iani.cubesideutils.plugin.GlobalDataHelperBaseImpl;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.json.JSONComponentSerializer;

public abstract class GlobalDataHelperAdventureImpl<T extends Enum<T>> extends GlobalDataHelperBaseImpl<T> {
    public GlobalDataHelperAdventureImpl(Class<T> messageTypeClass, String channel) {
        super(messageTypeClass, channel);
    }

    @Override
    protected void sendMsgPart(DataOutputStream msgout, Object msg) throws IOException {
        if (msg instanceof Component component) {
            msgout.writeUTF(JSONComponentSerializer.json().serialize(component));
        } else {
            super.sendMsgPart(msgout, msg);
        }
    }

    protected Component readComponent(DataInputStream msgin) throws IOException {
        return JSONComponentSerializer.json().deserialize(msgin.readUTF());
    }
}
