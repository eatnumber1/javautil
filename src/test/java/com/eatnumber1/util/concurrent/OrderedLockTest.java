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

import com.eatnumber1.util.concurrent.lock.BasicOrderedLockFactory;
import com.eatnumber1.util.concurrent.lock.CompositeLockFactory;
import com.eatnumber1.util.concurrent.lock.OrderedLock;
import com.eatnumber1.util.concurrent.lock.OrderedLockFactory;
import java.util.List;
import java.util.concurrent.locks.Lock;
import org.jetbrains.annotations.NotNull;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * @author Russell Harmon
 * @since Jul 22, 2009
 */
public class OrderedLockTest extends AbstractCompositeLockTest {
    @NotNull
    protected CompositeLockFactory newCompositeLockFactory() {
        return new BasicOrderedLockFactory();
    }

    @NotNull
    private OrderedLock orderedLock;

    @Before
    public void setUp() {
        super.setUp();
        OrderedLockFactory factory = new BasicOrderedLockFactory();
        factory.addLocks(lock1, lock2, lock3);
        orderedLock = factory.getOrderedLock();
    }

    @Test
    public void lockOrder() {
        List<Lock> list = orderedLock.getLockList();
        Assert.assertTrue(list.size() == 3);
        Assert.assertTrue(list.get(0).equals(lock1));
        Assert.assertTrue(list.get(1).equals(lock2));
        Assert.assertTrue(list.get(2).equals(lock3));
    }

    // TODO: Check if it locks in order.
}
