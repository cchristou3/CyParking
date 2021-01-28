package io.github.cchristou3.CyParking.data.repository;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.functions.FirebaseFunctions;
import com.google.firebase.functions.HttpsCallableResult;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;

import io.github.cchristou3.CyParking.ui.views.parking.lots.map.ParkingMapViewModel;

import static io.github.cchristou3.CyParking.data.repository.RepositoryData.GET_NEARBY_PARKING_LOTS;
import static io.github.cchristou3.CyParking.data.repository.RepositoryData.LATITUDE;
import static io.github.cchristou3.CyParking.data.repository.RepositoryData.LONGITUDE;
import static io.github.cchristou3.CyParking.data.repository.RepositoryData.PARKING_LOTS;

/**
 * Purpose: Provide methods to the {@link ParkingMapViewModel}
 * to access the database's parking lots.
 *
 * @author Charalambos Christou
 * @version 2.0 21/01/21
 */
public class ParkingMapRepository {

    /**
     * Hold a {@link CollectionReference} of the parking lots node
     * from the database.
     *
     * @return The {@link CollectionReference} reference to be observed.
     */
    @NotNull
    public CollectionReference getParkingLots() {
        return FirebaseFirestore.getInstance().collection(PARKING_LOTS);
    }

    /**
     * Sends an HTTPS request via a callable cloud-function.
     * The response contains all the parking lots
     * that are nearby the given coordinates
     *
     * @param userLatitude  The user's latest retrieved latitude.
     * @param userLongitude The user's latest retrieved longitude.
     */
    public Task<HttpsCallableResult> fetchParkingLots(double userLatitude, double userLongitude) {
        return FirebaseFunctions.getInstance()
                .getHttpsCallable(GET_NEARBY_PARKING_LOTS)
                .call(new HashMap<String, Double>() {{ // The request's data.
                    put(LATITUDE, userLatitude);
                    put(LONGITUDE, userLongitude);
                }});
    }
}
