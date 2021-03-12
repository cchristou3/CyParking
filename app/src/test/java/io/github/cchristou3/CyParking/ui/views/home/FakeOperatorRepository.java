package io.github.cchristou3.CyParking.ui.views.home;

import android.net.Uri;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.Query;

import org.jetbrains.annotations.NotNull;
import org.mockito.Mockito;

import io.github.cchristou3.CyParking.apiClient.model.parking.lot.ParkingLot;
import io.github.cchristou3.CyParking.apiClient.remote.repository.DefaultOperatorRepository;
import io.github.cchristou3.CyParking.apiClient.remote.repository.OperatorRepository;

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
     * @param selectedImageUri     The Uri of an image.
     * @param parkingLotToBeStored Stores all necessary info about the private parking
     * @return The task to be handled in the active fragment
     * @throws NullPointerException in case the continuation returns null
     * @see ParkingLot#generateUniqueId()
     * @see Task#getException()
     */
    @NotNull
    @Override
    public Task<Boolean> registerParkingLot(Uri selectedImageUri, @NotNull ParkingLot parkingLotToBeStored) {
        return null;
    }

    @NotNull
    @Override
    public Query getParkingLot(String operatorId) {
        return Mockito.mock(Query.class);
    }

    @Override
    public void incrementAvailableSpacesOf(@NotNull DocumentReference lotReference) {

    }

    @Override
    public void decrementAvailableSpacesOf(@NotNull DocumentReference lotReference) {

    }

    /**
     * The booking that has the given document id, gets its
     * completed attribute set to true (aka, completed).
     *
     * @param bookingDocId The document id of a booking
     */
    @Override
    public void updateBookingStatus(String bookingDocId) {

    }

    /**
     * Uploads the given file Uri to Firebase storage
     *
     * @param selectedImageUri The Uri of an image.
     * @return
     */
    @Override
    public Task<Uri> uploadPhoto(Uri selectedImageUri) {
        return null;
    }
}
