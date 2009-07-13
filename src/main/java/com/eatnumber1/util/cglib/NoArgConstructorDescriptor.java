package com.eatnumber1.util.cglib;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author Russell Harmon
 */
public class NoArgConstructorDescriptor<T> implements ConstructorDescriptor<T> {
    @NotNull
    private static final Class<?>[] CLASS_ARRAY = new Class<?>[0];

    @NotNull
    private static final Object[] OBJECT_ARRAY = new Object[0];

    @NotNull
    private Class<T> type;

    public NoArgConstructorDescriptor( @NotNull Class<T> type ) {
        this.type = type;
    }

    @NotNull
    public Class<?>[] getArgumentTypes() {
        return CLASS_ARRAY;
    }

    @Nullable
    public Object[] getArguments() {
        return OBJECT_ARRAY;
    }

    @NotNull
    public Class<T> getType() {
        return type;
    }
}
