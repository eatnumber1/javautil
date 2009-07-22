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

package com.eatnumber1.util.concurrent;

import com.eatnumber1.util.concurrent.lock.CompositeLock;
import com.eatnumber1.util.concurrent.lock.CompositeLockFactory;
import java.util.Collection;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import org.jetbrains.annotations.NotNull;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * @author Russell Harmon
 * @since Jul 22, 2009
 */
public abstract class AbstractCompositeLockTest {
    @NotNull
    protected ReentrantLock lock1, lock2, lock3;

    @NotNull
    protected CompositeLock compositeLock;

    @Before
    public void setUp() {
        lock1 = new ReentrantLock();
        lock2 = new ReentrantLock();
        lock3 = new ReentrantLock();
        CompositeLockFactory factory = newCompositeLockFactory();
        factory.addLocks(lock1, lock2, lock3);
        compositeLock = factory.getLock();
    }

    @NotNull
    protected abstract CompositeLockFactory newCompositeLockFactory();

    @Test
    public void containsAllLocks() {
        Collection<Lock> locks = compositeLock.getLocks();
        Assert.assertTrue(locks.contains(lock1));
        Assert.assertTrue(locks.contains(lock2));
        Assert.assertTrue(locks.contains(lock3));
    }

    @Test
    public void lockLocksAllLocks() {
        compositeLock.lock();
        Assert.assertEquals(1, lock1.getHoldCount());
        Assert.assertEquals(1, lock2.getHoldCount());
        Assert.assertEquals(1, lock3.getHoldCount());
    }

    @Test
    public void unlockUnlocksAllLocks() {
        compositeLock.lock();
        compositeLock.unlock();
        Assert.assertEquals(0, lock1.getHoldCount());
        Assert.assertEquals(0, lock2.getHoldCount());
        Assert.assertEquals(0, lock3.getHoldCount());
    }
}
