package de.iani.cubesideutils.nbt;

import java.util.Arrays;

public class IntArrayTag implements BaseTag<IntArrayTag> {
    public static final int[] EMPTY_ARRAY = new int[0];
    private int[] data = EMPTY_ARRAY;

    public IntArrayTag() {
    }

    public IntArrayTag(int[] data) {
        setData(data);
    }

    public int[] getData() {
        return data;
    }

    public void setData(int[] data) {
        if (data == null) {
            throw new IllegalArgumentException("data");
        }
        this.data = data;
    }

    @Override
    public TagType getType() {
        return TagType.INT_ARRAY;
    }

    @Override
    public String toString() {
        return "[" + data.length + " ints]";
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof IntArrayTag other && Arrays.equals(data, other.data);
    }

    @Override
    public IntArrayTag clone() {
        try {
            IntArrayTag clone = (IntArrayTag) super.clone();
            clone.data = data.clone();
            return clone;
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException("clone should be supported");
        }
    }
}
