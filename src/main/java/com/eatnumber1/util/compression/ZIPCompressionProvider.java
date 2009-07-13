package com.eatnumber1.util.compression;

import com.eatnumber1.util.io.OutputStreamAdapter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;
import org.jetbrains.annotations.Nullable;

/**
 * @author Russell Harmon
 */
public class ZIPCompressionProvider extends AbstractCompressionProvider {
    @Override
    public InputStream compress( @Nullable final InputStream data ) throws CompressionException {
        if( data == null ) return null;
        OutputStreamAdapter adapter = new OutputStreamAdapter();
        final OutputStream out = new ZipOutputStream(adapter);
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
        return new ZipInputStream(data);
    }
}
