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

package com.eatnumber1.util.collections.iterators;

import com.eatnumber1.util.facade.SimpleFacade;
import java.util.Iterator;
import org.jetbrains.annotations.NotNull;

/**
 * @author Russell Harmon
 * @since Jul 13, 2007
 */
public class UnmodifiableIterator<T, D extends Iterator<T>> extends SimpleFacade<D> implements Iterator<T> {
    public UnmodifiableIterator( @NotNull D delegate ) {
        super(delegate);
    }

    public boolean hasNext() {
        return getDelegate().hasNext();
    }

    public T next() {
        return getDelegate().next();
    }

    public void remove() {
        throw new UnsupportedOperationException();
    }
}
