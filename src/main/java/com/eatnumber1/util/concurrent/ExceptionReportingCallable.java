package com.eatnumber1.util.concurrent;

import java.util.concurrent.Callable;
import org.apache.log4j.Logger;
import org.jetbrains.annotations.NotNull;

/**
 * @author Russell Harmon
 */
public class ExceptionReportingCallable<T> implements Callable<T> {
    @NotNull
    private static Logger log = Logger.getLogger(ExceptionReportingCallable.class);

    @NotNull
    private Callable<T> delegate;

    public ExceptionReportingCallable( @NotNull Callable<T> delegate ) {
        this.delegate = delegate;
    }

    public T call() throws Exception {
        try {
            return delegate.call();
        } catch( Exception e ) {
            log.error("", e);
            throw e;
        } catch( Error e ) {
            log.error("", e);
            throw e;
        }
    }
}
