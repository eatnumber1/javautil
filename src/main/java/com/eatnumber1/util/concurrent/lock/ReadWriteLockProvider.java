package com.eatnumber1.util.concurrent.lock;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import org.jetbrains.annotations.NotNull;

/**
 * @author Russell Harmon
 */
public interface ReadWriteLockProvider extends ReadWriteLock, LockProvider {
    @NotNull
    ReadWriteLock getReadWriteLock();

    @NotNull
    Lock getReadLock();

    @NotNull
    Lock getWriteLock();

    void setReadWriteLock( @NotNull ReadWriteLock lock );
}
