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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import org.jetbrains.annotations.NotNull;

/**
 * @author Russell Harmon
 * @since Jul 21, 2009
 */
public class BasicOrderedLockFactory extends AbstractOrderedLockFactory {
    public static OrderedLock getOrderedLock( @NotNull Lock... locks ) {
        return getOrderedLock(Arrays.asList(locks));
    }

    public static OrderedLock getOrderedLock( @NotNull Collection<? extends Lock> locks ) {
        return new BasicOrderedLockFactory(locks).getOrderedLock();
    }

    @NotNull
    private List<Lock> orderedLocks = new LinkedList<Lock>();

    public BasicOrderedLockFactory() {
    }

    public BasicOrderedLockFactory( @NotNull Collection<? extends Lock> orderedLocks ) {
        this.orderedLocks.addAll(orderedLocks);
    }

    @NotNull
    public OrderedLock getOrderedLock() {
        return new AbstractOrderedLock() {
            @NotNull
            private List<Lock> orderedLocks = Collections.unmodifiableList(new ArrayList<Lock>(BasicOrderedLockFactory.this.orderedLocks));

            @NotNull
            private Lock lock = new ReentrantLock();

            @NotNull
            public List<Lock> getLockList() {
                return orderedLocks;
            }

            public void lock() {
                lock.lock();
                try {
                    for( Lock lock : orderedLocks ) {
                        lock.lock();
                    }
                } finally {
                    lock.unlock();
                }
            }

            public void lockInterruptibly() throws InterruptedException {
                lock.lock();
                try {
                    for( Lock lock : orderedLocks ) {
                        lock.lockInterruptibly();
                    }
                } finally {
                    lock.unlock();
                }
            }

            public boolean tryLock() {
                lock.lock();
                try {
                    HashSet<Lock> acquiredLocks = new HashSet<Lock>(orderedLocks.size());
                    for( Lock lock : orderedLocks ) {
                        if( lock.tryLock() ) {
                            acquiredLocks.add(lock);
                        } else {
                            for( Lock acquiredLock : acquiredLocks ) {
                                acquiredLock.unlock();
                            }
                            return false;
                        }
                    }
                    return true;
                } finally {
                    lock.unlock();
                }
            }

            public boolean tryLock( long time, TimeUnit unit ) throws InterruptedException {
                lock.lock();
                try {
                    HashSet<Lock> acquiredLocks = new HashSet<Lock>(orderedLocks.size());
                    for( Lock lock : orderedLocks ) {
                        if( lock.tryLock(time, unit) ) {
                            acquiredLocks.add(lock);
                        } else {
                            for( Lock acquiredLock : acquiredLocks ) {
                                acquiredLock.unlock();
                            }
                            return false;
                        }
                    }
                    return true;
                } finally {
                    lock.unlock();
                }
            }

            public void unlock() {
                lock.lock();
                try {
                    for( Lock lock : orderedLocks ) {
                        lock.unlock();
                    }
                } finally {
                    lock.unlock();
                }
            }

            public Condition newCondition() {
                // TODO: Implement this.
                throw new UnsupportedOperationException();
            }
        };
    }

    public void addLocks( @NotNull Collection<? extends Lock> locks ) {
        orderedLocks.addAll(locks);
    }
}
