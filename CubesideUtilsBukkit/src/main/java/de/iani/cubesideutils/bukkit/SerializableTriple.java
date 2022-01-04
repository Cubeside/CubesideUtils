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

    public SerializableTriple(Triple<T, S, U> copyOf) {
        this(copyOf.first, copyOf.second, copyOf.third);
    }

    public <X> SerializableTriple<X, S, U> setFirst(X first) {
        return new SerializableTriple<>(first, this.second, this.third);
    }

    public <X> SerializableTriple<T, X, U> setSecond(X second) {
        return new SerializableTriple<>(first, second, this.third);
    }

    public <X> SerializableTriple<T, S, X> setThird(X third) {
        return new SerializableTriple<>(first, this.second, third);
    }

    @SuppressWarnings("unchecked")
    public SerializableTriple(Map<String, Object> serialized) {
        this((T) serialized.get("first"), (S) serialized.get("second"), (U) serialized.get("third"));
    }

    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> result = new LinkedHashMap<>(3);
        result.put("first", this.first);
        result.put("second", this.second);
        result.put("third", this.third);
        return result;
    }

}
