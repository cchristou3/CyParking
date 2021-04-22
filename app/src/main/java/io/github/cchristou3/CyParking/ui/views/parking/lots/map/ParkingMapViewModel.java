package io.github.cchristou3.CyParking.ui.views.parking.lots.map;

import android.util.Log;
import android.view.View;

import androidx.core.util.Consumer;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.FieldPath;
import com.google.firebase.firestore.Query;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import io.github.cchristou3.CyParking.R;
import io.github.cchristou3.CyParking.apiClient.interfaces.HttpsCallHandler;
import io.github.cchristou3.CyParking.apiClient.model.data.parking.lot.ParkingLot;
import io.github.cchristou3.CyParking.apiClient.model.data.user.LoggedInUser;
import io.github.cchristou3.CyParking.apiClient.remote.repository.ParkingMapRepository;
import io.github.cchristou3.CyParking.ui.components.SingleLiveEvent;
import io.github.cchristou3.CyParking.ui.views.parking.slots.booking.BookingFragment;

/**
 * <p>A ViewModel implementation, adopted to the ParkingMapFragment fragment.
 * Purpose: Data persistence during configuration changes.</p>
 *
 * @author Charalambos Christou
 * @version 5.0 27/03/21
 */
public class ParkingMapViewModel extends ViewModel {

    // Static constant
    private static final String TAG = ParkingMapViewModel.class.getName();
    // States of the parking map ViewModel
    private final MutableLiveData<List<DocumentChange>> mDocumentChangesState = new MutableLiveData<>();
    private final MutableLiveData<Set<String>> mDocumentIdsOfNearbyLots = new MutableLiveData<>();
    private final MutableLiveData<ParkingLot> mSelectedLotState = new MutableLiveData<>(null);
    private final MutableLiveData<Integer> mInfoLayoutState = new MutableLiveData<>(View.GONE);
    private final MutableLiveData<Object> mPromptUser = new SingleLiveEvent<>();
    private final MutableLiveData<ParkingLot> mNavigateToBooking = new SingleLiveEvent<>();

    // Its repository
    private final ParkingMapRepository mParkingMapRepository;

    /**
     * Initialize the ViewModel's ParkingMapRepository instance
     * with the given argument.
     *
     * @param parkingMapRepository An ParkingMapRepository instance.
     */
    public ParkingMapViewModel(ParkingMapRepository parkingMapRepository) {
        this.mParkingMapRepository = parkingMapRepository;
    }

    /**
     * Access the {@link #mNavigateToBooking}.
     *
     * @return A reference to {@link #mNavigateToBooking}.
     */
    public LiveData<ParkingLot> getNavigationToBookingState() {
        return mNavigateToBooking;
    }

    /**
     * Access the {@link #mDocumentChangesState}.
     *
     * @return A reference to {@link #mDocumentChangesState}.
     */
    public LiveData<Object> getPromptingState() {
        return mPromptUser;
    }

    /**
     * Access the {@link #mDocumentChangesState}.
     *
     * @return A reference to {@link #mDocumentChangesState}.
     */
    public LiveData<List<DocumentChange>> getDocumentChangesState() {
        return mDocumentChangesState;
    }

    /**
     * Updates the value of {@link #mDocumentChangesState}
     * with the given argument.
     */
    public void updateDocumentState(List<DocumentChange> documentChanges) {
        mDocumentChangesState.setValue(documentChanges);
    }

    /**
     * Updates the value of {@link #mDocumentIdsOfNearbyLots}
     * with the given argument.
     */
    public void updateIdsState(String[] ids) {
        mDocumentIdsOfNearbyLots.setValue(new HashSet<>(Arrays.asList(ids)));
    }

    /**
     * Access the {@link #mDocumentIdsOfNearbyLots}.
     *
     * @return A reference to {@link #mDocumentIdsOfNearbyLots}.
     */
    public LiveData<Set<String>> getDocumentIdsOfNearbyLots() {
        return mDocumentIdsOfNearbyLots;
    }

    /**
     * Checks whether the ids of nearby parking lots have been already fetched.
     *
     * @return True, if {@link #mDocumentIdsOfNearbyLots} is not null. Otherwise, false.
     */
    public boolean didPreviouslyRetrieveDocumentIds() {
        return mDocumentIdsOfNearbyLots.getValue() != null;
    }

    ///////////////////////////////////////////////////////////////////////////
    // Methods related to the selected Lot State
    ///////////////////////////////////////////////////////////////////////////

    /**
     * Access the {@link #mSelectedLotState} {@link MutableLiveData}
     * instance.
     *
     * @return A reference to  {@link #mSelectedLotState}.
     */
    public MutableLiveData<ParkingLot> getSelectedLotState() {
        return mSelectedLotState;
    }

    /**
     * Updates the state of the {@link #mSelectedLotState}
     * to the specified state.
     *
     * @param lot The new value of {@link #mSelectedLotState}.
     */
    public void updateSelectedLotState(ParkingLot lot) {
        mSelectedLotState.setValue(lot);
    }

    ///////////////////////////////////////////////////////////////////////////
    // Methods related to the  infoLayout state
    ///////////////////////////////////////////////////////////////////////////

    /**
     * Access the {@link #mInfoLayoutState} {@link MutableLiveData}
     * instance.
     *
     * @return A reference to  {@link #mInfoLayoutState}.
     */
    public MutableLiveData<Integer> getInfoLayoutState() {
        return mInfoLayoutState;
    }

    /**
     * Checks whether the the {@link #mInfoLayoutState} is in
     * {@link View#VISIBLE} state.
     *
     * @return True, if the state is set to {@link View#VISIBLE}. Otherwise, false.
     */
    public boolean isInfoLayoutShown() {
        // If null then it is still hidden
        return mInfoLayoutState.getValue() != null && mInfoLayoutState.getValue() == View.VISIBLE;
    }

    /**
     * Sets the visibility state of the {@link #mInfoLayoutState} to
     * {@link View#VISIBLE}.
     */
    public void showInfoLayout() {
        updateInfoLayoutStateTo(View.VISIBLE);
    }

    /**
     * Sets the visibility state of the {@link #mInfoLayoutState} to
     * {@link View#GONE}.
     */
    public void hideInfoLayout() {
        updateInfoLayoutStateTo(View.GONE);
    }

    /**
     * Updates the state of the {@link #mInfoLayoutState}
     * to the specified state.
     *
     * @param visibilityState The new value of {@link #mInfoLayoutState}.
     */
    private void updateInfoLayoutStateTo(int visibilityState) {
        mInfoLayoutState.setValue(visibilityState);
    }

    /**
     * Sets the visibility state of the {@link #mInfoLayoutState} to
     * {@link View#GONE}.
     *
     * @return If the {@link #mInfoLayoutState} was already in that state before,
     * true is returned. Otherwise, false.
     */
    public boolean hideInfoLayoutWithStateCheck() {
        // If not set then it is still hidden
        int previousState = mInfoLayoutState.getValue() != null ? mInfoLayoutState.getValue() : View.GONE;
        updateInfoLayoutStateTo(View.GONE);
        return previousState == View.VISIBLE;
    }

    ///////////////////////////////////////////////////////////////////////////
    // APIs -  fetching the parking lots from the database via
    // Firebase Cloud Functions / FirebaseFirestore API
    ///////////////////////////////////////////////////////////////////////////

    /**
     * Retrieves all the parking lots from the database via the
     * {@link com.google.firebase.firestore.FirebaseFirestore} API.
     *
     * @return The collection reference that contains all the parking lots
     * in the database.
     */
    public Query getParkingLots(Set<String> ids) {
        return mParkingMapRepository.getParkingLotsRef()
                .whereIn(FieldPath.documentId(), new ArrayList<>(ids));
    }

    /**
     * Sends an HTTPS request via a callable cloud-function.
     * The response contains all the parking lots
     * that are nearby the given coordinates
     *
     * @param userLatitude   The user's latest retrieved latitude.
     * @param userLongitude  The user's latest retrieved longitude.
     * @param hideLoadingBar A runnable responsible for hiding the current displaying loading bar.
     */
    public void fetchParkingLots(double userLatitude, double userLongitude,
                                 Runnable hideLoadingBar) {
        mParkingMapRepository.fetchParkingLots(userLatitude, userLongitude, new HttpsCallHandler() {
            @Override
            public void onSuccess(String rawJsonResponse) {
                Log.d(TAG, "Before parsing: " + rawJsonResponse);
                // Convert json object into an array of ParkingLot objects and update the Doc Ids state
                updateIdsState(new Gson().fromJson(rawJsonResponse, String[].class));
            }

            @Override
            public void onFailure(Exception exception) {
                if (exception == null) return;
                // If there are no document ids on the cache then prompt the user
                // about his/her internet connection
                if (shouldPromptUser()) {
                    promptUser();
                }
            }

            @Override
            public void onComplete() {
                hideLoadingBar.run();
            }
        });
    }

    /**
     * Inform the {@link #mPromptUser}'s observes that it is time
     * to prompt the user.
     */
    public void promptUser() {
        mPromptUser.setValue(null);
    }

    /**
     * Access the value of {@link #mDocumentIdsOfNearbyLots}.
     *
     * @return the value of {@link #mDocumentIdsOfNearbyLots}.
     */
    public Set<String> getCurrentParkingLotDocIds() {
        return getDocumentIdsOfNearbyLots().getValue();
    }

    /**
     * Check whether the user should be prompted.
     *
     * @return True if either the cached parking document ids are null or
     * when its list is empty.
     */
    private boolean shouldPromptUser() {
        return getCurrentParkingLotDocIds() == null || getCurrentParkingLotDocIds().isEmpty();
    }

    /**
     * If the user is not logged in, a Toast message is displayed.
     * Otherwise, the user is navigated to {@link BookingFragment}.
     *
     * @param currentUser  the current instance of {@link LoggedInUser}.
     * @param selectedLot  the selected parking lot.
     * @param displayToast A handler for displaying toast messages.
     */
    public void navigateToBookingScreen(LoggedInUser currentUser, ParkingLot selectedLot, Consumer<Integer> displayToast) {
        // If the user is not logged in, display a Toast msg
        if (currentUser == null) {
            displayToast.accept(R.string.no_booking_allowed_to_non_logged_in_users);
            return;
        }
        if (selectedLot != null) {
            // Navigate to the Booking Fragment
            mNavigateToBooking.setValue(selectedLot);
        } else { // Otherwise, display a message
            displayToast.accept(R.string.unknown_error);
        }
    }
}