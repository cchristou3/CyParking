package io.github.cchristou3.CyParking.ui.views.host;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import static org.junit.Assert.assertNotNull;

@RunWith(RobolectricTestRunner.class)
@Config(sdk = Config.OLDEST_SDK)
public class MainHostActivityTest {

    // Subject under test
    private MainHostActivity activity;

    @Before
    public void setUp() {
        activity = Robolectric.buildActivity(MainHostActivity.class)
                .create()
                .resume()
                .get();
    }

    @Test
    public void shouldNotBeNull() {
        assertNotNull(activity);
    }

    @Test
    public void shouldHaveNavHostFragment() {
        assertNotNull(activity.getSupportFragmentManager().getFragments().get(0));
    }
}