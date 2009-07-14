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

import java.io.File;
import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel.MapMode;
import org.jetbrains.annotations.NotNull;

/**
 * @author Russell Harmon
 * @since Jul 14, 2009
 */
public class FileBackedMappedInteger extends AbstractFileBackedNumber implements FileBackedInteger {
    public static final long serialVersionUID = FileBackedUnmappedInteger.serialVersionUID + 1;
    public static final int SIZE = Integer.SIZE / 8;

    @NotNull
    private MappedByteBuffer buf;

    public FileBackedMappedInteger( @NotNull File file ) throws IOException {
        super(file);
        buf = getValueChannel().map(MapMode.READ_WRITE, 0, SIZE);
    }

    @NotNull
    protected Number getValue() {
        buf.position(0);
        return buf.getInt();
    }

    protected void setValue( @NotNull Number number ) {
        buf.position(0);
        buf.putInt(number.intValue());
    }

    public boolean isMapped() {
        return true;
    }

    @Override
    public void flush() throws IOException {
        buf.force();
        super.flush();
    }
}
