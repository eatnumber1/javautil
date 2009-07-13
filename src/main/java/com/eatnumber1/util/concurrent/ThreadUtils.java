package com.eatnumber1.util.concurrent;

import java.util.concurrent.Callable;
import org.jetbrains.annotations.NotNull;

/**
 * @author Russell Harmon
 */
public class ThreadUtils {
    private ThreadUtils() {}

    @NotNull
    public static <T> Callable<T> caughtCallable( @NotNull Callable<T> callable ) {
        return new ExceptionReportingCallable<T>(callable);
    }
}
