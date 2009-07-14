package com.eatnumber1.util.observer;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author Russell Harmon
 * @since Jul 13, 2007
 */
public interface Observable<T> {
    void addObserver( @NotNull Observer<T> o );

    void deleteObserver( @Nullable Observer<T> o );

    void notifyObservers();

    void notifyObservers( @Nullable T arg );

    void deleteObservers();

    int countObservers();

    @NotNull
    ObservableNotificationProvider<T> getNotificationProvider();

    void setNotificationProvider( @NotNull ObservableNotificationProvider<T> notificationProvider );
}
