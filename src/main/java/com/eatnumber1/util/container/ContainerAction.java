package com.eatnumber1.util.container;

import org.jetbrains.annotations.Nullable;

/**
 * @author Russell Harmon
 */
public interface ContainerAction<T, V, E extends Throwable> {
    V doAction( @Nullable T param ) throws E;
}
