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
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang.math.RandomUtils;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * @author Russell Harmon
 * @since Jul 13, 2007
 */
public class FileBackedArrayListTest {
    private static Logger log = Logger.getLogger(FileBackedArrayListTest.class);

    private static final int ELEMENT_COUNT = 500;
    private static final boolean RANDOM_STRINGS = true;
    private static final int MAX_STRING_LENGTH = 300;
    private static final String SHORT_STRING = "a";
    private static final String LONG_STRING = "HELLOHELLOHELLOHELLOHELLOHELLOHELLOHELLOHELLOHELLOHELLOHELLOHELLOHELLOHELLOHELLOHELLOHELLOHELLOHELLOHELLOHELLOHELLOHELLOHELLOHELLOHELLOHELLOHELLOHELLOHELLOHELLOHELLOHELLOHELLOHELLOHELLOHELLOHELLOHELLOHELLOHELLOHELLOHELLOHELLOHELLOHELLO";
    private FileBackedArrayList<String> list;

    private List<String> strings;

    static {
        BasicConfigurator.configure();
    }

    @Before
    public void setUp() throws IOException {
        final File tempFile = File.createTempFile("FileBackedArrayListTest", "");
        if( !tempFile.delete() ) {
            throw new RuntimeException("Unable to delete temporary file.");
        } else {
            if( !tempFile.mkdir() ) throw new RuntimeException("Unable to create temporary directory.");
        }
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                try {
                    FileUtils.deleteDirectory(tempFile);
                } catch( IOException e ) {
                    log.warn(e.getMessage());
                }
            }
        });
        //noinspection ResultOfMethodCallIgnored
        tempFile.mkdir();
        list = new FileBackedArrayList<String>(tempFile, true, new CompressedPersistenceProvider<String>(new SerializablePersistenceProvider<String>(), new ZLIBCompressionProvider()));
        strings = new ArrayList<String>(ELEMENT_COUNT);
        if( RANDOM_STRINGS ) {
            for( int i = 0; i < ELEMENT_COUNT; i++ ) {
                strings.add(RandomStringUtils.randomAlphanumeric(RandomUtils.nextInt(MAX_STRING_LENGTH)));
            }
        } else {
            for( int i = 0; i < ELEMENT_COUNT; i++ ) {
                StringBuilder sb = new StringBuilder();
                for( int d = 0; d < MAX_STRING_LENGTH; d++ ) {
                    sb.append('a');
                }
                strings.add(sb.toString());
            }
        }
        strings = Collections.unmodifiableList(strings);
    }

    @After
    public void tearDown() throws IOException {
        if( list != null ) list.close();
    }

    @Test
    public void add() {
        for( int i = 0; i < ELEMENT_COUNT; i++ ) {
            list.add(strings.get(i));
        }
        assertFull();
    }

    @Test
    public void addToEnd() {
        for( int i = 0; i < ELEMENT_COUNT; i++ ) {
            list.add(i, strings.get(i));
        }
        assertFull();
    }

    @Test
    public void addToBeginning() {
        for( int i = 0; i < ELEMENT_COUNT; i++ ) {
            list.add(0, strings.get(i));
        }
        assertFullInReverse();
    }

    @Test
    public void remove() {
        addAll();
        removeInternal();
    }

    private void removeInternal() {
        for( int i = 0; i < ELEMENT_COUNT; i++ ) {
            Assert.assertTrue(list.remove(strings.get(i)));
        }
        assertEmpty();
        Assert.assertFalse(list.remove(strings.get(0)));
    }

    @Test
    public void removeAtEnd() {
        addAll();
        removeAtEndInternal();
    }

    private void removeAtEndInternal() {
        for( int i = ELEMENT_COUNT - 1; i >= 0; i-- ) {
            Assert.assertEquals(strings.get(i), list.remove(i));
        }
        assertEmpty();
    }


    @Test
    public void removeAtBeginning() {
        addAll();
        removeAtBeginningInternal();
    }

    private void removeAtBeginningInternal() {
        for( int i = 0; i < ELEMENT_COUNT; i++ ) {
            Assert.assertEquals(strings.get(i), list.remove(0));
        }
        assertEmpty();
    }

    @Test
    public void clear() {
        addAll();
        list.clear();
        assertEmpty();
    }

    @Test
    public void addAll() {
        list.addAll(strings);
        assertFull();
    }

    @Test
    public void contains() {
        String string = strings.get(0);
        Assert.assertFalse(list.contains(string));
        list.add(string);
        Assert.assertTrue(list.contains(string));
    }

    @Test
    public void containsBestCase() {
        addAll();
        Assert.assertTrue(list.contains(strings.get(0)));
    }

    @Test
    public void containsWorstCase() {
        addAll();
        Assert.assertTrue(list.contains(strings.get(strings.size() - 1)));
    }

    @Test
    public void isEmpty() {
        Assert.assertTrue(list.isEmpty());
        addAll();
        Assert.assertFalse(list.isEmpty());
    }

    @Test
    public void indexOf() {
        String string = strings.get(0);
        Assert.assertEquals(-1, list.indexOf(string));
        list.add(string);
        Assert.assertEquals(0, list.indexOf(string));
    }

    @Test
    public void lastIndexOf() {
        String string = strings.get(ELEMENT_COUNT - 1);
        Assert.assertEquals(-1, list.lastIndexOf(string));
        addAll();
        Assert.assertEquals(strings.indexOf(string), list.lastIndexOf(string));
    }

    @Test
    public void setShrinkNeeded() {
        list.add(LONG_STRING);
        list.set(0, SHORT_STRING);
        Assert.assertEquals(SHORT_STRING, list.get(0));
        Assert.assertEquals(1, list.size());
    }

    @Test
    public void setGrowNeeded() {
        list.add(SHORT_STRING);
        list.set(0, LONG_STRING);
        Assert.assertEquals(LONG_STRING, list.get(0));
        Assert.assertEquals(1, list.size());
    }

    @Test
    public void setSameSize() {
        String string = strings.get(0), string1 = RandomStringUtils.random(string.length());
        list.add(string);
        list.set(0, string1);
        Assert.assertEquals(string1, list.get(0));
        Assert.assertEquals(1, list.size());
    }

    @Test
    public void remap() throws IOException {
        addAll();
        list.remap();
        list.load();
        assertFull();
    }

    @Test
    public void remapAndRemoveAtBeginning() throws IOException {
        remap();
        removeAtBeginningInternal();
        assertEmpty();
    }

    @Test
    public void remapAndRemoveAtEnd() throws IOException {
        remap();
        removeAtEndInternal();
        assertEmpty();
    }

    @Test
    public void remapAndRemove() throws IOException {
        remap();
        removeAllInternal();
        assertEmpty();
    }

    @Test
    public void removeAll() {
        addAll();
        removeAllInternal();
    }

    private void removeAllInternal() {
        list.removeAll(strings);
        assertEmpty();
    }

    @Test
    public void remapAndAdd() throws IOException {
        remap();
        for( int i = 0; i < ELEMENT_COUNT; i++ ) {
            list.add(strings.get(i));
        }
        Assert.assertEquals(ELEMENT_COUNT * 2, list.size());
    }

    @Test
    public void remapAddAndRemove() throws IOException {
        remapAndAdd();
        for( int i = ELEMENT_COUNT - 1; i >= 0; i-- ) {
            Assert.assertEquals(strings.get(i), list.remove(i));
        }
        assertFull();
    }

    private void assertEmpty() {
        Assert.assertTrue(list.size() == 0);
        Assert.assertFalse(list.iterator().hasNext());
        try {
            list.get(0);
            Assert.fail();
        } catch( IndexOutOfBoundsException e ) {
            // Do nothing
        }
    }

    private void assertFullInReverse() {
        Assert.assertTrue(list.size() == ELEMENT_COUNT);
        int count = 0;
        for( int i = ELEMENT_COUNT - 1; i >= 0; i-- ) {
            Assert.assertEquals(strings.get(count++), list.get(i));
        }
        count = 0;
        //noinspection ForLoopReplaceableByForEach
        for( ListIterator<String> iter = list.listIterator(list.size()); iter.hasPrevious(); ) {
            Assert.assertEquals(strings.get(count++), iter.previous());
        }
        Assert.assertEquals(ELEMENT_COUNT, count);
        try {
            Assert.assertEquals(strings.get(0), list.get(ELEMENT_COUNT - 1));
        } catch( IndexOutOfBoundsException e ) {
            Assert.fail();
        }
        try {
            list.get(ELEMENT_COUNT);
            Assert.fail();
        } catch( IndexOutOfBoundsException e ) {
            // Ignore
        }
    }

    private void assertFull() {
        Assert.assertTrue(list.size() == ELEMENT_COUNT);
        for( int i = 0; i < ELEMENT_COUNT; i++ ) {
            Assert.assertEquals(strings.get(i), list.get(i));
        }
        int count = 0;
        //noinspection ForLoopReplaceableByForEach
        for( Iterator<String> iter = list.iterator(); iter.hasNext(); ) {
            Assert.assertEquals(strings.get(count), iter.next());
            count++;
        }
        Assert.assertEquals(ELEMENT_COUNT, count);
        try {
            Assert.assertEquals(strings.get(ELEMENT_COUNT - 1), list.get(ELEMENT_COUNT - 1));
        } catch( IndexOutOfBoundsException e ) {
            Assert.fail();
        }
        try {
            list.get(ELEMENT_COUNT);
            Assert.fail();
        } catch( IndexOutOfBoundsException e ) {
            // Ignore
        }
    }
}
