package io.github.cchristou3.CyParking.data.repository;

import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.Query;
import com.google.firebase.storage.StorageReference;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import io.github.cchristou3.CyParking.data.interfaces.OperatorRepository;
import io.github.cchristou3.CyParking.data.model.parking.Parking;
import io.github.cchristou3.CyParking.data.model.parking.lot.ParkingLot;

import static io.github.cchristou3.CyParking.ui.views.parking.lots.map.ParkingMapFragment.TAG;

/**
 * Purpose: contain operator-related methods.
 * The concrete implementation of {@link OperatorRepository}.
 * Used by {@link io.github.cchristou3.CyParking.ui.views.home.OperatorViewModel}
 * for operator-role users.
 *
 * @author Charalambos Christou
 * @version 3.0 06/02/21
 */
public class DefaultOperatorRepository implements OperatorRepository,
        DataSourceRepository.ParkingLotHandler,
        DataSourceRepository.BookingHandler,
        DataSourceRepository.StorageHandler {

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
    public Task<Boolean> registerParkingLot(Uri selectedImageUri, @NotNull ParkingLot parkingLotToBeStored) {
        // Add the info to the database
        return checkIfAlreadyExists(parkingLotToBeStored)
                .continueWithTask(checkResultTask -> {
                    boolean lotAlreadyExist = checkResultTask.getResult();
                    if (lotAlreadyExist) {
                        return sendResponse(checkResultTask, false);
                    } else {
                        return checkResultTask // Upload the photo in Firebase Storage
                                .continueWithTask(task -> uploadPhoto(selectedImageUri))
                                // Add the lot to the database
                                .continueWithTask(uploadPhotoTask -> {
                                    if (uploadPhotoTask.isSuccessful()) {
                                        // Access the download URL
                                        Uri photoDownloadUri = uploadPhotoTask.getResult();

                                        // Set its photo url
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

    @NotNull
    @Contract("_, _ -> !null")
    private <T> Task<Boolean> sendResponse(@NotNull Task<T> checkResultTask, boolean isSuccessful) {
        return checkResultTask.continueWith(new Continuation<T, Boolean>() {
            @Override
            public Boolean then(@NonNull Task<T> task) throws Exception {
                return isSuccessful;
            }
        });
    }

    @NotNull
    @Contract("_ -> !null")
    private Task<Boolean> checkIfAlreadyExists(@NotNull ParkingLot parkingLotToBeStored) {
        return getParkingLotsRef()
                .document(parkingLotToBeStored.generateUniqueId())
                .get()
                .continueWith(checkIfAlreadyExistTask -> {
                    // If the task was successful, then the document already exists
                    // within the database.
                    if (checkIfAlreadyExistTask.isSuccessful()
                            && checkIfAlreadyExistTask.getResult().getData() != null) {
                        return true; // Do not do any more tasks - registration failed
                        // TODO: what if the task failed?
                    } else {
                        Log.d(TAG, "registerParkingLot: " + parkingLotToBeStored);
                        return false;
                    }
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
     * @see ParkingLot#generateUniqueId()
     * @see Task#getException()
     */
    @NotNull
    private Task<Void> registerParkingLot(@NotNull ParkingLot parkingLotToBeStored) {
        return getParkingLotsRef()
                .document(parkingLotToBeStored.generateUniqueId())
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
     * @return A {@link Task<Uri>} instance to be handled by the caller.
     */
    @Override
    public Task<Uri> uploadPhoto(@NotNull Uri selectedImageUri) {
        // Get a reference to store file at LOT_PHOTOS/<FILENAME>
        StorageReference photoRef = getLotPhotosStorageRef().child(selectedImageUri.getLastPathSegment());

        // Upload file to Firebase storage
        return photoRef.putFile(selectedImageUri) // upload photo task
                .continueWithTask(task -> {
                    if (!task.isSuccessful()) throw task.getException();

                    // Continue with the task to get the URL
                    return photoRef.getDownloadUrl();
                });
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
