package io.github.cchristou3.CyParking.ui.views.home;

import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewbinding.ViewBinding;

import com.google.android.gms.location.LocationResult;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.greenrobot.eventbus.EventBus;
import org.jetbrains.annotations.NotNull;

import io.github.cchristou3.CyParking.R;
import io.github.cchristou3.CyParking.data.interfaces.LocationHandler;
import io.github.cchristou3.CyParking.data.interfaces.Navigable;
import io.github.cchristou3.CyParking.data.manager.DatabaseObserver;
import io.github.cchristou3.CyParking.data.manager.EncryptionManager;
import io.github.cchristou3.CyParking.data.manager.location.LocationManager;
import io.github.cchristou3.CyParking.data.manager.location.SingleUpdateHelper;
import io.github.cchristou3.CyParking.data.model.parking.lot.ParkingLot;
import io.github.cchristou3.CyParking.data.model.parking.slot.booking.Booking;
import io.github.cchristou3.CyParking.data.model.user.LoggedInUser;
import io.github.cchristou3.CyParking.databinding.FragmentHomeBinding;
import io.github.cchristou3.CyParking.ui.components.BaseFragment;
import io.github.cchristou3.CyParking.ui.views.host.GlobalStateViewModel;
import io.github.cchristou3.CyParking.ui.views.host.MainHostActivity;
import io.github.cchristou3.CyParking.ui.views.user.account.AccountFragment;

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
 * @version 11.0 24/02/21
 */
public class HomeFragment extends BaseFragment<FragmentHomeBinding> implements Navigable, LocationHandler {

    // Fragment variables
    private static final String TAG = HomeFragment.class.getName();
    private SingleUpdateHelper mLocationManager;
    // Members related to the Operator
    private OperatorViewModel mOperatorViewModel;
    private DatabaseObserver<Query, QuerySnapshot> mDatabaseObserver;
    private IntentIntegrator mIntentIntegrator;

    /**
     * Inflates our fragment's view.
     *
     * @param inflater           Inflater which will inflate our view
     * @param container          The parent view
     * @param savedInstanceState A bundle which contains info about previously stored data
     * @return The view of the fragment
     * @see BaseFragment#onCreateView(ViewBinding)
     */
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        return super.onCreateView(FragmentHomeBinding.inflate(inflater));
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
        observeUserState(this::updateUi);
        initializeButtonListeners();

        // TODO: Add splash screen till the app has been initialized (FirebaseApp, Network broadcasters, validating user's data, etc.).
    }

    /**
     * Called when the fragment is visible to the user and actively running.
     */
    @Override
    public void onResume() {
        super.onResume();
    }

    /**
     * Called when the Fragment is no longer resumed.
     */
    @Override
    public void onPause() {
        super.onPause();
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
            mLocationManager.onRequestPermissionsResult(requireContext(), requestCode, grantResults);
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
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            if (result.getContents() == null) { // Used pressed the back button
                Toast.makeText(requireContext(), "Cancelled", Toast.LENGTH_LONG).show();
            } else {
                try {
                    // Access the qr code's payload
                    String hex = result.getContents();
                    // Convert it to an array of bytes
                    byte[] newEncodedBytes = EncryptionManager.hexStringToByteArray(hex);
                    // Decrypt it
                    String decodedWithHex = new EncryptionManager().decrypt(newEncodedBytes);
                    mOperatorViewModel.receiveBooking(
                            // Access the previously stored lot reference
                            (DocumentReference) getIntentIntegrator().getMoreExtras().get("ref"),
                            // Convert the string into a Booking object and access its unique id
                            Booking.toBooking(decodedWithHex).generateUniqueId()
                    );
                } catch (Exception ignored) {
                }

            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    /**
     * Attach on click listeners to the 'scan lot' and 'parking map' buttons.
     * 'Scan lot' on click: open up the QR code scanner.
     * 'Parking map' on click: transition to the Google maps fragment.
     */
    private void initializeButtonListeners() {
        // TODO: 10/02/2021 Implement the buttons and add descriptions below then
        getBinding().fragmentHomeBtnScanLot.setOnClickListener(v -> {
            Toast.makeText(requireContext(), "Not implemented yet!", Toast.LENGTH_SHORT).show();
        });

        // Attach listener to "Parking Map" button
        getBinding().fragmentHomeBtnNavToMap.setOnClickListener(v -> {
            // Initialize the SingleLocationManager object
            if (mLocationManager == null) {
                mLocationManager = LocationManager.createSingleUpdateHelper(requireContext(), this);
            } else {
                mLocationManager.prepareCallback();
            }
            // Request for the user's latest known location
            Log.d(TAG, "requestUserLocationUpdates");
            mLocationManager.requestUserLocationUpdates(this);
        });
    }

    /**
     * Update the Ui based on the given {@link LoggedInUser} instance.
     * If the user is not logged in, or is but is not as an operator,
     * everything related to the operator is hidden.
     *
     * @param loggedInUser The current {@link LoggedInUser} instance.
     */
    private void updateUi(LoggedInUser loggedInUser) {
        if (loggedInUser == null) {
            // If operator logged out, remove observer to its parking lot
            if (mDatabaseObserver != null) mDatabaseObserver.unregisterLifecycleObserver();
            // Hide anything related to parking lot from the user
            getBinding().fragmentHomeCvLotInfo.setVisibility(View.GONE);
            return;
        }

        // TODO: 10/02/2021 Display QR Code scanner button:
        //  users use it to can scan the QR Code of an operator nad
        //  they will be navigated to the payment screen (see Instant App)

        if (loggedInUser.isOperator()) {
            // Is an operator but not a user
            initializeOperator();
        } else {
            // User is not an operator
            // Hide anything related to parking lot from the user
            getBinding().fragmentHomeCvLotInfo.setVisibility(View.GONE);
        }
    }

    /**
     * Initializes both the Ui and the database logic related to the operator.
     */
    private void initializeOperator() {
        // TODO: 19/01/2021 Encapsulate all operator logic to a fragment and simply inflate it
        // Set up the Ui for the operator
        getBinding().fragmentHomeCvLotInfo.setVisibility(View.VISIBLE);
        // Initialize the OperatorViewModel
        mOperatorViewModel = new ViewModelProvider(this,
                new OperatorViewModelFactory()).get(OperatorViewModel.class);
        // Attach observer to update the view's parking lot info whenever it changes
        mOperatorViewModel.getParkingLotState().observe(getViewLifecycleOwner(),
                this::updateLotContents); // Display the parking lot's contents

        if (getGlobalStateViewModel().getUser() == null) return;

        String operatorId = getGlobalStateViewModel().getUser().getUserId();
        // Get the operator's lot info from the database.
        getParkingLotInfo(operatorId);

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

    public void setUpScanBookingButton(DocumentReference lotRef) {
        getBinding().fragmentHomeBtnScanBooking.setOnClickListener(v -> {
            // Only available to operators
            getIntentIntegrator()
                    .addExtra("ref", lotRef)
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
                    .setPrompt("Please place the scanner on a booking generated QR Code.")
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
        // Compose the TextViews' text that display the lot's name and capacity.
        final String availability = userParkingLot.getLotAvailability(requireContext());
        final String name = HomeFragment.this.getString(R.string.lot_name) + " " + userParkingLot.getLotName();
        // Update their texts with the above ones
        getBinding().fragmentHomeTxtLotCapacity.setText(availability);
        getBinding().fragmentHomeTxtLotName.setText(name);
    }

    /**
     * Displays a view giving the operator the option
     * to register a parking lot.
     */
    private void displayLotRegistrationLayout() {
        checkVisibilityOfAppropriateLayout(View.GONE, View.VISIBLE);
        getBinding().fragmentHomeClShowLotInfo.setVisibility(View.GONE); // Hide showLotInfo
        getBinding().fragmentHomeClRegisterLotInfo.setVisibility(View.VISIBLE); // Show registerLotInfo
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
     *
     * @param lotInfoVisibility     The new visibility of the lot info layout.
     * @param registerLotVisibility The new visibility of register lot layout.
     */
    private void checkVisibilityOfAppropriateLayout(int lotInfoVisibility, int registerLotVisibility) {
        updateVisibilityOf(getBinding().fragmentHomeClShowLotInfo, lotInfoVisibility); // showLotInfo
        updateVisibilityOf(getBinding().fragmentHomeClRegisterLotInfo, registerLotVisibility); // registerLotInfo
    }

    /**
     * Updates the visibility status of the given view with the
     * specified visibility.
     *
     * @param view       The view to has its visibility updated.
     * @param visibility The new visibility of the given view.
     */
    private void updateVisibilityOf(@NonNull View view, int visibility) {
        if (view.getVisibility() != visibility) {
            view.setVisibility(visibility); // showLotInfo
        }
    }

    /**
     * Hooks up both "increment" and "decrement" buttons with on click listeners.
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
     * @see LocationManager#requestUserLocationUpdates(Fragment)
     */
    @Override
    public void onLocationResult(LocationResult locationResult) {
        Log.d(TAG, "onLocationResult");
        if (locationResult != null) {
            // Access the user's latest location
            Location userLatestLocation = locationResult.getLastLocation();
            Toast.makeText(requireContext(), userLatestLocation.toString(), Toast.LENGTH_SHORT).show();

            // Pass it to the ParkingMapFragment
            EventBus.getDefault().postSticky(new LatLng(userLatestLocation.getLatitude(), userLatestLocation.getLongitude()));
            // Navigate to the ParkingMapFragment
            getNavController(requireActivity())
                    .navigate(
                            HomeFragmentDirections.actionHomeToParkingMap()
                    );
        } else {
            // Inform the user something wrong happened
            Toast.makeText(requireContext(), getString(R.string.error_retrieving_location), Toast.LENGTH_SHORT).show();
        }
    }
}