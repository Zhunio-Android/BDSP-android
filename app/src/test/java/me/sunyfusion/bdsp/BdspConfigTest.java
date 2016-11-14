package me.sunyfusion.bdsp;

import android.content.Context;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import me.sunyfusion.bdsp.exception.BdspConfigException;
import me.sunyfusion.bdsp.state.BdspConfig;

import static junit.framework.Assert.assertTrue;

/**
 * Created by deisingj1 on 10/24/2016.
 */

@RunWith(RobolectricGradleTestRunner.class)
// To use Robolectric you'll need to setup some constants.
// Change it according to your needs.
@Config(constants = BuildConfig.class, sdk = 21, manifest = "/src/main/AndroidManifest.xml")
public class BdspConfigTest {
    Context c;
    BdspConfig config;
    String testFile = Resources.testFile;

    @Before
    public void setup() {
        c = RuntimeEnvironment.application;
        config = new BdspConfig(c);
    }
    @Test
    public void config_initTestNull() {
        Throwable ex = null;
        try {
            config.init(null);
        } catch (Exception e) {
            ex = e;
        }
        assertTrue(ex != null && ex instanceof BdspConfigException);
    }
    @Test
    public void config_initTestValid() {
        InputStream is = new ByteArrayInputStream(testFile.getBytes());
        Throwable ex = null;
        try {
            config.init(is);
        }
        catch(BdspConfigException e) {
            ex = e;
        }
        assertTrue(ex == null);
        assertTrue(config.getIdKey().equals("userId"));
        assertTrue(config.getFields().size() == 3);
    }
}