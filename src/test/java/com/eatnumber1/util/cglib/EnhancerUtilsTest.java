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

package com.eatnumber1.util.cglib;

import com.eatnumber1.util.concurrent.lock.LockProvider;
import java.util.concurrent.locks.Lock;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author Russell Harmon
 * @since Jul 13, 2009
 */
public class EnhancerUtilsTest {
    private static final int ITERATIONS = 1000000;

    private static class Reference<T> {
        private volatile T referent;

        private Reference( T referent ) {
            this.referent = referent;
        }

        public T getReferent() {
            return referent;
        }

        public void setReferent( T referent ) {
            this.referent = referent;
        }
    }

    public static class IncrementableInteger {
        private Reference<Integer> num = new Reference<Integer>(0);

        public int increment() {
            num.setReferent(num.getReferent() + 1);
            return num.getReferent();
        }

        public Reference<Integer> getNum() {
            return num;
        }

        public void setNum( Reference<Integer> num ) {
            this.num = num;
        }
    }

    @Test
    public void synchronize() throws InterruptedException {
        final IncrementableInteger integer = EnhancerUtils.synchronize(new IncrementableInteger());

        Thread[] threads = {
                new Thread() {
                    @Override
                    public void run() {
                        for( int i = 0; i < ITERATIONS; i++ ) {
                            integer.increment();
                        }
                    }
                },
                new Thread() {
                    @Override
                    public void run() {
                        for( int i = 0; i < ITERATIONS; i++ ) {
                            integer.increment();
                        }
                    }
                },
                new Thread() {
                    @Override
                    public void run() {
                        Lock lock = ( (LockProvider) integer ).getLock();
                        for( int i = 0; i < ITERATIONS; i++ ) {
                            lock.lock();
                            try {
                                Reference<Integer> ref = integer.getNum();
                                ref.setReferent(ref.getReferent() + 1);
                            } finally {
                                lock.unlock();
                            }
                        }
                    }
                }
        };

        for( Thread thread : threads ) {
            thread.start();
        }
        for( Thread thread : threads ) {
            thread.join();
        }

        // Prevents ambiguous method call.
        //noinspection RedundantCast
        Assert.assertEquals(threads.length * ITERATIONS, (long) integer.getNum().getReferent());
    }
}
