package com.eatnumber1.util.observer;

import com.eatnumber1.util.collections.Collections;
import com.eatnumber1.util.collections.concurrent.SynchronizedSet;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author Russell Harmon
 * @since Jul 13, 2007
 */
public class SimpleObservable<T> implements Observable<T> {
    @NotNull
    protected SynchronizedSet<Observer<T>, ? extends Set<Observer<T>>> observers;

    @NotNull
    private ObservableNotificationProvider<T> notificationProvider;

    public SimpleObservable( @NotNull Set<Observer<T>> observers, @NotNull ObservableNotificationProvider<T> notificationProvider ) {
        this.observers = Collections.synchronizedSet(new HashSet<Observer<T>>());
        this.observers.addAll(observers);
        this.notificationProvider = notificationProvider;
    }

    public SimpleObservable( @NotNull Set<Observer<T>> observers ) {
        this(observers, new CopyOnNotifyNotificationProvider<T>());
    }

    public SimpleObservable() {
        //noinspection unchecked
        this(java.util.Collections.EMPTY_SET);
    }

    public SimpleObservable( @NotNull Observer<T>... observers ) {
        this();
        this.observers.addAll(Arrays.asList(observers));
    }

    @Override
    public void addObserver( @NotNull Observer<T> o ) {
        observers.add(o);
    }

    @Override
    public void deleteObserver( @Nullable Observer<T> o ) {
        observers.remove(o);
    }

    @Override
    public void notifyObservers() {
        notifyObservers(null);
    }

    @Override
    public void notifyObservers( @Nullable T arg ) {
        notificationProvider.notifyObservers(this, observers, arg);
    }

    @Override
    public void deleteObservers() {
        observers.clear();
    }

    @Override
    public int countObservers() {
        return observers.size();
    }

    @NotNull
    @Override
    public ObservableNotificationProvider<T> getNotificationProvider() {
        return notificationProvider;
    }

    @Override
    public void setNotificationProvider( @NotNull ObservableNotificationProvider<T> notificationProvider ) {
        this.notificationProvider = notificationProvider;
    }
}
