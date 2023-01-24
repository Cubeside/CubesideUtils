package de.iani.cubesideutils.nbt;

public class ShortTag implements BaseTag<ShortTag> {
    private short data;

    public ShortTag() {
    }

    public ShortTag(short data) {
        this.data = data;
    }

    public short getData() {
        return data;
    }

    public void setData(short data) {
        this.data = data;
    }

    @Override
    public TagType getType() {
        return TagType.SHORT;
    }

    @Override
    public String toString() {
        return Short.toString(data);
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof ShortTag o && data == o.data;
    }

    @Override
    public ShortTag clone() {
        try {
            ShortTag clone = (ShortTag) super.clone();
            return clone;
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException("clone should be supported");
        }
    }
}
