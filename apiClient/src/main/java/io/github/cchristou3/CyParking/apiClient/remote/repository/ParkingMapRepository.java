package io.github.cchristou3.CyParking.apiClient.remote.repository;

import android.util.Log;

import com.google.firebase.functions.FirebaseFunctionsException;

import io.github.cchristou3.CyParking.apiClient.interfaces.HttpsCallHandler;

/**
 * Purpose: Provide methods to the ParkingMapViewModel
 * to access the database's parking lots.
 *
 * @author Charalambos Christou
 * @version 3.0 06/02/21
 */
public class ParkingMapRepository implements
        DataSourceRepository.ParkingLotHandler,
        DataSourceRepository.CloudFunctionCaller {

    private static final String TAG = ParkingMapRepository.class.getCanonicalName();

    /**
     * Sends an HTTPS request via a callable cloud-function.
     * The response contains all the parking lots
     * that are nearby the given coordinates
     *
     * @param userLatitude  The user's latest retrieved latitude.
     * @param userLongitude The user's latest retrieved longitude.
     * @param handler       The handler for the cloud function's HTTPS request.
     */
    public void fetchParkingLots(double userLatitude, double userLongitude, HttpsCallHandler handler) {
        callNearbyParkingLotsFunction(userLatitude, userLongitude)
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
    public void logError(Exception exception) {

        if (exception instanceof FirebaseFunctionsException) {
            FirebaseFunctionsException.Code code = ((FirebaseFunctionsException) exception).getCode();
            Object details = ((FirebaseFunctionsException) exception).getDetails();
            Log.e(TAG, "FirebaseFunctionsException error: code: "
                    + code + ", Details: " + details);
        }
        Log.d(TAG, exception.getClass() + ": " + exception);
    }
}
