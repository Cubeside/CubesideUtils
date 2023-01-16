package de.iani.cubesideutils.partialfunctions;

public interface PartialRunnable<E extends Throwable> {

    public abstract void run() throws E;

}
