package com.eatnumber1.util.concurrent.executors;

import java.util.concurrent.ExecutorService;
import org.jetbrains.annotations.NotNull;

/**
 * @author Russell Harmon
 * @since Jul 13, 2007
 */
public interface ExecutorServiceFactory {
    @NotNull
    ExecutorService getExecutorService();
}
