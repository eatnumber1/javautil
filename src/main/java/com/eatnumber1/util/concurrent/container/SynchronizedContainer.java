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

import com.eatnumber1.util.concurrent.facade.SynchronizedFacade;
import com.eatnumber1.util.container.Container;
import com.eatnumber1.util.container.ContainerAction;
import com.eatnumber1.util.container.ContainerException;
import java.util.concurrent.locks.Lock;
import net.jcip.annotations.ThreadSafe;
import org.jetbrains.annotations.NotNull;

/**
 * @author Russell Harmon
 * @since Jul 13, 2007
 */
@ThreadSafe
public class SynchronizedContainer<V> extends SynchronizedFacade<V> implements Container<V> {
    public SynchronizedContainer( V delegate ) {
        super(delegate);
    }

    public SynchronizedContainer( V delegate, @NotNull Lock lock ) {
        super(delegate, lock);
    }

    public SynchronizedContainer() {
    }

    @Override
    public <T> T doAction( @NotNull ContainerAction<V, T> action ) throws ContainerException {
        Lock lock = getLock();
        lock.lock();
        try {
            return doActionInternal(action);
        } finally {
            lock.unlock();
        }
    }

    protected <T> T doActionInternal( @NotNull ContainerAction<V, T> action ) throws ContainerException {
        return action.doAction(getDelegate());
    }
}
