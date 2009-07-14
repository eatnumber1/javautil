/*
 * Copyright 2007 Russell Harmon
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

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
 * @since Jul 13, 2007
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
