package de.iani.cubesideutils.concurrent;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.AbstractExecutorService;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.TimeUnit;

public final class SimpleSingleThreadExecutor extends AbstractExecutorService {

    private final ArrayDeque<Runnable> queue = new ArrayDeque<>();
    private final Thread worker;

    // All three only accessed in synchronized(queue)
    private boolean shutdown = false;
    private boolean stopNow = false;
    private boolean terminated = false;

    public SimpleSingleThreadExecutor() {
        this(false);
    }

    public SimpleSingleThreadExecutor(boolean daemon) {
        this(daemon, "SingleThreadExecutor");
    }

    public SimpleSingleThreadExecutor(String threadName) {
        this(false, threadName);
    }

    public SimpleSingleThreadExecutor(boolean daemon, String threadName) {
        worker = threadName == null ? new Thread(this::runWorker) : new Thread(this::runWorker, threadName);
        if (daemon) {
            worker.setDaemon(true);
        }
        worker.start();
    }

    @Override
    public void execute(Runnable task) {
        if (task == null) {
            throw new NullPointerException("task");
        }

        synchronized (queue) {
            if (shutdown) {
                throw new RejectedExecutionException("Executor is shut down");
            }

            queue.addLast(task);
            queue.notifyAll();
        }
    }

    private void runWorker() {
        try {
            while (true) {
                Runnable task;

                synchronized (queue) {
                    while (queue.isEmpty() && !shutdown) {
                        try {
                            queue.wait();
                        } catch (InterruptedException e) {
                            if (stopNow) {
                                return;
                            }
                        }
                    }

                    if (stopNow || (shutdown && queue.isEmpty())) {
                        return;
                    }

                    task = queue.removeFirst();
                }

                // run tasks outside of the synchronisation
                try {
                    task.run();
                } catch (Throwable e) {
                    e.printStackTrace();
                }
            }
        } finally {
            synchronized (queue) {
                terminated = true;
                queue.notifyAll();
            }
        }
    }

    @Override
    public void shutdown() {
        synchronized (queue) {
            shutdown = true;
            queue.notifyAll();
        }
    }

    @Override
    public List<Runnable> shutdownNow() {
        List<Runnable> remaining;

        synchronized (queue) {
            shutdown = true;
            stopNow = true;

            remaining = new ArrayList<>(queue);
            queue.clear();

            queue.notifyAll();
        }

        worker.interrupt();
        return remaining;
    }

    @Override
    public boolean isShutdown() {
        synchronized (queue) {
            return shutdown;
        }
    }

    @Override
    public boolean isTerminated() {
        synchronized (queue) {
            return terminated;
        }
    }

    @Override
    public boolean awaitTermination(long timeout, TimeUnit unit)
            throws InterruptedException {

        long remaining = unit.toNanos(timeout);

        synchronized (queue) {
            while (!terminated) {
                if (remaining <= 0) {
                    return false;
                }

                long start = System.nanoTime();
                TimeUnit.NANOSECONDS.timedWait(queue, remaining);
                remaining -= System.nanoTime() - start;
            }

            return true;
        }
    }
}