package io.github.cchristou3.CyParking.ui.views.parking.slots.booking;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.transition.Fade;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListAdapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewbinding.ViewBinding;

import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.timepicker.MaterialTimePicker;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import io.github.cchristou3.CyParking.PaymentSessionHelper;
import io.github.cchristou3.CyParking.R;
import io.github.cchristou3.CyParking.apiClient.model.data.parking.lot.ParkingLot;
import io.github.cchristou3.CyParking.apiClient.model.data.parking.lot.SlotOffer;
import io.github.cchristou3.CyParking.data.interfaces.Navigable;
import io.github.cchristou3.CyParking.databinding.FragmentBookingBinding;
import io.github.cchristou3.CyParking.ui.components.BaseFragment;
import io.github.cchristou3.CyParking.ui.components.NavigatorFragment;
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
import io.github.cchristou3.CyParking.utils.Utility;

/**
 * Purpose: <p>View parking details,
 * and book the specific parking for a specific date and time.</p>
 * <p>
 * <p>
 * In terms of Authentication, this is achieved by communicating with the hosting
 * activity {@link MainHostActivity} via the {@link GlobalStateViewModel}.
 * </p>
 * <p>
 *
 * @author Charalambos Christou
 * @version 25.0 27/03/21
 */
public class BookingFragment extends NavigatorFragment<FragmentBookingBinding> {

    // Fragment variables
    private static final String TAG = BookingFragment.class.getName() + "UniqueTag";
    private BookingViewModel mBookingViewModel;
    private ParkingLot mSelectedParking;

    /**
     * Initialises the fragment. Access to data send by the previous fragment.
     * Also sets up the Customer Session related to the payment flow.
     *
     * @param savedInstanceState A bundle which contains info about previously stored data
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Initialize a Customer Session
        PaymentSessionHelper.initCustomerSession(requireContext());
        // Access the parking that was selected from the previous screen
        if (getArguments() != null) {
            mSelectedParking = getArguments().getParcelable(getString(R.string.selected_lot_arg));
        }
    }

    /**
     * Inflates our fragment's view. Saves a reference to the fragment's view
     *
     * @param inflater           Inflater which will inflate our view
     * @param container          The parent view
     * @param savedInstanceState A bundle which contains info about previously stored data
     * @return The view of the fragment
     * @see BaseFragment#onCreateView(ViewBinding, int)
     */
    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return super.onCreateView(FragmentBookingBinding.inflate(inflater), R.string.booking_label);
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
     */
    @Override
    public void onStart() {
        super.onStart();
        try {
            mBookingViewModel.observeParkingLotToBeBooked(requireContext(), mSelectedParking)
                    .registerLifecycleObserver(getLifecycle());
        } catch (NullPointerException e) {
            // This should get triggered when the user has finished the booking, navigated to
            // any other destination using the drawer and then pressed the back button to navigate back here
            Log.d(TAG, "onStart: User returned! <> " + e.getMessage());
        }
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
        DropDownMenuHelper.cleanUp(getBinding().fragmentParkingBookingDropDown);
        super.onDestroyView();
    }

    /**
     * Handle the previous activity's result.
     *
     * @param requestCode The integer request code originally supplied to
     *                    startActivityForResult(), allowing you to identify who this
     *                    result came from.
     * @param resultCode  The integer result code returned by the child activity
     *                    through its setResult().
     * @param data        An Intent, which can return result data to the caller
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            mBookingViewModel.handlePaymentData(requestCode, resultCode,
                    data == null ? new Intent() : data);
        } else if (resultCode == Activity.RESULT_CANCELED) {
            getGlobalStateViewModel().updateToastMessage(R.string.no_payment_method_selected);
        }
    }

    /**
     * Initializes the {@link #mBookingViewModel}
     * ViewModel of the fragment.
     */
    private void initializeViewModel() {
        mBookingViewModel = new ViewModelProvider(this,
                new BookingViewModelFactory(this)).get(BookingViewModel.class);
        mBookingViewModel.setUpPaymentSession(this);
    }

    /**
     * Attaches observers to the picked date state, picked starting time state,
     * the user's state and the visibility state of the 'View QR Code', 'Book' buttons.
     */
    private void setViewModelObservers() {
        // Whenever a user picks a date, its corresponding TextView is updated with the new date
        mBookingViewModel.getDateState()
                .observe(getViewLifecycleOwner(), getBinding().fragmentParkingBookingTxtDate::setText);

        // The same goes for the picked time
        mBookingViewModel.getStartingTimeState()
                .observe(getViewLifecycleOwner(),
                        time -> getBinding().fragmentParkingBookingTxtStartingTime.setText(time.toString())
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
            else hideQRCodeButton();
        });

        // Listen for snack bars
        mBookingViewModel.getSnackBarState().observe(getViewLifecycleOwner(), this::displayUndoOption);

        // Listen for the payment confirmation state
        mBookingViewModel.getPaymentFlow().observe(getViewLifecycleOwner(), shouldInitiatePayment -> {
            if (shouldInitiatePayment != null && shouldInitiatePayment) {
                mBookingViewModel.confirmPayment(
                        BookingFragment.this, getUser(), this.mSelectedParking,
                        Utility.getCurrency().getCurrencyCode()
                );
            }
        });

        // When a payment method is selected display it
        mBookingViewModel.getPaymentMethod().observe(getViewLifecycleOwner(), paymentMethodDetails -> {
            getBinding().fragmentParkingBookingTxtSelectedPaymentMethod.setText(paymentMethodDetails);
            if (!getBinding().fragmentParkingBookingTxtSelectedPaymentMethod.isShown()) {
                AnimationUtility.fade(getBinding().getRoot(), getBinding().fragmentParkingBookingTxtSelectedPaymentMethod,
                        false, 275L, Fade.IN, null);
            }
        });

        // When a fatal error occurs display an alert
        mBookingViewModel.getAlertErrorState().observe(getViewLifecycleOwner(), errorMessage ->
                AlertBuilder.showSingleActionAlert(
                        getChildFragmentManager(),
                        errorMessage,
                        R.string.navigate_back_msg,
                        (v) -> goBack())
        );

        // Listen to changes (spaces) to the current parking lot.
        mBookingViewModel.getNewParkingLotVersion().observe(getViewLifecycleOwner(), newParkingLotVersion -> {
            final String availability = newParkingLotVersion.getLotAvailability(requireContext());
            // Animate color to display a lot availability change to the user
            AnimationUtility.animateAvailabilityColorChanges(
                    getBinding().fragmentParkingBookingCvParkingAvailability,
                    getBinding().fragmentParkingBookingTxtParkingAvailability,
                    newParkingLotVersion.getAvailableSpaces(),
                    mSelectedParking.getAvailableSpaces());

            // Update the text of the availability
            getBinding().fragmentParkingBookingTxtParkingAvailability.setText(availability);
            mSelectedParking = newParkingLotVersion; // Save a reference to the updated lot
        });

        mBookingViewModel.isBookingCompleted().observe(getViewLifecycleOwner(), this::setUiInputEnabled);
    }

    /**
     * Hide the qr code button.
     */
    private void hideQRCodeButton() {
        AnimationUtility.slideBottom(
                getBinding().fragmentParkingBookingClMainCl, // ViewGroup / parent
                getBinding().fragmentParkingBookingBtnDisplayQrCode, // child we want to animate
                true, // we want to display it
                1000L, null, true
        );
    }

    /**
     * Displays the QR Code button by sliding it upwards from the screen's
     * bottom to the top of the 'book' button. Also, attaches an on click listener to
     * the button that will display the stored message as QR Code in a dialog.
     */
    private void setUpQRCodeButton() {
        // Display the 'View QR code' button
        AnimationUtility.slideBottom(
                getBinding().fragmentParkingBookingClMainCl, // ViewGroup / parent
                getBinding().fragmentParkingBookingBtnDisplayQrCode, // child we want to animate
                false, // we want to display it
                1000L, null
        );

        // And hook it up with an click listener that will display the
        // QR Code when clicked
        getBinding().fragmentParkingBookingBtnDisplayQrCode
                .setOnClickListener(v -> {
                    if (mBookingViewModel.getQRCodeMessage() != null) {
                        new QRCodeDialog(
                                requireContext(),
                                getBinding().fragmentParkingBookingClMainCl,
                                mBookingViewModel.getQRCodeMessage())
                                .show();
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
        getBinding().fragmentParkingBookingTxtParkingName
                .setText(String.format(getString(R.string.lot_name),
                        mSelectedParking.getLotName())); // Set parking name
        getBinding().fragmentParkingBookingTxtParkingAvailability
                .setText(mSelectedParking.getLotAvailability(requireContext())); // Set parking availability

        setUpSlotOfferDropDownMenu(); // Initialize, attach listener to the drop down menu

        // Set the date and the start time to their corresponding TextView objects
        getBinding().fragmentParkingBookingTxtDate.setText(
                mBookingViewModel.getDateState().getValue()
        );
        getBinding().fragmentParkingBookingTxtStartingTime.setText(
                mBookingViewModel.getPickedStartingTime() != null ? mBookingViewModel.getPickedStartingTime().toString() : ""
        );

        // Set up time pickers' listeners
        getBinding().fragmentParkingBookingBtnStartingTimeButton
                .setOnClickListener(buildTimePickerListener());

        // Set up date picker listener
        getBinding().fragmentParkingBookingBtnDateButton.setOnClickListener(v -> DateTimePicker.getDatePicker(
                mBookingViewModel::updatePickedDate // On date selected callback
        )
                .show(getChildFragmentManager(), MaterialDatePicker.class.getCanonicalName()));

        // Set listener to "BOOK" button
        getBinding().fragmentParkingBtnBookingButton.setOnClickListener(v -> bookParking());

        getBinding().fragmentParkingBookingBtnSelectPaymentMethod
                .setOnClickListener(v -> mBookingViewModel.presentPaymentMethodSelection());
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
                    @NotNull
                    @Override
                    public String onOutput(SlotOffer item) {
                        return item.toString(requireContext());
                    }

                    @NotNull
                    @Override
                    public SlotOffer castItem(@NotNull ListAdapter parent, int position) {
                        return (SlotOffer) parent.getItem(position);
                    }

                    @Override
                    public void onItemSelected(SlotOffer item) {
                        mBookingViewModel.handleSelectedItem(item);
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
        mBookingViewModel.bookParkingLot(getUser(), mSelectedParking,
                getGlobalStateViewModel()::showLoadingBar,
                getGlobalStateViewModel()::hideLoadingBar,
                getGlobalStateViewModel()::updateToastMessage);
    }

    /**
     * Display a temporary Snackbar allowing the user to undo the booking.
     * Also, disable any relevant input components.
     *
     * @param bookingId The id of the booking to be potentially cancelled.
     */
    private void displayUndoOption(@Nullable String bookingId) {
        if (bookingId != null) {
            Snackbar.make(requireView(), getString(R.string.booking_success), Snackbar.LENGTH_LONG)
                    .setAction(R.string.undo,
                            v -> mBookingViewModel.cancelBooking(bookingId)).show();
        }
    }

    /**
     * Disable any input Ui.
     */
    private void setUiInputEnabled(boolean isBookingCompleted) {
        getBinding().fragmentParkingBookingBtnStartingTimeButton.setClickable(!isBookingCompleted);
        getBinding().fragmentParkingBookingBtnSelectPaymentMethod.setClickable(!isBookingCompleted);
        getBinding().fragmentParkingBookingBtnDateButton.setClickable(!isBookingCompleted);
        getBinding().fragmentParkingBtnBookingButton.setClickable(!isBookingCompleted);
        getBinding().fragmentParkingBookingDropDown.setClickable(!isBookingCompleted);
    }

    /**
     * Creates an OnClickListener to the given button.
     * OnClick: Creates a {@link MaterialTimePicker} with its own OnPositiveButtonClickListener.
     * OnPositiveButtonClickListener-onClick: Updates the value of the specified LiveData Object.
     *
     * @return A View.OnClickListener
     */
    @NotNull
    @Contract(pure = true)
    private View.OnClickListener buildTimePickerListener() {
        return v -> DateTimePicker.getTimePicker(
                requireContext(),
                mBookingViewModel::updateStartingTime) // On time selected listener
                // Display it
                .show(getChildFragmentManager(), MaterialTimePicker.class.getCanonicalName());
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
        navigateTo(
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
        navigateTo(
                        BookingFragmentDirections.actionNavParkingBookingFragmentToNavViewBookings()
                );
    }

    /**
     * Navigates from the current Fragment subclass to the
     * {@link AccountFragment}.
     */
    @Override
    public void toAccount() {
        navigateTo(
                        BookingFragmentDirections.actionNavParkingBookingFragmentToNavAccount()
                );
    }

    /**
     * Navigates from the current Fragment subclass to the
     * {@link FeedbackFragment}.
     */
    @Override
    public void toFeedback() {
        navigateTo(
                        BookingFragmentDirections.actionNavParkingBookingFragmentToNavFeedback()
                );
    }

    /**
     * Navigates from the current Fragment subclass to the
     * {@link HomeFragment}.
     */
    @Override
    public void toHome() {
        navigateTo(
                        BookingFragmentDirections.actionNavParkingBookingFragmentToNavHome()
                );
    }

    /**
     * Trigger an observer update causing a dialog to pop out with the given message.
     *
     * @param errorMessage The message to be displayed.
     */
    public void showAlert(String errorMessage) {
        mBookingViewModel.updateAlertErrorState(errorMessage);
    }

    /**
     * Access the fragment's ViewModel
     *
     * @return A reference to {@link #mBookingViewModel}
     */
    public BookingViewModel getBookingViewModel() {
        return mBookingViewModel;
    }
}