package com.eatnumber1.util.collections.iterators;

import com.eatnumber1.util.collections.concurrent.iterators.SynchronizedIterator;
import com.eatnumber1.util.collections.concurrent.iterators.SynchronizedListIterator;
import java.util.Iterator;
import java.util.ListIterator;
import org.jetbrains.annotations.NotNull;

/**
 * @author Russell Harmon
 */
public class Iterators {
    private Iterators() {}

    @NotNull
    public static <T, D extends Iterator<T>> Iterator<T> synchronizedIterator( @NotNull D iterator ) {
        return new SynchronizedIterator<T, D>(iterator);
    }

    @NotNull
    public static <T, D extends ListIterator<T>> ListIterator<T> synchronizedListIterator( @NotNull D iterator ) {
        return new SynchronizedListIterator<T, D>(iterator);
    }

    @NotNull
    public static <T, D extends Iterator<T>> Iterator<T> unmodifiableIterator( @NotNull D iterator ) {
        return new UnmodifiableIterator<T, D>(iterator);
    }

    @NotNull
    public static <T, D extends ListIterator<T>> ListIterator<T> unmodifiableListIterator( @NotNull D iterator ) {
        return new UnmodifiableListIterator<T, D>(iterator);
    }
}
