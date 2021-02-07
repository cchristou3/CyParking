package io.github.cchristou3.CyParking.ui.views.parking.slots.booking;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewbinding.ViewBinding;

import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.timepicker.MaterialTimePicker;

import org.greenrobot.eventbus.EventBus;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.text.ParseException;
import java.util.Date;
import java.util.Objects;

import io.github.cchristou3.CyParking.R;
import io.github.cchristou3.CyParking.data.interfaces.Navigable;
import io.github.cchristou3.CyParking.data.manager.AlertBuilder;
import io.github.cchristou3.CyParking.data.manager.DatabaseObserver;
import io.github.cchristou3.CyParking.data.manager.DateTimePicker;
import io.github.cchristou3.CyParking.data.model.parking.lot.ParkingLot;
import io.github.cchristou3.CyParking.data.model.parking.lot.SlotOffer;
import io.github.cchristou3.CyParking.data.model.parking.slot.booking.Booking;
import io.github.cchristou3.CyParking.data.model.parking.slot.booking.BookingDetails;
import io.github.cchristou3.CyParking.data.model.user.LoggedInUser;
import io.github.cchristou3.CyParking.databinding.FragmentBookingBinding;
import io.github.cchristou3.CyParking.ui.components.CommonFragment;
import io.github.cchristou3.CyParking.ui.views.home.HomeFragment;
import io.github.cchristou3.CyParking.ui.views.host.GlobalStateViewModel;
import io.github.cchristou3.CyParking.ui.views.host.MainHostActivity;
import io.github.cchristou3.CyParking.ui.views.parking.slots.viewBooking.ViewBookingsFragment;
import io.github.cchristou3.CyParking.ui.views.user.account.AccountFragment;
import io.github.cchristou3.CyParking.ui.views.user.feedback.FeedbackFragment;
import io.github.cchristou3.CyParking.ui.views.user.login.AuthenticatorFragment;
import io.github.cchristou3.CyParking.utilities.DateTimeUtility;
import io.github.cchristou3.CyParking.utilities.ViewUtility;

/**
 * Purpose: <p>View parking details,
 * and book the specific parking for a specific date and time.</p>
 * <p>
 * <p>
 * In terms of Authentication, this is achieved by communicating with the hosting
 * activity {@link MainHostActivity} via the {@link GlobalStateViewModel}.
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
 * @version 8.0 28/01/21
 */
public class BookingFragment extends CommonFragment<FragmentBookingBinding> implements Navigable {

    // Fragment variables
    private static final String TAG = BookingFragment.class.getName() + "UniqueTag";
    private BookingViewModel mBookingViewModel;
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
     * @see CommonFragment#onCreateView(ViewBinding)
     */
    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return super.onCreateView(FragmentBookingBinding.inflate(inflater));
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

        initializeViewModel();

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
                        AlertBuilder.showSingleActionAlert(
                                getChildFragmentManager(),
                                R.string.no_lot_found_title,
                                R.string.no_lot_found_msg,
                                (v) -> goBack(requireActivity())
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
     *
     * @see CommonFragment#onDestroyView()
     */
    @Override
    public void onDestroyView() {
        super.removeOnClickListeners(
                getBinding().fragmentParkingBookingBtnStartingTimeButton,
                getBinding().fragmentParkingBookingBtnDateButton,
                getBinding().fragmentParkingBtnBookingButton
        );
        super.onDestroyView();
    }

    /**
     * Initializes the {@link #mBookingViewModel}
     * ViewModel of the fragment.
     */
    private void initializeViewModel() {
        mBookingViewModel = new ViewModelProvider(this,
                new BookingViewModelFactory()).get(BookingViewModel.class);
    }

    /**
     * Attaches observers to the picked date state, picked starting time state
     * and the user's state.
     */
    private void setViewModelObservers() {
        // Set up LiveData's Observers
        mBookingViewModel.getPickedDateState()
                .observe(getViewLifecycleOwner(), getBinding().fragmentParkingBookingTxtDate::setText);

        mBookingViewModel.getPickedStartingTimeState()
                .observe(getViewLifecycleOwner(),
                        time -> getBinding().fragmentParkingBookingTxtStartingTime.setText(time.toString()));

        // Observe the user's Auth state
        observeUserState(loggedInUser -> {
            // If the user logged out, prompt to either log in or to return to previous screen.
            if (loggedInUser == null) {
                AlertBuilder.promptUserToLogIn(getChildFragmentManager(), requireActivity(), this,
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
        final String parkingID = getString(R.string.lot_name) + mSelectedParking.getLotName(); // Compose parking name text
        final String availability = mSelectedParking.getLotAvailability(requireContext()); // Compose lot availability text
        getBinding().fragmentParkingBookingTxtParkingName.setText(parkingID); // Set parking name
        getBinding().fragmentParkingBookingTxtParkingAvailability.setText(availability); // Set parking availability

        setUpSlotOfferDropDownMenu(); // Initialize, attach listener to the drop down menu

        // Set the date and the start time to their corresponding TextView objects
        getBinding().fragmentParkingBookingTxtDate.setText(
                mBookingViewModel.getPickedDateState().getValue()
        );
        getBinding().fragmentParkingBookingTxtStartingTime.setText(
                mBookingViewModel.getPickedStartingTime().toString()
        );

        // Set up time pickers' listeners
        getBinding().fragmentParkingBookingBtnStartingTimeButton
                .setOnClickListener(buildTimePickerListener());

        // Set up date picker listener
        getBinding().fragmentParkingBookingBtnDateButton.setOnClickListener(v -> {
            DateTimePicker.getDatePicker(
                    mBookingViewModel::updatePickedDate // On date selected callback
            )
                    .show(getChildFragmentManager(), MaterialDatePicker.class.getCanonicalName());
        });

        // Set listener to "BOOK" button
        getBinding().fragmentParkingBtnBookingButton.setOnClickListener(v -> bookParking());
    }

    /**
     * Initialize the AutoCompleteTextView's values. Also, hook it up with an {@link AdapterView.OnItemSelectedListener}.
     * Whenever the listener gets triggered, update the value of ViewModel's slotOffer member
     * with the AutoCompleteTextView's value.
     */
    private void setUpSlotOfferDropDownMenu() {
        if (!(getBinding().fragmentParkingBookingDropDown.getEditText() instanceof
                AutoCompleteTextView)) {
            return; // should never happen as fragmentParkingBookingDropDown's direct child is an
            // AutoCompleteTextView
        }

        // Get a reference to it
        AutoCompleteTextView autoCompleteTextView =
                (AutoCompleteTextView) getBinding().fragmentParkingBookingDropDown.getEditText();

        autoCompleteTextView // Add an on item selected listener
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
        final SlotOffer[] slotOffers = SlotOffer.toArray(mSelectedParking.getSlotOfferList());
        SlotOffer.sortArray(slotOffers, true); // sort it in ascending order

        // Initialize an ArrayAdapter
        final ArrayAdapter<SlotOffer> slotOfferArrayAdapter = new ArrayAdapter<>(requireContext(),
                R.layout.slot_offer_drop_down_item,
                slotOffers);
        // bind the autoCompleteTextView with the above adapter
        autoCompleteTextView.setAdapter(slotOfferArrayAdapter);
        // Add an OnFocusChangeListener
        setDropDownMenuInitialFocus(autoCompleteTextView);
    }

    /**
     * Set an {@link android.view.View.OnFocusChangeListener} to the given
     * {@link AutoCompleteTextView} instance.
     * On-focus-changed: sets both the text of the {@link AutoCompleteTextView}
     * instance and the state's value, responsible for the slot offer,
     * with the first {@link SlotOffer} of the adapter's array.
     * purpose: ensures that a slot offer is picked, even if the user dismisses
     * the drop-down menu.
     *
     * @param autoCompleteTextView The {@link AutoCompleteTextView} instance to be attached the
     *                             onFocusChangeListener.
     */
    private void setDropDownMenuInitialFocus(@NotNull AutoCompleteTextView autoCompleteTextView) {
        autoCompleteTextView.setOnFocusChangeListener((v, hasFocus) -> {
            autoCompleteTextView.setText(
                    autoCompleteTextView.getAdapter().getItem(0).toString(), false
            );
            mBookingViewModel.updateSlotOffer(
                    (SlotOffer) autoCompleteTextView.getAdapter().getItem(0)
            );
            autoCompleteTextView.setOnFocusChangeListener(null);// remove listener
        });
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

        // TODO: 05/02/2021 Check time if the date is the same as the current date

        // Access parking operator's details via the Intent object
        final String pickedDate = mBookingViewModel.getPickedDate();
        Date pickedDateObject;
        try {
            pickedDateObject = DateTimeUtility.fromStringToDate(pickedDate);
        } catch (ParseException e) {
            // Display error message
            Toast.makeText(requireContext(), getString(R.string.parse_error_msg), Toast.LENGTH_SHORT).show();
            return; // Terminate the method
        }

        final LoggedInUser user = getUser();
        if (user == null) return; // If not logged in, exit the method

        getGlobalStateViewModel().showLoadingBar(); // show loading bar
        // Create a new Booking instance that will hold all data of the booking.
        final Booking booking = buildBooking(user, pickedDateObject);

        mBookingViewModel.bookParkingLot(booking)
                // Attach an onComplete listener to handle the task's result
                .addOnCompleteListener(task -> {
                    getGlobalStateViewModel().hideLoadingBar(); // hide loading bar
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
    private Booking buildBooking(@NotNull LoggedInUser user, Date pickedDate) {
        final String userID = user.getUserId();
        // coordinates parkingID operatorId lotName bookingUserId bookingDetails
        return new Booking(
                mSelectedParking.getParkingId(),
                mSelectedParking.getOperatorId(),
                mSelectedParking.getLotName(),
                userID,
                new BookingDetails(
                        pickedDate,
                        mBookingViewModel.getPickedStartingTime(),
                        mBookingViewModel.getPickedSlotOffer()
                )
        );
    }

    /**
     * Creates an OnClickListener to the given button.
     * OnClick: Creates a {@link MaterialTimePicker} with its own OnPositiveButtonClickListener.
     * OnPositiveButtonClickListener-onClick: Updates the value of the specified LiveData Object.
     *
     * @return An View.OnClickListener
     */
    @NotNull
    @Contract(pure = true)
    private View.OnClickListener buildTimePickerListener() {

        return v -> {
            DateTimePicker.getTimePicker(
                    requireContext(),
                    mBookingViewModel::updateStartingTime) // On time selected listener
                    // Display it
                    .show(getChildFragmentManager(), MaterialTimePicker.class.getCanonicalName());
        };
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