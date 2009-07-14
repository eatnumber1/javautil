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

import net.sf.cglib.proxy.Dispatcher;
import org.jetbrains.annotations.NotNull;

/**
 * @author Russell Harmon
 * @since Jul 13, 2007
 */
public class DelegatingCallback<T> implements Dispatcher {
    @NotNull
    private T delegate;

    public DelegatingCallback( @NotNull T delegate ) {
        this.delegate = delegate;
    }

    @NotNull
    public Object loadObject() throws Exception {
        return delegate;
    }
}
