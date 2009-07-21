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
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import org.jetbrains.annotations.NotNull;

/**
 * @author Russell Harmon
 * @since Jul 21, 2009
 */
public class BasicCompositeLockFactory extends AbstractCompositeLockFactory {
    public static CompositeLock getCompositeLock( @NotNull Lock... locks ) {
        return getCompositeLock(Arrays.asList(locks));
    }

    public static CompositeLock getCompositeLock( @NotNull List<Lock> locks ) {
        return new BasicCompositeLockFactory(locks).getLock();
    }

    @NotNull
    private List<Lock> orderedLocks = new LinkedList<Lock>();

    public BasicCompositeLockFactory() {
    }

    public BasicCompositeLockFactory( @NotNull List<Lock> orderedLocks ) {
        this.orderedLocks.addAll(orderedLocks);
    }

    @NotNull
    public CompositeLock getLock( @NotNull final Collection<Lock> locks ) {
        return new CompositeLock() {
            @NotNull
            private List<Lock> orderedLocks;

            {
                LinkedList<Lock> lockLinkedList = new LinkedList<Lock>(BasicCompositeLockFactory.this.orderedLocks);
                lockLinkedList.retainAll(locks);
                orderedLocks = Collections.unmodifiableList(new ArrayList<Lock>(lockLinkedList));
            }

            @NotNull
            public Collection<Lock> getLocks() {
                return Collections.unmodifiableCollection(orderedLocks);
            }

            public void lock() {
                for( Lock lock : orderedLocks ) {
                    lock.lock();
                }
            }

            public void lockInterruptibly() throws InterruptedException {
                for( Lock lock : orderedLocks ) {
                    lock.lockInterruptibly();
                }
            }

            public boolean tryLock() {
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
            }

            public boolean tryLock( long time, TimeUnit unit ) throws InterruptedException {
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
            }

            public void unlock() {
                for( Lock lock : orderedLocks ) {
                    lock.unlock();
                }
            }

            public Condition newCondition() {
                return new Condition() {
                    @NotNull
                    private List<Condition> conditions = new ArrayList<Condition>(orderedLocks.size());

                    {
                        for( Lock lock : orderedLocks ) {
                            conditions.add(lock.newCondition());
                        }
                    }

                    // Not sure how to implement these methods.
                    public void await() throws InterruptedException {
                        throw new UnsupportedOperationException();
                    }

                    public void awaitUninterruptibly() {
                        throw new UnsupportedOperationException();
                    }

                    public long awaitNanos( long nanosTimeout ) throws InterruptedException {
                        throw new UnsupportedOperationException();
                    }

                    public boolean await( long time, TimeUnit unit ) throws InterruptedException {
                        throw new UnsupportedOperationException();
                    }

                    public boolean awaitUntil( Date deadline ) throws InterruptedException {
                        throw new UnsupportedOperationException();
                    }

                    public void signal() {
                        for( Condition c : conditions ) {
                            c.signal();
                        }
                    }

                    public void signalAll() {
                        for( Condition c : conditions ) {
                            c.signalAll();
                        }
                    }
                };
            }
        };
    }

    public void addLock( @NotNull Lock lock ) {
        orderedLocks.add(lock);
    }

    public void addLocks( @NotNull Collection<? extends Lock> locks ) {
        orderedLocks.addAll(locks);
    }
}
