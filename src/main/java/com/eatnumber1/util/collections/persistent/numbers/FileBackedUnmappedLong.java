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

package com.eatnumber1.util.collections.persistent.numbers;

import com.eatnumber1.util.collections.persistent.channel.ChannelProvider;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import org.jetbrains.annotations.NotNull;

/**
 * @author Russell Harmon
 * @since Jul 14, 2009
 */
public class FileBackedUnmappedLong extends AbstractFileBackedUnmappedNumber implements FileBackedLong {
    public static final int SIZE = Long.SIZE / 8;

    public FileBackedUnmappedLong( @NotNull File file ) throws IOException {
        super(file);
    }

    public FileBackedUnmappedLong( @NotNull File file, @NotNull ChannelProvider provider ) throws IOException {
        super(file, provider);
    }

    @Override
    public int getSize() {
        return SIZE;
    }

    @NotNull
    @Override
    protected Number getValue( @NotNull ByteBuffer buf ) {
        return buf.getLong();
    }

    @Override
    protected void setValue( @NotNull ByteBuffer buf, @NotNull Number number ) {
        buf.putLong(number.longValue());
    }

    @Override
    public String toString() {
        return String.valueOf(longValue());
    }
}
