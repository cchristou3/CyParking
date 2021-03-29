package io.github.cchristou3.CyParking.ui.views.home;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.util.Consumer;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.Query;
import com.google.zxing.integration.android.IntentResult;

import org.jetbrains.annotations.NotNull;

import io.github.cchristou3.CyParking.R;
import io.github.cchristou3.CyParking.apiClient.model.data.parking.lot.ParkingLot;
import io.github.cchristou3.CyParking.apiClient.model.data.parking.slot.booking.Booking;
import io.github.cchristou3.CyParking.apiClient.remote.repository.OperatorRepository;
import io.github.cchristou3.CyParking.data.manager.EncryptionManager;

/**
 * Purpose: <p>Data persistence when configuration changes.
 * Used when the operator is viewing/updating his/her parking lot.</p>
 *
 * @author Charalambos Christou
 * @version 5.0 27/03/21
 */
public class OperatorViewModel extends ViewModel {

    // Data member
    private final MutableLiveData<ParkingLot> mParkingLotState = new MutableLiveData<>();

    private final OperatorRepository mDefaultOperatorRepository;

    /**
     * Initialize the ViewModel's OperatorRepository instance
     * with the given argument.
     *
     * @param defaultOperatorRepository An OperatorRepository instance.
     */
    public OperatorViewModel(OperatorRepository defaultOperatorRepository) {
        this.mDefaultOperatorRepository = defaultOperatorRepository;
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
        if (operatorId.length() == 0) throw new IllegalArgumentException();
        return mDefaultOperatorRepository.getParkingLot(operatorId);
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

    /**
     * Sets the Booking's (document id) completed status to true and
     * decreases the lot's number of a available slots by one.
     *
     * @param lotReference The lot that the booking was issued for.
     * @param bookingDocId The document id of the booking.
     */
    public void receiveBooking(DocumentReference lotReference, String bookingDocId) {
        mDefaultOperatorRepository.updateBookingStatus(bookingDocId);
        incrementPersonCount(lotReference);
    }

    /**
     * Handle the QR Code scanner's result.
     *
     * @param qRCodeResult The QR Code's intent data if there are any.
     * @param lotReference The lot reference of the operator's parking lot.
     * @param displayToast A handler for displaying toast messages.
     */
    public void handleQRCodeScannerContents(@Nullable IntentResult qRCodeResult, DocumentReference lotReference, Consumer<Integer> displayToast) {
        if (lotReference == null || qRCodeResult == null)
            return; // Method was called without initiating a QR Code scan

        if (qRCodeResult.getContents() == null) { // User pressed the back button
            displayToast.accept(R.string.cancelled);
            return;
        }
        try {
            // Access the qr code's payload
            // Convert it to an array of bytes
            byte[] encodedMessageInBytes = EncryptionManager.hexStringToByteArray(qRCodeResult.getContents());
            // Decrypt it
            String decodedMessage = new EncryptionManager().decrypt(encodedMessageInBytes);
            receiveBooking(
                    // Access the previously stored lot reference
                    lotReference,
                    // Convert the string into a Booking object and access its unique id
                    Booking.toBooking(decodedMessage).generateDocumentId()
            );
            displayToast.accept(R.string.booking_completed);
        } catch (Exception ignored) {
            // Display a message in case decryption failed (malformed/corrupted/outside of the application's bounds message)
            displayToast.accept(R.string.invalid_qr_code);
        }
    }
}