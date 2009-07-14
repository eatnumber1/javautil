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

package com.eatnumber1.util.concurrent;

import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import org.jetbrains.annotations.NotNull;

/**
 * @author Russell Harmon
 * @since Jul 13, 2007
 */
public class ThreadAdapter<T> implements Callable<T>, Runnable {
    @NotNull
    private Callable<T> delegate;

    public ThreadAdapter( @NotNull Runnable delegate ) {
        //noinspection unchecked
        this.delegate = (Callable<T>) Executors.callable(delegate);
    }

    public ThreadAdapter( @NotNull Runnable delegate, @NotNull T result ) {
        this.delegate = Executors.callable(delegate, result);
    }

    public ThreadAdapter( @NotNull Callable<T> delegate ) {
        this.delegate = delegate;
    }

    @NotNull
    protected Callable<T> getDelegate() {
        return delegate;
    }

    public void run() {
        try {
            call();
        } catch( Exception e ) {
            throw new RuntimeException(e);
        }
    }

    public T call() throws Exception {
        return delegate.call();
    }
}