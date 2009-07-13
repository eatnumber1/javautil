package com.eatnumber1.util.collections;

import org.jetbrains.annotations.NotNull;

/**
 * @author Russell Harmon
 */
public interface EqualityComparator<T> {
    boolean equals( @NotNull T o1, @NotNull T o2 );
}
