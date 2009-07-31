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

package com.eatnumber1.util.persistent.numbers;

import com.eatnumber1.util.io.FileUtils;
import com.eatnumber1.util.numbers.AbstractMutableNumber;
import com.eatnumber1.util.persistent.channel.FileChannelProvider;
import com.eatnumber1.util.persistent.channel.SimpleFileChannelProvider;
import java.io.File;
import java.io.IOException;
import org.jetbrains.annotations.NotNull;

/**
 * @author Russell Harmon
 * @since Jul 14, 2009
 */
public abstract class AbstractFileBackedNumber extends AbstractMutableNumber implements FileBackedNumber {
    protected boolean closed = false;

    @NotNull
    protected FileChannelProvider provider;

    @NotNull
    protected File file;

    protected AbstractFileBackedNumber( @NotNull File file, @NotNull FileChannelProvider provider ) throws IOException {
        initInternal(file, provider);
    }

    protected AbstractFileBackedNumber( @NotNull File file ) throws IOException {
        initInternal(file, new SimpleFileChannelProvider(file, isMapped() ? "rw" : "rws"));
    }

    private void initInternal( @NotNull File file, @NotNull FileChannelProvider provider ) throws IOException {
        this.provider = provider;
        this.file = file;
        if( !file.exists() ) FileUtils.createNewFile(file);
        openInternal(file);
        try {
            getValue();
        } catch( IOException e ) {
            setValue(0);
        }
    }

    protected void openInternal( @NotNull File file ) throws IOException {
        provider.open();
    }

    protected void closeInternal() throws IOException {
        provider.close();
    }

    @Override
    public void open() throws IOException {
        if( closed ) {
            initInternal(file, provider);
            closed = false;
        }
    }

    @Override
    public void longValue( long value ) {
        try {
            setValue(value);
        } catch( IOException e ) {
            throw new RuntimeException(e);
        }
    }

    protected void flushInternal() throws IOException {
        provider.flush();
    }

    @Override
    public void flush() throws IOException {
        if( !closed ) flushInternal();
    }

    @Override
    public long longValue() {
        try {
            return getValue().longValue();
        } catch( IOException e ) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void doubleValue( double value ) {
        try {
            setValue(value);
        } catch( IOException e ) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public double doubleValue() {
        try {
            return getValue().doubleValue();
        } catch( IOException e ) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void close() throws IOException {
        if( !closed ) {
            closed = true;
            flush();
            closeInternal();
            provider.close();
        }
    }

    public abstract int getSize();

    @Override
    protected void finalize() throws Throwable {
        try {
            close();
        } finally {
            super.finalize();
        }
    }

    protected abstract void setValue( @NotNull Number number ) throws IOException;

    @NotNull
    protected abstract Number getValue() throws IOException;
}
