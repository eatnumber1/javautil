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

import com.eatnumber1.util.nio.MappedByteBufferUtils;
import com.eatnumber1.util.persistent.channel.FileChannelProvider;
import com.eatnumber1.util.persistent.channel.FileChannelVisitor;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileChannel.MapMode;
import org.jetbrains.annotations.NotNull;

/**
 * @author Russell Harmon
 * @since Jul 27, 2009
 */
public abstract class AbstractFileBackedMappedNumber extends AbstractFileBackedNumber {
    @NotNull
    private MappedByteBuffer buf;

    protected AbstractFileBackedMappedNumber( @NotNull File file ) throws IOException {
        super(file);
    }

    protected AbstractFileBackedMappedNumber( @NotNull File file, @NotNull FileChannelProvider provider ) throws IOException {
        super(file, provider);
    }

    @Override
    protected void openInternal( @NotNull File file ) throws IOException {
        super.openInternal(file);
        provider.visitValueChannel(new FileChannelVisitor<Void>() {
            @Override
            public Void visit( @NotNull FileChannel channel ) throws IOException {
                buf = channel.map(MapMode.READ_WRITE, 0, getSize());
                return null;
            }
        });
    }

    @Override
    public boolean isMapped() {
        return true;
    }

    @Override
    public void flushInternal() throws IOException {
        buf.force();
        super.flushInternal();
    }

    @Override
    public void closeInternal() throws IOException {
        MappedByteBufferUtils.unmap(buf);
        super.closeInternal();
    }

    @Override
    protected void setValue( @NotNull Number number ) throws IOException {
        buf.position(0);
        setValue(buf, number);
    }

    @NotNull
    protected abstract Number getValue( @NotNull ByteBuffer buf );

    protected abstract void setValue( @NotNull ByteBuffer buf, @NotNull Number number );

    @NotNull
    @Override
    protected Number getValue() throws IOException {
        buf.position(0);
        return getValue(buf);
    }
}
