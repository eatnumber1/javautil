package com.eatnumber1.util.collections.iterators;

import java.util.ListIterator;
import org.jetbrains.annotations.NotNull;

/**
 * @author Russell Harmon
 */
public class UnmodifiableListIterator<T, D extends ListIterator<T>> extends UnmodifiableIterator<T, D> implements ListIterator<T> {
    public UnmodifiableListIterator( @NotNull D delegate ) {
        super(delegate);
    }

    public boolean hasPrevious() {
        return getDelegate().hasPrevious();
    }

    public T previous() {
        return getDelegate().previous();
    }

    public int nextIndex() {
        return getDelegate().nextIndex();
    }

    public int previousIndex() {
        return getDelegate().previousIndex();
    }

    public void set( T o ) {
        throw new UnsupportedOperationException();
    }

    public void add( T o ) {
        throw new UnsupportedOperationException();
    }
}
