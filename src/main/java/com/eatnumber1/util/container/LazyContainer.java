package com.eatnumber1.util.container;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import net.jcip.annotations.ThreadSafe;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author Russell Harmon
 * @since Jul 13, 2007
 */
@ThreadSafe
public abstract class LazyContainer<T> extends SimpleContainer<T> {
    @NotNull
    private Lock lock = new ReentrantLock();

    protected LazyContainer() {
    }

    protected LazyContainer( @Nullable T delegate ) {
        super(delegate);
    }

    @Nullable
    @Override
    public T getDelegate() {
        lock.lock();
        try {
            if( super.getDelegate() == null ) setDelegate(getInternal());
            return super.getDelegate();
        } finally {
            lock.unlock();
        }
    }

    public void clear() {
        lock.lock();
        try {
            super.setDelegate(null);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void setDelegate( @Nullable T property ) {
        lock.lock();
        try {
            super.setDelegate(property);
        } finally {
            lock.unlock();
        }
    }

    @Nullable
    protected abstract T getInternal();
}
