package de.iani.cubesideutils.nbt;

import java.util.Arrays;

public class LongArrayTag implements BaseTag<LongArrayTag> {
    private static final long[] EMPTY_ARRAY = new long[0];
    public long[] data = EMPTY_ARRAY;

    public LongArrayTag() {
    }

    public LongArrayTag(long[] data) {
        setData(data);
    }

    public long[] getData() {
        return data;
    }

    public void setData(long[] data) {
        if (data == null) {
            throw new IllegalArgumentException("data");
        }
        this.data = data;
    }

    @Override
    public TagType getType() {
        return TagType.LONG_ARRAY;
    }

    @Override
    public String toString() {
        return "[" + data.length + " longs]";
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof LongArrayTag other && Arrays.equals(data, other.data);
    }

    @Override
    public LongArrayTag clone() {
        try {
            LongArrayTag clone = (LongArrayTag) super.clone();
            clone.data = data.clone();
            return clone;
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException("clone should be supported");
        }
    }
}
