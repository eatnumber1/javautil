package com.eatnumber1.util.collections.concurrent;

import java.util.Queue;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import org.jetbrains.annotations.NotNull;

/**
 * @author Russell Harmon
 */
public class SynchronizedQueueFacade<T, D extends Queue<T>> extends SynchronizedCollectionFacade<T, D> implements SynchronizedQueue<T, D> {
    public SynchronizedQueueFacade( D delegate, @NotNull Lock lock ) {
        super(delegate, lock);
    }

    public SynchronizedQueueFacade( @NotNull D delegate, @NotNull ReadWriteLock lock ) {
        super(delegate, lock);
    }

    public SynchronizedQueueFacade( @NotNull D delegate ) {
        super(delegate);
    }

    @Override
    public boolean offer( T t ) {
        Lock lock = getWriteLock();
        lock.lock();
        try {
            return getDelegate().offer(t);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public T remove() {
        Lock lock = getWriteLock();
        lock.lock();
        try {
            return getDelegate().remove();
        } finally {
            lock.unlock();
        }
    }

    @Override
    public T poll() {
        Lock lock = getWriteLock();
        lock.lock();
        try {
            return getDelegate().poll();
        } finally {
            lock.unlock();
        }
    }

    @Override
    public T element() {
        Lock lock = getReadLock();
        lock.lock();
        try {
            return getDelegate().element();
        } finally {
            lock.unlock();
        }
    }

    @Override
    public T peek() {
        Lock lock = getReadLock();
        lock.lock();
        try {
            return getDelegate().peek();
        } finally {
            lock.unlock();
        }
    }
}
