package com.eatnumber1.util.container;

import org.jetbrains.annotations.NotNull;

/**
 * @author Russell Harmon
 */
public interface ReadWriteContainer<V> extends Container<V> {
    <T, E extends Throwable> T doReadAction( @NotNull ContainerAction<V, T, E> action ) throws E;

    <T, E extends Throwable> T doWriteAction( @NotNull ContainerAction<V, T, E> action ) throws E;
}
