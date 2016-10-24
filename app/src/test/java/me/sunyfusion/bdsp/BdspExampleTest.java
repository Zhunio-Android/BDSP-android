package me.sunyfusion.bdsp;

import android.content.Context;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

/**
 * Created by deisingj1 on 10/24/2016.
 */

@RunWith(RobolectricGradleTestRunner.class)
// To use Robolectric you'll need to setup some constants.
// Change it according to your needs.
@Config(constants = BuildConfig.class, sdk = 21, manifest = "/src/main/AndroidManifest.xml")
public class BdspExampleTest {
    @Before
    public void setup() {
        Context c = RuntimeEnvironment.application;

    }
    @Test
    public void config_() {

    }
}