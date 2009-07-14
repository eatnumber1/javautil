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
import com.eatnumber1.util.collections.persistent.numbers.FileBackedUnmappedInteger;
import com.eatnumber1.util.collections.persistent.provider.PersistenceProvider;
import com.eatnumber1.util.collections.persistent.provider.XMLPersistenceProvider;
import com.eatnumber1.util.io.FileUtils;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.AbstractSet;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author Russell Harmon
 * @since Jul 14, 2009
 */
public class FileBackedHashSet<T> extends AbstractSet<T> implements FileBackedSet<T> {
    @NotNull
    private static final String NULL_FILE = "NULL";

    @NotNull
    private static final String DATADIR_FILENAME = "data";

    @NotNull
    private PersistenceProvider<T> persistenceProvider;

    @NotNull
    private File nullFile;

    @NotNull
    protected File dataDirectory;

    @NotNull
    private FileBackedInteger size;

    public FileBackedHashSet() throws IOException {
        File directory = FileUtils.createTempDirectory("FileBackedHashSet");
        FileUtils.forceDeleteOnExit(directory);
        init(directory, new XMLPersistenceProvider<T>());
    }

    public FileBackedHashSet( @NotNull File directory, @NotNull PersistenceProvider<T> persistenceProvider ) throws IOException {
        init(directory, persistenceProvider);
    }

    private void init( @NotNull File directory, @NotNull PersistenceProvider<T> persistenceProvider ) throws IOException {
        size = newFileBackedInteger(new File(directory, "size"));
        dataDirectory = new File(directory, DATADIR_FILENAME);
        FileUtils.forceMkdir(dataDirectory);
        this.persistenceProvider = persistenceProvider;
        this.nullFile = new File(dataDirectory, NULL_FILE);
    }

    protected FileBackedInteger newFileBackedInteger( @NotNull File file ) throws IOException {
        return new FileBackedUnmappedInteger(file);
    }

    @Override
    public boolean contains( Object o ) {
        return FileUtils.contains(dataDirectory, String.valueOf(o.hashCode()));
    }

    @NotNull
    File getDataDirectory() {
        return dataDirectory;
    }

    @Nullable
    T get( @NotNull String hash ) throws IOException, PersistenceException {
        return persistenceProvider.fromBytes(FileUtils.readFileToByteArray(new File(dataDirectory, hash)));
    }

    boolean removeInternal( @Nullable String hash ) {
        File file = hash == null ? nullFile : new File(dataDirectory, hash);
        if( FileUtils.contains(dataDirectory, file) ) {
            try {
                FileUtils.forceDelete(file);
            } catch( IOException e ) {
                throw new RuntimeException(e);
            }
            setSize(size() - 1);
            return true;
        } else {
            return false;
        }
    }

    public Iterator<T> iterator() {
        return new Iterator<T>() {
            @NotNull
            private Iterator<File> listing = Arrays.asList(dataDirectory.listFiles()).iterator();

            public boolean hasNext() {
                return listing.hasNext();
            }

            public T next() {
                try {
                    return persistenceProvider.fromBytes(FileUtils.readFileToByteArray(listing.next()));
                } catch( IOException e ) {
                    throw new RuntimeException(e);
                } catch( PersistenceException e ) {
                    throw new RuntimeException(e);
                }
            }

            public void remove() {
                try {
                    FileUtils.forceDelete(listing.next());
                    setSize(size() - 1);
                } catch( IOException e ) {
                    throw new RuntimeException(e);
                }
            }
        };
    }

    @Override
    public boolean add( T o ) {
        @Nullable
        byte[] bytes;
        try {
            bytes = persistenceProvider.toBytes(o);
        } catch( PersistenceException e ) {
            throw new RuntimeException(e);
        }
        try {
            if( bytes == null ) {
                if( !nullFile.exists() ) {
                    FileUtils.forceCreateNewFile(nullFile);
                    setSize(size() + 1);
                    return true;
                } else {
                    return false;
                }
            } else {
                File file = new File(dataDirectory, String.valueOf(o.hashCode()));
                if( file.exists() ) {
                    return false;
                } else {
                    FileUtils.forceCreateNewFile(file);
                    FileUtils.writeByteArrayToFile(file, bytes);
                    setSize(size() + 1);
                    return true;
                }
            }
        } catch( IOException e ) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean remove( Object o ) {
        return removeInternal(o == null ? null : String.valueOf(o.hashCode()));
    }

    @Override
    public boolean containsAll( Collection<?> c ) {
        List<String> files = Arrays.asList(dataDirectory.list());
        List<String> hashes = new ArrayList<String>(c.size());
        for( Object o : c ) {
            hashes.add(o == null ? nullFile.getName() : String.valueOf(o.hashCode()));
        }
        return files.containsAll(hashes);
    }

    @Override
    public boolean addAll( Collection<? extends T> c ) {
        boolean changed = false;
        for( T obj : c ) {
            changed |= add(obj);
        }
        return changed;
    }

    @Override
    public boolean removeAll( Collection<?> c ) {
        final List<String> hashes = new ArrayList<String>(c.size());
        for( Object o : c ) {
            hashes.add(String.valueOf(o.hashCode()));
        }
        boolean changed = false;
        int filesRemoved = 0;
        try {
            for( File f : dataDirectory.listFiles(new FilenameFilter() {
                public boolean accept( File dir, String name ) {
                    return hashes.contains(name);
                }
            }) ) {
                FileUtils.forceDelete(f);
                filesRemoved++;
                changed = true;
            }
            if( hashes.contains(null) ) FileUtils.forceDelete(nullFile);
        } catch( IOException e ) {
            throw new RuntimeException(e);
        }
        if( changed ) setSize(size() - filesRemoved);
        return changed;
    }

    @Override
    public boolean retainAll( Collection<?> c ) {
        final List<String> hashes = new ArrayList<String>(c.size());
        for( Object o : c ) {
            hashes.add(String.valueOf(o.hashCode()));
        }
        boolean changed = false;
        try {
            for( File f : dataDirectory.listFiles(new FilenameFilter() {
                public boolean accept( File dir, String name ) {
                    return !hashes.contains(name);
                }
            }) ) {
                FileUtils.forceDelete(f);
                changed = true;
            }
            if( !hashes.contains(null) ) FileUtils.forceDelete(nullFile);
        } catch( IOException e ) {
            throw new RuntimeException(e);
        }
        if( changed ) setSize(dataDirectory.list().length);
        return changed;
    }

    @Override
    public void clear() {
        setSize(0);
        try {
            FileUtils.cleanDirectory(dataDirectory);
        } catch( IOException e ) {
            throw new RuntimeException(e);
        }
    }

    public int size() {
        return size.intValue();
    }

    public boolean isMapped() {
        return false;
    }

    public void close() throws IOException {
        size.close();
    }

    public void flush() throws IOException {
        size.flush();
    }

    @Override
    public String toString() {
        StringBuilder buf = new StringBuilder();
        buf.append("[");

        Iterator<T> i = iterator();
        boolean hasNext = i.hasNext();
        while( hasNext ) {
            T o = i.next();
            buf.append(o == this ? "(this Collection)" : String.valueOf(o));
            hasNext = i.hasNext();
            if( hasNext )
                buf.append(", ");
        }

        buf.append("]");
        return buf.toString();
    }

    protected void setSize( int size ) {
        this.size.intValue(size);
    }
}
