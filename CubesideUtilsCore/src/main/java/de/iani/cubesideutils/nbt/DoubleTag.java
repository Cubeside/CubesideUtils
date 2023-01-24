package de.iani.cubesideutils.nbt;

public class DoubleTag implements BaseTag<DoubleTag> {
    private double data;

    public DoubleTag() {
    }

    public DoubleTag(double data) {
        this.data = data;
    }

    public double getData() {
        return data;
    }

    public void setData(double data) {
        this.data = data;
    }

    @Override
    public TagType getType() {
        return TagType.DOUBLE;
    }

    @Override
    public String toString() {
        return Double.toString(data);
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof DoubleTag o && data == o.data;
    }

    @Override
    public DoubleTag clone() {
        try {
            DoubleTag clone = (DoubleTag) super.clone();
            return clone;
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException("clone should be supported");
        }
    }
}
