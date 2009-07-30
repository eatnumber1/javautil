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

package com.eatnumber1.util.io;

import java.io.Closeable;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;
import org.jetbrains.annotations.NotNull;

/**
 * @author Russell Harmon
 * @since Jul 14, 2009
 */
public class IOUtils extends org.apache.commons.io.IOUtils {
    protected IOUtils() {
    }

    public static void write( @NotNull WritableByteChannel channel, @NotNull ByteBuffer src, int expected ) throws IOException {
        if( channel.write(src) != expected ) throw new IOException("Did not write expected amount of data");
    }

    public static void read( @NotNull ReadableByteChannel channel, @NotNull ByteBuffer dst, int expected ) throws IOException {
        if( channel.read(dst) != expected ) throw new IOException("Did not read expected amount of data");
    }

    public static void closeQuietly( @NotNull Closeable closeable ) {
        try {
            closeable.close();
        } catch( IOException e ) {
            // Ignore
        }
    }
}
