package com.eatnumber1.util.compression;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import org.apache.commons.io.IOUtils;
import org.jetbrains.annotations.Nullable;

/**
 * @author Russell Harmon
 */
public abstract class AbstractCompressionProvider implements CompressionProvider {
    @Override
    public byte[] compress( @Nullable byte[] data ) throws CompressionException {
        InputStream compressedData = compress(new ByteArrayInputStream(data));
        if( compressedData == null ) return null;
        try {
            return IOUtils.toByteArray(compressedData);
        } catch( IOException e ) {
            throw new CompressionException(e);
        }
    }

    @Override
    public byte[] decompress( @Nullable byte[] data ) throws CompressionException {
        InputStream decompressedData = decompress(new ByteArrayInputStream(data));
        if( decompressedData == null ) return null;
        try {
            return IOUtils.toByteArray(decompressedData);
        } catch( IOException e ) {
            throw new CompressionException(e);
        }
    }
}