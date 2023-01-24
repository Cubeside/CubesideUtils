package de.iani.cubesideutils.nbt;

public class FloatTag implements BaseTag<FloatTag> {
    private float data;

    public FloatTag() {
    }

    public FloatTag(float data) {
        this.data = data;
    }

    public float getData() {
        return data;
    }

    public void setData(float data) {
        this.data = data;
    }

    @Override
    public TagType getType() {
        return TagType.FLOAT;
    }

    @Override
    public String toString() {
        return Float.toString(data);
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof FloatTag o && data == o.data;
    }

    @Override
    public FloatTag clone() {
        try {
            FloatTag clone = (FloatTag) super.clone();
            return clone;
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException("clone should be supported");
        }
    }
}
