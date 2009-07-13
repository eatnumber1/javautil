package com.eatnumber1.util.collections.concurrent;

import java.util.Set;

/**
 * @author Russell Harmon
 */
public interface SynchronizedSet<T, D extends Set<T>> extends SynchronizedCollection<T, D>, Set<T> {
}
