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

package com.eatnumber1.util.concurrent.container;

import com.eatnumber1.util.concurrent.facade.SynchronizedReadWriteFacade;
import com.eatnumber1.util.container.Container;
import com.eatnumber1.util.container.ContainerAction;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import net.jcip.annotations.ThreadSafe;
import org.jetbrains.annotations.NotNull;

/**
 * @author Russell Harmon
 * @since Jul 13, 2009
 */
@ThreadSafe
public class SynchronizedContainer<V> extends SynchronizedReadWriteFacade<V> implements Container<V> {
    public SynchronizedContainer( V delegate ) {
        super(delegate);
    }

    public SynchronizedContainer( V delegate, @NotNull ReadWriteLock lock ) {
        super(delegate, lock);
    }

    public SynchronizedContainer( V delegate, @NotNull Lock lock ) {
        super(delegate, lock);
    }

    @Override
    public <T, E extends Throwable> T doAction( @NotNull ContainerAction<V, T, E> action ) throws E {
        Lock lock = getLock();
        lock.lock();
        try {
            return action.doAction(getDelegate());
        } catch( RuntimeException e ) {
            throw e;
        } catch( Exception e ) {
            try {
                //noinspection unchecked
                throw (E) e;
            } catch( ClassCastException e1 ) {
                throw new RuntimeException(e);
            }
        } catch( Error e ) {
            try {
                //noinspection unchecked
                throw (E) e;
            } catch( ClassCastException e1 ) {
                throw e;
            }
        } finally {
            lock.unlock();
        }
    }
}
