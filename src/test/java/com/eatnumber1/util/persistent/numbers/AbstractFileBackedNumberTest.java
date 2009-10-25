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

package com.eatnumber1.util.persistent.numbers;

import com.eatnumber1.util.io.FileUtils;
import com.eatnumber1.util.persistent.numbers.FileBackedNumber;
import java.io.File;
import java.io.IOException;
import org.jetbrains.annotations.NotNull;
import org.junit.Before;

/**
 * @author Russell Harmon
 * @since Jul 27, 2009
 */
public abstract class AbstractFileBackedNumberTest {
    @NotNull
    protected File tempFile;

    @NotNull
    protected FileBackedNumber number;

    @Before
    public void createDirectory() throws IOException {
        tempFile = FileUtils.createTempFile(getClass().getSimpleName());
        FileUtils.forceDelete(tempFile);
        FileUtils.forceDeleteOnExit(tempFile);
        initNumber();
    }

    /*
    @Test
    public void setShortValue() {
        number.shortValue(Short.MAX_VALUE);
        Assert.assertEquals(Short.MAX_VALUE, number.shortValue());
    }

    @Test
    public void setByteValue() {
        number.byteValue(Byte.MAX_VALUE);
        Assert.assertEquals(Byte.MAX_VALUE, number.byteValue());
    }

    @Test
    public void setDoubleValue() {
        number.doubleValue(Double.MAX_VALUE);
        Assert.assertEquals(Double.MAX_VALUE, number.doubleValue(), 0);
    }

    @Test
    public void setFloatValue() {
        number.floatValue(Float.MAX_VALUE);
        Assert.assertEquals(Float.MAX_VALUE, number.floatValue(), 0);
    }

    @Test
    public void persistentShortValue() throws IOException {
        number.shortValue(Short.MAX_VALUE);
        initNumber();
        Assert.assertEquals(Short.MAX_VALUE, number.shortValue());
    }

    @Test
    public void persistentByteValue() throws IOException {
        number.byteValue(Byte.MAX_VALUE);
        initNumber();
        Assert.assertEquals(Byte.MAX_VALUE, number.byteValue());
    }

    @Test
    public void persistentDoubleValue() throws IOException {
        number.doubleValue(Double.MAX_VALUE);
        initNumber();
        Assert.assertEquals(Double.MAX_VALUE, number.doubleValue(), 0);
    }

    @Test
    public void persistentFloatValue() throws IOException {
        number.floatValue(Float.MAX_VALUE);
        initNumber();
        Assert.assertEquals(Float.MAX_VALUE, number.floatValue(), 0);
    }*/

    protected void initNumber() throws IOException {
        //noinspection ConstantConditions
        if( number != null ) number.close();
        number = newNumber(tempFile);
    }

    @NotNull
    protected abstract FileBackedNumber newNumber( @NotNull File file ) throws IOException;
}
