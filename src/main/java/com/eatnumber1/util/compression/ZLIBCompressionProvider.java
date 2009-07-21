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

import com.eatnumber1.util.compat.DeflaterInputStream;
import com.eatnumber1.util.compat.Override;
import java.io.InputStream;
import java.util.zip.Deflater;
import java.util.zip.Inflater;
import java.util.zip.InflaterInputStream;
import net.jcip.annotations.NotThreadSafe;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author Russell Harmon
 * @since Jul 13, 2007
 */
@NotThreadSafe
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

    @NotNull
    @Override
    public InputStream compress( @NotNull InputStream data ) throws CompressionException {
        if( deflater == null ) {
            return new DeflaterInputStream(data);
        } else if( buflen == null ) {
            return new DeflaterInputStream(data, deflater);
        } else {
            return new DeflaterInputStream(data, deflater, buflen);
        }
    }

    @NotNull
    @Override
    public InputStream decompress( @NotNull InputStream data ) throws CompressionException {
        if( inflater == null ) {
            return new InflaterInputStream(data);
        } else if( buflen == null ) {
            return new InflaterInputStream(data, inflater);
        } else {
            return new InflaterInputStream(data, inflater, buflen);
        }
    }
}
