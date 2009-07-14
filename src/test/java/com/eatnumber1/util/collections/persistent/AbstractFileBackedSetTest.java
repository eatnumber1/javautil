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

import com.eatnumber1.util.collections.AbstractSetTest;
import java.io.File;
import java.io.IOException;
import java.util.Collection;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jetbrains.annotations.NotNull;
import org.junit.After;

/**
 * @author Russell Harmon
 * @since Jul 14, 2009
 */
public abstract class AbstractFileBackedSetTest extends AbstractSetTest {
    @NotNull
    private Log log = LogFactory.getLog(AbstractFileBackedCollectionTest.class);

    @NotNull
    protected FileBackedCollection<String> collection;

    @NotNull
    protected File tempFile;

    public void createDirectory() throws IOException {
        tempFile = File.createTempFile("FileBackedArrayListTest", "");
        if( !tempFile.delete() ) {
            throw new RuntimeException("Unable to delete temporary file.");
        } else {
            if( !tempFile.mkdir() ) throw new RuntimeException("Unable to create temporary directory.");
        }
        /*Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                try {
                    FileUtils.deleteDirectory(tempFile);
                } catch( IOException e ) {
                    log.warn(e.getMessage());
                }
            }
        });*/
        //noinspection ResultOfMethodCallIgnored
        tempFile.mkdir();
    }

    @After
    public void close() throws IOException {
        //noinspection ConstantConditions
        if( collection != null ) collection.close();
    }

    @NotNull
    protected Collection<String> newCollection() throws Throwable {
        try {
            close();
        } catch( IOException e ) {
            throw new RuntimeException(e);
        }
        collection = newCollection2();
        return collection;
    }

    @NotNull
    protected abstract FileBackedCollection<String> newCollection2() throws Throwable;
}
