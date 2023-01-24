package de.iani.cubesideutils.nbt;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.ListIterator;
import java.util.function.UnaryOperator;

public class ListTag<T extends BaseTag<T>> extends ArrayList<T> implements BaseTag<ListTag<T>> {
    private static final long serialVersionUID = 2161704096949298689L;
    private TagType elementType = TagType.BYTE;

    public TagType getElementType() {
        return elementType;
    }

    public void setElementType(TagType elementType) {
        if (elementType == null) {
            throw new IllegalArgumentException("elementType");
        }
        if (!isEmpty() && elementType != this.elementType) {
            throw new IllegalStateException("list must be empty to change the element type");
        }
        this.elementType = elementType;
    }

    @Override
    public boolean add(T element) {
        if (isEmpty()) {
            elementType = element.getType();
        } else if (elementType != element.getType()) {
            throw new IllegalArgumentException("invalid type added. expected: " + elementType + " got: " + element.getType());
        }
        return super.add(element);
    }

    @Override
    public void add(int index, T element) {
        if (isEmpty()) {
            elementType = element.getType();
        } else if (elementType != element.getType()) {
            throw new IllegalArgumentException("invalid type added. expected: " + elementType + " got: " + element.getType());
        }
        super.add(index, element);
    }

    @Override
    public T set(int index, T element) {
        if (size() == 1) {
            elementType = element.getType();
        } else if (elementType != element.getType()) {
            throw new IllegalArgumentException("invalid type added. expected: " + elementType + " got: " + element.getType());
        }
        return super.set(index, element);
    }

    @Override
    public boolean addAll(Collection<? extends T> c) {
        if (!c.isEmpty()) {
            TagType requiredType = isEmpty() ? null : elementType;
            for (T element : c) {
                if (requiredType == null) {
                    requiredType = element.getType();
                } else if (requiredType != element.getType()) {
                    throw new IllegalArgumentException("invalid type added. expected: " + requiredType + " got: " + element.getType());
                }
            }
            elementType = requiredType;
        }
        return super.addAll(c);
    }

    @Override
    public boolean addAll(int index, Collection<? extends T> c) {
        if (!c.isEmpty()) {
            TagType requiredType = isEmpty() ? null : elementType;
            for (T element : c) {
                if (requiredType == null) {
                    requiredType = element.getType();
                } else if (requiredType != element.getType()) {
                    throw new IllegalArgumentException("invalid type added. expected: " + requiredType + " got: " + element.getType());
                }
            }
            elementType = requiredType;
        }
        return super.addAll(index, c);
    }

    @Override
    public void replaceAll(UnaryOperator<T> operator) {
        throw new UnsupportedOperationException("this implementations does not allow replaceAll");
    }

    @Override
    public List<T> subList(int fromIndex, int toIndex) {
        throw new UnsupportedOperationException("this implementations does not allow subList");
    }

    @Override
    public TagType getType() {
        return TagType.LIST;
    }

    @Override
    public String toString() {
        return size() + " entries of type " + elementType;
    }

    @Override
    public void print(String name, String prefix, PrintStream out) {
        BaseTag.super.print(name, prefix, out);

        out.println(prefix + "{");
        String orgPrefix = prefix;
        prefix += "   ";
        for (int i = 0; i < size(); i++) {
            get(i).print(null, prefix, out);
        }
        out.println(orgPrefix + "}");
    }

    @SuppressWarnings("unchecked")
    @Override
    public ListTag<T> clone() {
        ListTag<T> clone = (ListTag<T>) super.clone();
        ListIterator<T> it = clone.listIterator();
        while (it.hasNext()) {
            it.set(it.next().clone());
        }
        return clone;
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof ListTag && super.equals(obj);
    }
}
