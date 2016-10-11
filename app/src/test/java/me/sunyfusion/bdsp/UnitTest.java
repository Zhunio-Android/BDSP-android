package me.sunyfusion.bdsp;

import android.test.mock.MockContext;

import org.junit.Assert;
import org.junit.Test;

import me.sunyfusion.bdsp.column.Unique;

/**
 * <a href="http://d.android.com/tools/testing/testing_android.html">Testing Fundamentals</a>
 */

public class UnitTest {
    @Test
    public void unique_SetUniqueName_ReturnsTrue() {
        Unique unique = new Unique(new MockContext(), "TestUnique");
        Assert.assertTrue("column name set", unique.getColumnName().equals("TestUnique"));
    }
}