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

package com.eatnumber1.util.collections.persistent.channel;

import java.io.File;
import java.io.IOException;
import java.nio.channels.ClosedChannelException;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import org.jetbrains.annotations.NotNull;

/**
 * @author Russell Harmon
 * @since Jul 27, 2009
 */
public abstract class AbstractFileChannelProvider implements FileChannelProvider {
    @NotNull
    protected Lock lock = new ReentrantLock();

    @NotNull
    private File file;

    @NotNull
    private String permissions;

    protected boolean closed = true;

    protected AbstractFileChannelProvider( @NotNull File file, @NotNull String permissions ) throws IOException {
        this.file = file;
        this.permissions = permissions;
        open();
    }

    @NotNull
    public File getFile() {
        return file;
    }

    @NotNull
    public String getPermissions() {
        return permissions;
    }

    @Override
    public void close() throws IOException {
        if( !closed ) {
            closeInternal();
            closed = true;
        }
    }

    protected abstract void closeInternal() throws IOException;

    @Override
    public void open() throws IOException {
        if( closed ) {
            openInternal();
            closed = false;
        }
    }

    protected abstract void openInternal() throws IOException;

    @Override
    public void flush() throws IOException {
        if( !closed ) flushInternal();
    }

    protected abstract void flushInternal() throws IOException;

    @Override
    protected void finalize() throws Throwable {
        try {
            close();
        } finally {
            super.finalize();
        }
    }

    @Override
    public <T> T visitValueChannel( @NotNull FileChannelVisitor<T> visitor ) throws IOException {
        if( closed ) throw new ClosedChannelException();
        lock.lock();
        try {
            return visitValueChannelInternal(visitor);
        } finally {
            lock.unlock();
        }
    }

    protected abstract <T> T visitValueChannelInternal( @NotNull FileChannelVisitor<T> visitor ) throws IOException;
}
