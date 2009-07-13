package com.eatnumber1.util.concurrent.lock;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import org.jetbrains.annotations.NotNull;

/**
 * @author Russell Harmon
 */
public class SimpleLockProvider implements LockProvider {
    @NotNull
    private Lock lock;

    public SimpleLockProvider( @NotNull Lock lock ) {
        this.lock = lock;
    }

    public SimpleLockProvider() {
        lock = new ReentrantLock();
    }

    @NotNull
    public Lock getLock() {
        return lock;
    }

    public void setLock( @NotNull Lock lock ) {
        Lock oldLock = this.lock;
        oldLock.lock();
        try {
            this.lock = lock;
        } finally {
            oldLock.unlock();
        }
    }

    @Override
    public boolean equals( Object o ) {
        if( this == o ) return true;
        if( !( o instanceof SimpleLockProvider ) ) return false;
        SimpleLockProvider that = (SimpleLockProvider) o;
        return lock.equals(that.lock);
    }

    @Override
    public int hashCode() {
        return lock.hashCode();
    }
}
