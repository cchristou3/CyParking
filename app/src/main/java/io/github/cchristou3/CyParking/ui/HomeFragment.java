package io.github.cchristou3.CyParking.ui;

import android.location.Location;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.google.android.gms.location.LocationResult;
import com.google.android.gms.maps.model.LatLng;

import org.greenrobot.eventbus.EventBus;
import org.jetbrains.annotations.NotNull;

import io.github.cchristou3.CyParking.R;
import io.github.cchristou3.CyParking.data.interfaces.LocationHandler;
import io.github.cchristou3.CyParking.data.interfaces.Navigable;
import io.github.cchristou3.CyParking.data.manager.AuthObserver;
import io.github.cchristou3.CyParking.data.manager.LocationManager;

/**
 * Purpose: <p>Show to the user all available options</p>
 *
 * @author Charalambos Christou
 * @version 4.0 23/12/20
 */
public class HomeFragment extends Fragment implements Navigable, LocationHandler {

    // Fragment variables
    private LocationManager mLocationManager;

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
        // Attach listener to "Parking Map" button
        view.findViewById(R.id.fragment_home_btn_nav_to_map).setOnClickListener(v -> {
            // Initialize the SingleLocationManager object
            if (mLocationManager == null)
                mLocationManager = new LocationManager(requireContext(), this, true);
            // Request for the user's latest known location
            mLocationManager.requestUserLocationUpdates(this);
        });
        // Attach listener to "Register Parking lot" button
        view.findViewById(R.id.fragment_home_mbtn_register_parking_lot).setOnClickListener(v ->
                // Navigate to the parking lot registration form
                Navigation.findNavController(getActivity().findViewById(R.id.fragment_main_host_nv_nav_view))
                        .navigate(R.id.action_nav_home_to_nav_register_lot_fragment));

        // Get a reference to the UI's CardView
        final CardView lotRegistrationCardView = view.findViewById(R.id.fragment_home_cv_lot_info);
        // Set up an Auth state observer
        AuthObserver.newInstance(currentUser -> {
            // TODO: Create a global state for the current user
            if (currentUser != null) { // Check whether logged in.
                // TODO: Access roles (locally / cloud database)
                // if Operator
                if (true /* isOperator == true ? */) {
                    // TODO: Fetch data about his/her lot
                    // If none then show to the user the registration button
                    // Otherwise, show his/her Lot details

                    // Make it visible to the user if it's hidden
                    if (lotRegistrationCardView.getVisibility() == View.GONE) {
                        lotRegistrationCardView.setVisibility(View.VISIBLE);
                    }
                    return; // Terminate method. Code below will not execute
                }
            }
            // Hide it from the user if it's visible
            if (lotRegistrationCardView.getVisibility() == View.VISIBLE) {
                lotRegistrationCardView.setVisibility(View.GONE);
            }
        }).registerObserver(getLifecycle());
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
     * Navigates from the current Fragment subclass to the
     * {@link io.github.cchristou3.CyParking.ui.user.login.AuthenticatorFragment}.
     */
    @Override
    public void toAuthenticator() {
        Navigation.findNavController(getActivity().findViewById(R.id.fragment_main_host_nv_nav_view)).navigate(R.id.action_nav_home_to_nav_authenticator_fragment);
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
     * {@link io.github.cchristou3.CyParking.ui.HomeFragment}.
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