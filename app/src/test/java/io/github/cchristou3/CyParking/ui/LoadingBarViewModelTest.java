package io.github.cchristou3.CyParking.ui;

import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import io.github.cchristou3.CyParking.ui.components.LoadingBarViewModel;

import static io.github.cchristou3.CyParking.ui.LiveDataTestUtil.getOrAwaitValue;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * Unit tests for the {@link LoadingBarViewModel} class.
 */
@RunWith(AndroidJUnit4.class)
public class LoadingBarViewModelTest extends InstantTaskRuler {

    LoadingBarViewModel loadingBarViewModel; // Subject under test

    @Before
    public void setUp() {
        loadingBarViewModel = new LoadingBarViewModel();
    }

    @Test
    public void showLoadingBar_setsValueToTrue() throws InterruptedException {
        // When showLoadingBar gets invoked
        loadingBarViewModel.showLoadingBar();
        // Then its value should be set to true
        assertThat(getOrAwaitValue(loadingBarViewModel.getLoadingBarState()), is(true));
        assertThat(loadingBarViewModel.isLoadingBarShowing(), is(true));
    }

    @Test
    public void hideLoadingBar_setsValueToFalse() throws InterruptedException {
        // When showLoadingBar gets invoked
        loadingBarViewModel.hideLoadingBar();
        // Then its value should be set to true
        assertThat(getOrAwaitValue(loadingBarViewModel.getLoadingBarState()), is(false));
        assertThat(loadingBarViewModel.isLoadingBarShowing(), is(false));
    }
}