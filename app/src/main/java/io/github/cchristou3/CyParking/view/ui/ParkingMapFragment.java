package io.github.cchristou3.CyParking.view.ui;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.widget.ContentLoadingProgressBar;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.gson.Gson;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;

import io.github.cchristou3.CyParking.R;
import io.github.cchristou3.CyParking.view.data.pojo.parking.PrivateParkingResultSet;
import io.github.cchristou3.CyParking.view.data.repository.ParkingRepository;
import io.github.cchristou3.CyParking.view.data.repository.Utility;

/**
 * purpose: View all nearby parking.
 * The user can select a parking and view more details.
 * Lastly, there is an option to book a specific parking.
 *
 * @author Charalambos Christou
 * @version 1.0 29/10/20
 */

// TODO: Save state of info layout and clicked marker when orientation is changed
public class ParkingMapFragment extends Fragment implements GoogleMap.OnMapClickListener,
        GoogleMap.OnMarkerClickListener, GoogleMap.OnMapLoadedCallback {

    // Constant variables
    public static final String TAG = "cchristou3-CyParking";
    public static final int LOCATION_PERMISSION_REQUEST_CODE = 321;
    public static final long INTERVAL_TIME = 5000L;
    private static final int ZOOM_LEVEL = 16;
    final private HashMap<Marker, PrivateParkingResultSet> mHashMapToValuesOfMarkersInScene = new HashMap<>();
    // Location related variables
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private LocationCallback mLocationCallback;
    private LocationRequest mLocationRequest;
    private final OnMapReadyCallback callback = new OnMapReadyCallback() {

        /**
         * Manipulates the map once available.
         * This callback is triggered when the map is ready to be used.
         * This is where we can add markers or lines, add listeners or move the camera.
         * If Google Play services is not installed on the device, the user will be prompted to
         * install it inside the SupportMapFragment. This method will only be triggered once the
         * user has installed Google Play services and returned to the app.
         */
        @Override
        public void onMapReady(GoogleMap googleMap) {
            mGoogleMap = googleMap;
            registerCurrentLocationUpdates();
            // TODO: Create a custom InfoWindowsAdapter
            //mGoogleMap.setInfoWindowAdapter(new CustomInfoWindowAdapter(this, mHashMapToValuesOfMarkersInScene));

            // Add listeners to the markers + map
            mGoogleMap.setOnMarkerClickListener(ParkingMapFragment.this);
            mGoogleMap.setOnMapClickListener(ParkingMapFragment.this);
            mGoogleMap.setOnMapLoadedCallback(ParkingMapFragment.this);

            mGoogleMap.moveCamera(CameraUpdateFactory.zoomTo(ZOOM_LEVEL));
        }
    };
    private boolean triggeredFirstDatabaseUpdate;
    private GoogleMap mGoogleMap;
    private LatLng mCurrentLatLngOfUser;
    private boolean mRegistered;
    // Fragment variables
    private View mView;
    private PrivateParkingResultSet mSelectedPrivateParking;
    private ContentLoadingProgressBar mContentLoadingProgressBar;

    /**
     * Initialises the fragment. Uses the EventBus, to get access to data send by the previous fragment.
     *
     * @param savedInstanceState A bundle which contains info about previously stored data
     */
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /*
         * By registering the EventBus we have access to both ongoing and sticky events.
         * The onResultReceived method is our subscriber.
         * If "sticky" is set to true, the subscriber method delivers the most recent sticky event (posted with
         * {@link EventBus#postSticky(Object)}) to this subscriber (if event available).
         */
        EventBus.getDefault().register(this);
        // onResultReceived gets invoked, no need to further listen for updates. Thus, unregister.
        EventBus.getDefault().unregister(this);
    }

    /**
     * Our event subscriber method. In this case, the event is the POJO PrivateParking.
     * Receives the latest event that was posted using EventBus.getInstance().postSticky([object]),
     * once the EventBus is registered.
     *
     * @param latLng The latitude and longitude of the user which were acquired in the previous fragment (HomeFragment)
     */
    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    public void onResultReceived(LatLng latLng) {
        mCurrentLatLngOfUser = latLng;
    }

    /**
     * Initialises the activity.
     * TODO: Builds the activity's Toolbar and Drawer navigation.
     * Requests a Support Fragment to place the Google Map.
     * Retrieves the location of the user from previous activity and sends a HTTPS request
     * to the backend to retrieve all nearby parking. While retrieving the data, a loading progress
     * bar is shown for feedback. Lastly, Location and activity members are initialised.
     *
     * @param savedInstanceState A bundle which contains info about previously stored data
     * @param inflater           The object which will inflate (create) our layout
     * @param container          ViewGroup container
     */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_parking_map, container, false);

        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(requireContext());
        mLocationCallback = getLocationCallback();

        // Initialize location based variables
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(INTERVAL_TIME);
        mRegistered = false;
        triggeredFirstDatabaseUpdate = false;

        mView.findViewById(R.id.fragment_parking_map_btn_booking).setOnClickListener(v -> {
            if (mSelectedPrivateParking != null) {
                // Navigate to the ParkingBookingFragment
                Log.d(TAG, "onCreateView: " + mSelectedPrivateParking.getDocumentID());
                EventBus.getDefault().postSticky(mSelectedPrivateParking);
                Navigation.findNavController(mView).navigate(R.id.action_nav_parking_map_fragment_to_parking_booking_fragment);
            } else {
                Toast.makeText(requireContext(), "Oops something went wrong!", Toast.LENGTH_SHORT).show();
            }
        });

        // Show a progress bar to inform the user that the data is loading
        mContentLoadingProgressBar = mView.findViewById(R.id.fragment_parking_map_pb_loadingMarkers);
        mContentLoadingProgressBar.getIndeterminateDrawable().setColorFilter(0xFFFF0000, android.graphics.PorterDuff.Mode.MULTIPLY);
        mContentLoadingProgressBar.show();

        fetchPrivateParking(mCurrentLatLngOfUser);

        return mView;
    }

    /**
     * Invoked at the completion of onCreateView. Initializes fragment's ViewModel.
     * Lastly, it instantiates the fragment with a GoogleMaps instance.
     *
     * @param view               The view of the fragment
     * @param savedInstanceState A bundle which contains info about previously stored data
     */
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        SupportMapFragment mapFragment =
                (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.fragment_parking_map_fcv_map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(callback);
        }
    }

    /**
     * Gets invoked after onCreate Callback.
     * Initialises an observer to a node of the database. When changes occur on specified node,
     * parking data is being re-fetched.
     */
    @Override
    public void onStart() {
        super.onStart();

        // Initialize a firebase firestore observer to inform us about changes in the DB
        // By passing the activity (this), the listener will get removed automatically when the
        // activity calls onStop()
        // The callback of the SnapshotListener gets triggered straight away when we add it
        FirebaseFirestore.getInstance().collection("private_parking").addSnapshotListener(requireActivity(), (value, error) -> {
            // ref: https://firebase.google.com/docs/firestore/query-data/listen#view_changes_between_snapshots
            if (!triggeredFirstDatabaseUpdate) { // To ensure that when we add the listener, the block of code will not get executed
                triggeredFirstDatabaseUpdate = true;
                return;
            } else if (error != null) {
                Log.w(TAG, "listen:error", error);
                return;
            }
            if (value != null) {
                for (DocumentChange dc : value.getDocumentChanges()) {
                    switch (dc.getType()) {
                        case ADDED:
                            //Log.d(TAG, "New parking: " + dc.getDocument().getData());
                            break;
                        case MODIFIED:
                            //Log.d(TAG, "Modified parking: " + dc.getDocument().getData());
                            break;
                        case REMOVED:
                            //Log.d(TAG, "Removed parking: " + dc.getDocument().getData());
                            break;
                    }
                }
            }
            if (mCurrentLatLngOfUser != null && ParkingMapFragment.this.isAdded()) { // re-fetch the data from the server
                Log.d(TAG, "onStart: Fetching data");
                fetchPrivateParking(mCurrentLatLngOfUser);
                Toast.makeText(requireContext(), "Database change triggered!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * Unregisters location updates.
     */
    @Override
    public void onPause() {
        super.onPause();
        unRegisterCurrentLocationUpdates();
    }

    /**
     * Gets invoked after the user has been asked for a permission for a given package.
     * If permission was granted, register a request for the user's latest known location.
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
                registerCurrentLocationUpdates();

            } else { // if permission is not granted

                // decide what you want to do if you don't get permissions
                Toast.makeText(requireContext(), "Permission is not granted!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    /**
     * Registers the FusedLocationProviderClient instance for updates if it was
     * not already registered.
     */
    private void registerCurrentLocationUpdates() {
        if (!mRegistered) {
            if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                if (Build.VERSION.SDK_INT >= 23) // Marshmallow
                    ActivityCompat.requestPermissions(requireActivity(), new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
            } else { // no need to ask for permission
                // start to find location...
                mFusedLocationProviderClient.requestLocationUpdates(mLocationRequest, mLocationCallback, requireActivity().getMainLooper());
                mRegistered = true;
            }
        }
    }

    /**
     * Unregisters the FusedLocationProviderClient instance from updates if it was registered.
     */
    private void unRegisterCurrentLocationUpdates() {
        if (mRegistered) {
            mFusedLocationProviderClient.removeLocationUpdates(mLocationCallback);
            mRegistered = false;
        }
    }

    /**
     * Creates and sends an HTTPS request to our Backend.
     * Requests all the parking locations and for each, a Marker is placed
     * on the map.
     * When such a request occurs, a cloud function is executed, which filters
     * the parking locations. Only the ones which are nearby the user are sent back
     * too the client.
     *
     * @param latLng The latest recorded lattitude and longitude of the user.
     */
    public void fetchPrivateParking(@NotNull LatLng latLng) {
        RequestQueue requestQueue = Volley.newRequestQueue(requireContext());
        // TODO: While the user waits -> show loading dialog
        //  OR -> Lazy load the markers
        //  -> animate user's location ref: https://codinginfinite.com/android-example-animate-marker-map-current-location/

        double userLatitude = latLng.latitude;
        double userLongitude = latLng.longitude;
        final Request<String> requestForPrivateParking = new StringRequest(Request.Method.GET,
                getResources().getString(R.string.firestore_api_url) + "?latitude=" + userLatitude + "&longitude=" + userLongitude,
                response -> {
                    // Convert json object into an array of objects
                    final PrivateParkingResultSet[] privateParking = new Gson().fromJson(response, PrivateParkingResultSet[].class);

                    // clear all the current markers from the map except fromt the user's one.
                    for (Marker marker : mHashMapToValuesOfMarkersInScene.keySet()) { // the key is of type Marker
                        marker.remove();
                    }

                    // Traverse through the fetched objects and add them to the map.
                    for (PrivateParkingResultSet parking : privateParking) {
                        // Get the coordinates of the parking
                        try {
                            double parkingLatitude = parking.getParking().getCoordinates().get(ParkingRepository.LATITUDE_KEY);
                            double parkingLongitude = parking.getParking().getCoordinates().get(ParkingRepository.LONGITUDE_KEY);
                            LatLng parkingLatLng = new LatLng(parkingLatitude, parkingLongitude);
                            // Add a marker to the parking's coordinates
                            Marker marker = mGoogleMap.addMarker(new MarkerOptions()
                                    .position(parkingLatLng)
                                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));

                            // Add to hashmap to keep track of each marker's corresponding PrivateParking object
                            mHashMapToValuesOfMarkersInScene.put(marker, parking);
                        } catch (NullPointerException e) {
                            Log.d(TAG, "Parking producing null coordinates!");
                        }
                    }
                }, error -> {
            // TODO: inform the user
            Toast.makeText(requireContext(), "Unexpected error occurred!\n" + error.getMessage(), Toast.LENGTH_SHORT).show();
            Log.e(TAG, "Volley error: " + error.getMessage());
        });
        requestQueue.add(requestForPrivateParking);
        requestQueue.addRequestFinishedListener((RequestQueue.RequestFinishedListener<String>) request -> {
            if (mContentLoadingProgressBar.isShown()) mContentLoadingProgressBar.hide();
        });
    }

    /**
     * Gets triggered whenever the user taps on the map.
     * If the user did not tapped on a marker or the the user
     * then the info plane is hidden.
     *
     * @param latLng The position of the map whcich the user clicked
     */
    @Override
    public void onMapClick(@NotNull LatLng latLng) {
        // Create a hash map based on clicked location
        final HashMap<String, Double> mapOfClickedLocation = new HashMap<String, Double>() {
            {
                put(ParkingRepository.LATITUDE_KEY, latLng.latitude);
                put(ParkingRepository.LONGITUDE_KEY, latLng.longitude);
            }
        };
        // Check whether the same coordinates exist inside our global hash map
        // If not then the user did not tap any of the markers. Thus, we hide the info layout.
        boolean containsValue = mHashMapToValuesOfMarkersInScene.containsValue(mapOfClickedLocation);
        if (!containsValue) changeInfoLayoutVisibilityTo(View.GONE);
    }

    /**
     * Gets triggered whenever a marker gets tapped.
     * Keeps track of the latest marker that was clicked.
     * Makes the info plane visible to show additional info.
     *
     * @param marker The marker which the user clicked
     * @return A boolean value
     */
    @Override
    public boolean onMarkerClick(Marker marker) {
        showDetails(marker);
        mSelectedPrivateParking = mHashMapToValuesOfMarkersInScene.get(marker);
        return false;
    }

    /**
     * Gets triggered every time we receive info about the user's location.
     * Updates the value of the data member mCurrentLatLngOfUser with the received
     * location. Also, a marker is placed on the user's current Lat Lng.
     * Further, a request is send for the private parking if we already did not
     * request it.
     *
     * @return A locationCallback interface
     */
    private LocationCallback getLocationCallback() {
        return new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);
                if (locationResult == null) return;

                // Update the user's location
                Location receivedLocation = locationResult.getLastLocation();
                mCurrentLatLngOfUser = new LatLng(receivedLocation.getLatitude(), receivedLocation.getLongitude());

                // TODO: replace drawable of my location
                Drawable drawable = ResourcesCompat.getDrawable(getResources(), R.drawable.ic_menu_gallery, null);
                if (drawable != null) {
                    drawable.setAlpha(50); // = Opacity
                    Bitmap bitmapForMarker = Utility.drawableToBitmap(drawable);

                    // TODO: Keep track of the user's marker. When thier location changes, remove current one
                    //  and add a new one.
                    Marker marker = mGoogleMap.addMarker(new MarkerOptions()
                            .title("Title")
                            .position(mCurrentLatLngOfUser)
                            .icon(BitmapDescriptorFactory.fromBitmap(bitmapForMarker)) // TODO: Replace with an actual icon
                            .snippet("Current Location!")
                            .title("Me"));

                    // Smoothly moves the camera to location
                    mGoogleMap.animateCamera(CameraUpdateFactory.newLatLng(marker.getPosition()));
                } else {
                    Log.d(TAG, "onLocationResult: Drawable was null!");
                }

            }
        };
    }

    /**
     * Shows a plane which contains information about the marker's
     * associated Parking object
     *
     * @param marker The marker which was tapped on the map.
     */
    private void showDetails(Marker marker) {
        Toast.makeText(requireContext(), "Marker has been clicked!", Toast.LENGTH_SHORT).show();
        changeInfoLayoutVisibilityTo(View.VISIBLE);
        // Get references to the view elements
        TextView nameTextView = mView.findViewById(R.id.fragment_parking_map_txt_name);
        TextView priceTextView = mView.findViewById(R.id.fragment_parking_map_txt_price);
        TextView capacityTextView = mView.findViewById(R.id.fragment_parking_map_txt_capacity);
        // Get the corresponding hash map object
        PrivateParkingResultSet dataOfSelectedMarker = mHashMapToValuesOfMarkersInScene.get(marker);

        // Set their texts the according the clicked marker's data
        String nameTextViewString = "Opening hours: " + ((dataOfSelectedMarker != null) ? dataOfSelectedMarker.getParking().getOpeningHours() : "Unavailable");
        String capacityTextViewString = "Capacity: " + ((dataOfSelectedMarker != null) ?
                dataOfSelectedMarker.getParking().getAvailableSpaces() + " / " + dataOfSelectedMarker.getParking().getCapacity() : "Unavailable");
        String priceTextViewString = "Price: " + ((dataOfSelectedMarker != null) ? dataOfSelectedMarker.getParking().getPricingList() : "Unavailable");
        nameTextView.setText(nameTextViewString);
        capacityTextView.setText(capacityTextViewString);
        priceTextView.setText(priceTextViewString);
    }

    /**
     * Changes the visibility of the info plane based on the specified attribute
     *
     * @param visibility The state of the visibility (E.g. View.Gone / View.VISIBLE / View.INVISIBLE)
     */
    private void changeInfoLayoutVisibilityTo(final int visibility) {
        // Get a reference to the hidden info layout
        LinearLayout infoLayout = mView.findViewById(R.id.fragment_parking_map_li_infoLayout);
        // Check if the current visibility is already set with the given one (to avoid unnecessary actions)
        if (infoLayout.getVisibility() != visibility) infoLayout.setVisibility(visibility);
    }

    @Override
    public void onMapLoaded() {
        Log.d(TAG, "onMapLoaded: Zooming...");
        mGoogleMap.moveCamera(CameraUpdateFactory.zoomTo(ZOOM_LEVEL));
    }
}