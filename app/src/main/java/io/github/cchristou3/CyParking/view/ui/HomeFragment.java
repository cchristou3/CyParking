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

import static io.github.cchristou3.CyParking.view.ui.ParkingMapFragment.LOCATION_PERMISSION_REQUEST_CODE;

/**
 * Purpose: <p>Show to the user all available options</p>
 *
 * @author Charalambos Christou
 * @version 1.0 29/10/20
 */
public class HomeFragment extends Fragment {

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

    /**
     * Invoked at the completion of onCreateView. Initializes fragment's ViewModel.
     * Lastly, it attaches a listener to our UI button
     *
     * @param view               The view of the fragment
     * @param savedInstanceState A bundle which contains info about previously stored data
     */
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        view.findViewById(R.id.fragment_home_nav_button).setOnClickListener(v -> {
            mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(requireContext());
            getLastKnownLocationOfUser(v);
        });
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
                                    Navigation.findNavController(view).navigate(R.id.action_home_to_parking_map);
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
}