package com.eatnumber1.util.concurrent;

import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import org.jetbrains.annotations.NotNull;

/**
 * @author Russell Harmon
 */
public class ThreadAdapter<T> implements Callable<T>, Runnable {
    @NotNull
    private Callable<T> delegate;

    public ThreadAdapter( @NotNull Runnable delegate ) {
        //noinspection unchecked
        this.delegate = (Callable<T>) Executors.callable(delegate);
    }

    public ThreadAdapter( @NotNull Runnable delegate, @NotNull T result ) {
        this.delegate = Executors.callable(delegate, result);
    }

    public ThreadAdapter( @NotNull Callable<T> delegate ) {
        this.delegate = delegate;
    }

    @NotNull
    protected Callable<T> getDelegate() {
        return delegate;
    }

    public void run() {
        try {
            call();
        } catch( Exception e ) {
            throw new RuntimeException(e);
        }
    }

    public T call() throws Exception {
        return delegate.call();
    }
}