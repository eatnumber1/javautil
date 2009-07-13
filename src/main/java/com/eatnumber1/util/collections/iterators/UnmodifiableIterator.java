package com.eatnumber1.util.collections.iterators;

import com.eatnumber1.util.facade.SimpleFacade;
import java.util.Iterator;
import org.jetbrains.annotations.NotNull;

/**
 * @author Russell Harmon
 */
public class UnmodifiableIterator<T, D extends Iterator<T>> extends SimpleFacade<D> implements Iterator<T> {
    public UnmodifiableIterator( @NotNull D delegate ) {
        super(delegate);
    }

    public boolean hasNext() {
        return getDelegate().hasNext();
    }

    public T next() {
        return getDelegate().next();
    }

    public void remove() {
        throw new UnsupportedOperationException();
    }
}
