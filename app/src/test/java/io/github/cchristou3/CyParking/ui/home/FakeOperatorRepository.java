package io.github.cchristou3.CyParking.ui.home;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.Query;

import org.jetbrains.annotations.NotNull;
import org.mockito.Mockito;

import io.github.cchristou3.CyParking.data.interfaces.OperatorRepository;
import io.github.cchristou3.CyParking.data.model.parking.lot.ParkingLot;
import io.github.cchristou3.CyParking.data.repository.DefaultOperatorRepository;

/**
 * Mock repository to substitute {@link DefaultOperatorRepository}
 * in {@link OperatorViewModelTest}.
 */
public class FakeOperatorRepository implements OperatorRepository {

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
        return null;
    }

    @NotNull
    @Override
    public Query observeParkingLot(String operatorId) {
        return Mockito.mock(Query.class);
    }

    @Override
    public void incrementAvailableSpacesOf(@NotNull DocumentReference lotReference) {

    }

    @Override
    public void decrementAvailableSpacesOf(@NotNull DocumentReference lotReference) {

    }
}
