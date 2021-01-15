package io.github.cchristou3.CyParking.ui.parking.lots.map;

import android.view.View;

import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.google.firebase.firestore.CollectionReference;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;

import io.github.cchristou3.CyParking.data.model.parking.lot.ParkingLot;
import io.github.cchristou3.CyParking.data.repository.ParkingMapRepository;
import io.github.cchristou3.CyParking.ui.InstantTaskRuler;

import static io.github.cchristou3.CyParking.ui.LiveDataTestUtil.getOrAwaitValue;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.when;

/**
 * Unit tests for the {@link ParkingMapViewModel} class.
 */
@RunWith(AndroidJUnit4.class)
public class ParkingMapViewModelTest extends InstantTaskRuler {

    // Subject under test
    private ParkingMapViewModel parkingMapViewModel;

    @Before
    public void setUp() {
        ParkingMapRepository mockRepo = Mockito.mock(ParkingMapRepository.class);
        CollectionReference mockRef = Mockito.mock(CollectionReference.class);
        when(mockRepo.getParkingLotsNode()).thenReturn(mockRef);
        parkingMapViewModel = new ParkingMapViewModel(mockRepo);
    }

    @Test
    public void getSelectedLotState_returnsNonNull() {
        assertThat(parkingMapViewModel.getSelectedLotState(), not(nullValue()));
    }

    @Test
    public void updateSelectedLotState_setsNewValue() throws InterruptedException {
        // When the SelectedLotState's value gets changed
        parkingMapViewModel.updateSelectedLotState(new ParkingLot());
        // Then the state's value is no longer null
        assertThat(getOrAwaitValue(parkingMapViewModel.getSelectedLotState()), not(nullValue()));
    }

    @Test
    public void getInfoLayoutState_returnsNonNull() {
        assertThat(parkingMapViewModel.getInfoLayoutState(), not(nullValue()));
    }

    @Test
    public void getInfoLayoutState_returnsGone() throws InterruptedException {
        assertThat(getOrAwaitValue(parkingMapViewModel.getInfoLayoutState()), is(View.GONE));
    }

    @Test
    public void isInfoLayoutShown_returnsTrue() {
        // When the layout is showing
        parkingMapViewModel.showInfoLayout();
        // Then
        assertThat(parkingMapViewModel.isInfoLayoutShown(), is(true));
    }

    @Test
    public void isInfoLayoutShown_gone_returnsFalse() {
        // When the layout gets hidden
        parkingMapViewModel.hideInfoLayout();
        // Then, isInfoLayoutShown should return false
        assertThat(parkingMapViewModel.isInfoLayoutShown(), is(false));
    }

    @Test
    public void hideInfoLayout_gone_returnsGone() throws InterruptedException {
        // When the layout gets hidden
        parkingMapViewModel.hideInfoLayout();
        // Then, isInfoLayoutShown should return false
        assertThat(getOrAwaitValue(parkingMapViewModel.getInfoLayoutState()), is(View.GONE));
    }

    @Test
    public void hideInfoLayoutWithStateCheck_wasVisible_returnsTrue() {
        // Given the layout is showing
        parkingMapViewModel.showInfoLayout();
        // When the layout was showing
        boolean wasVisible = parkingMapViewModel.hideInfoLayoutWithStateCheck();
        // Then it should return true
        assertThat(wasVisible, is(true));
    }

    @Test
    public void hideInfoLayoutWithStateCheck_wasNotVisible_returnsFalse() {
        // Given the layout got hidden
        parkingMapViewModel.hideInfoLayout();
        // When the layout was not showing
        boolean wasVisible = parkingMapViewModel.hideInfoLayoutWithStateCheck();
        // Then it should return false
        assertThat(wasVisible, is(false));
    }

    @Test
    public void getParkingLots_returnsNonNull() {
        assertThat(parkingMapViewModel.getParkingLots(), is(not(nullValue())));
    }
}