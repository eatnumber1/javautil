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

import com.eatnumber1.util.io.FileUtils;
import com.eatnumber1.util.persistent.channel.FileChannelProvider;
import com.eatnumber1.util.persistent.channel.FileChannelProviderFactory;
import com.eatnumber1.util.persistent.channel.FileChannelVisitor;
import com.eatnumber1.util.persistent.channel.SimpleFileChannelProvider;
import com.eatnumber1.util.persistent.numbers.FileBackedInteger;
import com.eatnumber1.util.persistent.numbers.FileBackedUnmappedInteger;
import com.eatnumber1.util.persistent.provider.PersistenceProvider;
import java.io.EOFException;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.AbstractList;
import java.util.RandomAccess;
import net.jcip.annotations.NotThreadSafe;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author Russell Harmon
 * @since Jul 13, 2007
 */
// TODO: Optionally cache the list
@NotThreadSafe
public class FileBackedUnmappedArrayList<T> extends AbstractList<T> implements FileBackedList<T>, RandomAccess {
    @NotNull
    protected static final String LIST_FILENAME = "list", DATA_FILENAME = "data", SIZE_FILENAME = "size";

    protected static final int POINTER_SIZE = Long.SIZE / 8;
    protected static final int OBJECT_LENGTH_SIZE = Integer.SIZE / 8;
    protected static final int ELEMENT_SIZE = POINTER_SIZE + OBJECT_LENGTH_SIZE;

    @NotNull
    protected FileChannelProvider elementChannel, dataChannel;

    @NotNull
    protected FileBackedInteger size;

    @NotNull
    protected ByteBuffer element_buf = ByteBuffer.allocate(ELEMENT_SIZE);

    @NotNull
    protected PersistenceProvider<T> persistenceProvider;

    @NotNull
    private FileChannelProviderFactory factory;

    protected long nextFree;

    protected long listTruncateSize, dataTruncateSize;

    protected boolean closed = true;

    @NotNull
    private File directory;

    protected abstract class Element {
        protected long start;
        protected int size;

        public Element( long start, int size ) {
            this.start = start;
            this.size = size;
        }

        protected Element() {
        }

        public abstract void write( int index ) throws IOException;
    }

    protected class UnmappedElement extends Element {
        public UnmappedElement( long start, int size ) {
            super(start, size);
        }

        public UnmappedElement( int index ) throws IOException {
            element_buf.position(0);
            readData(element_buf, elementChannel, getElementOffset(index));
            element_buf.position(0);
            start = element_buf.getLong();
            size = element_buf.getInt();
        }

        public void write( int index ) throws IOException {
            element_buf.position(0);
            element_buf.putLong(start).putInt(size);
            element_buf.position(0);
            writeData(element_buf, elementChannel, getElementOffset(index));
        }
    }

    @NotNull
    protected Element newElement( long start, int size ) {
        return new UnmappedElement(start, size);
    }

    @NotNull
    protected Element newElement( int index ) throws IOException {
        return new UnmappedElement(index);
    }

    public FileBackedUnmappedArrayList( @NotNull File file, @NotNull PersistenceProvider<T> persistenceProvider ) throws IOException {
        this(file, persistenceProvider, new FileChannelProviderFactory() {
            @NotNull
            @Override
            public FileChannelProvider create( @NotNull File file ) throws IOException {
                return new SimpleFileChannelProvider(file, "rw");
            }
        });
    }

    public FileBackedUnmappedArrayList( @NotNull File directory, @NotNull PersistenceProvider<T> persistenceProvider, @NotNull FileChannelProviderFactory factory ) throws IOException {
        this.persistenceProvider = persistenceProvider;
        this.factory = factory;
        this.directory = directory;
        open();
    }

    @Override
    public void open() throws IOException {
        if( closed ) {
            if( directory.exists() ) {
                if( !directory.isDirectory() ) throw new IOException(directory + " is not a directory.");
            } else {
                FileUtils.mkdir(directory);
            }
            File listFile = new File(directory, LIST_FILENAME), dataFile = new File(directory, DATA_FILENAME);
            boolean listFileExists = listFile.exists(), dataFileExists = dataFile.exists();
            if( listFileExists || dataFileExists ) {
                if( !listFileExists ) throw new IOException("List file is missing. The list is corrupt.");
                if( !dataFileExists ) throw new IOException("Data file is missing. The list is corrupt.");
            } else {
                FileUtils.createNewFile(listFile);
                FileUtils.createNewFile(dataFile);
            }
            listTruncateSize = dataTruncateSize = 0;
            elementChannel = factory.create(listFile);
            dataChannel = factory.create(dataFile);
            File sizeFile = new File(directory, SIZE_FILENAME);
            size = newSizeInteger(sizeFile, factory.create(sizeFile));
            int size = size();
            if( size != 0 && listFileExists && dataFileExists ) {
                Element last = newElement(size - 1);
                nextFree = last.start + last.size;
            } else {
                nextFree = 0;
            }
            closed = false;
        }
    }

    @NotNull
    protected FileBackedInteger newSizeInteger( @NotNull File sizeFile, @NotNull FileChannelProvider channelProvider ) throws IOException {
        return new FileBackedUnmappedInteger(sizeFile, channelProvider);
    }

    protected void truncate() throws IOException {
        if( listTruncateSize > 0 ) {
            elementChannel.visitValueChannel(new FileChannelVisitor<Void>() {
                @Override
                public Void visit( @NotNull FileChannel channel ) throws IOException {
                    try {
                        channel.truncate(channel.size() - listTruncateSize);
                        listTruncateSize = 0;
                    } catch( IOException e ) {
                        // Ignore
                    }
                    return null;
                }
            });
        }
        if( dataTruncateSize > 0 ) {
            dataChannel.visitValueChannel(new FileChannelVisitor<Void>() {
                @Override
                public Void visit( @NotNull FileChannel channel ) throws IOException {
                    channel.truncate(channel.size() - dataTruncateSize);
                    dataTruncateSize = 0;
                    return null;
                }
            });
        }
    }

    public boolean isMapped() {
        return false;
    }

    public void close() throws IOException {
        if( !closed ) {
            flush();
            size.close();
            elementChannel.close();
            dataChannel.close();
            closed = true;
        }
    }

    public void flush() throws IOException {
        size.flush();
        truncate();
    }

    @Override
    protected void finalize() throws Throwable {
        try {
            close();
        } finally {
            super.finalize();
        }
    }

    protected int getElementOffset( int index ) {
        return index * ELEMENT_SIZE;
    }

    private void readData( @NotNull final ByteBuffer dst, @NotNull FileChannelProvider channel, final long start ) throws IOException {
        channel.visitValueChannel(new FileChannelVisitor<Void>() {
            @Override
            public Void visit( @NotNull FileChannel channel ) throws IOException {
                int bytesRead = channel.position(start).read(dst);
                if( bytesRead == -1 ) {
                    throw new EOFException("Premature end of file.");
                } else if( bytesRead != dst.capacity() ) {
                    throw new IOException("Unable to read element fully.");
                }
                return null;
            }
        });
    }

    private void writeData( @NotNull final ByteBuffer src, @NotNull FileChannelProvider channel, final long start ) throws IOException {
        channel.visitValueChannel(new FileChannelVisitor<Void>() {
            @Override
            public Void visit( @NotNull FileChannel channel ) throws IOException {
                int bytesWritten = channel.position(start).write(src);
                if( bytesWritten != src.capacity() ) {
                    throw new IOException("Unable to write element fully.");
                }
                return null;
            }
        });
    }

    @Nullable
    private T readObject( int index ) throws IOException, PersistenceException {
        return readObject(newElement(index));
    }

    @Nullable
    private T readObject( @NotNull Element element ) throws IOException, PersistenceException {
        ByteBuffer buf = ByteBuffer.allocate(element.size);
        readData(buf, dataChannel, element.start);
        return persistenceProvider.fromBytes(buf.array());
    }

    private void moveLeft( int index, int newIndex, long newStart ) throws IOException {
        int idx = index, newIdx = newIndex;
        long start = newStart;
        int size = size();
        while( idx != size ) {
            Element element = newElement(idx++);
            newElement(start, element.size).write(newIdx++);
            ByteBuffer object = ByteBuffer.allocate(element.size);
            readData(object, dataChannel, element.start);
            object.position(0);
            writeData(object, dataChannel, start);
            start += element.size;
        }
        dataTruncateSize += start - nextFree;
        listTruncateSize += index - newIndex;
        nextFree = start;
    }

    private void moveRight( int index, int newIndex, long newStart ) throws IOException {
        int size = size();
        assert size >= newIndex && newIndex >= index;
        long indexStart = index == size ? 0 : newElement(index).start;
        long dataSpaceNeeded = newStart - indexStart;
        assert newStart >= dataSpaceNeeded;
        int elementSpaceNeeded = newIndex - index;
        boolean setNextFree = false;
        for( int i = size - 1; i >= newIndex - 1; i-- ) {
            // Read the element
            Element diskElement = newElement(i);

            // Move the data
            ByteBuffer object = ByteBuffer.allocate(diskElement.size);
            readData(object, dataChannel, diskElement.start);
            long diskElementStart = diskElement.start + dataSpaceNeeded;
            object.position(0);
            writeData(object, dataChannel, diskElementStart);

            // Write the element
            diskElement.start = diskElementStart;
            diskElement.write(i + elementSpaceNeeded);

            if( !setNextFree ) {
                nextFree = diskElement.start + diskElement.size;
                setNextFree = true;
            }
        }
        dataTruncateSize -= Math.max(dataSpaceNeeded, 0);
        listTruncateSize -= Math.max(elementSpaceNeeded, 0);
    }

    private void writeObject( int index, @Nullable T object ) throws PersistenceException, IOException {
        ByteBuffer buf = ByteBuffer.wrap(persistenceProvider.toBytes(object));
        Element element;
        int size = size();
        if( index == size ) {
            element = newElement(nextFree, buf.capacity());
        } else {
            element = newElement(index);
        }
        int newSize = buf.capacity();
        if( element.size != newSize ) {
            int newIndex = index + 1;
            if( newSize < element.size ) {
                moveLeft(newIndex, newIndex, element.start + newSize);
            } else {
                moveRight(newIndex, newIndex, element.start + newSize);
            }
            element.size = newSize;
        }
        element.write(index);
        writeData(buf, dataChannel, element.start);
        if( index == size ) nextFree += element.size;
    }

    public T get( int index ) {
        if( index < 0 || index >= size() ) throw new IndexOutOfBoundsException();
        try {
            return readObject(index);
        } catch( IOException e ) {
            throw new RuntimeException(e);
        } catch( PersistenceException e ) {
            throw new RuntimeException(e);
        }
    }

    public int size() {
        return size.intValue();
    }

    @Override
    public T set( int index, T element ) {
        if( index < 0 || index >= size() ) throw new IndexOutOfBoundsException();
        try {
            T object = readObject(index);
            writeObject(index, element);
            return object;
        } catch( PersistenceException e ) {
            throw new RuntimeException(e);
        } catch( IOException e ) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void add( int index, T element ) {
        int size = size();
        if( index < 0 || index > size ) throw new IndexOutOfBoundsException();
        try {
            if( index != size ) {
                ByteBuffer buf = ByteBuffer.wrap(persistenceProvider.toBytes(element));
                Element ele = newElement(index);
                moveRight(index, index + 1, ele.start + buf.capacity());
                writeData(buf, dataChannel, ele.start);
                ele.size = buf.capacity();
                ele.write(index);
            } else {
                writeObject(index, element);
            }
            setSize(size + 1);
        } catch( IOException e ) {
            throw new RuntimeException(e);
        } catch( PersistenceException e ) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public T remove( int index ) {
        int size = size();
        if( index < 0 || index >= size ) throw new IndexOutOfBoundsException();
        try {
            Element element = newElement(index);
            T object = readObject(element);
            if( index != size - 1 ) {
                moveLeft(index + 1, index, element.start);
            }
            setSize(size - 1);
            truncate();
            return object;
        } catch( IOException e ) {
            throw new RuntimeException(e);
        } catch( PersistenceException e ) {
            throw new RuntimeException(e);
        }
    }

    protected void setSize( int size ) {
        this.size.intValue(size);
    }

    @Override
    public void clear() {
        listTruncateSize += ELEMENT_SIZE * size();
        setSize(0);
        try {
            dataTruncateSize += dataChannel.visitValueChannel(new FileChannelVisitor<Long>() {
                @Override
                public Long visit( @NotNull FileChannel channel ) throws IOException {
                    return channel.size();
                }
            });
            truncate();
        } catch( IOException e ) {
            throw new RuntimeException(e);
        }
        nextFree = 0;
    }

    @NotNull
    @Override
    public File getStorageFile() {
        return directory;
    }
}
