package com.eatnumber1.util.concurrent.executors;

import com.eatnumber1.util.concurrent.ThreadAdapter;
import java.util.concurrent.Callable;
import java.util.concurrent.Delayed;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import org.jetbrains.annotations.NotNull;

/**
 * @author Russell Harmon
 * @since Jul 13, 2007
 */
public class ManagedScheduledExecutorService extends ManagedExecutorService implements ScheduledExecutorService {
    private int scheduledThreads = 0;

    @NotNull
    private Lock scheduledThreadsLock = new ReentrantLock();

    private class CountingScheduledFutureAdaptor<V> implements ScheduledFuture<V> {
        private ScheduledFuture<V> delegate;

        private CountingScheduledFutureAdaptor( ScheduledFuture<V> delegate ) {
            this.delegate = delegate;
            scheduledThreadsLock.lock();
            try {
                ManagedScheduledExecutorService.this.scheduledThreads++;
            } finally {
                scheduledThreadsLock.unlock();
            }
        }

        @Override
        public long getDelay( TimeUnit unit ) {
            return delegate.getDelay(unit);
        }

        @Override
        public int compareTo( Delayed o ) {
            return delegate.compareTo(o);
        }

        @Override
        public boolean cancel( boolean mayInterruptIfRunning ) {
            scheduledThreadsLock.lock();
            try {
                ManagedScheduledExecutorService.this.scheduledThreads--;
                if( canShutdown() )
                    getExecutorService(false).shutdown();
            } finally {
                scheduledThreadsLock.unlock();
            }
            return delegate.cancel(mayInterruptIfRunning);
        }

        @Override
        public boolean isCancelled() {
            return delegate.isCancelled();
        }

        @Override
        public boolean isDone() {
            return delegate.isDone();
        }

        @Override
        public V get() throws InterruptedException, ExecutionException {
            return delegate.get();
        }

        @Override
        public V get( long timeout, TimeUnit unit ) throws InterruptedException, ExecutionException, TimeoutException {
            return delegate.get(timeout, unit);
        }
    }

    protected class CountingScheduledThreadAdaptor<T> extends ThreadAdapter<T> {
        public CountingScheduledThreadAdaptor( @NotNull Runnable delegate ) {
            super(delegate);
        }

        public CountingScheduledThreadAdaptor( @NotNull Runnable delegate, @NotNull T result ) {
            super(delegate, result);
        }

        public CountingScheduledThreadAdaptor( @NotNull Callable<T> delegate ) {
            super(delegate);
        }

        @Override
        public T call() throws Exception {
            try {
                return getDelegate().call();
            } catch( Exception e ) {
                scheduledThreadsLock.lock();
                try {
                    ManagedScheduledExecutorService.this.scheduledThreads--;
                } finally {
                    scheduledThreadsLock.unlock();
                }
                throw e;
            } catch( Error e ) {
                scheduledThreadsLock.lock();
                try {
                    ManagedScheduledExecutorService.this.scheduledThreads--;
                } finally {
                    scheduledThreadsLock.unlock();
                }
                throw e;
            }
        }
    }

    public ManagedScheduledExecutorService( @NotNull ScheduledExecutorServiceFactory factory ) {
        super(factory);
    }

    @Override
    protected boolean canShutdown() {
        boolean result;
        scheduledThreadsLock.lock();
        try {
            assert scheduledThreads >= 0;
            result = scheduledThreads == 0;
        } finally {
            scheduledThreadsLock.unlock();
        }
        return super.canShutdown() && result;
    }

    @NotNull
    @Override
    protected ScheduledExecutorService getExecutorService( boolean initialize ) {
        return (ScheduledExecutorService) super.getExecutorService(initialize);
    }

    @Override
    public ScheduledFuture<?> schedule( Runnable command, long delay, TimeUnit unit ) {
        return getExecutorService(true).schedule((Runnable) new CountingScheduledThreadAdaptor<Object>(command), delay, unit);
    }

    @Override
    public <V> ScheduledFuture<V> schedule( Callable<V> callable, long delay, TimeUnit unit ) {
        return getExecutorService(true).schedule((Callable<V>) new CountingScheduledThreadAdaptor<V>(callable), delay, unit);
    }

    @Override
    public ScheduledFuture<?> scheduleAtFixedRate( Runnable command, long initialDelay, long period, TimeUnit unit ) {
        //noinspection unchecked
        return new CountingScheduledFutureAdaptor(getExecutorService(true).scheduleAtFixedRate(new CountingScheduledThreadAdaptor<Object>(command), initialDelay, period, unit));
    }

    @Override
    public ScheduledFuture<?> scheduleWithFixedDelay( Runnable command, long initialDelay, long delay, TimeUnit unit ) {
        //noinspection unchecked
        return new CountingScheduledFutureAdaptor(getExecutorService(true).scheduleWithFixedDelay(new CountingScheduledThreadAdaptor<Object>(command), initialDelay, delay, unit));
    }
}
