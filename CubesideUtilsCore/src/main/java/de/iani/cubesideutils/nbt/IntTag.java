package de.iani.cubesideutils.nbt;

public class IntTag implements BaseTag<IntTag> {
    private int data;

    public IntTag() {
    }

    public IntTag(int data) {
        this.data = data;
    }

    public int getData() {
        return data;
    }

    public void setData(int data) {
        this.data = data;
    }

    @Override
    public TagType getType() {
        return TagType.INT;
    }

    @Override
    public String toString() {
        return Integer.toString(data);
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof IntTag o && data == o.data;
    }

    @Override
    public IntTag clone() {
        try {
            IntTag clone = (IntTag) super.clone();
            return clone;
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException("clone should be supported");
        }
    }
}
