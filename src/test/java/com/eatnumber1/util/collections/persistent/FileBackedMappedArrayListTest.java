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

import com.eatnumber1.util.collections.persistent.provider.SerializablePersistenceProvider;
import com.eatnumber1.util.io.FileUtils;
import java.io.File;
import java.io.IOException;
import java.util.List;
import org.apache.commons.collections.list.AbstractTestList;

/**
 * @author Russell Harmon
 * @since Jul 13, 2007
 */
public class FileBackedMappedArrayListTest extends AbstractTestList {
    public FileBackedMappedArrayListTest( String s ) {
        super(s);
    }

    public List makeEmptyList() {
        try {
            File tempFile = FileUtils.createTempDirectory(FileBackedMappedArrayListTest.class.getSimpleName());
            FileUtils.forceDeleteOnExit(tempFile);
            return new FileBackedArrayList<String>(tempFile, true, new SerializablePersistenceProvider<String>());
        } catch( IOException e ) {
            throw new RuntimeException(e);
        }
    }

    public void testRemap() throws IOException {
        FileBackedArrayList list = (FileBackedArrayList) makeFullList();
        list.remap();
        list.load();
    }
}
