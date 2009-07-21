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
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import org.apache.commons.io.IOUtils;
import org.jetbrains.annotations.NotNull;

/**
 * @author Russell Harmon
 * @since Jul 13, 2007
 */
public abstract class AbstractCompressionProvider implements CompressionProvider {
    @NotNull
    @Override
    public byte[] compress( @NotNull byte[] data ) throws CompressionException {
        InputStream compressedData = compress(new ByteArrayInputStream(data));
        try {
            return IOUtils.toByteArray(compressedData);
        } catch( IOException e ) {
            throw new CompressionException(e);
        }
    }

    @NotNull
    @Override
    public byte[] decompress( @NotNull byte[] data ) throws CompressionException {
        InputStream decompressedData = decompress(new ByteArrayInputStream(data));
        try {
            return IOUtils.toByteArray(decompressedData);
        } catch( IOException e ) {
            throw new CompressionException(e);
        }
    }
}