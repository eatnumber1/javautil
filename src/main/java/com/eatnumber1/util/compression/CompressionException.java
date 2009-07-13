package com.eatnumber1.util.compression;

/**
 * @author Russell Harmon
 */
public class CompressionException extends Exception {
    public CompressionException( Throwable cause ) {
        super(cause);
    }

    public CompressionException( String message, Throwable cause ) {
        super(message, cause);
    }
}
