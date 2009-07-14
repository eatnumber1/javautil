package com.eatnumber1.util.observer;

import com.eatnumber1.util.collections.concurrent.SynchronizedCollection;
import java.util.Collection;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author Russell Harmon
 * @since Jul 13, 2007
 */
public interface ObservableNotificationProvider<T> {
    void notifyObservers( @NotNull Observable<T> observable, @NotNull SynchronizedCollection<Observer<T>, ? extends Collection<Observer<T>>> observers, @Nullable T arg );
}
