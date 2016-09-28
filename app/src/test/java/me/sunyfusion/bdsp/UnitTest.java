package me.sunyfusion.bdsp;

import org.junit.Assert;
import org.junit.Test;

/**
 * <a href="http://d.android.com/tools/testing/testing_android.html">Testing Fundamentals</a>
 */

public class UnitTest {
    @Test
    public void unique_SetUniqueName_ReturnsTrue() {
        Assert.assertTrue("not true", "MY VALUE".equals("MY VALUE"));
    }
}