/*
 * Copyright 2007 Russell Harmon
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package com.eatnumber1.util.collections.concurrent;

import com.eatnumber1.util.collections.concurrent.iterators.SynchronizedIterator;
import java.util.Deque;
import java.util.Iterator;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import org.jetbrains.annotations.NotNull;

/**
 * @author Russell Harmon
 * @since Jul 13, 2009
 */
public class SynchronizedDequeFacade<T, D extends Deque<T>> extends SynchronizedQueueFacade<T, D> implements SynchronizedDeque<T, D> {
    public SynchronizedDequeFacade( D delegate, @NotNull Lock lock ) {
        super(delegate, lock);
    }

    public SynchronizedDequeFacade( @NotNull D delegate, @NotNull ReadWriteLock lock ) {
        super(delegate, lock);
    }

    public SynchronizedDequeFacade( @NotNull D delegate ) {
        super(delegate);
    }

    @Override
    public void addFirst( T t ) {
        Lock lock = writeLock();
        lock.lock();
        try {
            getDelegate().addFirst(t);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void addLast( T t ) {
        Lock lock = writeLock();
        lock.lock();
        try {
            getDelegate().addLast(t);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public boolean offerFirst( T t ) {
        Lock lock = writeLock();
        lock.lock();
        try {
            return getDelegate().offerFirst(t);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public boolean offerLast( T t ) {
        Lock lock = writeLock();
        lock.lock();
        try {
            return getDelegate().offerLast(t);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public T removeFirst() {
        Lock lock = writeLock();
        lock.lock();
        try {
            return getDelegate().removeFirst();
        } finally {
            lock.unlock();
        }
    }

    @Override
    public T removeLast() {
        Lock lock = writeLock();
        lock.lock();
        try {
            return getDelegate().removeLast();
        } finally {
            lock.unlock();
        }
    }

    @Override
    public T pollFirst() {
        Lock lock = writeLock();
        lock.lock();
        try {
            return getDelegate().pollFirst();
        } finally {
            lock.unlock();
        }
    }

    @Override
    public T pollLast() {
        Lock lock = writeLock();
        lock.lock();
        try {
            return getDelegate().pollLast();
        } finally {
            lock.unlock();
        }
    }

    @Override
    public T getFirst() {
        Lock lock = readLock();
        lock.lock();
        try {
            return getDelegate().getFirst();
        } finally {
            lock.unlock();
        }
    }

    @Override
    public T getLast() {
        Lock lock = readLock();
        lock.lock();
        try {
            return getDelegate().getLast();
        } finally {
            lock.unlock();
        }
    }

    @Override
    public T peekFirst() {
        Lock lock = readLock();
        lock.lock();
        try {
            return getDelegate().peekFirst();
        } finally {
            lock.unlock();
        }
    }

    @Override
    public T peekLast() {
        Lock lock = readLock();
        lock.lock();
        try {
            return getDelegate().peekLast();
        } finally {
            lock.unlock();
        }
    }

    @Override
    public boolean removeFirstOccurrence( Object o ) {
        Lock lock = writeLock();
        lock.lock();
        try {
            return getDelegate().removeFirstOccurrence(o);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public boolean removeLastOccurrence( Object o ) {
        Lock lock = writeLock();
        lock.lock();
        try {
            return getDelegate().removeLastOccurrence(o);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void push( T t ) {
        Lock lock = writeLock();
        lock.lock();
        try {
            getDelegate().push(t);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public T pop() {
        Lock lock = writeLock();
        lock.lock();
        try {
            return getDelegate().pop();
        } finally {
            lock.unlock();
        }
    }

    @Override
    public Iterator<T> descendingIterator() {
        Lock lock = readLock();
        lock.lock();
        try {
            return new SynchronizedIterator<T, Iterator<T>>(getDelegate().descendingIterator(), getReadWriteLock());
        } finally {
            lock.unlock();
        }
    }
}
