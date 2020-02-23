package de.iani.cubesideutils.partialfunctions;

@FunctionalInterface
public interface PartialSupplier<T, E extends Throwable> {

    public abstract T get() throws E;

}
