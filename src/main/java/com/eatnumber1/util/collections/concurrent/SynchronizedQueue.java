package com.eatnumber1.util.collections.concurrent;

import java.util.Queue;

/**
 * @author Russell Harmon
 */
public interface SynchronizedQueue<T, D extends Queue<T>> extends SynchronizedCollection<T, D>, Queue<T> {
}
