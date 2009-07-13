package com.eatnumber1.util.collections.concurrent.iterators;

import java.util.ListIterator;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import org.jetbrains.annotations.NotNull;

/**
 * @author Russell Harmon
 */
public class SynchronizedListIterator<T, D extends ListIterator<T>> extends SynchronizedIterator<T, D> implements ListIterator<T> {
    public SynchronizedListIterator( @NotNull D delegate, @NotNull Lock lock ) {
        super(delegate, lock);
    }

    public SynchronizedListIterator( @NotNull D delegate ) {
        super(delegate);
    }

    public SynchronizedListIterator( @NotNull D delegate, @NotNull ReadWriteLock lock ) {
        super(delegate, lock);
    }

    public boolean hasPrevious() {
        Lock readLock = readLock();
        readLock.lock();
        try {
            return getDelegate().hasPrevious();
        } finally {
            readLock.unlock();
        }
    }

    public T previous() {
        Lock readLock = readLock();
        readLock.lock();
        try {
            return getDelegate().previous();
        } finally {
            readLock.unlock();
        }
    }

    public int nextIndex() {
        Lock readLock = readLock();
        readLock.lock();
        try {
            return getDelegate().nextIndex();
        } finally {
            readLock.unlock();
        }
    }

    public int previousIndex() {
        Lock readLock = readLock();
        readLock.lock();
        try {
            return getDelegate().previousIndex();
        } finally {
            readLock.unlock();
        }
    }

    public void set( T o ) {
        Lock writeLock = writeLock();
        writeLock.lock();
        try {
            getDelegate().set(o);
        } finally {
            writeLock.unlock();
        }
    }

    public void add( T o ) {
        Lock writeLock = writeLock();
        writeLock.lock();
        try {
            getDelegate().add(o);
        } finally {
            writeLock.unlock();
        }
    }
}
