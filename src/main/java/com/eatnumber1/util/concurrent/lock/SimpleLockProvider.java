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
import java.util.concurrent.locks.ReentrantLock;
import net.jcip.annotations.NotThreadSafe;
import org.jetbrains.annotations.NotNull;

/**
 * @author Russell Harmon
 * @since Jul 13, 2009
 */
@NotThreadSafe
public class SimpleLockProvider implements LockProvider {
    @NotNull
    private Lock lock;

    public SimpleLockProvider( @NotNull Lock lock ) {
        this.lock = lock;
    }

    public SimpleLockProvider() {
        lock = new ReentrantLock();
    }

    @NotNull
    public Lock getLock() {
        return lock;
    }

    public void setLock( @NotNull Lock lock ) {
        Lock oldLock = this.lock;
        oldLock.lock();
        try {
            this.lock = lock;
        } finally {
            oldLock.unlock();
        }
    }

    @Override
    public boolean equals( Object o ) {
        if( this == o ) return true;
        if( !( o instanceof SimpleLockProvider ) ) return false;
        SimpleLockProvider that = (SimpleLockProvider) o;
        return lock.equals(that.lock);
    }

    @Override
    public int hashCode() {
        return lock.hashCode();
    }
}
