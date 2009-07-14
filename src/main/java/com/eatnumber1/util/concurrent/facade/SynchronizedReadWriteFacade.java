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

import com.eatnumber1.util.concurrent.lock.SynchronizedReadWriteLockProvider;
import com.eatnumber1.util.facade.Facade;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import net.jcip.annotations.ThreadSafe;
import org.jetbrains.annotations.NotNull;

/**
 * @author Russell Harmon
 * @since Jul 13, 2009
 */
@ThreadSafe
public class SynchronizedReadWriteFacade<T> extends SynchronizedReadWriteLockProvider implements Facade<T> {
    private T delegate;

    public SynchronizedReadWriteFacade() {
    }

    public SynchronizedReadWriteFacade( T delegate ) {
        this.delegate = delegate;
    }

    public SynchronizedReadWriteFacade( T delegate, @NotNull ReadWriteLock lock ) {
        super(lock);
        this.delegate = delegate;
    }

    public SynchronizedReadWriteFacade( T delegate, @NotNull Lock lock ) {
        super(lock);
        this.delegate = delegate;
    }

    @Override
    public String toString() {
        Lock readLock = getReadLock();
        readLock.lock();
        try {
            if( delegate == null ) {
                return super.toString();
            } else {
                return delegate.toString();
            }
        } finally {
            readLock.unlock();
        }
    }

    @Override
    protected boolean equalsInternal( Object o ) {
        if( this == o ) return true;
        if( o instanceof SynchronizedReadWriteFacade ) {
            SynchronizedReadWriteFacade that = (SynchronizedReadWriteFacade) o;
            Lock readLock = that.getReadLock();
            readLock.lock();
            try {
                return that.delegate == delegate || !( that.delegate == null || delegate == null ) && delegate.equals(that.delegate);
            } finally {
                readLock.unlock();
            }
        } else {
            return delegate == o || delegate != null && delegate.equals(o);
        }
    }

    @Override
    protected int hashCodeInternal() {
        if( delegate == null ) {
            return super.hashCodeInternal();
        } else {
            return delegate.hashCode();
        }
    }

    public void setDelegate( T delegate ) {
        this.delegate = delegate;
    }

    public T getDelegate() {
        return delegate;
    }
}
