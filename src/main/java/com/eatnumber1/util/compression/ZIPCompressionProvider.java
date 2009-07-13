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

import com.eatnumber1.util.io.OutputStreamAdapter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;
import org.jetbrains.annotations.Nullable;

/**
 * @author Russell Harmon
 * @since Jul 13, 2009
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
