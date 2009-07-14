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

import com.eatnumber1.util.io.FileUtils;
import com.eatnumber1.util.numbers.AbstractMutableNumber;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import org.jetbrains.annotations.NotNull;

/**
 * @author Russell Harmon
 * @since Jul 14, 2009
 */
public abstract class AbstractFileBackedNumber extends AbstractMutableNumber implements FileBackedNumber {
    @NotNull
    private RandomAccessFile valueFile;

    @NotNull
    private FileChannel valueChannel;

    protected boolean isClosed;

    @NotNull
    private File file;

    protected AbstractFileBackedNumber( @NotNull File file ) throws IOException {
        this.file = file;
        FileUtils.forceCreateNewFile(file);
        valueFile = new RandomAccessFile(file, isMapped() ? "rw" : "rws");
        valueChannel = valueFile.getChannel();
    }

    public void longValue( long value ) {
        setValue(value);
    }

    public long longValue() {
        return getValue().longValue();
    }

    public void close() throws IOException {
        if( !isClosed ) {
            isClosed = true;
            flush();
            valueChannel.close();
            valueFile.close();
        }
    }

    public void flush() throws IOException {
    }

    @NotNull
    protected FileChannel getValueChannel() {
        return valueChannel;
    }

    @NotNull
    protected File getFile() {
        return file;
    }

    @Override
    protected void finalize() throws Throwable {
        try {
            close();
        } finally {
            super.finalize();
        }
    }

    protected abstract void setValue( @NotNull Number number );

    @NotNull
    protected abstract Number getValue();
}
