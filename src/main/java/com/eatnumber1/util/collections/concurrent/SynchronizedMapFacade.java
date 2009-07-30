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

package com.eatnumber1.util.collections.concurrent;

import com.eatnumber1.util.concurrent.facade.SynchronizedReadWriteFacade;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import net.jcip.annotations.ThreadSafe;
import org.jetbrains.annotations.NotNull;

/**
 * @author Russell Harmon
 * @since Jul 13, 2007
 */
@ThreadSafe
public class SynchronizedMapFacade<K, V, D extends Map<K, V>> extends SynchronizedReadWriteFacade<D> implements SynchronizedMap<K, V, D> {
    public SynchronizedMapFacade( @NotNull D delegate, @NotNull Lock lock ) {
        super(delegate, lock);
    }

    public SynchronizedMapFacade( @NotNull D delegate, @NotNull ReadWriteLock lock ) {
        super(delegate, lock);
    }

    public SynchronizedMapFacade( @NotNull D delegate ) {
        super(delegate);
    }

    @Override
    public int size() {
        Lock readLock = readLock();
        readLock.lock();
        try {
            return getDelegate().size();
        } finally {
            readLock.unlock();
        }
    }

    @Override
    public boolean isEmpty() {
        Lock readLock = readLock();
        readLock.lock();
        try {
            return getDelegate().isEmpty();
        } finally {
            readLock.unlock();
        }
    }

    @Override
    public boolean containsKey( Object key ) {
        Lock readLock = readLock();
        readLock.lock();
        try {
            return getDelegate().containsKey(key);
        } finally {
            readLock.unlock();
        }
    }

    @Override
    public boolean containsValue( Object value ) {
        Lock readLock = readLock();
        readLock.lock();
        try {
            return getDelegate().containsValue(value);
        } finally {
            readLock.unlock();
        }
    }

    @Override
    public V get( Object key ) {
        Lock readLock = readLock();
        readLock.lock();
        try {
            return getDelegate().get(key);
        } finally {
            readLock.unlock();
        }
    }

    @Override
    public V put( K key, V value ) {
        Lock writeLock = writeLock();
        writeLock.lock();
        try {
            return getDelegate().put(key, value);
        } finally {
            writeLock.unlock();
        }
    }

    @Override
    public V remove( Object key ) {
        Lock writeLock = writeLock();
        writeLock.lock();
        try {
            return getDelegate().remove(key);
        } finally {
            writeLock.unlock();
        }
    }

    @Override
    public void putAll( Map<? extends K, ? extends V> t ) {
        Lock writeLock = writeLock();
        writeLock.lock();
        try {
            getDelegate().putAll(t);
        } finally {
            writeLock.unlock();
        }
    }

    @Override
    public void clear() {
        Lock writeLock = writeLock();
        writeLock.lock();
        try {
            getDelegate().clear();
        } finally {
            writeLock.unlock();
        }
    }

    @Override
    public Set<K> keySet() {
        Lock readLock = readLock();
        readLock.lock();
        try {
            return new SynchronizedSetFacade<K, Set<K>>(getDelegate().keySet(), getReadWriteLock());
        } finally {
            readLock.unlock();
        }
    }

    @Override
    public Collection<V> values() {
        Lock readLock = readLock();
        readLock.lock();
        try {
            return new SynchronizedCollectionFacade<V, Collection<V>>(getDelegate().values(), getReadWriteLock());
        } finally {
            readLock.unlock();
        }
    }

    @Override
    public Set<Entry<K, V>> entrySet() {
        Lock readLock = readLock();
        readLock.lock();
        try {
            return new SynchronizedSetFacade<Entry<K, V>, Set<Entry<K, V>>>(getDelegate().entrySet(), getReadWriteLock());
        } finally {
            readLock.unlock();
        }
    }

    @Override
    public V putIfAbsent( K key, V value ) {
        Lock writeLock = writeLock();
        writeLock.lock();
        try {
            return containsKey(key) ? null : put(key, value);
        } finally {
            writeLock.unlock();
        }
    }

    @Override
    public boolean remove( Object key, Object value ) {
        Lock writeLock = writeLock();
        writeLock.lock();
        try {
            if( containsKey(key) && get(key).equals(value) ) {
                remove(key);
                return true;
            }
            return false;
        } finally {
            writeLock.unlock();
        }
    }

    @Override
    public boolean replace( K key, V oldValue, V newValue ) {
        Lock writeLock = writeLock();
        writeLock.lock();
        try {
            if( remove(key, oldValue) ) {
                put(key, newValue);
                return true;
            }
            return false;
        } finally {
            writeLock.unlock();
        }
    }

    @Override
    public V replace( K key, V value ) {
        Lock writeLock = writeLock();
        writeLock.lock();
        try {
            if( containsKey(key) ) {
                return put(key, value);
            }
            return null;
        } finally {
            writeLock.unlock();
        }
    }
}
