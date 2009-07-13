package com.eatnumber1.util.collections.persistent.provider;

import com.eatnumber1.util.collections.persistent.PersistenceException;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import org.jetbrains.annotations.Nullable;

/**
 * @author Russell Harmon
 */
public class SerializablePersistenceProvider<T extends Serializable> implements PersistenceProvider<T> {
    @Nullable
    public byte[] toBytes( @Nullable T object ) throws PersistenceException {
        if( object == null ) return null;
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        ObjectOutputStream out;
        try {
            ( out = new ObjectOutputStream(bytes) ).writeObject(object);
        } catch( IOException e ) {
            throw new PersistenceException(e);
        }
        try {
            out.close();
        } catch( IOException e ) {
            // Do nothing
        }
        return bytes.toByteArray();
    }

    @Nullable
    public T fromBytes( @Nullable byte[] bytes ) throws PersistenceException {
        if( bytes == null ) return null;
        ObjectInputStream in;
        T object;
        try {
            in = new ObjectInputStream(new ByteArrayInputStream(bytes));
            //noinspection unchecked
            object = (T) in.readObject();
        } catch( IOException e ) {
            throw new PersistenceException(e);
        } catch( ClassNotFoundException e ) {
            throw new PersistenceException(e);
        }
        try {
            in.close();
        } catch( IOException e ) {
            // Do nothing
        }
        return object;
    }
}
