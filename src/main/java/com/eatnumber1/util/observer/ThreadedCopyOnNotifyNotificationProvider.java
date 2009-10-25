/*
 * Copyright 2007 Russell Harmon
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package com.eatnumber1.util.observer;

import com.eatnumber1.util.collections.concurrent.SynchronizedCollection;
import com.eatnumber1.util.concurrent.executors.ExecutorServiceFactory;
import com.eatnumber1.util.concurrent.executors.ManagedExecutorService;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.Lock;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author Russell Harmon
 * @since Aug 7, 2009
 */
public class ThreadedCopyOnNotifyNotificationProvider<T> implements ObservableNotificationProvider<T> {
    @NotNull
    private ExecutorService threadPool;

    public ThreadedCopyOnNotifyNotificationProvider( @NotNull ExecutorService threadPool ) {
        this.threadPool = threadPool;
    }

    public ThreadedCopyOnNotifyNotificationProvider() {
        threadPool = new ManagedExecutorService(new ExecutorServiceFactory() {
            @NotNull
            @Override
            public ExecutorService getExecutorService() {
                return Executors.newCachedThreadPool();
            }
        });
    }

    @Override
    public void notifyObservers( @NotNull final Observable<T> observable, @NotNull SynchronizedCollection<Observer<T>, ? extends Collection<Observer<T>>> observers, @Nullable final T arg ) {
        Set<Observer<T>> observersCopy;
        Lock readLock = observers.readLock();
        readLock.lock();
        try {
            observersCopy = new HashSet<Observer<T>>(observers);
        } finally {
            readLock.unlock();
        }
        for( final Observer<T> observer : observersCopy ) {
            threadPool.submit(new Runnable() {
                @Override
                public void run() {
                    observer.update(observable, arg);
                }
            });
        }
    }
}
