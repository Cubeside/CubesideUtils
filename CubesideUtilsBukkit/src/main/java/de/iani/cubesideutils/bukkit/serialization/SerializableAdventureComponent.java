package de.iani.cubesideutils.bukkit.serialization;

import com.google.common.base.Preconditions;
import java.util.HashMap;
import java.util.Map;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentLike;
import net.kyori.adventure.text.serializer.json.JSONComponentSerializer;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.jetbrains.annotations.NotNull;

public class SerializableAdventureComponent implements ConfigurationSerializable, ComponentLike {

    static {
        ConfigurationSerialization.registerClass(SerializableAdventureComponent.class, "SerializableComponent");
    }

    public static SerializableAdventureComponent ofOrNull(Component component) {
        return component == null ? null : new SerializableAdventureComponent(component);
    }

    private final Component component;

    public SerializableAdventureComponent(Component component) {
        this.component = Preconditions.checkNotNull(component);
    }

    public SerializableAdventureComponent(Map<String, Object> serialized) {
        this.component = Preconditions.checkNotNull(JSONComponentSerializer.json().deserialize((String) serialized.get("data")));
    }

    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> result = new HashMap<>(1);
        result.put("data", JSONComponentSerializer.json().serialize(component));
        return result;
    }

    public Component getComponent() {
        return component;
    }

    @Override
    public @NotNull Component asComponent() {
        return getComponent();
    }
}
