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

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.LinkedBlockingDeque;
import net.jcip.annotations.ThreadSafe;
import org.jetbrains.annotations.NotNull;

/**
 * @author Russell Harmon
 * @since Jul 13, 2007
 */
@ThreadSafe
public class OutputStreamAdapter extends OutputStream {
    @NotNull
    private BlockingDeque<Integer> data = new LinkedBlockingDeque<Integer>();

    public OutputStreamAdapter() {
    }

    @Override
    public void write( int b ) throws IOException {
        try {
            data.putLast(b);
        } catch( InterruptedException e ) {
            throw new RuntimeException(e);
        }
    }

    @NotNull
    public InputStream asInputStream() {
        return new InputStream() {
            @Override
            public int read() throws IOException {
                try {
                    return data.takeFirst();
                } catch( InterruptedException e ) {
                    throw new RuntimeException(e);
                }
            }
        };
    }
}