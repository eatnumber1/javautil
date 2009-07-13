package com.eatnumber1.util.facade;

/**
 * @author Russell Harmon
 */
public interface Facade<T> {
    T getDelegate();

    void setDelegate( T delegate );
}
