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

import com.eatnumber1.util.collections.persistent.provider.CompressedPersistenceProvider;
import com.eatnumber1.util.collections.persistent.provider.SerializablePersistenceProvider;
import com.eatnumber1.util.compression.ZLIBCompressionProvider;
import org.jetbrains.annotations.NotNull;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author Russell Harmon
 * @since Jul 14, 2009
 */
public class FileBackedHashSetTest extends AbstractFileBackedSetTest {
    @NotNull
    private FileBackedHashSet<String> set;

    @NotNull
    protected FileBackedCollection<String> newCollection2() throws Throwable {
        createDirectory();
        set = new FileBackedMappedHashSet<String>(tempFile, new CompressedPersistenceProvider<String>(new SerializablePersistenceProvider<String>(), new ZLIBCompressionProvider()));
        return set;
    }

    @Test
    public void addSame() {
        Assert.assertTrue(set.add(SHORT_STRING));
        for( int i = 0; i < 10; i++ ) {
            Assert.assertFalse(set.add(SHORT_STRING));
        }
        Assert.assertTrue(set.add(LONG_STRING));
        for( int i = 0; i < 10; i++ ) {
            Assert.assertFalse(set.add(LONG_STRING));
        }
        Assert.assertEquals(2, set.size());
    }
}
