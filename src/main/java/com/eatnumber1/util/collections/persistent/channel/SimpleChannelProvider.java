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

package com.eatnumber1.util.collections.persistent.channel;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import org.jetbrains.annotations.NotNull;

/**
 * @author Russell Harmon
 * @since Jul 27, 2009
 */
public class SimpleChannelProvider extends AbstractChannelProvider {
    @NotNull
    private RandomAccessFile file;

    @NotNull
    private FileChannel channel;

    public SimpleChannelProvider( @NotNull File file, @NotNull String permissions ) throws FileNotFoundException {
        super(file, permissions);
        this.file = new RandomAccessFile(file, permissions);
        channel = this.file.getChannel();
    }

    @Override
    public <T> T visitValueChannel( @NotNull ChannelVisitor<T> visitor ) throws IOException {
        return visitor.visit(channel);
    }

    @Override
    public void close() throws IOException {
        channel.close();
        file.close();
    }

    @Override
    protected void finalize() throws Throwable {
        try {
            close();
        } finally {
            super.finalize();
        }
    }
}
