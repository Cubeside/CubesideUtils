package de.iani.cubesideutils.partialfunctions;

@FunctionalInterface
public interface PartialBiFunction<T, U, R, E extends Throwable> {

    public abstract R apply(T t, U u) throws E;

}
