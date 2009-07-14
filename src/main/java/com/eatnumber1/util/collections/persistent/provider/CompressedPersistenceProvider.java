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
import com.eatnumber1.util.compression.CompressionException;
import com.eatnumber1.util.compression.CompressionProvider;
import com.eatnumber1.util.facade.SimpleFacade;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author Russell Harmon
 * @since Jul 13, 2007
 */
public class CompressedPersistenceProvider<T> extends SimpleFacade<PersistenceProvider<T>> implements PersistenceProvider<T> {
    @NotNull
    private CompressionProvider compressionProvider;

    public CompressedPersistenceProvider( @NotNull PersistenceProvider<T> delegate, @NotNull CompressionProvider compressionProvider ) {
        super(delegate);
        this.compressionProvider = compressionProvider;
    }

    @Override
    @Nullable
    public byte[] toBytes( @Nullable T object ) throws PersistenceException {
        try {
            return compressionProvider.compress(getDelegate().toBytes(object));
        } catch( CompressionException e ) {
            throw new PersistenceException(e);
        }
    }

    @Override
    @Nullable
    public T fromBytes( @Nullable byte[] bytes ) throws PersistenceException {
        try {
            return getDelegate().fromBytes(compressionProvider.decompress(bytes));
        } catch( CompressionException e ) {
            throw new PersistenceException(e);
        }
    }
}
