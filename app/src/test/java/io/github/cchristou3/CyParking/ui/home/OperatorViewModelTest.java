package io.github.cchristou3.CyParking.ui.home;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import io.github.cchristou3.CyParking.data.model.parking.lot.ParkingLot;

import static io.github.cchristou3.CyParking.ui.LiveDataTestUtil.getOrAwaitValue;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * Unit tests for the {@link OperatorViewModel} class.
 */
@RunWith(AndroidJUnit4.class)
public class OperatorViewModelTest {

    @Rule
    public InstantTaskExecutorRule instantExecutorRule = new InstantTaskExecutorRule();

    // Subject under test
    private OperatorViewModel operatorViewModel;

    @Before
    public void setUp() {
        operatorViewModel = new OperatorViewModel(new FakeOperatorRepository());
    }

    @Test
    public void updateLotState_setsNewLotState() throws InterruptedException {
        // When updating the state of the parking lot
        ParkingLot lot = new ParkingLot();
        operatorViewModel.updateLotState(lot);

        // Then, the the live data's value should be the one inputted
        assertThat(getOrAwaitValue(operatorViewModel.getParkingLotState()), is(lot));
    }

    @Test
    public void getParkingLotState_returnsNotNull() {
        // When getting the parking lot state
        assertThat(operatorViewModel.getParkingLotState(), not(nullValue()));
    }

    @Test
    public void observeParkingLot_inputValid_returnsNonNull() {
        assertThat(operatorViewModel.observeParkingLot("Id"), not(nullValue()));
    }

    @Test(expected = IllegalArgumentException.class)
    public void observeParkingLot_inputNull_throwsException() {
        assertThat(operatorViewModel.observeParkingLot(null), not(nullValue()));
    }

    @Test(expected = IllegalArgumentException.class)
    public void observeParkingLot_inputEmpty_throwsException() {
        assertThat(operatorViewModel.observeParkingLot(""), not(nullValue()));
    }
}