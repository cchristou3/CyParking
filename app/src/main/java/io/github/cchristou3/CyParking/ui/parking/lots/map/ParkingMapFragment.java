package io.github.cchristou3.CyParking.ui.parking.lots.map;

import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.gms.location.LocationResult;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.firebase.firestore.DocumentChange;
import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;

import org.greenrobot.eventbus.EventBus;
import org.jetbrains.annotations.NotNull;

import java.io.StringReader;
import java.lang.ref.WeakReference;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import io.github.cchristou3.CyParking.R;
import io.github.cchristou3.CyParking.data.interfaces.LocationHandler;
import io.github.cchristou3.CyParking.data.interfaces.Navigable;
import io.github.cchristou3.CyParking.data.manager.DatabaseObserver;
import io.github.cchristou3.CyParking.data.manager.LocationManager;
import io.github.cchristou3.CyParking.data.manager.MarkerManager;
import io.github.cchristou3.CyParking.data.model.parking.lot.ParkingLot;
import io.github.cchristou3.CyParking.data.pojo.SnapshotState;
import io.github.cchristou3.CyParking.databinding.FragmentParkingMapBinding;
import io.github.cchristou3.CyParking.ui.home.HomeFragment;
import io.github.cchristou3.CyParking.ui.host.AuthStateViewModel;
import io.github.cchristou3.CyParking.ui.host.MainHostActivity;
import io.github.cchristou3.CyParking.ui.parking.slots.booking.BookingFragment;
import io.github.cchristou3.CyParking.ui.parking.slots.viewBooking.ViewBookingsFragment;
import io.github.cchristou3.CyParking.ui.user.account.AccountFragment;
import io.github.cchristou3.CyParking.ui.user.feedback.FeedbackFragment;
import io.github.cchristou3.CyParking.ui.user.login.AuthenticatorFragment;

import static io.github.cchristou3.CyParking.utilities.Utility.isNearbyUser;
import static io.github.cchristou3.CyParking.utilities.ViewUtility.animateAvailabilityColorChanges;

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
 * <p>
 * In terms of Authentication, this is achieved by communicating with the hosting
 * activity {@link MainHostActivity} via the {@link AuthStateViewModel}.
 * </p>
 *
 * @author Charalambos Christou
 * @version 8.0 12/01/21
 */
public class ParkingMapFragment extends Fragment implements OnMapReadyCallback, GoogleMap.OnMapClickListener,
        GoogleMap.OnMarkerClickListener, Navigable, LocationHandler {

    // Constant variables
    public static final String TAG = ParkingMapFragment.class.getName() + "UniqueTag";
    private static final int ZOOM_LEVEL = 16;
    private static final String UNAVAILABLE = "Unavailable";

    // Fragment's variables
    private ParkingMapViewModel mParkingMapViewModel;
    private AuthStateViewModel mAuthStateViewModel;
    private FragmentParkingMapBinding mFragmentParkingMapBinding;
    private MarkerManager mMarkerManager;
    private SnapshotState mSnapshotState;

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
        mFragmentParkingMapBinding = FragmentParkingMapBinding.inflate(inflater);
        return mFragmentParkingMapBinding.getRoot();
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
        initializeGoogleMaps();

        // Initialize the fragment's snapshot state
        mSnapshotState = new SnapshotState(SnapshotState.INITIAL_DATA_RETRIEVAL);
        Log.d(TAG, "onViewCreated: " + mSnapshotState.getState());

        initializeViewModelsWithObservers();

        attachButtonListeners();

        // Customise progress bar
        getBinding().fragmentParkingMapClpbLoadingMarkers
                .getIndeterminateDrawable().setColorFilter(0xFFFF0000, android.graphics.PorterDuff.Mode.MULTIPLY);
    }

    /**
     * Hooks up "directions" and "book" buttons with appropriate on click listeners.
     */
    private void attachButtonListeners() {
        // Hook up the "directions" button with an on click listener
        getBinding().fragmentParkingMapImgbtnDirections.setOnClickListener(v -> getDirections());

        // Hook up the "book" button with an onClick listener
        getBinding().fragmentParkingMapBtnBooking.setOnClickListener(v -> navigateToBookingScreen());
    }

    /**
     * Initializes the fragment's ViewModels ({@link #mParkingMapViewModel},{@link #mAuthStateViewModel})
     * and attaches observer to the info layout's state (whenever its state changes, the its visibility
     * on the Ui is updated accordingly).
     */
    private void initializeViewModelsWithObservers() {
        // Initialize the mParkingMapViewModel and the mAuthStateViewModel
        mParkingMapViewModel = new ViewModelProvider(this, new ParkingMapViewModelFactory())
                .get(ParkingMapViewModel.class);
        mAuthStateViewModel = new ViewModelProvider(requireActivity())
                .get(AuthStateViewModel.class);

        // Attach observer to ParkingMapViewModel's info layout state
        mParkingMapViewModel.getInfoLayoutState().observe(getViewLifecycleOwner(),
                this::updateInfoLayoutVisibilityTo);
        // Attach observer to ParkingMapViewModel's selected lot state
        mParkingMapViewModel.getSelectedLotState().observe(getViewLifecycleOwner(), lot -> {
            if (lot != null) {
                showDetails(lot);
                mMarkerManager.setSelectedParkingLot(lot);
            }
        });
    }

    /**
     * Initializes the fragment's GoogleMaps Ui.
     */
    private void initializeGoogleMaps() {
        SupportMapFragment mapFragment =
                (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.fragment_parking_map_fcv_map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }
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

        clearBackgroundMode();

        DatabaseObserver.createCollectionReferenceObserver(
                mParkingMapViewModel.getParkingLots(), // Collection reference
                (value, error) -> { // Event handler
                    Log.d(TAG, "onStart: " + (mSnapshotState.getState() == SnapshotState.INITIAL_DATA_RETRIEVAL ?
                            "INITIAL_DATA_RETRIEVAL" : "LISTENING_TO_DATA_CHANGES"));
                    switch (mSnapshotState.getState()) {
                        case SnapshotState.INITIAL_DATA_RETRIEVAL:
                            if (mUserCurrentLatLng == null) return;
                            // Fetch initial data, the filtering will be done by a cloud function
                            fetchParkingLots(mUserCurrentLatLng);
                            mSnapshotState.setState(SnapshotState.LISTENING_TO_DATA_CHANGES);
                            break;
                        case SnapshotState.LISTENING_TO_DATA_CHANGES:
                            if (error != null) {
                                Log.d(TAG, "LISTENING_TO_DATA_CHANGES: error: " + error.getLocalizedMessage());
                                break;
                            }
                            if (value == null || value.isEmpty()) break;

                            updateLocalDocuments(value.getDocumentChanges());
                            break;
                    }
                }
        ).registerLifecycleObserver(getLifecycle());
    }

    /**
     * Called when the fragment is visible to the user and actively running.
     * This is generally
     */
    @Override
    public void onResume() {
        super.onResume();
        //getBinding().fragmentParkingMapFcvMap.clearFocus();
        getBinding().fragmentParkingMapFcvMap.requestFocus();
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

    /**
     * Called when the view previously created by {@link #onCreateView} has
     * been detached from the fragment.
     */
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        getBinding().fragmentParkingMapImgbtnDirections.setOnClickListener(null);
        getBinding().fragmentParkingMapBtnBooking.setOnClickListener(null);
        mFragmentParkingMapBinding = null;
    }

    /**
     * The method's solely purpose is to remove the bug that occurs
     * when navigating back to the map.
     * Bug: the map is extremely laggy.
     */
    private void clearBackgroundMode() {
        // Solution to
        // https://stackoverflow.com/questions/27978188/google-maps-v2-mapfragment-is-extremely-laggy-on-returning-from-the-backstack
        // TODO: 15/01/2021 Find a better workaround
        WeakReference<Dialog> dial = new WeakReference<>(new Dialog(requireContext()));
        dial.get().show();
        dial.get().dismiss();
        dial.clear();
        dial = null;
    }

    /**
     * Creates and launches a GoogleMaps intent.
     * The user will be navigated to his GoogleMaps app
     * and directions will be set for the given position.
     */
    private void getDirections() {
        if (mMarkerManager.getSelectedParkingLot() == null) return;
        // Access the coordinates of the selected marker
        double selectedParkingLatitude = mMarkerManager.getSelectedMarkerLatitude();
        double selectedParkingLongitude = mMarkerManager.getSelectedMarkerLongitude();
        // Create Uri (query string) for a Google Maps Intent
        // :q= indicates that we request for directions
        // Launch Google Maps activity
        startActivity(new Intent(Intent.ACTION_VIEW,
                Uri.parse("google.navigation:q=" + selectedParkingLatitude
                        + "," + selectedParkingLongitude)).setPackage("com.google.android.apps.maps"));
    }

    /**
     * Iterates through the document changes (updated parking lot instances)
     * list and updates its corresponding local document (parking lot instance)
     * based on its type (e.g. {@link DocumentChange#getType()}).
     *
     * @param documentChanges The latest parking lot instances from the database.
     */
    private void updateLocalDocuments(@NotNull List<DocumentChange> documentChanges) {
        // Traverse through all the document changes
        for (DocumentChange dc : documentChanges) {
            // Access the Parking's coordinates
            final ParkingLot receivedParkingLot = dc.getDocument().toObject(ParkingLot.class);
            double receivedParkingLatitude = receivedParkingLot.getLatitude();
            double receivedParkingLongitude = receivedParkingLot.getLongitude();
            // and check whether it is nearby the user.
            // If not, then move on to the next document that got changed
            if (!isNearbyUser(mUserCurrentLatLng, receivedParkingLatitude, receivedParkingLongitude))
                continue;

            switch (dc.getType()) {
                case ADDED: // If the document was newly added
                    // Add a marker to the parking's coordinates
                    // Add to HashMap to keep track of each marker's corresponding ParkingLot object
                    mMarkerManager.addMarker(mGoogleMap, receivedParkingLot, receivedParkingLatitude, receivedParkingLongitude);
                    break;
                default:
                    // Either modified or removed
                    // Traverse the markers
                    for (Marker markerOfParking : mMarkerManager.getKeySets()) { // the key is of type Marker
                        // Check whether the marker is associated with a ParkingLot object (marker -> null)
                        if (!mMarkerManager.exists(markerOfParking)) return;

                        // Check if the marker's lat & lng are the same with the same with the coordinates
                        // of the private parking that got changed
                        if (mMarkerManager.areCoordinatesTheSame(markerOfParking, receivedParkingLatitude, receivedParkingLongitude)) {
                            // then update the private parking's content
                            if (dc.getType() == DocumentChange.Type.MODIFIED) {
                                // Keep track of the old value of the object's AvailableSpaces
                                int oldAvailableSpaces = mMarkerManager
                                        .getParkingLotOf(markerOfParking).getAvailableSpaces();
                                // Replace the old lot with the new lot.
                                mMarkerManager.addMarker(receivedParkingLot, markerOfParking);
                                // Update the info layout's contents if it's showing
                                if (mParkingMapViewModel.isInfoLayoutShown()) {
                                    mParkingMapViewModel.updateSelectedLotState(receivedParkingLot);
                                    int updatedAvailableSpaces = receivedParkingLot.getAvailableSpaces();
                                    animateAvailabilityColorChanges(
                                            getBinding().fragmentParkingMapCvInfoLayout, // Parent card view
                                            getBinding().fragmentParkingMapTxtAvailability, // child
                                            updatedAvailableSpaces,
                                            oldAvailableSpaces
                                    );
                                }
                            } else { // REMOVED
                                mMarkerManager.removeMarker(markerOfParking);
                                // If its info was showing, hide it and inform the user
                                boolean wasVisibleBefore = mParkingMapViewModel.hideInfoLayoutWithStateCheck();
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
    }

    /**
     * Creates and sends an HTTP request to our Backend.
     * Requests all the parking locations and for each, a Marker is placed
     * on the map.
     * When such a request occurs, a cloud function is executed, which filters
     * the parking locations. Only the ones which are nearby the user are sent back
     * too the client.
     * <strong>Note: In case the user exits the fragment before the HTTPS response is returned,
     * the view would be destroyed when the onRequestFinish listener will get
     * triggered. Thus, it will cause a NullPointerException when trying to access
     * the {@link FragmentParkingMapBinding#fragmentParkingMapClpbLoadingMarkers}.
     * For this reason, the listener is wrapped around a try-catch block.</strong>
     *
     * @param latLng The latest recorded latitude and longitude of the user.
     * @see #onDestroyView()
     */
    public void fetchParkingLots(@NotNull LatLng latLng) {
        getBinding().fragmentParkingMapClpbLoadingMarkers.show(); // Show loading bar
        mParkingMapViewModel.fetchParkingLots(latLng.latitude, latLng.longitude, new HttpsCallHandler() {
            @Override
            public void onSuccess(String rawJsonResponse) {
                // Zoom in
                Log.d(TAG, "Before parsing: " + rawJsonResponse);
                mGoogleMap.moveCamera(CameraUpdateFactory.zoomTo(ZOOM_LEVEL));
                // Convert json object into an array of ParkingLot objects and add for each a marker to the map.
                JsonReader reader = new JsonReader(new StringReader(rawJsonResponse));
                reader.setLenient(true);
                ParkingLot[] result = new Gson().fromJson(reader, ParkingLot[].class);
                Log.d(TAG, "Parsed: " + Arrays.toString(result));
                mMarkerManager.addAll(mGoogleMap, result);
                try {
                    if (getBinding().fragmentParkingMapClpbLoadingMarkers.isShown()) {
                        getBinding().fragmentParkingMapClpbLoadingMarkers.hide();
                    }
                } catch (NullPointerException ignore) { /* view got destroyed */ }
            }

            @Override
            public void onFailure(Exception exception) {
                if (exception == null) return;
                // Plan B: Reload map
                Toast.makeText(requireContext(), "Unexpected error occurred!\n" + exception.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e(ParkingMapFragment.TAG, "Error of class " + exception.getClass() + ": " + exception.getMessage());
            }
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
        if (!containsValue)
            mParkingMapViewModel.hideInfoLayout();
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
            mParkingMapViewModel.hideInfoLayout();
            return false;
        }
        mParkingMapViewModel.updateSelectedLotState(
                mMarkerManager.getParkingLotOf(marker)
        );
        return false;
    }

    /**
     * Shows a plane that contains information about the marker's
     * associated {@link ParkingLot} object.
     *
     * @param lot The lot that got tapped on the map.
     */
    private void showDetails(ParkingLot lot) {
        mParkingMapViewModel.showInfoLayout();

        // Declare strings and initialize them with default values
        String name = UNAVAILABLE, availability = UNAVAILABLE, slotOffer = UNAVAILABLE;

        if (lot != null) {
            // Get the corresponding hash map object
            name = lot.getLotName();
            availability = lot.getLotAvailability(requireContext());
            slotOffer = "Best offer: " + lot.getBestOffer().toString() + "";
        }
        // Get a reference to the view and update each infoLayout field with the clicked marker's corresponding data
        getBinding().fragmentParkingMapTxtName.setText(name);
        getBinding().fragmentParkingMapTxtOffer.setText(slotOffer);
        getBinding().fragmentParkingMapTxtAvailability.setText(availability);
    }

    /**
     * Changes the visibility of the info plane based on the specified attribute.
     *
     * @param visibility The state of the visibility (E.g. View.Gone / View.VISIBLE / View.INVISIBLE)
     */
    private void updateInfoLayoutVisibilityTo(final int visibility) {
        getBinding().fragmentParkingMapCvInfoLayout.setVisibility(visibility);
    }

    /**
     * Access the {@link #mFragmentParkingMapBinding}.
     *
     * @return A reference to {@link #mFragmentParkingMapBinding}.
     */
    private FragmentParkingMapBinding getBinding() {
        return mFragmentParkingMapBinding;
    }

    /**
     * If the user is not logged in, a Toast message is displayed.
     * Otherwise, the user is navigated to {@link BookingFragment}.
     * The {@link EventBus} instance is used to pass the selected {@link ParkingLot}
     * object to {@link BookingFragment}.
     */
    private void navigateToBookingScreen() {
        // If the user is not logged in, display a Toast msg
        Log.d(TAG, "navigateToBookingScreen: " + mAuthStateViewModel.getUser().getRoles());
        if (mAuthStateViewModel.getUser() == null || !mAuthStateViewModel.getUser().isUser()) {
            // TODO: 16/01/2021 Replace string with getString(R.string...)
            Toast.makeText(requireContext(), "You need to be logged as a 'User' in to book a parking slot!", Toast.LENGTH_SHORT).show();
            return;
        }
        if (mMarkerManager.getSelectedParkingLot() != null) {
            // Navigate to the ParkingBookingFragment
            Log.d(TAG, "onViewCreated: sending over: " + mMarkerManager.getSelectedParkingLot());
            EventBus.getDefault().postSticky(mMarkerManager.getSelectedParkingLot());
            getNavController(requireActivity())
                    .navigate(R.id.action_nav_parking_map_fragment_to_parking_booking_fragment);
        } else {
            Toast.makeText(requireContext(), "Oops something went wrong!", Toast.LENGTH_SHORT).show();
        }
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
        Log.d(TAG, "onMapReady: ");
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
        getNavController(requireActivity())
                .navigate(R.id.action_nav_parking_map_fragment_to_nav_authenticator_fragment);
    }

    /**
     * Navigates from the current Fragment subclass to the
     * {@link ViewBookingsFragment}.
     */
    @Override
    public void toBookings() {
        getNavController(requireActivity())
                .navigate(R.id.action_nav_parking_map_fragment_to_nav_view_bookings);
    }

    /**
     * Navigates from the current Fragment subclass to the
     * {@link AccountFragment}.
     */
    @Override
    public void toAccount() {
        getNavController(requireActivity())
                .navigate(R.id.action_nav_parking_map_fragment_to_nav_account);
    }

    /**
     * Navigates from the current Fragment subclass to the
     * {@link FeedbackFragment}.
     */
    @Override
    public void toFeedback() {
        getNavController(requireActivity())
                .navigate(R.id.action_nav_parking_map_fragment_to_nav_feedback);
    }

    /**
     * Navigates from the current Fragment subclass to the
     * {@link HomeFragment}.
     */
    @Override
    public void toHome() {
        getNavController(requireActivity())
                .navigate(R.id.action_nav_parking_map_fragment_to_nav_home);
    }

    interface HttpsCallHandler {
        void onSuccess(String rawJsonResponse);

        void onFailure(Exception exception);
    }
}