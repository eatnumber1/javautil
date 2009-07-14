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

package com.eatnumber1.util.cglib;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author Russell Harmon
 * @since Jul 13, 2007
 */
public class NoArgConstructorDescriptor<T> implements ConstructorDescriptor<T> {
    @NotNull
    private static final Class<?>[] CLASS_ARRAY = new Class<?>[0];

    @NotNull
    private static final Object[] OBJECT_ARRAY = new Object[0];

    @NotNull
    private Class<T> type;

    public NoArgConstructorDescriptor( @NotNull Class<T> type ) {
        this.type = type;
    }

    @NotNull
    public Class<?>[] getArgumentTypes() {
        return CLASS_ARRAY;
    }

    @Nullable
    public Object[] getArguments() {
        return OBJECT_ARRAY;
    }

    @NotNull
    public Class<T> getType() {
        return type;
    }
}
