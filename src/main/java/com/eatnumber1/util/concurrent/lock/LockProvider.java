package com.eatnumber1.util.concurrent.lock;

import java.util.concurrent.locks.Lock;
import org.jetbrains.annotations.NotNull;

/**
 * @author Russell Harmon
 */
public interface LockProvider {
    @NotNull
    Lock getLock();

    void setLock( @NotNull Lock lock );
}
