package org.bd_sp.bdsp;

import android.content.Context;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;

/**
 * Created by jesse on 10/17/16.
 */
@RunWith(RobolectricGradleTestRunner.class)
// To use Robolectric you'll need to setup some constants.
// Change it according to your needs.
@Config(constants = BuildConfig.class, sdk = 21, manifest = "/src/main/AndroidManifest.xml")
public class BdspRowUnitTest {
    @Before
    public void setup() {

    }
    @After
    public void cleanup() {

    }
    @Test
    //it should save a key/value pair
    public void BdspRow_saveKeyValuePair() {
        BdspRow r = new BdspRow();
        assertTrue(r.put("test", "testValue"));
        assertTrue(r.getRow().containsKey("test"));
        assertTrue(r.getRow().get("test").equals("testValue"));
        assertTrue(r.put("test", ""));
        assertFalse(r.put("", "testValue"));
        assertFalse(r.put("test", null));
        assertFalse(r.put(null,null));
    }
    //it should submit a ContentValues to the database
    @Test
    public void BsdpRow_submitContentValues() {
        Context c = RuntimeEnvironment.application;
        BdspRow r = new BdspRow();
        r.ColumnNames.put(BdspRow.ColumnType.ID,"id");
        r.ColumnNames.put(BdspRow.ColumnType.RUN,"run");
        r.ColumnNames.put(BdspRow.ColumnType.GEOMETRY,"geometry");
        r.ColumnNames.put(BdspRow.ColumnType.PHOTO,"photo");
        r.ColumnNames.put(BdspRow.ColumnType.LATITUDE,"latitude");
        r.ColumnNames.put(BdspRow.ColumnType.LONGITUDE,"longitude");
        r.put(r.ColumnNames.get(BdspRow.ColumnType.ID),"test");
        r.put(r.ColumnNames.get(BdspRow.ColumnType.RUN),"1");
        r.put(r.ColumnNames.get(BdspRow.ColumnType.GEOMETRY),"kml");
        r.put(r.ColumnNames.get(BdspRow.ColumnType.PHOTO),"img.jpg");
        r.put(r.ColumnNames.get(BdspRow.ColumnType.LATITUDE),"100");
        r.put(r.ColumnNames.get(BdspRow.ColumnType.LONGITUDE),"20");
        assertTrue(r.prepare(c));
    }
    @Test
    public void BdspRow_appendValueToKey() {
        BdspRow r = new BdspRow();
        assertFalse(r.append("string", "value"));
        assertTrue(r.append("string", "value2"));
        assertTrue(r.getRow().get("string").equals("valuevalue2"));
    }
    @Test
    public void BdspRow_clearRow() {
        BdspRow r = new BdspRow();
        r.put("test","value");
        r.clear();
        assertTrue(r.getRow().size() == 0);
    }
}
