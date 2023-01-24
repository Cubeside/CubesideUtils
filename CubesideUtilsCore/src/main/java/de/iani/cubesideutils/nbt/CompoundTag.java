package de.iani.cubesideutils.nbt;

import java.io.PrintStream;
import java.util.LinkedHashMap;
import java.util.Map;

public class CompoundTag extends LinkedHashMap<String, BaseTag<?>> implements BaseTag<CompoundTag> {
    private static final long serialVersionUID = -2661932464220266252L;

    @Override
    public TagType getType() {
        return TagType.COMPOUND;
    }

    public BaseTag<?> putByte(String name, byte value) {
        return put(name, new ByteTag(value));
    }

    public BaseTag<?> putShort(String name, short value) {
        return put(name, new ShortTag(value));
    }

    public BaseTag<?> putInt(String name, int value) {
        return put(name, new IntTag(value));
    }

    public BaseTag<?> putLong(String name, long value) {
        return put(name, new LongTag(value));
    }

    public BaseTag<?> putFloat(String name, float value) {
        return put(name, new FloatTag(value));
    }

    public BaseTag<?> putDouble(String name, double value) {
        return put(name, new DoubleTag(value));
    }

    public BaseTag<?> putString(String name, String value) {
        return put(name, new StringTag(value));
    }

    public BaseTag<?> putByteArray(String name, byte[] value) {
        return put(name, new ByteArrayTag(value));
    }

    public BaseTag<?> putIntArray(String name, int[] value) {
        return put(name, new IntArrayTag(value));
    }

    public BaseTag<?> putLongArray(String name, long[] value) {
        return put(name, new LongArrayTag(value));
    }

    public BaseTag<?> putCompound(String name, CompoundTag value) {
        return put(name, value);
    }

    public BaseTag<?> putBoolean(String string, boolean val) {
        return putByte(string, val ? (byte) 1 : 0);
    }

    public byte getByte(String name, byte defaultValue) {
        if (!containsKey(name)) {
            return defaultValue;
        }
        return ((ByteTag) get(name)).getData();
    }

    public short getShort(String name, short defaultValue) {
        if (!containsKey(name)) {
            return defaultValue;
        }
        return ((ShortTag) get(name)).getData();
    }

    public int getInt(String name, int defaultValue) {
        if (!containsKey(name)) {
            return defaultValue;
        }
        return ((IntTag) get(name)).getData();
    }

    public long getLong(String name, long defaultValue) {
        if (!containsKey(name)) {
            return defaultValue;
        }
        return ((LongTag) get(name)).getData();
    }

    public float getFloat(String name, float defaultValue) {
        if (!containsKey(name)) {
            return defaultValue;
        }
        return ((FloatTag) get(name)).getData();
    }

    public double getDouble(String name, double defaultValue) {
        if (!containsKey(name)) {
            return defaultValue;
        }
        return ((DoubleTag) get(name)).getData();
    }

    public String getString(String name, String defaultValue) {
        if (!containsKey(name)) {
            return defaultValue;
        }
        return ((StringTag) get(name)).getData();
    }

    public byte[] getByteArray(String name, byte[] defaultValue) {
        if (!containsKey(name)) {
            return defaultValue;
        }
        return ((ByteArrayTag) get(name)).getData();
    }

    public int[] getIntArray(String name, int[] defaultValue) {
        if (!containsKey(name)) {
            return defaultValue;
        }
        return ((IntArrayTag) get(name)).getData();
    }

    public long[] getLongArray(String name, long[] defaultValue) {
        if (!containsKey(name)) {
            return defaultValue;
        }
        return ((LongArrayTag) get(name)).getData();
    }

    public CompoundTag getCompound(String name, CompoundTag defaultValue) {
        if (!containsKey(name)) {
            return defaultValue;
        }
        return (CompoundTag) get(name);
    }

    @SuppressWarnings("unchecked")
    public ListTag<? extends BaseTag<?>> getList(String name, ListTag<? extends BaseTag<?>> defaultValue) {
        if (!containsKey(name)) {
            return defaultValue;
        }
        return (ListTag<? extends BaseTag<?>>) get(name);
    }

    public boolean getBoolean(String string, boolean defaultValue) {
        return getByte(string, defaultValue ? (byte) 1 : (byte) 0) != 0;
    }

    @Override
    public String toString() {
        return size() + " entries";
    }

    @Override
    public void print(String name, String prefix, PrintStream out) {
        BaseTag.super.print(name, prefix, out);
        out.println(prefix + "{");
        String orgPrefix = prefix;
        prefix += "   ";
        for (Map.Entry<String, BaseTag<?>> e : entrySet()) {
            e.getValue().print(e.getKey(), prefix, out);
        }
        out.println(orgPrefix + "}");
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof CompoundTag && super.equals(obj);
    }

    @Override
    public CompoundTag clone() {
        CompoundTag clone = new CompoundTag();
        for (Map.Entry<String, BaseTag<?>> e : entrySet()) {
            clone.put(e.getKey(), e.getValue().clone());
        }
        return clone;
    }
}
