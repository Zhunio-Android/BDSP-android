package org.bd_sp.bdsp;

import android.content.Context;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import java.util.Random;

import org.bd_sp.bdsp.exception.LocationManagerNullException;
import org.bd_sp.bdsp.service.GpsService;

import static junit.framework.Assert.fail;

/**
 * Created by jesse on 10/12/16.
 */
@RunWith(RobolectricGradleTestRunner.class)
// To use Robolectric you'll need to setup some constants.
// Change it according to your needs.
@Config(constants = BuildConfig.class, sdk = 21, manifest = "/src/main/AndroidManifest.xml")
public class GpsServiceUnitTest {
    Context c;
    GpsService gpsService;
    Random generator;
    @Before
    public void setup() {
        c = RuntimeEnvironment.application;
        gpsService = new GpsService();
    }
    @After
    public void cleanup() {

    }
    @Test
    public void GpsService_verifyLocationManagerIsNotNull_returnsTrue() {
        try {
            gpsService.startLocationUpdates();
        }
        catch(LocationManagerNullException e) {
            fail("LocationManagerNullException detected");
        }
    }
}
