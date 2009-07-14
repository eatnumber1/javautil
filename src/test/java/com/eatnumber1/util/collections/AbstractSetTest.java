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

import java.util.HashSet;
import java.util.Set;
import org.jetbrains.annotations.NotNull;
import org.junit.Assert;
import org.junit.Before;

/**
 * @author Russell Harmon
 * @since Jul 14, 2009
 */
public abstract class AbstractSetTest extends AbstractCollectionTest {
    @NotNull
    private Set<String> strings;

    @Before
    public void initStringSet() {
        strings = new HashSet<String>(super.strings);
    }

    @Override
    protected void assertFull() {
        Assert.assertTrue(collection.size() == strings.size());
        int count = 0;
        for( String s : collection ) {
            Assert.assertTrue(strings.contains(s));
            count++;
        }
        Assert.assertEquals(strings.size(), count);
    }
}
