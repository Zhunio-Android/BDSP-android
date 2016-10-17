package me.sunyfusion.bdsp;

import android.content.Context;
import android.support.v4.content.LocalBroadcastManager;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import me.sunyfusion.bdsp.column.Column;
import me.sunyfusion.bdsp.db.BdspDB;

import static junit.framework.Assert.assertTrue;

/**
 * <a href="http://d.android.com/tools/testing/testing_android.html">Testing Fundamentals</a>
 */
@RunWith(RobolectricGradleTestRunner.class)
// To use Robolectric you'll need to setup some constants.
// Change it according to your needs.
@Config(constants = BuildConfig.class, sdk = 21, manifest = "/src/main/AndroidManifest.xml")
public class UniqueUnitTest {
    @Test
    public void unique_SetUniqueName_ReturnsTrue() {
        Context c = RuntimeEnvironment.application;
        LocalBroadcastManager lbm = LocalBroadcastManager.getInstance(c);
        Column unique = new Column(lbm, Column.ColumnType.UNIQUE, "TestUnique", new BdspDB(c));
        Assert.assertTrue("column name not set", unique.getName().equals("TestUnique"));
    }
}