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

import com.eatnumber1.util.io.IOUtils;
import com.eatnumber1.util.persistent.channel.FileChannelProvider;
import com.eatnumber1.util.persistent.channel.FileChannelVisitor;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import org.jetbrains.annotations.NotNull;

/**
 * @author Russell Harmon
 * @since Jul 27, 2009
 */
public abstract class AbstractFileBackedUnmappedNumber extends AbstractFileBackedNumber {
    protected AbstractFileBackedUnmappedNumber( @NotNull File file ) throws IOException {
        super(file);
    }

    protected AbstractFileBackedUnmappedNumber( @NotNull File file, @NotNull FileChannelProvider provider ) throws IOException {
        super(file, provider);
    }

    public boolean isMapped() {
        return false;
    }

    @NotNull
    protected Number getValue() throws IOException {
        return provider.visitValueChannel(new FileChannelVisitor<Number>() {
            @Override
            public Number visit( @NotNull FileChannel channel ) throws IOException {
                ByteBuffer buf = ByteBuffer.allocate(getSize());
                channel.position(0);
                IOUtils.read(channel, buf, getSize());
                buf.position(0);
                return getValue(buf);
            }
        });
    }

    @NotNull
    protected abstract Number getValue( @NotNull ByteBuffer buf );

    protected abstract void setValue( @NotNull ByteBuffer buf, @NotNull Number number );

    protected void setValue( @NotNull final Number number ) throws IOException {
        provider.visitValueChannel(new FileChannelVisitor<Void>() {
            @Override
            public Void visit( @NotNull FileChannel channel ) throws IOException {
                ByteBuffer buf = ByteBuffer.allocate(getSize());
                setValue(buf, number);
                buf.position(0);
                try {
                    channel.position(0);
                    IOUtils.write(channel, buf, getSize());
                } catch( IOException e ) {
                    throw new RuntimeException(e);
                }
                return null;
            }
        });
    }
}
