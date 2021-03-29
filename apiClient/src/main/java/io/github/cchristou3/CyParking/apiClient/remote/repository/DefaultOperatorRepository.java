package io.github.cchristou3.CyParking.apiClient.remote.repository;

import android.net.Uri;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.Query;
import com.google.firebase.storage.StorageReference;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import io.github.cchristou3.CyParking.apiClient.model.data.parking.lot.ParkingLot;

/**
 * Purpose: contain operator-related methods.
 * The concrete implementation of {@link OperatorRepository}.
 * Used by OperatorViewModel
 * for operator-role users.
 *
 * @author Charalambos Christou
 * @version 4.0 26/03/21
 */
public class DefaultOperatorRepository implements OperatorRepository,
        DataSourceRepository.ParkingLotHandler,
        DataSourceRepository.BookingHandler,
        DataSourceRepository.StorageHandler {

    private static final String TAG = DefaultOperatorRepository.class.getCanonicalName();

    /**
     * Stores to the database's PRIVATE_PARKING node the specified object.
     * The document id used corresponds to the merge of the ParkingLot object's
     * coordinates and the given operator mobile number.
     *
     * @param parkingLotToBeStored Stores all necessary info about the private parking
     * @return The task to be handled in the active fragment
     * @throws NullPointerException in case the continuation returns null
     * @see ParkingLot#generateDocumentId()
     * @see Task#getException()
     */
    @NotNull
    @Override
    public Task<Boolean> registerParkingLot(Uri selectedImageUri, @NotNull ParkingLot parkingLotToBeStored) {
        // Add the info to the database
        return checkIfAlreadyExists(parkingLotToBeStored)
                .continueWithTask(checkResultTask -> {
                    boolean lotAlreadyExist = checkResultTask.getResult() != null && checkResultTask.getResult();
                    if (lotAlreadyExist) {
                        return sendResponse(checkResultTask, false);
                    } else {
                        return checkResultTask // Upload the photo in Firebase Storage
                                .continueWithTask(task -> uploadPhoto(selectedImageUri, parkingLotToBeStored.getOperatorId()))
                                // Add the lot to the database
                                .continueWithTask(uploadPhotoTask -> {
                                    if (uploadPhotoTask.isSuccessful()) {
                                        // Access the download URL
                                        Uri photoDownloadUri = uploadPhotoTask.getResult();

                                        // Set its photo url
                                        if (photoDownloadUri != null)
                                            parkingLotToBeStored.setLotPhotoUrl(photoDownloadUri.toString());

                                        // Store to database
                                        return sendResponse(registerParkingLot(parkingLotToBeStored), true);

                                    }
                                    // Should never reach to this point
                                    return null;
                                });
                    }
                });

    }

    /**
     * Encapsulate code for creating boolean {@link Continuation}'s.
     *
     * @param checkResultTask The task responsible for checking whether a lot already exists
     *                        with the same information.
     * @param isSuccessful    True if it already exists. Otherwise false.
     * @param <T>             The task result.
     * @return A Task of type Boolean based on the given flag.
     */
    @NotNull
    @Contract("_, _ -> !null")
    private <T> Task<Boolean> sendResponse(@NotNull Task<T> checkResultTask, boolean isSuccessful) {
        return checkResultTask.continueWith(task -> isSuccessful);
    }

    /**
     * Check whether the given parking lot object already exists in the database.
     *
     * @param parkingLotToBeStored A parking lot instance.
     * @return A Task of type Boolean based on the above condition.
     */
    @NotNull
    @Contract("_ -> !null")
    private Task<Boolean> checkIfAlreadyExists(@NotNull ParkingLot parkingLotToBeStored) {
        return getParkingLotsRef()
                .document(parkingLotToBeStored.generateDocumentId())
                .get()
                .continueWith(checkIfAlreadyExistTask -> {
                    // If the task was successful, then the document already exists
                    // within the database.
                    return (checkIfAlreadyExistTask.isSuccessful()
                            && checkIfAlreadyExistTask.getResult() != null
                            && checkIfAlreadyExistTask.getResult().getData() != null);
                });
    }

    /**
     * Stores to the database's PRIVATE_PARKING node the specified object.
     * The document id used corresponds to the merge of the ParkingLot object's
     * coordinates and the given operator mobile number.
     *
     * @param parkingLotToBeStored Stores all necessary info about the private parking
     * @return The task to be handled in the active fragment
     * @throws NullPointerException in case the continuation returns null
     * @see ParkingLot#generateDocumentId()
     * @see Task#getException()
     */
    @NotNull
    private Task<Void> registerParkingLot(@NotNull ParkingLot parkingLotToBeStored) {
        return getParkingLotsRef()
                .document(parkingLotToBeStored.generateDocumentId())
                .set(parkingLotToBeStored);
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
        return getParkingLotsRef()
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
     * The booking that has the given document id, gets its
     * completed attribute set to true (aka, completed).
     *
     * @param bookingDocId The document id of a booking
     */
    @Override
    public void updateBookingStatus(String bookingDocId) {
        getBookingsRef().document(bookingDocId)
                .update(BOOKING_DETAILS + "." + COMPLETED, true);
    }

    /**
     * Uploads the given file Uri to Firebase storage
     *
     * @param selectedImageUri The Uri of an image.
     * @param operatorId       The id of the operator.
     * @return A {@link Task<Uri>} instance to be handled by the caller.
     */
    @Override
    public Task<Uri> uploadPhoto(@NotNull Uri selectedImageUri, String operatorId) {
        // Get a reference to store file at LOT_PHOTOS/<OPERATOR_ID>:<FILENAME>
        StorageReference photoRef = getLotPhotosStorageRef().child(operatorId + ":" + selectedImageUri.getLastPathSegment());

        // Upload file to Firebase storage
        return photoRef.putFile(selectedImageUri) // upload photo task
                .continueWithTask(task -> {
                    if (!task.isSuccessful() && task.getException() != null)
                        throw task.getException();

                    // Continue with the task to get the URL
                    return photoRef.getDownloadUrl();
                });
    }

    /**
     * Retrieve the booking that corresponds to the given document id.
     *
     * @param bookingId the document id of a booking
     * @return A task with the booking with there was one.
     */
    @Override
    public DocumentReference getBooking(String bookingId) {
        return getBookingsRef().document(bookingId);
    }
}
