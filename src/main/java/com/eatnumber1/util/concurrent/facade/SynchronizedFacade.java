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

package com.eatnumber1.util.concurrent.facade;

import com.eatnumber1.util.concurrent.lock.SimpleLockProvider;
import com.eatnumber1.util.facade.Facade;
import java.util.concurrent.locks.Lock;
import net.jcip.annotations.ThreadSafe;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author Russell Harmon
 * @since Jul 13, 2009
 */
@ThreadSafe
public class SynchronizedFacade<T> extends SimpleLockProvider implements Facade<T> {
    private T delegate;

    public SynchronizedFacade() {
    }

    public SynchronizedFacade( T delegate ) {
        this.delegate = delegate;
    }

    public SynchronizedFacade( T delegate, @NotNull Lock lock ) {
        super(lock);
        this.delegate = delegate;
    }

    @Override
    public String toString() {
        Lock lock = getLock();
        lock.lock();
        try {
            if( delegate == null ) {
                return super.toString();
            } else {
                return delegate.toString();
            }
        } finally {
            lock.unlock();
        }
    }

    @Override
    public boolean equals( @Nullable Object o ) {
        if( this == o ) return true;
        if( o instanceof SynchronizedFacade ) {
            SynchronizedFacade that = (SynchronizedFacade) o;
            Lock lock = getLock(), thatLock = that.getLock();
            lock.lock();
            try {
                thatLock.lock();
                try {
                    return that.delegate == delegate || !( that.delegate == null || delegate == null ) && delegate.equals(that.delegate);
                } finally {
                    thatLock.unlock();
                }
            } finally {
                lock.unlock();
            }
        } else {
            Lock readLock = getLock();
            readLock.lock();
            try {
                return delegate == o || delegate != null && delegate.equals(o);
            } finally {
                readLock.unlock();
            }
        }
    }

    @Override
    public int hashCode() {
        Lock lock = getLock();
        try {
            if( delegate == null ) {
                return super.hashCode();
            } else {
                return delegate.hashCode();
            }
        } finally {
            lock.unlock();
        }
    }

    public void setDelegate( T delegate ) {
        this.delegate = delegate;
    }

    public T getDelegate() {
        return delegate;
    }
}
