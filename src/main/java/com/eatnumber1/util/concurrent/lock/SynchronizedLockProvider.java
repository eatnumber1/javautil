package com.eatnumber1.util.concurrent.lock;

import java.util.concurrent.locks.Lock;
import org.jetbrains.annotations.NotNull;

/**
 * @author Russell Harmon
 */
public class SynchronizedLockProvider extends SimpleLockProvider {
    protected SynchronizedLockProvider( @NotNull Lock lock ) {
        super(lock);
    }

    protected SynchronizedLockProvider() {
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
