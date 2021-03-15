package io.github.cchristou3.CyParking.ui.views.parking.lots.map;

import android.view.View;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.FieldPath;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import io.github.cchristou3.CyParking.apiClient.interfaces.HttpsCallHandler;
import io.github.cchristou3.CyParking.apiClient.model.data.parking.lot.ParkingLot;
import io.github.cchristou3.CyParking.apiClient.remote.repository.ParkingMapRepository;
import io.github.cchristou3.CyParking.ui.components.ToastViewModel;

/**
 * <p>A ViewModel implementation, adopted to the ParkingMapFragment fragment.
 * Purpose: Data persistence during configuration changes.</p>
 *
 * @author Charalambos Christou
 * @version 2.0 06/03/21
 */
public class ParkingMapViewModel extends ToastViewModel {

    // Static constant
    private static final String TAG = ParkingMapViewModel.class.getName();
    // States of the parking map ViewModel
    private final MutableLiveData<List<DocumentChange>> mDocumentChangesState = new MutableLiveData<>();
    private final MutableLiveData<Set<String>> mDocumentIdsOfNearbyLots = new MutableLiveData<>();
    private final MutableLiveData<ParkingLot> mSelectedLotState = new MutableLiveData<>(null);
    private final MutableLiveData<Integer> mInfoLayoutState = new MutableLiveData<>(View.GONE);

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
        return mInfoLayoutState.getValue() == View.VISIBLE;
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
        int previousState = mInfoLayoutState.getValue();
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
     * @param userLatitude  The user's latest retrieved latitude.
     * @param userLongitude The user's latest retrieved longitude.
     * @param handler       The handler for the cloud function's HTTPS request.
     */
    public void fetchParkingLots(double userLatitude, double userLongitude,
                                 HttpsCallHandler handler) {
        mParkingMapRepository.fetchParkingLots(userLatitude, userLongitude, handler);
    }

    /**
     * Reports error to the logcat.
     *
     * @param exception The given exception.
     */
    private void logError(Exception exception) {
        mParkingMapRepository.logError(exception);
    }
}