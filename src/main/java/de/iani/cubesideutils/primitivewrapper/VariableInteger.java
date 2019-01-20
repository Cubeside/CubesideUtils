package de.iani.cubesideutils.primitivewrapper;

public class VariableInteger extends Number implements Comparable<VariableInteger> {
    private static final long serialVersionUID = -2311252991544761338L;

    private int value;

    public VariableInteger() {
        this(0);
    }

    public VariableInteger(int value) {
        this.value = value;
    }

    public void set(int value) {
        this.value = value;
    }

    @Override
    public int intValue() {
        return value;
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
        return value;
    }

    @Override
    public boolean equals(Object other) {
        if (other instanceof VariableInteger) {
            return value == ((VariableInteger) other).value;
        }
        return false;
    }

    @Override
    public int compareTo(VariableInteger other) {
        return Integer.compare(value, other.value);
    }

    @Override
    public String toString() {
        return Integer.toString(value);
    }
}
