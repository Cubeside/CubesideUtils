package de.iani.cubesideutils.partialfunctions;

@FunctionalInterface
public interface PartialFunction<T, R, E extends Throwable> {

    public abstract R apply(T t) throws E;

}
