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

import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang.math.RandomUtils;
import org.jetbrains.annotations.NotNull;
import org.junit.Before;

/**
 * @author Russell Harmon
 * @since Jul 14, 2009
 */
public abstract class AbstractStringsTest {
    protected static final int ELEMENT_COUNT = 500;
    protected static final boolean RANDOM_STRINGS = true;
    protected static final int MAX_STRING_LENGTH = 300;
    protected static final String SHORT_STRING = "a";
    protected static final String LONG_STRING = "HELLOHELLOHELLOHELLOHELLOHELLOHELLOHELLOHELLOHELLOHELLOHELLOHELLOHELLOHELLOHELLOHELLOHELLOHELLOHELLOHELLOHELLOHELLOHELLOHELLOHELLOHELLOHELLOHELLOHELLOHELLOHELLOHELLOHELLOHELLOHELLOHELLOHELLOHELLOHELLOHELLOHELLOHELLOHELLOHELLOHELLOHELLO";

    @NotNull
    protected List<String> strings;

    @Before
    public void generateStrings() {
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
        strings = java.util.Collections.unmodifiableList(strings);
    }
}
