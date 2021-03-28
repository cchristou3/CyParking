package io.github.cchristou3.CyParking.apiClient.remote.repository;

import android.net.Uri;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.Query;

import org.jetbrains.annotations.NotNull;

import io.github.cchristou3.CyParking.apiClient.model.data.parking.lot.ParkingLot;

/**
 * Purpose: provide the OperatorViewModel
 * functionality related to the operator's tasks.
 *
 * @author Charalambos Christou
 * @version 2.0 26/03/2021
 * @see DefaultOperatorRepository
 */
public interface OperatorRepository {

    /**
     * Stores to the database's PRIVATE_PARKING node the specified object.
     * The document id used corresponds to the merge of the ParkingLot object's
     * coordinates and the given operator mobile number.
     *
     * @param selectedImageUri     The Uri of an image.
     * @param parkingLotToBeStored Stores all necessary info about the private parking
     * @return The task to be handled in the active fragment
     * @throws NullPointerException in case the continuation returns null
     * @see ParkingLot#generateDocumentId()
     * @see Task#getException()
     */
    @NotNull
    Task<Boolean> registerParkingLot(Uri selectedImageUri, @NotNull ParkingLot parkingLotToBeStored);

    /**
     * Returns the operator's parking lot based on his/hers id.
     *
     * @param operatorId The id of the operator.
     * @return A query that returns the parking lot of the operator with the specified id.
     */
    @NotNull
    Query getParkingLot(String operatorId);

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

    /**
     * The booking that has the given document id, gets its
     * completed attribute set to true (aka, completed).
     *
     * @param bookingDocId The document id of a booking
     */
    void updateBookingStatus(String bookingDocId);

    /**
     * Uploads the given file Uri to Firebase storage
     *
     * @param selectedImageUri The Uri of an image.
     * @param operatorId       the uid of the operator.
     * @return A {@link Task} object to be handled by the caller.
     */
    Task<Uri> uploadPhoto(Uri selectedImageUri, String operatorId);
}
