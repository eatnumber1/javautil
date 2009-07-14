package com.eatnumber1.util.observer;

import com.eatnumber1.util.collections.concurrent.SynchronizedCollection;
import com.eatnumber1.util.compat.Override;
import com.eatnumber1.util.concurrent.executors.ExecutorServiceFactory;
import com.eatnumber1.util.concurrent.executors.ManagedExecutorService;
import java.util.Collection;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.Lock;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author Russell Harmon
 * @since Jul 13, 2007
 */
public class ThreadedObservableNotificationProvider<T> implements ObservableNotificationProvider<T> {
    @NotNull
    private ExecutorService threadPool;

    public ThreadedObservableNotificationProvider( @NotNull ExecutorService threadPool ) {
        this.threadPool = threadPool;
    }

    public ThreadedObservableNotificationProvider() {
        threadPool = new ManagedExecutorService(new ExecutorServiceFactory() {
            @NotNull
            @Override
            public ExecutorService getExecutorService() {
                return Executors.newCachedThreadPool();
            }
        });
    }

    @Override
    public void notifyObservers( @NotNull final Observable<T> observable, @NotNull SynchronizedCollection<Observer<T>, ? extends Collection<Observer<T>>> observers, @Nullable final T arg ) {
        Lock readLock = observers.readLock();
        readLock.lock();
        try {
            for( final Observer<T> observer : observers ) {
                threadPool.submit(new Runnable() {
                    @Override
                    public void run() {
                        observer.update(observable, arg);
                    }
                });
            }
        } finally {
            readLock.unlock();
        }
    }
}
