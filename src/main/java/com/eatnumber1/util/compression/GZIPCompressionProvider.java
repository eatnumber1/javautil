package com.eatnumber1.util.compression;

import com.eatnumber1.util.io.OutputStreamAdapter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;
import org.jetbrains.annotations.Nullable;

/**
 * @author Russell Harmon
 */
public class GZIPCompressionProvider extends AbstractCompressionProvider {
    @Nullable
    private Integer buflen;

    public GZIPCompressionProvider( @Nullable Integer buflen ) {
        this.buflen = buflen;
    }

    public GZIPCompressionProvider() {

    }

    @Override
    public InputStream compress( @Nullable final InputStream data ) throws CompressionException {
        if( data == null ) return null;
        OutputStreamAdapter adapter = new OutputStreamAdapter();
        final OutputStream out;
        try {
            out = buflen == null ? new GZIPOutputStream(adapter) : new GZIPOutputStream(adapter, buflen);
        } catch( IOException e ) {
            throw new RuntimeException(e);
        }
        final InputStream in = adapter.asInputStream();
        return new InputStream() {
            @Override
            public int read() throws IOException {
                assert data != null;
                out.write(data.read());
                return in.read();
            }
        };
    }

    @Override
    public InputStream decompress( @Nullable InputStream data ) throws CompressionException {
        try {
            return buflen == null ? new GZIPInputStream(data) : new GZIPInputStream(data, buflen);
        } catch( IOException e ) {
            throw new CompressionException(e);
        }
    }
}
