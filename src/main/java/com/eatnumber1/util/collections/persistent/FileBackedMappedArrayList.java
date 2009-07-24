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

import com.eatnumber1.util.collections.persistent.numbers.FileBackedInteger;
import com.eatnumber1.util.collections.persistent.provider.PersistenceProvider;
import java.io.File;
import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel.MapMode;
import org.jetbrains.annotations.NotNull;

/**
 * @author Russell Harmon
 * @since Jul 24, 2009
 * @deprecated This class is broken.
 */
@Deprecated
public class FileBackedMappedArrayList<T> extends FileBackedUnmappedArrayList<T> {
    private class MappedElement extends Element {
        public MappedElement( long start, int size ) {
            super(start, size);
        }

        public MappedElement( int index ) throws IOException {
            elements.position(getMappedElementOffset(index));
            start = elements.getLong();
            size = elements.getInt();
        }

        @Override
        public void write( int index ) throws IOException {
        }
    }

    @NotNull
    private MappedByteBuffer elements;

    private int elementsMapped;

    public FileBackedMappedArrayList( @NotNull File file, @NotNull PersistenceProvider<T> tPersistenceProvider ) throws IOException {
        super(file, tPersistenceProvider);
        initList();
        remap();
    }

    @Override
    public boolean isMapped() {
        return true;
    }

    @Override
    public void flush() throws IOException {
        super.flush();
        elements.force();
    }

    public void remap() throws IOException {
        elementsMapped = size();
        elements.force();
        truncate();
        initList();
    }

    private void initList() throws IOException {
        elements = elementChannel.map(MapMode.READ_WRITE, LIST_START_POS, elementsMapped * ELEMENT_SIZE);
    }

    public void load() {
        elements.load();
    }

    @NotNull
    @Override
    protected Element newElement( long start, int size ) {
        return new MappedElement(start, size);
    }

    @NotNull
    @Override
    protected Element newElement( int index ) throws IOException {
        if( index < elementsMapped ) {
            return new MappedElement(index);
        } else {
            return new UnmappedElement(index);
        }
    }

    @NotNull
    @Override
    protected FileBackedInteger newSizeInteger( @NotNull File sizeFile ) throws IOException {
        return super.newSizeInteger(sizeFile);
    }

    private int getMappedElementOffset( int index ) {
        return index * ELEMENT_SIZE;
    }
}
