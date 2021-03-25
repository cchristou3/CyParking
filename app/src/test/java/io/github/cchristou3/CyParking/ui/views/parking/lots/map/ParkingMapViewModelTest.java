package io.github.cchristou3.CyParking.ui.views.parking.lots.map;

import android.view.View;

import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import io.github.cchristou3.CyParking.R;
import io.github.cchristou3.CyParking.apiClient.model.data.parking.lot.ParkingLot;
import io.github.cchristou3.CyParking.apiClient.model.data.user.LoggedInUser;
import io.github.cchristou3.CyParking.apiClient.remote.repository.ParkingMapRepository;
import io.github.cchristou3.CyParking.ui.InstantTaskRuler;

import static io.github.cchristou3.CyParking.ui.LiveDataTestUtil.getOrAwaitValue;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertTrue;
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
        when(mockRepo.getParkingLotsRef()).thenReturn(mockRef);
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

    @Test(expected = NullPointerException.class)
    public void getParkingLots_nullSet_throwsException() {
        parkingMapViewModel.getParkingLots(null);
    }

    @Test
    public void updateDocumentState_setsNewValue() throws InterruptedException {
        // Given the View got new DocumentChange instances
        int amount = 3;
        DocumentChange documentChange = Mockito.mock(DocumentChange.class);
        List<DocumentChange> changes = new ArrayList<>();
        for (int i = 0; i < amount; i++) {
            changes.add(documentChange);
        }
        // when the view updates the livedata via updateDocumentState
        parkingMapViewModel.updateDocumentState(changes);
        // Then calling getDocumentChangesState should return the same value
        assertThat(getOrAwaitValue(parkingMapViewModel.getDocumentChangesState()),
                is(changes));
        for (int i = 0; i < amount; i++) {
            assertThat(getOrAwaitValue(parkingMapViewModel.getDocumentChangesState())
                            .get(i),
                    is(documentChange));
        }
    }

    @Test
    public void updateIdsState_setsNewValue() throws InterruptedException {
        // Given the fragment received new document ids
        int amount = 3;
        String[] ids = new String[amount];
        Arrays.asList("1", "2", "3").toArray(ids);
        // When the the owner calls updateIdsState
        parkingMapViewModel.updateIdsState(ids);
        // Then calling getDocumentIdsOfNearbyLots should return the same value but as a HashSet
        assertThat(getOrAwaitValue(parkingMapViewModel.getDocumentIdsOfNearbyLots()),
                is(new HashSet<>(Arrays.asList(ids))));
        for (int i = 1; i <= amount; i++) {
            assertTrue(getOrAwaitValue(parkingMapViewModel.getDocumentIdsOfNearbyLots())
                    .contains(Integer.toString(i)));
        }
    }

    @Test
    public void didPreviouslyRetrieveDocumentIds_returnsExpectedResult() throws InterruptedException {
        parkingMapViewModel.updateIdsState(new String[]{});
        getOrAwaitValue(parkingMapViewModel.getDocumentIdsOfNearbyLots());
        assertThat(parkingMapViewModel.didPreviouslyRetrieveDocumentIds(), is(true));
    }

    @Test
    public void promptUser_SetsNullValue() throws InterruptedException {
        parkingMapViewModel.promptUser();
        assertThat(getOrAwaitValue(parkingMapViewModel.getPromptingState()), is(nullValue()));
    }

    @Test
    public void navigateToBookingScreen_nullUserNullLot() throws InterruptedException {
        parkingMapViewModel.navigateToBookingScreen(null, null);
        assertThat(getOrAwaitValue(parkingMapViewModel.getToastMessage()), is(R.string.no_booking_allowed_to_non_logged_in_users));
    }

    @Test
    public void navigateToBookingScreen_nonNullUserNullLot() throws InterruptedException {
        parkingMapViewModel.navigateToBookingScreen(new LoggedInUser(), null);
        assertThat(getOrAwaitValue(parkingMapViewModel.getToastMessage()), is(R.string.unknown_error));
    }

    @Test
    public void navigateToBookingScreen_nonNullUserNonNullLot() throws InterruptedException {
        final ParkingLot lot = new ParkingLot();
        parkingMapViewModel.navigateToBookingScreen(new LoggedInUser(), lot);
        assertThat(getOrAwaitValue(parkingMapViewModel.getNavigationToBookingState()), is(lot));
    }
}