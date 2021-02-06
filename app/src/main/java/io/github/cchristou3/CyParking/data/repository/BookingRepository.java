package io.github.cchristou3.CyParking.data.repository;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.Query;

import org.jetbrains.annotations.NotNull;

import io.github.cchristou3.CyParking.data.model.parking.lot.ParkingLot;
import io.github.cchristou3.CyParking.data.model.parking.slot.booking.Booking;

/**
 * Purpose: <p>contain all methods to access the (cloud / local) database's booking node.</p>
 *
 * @author Charalambos Christou
 * @version 9.0 06/02/21
 */
public class BookingRepository implements DataSourceRepository.BookingHandler, DataSourceRepository.ParkingLotHandler {

    /**
     * Returns the bookings of the specified userId,
     * starting from the "Pending" ones and finishing with the "Completed" ones
     *
     * @param userId The is of the Firebase user
     * @return A query which returns all the bookings of the specified userId
     */
    @NotNull
    public Query getUserBookings(String userId) {
        return getBookingsRef()
                .whereEqualTo(BOOKING_USER_ID, userId)
                .whereEqualTo(BOOKING_DETAILS + "." + COMPLETED, false);
    }

    /**
     * An observer is attached to the current document, to listen for changes (number of available spaces).
     * Removal of observer is self-managed by the hosting activity.
     *
     * @param selectedParking The Parking whose changes will be listen to.
     * @return The {@link DocumentReference} reference to be observed.
     */
    @NotNull
    public DocumentReference getParkingLot(@NotNull ParkingLot selectedParking) {
        return getParkingLotsRef()
                .document(selectedParking.generateUniqueId());
    }

    /**
     * Stores the specified object to the database's PRIVATE_PARKING_BOOKING node.
     *
     * @param bookingToBeStored Holds all necessary info about a booking of a private parking
     * @return A Task<Void> object to be handled in the calling fragment.
     */
    @NotNull
    private Task<Void> bookParkingSlot(@NotNull Booking bookingToBeStored) {
        // Add the booking info to the database
        return getBookingsRef()
                .document(bookingToBeStored.generateUniqueId())
                .set(bookingToBeStored);
    }

    /**
     * Stores the specified object to the database's PRIVATE_PARKING_BOOKING node.
     * The user has the option to check whether or not to check of this booking has already been made.
     * If specified, the booking document is queried from the database based on the booking's id.
     * If a document is returned, then it already exists. Otherwise, if null was returned, it indicates
     * that the booking does not exist in the database.
     * For the latter, the booking gets then stored to the database.
     *
     * @param booking              Holds all necessary info about a booking of a private parking
     * @param checkIfAlreadyExists Indicates whether or not to check of this booking already exists in the database.
     * @return A Task<Void> object to be handled in the calling fragment.
     * @throws NullPointerException in case the continuation returns null
     * @see Task#getException()
     */
    public Task<Void> bookParkingSlot(@NotNull Booking booking, boolean checkIfAlreadyExists) {
        if (checkIfAlreadyExists) {
            return getBookingsRef()
                    .document(booking.generateUniqueId()).get()
                    .continueWithTask(task -> {
                        if (task.isSuccessful()) {
                            if (task.getResult().getData() == null) { // If it does not already exist
                                // Store to the database
                                return bookParkingSlot(booking);
                            }
                        }
                        return null; // This should throw a NullPointerException (to be handled by the View).
                    });
        } else {
            // Store to the database immediately
            return bookParkingSlot(booking);
        }
    }

    /**
     * Deletes the specified document using the document ID
     *
     * @param idOfBookingToBeCancelled The id of the document which we want to delete
     */
    public void cancelParkingBooking(@NotNull String idOfBookingToBeCancelled) {
        // Delete the booking info to the database
        getBookingsRef()
                .document(idOfBookingToBeCancelled).delete();
    }
}
