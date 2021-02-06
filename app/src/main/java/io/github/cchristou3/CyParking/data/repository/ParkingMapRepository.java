package io.github.cchristou3.CyParking.data.repository;

import com.google.android.gms.tasks.Task;
import com.google.firebase.functions.HttpsCallableResult;

import io.github.cchristou3.CyParking.ui.views.parking.lots.map.ParkingMapViewModel;

/**
 * Purpose: Provide methods to the {@link ParkingMapViewModel}
 * to access the database's parking lots.
 *
 * @author Charalambos Christou
 * @version 3.0 06/02/21
 */
public class ParkingMapRepository implements
        DataSourceRepository.ParkingLotHandler,
        DataSourceRepository.CloudFunctionCaller {

    /**
     * Sends an HTTPS request via a callable cloud-function.
     * The response contains all the parking lots
     * that are nearby the given coordinates
     *
     * @param userLatitude  The user's latest retrieved latitude.
     * @param userLongitude The user's latest retrieved longitude.
     */
    public Task<HttpsCallableResult> fetchParkingLots(double userLatitude, double userLongitude) {
        return callNearbyParkingLotsFunction(userLatitude, userLongitude);
    }
}
