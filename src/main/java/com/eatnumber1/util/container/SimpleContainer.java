package com.eatnumber1.util.container;

import com.eatnumber1.util.facade.SimpleFacade;
import org.jetbrains.annotations.NotNull;

/**
 * @author Russell Harmon
 */
public class SimpleContainer<V> extends SimpleFacade<V> implements Container<V> {
    public SimpleContainer( V delegate ) {
        super(delegate);
    }

    @Override
    public <T, E extends Throwable> T doAction( @NotNull ContainerAction<V, T, E> action ) throws E {
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
