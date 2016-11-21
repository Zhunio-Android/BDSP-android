package me.sunyfusion.bdsp;

import android.content.Context;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import me.sunyfusion.bdsp.state.Global;

/**
 * <a href="http://d.android.com/tools/testing/testing_android.html">Testing Fundamentals</a>
 */
@RunWith(RobolectricGradleTestRunner.class)
// To use Robolectric you'll need to setup some constants.
// Change it according to your needs.
@Config(constants = BuildConfig.class, sdk = 21, manifest = "/src/main/AndroidManifest.xml")
public class TextUnitTest {
    @Test
    public void unique_SetUniqueName_ReturnsTrue() {
        Context c = RuntimeEnvironment.application;
        Global.getInstance().init(c);

    }
}