package de.iani.cubesideutils.nbt;

public class StringTag implements BaseTag<StringTag> {
    private String data = "";

    public StringTag() {
    }

    public StringTag(String data) {
        setData(data);
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        if (data == null) {
            throw new IllegalArgumentException("data");
        }
        this.data = data;
    }

    @Override
    public TagType getType() {
        return TagType.STRING;
    }

    @Override
    public String toString() {
        return data;
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof StringTag o && data == o.data;
    }

    @Override
    public StringTag clone() {
        try {
            StringTag clone = (StringTag) super.clone();
            return clone;
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException("clone should be supported");
        }
    }
}
