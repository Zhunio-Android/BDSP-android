package me.sunyfusion.bdsp;

import android.content.ContentValues;
import android.content.Context;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import java.util.Random;

import me.sunyfusion.bdsp.db.BdspDB;

import static junit.framework.Assert.assertTrue;

/**
 * <a href="http://d.android.com/tools/testing/testing_android.html">Testing Fundamentals</a>
 */
@RunWith(RobolectricGradleTestRunner.class)
// To use Robolectric you'll need to setup some constants.
// Change it according to your needs.
@Config(constants = BuildConfig.class, sdk = 21, manifest = "/src/main/AndroidManifest.xml")
public class BdspDBUnitTest {

    Context c;
    BdspDB db;
    Random generator;
    @Before
    public void setup() {
        c = RuntimeEnvironment.application;
        db = new BdspDB(c);
        generator = new Random();
    }
    @After
    public void cleanup() {
        db.close();
    }
    @Test
    public void BdspDB_submitEmptyContentValues_ReturnsTrue() {
        //Verify behavior when submitting a blank CV object
        ContentValues cv = new ContentValues();
        db.insert(cv);
    }
    @Test
    public void BdspDB_submitWhenColumnsDoNotExist_ReturnsTrue() {
        //Verify behavior when submitting a CV object for which there are not columns in the database
        ContentValues cv = new ContentValues();

        for(int i = 1; i <= 200; i++) {
            cv.put("run", i);
            cv.put("tons", generator.nextInt(50));
            cv.put("time", generator.nextLong());
            db.insert(cv);
        }
    }
    @Test
    public void BdspDB_submitUnderNormalConditions_ReturnsTrue() {
        //Add columns to the database, verify that CV object is submitted correctly
        db.addColumn("run", "TEXT");
        db.addColumn("tons", "TEXT");
        db.addColumn("time", "TEXT");

        ContentValues cv = new ContentValues();
        for(int i = 1; i <= 200; i++) {
            cv.put("run", i);
            cv.put("tons", generator.nextInt(50));
            cv.put("time", generator.nextLong());
            db.insert(cv);
        }
        int count = db.queueAll(null).getCount();
        assertTrue("incorrect row insertion, expected 200, actual = " + count, count == 200);
    }
}