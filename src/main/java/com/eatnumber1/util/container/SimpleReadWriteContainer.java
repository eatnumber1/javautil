package com.eatnumber1.util.container;

import org.jetbrains.annotations.NotNull;

/**
 * @author Russell Harmon
 */
public class SimpleReadWriteContainer<V> extends SimpleContainer<V> implements ReadWriteContainer<V> {
    public SimpleReadWriteContainer( V delegate ) {
        super(delegate);
    }

    @Override
    public <T, E extends Throwable> T doReadAction( @NotNull ContainerAction<V, T, E> action ) throws E {
        try {
            return action.doAction(getDelegate());
        } catch( RuntimeException e ) {
            throw e;
        } catch( Exception e ) {
            try {
                //noinspection unchecked
                throw (E) e;
            } catch( ClassCastException e1 ) {
                throw new RuntimeException(e);
            }
        } catch( Error e ) {
            try {
                //noinspection unchecked
                throw (E) e;
            } catch( ClassCastException e1 ) {
                throw e;
            }
        }
    }

    @Override
    public <T, E extends Throwable> T doWriteAction( @NotNull ContainerAction<V, T, E> action ) throws E {
        try {
            return action.doAction(getDelegate());
        } catch( RuntimeException e ) {
            throw e;
        } catch( Exception e ) {
            try {
                //noinspection unchecked
                throw (E) e;
            } catch( ClassCastException e1 ) {
                throw new RuntimeException(e);
            }
        } catch( Error e ) {
            try {
                //noinspection unchecked
                throw (E) e;
            } catch( ClassCastException e1 ) {
                throw e;
            }
        }
    }
}
