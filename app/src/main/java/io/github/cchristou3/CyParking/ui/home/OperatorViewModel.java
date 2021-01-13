package io.github.cchristou3.CyParking.ui.home;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.Query;

import org.jetbrains.annotations.NotNull;

import io.github.cchristou3.CyParking.data.model.parking.lot.ParkingLot;
import io.github.cchristou3.CyParking.data.repository.operator.OperatorRepository;

/**
 * Purpose: <p>Data persistence when orientation changes.
 * Used when the operator is viewing/updating his/her parking lot.</p>
 *
 * @author Charalambos Christou
 * @version 2.0 12/01/21
 */
public class OperatorViewModel extends ViewModel {

    // Data member
    private final MutableLiveData<ParkingLot> mParkingLotState = new MutableLiveData<>();

    private final OperatorRepository mDefaultOperatorRepository;

    public OperatorViewModel(OperatorRepository mDefaultOperatorRepository) {
        this.mDefaultOperatorRepository = mDefaultOperatorRepository;
    }

    /**
     * Access the the parking lot state.
     *
     * @return A reference to the parking lot state.
     */
    public LiveData<ParkingLot> getParkingLotState() {
        return mParkingLotState;
    }

    /**
     * Updates the value of {@link #mParkingLotState}
     * with the given {@link ParkingLot} object.
     *
     * @param newState The new state of the lot.
     */
    public void updateLotState(ParkingLot newState) {
        mParkingLotState.setValue(newState);
    }

    /**
     * Returns a Query object that retrieves the parking lot of the operator
     * with the specified id.
     *
     * @param operatorId The id of the operator.
     * @return A query that retrieves the operator's parking lot from the database.
     */
    public Query observeParkingLot(@NonNull String operatorId) {
        //noinspection ConstantConditions
        if (operatorId == null || operatorId.length() == 0) throw new IllegalArgumentException();
        return mDefaultOperatorRepository.observeParkingLot(operatorId);
    }

    /**
     * Increments the person count by decreasing the lot's number of
     * available slots.
     *
     * @param lotReference A database reference of the lot.
     */
    public void incrementPersonCount(@NotNull final DocumentReference lotReference) {
        // One more person entered the parking lot, decrease the number of available spaces
        mDefaultOperatorRepository.decrementAvailableSpacesOf(lotReference);
    }

    /**
     * Decrements the person count by increasing the lot's number of
     * available slots.
     *
     * @param lotReference A database reference of the lot.
     */
    public void decrementPersonCount(@NotNull final DocumentReference lotReference) {
        // One person left the parking lot, increase the number of available spaces.
        mDefaultOperatorRepository.incrementAvailableSpacesOf(lotReference);
    }
}
