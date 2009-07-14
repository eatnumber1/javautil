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

import com.eatnumber1.util.collections.concurrent.SynchronizedCollection;
import com.eatnumber1.util.collections.concurrent.SynchronizedCollectionFacade;
import com.eatnumber1.util.collections.concurrent.SynchronizedDeque;
import com.eatnumber1.util.collections.concurrent.SynchronizedDequeFacade;
import com.eatnumber1.util.collections.concurrent.SynchronizedList;
import com.eatnumber1.util.collections.concurrent.SynchronizedListFacade;
import com.eatnumber1.util.collections.concurrent.SynchronizedMap;
import com.eatnumber1.util.collections.concurrent.SynchronizedMapFacade;
import com.eatnumber1.util.collections.concurrent.SynchronizedQueue;
import com.eatnumber1.util.collections.concurrent.SynchronizedQueueFacade;
import com.eatnumber1.util.collections.concurrent.SynchronizedSet;
import com.eatnumber1.util.collections.concurrent.SynchronizedSetFacade;
import java.util.Collection;
import java.util.Comparator;
import java.util.Deque;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import org.jetbrains.annotations.NotNull;

/**
 * @author Russell Harmon
 * @since Jul 13, 2009
 */
public class Collections {
    public static <E> boolean contains( @NotNull Collection<E> c1, @NotNull E o, @NotNull Comparator<E> comparator ) {
        for( E obj : c1 ) {
            if( comparator.compare(obj, o) == 0 ) return true;
        }
        return false;
    }

    public static <E> boolean contains( @NotNull Collection<E> c1, @NotNull E o, @NotNull EqualityComparator<E> comparator ) {
        for( E obj : c1 ) {
            if( comparator.equals(obj, o) ) return true;
        }
        return false;
    }

    public static <K, V> V get( @NotNull Map<K, V> map, @NotNull K key, @NotNull Comparator<K> comparator ) {
        for( Map.Entry<K, V> entry : map.entrySet() ) {
            if( comparator.compare(key, entry.getKey()) == 0 ) return entry.getValue();
        }
        return null;
    }

    public static <K, V> void addAll( @NotNull Map<K, V> dst, @NotNull Map<? extends K, ? extends V> src ) {
        for( Map.Entry<? extends K, ? extends V> entry : src.entrySet() ) {
            dst.put(entry.getKey(), entry.getValue());
        }
    }

    public static <T> boolean removeAll( @NotNull Collection<T> target, @NotNull Collection<T> src, @NotNull EqualityComparator<T> comparator ) {
        boolean changed = false;
        for( Iterator<T> iter = target.iterator(); iter.hasNext(); ) {
            if( contains(src, iter.next(), comparator) ) {
                changed = true;
                iter.remove();
            }
        }
        return changed;
    }

    public static <T> boolean retainAll( @NotNull Collection<T> target, @NotNull Collection<T> src, @NotNull EqualityComparator<T> comparator ) {
        boolean changed = false;
        for( Iterator<T> iter = target.iterator(); iter.hasNext(); ) {
            if( !contains(src, iter.next(), comparator) ) {
                changed = true;
                iter.remove();
            }
        }
        return changed;
    }

    @NotNull
    public static <T, D extends Collection<T>> SynchronizedCollection<T, D> synchronizedCollection( @NotNull D collection ) {
        return new SynchronizedCollectionFacade<T, D>(collection);
    }

    @NotNull
    public static <T, D extends Set<T>> SynchronizedSet<T, D> synchronizedSet( @NotNull D set ) {
        return new SynchronizedSetFacade<T, D>(set);
    }

    @NotNull
    public static <K, V, D extends Map<K, V>> SynchronizedMap<K, V, D> synchronizedMap( @NotNull D map ) {
        return new SynchronizedMapFacade<K, V, D>(map);
    }

    @NotNull
    public static <T, D extends List<T>> SynchronizedList<T, D> synchronizedList( @NotNull D list ) {
        return new SynchronizedListFacade<T, D>(list);
    }

    @NotNull
    public static <T, D extends Queue<T>> SynchronizedQueue<T, D> synchronizedQueue( @NotNull D queue ) {
        return new SynchronizedQueueFacade<T, D>(queue);
    }

    @NotNull
    public static <T, D extends Deque<T>> SynchronizedDeque<T, D> synchronizedDeque( @NotNull D deque ) {
        return new SynchronizedDequeFacade<T, D>(deque);
    }
}
