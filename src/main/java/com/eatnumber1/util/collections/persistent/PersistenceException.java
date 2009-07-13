package com.eatnumber1.util.collections.persistent;

/**
 * @author Russell Harmon
 */
public class PersistenceException extends Exception {
    public PersistenceException( String message, Throwable cause ) {
        super(message, cause);
    }

    public PersistenceException( Throwable cause ) {
        super(cause);
    }
}
