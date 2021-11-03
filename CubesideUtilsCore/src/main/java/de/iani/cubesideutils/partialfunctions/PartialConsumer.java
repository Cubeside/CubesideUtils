package de.iani.cubesideutils.partialfunctions;

@FunctionalInterface
public interface PartialConsumer<T, E extends Throwable> {

    public abstract void consume(T t) throws E;

}
