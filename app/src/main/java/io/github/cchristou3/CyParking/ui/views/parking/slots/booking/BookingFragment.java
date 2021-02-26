package io.github.cchristou3.CyParking.ui.views.parking.slots.booking;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.ListAdapter;
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
import io.github.cchristou3.CyParking.data.manager.DatabaseObserver;
import io.github.cchristou3.CyParking.data.manager.EncryptionManager;
import io.github.cchristou3.CyParking.data.model.parking.lot.ParkingLot;
import io.github.cchristou3.CyParking.data.model.parking.lot.SlotOffer;
import io.github.cchristou3.CyParking.data.model.parking.slot.booking.Booking;
import io.github.cchristou3.CyParking.data.model.parking.slot.booking.BookingDetails;
import io.github.cchristou3.CyParking.data.model.user.LoggedInUser;
import io.github.cchristou3.CyParking.databinding.FragmentBookingBinding;
import io.github.cchristou3.CyParking.ui.components.BaseFragment;
import io.github.cchristou3.CyParking.ui.helper.AlertBuilder;
import io.github.cchristou3.CyParking.ui.helper.DateTimePicker;
import io.github.cchristou3.CyParking.ui.helper.DropDownMenuHelper;
import io.github.cchristou3.CyParking.ui.views.home.HomeFragment;
import io.github.cchristou3.CyParking.ui.views.host.GlobalStateViewModel;
import io.github.cchristou3.CyParking.ui.views.host.MainHostActivity;
import io.github.cchristou3.CyParking.ui.views.parking.slots.viewBooking.ViewBookingsFragment;
import io.github.cchristou3.CyParking.ui.views.user.account.AccountFragment;
import io.github.cchristou3.CyParking.ui.views.user.feedback.FeedbackFragment;
import io.github.cchristou3.CyParking.ui.views.user.login.AuthenticatorFragment;
import io.github.cchristou3.CyParking.ui.widgets.QRCodeDialog;
import io.github.cchristou3.CyParking.utilities.AnimationUtility;
import io.github.cchristou3.CyParking.utilities.DateTimeUtility;

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
 * - Add a Livedata form - validation for the date
 * -> if not valid show error to getBinding().fragmentParkingBookingTxtDate
 *
 * @author Charalambos Christou
 * @version 10.0 24/02/2021
 */
public class BookingFragment extends BaseFragment<FragmentBookingBinding> implements Navigable {

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
     * @see BaseFragment#onCreateView(ViewBinding)
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
                    if (error != null || value == null)
                        return;
                    final ParkingLot lot = value.toObject(ParkingLot.class);
                    if (lot == null) {
                        AlertBuilder.showSingleActionAlert(
                                getChildFragmentManager(),
                                R.string.no_lot_found_title,
                                R.string.no_lot_found_msg,
                                (v) -> goBack(requireActivity())
                        );
                        return;
                    }

                    final String availability = lot.getLotAvailability(requireContext());
                    // Animate color to display a lot availability change to the user
                    AnimationUtility.animateAvailabilityColorChanges(
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
     * @see BaseFragment#onDestroyView()
     */
    @Override
    public void onDestroyView() {
        super.removeOnClickListeners(
                getBinding().fragmentParkingBookingBtnStartingTimeButton,
                getBinding().fragmentParkingBookingBtnDateButton,
                getBinding().fragmentParkingBtnBookingButton,
                getBinding().fragmentParkingBookingBtnDisplayQrCode
        );
        ((AutoCompleteTextView) getBinding().fragmentParkingBookingDropDown.getEditText())
                .setOnItemSelectedListener(null);
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
     * Attaches observers to the picked date state, picked starting time state,
     * the user's state and the visibility state of the 'View QR Code', 'Book' buttons.
     */
    private void setViewModelObservers() {
        // Whenever a user picks a date, its corresponding TextView is updated with the new date
        mBookingViewModel.getPickedDateState()
                .observe(getViewLifecycleOwner(), getBinding().fragmentParkingBookingTxtDate::setText);

        // The same goes for the picked time
        mBookingViewModel.getPickedStartingTimeState()
                .observe(getViewLifecycleOwner(),
                        time ->
                                getBinding().fragmentParkingBookingTxtStartingTime.setText(time.toString())
                );

        // Observe the user's Auth state
        observeUserState(loggedInUser -> {
            // If the user logged out, prompt to either log in or to return to previous screen.
            if (loggedInUser == null) {
                AlertBuilder.promptUserToLogIn(getChildFragmentManager(), requireActivity(), this,
                        R.string.logout_book_parking_screen_msg);
            }
        });

        // Observe when the button should be enabled or disabled.
        mBookingViewModel.getBookingButtonState().observe(getViewLifecycleOwner(), shouldEnable ->
                getBinding().fragmentParkingBtnBookingButton.setEnabled(shouldEnable)
        );

        // Observe the visibility of the 'View QR Code' button
        mBookingViewModel.getQRCodeButtonState().observe(getViewLifecycleOwner(), show -> {
            if (show) setUpQRCodeButton();
        });
    }

    /**
     * Displays the QR Code button by sliding it upwards from the screen's
     * bottom to the top of the 'book' button. Also, attaches an on click listener to
     * the button that will display the stored message as QR Code in a dialog.
     */
    private void setUpQRCodeButton() {
        // Display the 'View QR code' button
        AnimationUtility.slideVerticallyToBottom(
                getBinding().fragmentParkingBookingClMainCl, // ViewGroup / parent
                getBinding().fragmentParkingBookingBtnDisplayQrCode, // child we want to animate
                false // we want to display it
        );

        // And hook it up with an click listener that will display the
        // QR Code when clicked
        getBinding().fragmentParkingBookingBtnDisplayQrCode
                .setOnClickListener(v -> {
                    new QRCodeDialog(
                            requireContext(),
                            getBinding().fragmentParkingBookingClMainCl,
                            mBookingViewModel.getQRCodeMessage())
                            .show();
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
        // Create an array that will hold all the values of the spinner - the lot's slot offers.
        final SlotOffer[] slotOffers = SlotOffer.toArray(mSelectedParking.getSlotOfferList());
        SlotOffer.sortArray(slotOffers, true); // sort it in ascending order
        DropDownMenuHelper.setUpSlotOfferDropDownMenu(
                requireContext(), getBinding().fragmentParkingBookingDropDown,
                slotOffers, new DropDownMenuHelper.ItemHandler<SlotOffer>() {
                    @Override
                    public SlotOffer castItem(@NotNull ListAdapter parent, int position) {
                        return (SlotOffer) parent.getItem(0);
                    }

                    @Override
                    public void onItemSelected(SlotOffer item) {
                        mBookingViewModel.updateSlotOffer(item);
                        // and display the booking button
                        mBookingViewModel.updateBookingButtonState(true);
                    }
                }
        );
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

        // add QR code text to the booking object
        String qrcodeText = "";
        try {
            qrcodeText = EncryptionManager.hex(new EncryptionManager().encrypt(booking.toString()));
        } catch (Exception ignore) {// used Exception for brevity as there are too many kinds of encryption related exceptions
        }
        booking.setQRCode(qrcodeText);

        mBookingViewModel.bookParkingLot(booking)
                // Attach an onComplete listener to handle the task's result
                .addOnCompleteListener(task -> {
                    getGlobalStateViewModel().hideLoadingBar(); // hide loading bar
                    // Inform the user booking was successful and offer a temporary UNDO option
                    if (task.isSuccessful() && task.getException() == null) {
                        // Save the value of the QR Code message in the ViewModel
                        mBookingViewModel.setQRCodeMessage(booking.getQRCode());

                        // Display undo option
                        displayUndoOption(booking.generateUniqueId());

                        // Display the 'View QR Code' button
                        mBookingViewModel.updateQRCodeButtonState(true);

                        // Disable the 'book' button
                        mBookingViewModel.updateBookingButtonState(false);
                        mBookingViewModel.updateSlotOffer(null); // and set offer to null
                        return;
                    }
                    // Otherwise, display an error message to the user
                    Toast.makeText(requireContext(), getString(R.string.slot_already_booked), Toast.LENGTH_SHORT).show();
                });
    }

    /**
     * Display a temporary Snackbar allowing the user to undo the booking.
     *
     * @param bookingId The id of the booking to be potentially cancelled.
     */
    private void displayUndoOption(String bookingId) {
        Snackbar.make(requireView(), getString(R.string.booking_success), Snackbar.LENGTH_LONG)
                .setAction(R.string.undo,
                        v -> mBookingViewModel.cancelBooking(bookingId)).show();
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
                .navigate(
                        BookingFragmentDirections
                                .actionNavParkingBookingFragmentToNavAuthenticatorFragment()
                );
    }

    /**
     * Navigates from the current Fragment subclass to the
     * {@link ViewBookingsFragment}.
     */
    @Override
    public void toBookings() {
        getNavController(requireActivity())
                .navigate(
                        BookingFragmentDirections.actionNavParkingBookingFragmentToNavViewBookings()
                );
    }

    /**
     * Navigates from the current Fragment subclass to the
     * {@link AccountFragment}.
     */
    @Override
    public void toAccount() {
        getNavController(requireActivity())
                .navigate(
                        BookingFragmentDirections.actionNavParkingBookingFragmentToNavAccount()
                );
    }

    /**
     * Navigates from the current Fragment subclass to the
     * {@link FeedbackFragment}.
     */
    @Override
    public void toFeedback() {
        getNavController(requireActivity())
                .navigate(
                        BookingFragmentDirections.actionNavParkingBookingFragmentToNavFeedback()
                );
    }

    /**
     * Navigates from the current Fragment subclass to the
     * {@link HomeFragment}.
     */
    @Override
    public void toHome() {
        getNavController(requireActivity())
                .navigate(
                        BookingFragmentDirections.actionNavParkingBookingFragmentToNavHome()
                );
    }
}