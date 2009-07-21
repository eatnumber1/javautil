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

package com.eatnumber1.util.io;

import com.eatnumber1.util.collections.Collections;
import com.eatnumber1.util.collections.concurrent.SynchronizedMap;
import com.eatnumber1.util.concurrent.facade.SynchronizedFacade;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.WeakHashMap;
import java.util.concurrent.locks.Lock;
import net.jcip.annotations.GuardedBy;
import org.jetbrains.annotations.NotNull;

/**
 * @author Russell Harmon
 * @since Jul 17, 2009
 */
public class FileLockProvider {
    private static class FileLockCounter extends SynchronizedFacade<java.nio.channels.FileLock> implements FileLock {
        @NotNull
        private FileChannel channel;

        @NotNull
        private FileInputStream stream;

        @NotNull
        private File file;

        @GuardedBy("lockStorage.writeLock()")
        private int owners = 1;

        private FileLockCounter( @NotNull java.nio.channels.FileLock lock, @NotNull FileChannel channel, @NotNull FileInputStream stream, @NotNull File file ) {
            super(lock);
            this.channel = channel;
            this.stream = stream;
            this.file = file;
        }

        private void incOwners() {
            owners++;
        }

        public void release() throws IOException {
            Lock mapLock = lockStorage.writeLock();
            mapLock.lock();
            try {
                if( --owners == 0 ) {
                    getDelegate().release();
                    lockStorage.remove(file);
                    channel.close();
                    stream.close();
                }
            } finally {
                mapLock.unlock();
            }
        }
    }

    @NotNull
    private static SynchronizedMap<File, FileLockCounter, WeakHashMap<File, FileLockCounter>> lockStorage = Collections.synchronizedMap(new WeakHashMap<File, FileLockCounter>());

    @NotNull
    public static FileLock acquire( @NotNull File file ) throws IOException {
        FileInputStream is = new FileInputStream(file);
        Lock lock = lockStorage.getWriteLock();
        lock.lock();
        try {
            FileLockCounter fileLock = lockStorage.get(file);
            if( fileLock != null ) {
                fileLock.incOwners();
                return fileLock;
            }
            FileChannel channel = is.getChannel();
            fileLock = new FileLockCounter(channel.lock(), channel, is, file);
            lockStorage.put(file, fileLock);
            return fileLock;
        } finally {
            lock.unlock();
        }
    }
}
