package io.github.cchristou3.CyParking.ui.parking.slots.booking;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.snackbar.Snackbar;

import org.greenrobot.eventbus.EventBus;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.Objects;

import io.github.cchristou3.CyParking.R;
import io.github.cchristou3.CyParking.data.interfaces.Navigable;
import io.github.cchristou3.CyParking.data.manager.AlertBuilder;
import io.github.cchristou3.CyParking.data.manager.DatabaseObserver;
import io.github.cchristou3.CyParking.data.model.parking.lot.ParkingLot;
import io.github.cchristou3.CyParking.data.model.parking.lot.SlotOffer;
import io.github.cchristou3.CyParking.data.model.parking.slot.booking.Booking;
import io.github.cchristou3.CyParking.data.model.parking.slot.booking.BookingDetails;
import io.github.cchristou3.CyParking.data.model.user.LoggedInUser;
import io.github.cchristou3.CyParking.databinding.FragmentParkingBookingBinding;
import io.github.cchristou3.CyParking.ui.home.HomeFragment;
import io.github.cchristou3.CyParking.ui.host.AuthStateViewModel;
import io.github.cchristou3.CyParking.ui.host.MainHostActivity;
import io.github.cchristou3.CyParking.ui.parking.slots.viewBooking.ViewBookingsFragment;
import io.github.cchristou3.CyParking.ui.user.account.AccountFragment;
import io.github.cchristou3.CyParking.ui.user.feedback.FeedbackFragment;
import io.github.cchristou3.CyParking.ui.user.login.AuthenticatorFragment;
import io.github.cchristou3.CyParking.ui.widgets.TimePickerDialog;
import io.github.cchristou3.CyParking.utilities.Utility;
import io.github.cchristou3.CyParking.utilities.ViewUtility;

import static io.github.cchristou3.CyParking.ui.parking.lots.map.ParkingMapFragment.TAG;

/**
 * Purpose: <p>View parking details,
 * and book the specific parking for a specific date and time.</p>
 * <p>
 * <p>
 * In terms of Authentication, this is achieved by communicating with the hosting
 * activity {@link MainHostActivity} via the {@link AuthStateViewModel}.
 * </p>
 * <p>
 * TODO: - choose a payment method
 * - generate QR code
 * - Change "end date" to duration. -> Simpler validation
 * -> Update {@link Booking}
 * - Add a Livedata form - validation for the date
 * -> if not valid show error to getBinding().fragmentParkingBookingTxtDate
 *
 * @author Charalambos Christou
 * @version 5.0 30/12/20
 */
public class BookingFragment extends Fragment implements Navigable {

    // Fragment variables
    private AuthStateViewModel mAuthStateViewModel;
    private BookingViewModel mBookingViewModel;
    private FragmentParkingBookingBinding mFragmentParkingBookingBinding;
    private ParkingLot mSelectedParking;

    /**
     * Initialises the fragment. Uses the EventBus, to get access to data send by the previous fragment.
     *
     * @param savedInstanceState A bundle which contains info about previously stored data
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            mSelectedParking = Objects.requireNonNull(EventBus.getDefault().getStickyEvent(ParkingLot.class));
        } catch (ClassCastException | NullPointerException e) {
            Log.e(TAG, "onCreateView: ", e); // TODO: Plan B
        }
    }

    /**
     * Inflates our fragment's view. Saves a reference to the fragment's view
     *
     * @param inflater           Inflater which will inflate our view
     * @param container          The parent view
     * @param savedInstanceState A bundle which contains info about previously stored data
     * @return The view of the fragment
     */
    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Create an instance of the binding class for the fragment to use.
        mFragmentParkingBookingBinding = FragmentParkingBookingBinding.inflate(getLayoutInflater());
        // Return the root view from the onCreateView() method to make it the active view on the screen.
        return mFragmentParkingBookingBinding.getRoot();
    }

    /**
     * Invoked at the completion of onCreateView. Initializes fragment's ViewModel and Ui.
     * Lastly, it attaches observers to our LiveData instances.
     *
     * @param view               The view of the fragment
     * @param savedInstanceState A bundle which contains info about previously stored data
     */
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initializeViewModels();

        initializeUi();

        setViewModelObservers();
    }

    /**
     * An observer is attached to the current document, to listen for changes (number of available spaces).
     * Removal of observer is self-managed by the hosting activity.
     */
    @Override
    public void onStart() {
        super.onStart();
        DatabaseObserver.createDocumentReferenceObserver(
                mBookingViewModel.observeParkingLotToBeBooked(mSelectedParking), // The Document Reference
                (value, error) -> { // The Event Listener
                    Log.d(TAG, "Error: " + error);
                    Log.d(TAG, "Value: " + value);
                    if (error != null || value == null)
                        return;
                    final ParkingLot lot = value.toObject(ParkingLot.class);
                    Log.d(TAG, "Lot: " + lot);
                    if (lot == null) {
                        AlertBuilder.showAlert(
                                requireContext(),
                                R.string.no_lot_found_title,
                                R.string.no_lot_found_msg,
                                android.R.string.ok,
                                (dialog, which) -> goBack(requireActivity())
                        );
                        Log.d(TAG, "lot == null");
                        return;
                    }

                    final String availability = lot.getLotAvailability(requireContext());
                    Log.d(TAG, "availability: " + availability);
                    // Animate color to display a lot availability change to the user
                    ViewUtility.animateAvailabilityColorChanges(
                            getBinding().fragmentParkingBookingCvParkingAvailability,
                            getBinding().fragmentParkingBookingTxtParkingAvailability,
                            lot.getAvailableSpaces(),
                            mSelectedParking.getAvailableSpaces());

                    // Update the text of the availability
                    getBinding().fragmentParkingBookingTxtParkingAvailability.setText(availability);
                    mSelectedParking = lot; // Save a reference to the updated lot
                })
                .registerLifecycleObserver(getLifecycle());
    }

    /**
     * Called when the view previously created by {@link #onCreateView} has
     * been detached from the fragment.
     */
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        getBinding().fragmentParkingBookingBtnStartingTimeButton.setOnClickListener(null);
        getBinding().fragmentParkingBookingBtnDateButton.setOnClickListener(null);
        getBinding().fragmentParkingBtnBookingButton.setOnClickListener(null);
        mFragmentParkingBookingBinding = null; // Ready to get CGed
    }

    /**
     * Initializes both {@link #mAuthStateViewModel} and {@link #mBookingViewModel}
     * ViewModels of the fragment.
     */
    private void initializeViewModels() {
        // Instantiate the fragment's ViewModels
        mAuthStateViewModel = new ViewModelProvider(requireActivity())  // Access the same instance as its hosting activity
                .get(AuthStateViewModel.class);
        mBookingViewModel = new ViewModelProvider(this,
                new BookingViewModelFactory()).get(BookingViewModel.class);
    }

    /**
     * Attaches observers to the picked date state, picked starting time state
     * and the user's state.
     */
    private void setViewModelObservers() {
        // Set up LiveData's Observers
        mBookingViewModel
                .getPickedDate().observe(getViewLifecycleOwner(), getBinding().fragmentParkingBookingTxtDate::setText);
        mBookingViewModel
                .getPickedStartingTime().observe(getViewLifecycleOwner(), getBinding().fragmentParkingBookingTxtStartingTime::setText);

        // Observe the users Auth state
        mAuthStateViewModel.getUserState().observe(getViewLifecycleOwner(), loggedInUser -> {
            // If the user logged out, prompt to either log in or to return to previous screen.
            if (loggedInUser == null) {
                AlertBuilder.promptUserToLogIn(requireContext(), requireActivity(), this,
                        R.string.logout_book_parking_screen_msg);
            }
        });
    }

    /**
     * Sets the initial values of the Ui with the values of {@link #mSelectedParking}
     * and attach listeners to booking button (to book a slot),
     * the slot offer spinner (to choose the offer for the booking),
     * "choose date" button (to choose the booking's date),
     * and "start time" button (to choose the start time of the booking).
     */
    private void initializeUi() {
        // Set their text to their corresponding value
        final String parkingID = "ParkingID: " + mSelectedParking.getParkingID(); // Compose parking id text
        final String availability = mSelectedParking.getLotAvailability(requireContext()); // Compose lot availability text
        getBinding().fragmentParkingBookingTxtParkingName.setText(parkingID); // Set parking name
        getBinding().fragmentParkingBookingTxtParkingAvailability.setText(availability); // Set parking availability

        setUpSlotOfferSpinner(); // Initialize, attach listener to the spinner

        // Set the date and the start time to their corresponding TextView objects
        getBinding().fragmentParkingBookingTxtDate.setText(mBookingViewModel.getPickedDate().getValue());
        getBinding().fragmentParkingBookingTxtStartingTime.setText(mBookingViewModel.getPickedStartingTime().getValue());

        // Set up time pickers' listeners
        getBinding().fragmentParkingBookingBtnStartingTimeButton
                .setOnClickListener(buildListenerForTimePicker());

        // Set up date picker listener
        getBinding().fragmentParkingBookingBtnDateButton.setOnClickListener(v -> {
            // Access the current date via the a Calendar object
            final int currentYear = Calendar.getInstance().get(Calendar.YEAR);
            final int currentMonth = Calendar.getInstance().get(Calendar.MONTH);
            final int currentDay = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);

            // Instantiate a DatePickerDialog and how it to the user
            // The dialog's date will be set to the current date.
            new DatePickerDialog(requireContext(),
                    (viewObject, selectedYear, selectedMonth, selectedDayOfMonth) ->
                            mBookingViewModel.updatePickedDate(selectedYear, selectedMonth, selectedDayOfMonth),
                    // The date to show when the user is prompt the date picker - current date
                    currentYear, currentMonth, currentDay)
                    .show();
        });

        // Set listener to "BOOK" button
        getBinding().fragmentParkingBtnBookingButton.setOnClickListener(v -> bookParking());
    }

    /**
     * Initialize the Spinner's values. Also, hook it up with an {@link AdapterView.OnItemSelectedListener}.
     * Whenever the listener gets triggered, update the value of ViewModel's slotOffer member
     * with the spinner's value.
     */
    private void setUpSlotOfferSpinner() {
        getBinding().fragmentParkingBookingSSlotOffer
                .setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        // Cast the selected object to a SlotOffer object
                        final SlotOffer selectedOffer = (SlotOffer) parent.getItemAtPosition(position);
                        // Update the ViewModel's corresponding LiveData object.
                        Log.d(TAG, "onItemSelected: " + selectedOffer);
                        mBookingViewModel.updateSlotOffer(selectedOffer);
                    }

                    public void onNothingSelected(AdapterView<?> parent) { /* ignore */ }
                });

        // Create an array that will hold all the values of the spinner - the lot's slot offers.
        int numOfOffers = mSelectedParking.getSlotOfferList().size();
        final SlotOffer[] slotOffers = new SlotOffer[numOfOffers];
        mSelectedParking.getSlotOfferList().toArray(slotOffers);
        // Initialize an ArrayAdapter
        final ArrayAdapter<SlotOffer> volumeAdapter = new ArrayAdapter<>(requireContext(),
                android.R.layout.simple_spinner_dropdown_item,
                slotOffers);
        // Bind the spinner with its adapter
        getBinding().fragmentParkingBookingSSlotOffer.setAdapter(volumeAdapter);
    }

    /**
     * Validates the user's selected date and the user's current log in status.
     * If they are valid, a booking object is created and stored in the database.
     * If the user has already booked the lot with the same details, an error is displayed
     * instead.
     */
    public void bookParking() {
        // Check whether there are available spaces
        if (mSelectedParking.getAvailableSpaces() == 0) { // if not
            // Display message to user and terminate the function.
            Toast.makeText(requireContext(), getString(R.string.no_space_msg), Toast.LENGTH_SHORT).show();
            return;
        }
        // Access parking operator's details via the Intent object
        final String pickedDate = mBookingViewModel.getPickedDateValue();
        Date pickedDateObject;
        try {
            pickedDateObject = Utility.fromStringToDate(pickedDate);
        } catch (ParseException e) {
            // Display error message
            Toast.makeText(requireContext(), getString(R.string.parse_error_msg), Toast.LENGTH_SHORT).show();
            return; // Terminate the method
        }
        // Access the current date and compare it with the inputted one.
        if (pickedDateObject.compareTo(Utility.getCurrentDate()) >= 0) {// Date is larger or equal than today's date)
            final LoggedInUser user = mAuthStateViewModel.getUserState().getValue();
            if (user == null) return; // If not logged in, exit the method
            // .
            changeLoadingBarVisibilityTo(View.VISIBLE);// show loading bar
            // Otherwise, proceed with transaction and create a booking
            // Create a new Booking instance that will hold all data of the booking.
            final Booking booking = buildBookingObject(user, pickedDateObject);

            mBookingViewModel.bookParkingLot(booking)
                    .addOnCompleteListener(task -> {
                        changeLoadingBarVisibilityTo(View.GONE);// hide loading bar
                        // Inform the user booking was successful and offer a temporary UNDO option
                        if (task.isSuccessful() && task.getException() == null) {
                            // TODO: Generate QR Code
                            // Display undo option
                            Snackbar.make(requireView(), getString(R.string.booking_success), Snackbar.LENGTH_LONG)
                                    .setAction(R.string.undo,
                                            v -> mBookingViewModel.cancelBooking(booking.generateUniqueId())).show();
                            // Navigate one screen back
                            goBack(requireActivity());
                            return;
                        }
                        // Otherwise, display an error message to the user
                        Toast.makeText(requireContext(), getString(R.string.slot_already_booked), Toast.LENGTH_SHORT).show();
                    });
        } else {
            Toast.makeText(requireContext(), getString(R.string.date_error_msg), Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Changes the visibility of the loading bar to the specified value.
     *
     * @param visibility The new visibility status of the loading bar.
     */
    private void changeLoadingBarVisibilityTo(int visibility) {
        getBinding().fragmentParkingClpbLoadingBar.setVisibility(visibility);
    }

    /**
     * Gather all information needed to create a Booking instance
     * based on the user's input and return it.
     *
     * @param user       The current LoggedInUser instance.
     * @param pickedDate The current date.
     * @return A Booking instance.
     */
    @NotNull
    @Contract("_, _ -> new")
    private Booking buildBookingObject(@NotNull LoggedInUser user, Date pickedDate) {
        final String userID = user.getUserId();
        // coordinates parkingID operatorId lotName bookingUserId bookingDetails
        return new Booking(
                mSelectedParking.getCoordinates(),
                mSelectedParking.getParkingID(),
                mSelectedParking.getOperatorId(),
                mSelectedParking.getLotName(),
                userID,
                new BookingDetails(
                        pickedDate,
                        mBookingViewModel.getPickedStartingTimeValue(),
                        mBookingViewModel.getPickedSlotOfferValue()
                )
        );
    }

    /**
     * Creates an OnClickListener to the given button.
     * OnClick: Creates a TimePickerDialog with its own OnTimeSetListener.
     * OnTimeSetListener-onClick: Updates the value of the specified LiveData Object.
     *
     * @return An View.OnClickListener
     */
    @NotNull
    @Contract(pure = true)
    private View.OnClickListener buildListenerForTimePicker() {
        return v -> {
            TimePickerDialog timePickerDialog = new TimePickerDialog(
                    requireContext(),
                    (view, hourOfDay, minute) -> mBookingViewModel.updateStartingTime(hourOfDay, minute)
            );
            timePickerDialog.show();
        };
    }

    /**
     * Access the {@link #mFragmentParkingBookingBinding}.
     *
     * @return A reference to {@link #mFragmentParkingBookingBinding}.
     */
    private FragmentParkingBookingBinding getBinding() {
        return mFragmentParkingBookingBinding;
    }

    /**
     * Navigates from the current Fragment subclass to the
     * {@link AuthenticatorFragment}.
     *
     * @see #bookParking()
     */
    @Override
    public void toAuthenticator() {
        // The user must be logged in to be in this fragment (Booking screen).
        // However, the method will be needed, in case the user logs out while being in this screen.
        getNavController(requireActivity())
                .navigate(R.id.action_nav_parking_booking_fragment_to_nav_authenticator_fragment);
    }

    /**
     * Navigates from the current Fragment subclass to the
     * {@link ViewBookingsFragment}.
     */
    @Override
    public void toBookings() {
        getNavController(requireActivity())
                .navigate(R.id.action_nav_parking_booking_fragment_to_nav_view_bookings);
    }

    /**
     * Navigates from the current Fragment subclass to the
     * {@link AccountFragment}.
     */
    @Override
    public void toAccount() {
        getNavController(requireActivity())
                .navigate(R.id.action_nav_parking_booking_fragment_to_nav_account);
    }

    /**
     * Navigates from the current Fragment subclass to the
     * {@link FeedbackFragment}.
     */
    @Override
    public void toFeedback() {
        getNavController(requireActivity())
                .navigate(R.id.action_nav_parking_booking_fragment_to_nav_feedback);
    }

    /**
     * Navigates from the current Fragment subclass to the
     * {@link HomeFragment}.
     */
    @Override
    public void toHome() {
        getNavController(requireActivity())
                .navigate(R.id.action_nav_parking_booking_fragment_to_nav_home);
    }
}