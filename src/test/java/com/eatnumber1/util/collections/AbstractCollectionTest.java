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

package com.eatnumber1.util.collections;

import java.util.Collection;
import java.util.Iterator;
import org.jetbrains.annotations.NotNull;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * @author Russell Harmon
 * @since Jul 14, 2009
 */
public abstract class AbstractCollectionTest extends AbstractStringsTest {

    @NotNull
    protected Collection<String> collection;

    @NotNull
    protected abstract Collection<String> newCollection() throws Throwable;

    @Before
    public void setUp() throws Throwable {
        collection = newCollection();
    }

    @Test
    public void add() {
        for( int i = 0; i < ELEMENT_COUNT; i++ ) {
            collection.add(strings.get(i));
        }
        assertFull();
    }

    @Test
    public void remove() {
        addAll();
        removeInternal();
    }

    protected void removeInternal() {
        for( int i = 0; i < ELEMENT_COUNT; i++ ) {
            collection.remove(strings.get(i));
        }
        assertEmpty();
        collection.remove(strings.get(0));
    }

    @Test
    public void clear() {
        addAll();
        collection.clear();
        assertEmpty();
    }

    @Test
    public void addAll() {
        collection.addAll(strings);
        assertFull();
    }

    @Test
    public void contains() {
        String string = strings.get(0);
        Assert.assertFalse(collection.contains(string));
        collection.add(string);
        Assert.assertTrue(collection.contains(string));
    }

    @Test
    public void isEmpty() {
        Assert.assertTrue(collection.isEmpty());
        addAll();
        Assert.assertFalse(collection.isEmpty());
    }

    @Test
    public void removeAll() {
        addAll();
        removeAllInternal();
    }

    protected void removeAllInternal() {
        collection.removeAll(strings);
        assertEmpty();
    }

    protected void assertEmpty() {
        Assert.assertTrue(collection.size() == 0);
        Assert.assertFalse(collection.iterator().hasNext());
        Assert.assertTrue(collection.toArray().length == 0);
    }

    protected void assertFull() {
        Assert.assertTrue(collection.size() == ELEMENT_COUNT);
        int count = 0;
        //noinspection ForLoopReplaceableByForEach
        for( Iterator<String> iter = collection.iterator(); iter.hasNext(); ) {
            Assert.assertEquals(strings.get(count), iter.next());
            count++;
        }
        Assert.assertEquals(ELEMENT_COUNT, count);
    }
}
