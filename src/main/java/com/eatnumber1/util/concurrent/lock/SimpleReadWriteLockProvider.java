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

package com.eatnumber1.util.concurrent.lock;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import net.jcip.annotations.NotThreadSafe;
import org.jetbrains.annotations.NotNull;

/**
 * @author Russell Harmon
 * @since Jul 13, 2007
 */
@NotThreadSafe
public class SimpleReadWriteLockProvider extends SimpleLockProvider implements ReadWriteLockProvider {
    @NotNull
    private ReadWriteLock lock;

    public SimpleReadWriteLockProvider( @NotNull final Lock lock ) {
        super(lock);
        this.lock = new ReadWriteLock() {
            @Override
            public Lock readLock() {
                return lock;
            }

            @Override
            public Lock writeLock() {
                return lock;
            }
        };
    }

    public SimpleReadWriteLockProvider( @NotNull ReadWriteLock lock ) {
        super(lock.writeLock());
        this.lock = lock;
    }

    public SimpleReadWriteLockProvider() {
        this(new ReentrantReadWriteLock());
    }

    @Override
    public Lock readLock() {
        return getReadLock();
    }

    @Override
    public Lock writeLock() {
        return getWriteLock();
    }

    @NotNull
    @Override
    public ReadWriteLock getReadWriteLock() {
        return lock;
    }

    @NotNull
    @Override
    public Lock getReadLock() {
        return lock.readLock();
    }

    @NotNull
    @Override
    public Lock getWriteLock() {
        return lock.writeLock();
    }

    @Override
    public void setReadWriteLock( @NotNull ReadWriteLock lock ) {
        Lock writeLock = this.lock.writeLock();
        writeLock.lock();
        try {
            this.lock = lock;
            super.setLock(lock.writeLock());
        } finally {
            writeLock.unlock();
        }
    }

    @Override
    public void setLock( @NotNull final Lock lock ) {
        setReadWriteLock(new ReadWriteLock() {
            @Override
            public Lock readLock() {
                return lock;
            }

            @Override
            public Lock writeLock() {
                return lock;
            }
        });
    }

    @Override
    public boolean equals( Object o ) {
        if( this == o ) return true;
        if( !( o instanceof SimpleReadWriteLockProvider ) ) return false;
        if( !super.equals(o) ) return false;
        SimpleReadWriteLockProvider that = (SimpleReadWriteLockProvider) o;
        return lock.equals(that.lock);
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + lock.hashCode();
        return result;
    }
}
