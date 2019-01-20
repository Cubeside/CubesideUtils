package de.iani.cubesideutils.primitivewrapper;

public class VariableLong extends Number implements Comparable<VariableLong> {
    private static final long serialVersionUID = -8867666357501031831L;

    private long value;

    public VariableLong() {
        this(0);
    }

    public VariableLong(long value) {
        this.value = value;
    }

    public void set(long value) {
        this.value = value;
    }

    @Override
    public int intValue() {
        return (int) value;
    }

    @Override
    public long longValue() {
        return value;
    }

    @Override
    public float floatValue() {
        return value;
    }

    @Override
    public double doubleValue() {
        return value;
    }

    @Override
    public int hashCode() {
        return Long.hashCode(value);
    }

    @Override
    public boolean equals(Object other) {
        if (other instanceof VariableLong) {
            return value == ((VariableLong) other).value;
        }
        return false;
    }

    @Override
    public int compareTo(VariableLong other) {
        return Long.compare(value, other.value);
    }

    @Override
    public String toString() {
        return Long.toString(value);
    }
}
