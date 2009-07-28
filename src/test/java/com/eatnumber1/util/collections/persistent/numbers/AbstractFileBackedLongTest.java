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

package com.eatnumber1.util.collections.persistent.numbers;

import java.io.IOException;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author Russell Harmon
 * @since Jul 27, 2009
 */
public abstract class AbstractFileBackedLongTest extends AbstractFileBackedNumberTest {
    @Test
    public void setLongValue() {
        number.longValue(Long.MAX_VALUE);
        Assert.assertEquals(Long.MAX_VALUE, number.longValue());
    }

    @Test
    public void persistentLongValue() throws IOException {
        number.longValue(Long.MAX_VALUE);
        initNumber();
        Assert.assertEquals(Long.MAX_VALUE, number.longValue());
    }
}
