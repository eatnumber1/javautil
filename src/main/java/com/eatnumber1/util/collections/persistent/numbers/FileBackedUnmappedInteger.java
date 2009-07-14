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

import com.eatnumber1.util.io.IOUtils;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import org.jetbrains.annotations.NotNull;

/**
 * @author Russell Harmon
 * @since Jul 14, 2009
 */
public class FileBackedUnmappedInteger extends AbstractFileBackedNumber implements FileBackedInteger {
    public static final long serialVersionUID = -9036741450133104409L;
    public static final int SIZE = Integer.SIZE / 8;

    public FileBackedUnmappedInteger( @NotNull File file ) throws IOException {
        super(file);
        getValueChannel().force(true);
    }

    @NotNull
    protected Number getValue() {
        FileChannel channel = getValueChannel();
        ByteBuffer buf = ByteBuffer.allocate(SIZE);
        try {
            channel.position(0);
            IOUtils.read(channel, buf, SIZE);
            return buf.getInt();
        } catch( IOException e ) {
            throw new RuntimeException(e);
        }
    }

    protected void setValue( @NotNull Number number ) {
        FileChannel channel = getValueChannel();
        ByteBuffer buf = ByteBuffer.allocate(SIZE);
        buf.putInt(number.intValue());
        buf.position(0);
        try {
            channel.position(0);
            IOUtils.write(channel, buf, SIZE);
        } catch( IOException e ) {
            throw new RuntimeException(e);
        }
    }

    public boolean isMapped() {
        return false;
    }
}
