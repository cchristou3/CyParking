package io.github.cchristou3.CyParking.ui.home;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.Query;

import org.jetbrains.annotations.NotNull;

import io.github.cchristou3.CyParking.data.pojo.parking.lot.ParkingLot;
import io.github.cchristou3.CyParking.data.repository.ParkingRepository;

/**
 * Purpose: <p>Data persistence when orientation changes.
 * Used when the operator is viewing his/her parking lot.</p>
 *
 * @author Charalambos Christou
 * @version 1.0 25/12/20
 */
public class OperatorViewModel extends ViewModel {

    // Data member
    private final MutableLiveData<ParkingLot> parkingLotState = new MutableLiveData<>();

    /**
     * Access the the parking lot state.
     *
     * @return A reference to the parking lot state.
     */
    public MutableLiveData<ParkingLot> getParkingLotState() {
        return parkingLotState;
    }

    /**
     * Returns a Query object that retrieves the parking lot of the operator
     * with the specified email address.
     *
     * @param email The email address of the operator.
     * @return A query that retrieves the operator's parking lot from the database.
     */
    public Query observeParkingLot(String email) {
        return ParkingRepository.observeParkingLot(email);
    }

    /**
     * Increments the person count by decreasing the lot's number of
     * available slots.
     *
     * @param lotReference A database reference of the lot.
     */
    public void incrementPersonCount(@NotNull final DocumentReference lotReference) {
        // One more person entered the parking lot, decrease the number of available spaces
        ParkingRepository.decrementAvailableSpacesOf(lotReference);
    }

    /**
     * Decrements the person count by increasing the lot's number of
     * available slots.
     *
     * @param lotReference A database reference of the lot.
     */
    public void decrementPersonCount(@NotNull final DocumentReference lotReference) {
        // One person left the parking lot, increase the number of available spaces.
        ParkingRepository.incrementAvailableSpacesOf(lotReference);
    }
}
