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
import java.io.IOException;
import java.util.Iterator;
import java.util.ListIterator;
import org.apache.commons.lang.RandomStringUtils;
import org.jetbrains.annotations.NotNull;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author Russell Harmon
 * @since Jul 13, 2007
 */
public class FileBackedArrayListTest extends AbstractFileBackedCollectionTest {
    private FileBackedArrayList<String> list;

    @NotNull
    protected FileBackedCollection<String> newCollection2() throws Throwable {
        createDirectory();
        list = new FileBackedArrayList<String>(tempFile, true, new CompressedPersistenceProvider<String>(new SerializablePersistenceProvider<String>(), new ZLIBCompressionProvider()));
        return list;
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

    @Override
    protected void assertEmpty() {
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

    @Override
    protected void assertFull() {
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
