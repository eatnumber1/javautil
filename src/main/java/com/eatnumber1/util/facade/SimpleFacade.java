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

package com.eatnumber1.util.facade;

import org.jetbrains.annotations.Nullable;

/**
 * @author Russell Harmon
 * @since Jul 13, 2009
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
