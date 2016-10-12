package me.sunyfusion.bdsp;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.test.mock.MockContext;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import me.sunyfusion.bdsp.column.Unique;
import me.sunyfusion.bdsp.db.BdspDB;
import me.sunyfusion.bdsp.state.Global;

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
        Global.getInstance().init(c);
        Unique unique = new Unique(c, "TestUnique");
        Assert.assertTrue("column name not set", unique.getColumnName().equals("TestUnique"));
    }
}