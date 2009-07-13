package com.eatnumber1.util.concurrent.lock;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import org.jetbrains.annotations.NotNull;

/**
 * @author Russell Harmon
 */
public class SynchronizedReadWriteLockProvider extends SimpleReadWriteLockProvider {
    protected SynchronizedReadWriteLockProvider( @NotNull Lock lock ) {
        super(lock);
    }

    protected SynchronizedReadWriteLockProvider( @NotNull ReadWriteLock lock ) {
        super(lock);
    }

    protected SynchronizedReadWriteLockProvider() {
    }

    @SuppressWarnings({ "EqualsWhichDoesntCheckParameterClass" })
    @Override
    public final boolean equals( Object obj ) {
        Lock lock = getLock();
        lock.lock();
        try {
            return equalsInternal(obj);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public final int hashCode() {
        Lock lock = getLock();
        lock.lock();
        try {
            return hashCodeInternal();
        } finally {
            lock.unlock();
        }
    }

    protected boolean equalsInternal( Object obj ) {
        return super.equals(obj);
    }

    protected int hashCodeInternal() {
        return super.hashCode();
    }
}
