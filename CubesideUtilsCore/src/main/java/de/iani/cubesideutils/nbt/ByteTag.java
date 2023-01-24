package de.iani.cubesideutils.nbt;

public class ByteTag implements BaseTag<ByteTag> {
    private byte data;

    public ByteTag() {
    }

    public ByteTag(byte data) {
        this.data = data;
    }

    public byte getData() {
        return data;
    }

    public void setData(byte data) {
        this.data = data;
    }

    @Override
    public TagType getType() {
        return TagType.BYTE;
    }

    @Override
    public String toString() {
        return Byte.toString(data);
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof ByteTag o && data == o.data;
    }

    @Override
    public ByteTag clone() {
        try {
            ByteTag clone = (ByteTag) super.clone();
            return clone;
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException("clone should be supported");
        }
    }
}
