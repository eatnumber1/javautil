package com.eatnumber1.util.concurrent.container;

import com.eatnumber1.util.concurrent.facade.SynchronizedReadWriteFacade;
import com.eatnumber1.util.container.ContainerAction;
import com.eatnumber1.util.container.ReadWriteContainer;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import org.jetbrains.annotations.NotNull;

/**
 * @author Russell Harmon
 */
public class SynchronizedReadWriteContainer<D> extends SynchronizedReadWriteFacade<D> implements ReadWriteContainer<D> {
    public SynchronizedReadWriteContainer( @NotNull D delegate ) {
        super(delegate);
    }

    public SynchronizedReadWriteContainer( @NotNull D delegate, @NotNull ReadWriteLock lock ) {
        super(delegate, lock);
    }

    public SynchronizedReadWriteContainer( @NotNull D delegate, @NotNull Lock lock ) {
        super(delegate, lock);
    }

    @Override
    public <T, E extends Throwable> T doAction( @NotNull ContainerAction<D, T, E> action ) throws E {
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

    @Override
    public <T, E extends Throwable> T doReadAction( @NotNull ContainerAction<D, T, E> action ) throws E {
        Lock lock = getWriteLock();
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

    @Override
    public <T, E extends Throwable> T doWriteAction( @NotNull ContainerAction<D, T, E> action ) throws E {
        Lock lock = getReadLock();
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
