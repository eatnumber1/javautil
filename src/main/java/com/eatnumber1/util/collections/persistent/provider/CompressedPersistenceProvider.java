package com.eatnumber1.util.collections.persistent.provider;

import com.eatnumber1.util.collections.persistent.PersistenceException;
import com.eatnumber1.util.compression.CompressionException;
import com.eatnumber1.util.compression.CompressionProvider;
import com.eatnumber1.util.facade.SimpleFacade;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author Russell Harmon
 */
public class CompressedPersistenceProvider<T> extends SimpleFacade<PersistenceProvider<T>> implements PersistenceProvider<T> {
    @NotNull
    private CompressionProvider compressionProvider;

    public CompressedPersistenceProvider( @NotNull PersistenceProvider<T> delegate, @NotNull CompressionProvider compressionProvider ) {
        super(delegate);
        this.compressionProvider = compressionProvider;
    }

    @Override
    public byte[] toBytes( @Nullable T object ) throws PersistenceException {
        try {
            return compressionProvider.compress(getDelegate().toBytes(object));
        } catch( CompressionException e ) {
            throw new PersistenceException(e);
        }
    }

    @Override
    public T fromBytes( @Nullable byte[] bytes ) throws PersistenceException {
        try {
            return getDelegate().fromBytes(compressionProvider.decompress(bytes));
        } catch( CompressionException e ) {
            throw new PersistenceException(e);
        }
    }
}
