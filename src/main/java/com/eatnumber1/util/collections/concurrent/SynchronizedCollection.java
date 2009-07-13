package com.eatnumber1.util.collections.concurrent;

import com.eatnumber1.util.concurrent.lock.ReadWriteLockProvider;
import com.eatnumber1.util.facade.Facade;
import java.util.Collection;

/**
 * @author Russell Harmon
 */
public interface SynchronizedCollection<T, D extends Collection<T>> extends Collection<T>, ReadWriteLockProvider, Facade<D> {
}
