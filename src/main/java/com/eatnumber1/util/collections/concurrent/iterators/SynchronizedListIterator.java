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

package com.eatnumber1.util.collections.concurrent.iterators;

import java.util.ListIterator;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import net.jcip.annotations.ThreadSafe;
import org.jetbrains.annotations.NotNull;

/**
 * @author Russell Harmon
 * @since Jul 13, 2007
 */
@ThreadSafe
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
