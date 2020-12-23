package io.github.cchristou3.CyParking.ui.parking.lots;

import android.content.Intent;
import android.net.Uri;
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
import androidx.core.content.res.ResourcesCompat;
import androidx.core.widget.ContentLoadingProgressBar;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.gson.Gson;

import org.greenrobot.eventbus.EventBus;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Objects;

import io.github.cchristou3.CyParking.R;
import io.github.cchristou3.CyParking.data.interfaces.LocationHandler;
import io.github.cchristou3.CyParking.data.interfaces.Navigable;
import io.github.cchristou3.CyParking.data.manager.LocationManager;
import io.github.cchristou3.CyParking.data.manager.MarkerManager;
import io.github.cchristou3.CyParking.data.pojo.parking.lot.ParkingLot;
import io.github.cchristou3.CyParking.data.repository.ParkingRepository;
import io.github.cchristou3.CyParking.ui.HomeFragment;
import io.github.cchristou3.CyParking.ui.parking.slots.viewBooking.ViewBookingsFragment;
import io.github.cchristou3.CyParking.ui.user.AccountFragment;
import io.github.cchristou3.CyParking.ui.user.feedback.FeedbackFragment;
import io.github.cchristou3.CyParking.ui.user.login.AuthenticatorFragment;
import io.github.cchristou3.CyParking.utilities.Utility;

/**
 * Purpose: <p>View all nearby parking.
 * The user can select a parking and view more details.
 * Lastly, there is an option to book a specific parking.</p>
 * <p>
 * <p>Implementation wise the fragment uses a {@link #mLocationManager}
 * and a {@link MarkerManager} that are responsible for acquiring
 * the user's location and managing the markers on the Google Map.
 * </p>
 * <p>The fragment receives the user's location from {@link HomeFragment}
 * via the {@link EventBus}</p> class.
 * TODO:
 * 1. Save state of info layout and clicked marker when orientation is changed
 *
 * @author Charalambos Christou
 * @version 3.0 07/11/20
 */
public class ParkingMapFragment extends Fragment implements OnMapReadyCallback, GoogleMap.OnMapClickListener,
        GoogleMap.OnMarkerClickListener, Navigable, LocationHandler {

    // Constant variables
    public static final String TAG = "cchristou3-CyParking";
    private static final int ZOOM_LEVEL = 16;
    private static final String UNAVAILABLE = "Unavailable";

    // Fragment's variables
    private ListenerRegistration mListenerRegistration;
    private ContentLoadingProgressBar mContentLoadingProgressBar;
    private LinearLayout mInfoLayout;
    private MarkerManager mMarkerManager;
    private boolean triggeredFirstDatabaseUpdate;

    // Location related variables
    private LocationManager mLocationManager;
    private GoogleMap mGoogleMap;
    private LatLng mUserCurrentLatLng;

    /**
     * Initialises the fragment and its {@link MarkerManager} instance.
     * Uses the EventBus, to get access to data send by the previous fragment.
     * <p>-> Retrieves the location of the user from previous activity.
     *
     * @param savedInstanceState A bundle which contains info about previously stored data
     */
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Toast.makeText(requireContext(), "Please wait while the map is loading.", Toast.LENGTH_LONG).show();
        try {
            mUserCurrentLatLng = Objects.requireNonNull(EventBus.getDefault().getStickyEvent(LatLng.class));
        } catch (ClassCastException | NullPointerException e) {
            Log.e(TAG, "onCreateView: ", e); // TODO: Plan B
        }
        // Initialize MarkerManager and provide it with the icon to be used to display the user's location on the map.
        mMarkerManager = new MarkerManager(ResourcesCompat.getDrawable(getResources(), R.drawable.ic_user_location, null));
    }

    /**
     * Inflates our fragment's view.
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
        return inflater.inflate(R.layout.fragment_parking_map, container, false);
    }

    /**
     * Invoked at the completion of onCreateView.
     * Requests a Support Fragment to place the Google Map
     * and instantiates it with a GoogleMaps instance.
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
            mapFragment.getMapAsync(this);
        }

        // Initialize location based variables
        triggeredFirstDatabaseUpdate = false;

        // Get a reference to the infoLayout
        mInfoLayout = view.findViewById(R.id.fragment_parking_map_li_infoLayout);

        // Hook up the "directions" button with an on click listener
        view.findViewById(R.id.fragment_parking_map_imgbtn_directions).setOnClickListener(v -> {
            if (mMarkerManager.getSelectedParkingLot() == null) return;
            // Access the coordinates of the selected marker
            double selectedParkingLatitude = mMarkerManager.getSelectedLocationAttribute(ParkingRepository.LATITUDE_KEY);
            double selectedParkingLongitude = mMarkerManager.getSelectedLocationAttribute(ParkingRepository.LONGITUDE_KEY);
            // Create Uri (query string) for a Google Maps Intent
            // :q= indicates that we request for directions
            // Launch Google Maps activity
            startActivity(new Intent(Intent.ACTION_VIEW,
                    Uri.parse("google.navigation:q=" + selectedParkingLatitude
                            + "," + selectedParkingLongitude)).setPackage("com.google.android.apps.maps"));

        });

        // Hook up the "book" button with an onClick listener
        view.findViewById(R.id.fragment_parking_map_btn_booking).setOnClickListener(v -> {
            // If the user is not logged in, display a Toast msg
            if (FirebaseAuth.getInstance().getCurrentUser() == null) {
                Toast.makeText(requireContext(), "You need to be logged in to book a parking slot!", Toast.LENGTH_SHORT).show();
                return;
            }
            if (mMarkerManager.getSelectedParkingLot() != null) {
                // Navigate to the ParkingBookingFragment
                EventBus.getDefault().postSticky(mMarkerManager.getSelectedParkingLot());
                Navigation.findNavController(getActivity().findViewById(R.id.fragment_main_host_nv_nav_view))
                        .navigate(R.id.action_nav_parking_map_fragment_to_parking_booking_fragment);
            } else {
                Toast.makeText(requireContext(), "Oops something went wrong!", Toast.LENGTH_SHORT).show();
            }
        });

        // Show a progress bar to inform the user that the data is loading
        mContentLoadingProgressBar = view.findViewById(R.id.fragment_parking_map_pb_loadingMarkers);
        mContentLoadingProgressBar.getIndeterminateDrawable().setColorFilter(0xFFFF0000, android.graphics.PorterDuff.Mode.MULTIPLY);
    }

    /**
     * Gets invoked after onCreate Callback.
     * Initialises an observer to a node of the database. When changes occur on specified node,
     * parking data is being updated.
     */
    @Override
    public void onStart() {
        super.onStart();
        // Initialize the LocationManager
        mLocationManager = new LocationManager(requireContext(), this, this, false);
        // Initialize a firebase firestore observer to inform us about changes in the DB
        // The callback of the SnapshotListener gets triggered straight away when we attach it
        mListenerRegistration = retrieveDataAndListenForChanges();
    }

    /**
     * Called when the fragment is visible to the user and actively running.
     */
    @Override
    public void onResume() {
        super.onResume();
        if (mListenerRegistration == null) {
            mListenerRegistration = retrieveDataAndListenForChanges();
        }
    }

    /**
     * Unregisters location updates.
     */
    @Override
    public void onPause() {
        super.onPause();
        mListenerRegistration.remove();
        mListenerRegistration = null;
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
        mLocationManager.onRequestPermissionsResult(requireContext(), requestCode, grantResults);
    }

    @NotNull
    public ListenerRegistration retrieveDataAndListenForChanges() throws NullPointerException {
        return ParkingRepository.observerParkingLots().addSnapshotListener((value, error) -> {
            // ref: https://firebase.google.com/docs/firestore/query-data/listen#view_changes_between_snapshots
            if (!triggeredFirstDatabaseUpdate) { // To ensure that when we add the listener, the block of code will not get executed
                // Fetch initial data via HTTPs request, the filtering will be done by a cloud function
                if (mUserCurrentLatLng == null) return;
                fetchPrivateParking(mUserCurrentLatLng);
                triggeredFirstDatabaseUpdate = true;
                Log.d(TAG, "triggeredFirstDatabaseUpdate ");
                return;
            }
            if (error != null || value == null) return; // TODO: Show feedback to user

            Log.d(TAG, "NOT triggeredFirstDatabaseUpdate ");
            // Traverse through all the document changes
            for (DocumentChange dc : value.getDocumentChanges()) {
                // Access the Parking's coordinates
                final ParkingLot receivedParkingLot = dc.getDocument().toObject(ParkingLot.class);
                double receivedParkingLatitude = receivedParkingLot.getCoordinates().get(ParkingRepository.LATITUDE_KEY);
                double receivedParkingLongitude = receivedParkingLot.getCoordinates().get(ParkingRepository.LONGITUDE_KEY);
                // and check whether it is nearby the user.
                // If not, then move on to the next document that got changed
                if (!Utility.isNearbyUser(mUserCurrentLatLng, receivedParkingLatitude, receivedParkingLongitude))
                    continue;

                switch (dc.getType()) {
                    case ADDED: // If the document was newly added
                        // Add a marker to the parking's coordinates
                        // Add to HashMap to keep track of each marker's corresponding PrivateParking object
                        mMarkerManager.addMarker(mGoogleMap, receivedParkingLot, receivedParkingLatitude, receivedParkingLongitude);
                        break;
                    default:
                        // Either modified or removed
                        // Traverse the markers
                        for (Marker markerOfParking : mMarkerManager.getKeySets()) { // the key is of type Marker
                            // Check whether it's null
                            if (!mMarkerManager.exists(markerOfParking)) return;

                            // Check if the marker's lat & lng are the same with the same with the coordinates
                            // of the private parking that got changed
                            if (mMarkerManager.areCoordinatesTheSame(markerOfParking, receivedParkingLatitude, receivedParkingLongitude)) {
                                // then update the private parking's content
                                if (dc.getType() == DocumentChange.Type.MODIFIED) {
                                    mMarkerManager.addMarker(receivedParkingLot, markerOfParking);
                                    if (mInfoLayout.getVisibility() == View.VISIBLE) {
                                        // Update its details
                                        showDetails(markerOfParking);
                                    }
                                } else { // REMOVED
                                    mMarkerManager.removeMarker(markerOfParking);
                                    // If its info was showing, hide it and inform the user
                                    boolean wasVisibleBefore = changeInfoLayoutVisibilityTo(View.GONE);
                                    if (wasVisibleBefore) {
                                        Toast.makeText(getContext(), "Unfortunately, the parking got removed!", Toast.LENGTH_SHORT).show();
                                    }
                                }
                                break; // Closest loop break
                            }
                        }
                        break; // Case break
                }
            }
        });
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
        mContentLoadingProgressBar.show(); // Show loading bar

        final RequestQueue requestQueue = Volley.newRequestQueue(requireContext());
        // TODO: While the user waits -> show loading dialog
        //  OR -> Lazy load the markers
        //  -> animate user's location ref: https://codinginfinite.com/android-example-animate-marker-map-current-location/

        double userLatitude = latLng.latitude;
        double userLongitude = latLng.longitude;
        final Request<String> requestForPrivateParking = new StringRequest(Request.Method.GET,
                getString(R.string.firestore_api_url) + "?latitude=" + userLatitude + "&longitude=" + userLongitude,
                response -> {
                    Log.d(TAG, "fetchPrivateParking: " + response);
                    Log.d(TAG, "fetchPrivateParking: " + Arrays.toString(new Gson().fromJson(response, ParkingLot[].class)));
                    // Zoom in
                    mGoogleMap.moveCamera(CameraUpdateFactory.zoomTo(ZOOM_LEVEL));
                    // Convert json object into an array of ParkingLot objects and add for each a marker to the map.
                    mMarkerManager.addAll(mGoogleMap, new Gson().fromJson(response, ParkingLot[].class));
                }, error -> {// No wifi?
            // TODO: inform the user
            // Plan B: Reload map
            Toast.makeText(requireContext(), "Unexpected error occurred!\n" + error.getMessage(), Toast.LENGTH_SHORT).show();
            Log.e(TAG, "Volley error: " + error.getMessage());
        });
        // Add it to the queue
        requestQueue.add(requestForPrivateParking);
        // Hide the loading bar once the request has finished
        requestQueue.addRequestFinishedListener((RequestQueue.RequestFinishedListener<String>) request -> {
            if (mContentLoadingProgressBar.isShown()) mContentLoadingProgressBar.hide();
        });
    }

    /**
     * Gets triggered whenever the user taps on the map.
     * If the user did not tap any of the markers (including
     * his/hers) then the info plane is hidden.
     *
     * @param latLng The position of the map that the user tapped.
     */
    @Override
    public void onMapClick(@NotNull LatLng latLng) {
        // Check whether the same coordinates exist inside our global hash map
        // If not then the user did not tap any of the markers. Thus, we hide the info layout.
        boolean containsValue = mMarkerManager.anyMatchWithCoordinates(latLng);
        if (!containsValue) changeInfoLayoutVisibilityTo(View.GONE);
    }

    /**
     * Gets triggered whenever a marker gets tapped.
     * Keeps track of the latest marker that was clicked.
     * Displays the infoLayout with the contents associated
     * with the tapped marker.
     * It does not display the infoLayout if the marker
     * tapped belongs to the user.
     *
     * @param marker The marker which the user clicked
     * @return A boolean value
     * @see MarkerManager#setUserMarker(GoogleMap, LatLng)
     */
    @Override
    public boolean onMarkerClick(@NotNull Marker marker) {
        // If the marker of the user is clicked, ignore it
        if (marker.getTag() != null) {
            changeInfoLayoutVisibilityTo(View.GONE);
            return false;
        }
        showDetails(marker);
        mMarkerManager.setSelectedParking(marker);
        return false;
    }

    /**
     * Shows a plane that contains information about the marker's
     * associated {@link ParkingLot} object.
     *
     * @param marker The marker which was tapped on the map.
     */
    private void showDetails(Marker marker) {
        changeInfoLayoutVisibilityTo(View.VISIBLE);

        // Declare strings and initialize them with default values
        String nameTextViewString = UNAVAILABLE, capacityTextViewString = UNAVAILABLE, slotOfferTextViewString = UNAVAILABLE;

        if (mMarkerManager.exists(marker)) {
            // Get the corresponding hash map object
            ParkingLot dataOfSelectedMarker = mMarkerManager.getParkingLotOf(marker);
            nameTextViewString = dataOfSelectedMarker.getOpeningHours();
            capacityTextViewString = dataOfSelectedMarker.getAvailableSpaces() + " / " + dataOfSelectedMarker.getCapacity();
            try {
                slotOfferTextViewString = dataOfSelectedMarker.getSlotOfferList().get(0).toString();
            } catch (NullPointerException ignored) {
            }
        }
        // Get a reference to the view and update each infoLayout field with the clicked marker's corresponding data
        ((TextView) requireView().findViewById(R.id.fragment_parking_map_txt_name)).setText(nameTextViewString);
        ((TextView) requireView().findViewById(R.id.fragment_parking_map_txt_price)).setText(capacityTextViewString);
        ((TextView) requireView().findViewById(R.id.fragment_parking_map_txt_capacity)).setText(slotOfferTextViewString);
    }

    /**
     * Changes the visibility of the info plane based on the specified attribute.
     * If its visibility is already in the specified attribute (e.g. was already VISIBLE
     * and tried to make it VISIBLE again) then terminate the method and return false.
     * Otherwise, update its visibility attribute and return true.
     *
     * @param visibility The state of the visibility (E.g. View.Gone / View.VISIBLE / View.INVISIBLE)
     * @return True of the InfoLayout was already in the visibility state as specified. Otherwise, false.
     */
    private boolean changeInfoLayoutVisibilityTo(final int visibility) {
        // Check if the current visibility is already set with the given one (to avoid unnecessary actions)
        if (mInfoLayout.getVisibility() != visibility) {
            mInfoLayout.setVisibility(visibility);
            return true;
        }
        return false;
    }

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
        mGoogleMap = googleMap; // Save a reference of the GoogleMap instance
        // Start listening to the user's location updates
        mLocationManager.requestUserLocationUpdates(this);

        // Add listeners to the markers + map
        mGoogleMap.setOnMarkerClickListener(this);
        mGoogleMap.setOnMapClickListener(this);
    }

    /**
     * Callback invoked when the user's location is received.
     *
     * @param locationResult The result of the user's requested location.
     * @see LocationManager#requestUserLocationUpdates(Fragment)
     */
    @Override
    public void onLocationResult(LocationResult locationResult) {
        if (locationResult == null) return;
        // Update the user's location with the received location
        mUserCurrentLatLng = new LatLng(locationResult.getLastLocation().getLatitude(),
                locationResult.getLastLocation().getLongitude());
        mMarkerManager.setUserMarker(mGoogleMap, mUserCurrentLatLng);
        // Smoothly moves the camera to the user's position
        mGoogleMap.animateCamera(CameraUpdateFactory.newLatLng(mUserCurrentLatLng));
    }

    /**
     * Navigates from the current Fragment subclass to the
     * {@link AuthenticatorFragment}.
     */
    @Override
    public void toAuthenticator() {
        Navigation.findNavController(getActivity().findViewById(R.id.fragment_main_host_nv_nav_view))
                .navigate(R.id.action_nav_parking_map_fragment_to_nav_authenticator_fragment);
    }

    /**
     * Navigates from the current Fragment subclass to the
     * {@link ViewBookingsFragment}.
     */
    @Override
    public void toBookings() {
        Navigation.findNavController(getActivity().findViewById(R.id.fragment_main_host_nv_nav_view))
                .navigate(R.id.action_nav_parking_map_fragment_to_nav_view_bookings);
    }

    /**
     * Navigates from the current Fragment subclass to the
     * {@link AccountFragment}.
     */
    @Override
    public void toAccount() {
        Navigation.findNavController(getActivity().findViewById(R.id.fragment_main_host_nv_nav_view))
                .navigate(R.id.action_nav_parking_map_fragment_to_nav_account);
    }

    /**
     * Navigates from the current Fragment subclass to the
     * {@link FeedbackFragment}.
     */
    @Override
    public void toFeedback() {
        Navigation.findNavController(getActivity().findViewById(R.id.fragment_main_host_nv_nav_view))
                .navigate(R.id.action_nav_parking_map_fragment_to_nav_feedback);
    }

    /**
     * Navigates from the current Fragment subclass to the
     * {@link HomeFragment}.
     */
    @Override
    public void toHome() {
        Navigation.findNavController(getActivity().findViewById(R.id.fragment_main_host_nv_nav_view))
                .navigate(R.id.action_nav_parking_map_fragment_to_nav_home);
    }
}