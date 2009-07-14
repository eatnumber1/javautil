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
import net.jcip.annotations.ThreadSafe;
import org.jetbrains.annotations.NotNull;

/**
 * @author Russell Harmon
 * @since Jul 13, 2009
 */
@ThreadSafe
public class SynchronizedReadWriteLockProvider extends SimpleReadWriteLockProvider {
    protected SynchronizedReadWriteLockProvider( @NotNull Lock lock ) {
        super(lock);
    }

    protected SynchronizedReadWriteLockProvider( @NotNull ReadWriteLock lock ) {
        super(lock);
    }

    protected SynchronizedReadWriteLockProvider() {
    }

    @SuppressWarnings({ "EqualsWhichDoesntCheckParameterClass" })
    @Override
    public final boolean equals( Object obj ) {
        Lock lock = getLock();
        lock.lock();
        try {
            return equalsInternal(obj);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public final int hashCode() {
        Lock lock = getLock();
        lock.lock();
        try {
            return hashCodeInternal();
        } finally {
            lock.unlock();
        }
    }

    protected boolean equalsInternal( Object obj ) {
        return super.equals(obj);
    }

    protected int hashCodeInternal() {
        return super.hashCode();
    }
}
