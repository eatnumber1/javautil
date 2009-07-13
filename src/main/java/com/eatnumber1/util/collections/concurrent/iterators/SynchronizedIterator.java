package com.eatnumber1.util.collections.concurrent.iterators;

import com.eatnumber1.util.concurrent.facade.SynchronizedReadWriteFacade;
import java.util.Iterator;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import org.jetbrains.annotations.NotNull;

/**
 * @author Russell Harmon
 */
public class SynchronizedIterator<T, D extends Iterator<T>> extends SynchronizedReadWriteFacade<D> implements Iterator<T> {
    public SynchronizedIterator( @NotNull D delegate, @NotNull Lock lock ) {
        super(delegate, lock);
    }

    public SynchronizedIterator( @NotNull D delegate ) {
        super(delegate);
    }

    public SynchronizedIterator( @NotNull D delegate, @NotNull ReadWriteLock lock ) {
        super(delegate, lock);
    }

    public boolean hasNext() {
        Lock readLock = readLock();
        readLock.lock();
        try {
            return getDelegate().hasNext();
        } finally {
            readLock.unlock();
        }
    }

    public T next() {
        Lock readLock = readLock();
        readLock.lock();
        try {
            return getDelegate().next();
        } finally {
            readLock.unlock();
        }
    }

    public void remove() {
        Lock writeLock = writeLock();
        writeLock.lock();
        try {
            getDelegate().remove();
        } finally {
            writeLock.unlock();
        }
    }
}
