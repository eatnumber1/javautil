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
import com.eatnumber1.util.collections.persistent.numbers.FileBackedMappedInteger;
import com.eatnumber1.util.collections.persistent.provider.PersistenceProvider;
import java.io.File;
import java.io.IOException;
import org.jetbrains.annotations.NotNull;

/**
 * @author Russell Harmon
 * @since Jul 14, 2009
 */
public class FileBackedMappedHashSet<T> extends FileBackedHashSet<T> {
    public FileBackedMappedHashSet() throws IOException {
    }

    public FileBackedMappedHashSet( @NotNull File directory, @NotNull PersistenceProvider<T> persistenceProvider ) throws IOException {
        super(directory, persistenceProvider);
    }

    @Override
    protected FileBackedInteger newFileBackedInteger( @NotNull File file ) throws IOException {
        return new FileBackedMappedInteger(file);
    }
}
