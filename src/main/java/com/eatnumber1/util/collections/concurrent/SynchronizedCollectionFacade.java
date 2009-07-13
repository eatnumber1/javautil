package com.eatnumber1.util.collections.concurrent;

import com.eatnumber1.util.collections.concurrent.iterators.SynchronizedIterator;
import com.eatnumber1.util.concurrent.facade.SynchronizedReadWriteFacade;
import java.util.Collection;
import java.util.Iterator;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import org.jetbrains.annotations.NotNull;

/**
 * @author Russell Harmon
 */
public class SynchronizedCollectionFacade<T, D extends Collection<T>> extends SynchronizedReadWriteFacade<D> implements SynchronizedCollection<T, D> {
    public SynchronizedCollectionFacade( D delegate, @NotNull Lock lock ) {
        super(delegate, lock);
    }

    public SynchronizedCollectionFacade( @NotNull D delegate, @NotNull ReadWriteLock lock ) {
        super(delegate, lock);
    }

    public SynchronizedCollectionFacade( @NotNull D delegate ) {
        super(delegate);
    }

    @Override
    public int size() {
        Lock readLock = readLock();
        readLock.lock();
        try {
            return getDelegate().size();
        } finally {
            readLock.unlock();
        }
    }

    @Override
    public boolean isEmpty() {
        Lock readLock = readLock();
        readLock.lock();
        try {
            return getDelegate().isEmpty();
        } finally {
            readLock.unlock();
        }
    }

    @Override
    public boolean contains( Object o ) {
        Lock readLock = readLock();
        readLock.lock();
        try {
            return getDelegate().contains(o);
        } finally {
            readLock.unlock();
        }
    }

    @Override
    public Iterator<T> iterator() {
        return new SynchronizedIterator<T, Iterator<T>>(getDelegate().iterator(), getReadWriteLock());
    }

    @Override
    public Object[] toArray() {
        Lock readLock = readLock();
        readLock.lock();
        try {
            return getDelegate().toArray();
        } finally {
            readLock.unlock();
        }
    }

    @Override
    public <T> T[] toArray( T[] a ) {
        Lock readLock = readLock();
        readLock.lock();
        try {
            //noinspection SuspiciousToArrayCall
            return getDelegate().toArray(a);
        } finally {
            readLock.unlock();
        }
    }

    @Override
    public boolean add( T t ) {
        Lock writeLock = writeLock();
        writeLock.lock();
        try {
            return getDelegate().add(t);
        } finally {
            writeLock.unlock();
        }
    }

    @Override
    public boolean remove( Object o ) {
        Lock writeLock = writeLock();
        writeLock.lock();
        try {
            return getDelegate().remove(o);
        } finally {
            writeLock.unlock();
        }
    }

    @Override
    public boolean containsAll( Collection<?> c ) {
        Lock readLock = readLock();
        readLock.lock();
        try {
            return getDelegate().containsAll(c);
        } finally {
            readLock.unlock();
        }
    }

    @Override
    public boolean addAll( Collection<? extends T> c ) {
        Lock writeLock = writeLock();
        writeLock.lock();
        try {
            return getDelegate().addAll(c);
        } finally {
            writeLock.unlock();
        }
    }

    @Override
    public boolean removeAll( Collection<?> c ) {
        Lock writeLock = writeLock();
        writeLock.lock();
        try {
            return getDelegate().removeAll(c);
        } finally {
            writeLock.unlock();
        }
    }

    @Override
    public boolean retainAll( Collection<?> c ) {
        Lock writeLock = writeLock();
        writeLock.lock();
        try {
            return getDelegate().retainAll(c);
        } finally {
            writeLock.unlock();
        }
    }

    @Override
    public void clear() {
        Lock writeLock = writeLock();
        writeLock.lock();
        try {
            getDelegate().clear();
        } finally {
            writeLock.unlock();
        }
    }
}
