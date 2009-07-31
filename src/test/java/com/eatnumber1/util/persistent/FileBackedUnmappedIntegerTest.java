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

package com.eatnumber1.util.persistent;

import com.eatnumber1.util.persistent.channel.ReopeningFileChannelProvider;
import com.eatnumber1.util.persistent.numbers.FileBackedNumber;
import com.eatnumber1.util.persistent.numbers.FileBackedUnmappedInteger;
import java.io.File;
import java.io.IOException;
import org.jetbrains.annotations.NotNull;

/**
 * @author Russell Harmon
 * @since Jul 27, 2009
 */
public class FileBackedUnmappedIntegerTest extends AbstractFileBackedIntegerTest {
    @NotNull
    @Override
    protected FileBackedNumber newNumber( @NotNull File file ) throws IOException {
        return new FileBackedUnmappedInteger(file, new ReopeningFileChannelProvider(file, "rw"));
    }
}
