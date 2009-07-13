package com.eatnumber1.util.collections.persistent;

import java.io.Closeable;
import java.io.Flushable;

/**
 * @author Russell Harmon
 */
public interface FileBackedPersistentCollection<T> extends PersistentCollection<T>, Closeable, Flushable {
}
