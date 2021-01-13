package io.github.cchristou3.CyParking.data.repository.operator;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.Query;

import org.jetbrains.annotations.NotNull;

import io.github.cchristou3.CyParking.data.repository.ParkingMapRepository;

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

    private static final String OPERATOR_ID = "operatorId";
    private final String AVAILABLE_SPACES = "availability.availableSpaces";

    /**
     * Returns the operator's parking lot based on his/hers id.
     *
     * @param operatorId The id of the operator.
     * @return A query that returns the parking lot of the operator with the specified id.
     */
    @Override
    @NotNull
    public Query observeParkingLot(String operatorId) {
        return observeAllParkingLots() // TODO: 12/01/2021 Remove static method
                .whereEqualTo(OPERATOR_ID, operatorId).limit(1L);
    }

    /**
     * Increases the the number of available spaces of the current lot reference.
     *
     * @param lotReference A DocumentReference of the lot
     */
    @Override
    public void incrementAvailableSpacesOf(@NotNull final DocumentReference lotReference) {
        lotReference.update(AVAILABLE_SPACES, FieldValue.increment(1));
    }

    /**
     * Decreases the the number of available spaces of the current lot reference.
     *
     * @param lotReference A DocumentReference of the lot
     */
    @Override
    public void decrementAvailableSpacesOf(@NotNull final DocumentReference lotReference) {
        lotReference.update(AVAILABLE_SPACES, FieldValue.increment(-1));
    }
}
