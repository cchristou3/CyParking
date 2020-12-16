package io.github.cchristou3.CyParking.view.ui;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;

import org.greenrobot.eventbus.EventBus;
import org.jetbrains.annotations.NotNull;

import io.github.cchristou3.CyParking.R;
import io.github.cchristou3.CyParking.view.data.interfaces.Navigate;
import io.github.cchristou3.CyParking.view.data.manager.AuthObserver;

import static io.github.cchristou3.CyParking.view.ui.parking.slots.ParkingMapFragment.LOCATION_PERMISSION_REQUEST_CODE;

/**
 * Purpose: <p>Show to the user all available options</p>
 *
 * @author Charalambos Christou
 * @version 2.0 15/12/20
 */
public class HomeFragment extends Fragment implements Navigate {

    // Fragment variables
    private com.google.android.gms.location.FusedLocationProviderClient mFusedLocationProviderClient;

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

    @NonNull
    @Override
    public String toString() {
        return "Yep, this is the home fragment!";

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
            // Initialize the FusedLocationProviderClient instance
            mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(requireContext());
            // Request for the user's latest known location
            getLastKnownLocationOfUser(v);
        });
        // Attach listener to "Register Parking lot" button
        view.findViewById(R.id.fragment_home_mbtn_register_parking_lot).setOnClickListener(v -> {
            // Navigate to the parking lot registration form

            Navigation.findNavController(getActivity().findViewById(R.id.fragment_main_host_nv_nav_view))
                    .navigate(R.id.action_nav_home_to_nav_register_lot_fragment);

        });

        // Get a reference to the UI's CardView
        final CardView lotRegistrationCardView = view.findViewById(R.id.fragment_home_cv_lot_info);
        // Set up an Auth state observer
        AuthObserver.newInstance(currentUser -> {
            if (currentUser != null) { // Check whether logged in.
                // TODO: Access roles (locally / cloud database)
                // if Operator
                if (true /* isOperator == true ? */) {
                    // Make it visible to the user if it's hidden
                    if (lotRegistrationCardView.getVisibility() == View.GONE) {
                        lotRegistrationCardView.setVisibility(View.VISIBLE);
                    }
                    return;
                }
            }
            // Hide it from the user if it's visible
            if (lotRegistrationCardView.getVisibility() == View.VISIBLE) {
                lotRegistrationCardView.setVisibility(View.GONE);
            }
        }).registerObserver(getLifecycle());
    }

    /**
     * Requests for the location of the user (only once)
     * with no further updates. If successful, the user is navigated to another
     * screen (the user's location is passed to the next activity). Otherwise,
     * inform the user about the error.
     */
    private void getLastKnownLocationOfUser(View view) {
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= 23) // Marshmallow
                ActivityCompat.requestPermissions(requireActivity(), new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
        } else { // no need to ask for permission
            // start to find location...
            if (mFusedLocationProviderClient != null) {
                mFusedLocationProviderClient.requestLocationUpdates(new LocationRequest()
                                .setNumUpdates(1)
                                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY),
                        new LocationCallback() {
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
                                    Toast.makeText(getActivity(), "Your location could not be processed! Check your GPS settings!", Toast.LENGTH_SHORT).show();
                                }
                            }
                        }, requireActivity().getMainLooper());
            }

        }
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

        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {

            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                // start to find location..
                getLastKnownLocationOfUser(requireView());

            } else { // if permission is not granted

                // decide what you want to do if you don't get permissions
                Toast.makeText(getContext(), "Permission is not granted!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    /**
     * Navigates from the current Fragment subclass to the
     * {@link io.github.cchristou3.CyParking.view.ui.user.login.AuthenticatorFragment}.
     */
    @Override
    public void toAuthenticator() {
        Navigation.findNavController(getActivity().findViewById(R.id.fragment_main_host_nv_nav_view)).navigate(R.id.action_nav_home_to_nav_authenticator_fragment);
    }

    /**
     * Navigates from the current Fragment subclass to the
     * {@link io.github.cchristou3.CyParking.view.ui.parking.slots.viewBooking.ViewBookingsFragment}.
     */
    @Override
    public void toBookings() {
        Navigation.findNavController(getActivity().findViewById(R.id.fragment_main_host_nv_nav_view))
                .navigate(R.id.action_nav_home_to_nav_view_bookings);
    }

    /**
     * Navigates from the current Fragment subclass to the
     * {@link io.github.cchristou3.CyParking.view.ui.user.AccountFragment}.
     */
    @Override
    public void toAccount() {
        Navigation.findNavController(getActivity().findViewById(R.id.fragment_main_host_nv_nav_view))
                .navigate(R.id.action_nav_home_to_nav_account);
    }

    /**
     * Navigates from the current Fragment subclass to the
     * {@link io.github.cchristou3.CyParking.view.ui.user.feedback.FeedbackFragment}.
     */
    @Override
    public void toFeedback() {
        Navigation.findNavController(getActivity().findViewById(R.id.fragment_main_host_nv_nav_view))
                .navigate(R.id.action_nav_home_to_nav_feedback);
    }

    /**
     * Navigates from the current Fragment subclass to the
     * {@link io.github.cchristou3.CyParking.view.ui.HomeFragment}.
     */
    @Override
    public void toHome() {
        // Already in this screen. Thus, no need to implement this method.
    }
}