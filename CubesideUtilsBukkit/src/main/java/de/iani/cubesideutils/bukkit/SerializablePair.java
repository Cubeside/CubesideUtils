package de.iani.cubesideutils.bukkit;

import de.iani.cubesideutils.Pair;
import java.util.LinkedHashMap;
import java.util.Map;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.ConfigurationSerialization;

public class SerializablePair<T, S> extends Pair<T, S> implements ConfigurationSerializable {

    static {
        ConfigurationSerialization.registerClass(SerializablePair.class);
    }

    public SerializablePair(T first, S second) {
        super(first, second);
    }

    @SuppressWarnings("unchecked")
    public SerializablePair(Map<String, Object> serialized) {
        this((T) serialized.get("first"), (S) serialized.get("second"));
    }

    @Override
    public SerializablePair<T, S> setFirst(T first) {
        return new SerializablePair<>(first, this.second);
    }

    @Override
    public SerializablePair<T, S> setSecond(S second) {
        return new SerializablePair<>(this.first, second);
    }

    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> result = new LinkedHashMap<>(2);
        result.put("first", this.first);
        result.put("second", this.second);
        return result;
    }

}