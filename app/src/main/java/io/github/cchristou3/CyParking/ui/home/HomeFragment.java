package io.github.cchristou3.CyParking.ui.home;

import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.google.android.gms.location.LocationResult;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.firestore.DocumentReference;

import org.greenrobot.eventbus.EventBus;
import org.jetbrains.annotations.NotNull;

import io.github.cchristou3.CyParking.AuthStateViewModel;
import io.github.cchristou3.CyParking.AuthStateViewModelFactory;
import io.github.cchristou3.CyParking.MainHostActivity;
import io.github.cchristou3.CyParking.R;
import io.github.cchristou3.CyParking.data.interfaces.LocationHandler;
import io.github.cchristou3.CyParking.data.interfaces.Navigable;
import io.github.cchristou3.CyParking.data.manager.DatabaseObserver;
import io.github.cchristou3.CyParking.data.manager.LocationManager;
import io.github.cchristou3.CyParking.data.pojo.parking.lot.ParkingLot;
import io.github.cchristou3.CyParking.data.pojo.user.LoggedInUser;

import static io.github.cchristou3.CyParking.MainHostActivity.TAG;

/**
 * Purpose: <p>Show to the user all available action options</p>
 * If the user is logged in and has a role of "Operator"
 * then a lot information area is shown. If the user does not
 * have a lot registered, an option for registering one is displayed.
 * Otherwise, if the user has a lot, then display critical info about it.
 * <p>
 * In terms of Authentication, this is achieved by communicating with the hosting
 * activity {@link MainHostActivity} via the {@link AuthStateViewModel}.
 * </p>
 *
 * @author Charalambos Christou
 * @version 5.0 25/12/20
 */
public class HomeFragment extends Fragment implements Navigable, LocationHandler {

    // Fragment variables
    private LocationManager mLocationManager;
    private AuthStateViewModel mAuthStateViewModel;
    // Members related to the Operator
    private OperatorViewModel mOperatorViewModel;
    private DatabaseObserver mDatabaseObserver;

    /**
     * Inflates our fragment's view.
     *
     * @param inflater           Inflater which will inflate our view
     * @param container          The parent view
     * @param savedInstanceState A bundle which contains info about previously stored data
     * @return The view of the fragment
     */
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
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
        initializeAuthStateViewModel(view);

        // Attach listener to "Parking Map" button
        view.findViewById(R.id.fragment_home_btn_nav_to_map).setOnClickListener(v -> {
            // Initialize the SingleLocationManager object
            if (mLocationManager == null)
                mLocationManager = new LocationManager(requireContext(), this, true);
            // Request for the user's latest known location
            mLocationManager.requestUserLocationUpdates(this);
        });

        // TODO: Add loading effect till the UI has been initialized
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
        mLocationManager.onRequestPermissionsResult(requireContext(), requestCode, grantResults);
    }

    /**
     * Initialize the AuthStateViewModel with ViewModelStoreOwner
     * the hosting activity {@link MainHostActivity} and
     * adds an observer to the user's auth state.
     *
     * @param view The user interface of the fragment.
     */
    private void initializeAuthStateViewModel(View view) {
        // Initialize the its AuthStateViewModel
        mAuthStateViewModel = new ViewModelProvider(requireActivity(), new AuthStateViewModelFactory())
                .get(AuthStateViewModel.class);

        // NOTE: Observer gets invoked once the state changes or when it has been initialized
        mAuthStateViewModel.getUserState().observe(getViewLifecycleOwner(), loggedInUser -> {
            updateUi(loggedInUser, view);
        });
    }

    /**
     * Update the Ui based on the given {@link LoggedInUser} instance.
     * If the user is not logged in, or is but is not as an operator,
     * everything related to the operator is hidden.
     *
     * @param loggedInUser The current {@link LoggedInUser} instance.
     * @param view         The user interface of the fragment.
     */
    private void updateUi(LoggedInUser loggedInUser, @NotNull View view) {
        final CardView lotCardView = view.findViewById(R.id.fragment_home_cv_lot_info);
        if (loggedInUser == null) {
            // If operator logged out, remove observer to its parking lot
            if (mDatabaseObserver != null) mDatabaseObserver.unregisterLifecycleObserver();
            // Hide anything related to parking lot from the user
            lotCardView.setVisibility(View.GONE);
            return;
        }

        // Check whether logged in.
        boolean isOperator = loggedInUser.getRoles().contains(MainHostActivity.OPERATOR);
        boolean isUser = loggedInUser.getRoles().contains(MainHostActivity.USER);
        if (!isOperator && !isUser) {
            return;// If neither stop here.
        }

        // Do not check for user role as
        // in this fragment the "User" role has not extra actions
        if (isOperator) {
            // Is an operator but not a user
            initializeOperator(view, lotCardView);
        } else { // Is a user but not an operator
            // Hide anything related to parking lot from the user
            lotCardView.setVisibility(View.GONE);
        }
    }

    /**
     * Initializes both the Ui and the database logic related to the operator.
     *
     * @param view     The user interface of the fragment.
     * @param cardView The view related to the operator.
     */
    private void initializeOperator(@NotNull View view, @NotNull View cardView) {
        // Set up the Ui for the operator
        cardView.setVisibility(View.VISIBLE);
        // Initialize the OperatorViewModel
        mOperatorViewModel = new ViewModelProvider(this).get(OperatorViewModel.class);
        // Attach observer to update the view's parking lot info whenever it changes
        mOperatorViewModel.getParkingLotState().observe(getViewLifecycleOwner(), parkingLot -> {
            // Display the parking lot's contents
            updateLotContents(view, parkingLot);
        });

        // Attach listener to "increment", "decrement" buttons
        if (mAuthStateViewModel.getUserState().getValue() == null) {
            return; // Do not attach another SnapshotListener if one is already in place
        }
        String email = mAuthStateViewModel.getUserState().getValue().getEmail();
        // Get the operator's lot info from the database.
        getParkingLotInfo(view, email);
    }

    /**
     * Initialize a self-management database observer.
     * The observer, handles retrieving the operators lot
     * and any of its changes.
     * On initial and consecutive data loads the Ui related to the
     * operator's lot is updated accordingly.
     *
     * @param view  The user interface of the fragment.
     * @param email The email of the operator.
     */
    private void getParkingLotInfo(View view, String email) {
        // Initialize the fragment's DatabaseObserver
        mDatabaseObserver = new DatabaseObserver(mOperatorViewModel.observeParkingLot(email),
                (value, error) -> {
                    Log.d(TAG, "getParkingLotInfo: ");
                    if (error != null || value == null) return;

                    if (value.getDocuments().size() == 0) { // The operator did not register lot yet
                        displayLotRegistrationLayout(view);
                        return;
                    }

                    // Remove listeners to the register lot button button TODO: Check
                    view.findViewById(R.id.fragment_home_mbtn_register_parking_lot).setOnClickListener(null);

                    // If the operator has registered a lot already, display its info
                    final ParkingLot userParkingLot = value.getDocuments().get(0).toObject(ParkingLot.class);

                    // Get a reference to the document
                    final DocumentReference ref = value.getDocuments().get(0).getReference();

                    // Hook up buttons with listeners
                    HomeFragment.this.setUpOperatorButtons(view, ref, userParkingLot);

                    // Trigger parking lot update.
                    if (userParkingLot != null) {
                        mOperatorViewModel.getParkingLotState().setValue(userParkingLot);
                    }
                });
        // Register it for lifecycle observation
        mDatabaseObserver.registerLifecycleObserver(getLifecycle());
        // TODO: addSnapshotListener remove activity and handle manually with mListenerRegistration
    }

    /**
     * Updates the views related to the lot with the specified {@link ParkingLot}
     * instance.
     *
     * @param view           The user interface of the fragment.
     * @param userParkingLot The updated version of the parking lot.
     */
    private void updateLotContents(@NotNull View view, @NotNull ParkingLot userParkingLot) {
        // Get references to the Ui
        final TextView lotCapacity = view.findViewById(R.id.fragment_home_txt_lot_capacity);
        final TextView lotName = view.findViewById(R.id.fragment_home_txt_lot_name);
        // Compose their texts
        final String availability = HomeFragment.this.getString(R.string.availability) + " "
                + (userParkingLot.getCapacity() - userParkingLot.getAvailableSpaces())
                + "/" + userParkingLot.getCapacity();
        final String name = HomeFragment.this.getString(R.string.lot_name) + " " + userParkingLot.getLotName();
        // Update their texts with the above ones
        lotCapacity.setText(availability);
        lotName.setText(name);
    }

    /**
     * Displays a view giving the operator the option
     * to register a parking lot.
     *
     * @param view The user interface of the fragment.
     */
    private void displayLotRegistrationLayout(@NotNull View view) {
        view.findViewById(R.id.fragment_home_cl_show_lot_info).setVisibility(View.GONE); // showLotInfo
        view.findViewById(R.id.fragment_home_cl_register_lot_info).setVisibility(View.VISIBLE); // registerLotInfo
        // Attach listener to "Register Parking lot" button
        view.findViewById(R.id.fragment_home_mbtn_register_parking_lot).setOnClickListener(v ->
                // Navigate to the parking lot registration form
                Navigation.findNavController(HomeFragment.this.requireActivity()
                        .findViewById(R.id.fragment_main_host_nv_nav_view))
                        .navigate(R.id.action_nav_home_to_nav_register_lot_fragment));
    }

    /**
     * Hooks up both "increment" and "decrement" buttons with on click listeners.
     *
     * @param view           The user interface of the fragment.
     * @param ref            A DocumentReference of the parking lot in the database.
     * @param userParkingLot The latest retrieved parking lot of the database.
     */
    private void setUpOperatorButtons(@NotNull View view, DocumentReference ref, ParkingLot userParkingLot) {
        view.findViewById(R.id.fragment_home_btn_increment).setOnClickListener(v -> {
            // If the lot has available spaces decrease its value by one
            // E.g. 40/40 -> do nothing
            if (userParkingLot.getAvailableSpaces() > 0) {
                mOperatorViewModel.incrementPersonCount(ref);
            }
        });
        view.findViewById(R.id.fragment_home_btn_decrement).setOnClickListener(v -> {
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
     * {@link io.github.cchristou3.CyParking.ui.user.login.AuthenticatorFragment}.
     */
    @Override
    public void toAuthenticator() {
        Navigation.findNavController(getActivity().findViewById(R.id.fragment_main_host_nv_nav_view))
                .navigate(R.id.action_nav_home_to_nav_authenticator_fragment);
    }

    /**
     * Navigates from the current Fragment subclass to the
     * {@link io.github.cchristou3.CyParking.ui.parking.slots.viewBooking.ViewBookingsFragment}.
     */
    @Override
    public void toBookings() {
        Navigation.findNavController(getActivity().findViewById(R.id.fragment_main_host_nv_nav_view))
                .navigate(R.id.action_nav_home_to_nav_view_bookings);
    }

    /**
     * Navigates from the current Fragment subclass to the
     * {@link io.github.cchristou3.CyParking.ui.user.AccountFragment}.
     */
    @Override
    public void toAccount() {
        Navigation.findNavController(getActivity().findViewById(R.id.fragment_main_host_nv_nav_view))
                .navigate(R.id.action_nav_home_to_nav_account);
    }

    /**
     * Navigates from the current Fragment subclass to the
     * {@link io.github.cchristou3.CyParking.ui.user.feedback.FeedbackFragment}.
     */
    @Override
    public void toFeedback() {
        Navigation.findNavController(getActivity().findViewById(R.id.fragment_main_host_nv_nav_view))
                .navigate(R.id.action_nav_home_to_nav_feedback);
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
        if (locationResult != null) {
            // Access the user's latest location
            Location userLatestLocation = locationResult.getLastLocation();
            // Pass it to the ParkingMapFragment
            EventBus.getDefault().postSticky(new LatLng(userLatestLocation.getLatitude(), userLatestLocation.getLongitude()));
            // Navigate to the ParkingMapFragment
            Navigation.findNavController(getActivity().findViewById(R.id.fragment_main_host_nv_nav_view)).navigate(R.id.action_home_to_parking_map);
        } else {
            // Inform the user something wrong happened
            Toast.makeText(requireContext(), getString(R.string.error_retrieving_location), Toast.LENGTH_SHORT).show();
        }
    }
}