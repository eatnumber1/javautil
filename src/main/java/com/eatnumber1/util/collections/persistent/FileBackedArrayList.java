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

import com.eatnumber1.util.collections.persistent.provider.PersistenceProvider;
import java.io.EOFException;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileChannel.MapMode;
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
public class FileBackedArrayList<T> extends AbstractList<T> implements FileBackedList<T>, RandomAccess {
    @NotNull
    private static final String LIST_FILENAME = "list";

    @NotNull
    private static final String DATA_FILENAME = "data";

    private static final int POINTER_SIZE = Long.SIZE / 8;
    private static final int OBJECT_LENGTH_SIZE = Integer.SIZE / 8;
    private static final int ELEMENT_SIZE = POINTER_SIZE + OBJECT_LENGTH_SIZE;
    private static final long SIZE_ELEMENT_POS = 0;
    private static final int LIST_START_POS = ELEMENT_SIZE;

    @NotNull
    private RandomAccessFile listFile, dataFile;

    @NotNull
    private FileChannel listChannel, dataChannel;

    @NotNull
    private MappedByteBuffer size_buffer;

    @Nullable
    private MappedByteBuffer list_buffer;

    @NotNull
    private IntBuffer size;

    @NotNull
    private ByteBuffer element_buf = ByteBuffer.allocate(ELEMENT_SIZE);

    @NotNull
    private PersistenceProvider<T> persistenceProvider;

    private long nextFree;

    private int elementsMapped;

    private long listTruncateSize, dataTruncateSize;

    private boolean closed;

    private class Element {
        private long start;
        private int size;

        private Element( long start, int size ) {
            this.start = start;
            this.size = size;
        }

        private Element( int index ) throws IOException {
            if( index < elementsMapped ) {
                list_buffer.position(getMappedElementOffset(index));
                start = list_buffer.getLong();
                size = list_buffer.getInt();
            } else {
                element_buf.position(0);
                readData(element_buf, listChannel, getUnmappedElementOffset(index));
                element_buf.position(0);
                start = element_buf.getLong();
                size = element_buf.getInt();
            }
        }

        private void write( int index ) throws IOException {
            if( index < elementsMapped ) {
                list_buffer.position(getMappedElementOffset(index));
                list_buffer.putLong(start).putInt(size);
            } else {
                element_buf.position(0);
                element_buf.putLong(start).putInt(size);
                element_buf.position(0);
                writeData(element_buf, listChannel, getUnmappedElementOffset(index));
            }
        }
    }

    public FileBackedArrayList( @NotNull File file, boolean map, @NotNull PersistenceProvider<T> persistenceProvider ) throws IOException {
        this.persistenceProvider = persistenceProvider;
        if( file.exists() ) {
            if( !file.isDirectory() ) throw new IOException(file + " is not a directory.");
        } else {
            if( !file.mkdir() ) throw new IOException("Unable to create container directory.");
        }
        File listFile = new File(file, LIST_FILENAME), dataFile = new File(file, DATA_FILENAME);
        boolean listFileExists = listFile.exists(), dataFileExists = dataFile.exists();
        if( listFileExists || dataFileExists ) {
            if( !listFileExists ) throw new IOException("List file is missing. The list is corrupt.");
            if( !dataFileExists ) throw new IOException("Data file is missing. The list is corrupt.");
        }
        createFile(listFile, dataFile);
        listTruncateSize = dataTruncateSize = 0;
        this.listFile = new RandomAccessFile(listFile, "rw");
        this.dataFile = new RandomAccessFile(dataFile, "rw");
        listChannel = this.listFile.getChannel();
        dataChannel = this.dataFile.getChannel();
        size_buffer = listChannel.map(MapMode.READ_WRITE, SIZE_ELEMENT_POS, ELEMENT_SIZE);
        size = size_buffer.asIntBuffer();
        if( !map ) elementsMapped = -1;
        remap();
        if( listFileExists && dataFileExists ) {
            Element last = new Element(size() - 1);
            nextFree = last.start + last.size;
        } else {
            nextFree = 0;
        }
    }

    private void createFile( @NotNull File listFile, @NotNull File dataFile ) throws IOException {
        if( listFile.createNewFile() && dataFile.createNewFile() ) {
            RandomAccessFile raf = new RandomAccessFile(listFile, "rws");
            FileChannel channel = raf.getChannel();
            ByteBuffer size_buffer = ByteBuffer.allocate(Integer.SIZE / 8);
            size_buffer.putInt(0);
            channel.write(size_buffer, SIZE_ELEMENT_POS);
            channel.close();
            raf.close();
        }
    }

    public void remap() throws IOException {
        if( isMapped() ) {
            elementsMapped = size();
            if( list_buffer != null ) list_buffer.force();
            list_buffer = null;
            truncate();
            list_buffer = listChannel.map(MapMode.READ_WRITE, LIST_START_POS, elementsMapped * ELEMENT_SIZE);
            assert list_buffer != null;
        }
    }

    public void load() {
        if( isMapped() ) {
            assert list_buffer != null;
            list_buffer.load();
        }
    }

    private void truncate() throws IOException {
        if( listTruncateSize > 0 ) {
            try {
                listChannel.truncate(listChannel.size() - listTruncateSize);
                listTruncateSize = 0;
            } catch( IOException e ) {
                // Ignore
            }
        }
        if( dataTruncateSize > 0 ) {
            //noinspection CaughtExceptionImmediatelyRethrown
            try {
                dataChannel.truncate(dataChannel.size() - dataTruncateSize);
                dataTruncateSize = 0;
            } catch( IOException e ) {
                throw e;
            }
        }
    }

    public boolean isMapped() {
        return elementsMapped != -1;
    }

    public void setMapped( boolean map ) throws IOException {
        if( map ) {
            elementsMapped = 0;
            remap();
        } else {
            if( elementsMapped != -1 ) {
                assert list_buffer != null;
                list_buffer.force();
                list_buffer = null;
            }
            elementsMapped = -1;
        }
    }

    public void close() throws IOException {
        if( !closed ) {
            closed = true;
            try {
                flush();
                // Throw these out because mapped memory doesn't become invalid till it's gc'ed.
                //noinspection ConstantConditions
                size = null;
                //noinspection ConstantConditions
                size_buffer = list_buffer = null;
            } finally {
                try {
                    listChannel.close();
                } finally {
                    try {
                        dataChannel.close();
                    } finally {
                        try {
                            listFile.close();
                        } finally {
                            dataFile.close();
                        }
                    }
                }
            }
        }
    }

    public void flush() throws IOException {
        size_buffer.force();
        if( isMapped() ) {
            assert list_buffer != null;
            list_buffer.force();
        }
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

    private int getUnmappedElementOffset( int index ) {
        return LIST_START_POS + getMappedElementOffset(index);
    }

    private int getMappedElementOffset( int index ) {
        return index * ELEMENT_SIZE;
    }

    private void readData( @NotNull ByteBuffer dst, @NotNull FileChannel channel, long start ) throws IOException {
        int bytesRead = channel.position(start).read(dst);
        if( bytesRead == -1 ) {
            throw new EOFException("Premature end of file.");
        } else if( bytesRead != dst.capacity() ) {
            throw new IOException("Unable to read element fully.");
        }
    }

    private void writeData( @NotNull ByteBuffer src, @NotNull FileChannel channel, long start ) throws IOException {
        int bytesWritten = channel.position(start).write(src);
        if( bytesWritten != src.capacity() ) {
            throw new IOException("Unable to write element fully.");
        }
    }

    @Nullable
    private T readObject( int index ) throws IOException, PersistenceException {
        return readObject(new Element(index));
    }

    @Nullable
    private T readObject( @NotNull Element element ) throws IOException, PersistenceException {
        ByteBuffer buf = ByteBuffer.allocate(element.size);
        readData(buf, dataChannel, element.start);
        return persistenceProvider.fromBytes(buf.array());
    }

    /*private void moveElements( int index, int newIndex ) throws IOException {
        if( index < size() ) {
            Element element = new Element(index);
            moveElements(index + 1, newIndex + 1);
            element.write(newIndex);
        }
    }*/

    private void moveLeft( int index, int newIndex, long newStart ) throws IOException {
        int idx = index, newIdx = newIndex;
        long start = newStart;
        int size = size();
        while( idx != size ) {
            Element element = new Element(idx++);
            new Element(start, element.size).write(newIdx++);
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
        long indexStart = index == size ? 0 : new Element(index).start;
        long dataSpaceNeeded = newStart - indexStart;
        assert newStart >= dataSpaceNeeded;
        int elementSpaceNeeded = newIndex - index;
        boolean setNextFree = false;
        for( int i = size - 1; i >= newIndex - 1; i-- ) {
            // Read the element
            Element diskElement = new Element(i);

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

    /*private void moveRight( int index, int newIndex, long newStart ) throws IOException {
        int size = size();
        int idx = index, newIdx = newIndex;
        long start = newStart;
        Element e = new Element(index);
        ByteBuffer object = ByteBuffer.allocate(e.size);
        readData(object, dataChannel, e.start);
        List<ByteBuffer> objects = new LinkedList<ByteBuffer>(Arrays.asList(object));
        while( idx != size ) {
            List<ByteBuffer> newObjects = new LinkedList<ByteBuffer>();
            while( !objects.isEmpty() ) {
                for( ByteBuffer obj : objects ) {
                    long freedSpace = 0;
                    int nextIdx = idx + 1;
                    @Nullable
                    Element nextE = null;
                    if( nextIdx != size ) nextE = new Element(nextIdx);
                    long needSpace = nextE == null ? 0 : nextE.size;
                    if( freedSpace >= needSpace ) {
                        object = ByteBuffer.allocate(e.size);
                        readData(object, dataChannel, e.start);
                        newObjects.add(object);
                        idx++;
                        if( idx != size ) e = new Element(idx);
                    } else {
                        while( freedSpace < needSpace ) {
                            object = ByteBuffer.allocate(e.size);
                            readData(object, dataChannel, e.start);
                            newObjects.add(object);
                            freedSpace += object.capacity();
                            idx++;
                            if( idx == size ) {
                                break;
                            }
                            e = new Element(idx);
                        }
                    }
                    obj.position(0);
                    writeData(obj, dataChannel, start);
                    int objSize = obj.capacity();
                    new Element(start, objSize).write(newIdx++);
                    start += objSize;
                }
                objects.clear();
                objects.addAll(newObjects);
            }
        }
        nextFree = start;
        dataTruncateSize -= Math.max(newStart - e.start, 0);
        listTruncateSize -= Math.max(newIndex - index, 0);
    }*/


    /*private void moveData( @NotNull Element element, int index, long newStart ) throws IOException {
        if( index < size() ) {
            ByteBuffer object = ByteBuffer.allocate(element.size);
            readData(object, dataChannel, element.start);
            int newIndex = index + 1;
            if( newIndex < size() ) moveData(new Element(newIndex), newIndex, newStart + element.size);
            object.position(0);
            writeData(object, dataChannel, newStart);
            new Element(newStart, element.size).write(index);
        } else {
            nextFree = element.size + element.start;
        }
    }

    private void moveData( int index, long newStart ) throws IOException {
        if( index < size() ) moveData(new Element(index), index, newStart);
    }*/

    private void writeObject( int index, @Nullable T object ) throws PersistenceException, IOException {
        ByteBuffer buf = ByteBuffer.wrap(persistenceProvider.toBytes(object));
        Element element;
        int size = size();
        if( index == size ) {
            element = new Element(nextFree, buf.capacity());
        } else {
            element = new Element(index);
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
        size.position(0);
        return size.get();
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
                Element ele = new Element(index);
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
            Element element = new Element(index);
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

    private void setSize( int size ) {
        this.size.position(0);
        this.size.put(size);
    }

    @Override
    public void clear() {
        listTruncateSize += ELEMENT_SIZE * size();
        setSize(0);
        try {
            dataTruncateSize += dataChannel.size();
            truncate();
        } catch( IOException e ) {
            throw new RuntimeException(e);
        }
        nextFree = 0;
    }
}
