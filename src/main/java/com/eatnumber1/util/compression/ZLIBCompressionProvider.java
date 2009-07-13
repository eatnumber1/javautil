package com.eatnumber1.util.compression;

import java.io.InputStream;
import java.util.zip.Deflater;
import java.util.zip.DeflaterInputStream;
import java.util.zip.Inflater;
import java.util.zip.InflaterInputStream;
import org.jetbrains.annotations.Nullable;

/**
 * @author Russell Harmon
 */
public class ZLIBCompressionProvider extends AbstractCompressionProvider {
    @Nullable
    private Deflater deflater;

    @Nullable
    private Inflater inflater;

    @Nullable
    private Integer buflen;

    public ZLIBCompressionProvider() {
    }

    public ZLIBCompressionProvider( @Nullable Deflater deflater ) {
        this.deflater = deflater;
    }

    public ZLIBCompressionProvider( @Nullable Inflater inflater ) {
        this.inflater = inflater;
    }

    public ZLIBCompressionProvider( @Nullable Deflater deflater, @Nullable Inflater inflater ) {
        this.deflater = deflater;
        this.inflater = inflater;
    }

    public ZLIBCompressionProvider( @Nullable Deflater deflater, @Nullable Inflater inflater, @Nullable Integer buflen ) {
        this.deflater = deflater;
        this.inflater = inflater;
        this.buflen = buflen;
    }

    @Override
    public InputStream compress( @Nullable InputStream data ) throws CompressionException {
        if( data == null ) return null;
        if( deflater == null ) {
            return new DeflaterInputStream(data);
        } else if( buflen == null ) {
            return new DeflaterInputStream(data, deflater);
        } else {
            return new DeflaterInputStream(data, deflater, buflen);
        }
    }

    @Override
    public InputStream decompress( @Nullable InputStream data ) throws CompressionException {
        if( data == null ) return null;
        if( inflater == null ) {
            return new InflaterInputStream(data);
        } else if( buflen == null ) {
            return new InflaterInputStream(data, inflater);
        } else {
            return new InflaterInputStream(data, inflater, buflen);
        }
    }
}
