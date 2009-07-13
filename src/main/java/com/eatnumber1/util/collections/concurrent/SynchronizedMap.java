package com.eatnumber1.util.collections.concurrent;

import com.eatnumber1.util.concurrent.lock.ReadWriteLockProvider;
import com.eatnumber1.util.facade.Facade;
import java.util.Map;
import java.util.concurrent.ConcurrentMap;

/**
 * @author Russell Harmon
 */
public interface SynchronizedMap<K, V, D extends Map<K, V>> extends ConcurrentMap<K, V>, ReadWriteLockProvider, Facade<D> {
}
