package de.iani.cubesideutils.nbt;

public class LongTag implements BaseTag<LongTag> {
    private long data;

    public LongTag() {
    }

    public LongTag(long data) {
        this.data = data;
    }

    public long getData() {
        return data;
    }

    public void setData(long data) {
        this.data = data;
    }

    @Override
    public TagType getType() {
        return TagType.LONG;
    }

    @Override
    public String toString() {
        return Long.toString(data);
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof LongTag o && data == o.data;
    }

    @Override
    public LongTag clone() {
        try {
            LongTag clone = (LongTag) super.clone();
            return clone;
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException("clone should be supported");
        }
    }
}
