package com.eatnumber1.util.collections.concurrent;

import java.util.Deque;

/**
 * @author Russell Harmon
 */
public interface SynchronizedDeque<T, D extends Deque<T>> extends SynchronizedQueue<T, D>, Deque<T> {
}
