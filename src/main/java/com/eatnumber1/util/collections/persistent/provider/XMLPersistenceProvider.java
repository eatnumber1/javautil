package com.eatnumber1.util.collections.persistent.provider;

import com.eatnumber1.util.collections.persistent.PersistenceException;
import java.beans.XMLDecoder;
import java.beans.XMLEncoder;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import org.jetbrains.annotations.Nullable;

/**
 * @author Russell Harmon
 */
public class XMLPersistenceProvider<T> implements PersistenceProvider<T> {
    @Override
    public byte[] toBytes( @Nullable T object ) throws PersistenceException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        XMLEncoder encoder = new XMLEncoder(out);
        encoder.writeObject(object);
        encoder.close();
        return out.toByteArray();
    }

    @Override
    public T fromBytes( @Nullable byte[] bytes ) throws PersistenceException {
        XMLDecoder decoder = new XMLDecoder(new ByteArrayInputStream(bytes));
        //noinspection unchecked
        T object = (T) decoder.readObject();
        decoder.close();
        return object;
    }
}
