package io.github.cchristou3.CyParking.data.repository;

import android.util.Log;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import io.github.cchristou3.CyParking.data.interfaces.OperatorRepository;
import io.github.cchristou3.CyParking.data.model.parking.Parking;
import io.github.cchristou3.CyParking.data.model.parking.lot.ParkingLot;

import static io.github.cchristou3.CyParking.data.repository.RepositoryData.PARKING_LOTS;
import static io.github.cchristou3.CyParking.ui.parking.lots.map.ParkingMapFragment.TAG;

/**
 * Purpose: contain operator-related methods.
 * The concrete implementation of {@link OperatorRepository}.
 * Used by {@link io.github.cchristou3.CyParking.ui.home.OperatorViewModel}
 * for operator-role users.
 *
 * @author Charalambos Christou
 * @version 2.0 13/01/21
 */
public class DefaultOperatorRepository extends ParkingMapRepository implements OperatorRepository {

    private static final String AVAILABILITY = "availability";
    private static final String OPERATOR_ID = "operatorId";
    private final String AVAILABLE_SPACES = "availableSpaces";

    /**
     * Stores to the database's PRIVATE_PARKING node the specified object.
     * The document id used corresponds to the merge of the ParkingLot object's
     * coordinates and the given operator mobile number.
     *
     * @param parkingLotToBeStored Stores all necessary info about the private parking
     * @return The task to be handled in the active fragment
     * @throws NullPointerException in case the continuation returns null
     * @see ParkingLot#generateUniqueId()
     * @see Task#getException()
     */
    @NotNull
    @Override
    public Task<Void> registerParkingLot(@NotNull ParkingLot parkingLotToBeStored) {
        // Add the info to the database
        return getParkingLots()
                .document(parkingLotToBeStored.generateUniqueId())
                .get()
                .continueWithTask(task -> {
                    // If the task was successful, then the document already exists
                    // within the database.
                    if (task.isSuccessful() && task.getResult().getData() != null) {
                        return null; // Do not do any more tasks
                        // Returning null will result into a NullPointerException("Continuation returned null")
                        // As the continueWithTask method cannot return null.
                        // Thus, in fragment check for this kind of exception and handle it appropriately
                        // TODO: what if the task failed?
                    } else {
                        Log.d(TAG, "registerParkingLot: " + parkingLotToBeStored);
                        // Add it to the database
                        return FirebaseFirestore.getInstance()
                                .collection(PARKING_LOTS)
                                .document(parkingLotToBeStored.generateUniqueId())
                                .set(parkingLotToBeStored);
                    }
                });
    }

    /**
     * Returns the operator's parking lot based on his/hers id.
     *
     * @param operatorId The id of the operator.
     * @return A query that returns the parking lot of the operator with the specified id.
     */
    @Override
    @NotNull
    public Query getParkingLot(String operatorId) {
        return getParkingLots()
                .whereEqualTo(OPERATOR_ID, operatorId).limit(1L);
    }

    /**
     * Increases the the number of available spaces of the current lot reference.
     *
     * @param lotReference A DocumentReference of the lot
     */
    @Override
    public void incrementAvailableSpacesOf(@NotNull final DocumentReference lotReference) {
        lotReference.update(AVAILABILITY + "." + AVAILABLE_SPACES, FieldValue.increment(1));
    }

    /**
     * Decreases the the number of available spaces of the current lot reference.
     *
     * @param lotReference A DocumentReference of the lot
     */
    @Override
    public void decrementAvailableSpacesOf(@NotNull final DocumentReference lotReference) {
        lotReference.update(AVAILABILITY + "." + AVAILABLE_SPACES, FieldValue.increment(-1));
    }

    /**
     * Adds hard-coded data to the firebase's PRIVATE_PARKING node.
     * Used for testing.
     */
    public void addDummyParkingData() {
        List<ParkingLot> parkingLotList = new ArrayList<>(Arrays.asList(
                new ParkingLot(new Parking.Coordinates(34.9214056, 33.621935),
                        "99999999", "A@gmail.com", "A name"),
                new ParkingLot(new Parking.Coordinates(34.9214672,
                        33.6227833), "88888888", "B@gmail.com", "Another name"),
                new ParkingLot(new Parking.Coordinates(34.9210801,
                        33.6236309), "77777777", "C@gmail.com", "And another name"),
                new ParkingLot(new Parking.Coordinates(34.921800,
                        33.623560), "66666666", "D@gmail.com", "A name again"))
        );
        for (ParkingLot parking : parkingLotList) {
            registerParkingLot(parking);
        }
    }
}
