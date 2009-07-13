package com.eatnumber1.util.cglib;

import com.eatnumber1.util.concurrent.lock.SynchronizedLockProvider;
import com.eatnumber1.util.facade.Facade;
import java.lang.reflect.Method;
import java.util.concurrent.locks.Lock;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;
import org.jetbrains.annotations.NotNull;

/**
 * @author Russell Harmon
 */
public class SynchronizedMethodInterceptor<T> extends SynchronizedLockProvider implements MethodInterceptor, Facade<T> {
    @NotNull
    private T delegate;

    public SynchronizedMethodInterceptor( @NotNull T delegate, @NotNull Lock lock ) {
        super(lock);
        this.delegate = delegate;
    }

    public SynchronizedMethodInterceptor( @NotNull T delegate ) {
        this.delegate = delegate;
    }

    public Object intercept( Object o, Method method, Object[] objects, MethodProxy methodProxy ) throws Throwable {
        Lock lock = getLock();
        lock.lock();
        try {
            return method.invoke(delegate, objects);
        } finally {
            lock.unlock();
        }
    }

    @NotNull
    @Override
    public T getDelegate() {
        return delegate;
    }

    @Override
    public void setDelegate( @NotNull T delegate ) {
        this.delegate = delegate;
    }
}
