package io.github.cchristou3.CyParking.data.repository.operator;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.Query;

import org.jetbrains.annotations.NotNull;

/**
 * Purpose: provide the {@link io.github.cchristou3.CyParking.ui.home.OperatorViewModel}
 * functionality related to the operator's tasks.
 *
 * @author Charalambos Christou
 * @version 12/01/2021
 */
public interface OperatorRepository {

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
