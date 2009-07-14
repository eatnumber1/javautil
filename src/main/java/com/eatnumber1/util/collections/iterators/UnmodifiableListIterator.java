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

import java.util.ListIterator;
import org.jetbrains.annotations.NotNull;

/**
 * @author Russell Harmon
 * @since Jul 13, 2007
 */
public class UnmodifiableListIterator<T, D extends ListIterator<T>> extends UnmodifiableIterator<T, D> implements ListIterator<T> {
    public UnmodifiableListIterator( @NotNull D delegate ) {
        super(delegate);
    }

    public boolean hasPrevious() {
        return getDelegate().hasPrevious();
    }

    public T previous() {
        return getDelegate().previous();
    }

    public int nextIndex() {
        return getDelegate().nextIndex();
    }

    public int previousIndex() {
        return getDelegate().previousIndex();
    }

    public void set( T o ) {
        throw new UnsupportedOperationException();
    }

    public void add( T o ) {
        throw new UnsupportedOperationException();
    }
}
