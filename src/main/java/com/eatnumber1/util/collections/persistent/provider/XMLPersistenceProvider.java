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
import com.eatnumber1.util.compat.Override;
import java.beans.XMLDecoder;
import java.beans.XMLEncoder;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author Russell Harmon
 * @since Jul 13, 2007
 */
public class XMLPersistenceProvider<T> implements PersistenceProvider<T> {
    @NotNull
    @Override
    public byte[] toBytes( @Nullable T object ) throws PersistenceException {
        if( object == null ) return new byte[0];
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        XMLEncoder encoder = new XMLEncoder(out);
        encoder.writeObject(object);
        encoder.close();
        return out.toByteArray();
    }

    @Nullable
    @Override
    public T fromBytes( @NotNull byte[] bytes ) throws PersistenceException {
        if( bytes.length == 0 ) return null;
        XMLDecoder decoder = new XMLDecoder(new ByteArrayInputStream(bytes));
        //noinspection unchecked
        T object = (T) decoder.readObject();
        decoder.close();
        return object;
    }
}
