package com.eatnumber1.util.cglib;

import net.sf.cglib.proxy.Dispatcher;
import org.jetbrains.annotations.NotNull;

/**
 * @author Russell Harmon
 */
public class DelegatingCallback<T> implements Dispatcher {
    @NotNull
    private T delegate;

    public DelegatingCallback( @NotNull T delegate ) {
        this.delegate = delegate;
    }

    @NotNull
    public Object loadObject() throws Exception {
        return delegate;
    }
}
