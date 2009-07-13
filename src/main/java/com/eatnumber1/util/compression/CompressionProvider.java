package com.eatnumber1.util.compression;

import java.io.InputStream;
import org.jetbrains.annotations.Nullable;

/**
 * @author Russell Harmon
 */
public interface CompressionProvider {
    @Nullable
    InputStream compress( @Nullable InputStream data ) throws CompressionException;

    @Nullable
    byte[] compress( @Nullable byte[] data ) throws CompressionException;

    @Nullable
    InputStream decompress( @Nullable InputStream data ) throws CompressionException;

    @Nullable
    byte[] decompress( @Nullable byte[] data ) throws CompressionException;
}
