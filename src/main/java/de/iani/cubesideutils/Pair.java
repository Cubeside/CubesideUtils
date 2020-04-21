package de.iani.cubesideutils;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.ConfigurationSerialization;

public class Pair<T, S> implements ConfigurationSerializable {

    static {
        ConfigurationSerialization.registerClass(Pair.class);
    }

    public final T first;
    public final S second;

    public Pair(T first, S second) {
        this.first = first;
        this.second = second;
    }

    @SuppressWarnings("unchecked")
    public Pair(Map<String, Object> serialized) {
        this.first = (T) serialized.get("first");
        this.second = (S) serialized.get("second");
    }

    public Pair<T, S> setFirst(T first) {
        return new Pair<>(first, this.second);
    }

    public Pair<T, S> setSecond(S second) {
        return new Pair<>(this.first, second);
    }

    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> result = new LinkedHashMap<>(2);
        result.put("first", this.first);
        result.put("second", this.second);
        return result;
    }

    @Override
    public String toString() {
        return "(" + this.first + ";" + this.second + ")";
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(this.first) + 31 * Objects.hashCode(this.second);
    }

    @Override
    public boolean equals(Object other) {
        if (!(other instanceof Pair<?, ?>)) {
            return false;
        }
        Pair<?, ?> op = (Pair<?, ?>) other;
        return Objects.equals(this.first, op.first) && Objects.equals(this.second, op.second);
    }

}
