package io.github.cchristou3.CyParking.ui.parking.slots.booking;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;

import org.jetbrains.annotations.NotNull;

import io.github.cchristou3.CyParking.data.model.parking.lot.ParkingLot;
import io.github.cchristou3.CyParking.data.model.parking.lot.SlotOffer;
import io.github.cchristou3.CyParking.data.model.parking.slot.booking.Booking;
import io.github.cchristou3.CyParking.data.repository.ParkingRepository;
import io.github.cchristou3.CyParking.utilities.Utility;

/**
 * <p>A ViewModel implementation, adopted to the {@link BookingFragment} fragment.
 * Purpose: Data persistence during orientation changes.</p>
 *
 * @author Charalambos Christou
 * @version 2.0 30/10/20
 */
public class BookingViewModel extends ViewModel {

    // Data members
    private final MutableLiveData<String> mPickedDate =
            new MutableLiveData<>(Utility.dateToString(Utility.getCurrentDate()));
    private final MutableLiveData<String> mPickedStartingTime =
            new MutableLiveData<>(Utility.getCurrentTime());
    private final MutableLiveData<SlotOffer> mPickedSlotOffer =
            new MutableLiveData<>();

    /**
     * Updates the value of {@link #mPickedSlotOffer} with the
     * specified one.
     *
     * @param newSlotOffer The latest selected slot offer.
     */
    public void updateSlotOffer(SlotOffer newSlotOffer) {
        mPickedSlotOffer.setValue(newSlotOffer);
    }

    /**
     * Updates the value of {@link #mPickedDate} with the
     * date formed by the specified arguments.
     *
     * @param selectedYear  The selected Year.
     * @param selectedMonth The selected Month.
     * @param selectedDay   The selected Day.
     */
    public void updatePickedDate(int selectedYear, int selectedMonth, int selectedDay) {
        mPickedDate.setValue(Utility.dateToString(selectedYear, selectedMonth, selectedDay));
    }

    /**
     * Getters for all its data members
     */
    public String getPickedDateValue() {
        return mPickedDate.getValue();
    }

    public String getPickedStartingTimeValue() {
        return mPickedStartingTime.getValue();
    }

    public SlotOffer getPickedSlotOfferValue() {
        return mPickedSlotOffer.getValue();
    }

    public MutableLiveData<String> getPickedDate() {
        return mPickedDate;
    }

    public MutableLiveData<String> getPickedStartingTime() {
        return mPickedStartingTime;
    }

    public MutableLiveData<SlotOffer> getPickedSlotOffer() {
        return mPickedSlotOffer;
    }

    /**
     * Returns a DocumentReference of the specified parking lot
     * in the database.
     *
     * @param selectedLot A parking lot object.
     * @return A reference of the specified lot in the database.
     */
    public DocumentReference observeParkingLotToBeBooked(ParkingLot selectedLot) {
        return ParkingRepository.observeParkingLot(selectedLot);
    }

    /**
     * Stores the specified {@link Booking} object
     * to the database.
     *
     * @param booking A {@link Booking} object.
     * @return A {@link Task} object to be handled by the view.
     */
    public Task<Void> bookParkingLot(@NotNull Booking booking) {
        return ParkingRepository.bookParkingSlot(booking, true);
    }
}
