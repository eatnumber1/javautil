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
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import org.apache.commons.io.FileUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author Russell Harmon
 * @since Jul 14, 2009
 */
public class FileBackedHashSet<T> extends FileBackedSize implements FileBackedSet<T> {
    @NotNull
    private static final String NULL_FILE = "NULL";

    @NotNull
    private static final String DATADIR_FILENAME = "data";

    @NotNull
    private PersistenceProvider<T> persistenceProvider;

    @NotNull
    private File dataDirectory, nullFile;

    public FileBackedHashSet( @NotNull File directory, boolean map, @NotNull PersistenceProvider<T> persistenceProvider ) throws IOException {
        super(directory, map);
        dataDirectory = new File(directory, DATADIR_FILENAME);
        com.eatnumber1.util.io.FileUtils.forceMkdir(dataDirectory);
        this.persistenceProvider = persistenceProvider;
        this.nullFile = new File(dataDirectory, NULL_FILE);
    }

    public boolean contains( Object o ) {
        return com.eatnumber1.util.io.FileUtils.contains(dataDirectory, String.valueOf(o.hashCode()));
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

    public Object[] toArray() {
        List<T> list = new ArrayList<T>(size());
        for( T obj : this ) {
            list.add(obj);
        }
        return list.toArray();
    }

    public <T> T[] toArray( T[] a ) {
        T[] array = a;
        int size = size();
        if( array.length < size ) {
            //noinspection unchecked
            array = (T[]) Array.newInstance(a.getClass(), size);
        }
        int count = 0;
        //noinspection unchecked
        Iterator<T> iter = (Iterator<T>) iterator();
        while( count != size ) {
            array[count++] = iter.next();
        }
        while( count != array.length ) {
            array[count++] = null;
        }
        return array;
    }

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
                    com.eatnumber1.util.io.FileUtils.forceCreateNewFile(nullFile);
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
                    com.eatnumber1.util.io.FileUtils.forceCreateNewFile(file);
                    FileUtils.writeByteArrayToFile(file, bytes);
                    setSize(size() + 1);
                    return true;
                }
            }
        } catch( IOException e ) {
            throw new RuntimeException(e);
        }
    }

    public boolean remove( Object o ) {
        File file = new File(dataDirectory, String.valueOf(o.hashCode()));
        if( com.eatnumber1.util.io.FileUtils.contains(dataDirectory, file) ) {
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

    public boolean containsAll( Collection<?> c ) {
        List<String> files = Arrays.asList(dataDirectory.list());
        List<String> hashes = new ArrayList<String>(c.size());
        for( Object o : c ) {
            hashes.add(String.valueOf(o.hashCode()));
        }
        return files.containsAll(hashes);
    }

    public boolean addAll( Collection<? extends T> c ) {
        boolean changed = false;
        for( T obj : c ) {
            changed |= add(obj);
        }
        return changed;
    }

    public boolean removeAll( Collection<?> c ) {
        final List<String> hashes = new ArrayList<String>(c.size());
        for( Object o : c ) {
            hashes.add(String.valueOf(o.hashCode()));
        }
        boolean changed = false;
        int filesRemoved = 0;
        for( File f : dataDirectory.listFiles(new FilenameFilter() {
            public boolean accept( File dir, String name ) {
                return hashes.contains(name);
            }
        }) ) {
            try {
                FileUtils.forceDelete(f);
                filesRemoved++;
            } catch( IOException e ) {
                throw new RuntimeException(e);
            }
            changed = true;
        }
        if( changed ) setSize(size() - filesRemoved);
        return changed;
    }

    public boolean retainAll( Collection<?> c ) {
        final List<String> hashes = new ArrayList<String>(c.size());
        for( Object o : c ) {
            hashes.add(String.valueOf(o.hashCode()));
        }
        boolean changed = false;
        for( File f : dataDirectory.listFiles(new FilenameFilter() {
            public boolean accept( File dir, String name ) {
                return !hashes.contains(name);
            }
        }) ) {
            try {
                FileUtils.forceDelete(f);
            } catch( IOException e ) {
                throw new RuntimeException(e);
            }
            changed = true;
        }
        if( changed ) setSize(dataDirectory.list().length);
        return changed;
    }

    public void clear() {
        setSize(0);
        try {
            FileUtils.cleanDirectory(dataDirectory);
        } catch( IOException e ) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean equals( Object o ) {
        if( this == o ) return true;
        if( !( o instanceof FileBackedHashSet ) ) return false;

        FileBackedHashSet that = (FileBackedHashSet) o;

        return dataDirectory.equals(that.dataDirectory) && getDirectory().equals(that.getDirectory());
    }

    @Override
    public int hashCode() {
        int result = dataDirectory.hashCode();
        result = 31 * result + nullFile.hashCode();
        return result;
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
}
