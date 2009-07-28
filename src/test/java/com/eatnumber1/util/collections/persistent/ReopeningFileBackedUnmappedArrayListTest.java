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

import com.eatnumber1.util.collections.persistent.channel.ReopeningChannelProviderFactory;
import com.eatnumber1.util.collections.persistent.provider.SerializablePersistenceProvider;
import com.eatnumber1.util.io.FileUtils;
import java.io.File;
import java.io.IOException;
import java.util.List;
import org.apache.commons.collections.list.AbstractTestList;

/**
 * @author Russell Harmon
 * @since Jul 27, 2009
 */
public class ReopeningFileBackedUnmappedArrayListTest extends AbstractTestList {
    public ReopeningFileBackedUnmappedArrayListTest( String testName ) {
        super(testName);
    }

    @Override
    public List makeEmptyList() {
        try {
            File tempFile = FileUtils.createTempDirectory(FileBackedUnmappedArrayListTest.class.getSimpleName());
            FileUtils.forceDeleteOnExit(tempFile);
            return new FileBackedUnmappedArrayList<String>(tempFile, new SerializablePersistenceProvider<String>(), new ReopeningChannelProviderFactory("rw"));
        } catch( IOException e ) {
            throw new RuntimeException(e);
        }
    }
}
