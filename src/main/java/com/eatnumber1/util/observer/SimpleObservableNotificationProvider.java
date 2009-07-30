package com.eatnumber1.util.observer;

import com.eatnumber1.util.collections.concurrent.SynchronizedCollection;
import java.util.Collection;
import java.util.concurrent.locks.Lock;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author Russell Harmon
 * @since Jul 13, 2007
 *        <p/>
 *        WARNING: If the observable's observers will be modified in any way by the observer, using this class will cause deadlock.
 */
public class SimpleObservableNotificationProvider<T> implements ObservableNotificationProvider<T> {
    @Override
    public void notifyObservers( @NotNull Observable<T> observable, @NotNull SynchronizedCollection<Observer<T>, ? extends Collection<Observer<T>>> observers, @Nullable T arg ) {
        Lock readLock = observers.readLock();
        readLock.lock();
        try {
            for( Observer<T> observer : observers ) {
                observer.update(observable, arg);
            }
        } finally {
            readLock.unlock();
        }
    }
}
