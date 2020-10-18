package de.iani.cubesideutils.bukkit;

import com.google.common.base.Preconditions;
import java.util.HashMap;
import java.util.Map;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.chat.ComponentSerializer;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.ConfigurationSerialization;

public class SerializableComponent implements ConfigurationSerializable {

    static {
        ConfigurationSerialization.registerClass(SerializableComponent.class, "SerializableComponent");
    }

    private final BaseComponent component;

    public SerializableComponent(BaseComponent component) {
        this.component = Preconditions.checkNotNull(component);
    }

    public SerializableComponent(Map<String, Object> serialized) {
        component = Preconditions.checkNotNull(ComponentSerializer.parse((String) serialized.get("data"))[0]);
    }

    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> result = new HashMap<>(1);
        result.put("data", ComponentSerializer.toString(component));
        return result;
    }

    public BaseComponent getComponent() {
        return component;
    }
}
