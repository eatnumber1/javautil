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
import com.eatnumber1.util.collections.persistent.channel.SimpleChannelProvider;
import com.eatnumber1.util.io.FileUtils;
import com.eatnumber1.util.numbers.AbstractMutableNumber;
import java.io.File;
import java.io.IOException;
import org.jetbrains.annotations.NotNull;

/**
 * @author Russell Harmon
 * @since Jul 14, 2009
 */
public abstract class AbstractFileBackedNumber extends AbstractMutableNumber implements FileBackedNumber {
    protected boolean isClosed;

    @NotNull
    protected ChannelProvider provider;

    protected AbstractFileBackedNumber( @NotNull File file, @NotNull ChannelProvider provider ) throws IOException {
        initInternal(file, provider);
    }

    protected AbstractFileBackedNumber( @NotNull File file ) throws IOException {
        initInternal(file, new SimpleChannelProvider(file, isMapped() ? "rw" : "rws"));
    }

    private void initInternal( @NotNull File file, @NotNull ChannelProvider provider ) throws IOException {
        this.provider = provider;
        if( !file.exists() ) FileUtils.createNewFile(file);
        init(file);
        try {
            getValue();
        } catch( IOException e ) {
            setValue(0);
        }
    }

    protected void init( @NotNull File file ) throws IOException {
    }

    @Override
    public void longValue( long value ) {
        try {
            setValue(value);
        } catch( IOException e ) {
            throw new RuntimeException(e);
        }
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
        if( !isClosed ) {
            isClosed = true;
            flush();
        }
    }

    public abstract int getSize();

    @Override
    public void flush() throws IOException {
    }

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
