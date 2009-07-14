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
import com.eatnumber1.util.collections.persistent.provider.XMLPersistenceProvider;
import com.eatnumber1.util.io.FileUtils;
import java.io.File;
import java.io.IOException;
import java.util.AbstractMap;
import java.util.Set;
import org.apache.commons.collections15.keyvalue.DefaultMapEntry;
import org.jetbrains.annotations.NotNull;

/**
 * @author Russell Harmon
 * @since Jul 14, 2009
 */
public class FileBackedHashMap<K, V> extends AbstractMap<K, V> {
    @NotNull
    private FileBackedHashSet<Entry<K, V>> entrySet;

    public FileBackedHashMap() throws IOException {
        File directory = FileUtils.createTempDirectory("FileBackedHashMap");
        FileUtils.forceDeleteOnExit(directory);
        init(directory, new XMLPersistenceProvider<Entry<K, V>>());
    }

    public FileBackedHashMap( @NotNull File directory, @NotNull PersistenceProvider<Entry<K, V>> persistenceProvider ) throws IOException {
        init(directory, persistenceProvider);
    }

    private void init( @NotNull File directory, @NotNull PersistenceProvider<Entry<K, V>> persistenceProvider ) throws IOException {
        entrySet = newEntrySet(directory, persistenceProvider);
    }

    protected FileBackedHashSet<Entry<K, V>> newEntrySet( @NotNull File directory, @NotNull PersistenceProvider<Entry<K, V>> persistenceProvider ) throws IOException {
        return new FileBackedHashSet<Entry<K, V>>(directory, persistenceProvider);
    }

    public Set<Entry<K, V>> entrySet() {
        return entrySet;
    }

    @Override
    public V put( K k, V v ) {
        V oldValue = null;
        for( Entry<K, V> entry : entrySet ) {
            if( entry.getKey().equals(k) ) {
                oldValue = entry.getValue();
            }
        }
        entrySet.add(new DefaultMapEntry<K, V>(k, v));
        return oldValue;
    }
}
