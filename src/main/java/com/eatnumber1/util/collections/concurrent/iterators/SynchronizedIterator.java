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

import com.eatnumber1.util.concurrent.facade.SynchronizedReadWriteFacade;
import java.util.Iterator;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import org.jetbrains.annotations.NotNull;

/**
 * @author Russell Harmon
 * @since Jul 13, 2009
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
