package com.eatnumber1.util.concurrent.lock;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import org.jetbrains.annotations.NotNull;

/**
 * @author Russell Harmon
 */
public class SimpleReadWriteLockProvider extends SimpleLockProvider implements ReadWriteLockProvider {
    @NotNull
    private ReadWriteLock lock;

    public SimpleReadWriteLockProvider( @NotNull final Lock lock ) {
        super(lock);
        this.lock = new ReadWriteLock() {
            @Override
            public Lock readLock() {
                return lock;
            }

            @Override
            public Lock writeLock() {
                return lock;
            }
        };
    }

    public SimpleReadWriteLockProvider( @NotNull ReadWriteLock lock ) {
        super(lock.writeLock());
        this.lock = lock;
    }

    public SimpleReadWriteLockProvider() {
        this(new ReentrantReadWriteLock());
    }

    public Lock readLock() {
        return getReadLock();
    }

    public Lock writeLock() {
        return getWriteLock();
    }

    @NotNull
    public ReadWriteLock getReadWriteLock() {
        return lock;
    }

    @NotNull
    public Lock getReadLock() {
        return lock.readLock();
    }

    @NotNull
    public Lock getWriteLock() {
        return lock.writeLock();
    }

    public void setReadWriteLock( @NotNull ReadWriteLock lock ) {
        Lock writeLock = this.lock.writeLock();
        writeLock.lock();
        try {
            this.lock = lock;
            super.setLock(lock.writeLock());
        } finally {
            writeLock.unlock();
        }
    }

    @Override
    public void setLock( @NotNull final Lock lock ) {
        setReadWriteLock(new ReadWriteLock() {
            public Lock readLock() {
                return lock;
            }

            public Lock writeLock() {
                return lock;
            }
        });
    }

    @Override
    public boolean equals( Object o ) {
        if( this == o ) return true;
        if( !( o instanceof SimpleReadWriteLockProvider ) ) return false;
        if( !super.equals(o) ) return false;
        SimpleReadWriteLockProvider that = (SimpleReadWriteLockProvider) o;
        return lock.equals(that.lock);
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + lock.hashCode();
        return result;
    }
}
