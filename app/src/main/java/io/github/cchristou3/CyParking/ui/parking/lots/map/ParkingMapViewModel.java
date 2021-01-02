package io.github.cchristou3.CyParking.ui.parking.lots.map;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.firestore.CollectionReference;

import io.github.cchristou3.CyParking.R;
import io.github.cchristou3.CyParking.data.model.parking.lot.ParkingLot;
import io.github.cchristou3.CyParking.data.repository.ParkingRepository;

import static io.github.cchristou3.CyParking.ui.parking.lots.map.ParkingMapFragment.TAG;

/**
 * <p>A ViewModel implementation, adopted to the ParkingMapFragment fragment.
 * Purpose: Data persistence during orientation changes.</p>
 *
 * @author Charalambos Christou
 * @version 1.0 29/12/20
 */
public class ParkingMapViewModel extends ViewModel {

    private final MutableLiveData<ParkingLot> mSelectedLotState =
            new MutableLiveData<>(null);

    private final MutableLiveData<Integer> mInfoLayoutState =
            new MutableLiveData<>(View.GONE);

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
    // HTTP (Volley) / FirebaseFirestore API
    ///////////////////////////////////////////////////////////////////////////

    /**
     * Retrieves all the parking lots from the database via the
     * {@link com.google.firebase.firestore.FirebaseFirestore} API.
     *
     * @return The collection reference that contains all the parking lots
     * in the database.
     */
    public CollectionReference getParkingLots() {
        return ParkingRepository.observeAllParkingLots();
    }

    /**
     * Using Volley (REST) - triggers cloud function.
     *
     * @param context             The context of the current visible fragment.
     * @param userLatitude        The user's latest retrieved latitude.
     * @param userLongitude       The user's latest retrieved longitude.
     * @param responseHandler     The handler for the HTTP request.
     * @param onFinishedUiHandler The handler for the completion of the HTTP request.
     */
    public void fetchParkingLots(Context context, double userLatitude, double userLongitude,
                                 Response.Listener<String> responseHandler,
                                 RequestQueue.RequestFinishedListener<String> onFinishedUiHandler) {
        final RequestQueue requestQueue = Volley.newRequestQueue(context);
        final Request<String> requestForParkingLots = new StringRequest(Request.Method.GET,
                context.getString(R.string.firestore_api_url) + "?latitude=" + userLatitude + "&longitude=" + userLongitude,
                responseHandler,
                error -> {// No wifi?
                    // TODO: inform the user
                    // Plan B: Reload map
                    Toast.makeText(context, "Unexpected error occurred!\n" + error.getMessage(), Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "Volley error: " + error.getMessage());
                });

        requestQueue.add(requestForParkingLots); // Add the request to the queue
        // Add a Request Finished Listener to handle the Ui and the clean up
        requestQueue.addRequestFinishedListener(new RequestQueue.RequestFinishedListener<String>() {
            @Override
            public void onRequestFinished(Request<String> request) {
                // Update Ui
                onFinishedUiHandler.onRequestFinished(request);
                // Clean up both the listeners
                requestQueue.removeRequestFinishedListener(onFinishedUiHandler);
                requestQueue.removeRequestFinishedListener(this);
            }
        });
    }
}
