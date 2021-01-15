package io.github.cchristou3.CyParking.data.interfaces;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.Query;

import org.jetbrains.annotations.NotNull;

import io.github.cchristou3.CyParking.data.model.parking.lot.ParkingLot;

/**
 * Purpose: provide the {@link io.github.cchristou3.CyParking.ui.home.OperatorViewModel}
 * functionality related to the operator's tasks.
 *
 * @author Charalambos Christou
 * @version 12/01/2021
 */
public interface OperatorRepository {

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
    Task<Void> registerParkingLot(@NotNull ParkingLot parkingLotToBeStored);

    /**
     * Returns the operator's parking lot based on his/hers id.
     *
     * @param operatorId The id of the operator.
     * @return A query that returns the parking lot of the operator with the specified id.
     */
    @NotNull
    Query observeParkingLot(String operatorId);

    /**
     * Increases the the number of available spaces of the current lot reference.
     *
     * @param lotReference A DocumentReference of the lot
     */
    void incrementAvailableSpacesOf(@NotNull DocumentReference lotReference);

    /**
     * Decreases the the number of available spaces of the current lot reference.
     *
     * @param lotReference A DocumentReference of the lot
     */
    void decrementAvailableSpacesOf(@NotNull DocumentReference lotReference);
}
