package com.eatnumber1.util.concurrent.executors;

import com.eatnumber1.util.concurrent.ThreadAdapter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import org.jetbrains.annotations.NotNull;

/**
 * @author Russell Harmon
 * @since Jul 13, 2007
 */
public class ManagedExecutorService implements ExecutorService {
    protected class CountingThreadAdaptor<T> extends ThreadAdapter<T> {
        public CountingThreadAdaptor( @NotNull Runnable delegate ) {
            super(delegate);
        }

        public CountingThreadAdaptor( @NotNull Runnable delegate, @NotNull T result ) {
            super(delegate, result);
        }

        public CountingThreadAdaptor( @NotNull Callable<T> delegate ) {
            super(delegate);
        }

        @Override
        public T call() throws Exception {
            runningThreadsLock.lock();
            try {
                ManagedExecutorService.this.runningThreads++;
            } finally {
                runningThreadsLock.unlock();
            }
            try {
                return getDelegate().call();
            } finally {
                runningThreadsLock.lock();
                try {
                    ManagedExecutorService.this.runningThreads--;
                    if( canShutdown() ) getExecutorService(false).shutdown();
                } finally {
                    runningThreadsLock.unlock();
                }
            }
        }
    }

    @NotNull
    private ExecutorService executorService;

    @NotNull
    private ExecutorServiceFactory factory;

    protected int runningThreads = 0;

    @NotNull
    private Lock runningThreadsLock = new ReentrantLock(), executorServiceLock = new ReentrantLock();

    @NotNull
    protected ExecutorService getExecutorService( boolean initialize ) {
        executorServiceLock.lock();
        try {
            if( initialize && executorService.isShutdown() ) executorService = factory.getExecutorService();
            return executorService;
        } finally {
            executorServiceLock.unlock();
        }
    }

    public ManagedExecutorService( @NotNull ExecutorServiceFactory factory ) {
        this.factory = factory;
        executorService = factory.getExecutorService();
    }

    protected boolean canShutdown() {
        runningThreadsLock.lock();
        try {
            assert runningThreads >= 0;
            return runningThreads == 0;
        } finally {
            runningThreadsLock.unlock();
        }
    }

    @Override
    protected void finalize() throws Throwable {
        try {
            getExecutorService(false).shutdownNow();
        } finally {
            super.finalize();
        }
    }

    @Override
    public void shutdown() {
        getExecutorService(false).shutdown();
    }

    @Override
    public List<Runnable> shutdownNow() {
        return getExecutorService(false).shutdownNow();
    }

    @Override
    public boolean isShutdown() {
        return getExecutorService(false).isShutdown();
    }

    @Override
    public boolean isTerminated() {
        return getExecutorService(false).isTerminated();
    }

    @Override
    public boolean awaitTermination( long timeout, TimeUnit unit ) throws InterruptedException {
        return getExecutorService(false).awaitTermination(timeout, unit);
    }

    @Override
    public <T> Future<T> submit( Callable<T> task ) {
        return getExecutorService(true).submit((Callable<T>) new CountingThreadAdaptor<T>(task));
    }

    @Override
    public <T> Future<T> submit( Runnable task, T result ) {
        //noinspection unchecked
        return (Future<T>) getExecutorService(true).submit((Runnable) new CountingThreadAdaptor<T>(task, result));
    }

    @Override
    public Future<?> submit( Runnable task ) {
        return getExecutorService(true).submit((Runnable) new CountingThreadAdaptor<Object>(task));
    }

    @Override
    public <T> List<Future<T>> invokeAll( Collection<? extends Callable<T>> tasks ) throws InterruptedException {
        List<Callable<T>> taskList = new ArrayList<Callable<T>>(tasks.size());
        for( Callable<T> task : tasks ) {
            taskList.add(new CountingThreadAdaptor<T>(task));
        }
        return getExecutorService(true).invokeAll(taskList);
    }

    @Override
    public <T> List<Future<T>> invokeAll( Collection<? extends Callable<T>> tasks, long timeout, TimeUnit unit ) throws InterruptedException {
        List<Callable<T>> taskList = new ArrayList<Callable<T>>(tasks.size());
        for( Callable<T> task : tasks ) {
            taskList.add(new CountingThreadAdaptor<T>(task));
        }
        return getExecutorService(true).invokeAll(taskList, timeout, unit);
    }

    @Override
    public <T> T invokeAny( Collection<? extends Callable<T>> tasks ) throws InterruptedException, ExecutionException {
        List<Callable<T>> taskList = new ArrayList<Callable<T>>(tasks.size());
        for( Callable<T> task : tasks ) {
            taskList.add(new CountingThreadAdaptor<T>(task));
        }
        ExecutorService executorService = getExecutorService(true);
        try {
            return executorService.invokeAny(taskList);
        } finally {
            executorService.shutdown();
        }
    }

    @Override
    public <T> T invokeAny( Collection<? extends Callable<T>> tasks, long timeout, TimeUnit unit ) throws InterruptedException, ExecutionException, TimeoutException {
        getExecutorService(true);
        List<Callable<T>> taskList = new ArrayList<Callable<T>>(tasks.size());
        for( Callable<T> task : tasks ) {
            taskList.add(new CountingThreadAdaptor<T>(task));
        }
        ExecutorService executorService = getExecutorService(true);
        try {
            return executorService.invokeAny(taskList, timeout, unit);
        } finally {
            executorService.shutdown();
        }
    }

    @Override
    public void execute( Runnable command ) {
        getExecutorService(true).execute(command);
    }
}
