package de.iani.cubesideutils.bukkit;

import de.iani.cubesideutils.Triple;
import java.util.LinkedHashMap;
import java.util.Map;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.ConfigurationSerialization;

public class SerializableTriple<T, S, U> extends Triple<T, S, U> implements ConfigurationSerializable {

    static {
        ConfigurationSerialization.registerClass(SerializableTriple.class);
    }

    public SerializableTriple(T first, S second, U third) {
        super(first, second, third);
    }

    @SuppressWarnings("unchecked")
    public SerializableTriple(Map<String, Object> serialized) {
        this((T) serialized.get("first"), (S) serialized.get("second"), (U) serialized.get("third"));
    }

    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> result = new LinkedHashMap<>(2);
        result.put("first", this.first);
        result.put("second", this.second);
        result.put("third", this.third);
        return result;
    }

}
