package de.iani.cubesideutils.primitivewrapper;

public class VariableDouble extends Number implements Comparable<VariableDouble> {
    private static final long serialVersionUID = 3991014195988214331L;

    private double value;

    public VariableDouble() {
        this(0);
    }

    public VariableDouble(double value) {
        this.value = value;
    }

    public void set(double value) {
        this.value = value;
    }

    @Override
    public int intValue() {
        return (int) value;
    }

    @Override
    public long longValue() {
        return (long) value;
    }

    @Override
    public float floatValue() {
        return (float) value;
    }

    @Override
    public double doubleValue() {
        return value;
    }

    @Override
    public int hashCode() {
        return Double.hashCode(value);
    }

    @Override
    public boolean equals(Object other) {
        if (other instanceof VariableDouble) {
            return value == ((VariableDouble) other).value;
        }
        return false;
    }

    @Override
    public int compareTo(VariableDouble other) {
        return Double.compare(value, other.value);
    }

    @Override
    public String toString() {
        return Double.toString(value);
    }
}
