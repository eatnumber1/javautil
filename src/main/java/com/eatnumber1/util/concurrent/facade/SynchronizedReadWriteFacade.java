package com.eatnumber1.util.concurrent.facade;

import com.eatnumber1.util.concurrent.lock.SynchronizedReadWriteLockProvider;
import com.eatnumber1.util.facade.Facade;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import org.jetbrains.annotations.NotNull;

/**
 * @author Russell Harmon
 */
public class SynchronizedReadWriteFacade<T> extends SynchronizedReadWriteLockProvider implements Facade<T> {
    private T delegate;

    public SynchronizedReadWriteFacade() {
    }

    public SynchronizedReadWriteFacade( T delegate ) {
        this.delegate = delegate;
    }

    public SynchronizedReadWriteFacade( T delegate, @NotNull ReadWriteLock lock ) {
        super(lock);
        this.delegate = delegate;
    }

    public SynchronizedReadWriteFacade( T delegate, @NotNull Lock lock ) {
        super(lock);
        this.delegate = delegate;
    }

    @Override
    public String toString() {
        Lock readLock = getReadLock();
        readLock.lock();
        try {
            if( delegate == null ) {
                return super.toString();
            } else {
                return delegate.toString();
            }
        } finally {
            readLock.unlock();
        }
    }

    @Override
    protected boolean equalsInternal( Object o ) {
        if( this == o ) return true;
        if( o instanceof SynchronizedReadWriteFacade ) {
            SynchronizedReadWriteFacade that = (SynchronizedReadWriteFacade) o;
            Lock readLock = that.getReadLock();
            readLock.lock();
            try {
                return that.delegate == delegate || !( that.delegate == null || delegate == null ) && delegate.equals(that.delegate);
            } finally {
                readLock.unlock();
            }
        } else {
            return delegate == o || delegate != null && delegate.equals(o);
        }
    }

    @Override
    protected int hashCodeInternal() {
        if( delegate == null ) {
            return super.hashCodeInternal();
        } else {
            return delegate.hashCode();
        }
    }

    public void setDelegate( T delegate ) {
        this.delegate = delegate;
    }

    public T getDelegate() {
        return delegate;
    }
}
