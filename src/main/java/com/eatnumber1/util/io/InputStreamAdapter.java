package com.eatnumber1.util.io;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.LinkedBlockingDeque;
import org.jetbrains.annotations.NotNull;

/**
 * @author Russell Harmon
 */
public class InputStreamAdapter extends InputStream {
    @NotNull
    private BlockingDeque<Integer> data = new LinkedBlockingDeque<Integer>();

    @Override
    public int read() throws IOException {
        try {
            return data.takeFirst();
        } catch( InterruptedException e ) {
            throw new RuntimeException(e);
        }
    }

    public OutputStream asOutputStream() {
        return new OutputStream() {
            @Override
            public void write( int b ) throws IOException {
                try {
                    data.putLast(b);
                } catch( InterruptedException e ) {
                    throw new RuntimeException(e);
                }
            }
        };
    }
}
