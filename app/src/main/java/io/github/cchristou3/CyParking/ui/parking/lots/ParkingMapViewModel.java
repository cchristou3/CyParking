package io.github.cchristou3.CyParking.ui.parking.lots;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import androidx.lifecycle.ViewModel;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.firestore.CollectionReference;

import io.github.cchristou3.CyParking.R;
import io.github.cchristou3.CyParking.data.repository.ParkingRepository;

import static io.github.cchristou3.CyParking.ui.parking.lots.ParkingMapFragment.TAG;

/**
 * <p>A ViewModel implementation, adopted to the ParkingMapFragment fragment.
 * Purpose: Data persistence during orientation changes.</p>
 *
 * @author Charalambos Christou
 * @version 1.0 29/12/20
 */
public class ParkingMapViewModel extends ViewModel {

    // TODO: Add state for infoLayout
    // TODO: Add state for parking lot list

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
