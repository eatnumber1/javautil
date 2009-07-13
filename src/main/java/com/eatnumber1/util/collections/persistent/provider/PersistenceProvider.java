package com.eatnumber1.util.collections.persistent.provider;

import com.eatnumber1.util.collections.persistent.PersistenceException;
import org.jetbrains.annotations.Nullable;

/**
 * @author Russell Harmon
 */
public interface PersistenceProvider<T> {
    @Nullable
    byte[] toBytes( @Nullable T object ) throws PersistenceException;

    @Nullable
    T fromBytes( @Nullable byte[] bytes ) throws PersistenceException;
}
