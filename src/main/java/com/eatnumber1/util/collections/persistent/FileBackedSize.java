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

package com.eatnumber1.util.collections.persistent;

import com.eatnumber1.util.concurrent.container.SynchronizedContainer;
import com.eatnumber1.util.container.Container;
import com.eatnumber1.util.container.ContainerAction;
import com.eatnumber1.util.container.ContainerException;
import com.eatnumber1.util.io.IOUtils;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileChannel.MapMode;
import org.apache.commons.io.FileUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author Russell Harmon
 * @since Jul 14, 2009
 */
public class FileBackedSize implements FileBacked {
    @NotNull
    private static final String SIZE_FILENAME = "size";

    @NotNull
    private RandomAccessFile size_file;

    @NotNull
    private Container<FileChannel> size_channel = new SynchronizedContainer<FileChannel>();

    @NotNull
    private ByteBuffer size;

    @Nullable
    private MappedByteBuffer mapped_size_buffer;

    private boolean isClosed, isMapped;

    @NotNull
    private File directory;

    public FileBackedSize( @NotNull File directory, boolean map ) throws IOException {
        isMapped = map;
        File sizeFile = new File(directory, SIZE_FILENAME);
        if( directory.exists() ) {
            if( !directory.isDirectory() ) throw new IllegalArgumentException(directory + " must be a directory.");
        } else {
            FileUtils.forceMkdir(directory);
        }
        this.directory = directory;
        com.eatnumber1.util.io.FileUtils.forceCreateNewFile(sizeFile);
        String mode = map ? "rw" : "rws";
        size_file = new RandomAccessFile(sizeFile, mode);
        size_channel.setDelegate(size_file.getChannel());
        if( map ) {
            mapped_size_buffer = size_channel.getDelegate().map(MapMode.READ_WRITE, 0, Integer.SIZE / 8);
            assert mapped_size_buffer != null;
            size = mapped_size_buffer;
        } else {
            size = ByteBuffer.allocate(Integer.SIZE / 8);
            size_channel.getDelegate().force(true);
        }
    }

    @NotNull
    public File getDirectory() {
        return directory;
    }

    public int size() {
        if( !isMapped ) {
            try {
                size_channel.doAction(new ContainerAction<FileChannel, Void>() {
                    public Void doAction( FileChannel param ) throws ContainerException {
                        try {
                            param.position(0);
                            IOUtils.read(param, size, size.capacity());
                        } catch( IOException e ) {
                            throw new ContainerException(e);
                        }
                        return null;
                    }
                });
            } catch( ContainerException e ) {
                throw new RuntimeException(e);
            }
        }
        size.position(0);
        return size.getInt();
    }

    protected void setSize( int size ) {
        assert size >= 0;
        this.size.position(0);
        this.size.putInt(size);
    }

    @SuppressWarnings({ "ThrowFromFinallyBlock" })
    public void close() throws IOException {
        if( !isClosed ) {
            isClosed = true;
            flush();
            try {
                size_channel.doAction(new ContainerAction<FileChannel, Void>() {
                    public Void doAction( FileChannel param ) throws ContainerException {
                        try {
                            param.close();
                        } catch( IOException e ) {
                            throw new ContainerException(e);
                        }
                        //noinspection ReturnInsideFinallyBlock
                        return null;
                    }
                });
            } catch( ContainerException e ) {
                Throwable cause = e.getCause();
                if( cause instanceof IOException ) throw (IOException) cause;
                if( cause instanceof Error ) throw (Error) cause;
                throw new RuntimeException(e);
            }
            size_file.close();
        }
    }

    public void flush() throws IOException {
        if( mapped_size_buffer != null ) mapped_size_buffer.force();
    }

    @Override
    protected void finalize() throws Throwable {
        try {
            close();
        } finally {
            super.finalize();
        }
    }

    public boolean isMapped() {
        return isMapped;
    }

    public boolean isEmpty() {
        return size() == 0;
    }
}
