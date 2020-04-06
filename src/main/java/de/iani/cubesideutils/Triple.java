package de.iani.cubesideutils;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.ConfigurationSerialization;

public class Triple<T, S, U> implements ConfigurationSerializable {

    static {
        ConfigurationSerialization.registerClass(Triple.class);
    }

    public final T first;
    public final S second;
    public final U third;

    public Triple(T first, S second, U third) {
        this.first = first;
        this.second = second;
        this.third = third;
    }

    @SuppressWarnings("unchecked")
    public Triple(Map<String, Object> serialized) {
        this.first = (T) serialized.get("first");
        this.second = (S) serialized.get("second");
        this.third = (U) serialized.get("third");
    }

    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> result = new LinkedHashMap<>(2);
        result.put("first", this.first);
        result.put("second", this.second);
        result.put("third", this.third);
        return result;
    }

    @Override
    public String toString() {
        return "(" + this.first + ";" + this.second + ";" + this.third + ")";
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.first, this.second, this.third);
    }

    @Override
    public boolean equals(Object other) {
        if (!(other instanceof Triple<?, ?, ?>)) {
            return false;
        }
        Triple<?, ?, ?> op = (Triple<?, ?, ?>) other;
        return Objects.equals(this.first, op.first) && Objects.equals(this.second, op.second) && Objects.equals(this.third, op.third);
    }

}
