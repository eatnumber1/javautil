package com.eatnumber1.util.collections.concurrent;

import java.util.List;

/**
 * @author Russell Harmon
 */
public interface SynchronizedList<T, D extends List<T>> extends SynchronizedCollection<T, D>, List<T> {
}
