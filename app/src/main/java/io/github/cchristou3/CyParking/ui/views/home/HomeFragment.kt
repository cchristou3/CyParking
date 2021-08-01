package io.github.cchristou3.CyParking.ui.views.home;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.FragmentNavigator;
import androidx.viewbinding.ViewBinding;

import com.google.android.gms.location.LocationResult;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.zxing.integration.android.IntentIntegrator;

import org.jetbrains.annotations.NotNull;

import java.util.concurrent.atomic.AtomicBoolean;

import io.github.cchristou3.CyParking.R;
import io.github.cchristou3.CyParking.apiClient.model.data.parking.lot.ParkingLot;
import io.github.cchristou3.CyParking.apiClient.model.data.parking.slot.booking.Booking;
import io.github.cchristou3.CyParking.apiClient.model.data.parking.slot.booking.BookingDetails;
import io.github.cchristou3.CyParking.apiClient.model.data.user.LoggedInUser;
import io.github.cchristou3.CyParking.data.interfaces.LocationHandler;
import io.github.cchristou3.CyParking.data.interfaces.Navigable;
import io.github.cchristou3.CyParking.data.manager.DatabaseObserver;
import io.github.cchristou3.CyParking.data.manager.location.LocationManager;
import io.github.cchristou3.CyParking.data.manager.location.SingleUpdateHelper;
import io.github.cchristou3.CyParking.databinding.FragmentHomeBinding;
import io.github.cchristou3.CyParking.ui.components.BaseFragment;
import io.github.cchristou3.CyParking.ui.components.LocationServiceViewModel;
import io.github.cchristou3.CyParking.ui.views.host.GlobalStateViewModel;
import io.github.cchristou3.CyParking.ui.views.host.MainHostActivity;
import io.github.cchristou3.CyParking.ui.views.user.account.AccountFragment;

import static io.github.cchristou3.CyParking.utilities.AnimationUtility.slideBottom;
import static io.github.cchristou3.CyParking.utilities.AnimationUtility.slideTop;
import static io.github.cchristou3.CyParking.utils.ViewUtility.updateViewVisibilityTo;

/**
 * Purpose: <p>Show to the user all available action options</p>
 * If the user is logged in and has a role of "Operator"
 * then a lot information area is shown. If the user does not
 * have a lot registered, an option for registering one is displayed.
 * Otherwise, if the user has a lot, then display critical info about it.
 * <p>
 * In terms of Authentication, this is achieved by communicating with the hosting
 * activity {@link MainHostActivity} via the {@link GlobalStateViewModel}.
 * </p>
 *
 * @author Charalambos Christou
 * @version 16.0 27/03/21
 */
public class HomeFragment extends BaseFragment<FragmentHomeBinding> implements Navigable, LocationHandler {

    // Fragment variables
    private static final String TAG = HomeFragment.class.getName();
    private static final long TRANSITION_DURATION = 750L;
    private static final String LOT_REFERENCE_KEY = "ref";
    private final AtomicBoolean mWasLocationRequested = new AtomicBoolean(false);
    private SingleUpdateHelper<HomeFragment, FragmentHomeBinding> mLocationManager;
    // Members related to the Operator
    private OperatorViewModel mOperatorViewModel;
    private HomeViewModel mHomeViewModel;
    private DatabaseObserver<Query, QuerySnapshot> mDatabaseObserver;
    private IntentIntegrator mIntentIntegrator;

    /**
     * Initialize the fragment.
     *
     * @param savedInstanceState If the fragment is being re-created from
     */
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mHomeViewModel = new ViewModelProvider(this).get(HomeViewModel.class);
    }

    /**
     * Inflates our fragment's view.
     *
     * @param inflater           Inflater which will inflate our view
     * @param container          The parent view
     * @param savedInstanceState A bundle which contains info about previously stored data
     * @return The view of the fragment
     * @see BaseFragment#onCreateView(ViewBinding, int)
     */
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        return super.onCreateView(FragmentHomeBinding.inflate(inflater), R.string.menu_home);
    }

    /**
     * Invoked at the completion of onCreateView. Initializes fragment's ViewModel.
     * Lastly, listeners are attached to all buttons.
     *
     * @param view               The view of the fragment
     * @param savedInstanceState A bundle which contains info about previously stored data
     */
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Listen to when the user should be navigated to the map
        mHomeViewModel.getNavigationToMap().observe(getViewLifecycleOwner(), this::navigateToMap);

        // Listen to when the location services fail
        LocationServiceViewModel
                .addObserverToLocationServicesError(mHomeViewModel, this);

        // Listen to the user state
        observeUserState(this::updateUi);

        initializeButtonListeners();

        // Each time the home screen becomes visible reset the map button back to normal
        resetMapButton();

        // TODO: Add splash screen till the app has been initialized (FirebaseApp, Network broadcasters, validating user's data, etc.).
    }

    /**
     * Gets invoked after the user has been asked for a permission for a given package.
     * If permission was granted, request for the user's latest known location.
     *
     * @param requestCode  The code of the user's request.
     * @param permissions  The permission that were asked.
     * @param grantResults The results of the user's response.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NotNull String[] permissions, @NotNull int[] grantResults) {
        if (mLocationManager != null) {
            mLocationManager.onRequestPermissionsResult(this, requestCode, grantResults);
        }
    }

    /**
     * Called when the view previously created by {@link #onCreateView} has
     * been detached from the fragment.  The next time the fragment needs
     * to be displayed, a new view will be created.  This is called
     * after {@link #onStop()} and before {@link #onDestroy()}.  It is called
     * <em>regardless</em> of whether {@link #onCreateView} returned a
     * non-null view.  Internally it is called after the view's state has
     * been saved but before it has been removed from its parent.
     *
     * @see BaseFragment#onDestroyView()
     */
    @Override
    public void onDestroyView() {
        super.removeOnClickListeners(
                getBinding().fragmentHomeMbtnRegisterParkingLot,
                getBinding().fragmentHomeMbtnRegisterParkingLot,
                getBinding().fragmentHomeBtnIncrement,
                getBinding().fragmentHomeBtnDecrement,
                getBinding().fragmentHomeBtnNavToMap
        );

        mLocationManager = null;
        super.onDestroyView();
    }

    /**
     * Receive the result from a previous call to
     * {@link #startActivityForResult(Intent, int)}.
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
        Log.d(TAG, "onActivityResult <> Home");
        if (mLocationManager != null)
            mLocationManager.onActivityResult(this, requestCode, resultCode, data);

        if (mOperatorViewModel != null)
            mOperatorViewModel.handleQRCodeScannerContents(
                    // Access the qr code's payload
                    IntentIntegrator.parseActivityResult(requestCode, resultCode, data),
                    // Access the previously stored lot reference
                    (DocumentReference) getIntentIntegrator().getMoreExtras().get(LOT_REFERENCE_KEY),
                    getGlobalStateViewModel()::updateToastMessage // A handler for messages
            );
    }

    /**
     * Attach on click listeners to the 'scan lot' and 'parking map' buttons.
     * 'Scan lot' on click: open up the QR code scanner.
     * 'Parking map' on click: transition to the Google maps fragment.
     */
    private void initializeButtonListeners() {
        // TODO: 10/02/2021 Implement the buttons and add descriptions below then
        getBinding().fragmentHomeBtnScanLot.setOnClickListener(v ->
                Toast.makeText(requireContext(), "Not implemented yet!", Toast.LENGTH_SHORT).show()
        );

        // Attach listener to "Parking Map" button
        getBinding().fragmentHomeBtnNavToMap.setOnClickListener(v -> requestLocation());
    }

    /**
     * Request the user's location.
     * Ensures that only a single request is sent
     * even when the user has clicked the button
     * multiple consecutive times.
     */
    private void requestLocation() {
        if (mWasLocationRequested.get()) return;
        mWasLocationRequested.compareAndSet(false, true);
        // Request for the user's latest known location
        Log.d(TAG, "requestUserLocationUpdates");
        getLocationManager().requestUserLocationUpdates(this);

    }

    /**
     * Access and initialize the fragment's location manager when required.
     * If the Location manager has already been initialized then prepare
     * it to handle another callback/request.
     *
     * @return the fragment's {@link SingleUpdateHelper} instance.
     */
    public SingleUpdateHelper<HomeFragment, FragmentHomeBinding> getLocationManager() {
        // Initialize the Location manager object
        if (mLocationManager == null) {
            mLocationManager = LocationManager.createSingleUpdateHelper(this, this,
                    this::resetMapButton,
                    mHomeViewModel::postLocationServicesError);
        } else {
            mLocationManager.prepareCallback(this);
        }
        return mLocationManager;
    }

    /**
     * Enable the map button to accept further clicks.
     */
    public void resetMapButton() {
        HomeFragment.this.mWasLocationRequested.set(false);
    }

    /**
     * Update the Ui based on the given {@link LoggedInUser} instance.
     * If the user is not logged in, or is but is not as an operator,
     * everything related to the operator is hidden.
     *
     * @param loggedInUser The current {@link LoggedInUser} instance.
     */
    private void updateUi(LoggedInUser loggedInUser) {

        // TODO: 29/03/2021 In the following cases:
        //   - If the user is not logged in,
        //   - The user is logged in but not as an operator and he/she has no upcoming bookings
        //   Then the lower screen will be left blank (which looks ugly).
        //   Instead of that, use that section to recommend to the user nearby parking lots, offers, etc.

        if (loggedInUser == null) {
            // If operator logged out, remove observer to its parking lot
            if (mDatabaseObserver != null) mDatabaseObserver.unregisterLifecycleObserver();
            cleanUpUi();
            return;
        }

        // TODO: 10/02/2021 Display QR Code scanner button:
        //  users use it to can scan the QR Code of an operator and
        //  they will be navigated to the payment screen (see Instant App)

        if (loggedInUser.isOperator()) {
            // Is an operator but not a user
            initializeOperator(loggedInUser);
        } else { // User is not an operator
            // Hide anything related to parking lot from the user
            getBinding().fragmentHomeCvLotInfo.setVisibility(View.GONE);
            // And display an upcoming booking if there is one
            initializeUser(loggedInUser);
        }
    }

    /**
     * Removes any Views that are accessible to logged in users.
     */
    private void cleanUpUi() {
        if (getBinding().fragmentHomeBtnScanBooking.isShown()) { // Operator has a lot
            // Hide the QR Code scanner to be used to scan bookings via animation
            slideTop(
                    getBinding().getRoot(), getBinding().fragmentHomeBtnScanBooking, true, TRANSITION_DURATION,
                    // Once that view is hidden, hide the lot info card view
                    /* next animation */ () -> hide(getBinding().fragmentHomeCvLotInfo)
            );
        } else {
            // This is the case when the user did not register a lot and only the register lot layout is shown
            hide(getBinding().fragmentHomeCvLotInfo);
        }

        // Hide Ui related layout related to logged in users
        hide(getBinding().fragmentHomeCvUserBooking);
    }

    /**
     * Hide the given view if it is shown.
     *
     * @param view the view to hide.
     */
    private void hide(@NotNull View view) {
        if (view.isShown()) {
            slideBottom(getBinding().getRoot(), view, true, TRANSITION_DURATION, null);
        }
    }

    /**
     * Show the given view if it is hidden.
     *
     * @param view the view to show.
     */
    private void show(@NotNull View view) {
        if (!view.isShown()) {
            slideBottom(getBinding().getRoot(), view, false, TRANSITION_DURATION, null);
        }
    }

    /**
     * Initialize components related to the user.
     *
     * @param loggedInUser the current instance of {@link LoggedInUser}
     */
    private void initializeUser(@NonNull LoggedInUser loggedInUser) {
        // Instantiate the ViewModel
        UserViewModel mUserViewModel = new ViewModelProvider(this, new UserViewModel.Factory()).get(UserViewModel.class);

        mUserViewModel.getUpcomingBooking().observe(getViewLifecycleOwner(), this::displayBooking);

        getUpcomingBooking(mUserViewModel, loggedInUser);

        mUserViewModel.getHideBooking().observe(getViewLifecycleOwner(), hideBooking ->
                hideBookingAndGetNext(mUserViewModel, loggedInUser)
        );
    }

    /**
     * Hides the current booking, and fetches the next one.
     *
     * @param mUserViewModel the business logic handler.
     * @param loggedInUser   the current instance of LoggedInUser.
     */
    private void hideBookingAndGetNext(UserViewModel mUserViewModel, LoggedInUser loggedInUser) {
        slideBottom(getBinding().getRoot(),
                getBinding().fragmentHomeCvUserBooking,
                true, TRANSITION_DURATION, () -> { // This time we want to fetch the next upcoming booking
                    // and not animate another view
                    getUpcomingBooking(mUserViewModel, loggedInUser);
                });
    }

    /**
     * Retrieve the most recent upcoming booking of the given user.
     *
     * @param mUserViewModel Does the business logic.
     * @param loggedInUser   The current instance of {@link LoggedInUser}.
     */
    private void getUpcomingBooking(@NotNull UserViewModel mUserViewModel, @NotNull LoggedInUser loggedInUser) {
        mUserViewModel.getUpcomingBooking(loggedInUser.getUserId(), requireActivity(), getGlobalStateViewModel()::updateToastMessage);
    }

    /**
     * Perform necessary Ui updates to display the given booking,
     * while also attach it an on click listener
     * on-click: navigate to booking details fragment.
     *
     * @param upcomingBooking The upcoming booking.
     */
    private void displayBooking(@NotNull Booking upcomingBooking) {
        // Make the user related CardView visible if it is not already
        show(getBinding().fragmentHomeCvUserBooking);

        // Update the contents if its children.
        getBinding().fragmentHomeBookingItem.bookingItemFullyTxtDate
                .setText(BookingDetails.getDateText(upcomingBooking.getBookingDetails().getDateOfBooking()));
        getBinding().fragmentHomeBookingItem.bookingItemFullyTxtOffer
                .setText(upcomingBooking.getBookingDetails().getSlotOffer().toString(requireContext()));
        getBinding().fragmentHomeBookingItem.bookingItemFullyTxtParkingName
                .setText(upcomingBooking.getLotName());
        getBinding().fragmentHomeBookingItem.bookingItemFullyTxtStatus
                .setText(Booking.getStatusText(requireContext(), upcomingBooking.isCompleted()));
        getBinding().fragmentHomeBookingItem.bookingItemFullyTxtStartTime
                .setText(upcomingBooking.getBookingDetails().getStartingTime().toString());
        getBinding().fragmentHomeBookingItem.bookingItemFullyTxtEndTime
                .setText(BookingDetails.Time.getEndTime(upcomingBooking.getBookingDetails()).toString());

        // Set the shared views to participate in the transition - see below
        FragmentNavigator.Extras.Builder sharedView = new FragmentNavigator.Extras.Builder();
        sharedView.addSharedElement(getBinding().fragmentHomeBookingItem.bookingItemFullyCv, getString(R.string.shared_booking_card_view));
        sharedView.addSharedElement(getBinding().fragmentHomeCvUserBooking, getString(R.string.shared_parent));

        // Make the booking clickable
        getBinding().fragmentHomeBookingItem.bookingItemFullyCv.setClickable(true);
        // Hook up the whole card view with an on click listener
        // on-click: navigate to booking details.
        getBinding().fragmentHomeBookingItem.bookingItemFullyCv
                .setOnClickListener(v -> {
                            // Do not allow the user from clicking the booking again
                            // Otherwise, it would trigger unexpected behaviour.
                            getBinding().fragmentHomeBookingItem.bookingItemFullyCv.setClickable(false);
                            getNavController(requireActivity())
                                    .navigate(HomeFragmentDirections.actionNavHomeToNavBookingDetailsFragment(upcomingBooking),
                                            sharedView.build());
                        }
                );
    }

    /**
     * Initializes both the Ui and the database logic related to the operator.
     *
     * @param loggedInUser current user.
     */
    private void initializeOperator(@NotNull LoggedInUser loggedInUser) {
        // TODO: 19/01/2021 Encapsulate all operator logic to a fragment and simply inflate it
        // Initialize the OperatorViewModel
        mOperatorViewModel = new ViewModelProvider(this,
                new OperatorViewModelFactory()).get(OperatorViewModel.class);

        // Attach observer to update the view's parking lot info whenever it changes
        mOperatorViewModel.getParkingLotState().observe(getViewLifecycleOwner(),
                this::updateLotContents); // Display the parking lot's contents

        // Get the operator's lot info from the database.
        getParkingLotInfo(loggedInUser.getUserId());

        // TODO: 10/02/2021 Display QR scanner button: scan QR code of users that have booked a slot.
    }

    /**
     * Initialize a self-management query observer.
     * The observer, handles retrieving the operators lot
     * and any of its changes.
     * On initial and consecutive data loads the Ui related to the
     * operator's lot is updated accordingly.
     *
     * @param operatorId The id of the operator.
     */
    private void getParkingLotInfo(String operatorId) {
        // Initialize the fragment's QueryObserver
        mDatabaseObserver = DatabaseObserver.createQueryObserver(
                mOperatorViewModel.observeParkingLot(operatorId), // The Query
                (value, error) -> { // The Event listener
                    if (error != null || value == null) return; // TODO: Handle error
                    Log.d(TAG, "getParkingLotInfo: is empty? " + value.isEmpty());
                    if (value.isEmpty()) { // The operator did not register lot yet
                        displayLotRegistrationLayout();
                        return;
                    }

                    // Remove listeners from the register lot button button
                    getBinding().fragmentHomeMbtnRegisterParkingLot.setOnClickListener(null);

                    // If the operator has registered a lot already, display its info
                    final ParkingLot userParkingLot = value.getDocuments().get(0).toObject(ParkingLot.class);
                    if (userParkingLot == null) return;
                    if (getUser() != null && !userParkingLot.getOperatorId().equals(getUser().getUserId()))
                        return;

                    // Get a reference to the document
                    final DocumentReference ref = value.getDocuments().get(0).getReference();

                    // Hook up buttons with listeners
                    HomeFragment.this.setUpOperatorButtons(ref, userParkingLot);

                    // Trigger parking lot update.
                    Log.d(TAG, "getParkingLotInfo: userParkingLot != null");
                    getBinding().fragmentHomeClRegisterLotInfo.setVisibility(View.VISIBLE);
                    mOperatorViewModel.updateLotState(userParkingLot);

                });
        // Register it for lifecycle observation
        mDatabaseObserver.registerLifecycleObserver(getLifecycle());
    }

    /**
     * Hook up the `scan booking` button with an on click listener.
     * onclick: initialize the QR Code scanner.
     *
     * @param lotRef A document reference to the operator's parking lot.
     */
    public void setUpScanBookingButton(DocumentReference lotRef) {
        getBinding().fragmentHomeBtnScanBooking.setOnClickListener(v -> {
            // Only available to operators
            getIntentIntegrator()
                    .addExtra(LOT_REFERENCE_KEY, lotRef)
                    .initiateScan();
        });
    }

    /**
     * Return the fragment's {@link #mIntentIntegrator}.
     * Lazy initialization is used.
     *
     * @return the fragment's {@link #mIntentIntegrator}.
     */
    private IntentIntegrator getIntentIntegrator() {
        if (mIntentIntegrator == null) {
            mIntentIntegrator = IntentIntegrator
                    .forSupportFragment(HomeFragment.this)
                    .setDesiredBarcodeFormats(IntentIntegrator.QR_CODE)
                    .setCameraId(0)
                    .setPrompt(getString(R.string.qr_code_scanner_bottom_text))
                    .setBeepEnabled(true)
                    .setBarcodeImageEnabled(true)
                    .setCaptureActivity(PortraitCaptureActivity.class);
        }
        return mIntentIntegrator;
    }

    /**
     * Updates the views related to the lot with the specified {@link ParkingLot}
     * instance.
     *
     * @param userParkingLot The updated version of the parking lot.
     */
    private void updateLotContents(@NotNull ParkingLot userParkingLot) {
        checkVisibilityOfAppropriateLayout(View.VISIBLE, View.GONE);
        // Display the lot's details
        getBinding().fragmentHomeTxtLotCapacity.setText(userParkingLot.getLotAvailability(requireContext()));
        getBinding().fragmentHomeTxtLotName.setText(String.format(getString(R.string.lot_name), userParkingLot.getLotName()));
    }

    /**
     * Displays a view giving the operator the option
     * to register a parking lot.
     */
    private void displayLotRegistrationLayout() {
        checkVisibilityOfAppropriateLayout(View.GONE, View.VISIBLE);
        // Attach listener to "Register Parking lot" button
        getBinding().fragmentHomeMbtnRegisterParkingLot
                .setOnClickListener(v ->
                        // Navigate to the parking lot registration form
                        getNavController(requireActivity())
                                .navigate(
                                        HomeFragmentDirections.actionNavHomeToNavRegisterLotFragment()
                                ));
    }

    /**
     * Changes the visibility of "lot info" layout and "register lot" layout,
     * according to the specified arguments.
     * If the above layouts' parent is not visible, then animate it to the UI.
     *
     * @param lotInfoVisibility     The new visibility of the lot info layout.
     * @param registerLotVisibility The new visibility of register lot layout.
     */
    private void checkVisibilityOfAppropriateLayout(int lotInfoVisibility, int registerLotVisibility) {
        updateViewVisibilityTo(getBinding().fragmentHomeClShowLotInfo, lotInfoVisibility); // showLotInfo layout
        updateViewVisibilityTo(getBinding().fragmentHomeClRegisterLotInfo, registerLotVisibility); // registerLotInfo layout

        if (lotInfoVisibility == View.VISIBLE) { // Operator has a lot
            // Display the QR Code scanner to be used to scan bookings via animation
            slideTop(
                    getBinding().getRoot(), getBinding().fragmentHomeBtnScanBooking, false, TRANSITION_DURATION,
                    // Once that view is shown, display the lot info card view
                    /* next animation */ () -> slideBottom(getBinding().getRoot(),
                            getBinding().fragmentHomeCvLotInfo, false, TRANSITION_DURATION, null)
            );
        } else {// Only show the lot info card view
            show(getBinding().fragmentHomeCvLotInfo);
        }
    }

    /**
     * Hooks up "increment", "decrement", and "scan booking" buttons with on click listeners.
     *
     * @param ref            A DocumentReference of the parking lot in the database.
     * @param userParkingLot The latest retrieved parking lot of the database.
     */
    private void setUpOperatorButtons(DocumentReference ref, ParkingLot userParkingLot) {
        // add an appropriate on click listener
        setUpScanBookingButton(ref);

        // Attach listeners to "increment", "decrement" buttons
        getBinding().fragmentHomeBtnIncrement.setOnClickListener(v -> {
            // If the lot has available spaces decrease its value by one
            // E.g. 40/40 -> do nothing
            if (userParkingLot.getAvailableSpaces() > 0) {
                mOperatorViewModel.incrementPersonCount(ref);
            }
        });
        getBinding().fragmentHomeBtnDecrement.setOnClickListener(v -> {
            // If the lot has the same number of available spaces as its capacity do nothing.
            // E.g. 0/40 -> do nothing
            if (userParkingLot.getAvailableSpaces() < userParkingLot.getCapacity()) {
                // Otherwise, increase in its value by one.
                mOperatorViewModel.decrementPersonCount(ref);
            }
        });
    }

    /**
     * Navigates from the current Fragment subclass to the
     * {@link io.github.cchristou3.CyParking.ui.views.user.login.AuthenticatorFragment}.
     */
    @Override
    public void toAuthenticator() {
        getNavController(requireActivity())
                .navigate(
                        HomeFragmentDirections.actionNavHomeToNavAuthenticatorFragment()
                );
    }

    /**
     * Navigates from the current Fragment subclass to the
     * {@link io.github.cchristou3.CyParking.ui.views.parking.slots.viewBooking.ViewBookingsFragment}.
     */
    @Override
    public void toBookings() {
        getNavController(requireActivity())
                .navigate(
                        HomeFragmentDirections.actionNavHomeToNavViewBookings()
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
                        HomeFragmentDirections.actionNavHomeToNavAccount()
                );
    }

    /**
     * Navigates from the current Fragment subclass to the
     * {@link io.github.cchristou3.CyParking.ui.views.user.feedback.FeedbackFragment}.
     */
    @Override
    public void toFeedback() {
        getNavController(requireActivity())
                .navigate(
                        HomeFragmentDirections.actionNavHomeToNavFeedback()
                );
    }

    /**
     * Navigates from the current Fragment subclass to the
     * {@link HomeFragment}.
     */
    @Override
    public void toHome() { /* Already in this screen. Thus, no need to implement this method. */ }

    /**
     * Callback invoked when the user's location is received.
     *
     * @param locationResult The result of the user's requested location.
     * @see LocationManager#requestUserLocationUpdates(BaseFragment)
     */
    @Override
    public void onLocationResult(LocationResult locationResult) {
        mHomeViewModel.navigateToMap(locationResult, getGlobalStateViewModel()::updateToastMessage);
    }

    /**
     * Navigate to the GoogleMaps fragment.
     * Transfer the user's latest location.
     *
     * @param userLocation The user's latest known location.
     */
    private void navigateToMap(LatLng userLocation) {
        getNavController(requireActivity())
                .navigate(
                        HomeFragmentDirections.actionHomeToParkingMap(
                                userLocation
                        )
                );
    }
}