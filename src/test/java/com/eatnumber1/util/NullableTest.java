package com.eatnumber1.util;

import org.jetbrains.annotations.NotNull;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author Russell Harmon
 */
public class NullableTest {
    private void notNull( @NotNull Object obj ) {}

    @Test
    public void nullable() {
        try {
            notNull(null);
            Assert.fail();
        } catch( IllegalArgumentException e ) {
            // Do nothing
        }
    }
}
