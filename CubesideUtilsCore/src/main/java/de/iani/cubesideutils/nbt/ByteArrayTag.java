package de.iani.cubesideutils.nbt;

import java.util.Arrays;

public final class ByteArrayTag implements BaseTag<ByteArrayTag> {
    public static final byte[] EMPTY_ARRAY = new byte[0];
    private byte[] data = EMPTY_ARRAY;

    public ByteArrayTag() {
    }

    public ByteArrayTag(byte[] data) {
        setData(data);
    }

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        if (data == null) {
            throw new IllegalArgumentException("data");
        }
        this.data = data;
    }

    @Override
    public TagType getType() {
        return TagType.BYTE_ARRAY;
    }

    @Override
    public String toString() {
        return "[" + data.length + " bytes]";
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof ByteArrayTag other && Arrays.equals(data, other.data);
    }

    @Override
    public ByteArrayTag clone() {
        try {
            ByteArrayTag clone = (ByteArrayTag) super.clone();
            clone.data = data.clone();
            return clone;
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException("clone should be supported");
        }
    }
}
