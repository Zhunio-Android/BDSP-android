package me.sunyfusion.bdsp;

import org.junit.After;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import java.util.Random;

import me.sunyfusion.bdsp.db.BdspDB;

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
    public void BsdpRow_submitContentValues() {

    }
    public void BdspRow_appendValueToKey() {
        BdspRow r = new BdspRow();
        r.put("string", "value");
        r.append("string", "value2");
        assertTrue(r.getRow().get("string").equals("valuevalue2"));
    }
}
