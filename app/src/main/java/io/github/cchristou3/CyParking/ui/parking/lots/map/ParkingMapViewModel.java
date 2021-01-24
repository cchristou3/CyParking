package io.github.cchristou3.CyParking.ui.parking.lots.map;

import android.util.Log;
import android.view.View;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.FieldPath;
import com.google.firebase.firestore.Query;
import com.google.firebase.functions.FirebaseFunctionsException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import io.github.cchristou3.CyParking.data.interfaces.HttpsCallHandler;
import io.github.cchristou3.CyParking.data.model.parking.lot.ParkingLot;
import io.github.cchristou3.CyParking.data.repository.ParkingMapRepository;

/**
 * <p>A ViewModel implementation, adopted to the ParkingMapFragment fragment.
 * Purpose: Data persistence during orientation changes.</p>
 *
 * @author Charalambos Christou
 * @version 1.0 29/12/20
 */
public class ParkingMapViewModel extends ViewModel {

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
     * to the specified state. If the current state of {@link #mInfoLayoutState}
     * is the same with the specified attribute then it gets ignored.
     *
     * @param visibilityState The new value of {@link #mInfoLayoutState}.
     */
    private void updateInfoLayoutStateTo(int visibilityState) {
        if (mInfoLayoutState.getValue() != visibilityState) {
            mInfoLayoutState.setValue(visibilityState);
        }
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
        return mParkingMapRepository.getParkingLots()
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
        mParkingMapRepository.fetchParkingLots(userLatitude, userLongitude)
                .addOnCompleteListener(task -> {
                    handler.onComplete();
                    if (task.isSuccessful() && task.getResult().getData() != null) {
                        handler.onSuccess(task.getResult().getData().toString());
                    } else {
                        handler.onFailure(task.getException());
                        logError(task.getException());
                    }
                });
    }

    /**
     * Reports error to the logcat.
     *
     * @param exception The given exception.
     */
    private void logError(Exception exception) {
        if (exception instanceof FirebaseFunctionsException) {
            FirebaseFunctionsException.Code code = ((FirebaseFunctionsException) exception).getCode();
            Object details = ((FirebaseFunctionsException) exception).getDetails();
            Log.e(TAG, "FirebaseFunctionsException error: code: "
                    + code + ", Details: " + details);
        }
        Log.d(TAG, exception.getClass() + ": " + exception);
    }
}
