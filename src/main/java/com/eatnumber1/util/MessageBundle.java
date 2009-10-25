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

package com.eatnumber1.util;

import com.eatnumber1.util.collections.Collections;
import com.eatnumber1.util.collections.concurrent.SynchronizedMap;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.concurrent.locks.Lock;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author Russell Harmon
 * @since Aug 20, 2009
 */
public class MessageBundle {
    @NotNull
    private static SynchronizedMap<String, MessageBundle, ? extends Map<String, MessageBundle>> bundleMap = Collections.synchronizedMap(new HashMap<String, MessageBundle>());

    @NotNull
    public static MessageBundle getMessageBundle( @Nullable String domain ) {
        MessageBundle bundle = constructMessageBundle(domain);
        if( bundle.parent == null && domain != null ) bundle.parent = getMessageBundle(getParentDomain(domain));
        return bundle;
    }

    @NotNull
    public static MessageBundle getMessageBundle( @NotNull Class domain ) {
        return getMessageBundle(domain.getPackage().getName());
    }

    @NotNull
    private static MessageBundle constructMessageBundle( @Nullable String domain ) {
        Lock lock = bundleMap.writeLock();
        lock.lock();
        try {
            MessageBundle bundle = bundleMap.get(domain);
            if( bundle != null ) return bundle;
            bundle = new MessageBundle(domain);
            bundleMap.put(domain, bundle);
            return bundle;
        } finally {
            lock.unlock();
        }
    }

    @Nullable
    private static String getParentDomain( @NotNull String domain ) {
        int index = domain.lastIndexOf('.');
        if( index == -1 ) return null;
        return domain.substring(0, index);
    }

    @Nullable
    private MessageBundle parent;

    @Nullable
    private ResourceBundle bundle;

    @Nullable
    private String domain;

    private MessageBundle( @Nullable String domain ) {
        this.domain = domain;
        domain = domain == null ? "" : domain + ".";
        try {
            bundle = ResourceBundle.getBundle(domain + "messages");
        } catch( MissingResourceException e ) {}
    }

    @Nullable
    public String getDomain() {
        return domain;
    }

    @NotNull
    public String getMessage( @NotNull String key, @Nullable Object... arguments ) {
        try {
            if( bundle == null ) {
                String className = MessageBundle.class.getName();
                throw new MissingResourceException("Can't find resource for bundle " + className + ", key " + key, className, key);
            }
            return MessageFormat.format(bundle.getString(key), arguments);
        } catch( MissingResourceException e ) {
            if( parent != null ) return parent.getMessage(key, arguments);
            throw e;
        }
    }

    @Override
    @NotNull
    public String toString() {
        return String.valueOf(domain);
    }
}
