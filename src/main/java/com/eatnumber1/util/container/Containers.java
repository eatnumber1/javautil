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

package com.eatnumber1.util.container;

import com.eatnumber1.util.concurrent.container.SynchronizedContainer;
import com.eatnumber1.util.concurrent.container.SynchronizedReadWriteContainer;
import org.jetbrains.annotations.NotNull;

/**
 * @author Russell Harmon
 * @since Jul 13, 2009
 */
public class Containers {
    private Containers() {
    }

    public static <T> Container<T> container( @NotNull T object ) {
        return new SimpleContainer<T>(object);
    }

    public static <T> ReadWriteContainer<T> readWriteContainer( @NotNull T object ) {
        return new SimpleReadWriteContainer<T>(object);
    }

    public static <T> Container<T> synchronizedContainer( @NotNull T object ) {
        return new SynchronizedContainer<T>(object);
    }

    public static <T> ReadWriteContainer<T> synchronizedReadWriteContainer( @NotNull T object ) {
        return new SynchronizedReadWriteContainer<T>(object);
    }
}
