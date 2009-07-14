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

import com.eatnumber1.util.compat.Override;
import java.util.Queue;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import net.jcip.annotations.ThreadSafe;
import org.jetbrains.annotations.NotNull;

/**
 * @author Russell Harmon
 * @since Jul 13, 2007
 */
@ThreadSafe
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
