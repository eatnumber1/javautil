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

package com.eatnumber1.util.compression;

import com.eatnumber1.util.compat.Override;
import com.eatnumber1.util.io.OutputStreamAdapter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;
import org.jetbrains.annotations.Nullable;

/**
 * @author Russell Harmon
 * @since Jul 13, 2007
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
