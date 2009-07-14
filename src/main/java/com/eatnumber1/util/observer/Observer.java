package com.eatnumber1.util.observer;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author Russell Harmon
 * @since Jul 13, 2007
 */
public interface Observer<T> {
    void update( @NotNull Observable<T> source, @Nullable T arg );
}
