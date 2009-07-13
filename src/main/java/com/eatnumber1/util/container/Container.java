package com.eatnumber1.util.container;

import org.jetbrains.annotations.NotNull;

/**
 * @author Russell Harmon
 */
public interface Container<V> {
    <T, E extends Throwable> T doAction( @NotNull ContainerAction<V, T, E> action ) throws E;
}
