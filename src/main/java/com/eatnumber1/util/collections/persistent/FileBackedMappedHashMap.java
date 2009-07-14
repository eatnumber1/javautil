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
import java.io.IOException;
import org.jetbrains.annotations.NotNull;

/**
 * @author Russell Harmon
 * @since Jul 14, 2009
 */
public class FileBackedMappedHashMap<K, V> extends FileBackedHashMap<K, V> {
    public FileBackedMappedHashMap() throws IOException {
    }

    public FileBackedMappedHashMap( @NotNull File directory, @NotNull PersistenceProvider<Entry<K, V>> persistenceProvider ) throws IOException {
        super(directory, persistenceProvider);
    }

    @Override
    protected FileBackedHashSet<Entry<K, V>> newEntrySet( @NotNull File directory, @NotNull PersistenceProvider<Entry<K, V>> persistenceProvider ) throws IOException {
        return new FileBackedMappedHashSet<Entry<K, V>>(directory, persistenceProvider);
    }
}
