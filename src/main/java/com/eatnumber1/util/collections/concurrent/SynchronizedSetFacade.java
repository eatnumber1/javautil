package com.eatnumber1.util.collections.concurrent;

import java.util.Set;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import org.jetbrains.annotations.NotNull;

/**
 * @author Russell Harmon
 */
public class SynchronizedSetFacade<T, D extends Set<T>> extends SynchronizedCollectionFacade<T, D> implements SynchronizedSet<T, D> {
    public SynchronizedSetFacade( D delegate, @NotNull Lock lock ) {
        super(delegate, lock);
    }

    public SynchronizedSetFacade( @NotNull D delegate, @NotNull ReadWriteLock lock ) {
        super(delegate, lock);
    }

    public SynchronizedSetFacade( @NotNull D delegate ) {
        super(delegate);
    }
}
