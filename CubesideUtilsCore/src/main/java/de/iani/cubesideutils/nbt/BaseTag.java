package de.iani.cubesideutils.nbt;

import java.io.PrintStream;

public interface BaseTag<T extends BaseTag<T>> extends Cloneable {
    public abstract TagType getType();

    default public void print(PrintStream out) {
        print(null, "", out);
    }

    default public void print(String name, String prefix, PrintStream out) {
        out.print(prefix);
        if (name != null) {
            out.print(name);
            out.print(" ");
        }
        out.print("");
        out.print(getType());
        out.print(": ");
        out.println(toString());
    }

    public T clone();
}
