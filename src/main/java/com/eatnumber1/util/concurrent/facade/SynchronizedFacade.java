package com.eatnumber1.util.concurrent.facade;

import com.eatnumber1.util.concurrent.lock.SimpleLockProvider;
import com.eatnumber1.util.facade.Facade;
import java.util.concurrent.locks.Lock;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author Russell Harmon
 */
public class SynchronizedFacade<T> extends SimpleLockProvider implements Facade<T> {
    private T delegate;

    public SynchronizedFacade() {
    }

    public SynchronizedFacade( T delegate ) {
        this.delegate = delegate;
    }

    public SynchronizedFacade( T delegate, @NotNull Lock lock ) {
        super(lock);
        this.delegate = delegate;
    }

    @Override
    public String toString() {
        Lock lock = getLock();
        lock.lock();
        try {
            if( delegate == null ) {
                return super.toString();
            } else {
                return delegate.toString();
            }
        } finally {
            lock.unlock();
        }
    }

    @Override
    public boolean equals( @Nullable Object o ) {
        if( this == o ) return true;
        if( o instanceof SynchronizedFacade ) {
            SynchronizedFacade that = (SynchronizedFacade) o;
            Lock lock = getLock(), thatLock = that.getLock();
            lock.lock();
            try {
                thatLock.lock();
                try {
                    return that.delegate == delegate || !( that.delegate == null || delegate == null ) && delegate.equals(that.delegate);
                } finally {
                    thatLock.unlock();
                }
            } finally {
                lock.unlock();
            }
        } else {
            Lock readLock = getLock();
            readLock.lock();
            try {
                return delegate == o || delegate != null && delegate.equals(o);
            } finally {
                readLock.unlock();
            }
        }
    }

    @Override
    public int hashCode() {
        Lock lock = getLock();
        try {
            if( delegate == null ) {
                return super.hashCode();
            } else {
                return delegate.hashCode();
            }
        } finally {
            lock.unlock();
        }
    }

    public void setDelegate( T delegate ) {
        this.delegate = delegate;
    }

    public T getDelegate() {
        return delegate;
    }
}
