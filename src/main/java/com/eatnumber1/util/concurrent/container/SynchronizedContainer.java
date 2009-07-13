package com.eatnumber1.util.concurrent.container;

import com.eatnumber1.util.concurrent.facade.SynchronizedReadWriteFacade;
import com.eatnumber1.util.container.Container;
import com.eatnumber1.util.container.ContainerAction;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import org.jetbrains.annotations.NotNull;

/**
 * @author Russell Harmon
 */
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
