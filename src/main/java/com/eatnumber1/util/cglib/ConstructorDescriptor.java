package com.eatnumber1.util.cglib;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author Russell Harmon
 */
public interface ConstructorDescriptor<T> {
    @NotNull
    Class<?>[] getArgumentTypes();

    @Nullable
    Object[] getArguments();

    @NotNull
    Class<T> getType();
}
