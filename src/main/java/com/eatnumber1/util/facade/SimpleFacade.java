package com.eatnumber1.util.facade;

import org.jetbrains.annotations.Nullable;

/**
 * @author Russell Harmon
 */
public class SimpleFacade<T> implements Facade<T> {
    private T delegate;

    public SimpleFacade() {
    }

    public SimpleFacade( T delegate ) {
        this.delegate = delegate;
    }

    public T getDelegate() {
        return delegate;
    }

    public void setDelegate( T delegate ) {
        this.delegate = delegate;
    }

    @Override
    public boolean equals( @Nullable Object o ) {
        if( this == o || delegate == o ) return true;
        if( o == null ) return false;
        if( o instanceof Facade ) {
            Object thatDelegate = ( (Facade) o ).getDelegate();
            return delegate != null ? delegate.equals(thatDelegate) : thatDelegate == null;
        } else {
            return delegate != null && delegate.equals(o);
        }
    }

    @Override
    public int hashCode() {
        return delegate != null ? delegate.hashCode() : 0;
    }
}
