package com.eatnumber1.util.container;

import com.eatnumber1.util.concurrent.container.SynchronizedContainer;
import com.eatnumber1.util.concurrent.container.SynchronizedReadWriteContainer;
import org.jetbrains.annotations.NotNull;

/**
 * @author Russell Harmon
 */
public class Containers {
    private Containers() {
    }

    public static <T> Container<T> container( @NotNull T object ) {
        return new SimpleContainer<T>(object);
    }

    public static <T> ReadWriteContainer<T> readWriteContainer( @NotNull T object ) {
        return new SimpleReadWriteContainer<T>(object);
    }

    public static <T> Container<T> synchronizedContainer( @NotNull T object ) {
        return new SynchronizedContainer<T>(object);
    }

    public static <T> ReadWriteContainer<T> synchronizedReadWriteContainer( @NotNull T object ) {
        return new SynchronizedReadWriteContainer<T>(object);
    }
}
