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

import com.eatnumber1.util.nio.MappedByteBufferUtils;
import java.io.File;
import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel.MapMode;
import org.jetbrains.annotations.NotNull;

/**
 * @author Russell Harmon
 * @since Jul 14, 2009
 */
public class FileBackedMappedLong extends AbstractFileBackedNumber implements FileBackedLong {
    public static final long serialVersionUID = FileBackedUnmappedLong.serialVersionUID + 1;
    public static final int SIZE = Long.SIZE / 8;

    @NotNull
    private MappedByteBuffer buf;

    public FileBackedMappedLong( @NotNull File file ) throws IOException {
        super(file);
        buf = getValueChannel().map(MapMode.READ_WRITE, 0, SIZE);
        setValue(0);
    }

    @NotNull
    protected Number getValue() {
        buf.position(0);
        return buf.getLong();
    }

    protected void setValue( @NotNull Number number ) {
        buf.position(0);
        buf.putLong(number.longValue());
    }

    public boolean isMapped() {
        return true;
    }

    @Override
    public void flush() throws IOException {
        buf.force();
        super.flush();
    }

    @Override
    public void close() throws IOException {
        MappedByteBufferUtils.unmap(buf);
    }
}
