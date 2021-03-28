package io.github.cchristou3.CyParking.ui.views.parking.lots.map;

import android.app.Dialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.util.Consumer;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewbinding.ViewBinding;

import com.google.android.gms.location.LocationResult;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import org.jetbrains.annotations.NotNull;

import java.lang.ref.WeakReference;
import java.util.List;

import io.github.cchristou3.CyParking.R;
import io.github.cchristou3.CyParking.apiClient.model.data.parking.lot.ParkingLot;
import io.github.cchristou3.CyParking.apiClient.utils.Utils;
import io.github.cchristou3.CyParking.data.interfaces.LocationHandler;
import io.github.cchristou3.CyParking.data.interfaces.Navigable;
import io.github.cchristou3.CyParking.data.manager.DatabaseObserver;
import io.github.cchristou3.CyParking.data.manager.MarkerManager;
import io.github.cchristou3.CyParking.data.manager.location.LocationManager;
import io.github.cchristou3.CyParking.data.manager.location.SubsequentUpdateHelper;
import io.github.cchristou3.CyParking.databinding.FragmentParkingMapBinding;
import io.github.cchristou3.CyParking.ui.components.BaseFragment;
import io.github.cchristou3.CyParking.ui.helper.AlertBuilder;
import io.github.cchristou3.CyParking.ui.views.home.HomeFragment;
import io.github.cchristou3.CyParking.ui.views.host.GlobalStateViewModel;
import io.github.cchristou3.CyParking.ui.views.host.MainHostActivity;
import io.github.cchristou3.CyParking.ui.views.parking.slots.viewBooking.ViewBookingsFragment;
import io.github.cchristou3.CyParking.ui.views.user.account.AccountFragment;
import io.github.cchristou3.CyParking.ui.views.user.feedback.FeedbackFragment;
import io.github.cchristou3.CyParking.ui.views.user.login.AuthenticatorFragment;

import static android.view.View.GONE;
import static io.github.cchristou3.CyParking.utilities.AnimationUtility.animateAvailabilityColorChanges;
import static io.github.cchristou3.CyParking.utilities.AnimationUtility.slideBottom;
import static io.github.cchristou3.CyParking.utilities.DrawableUtility.applyDrawableColor;

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
 * via the {@link #getArguments()}</p>.
 * <p>
 * In terms of Authentication, this is achieved by communicating with the hosting
 * activity {@link MainHostActivity} via the {@link GlobalStateViewModel}.
 * </p>
 *
 * @author Charalambos Christou
 * @version 17.0 27/03/21
 * <p>
 * New changes:
 * <p><b>On server</b>: via a cloud function retrieve the document ids of all
 * the Parking Lots that are nearby the user and send it to the client.
 * <b>On Client</b>: query the parking lots with the specified document ids
 * (retrieved from the server side) and listen for changes.</p>
 * <p>The client is fetching the ids only when entering the fragment. Navigating
 * back to this fragment will not result into re-fetching the ids. The ids, are
 * persisted via the ViewModel.</p>
 * <p>Also added a couple more state LiveData to its ViewModel,
 * to enable a smooth workflow between
 * fetching lot's document ids <-> listening for updates on the specified lots
 * <-> update the Ui.</p>
 * </p>
 * <p>
 * Pending use cases:
 *     <ul>
 *         <li>No nearby lots where found. What to do?</li>
 *         <li>No connection initially. What to do?</li>
 *         <li>No connection initially, then it got restored. What to do?</li>
 *         <li>Loaded doc Ids, then connection got lost, then its got restored. What to do?</li>
 *     </ul>
 * </p>
 * <p>
 *     TODO: Additional functionality
 *     <ul>
 *         <li>Cache the ids in an appropriate time</li>
 *     </ul>
 * </p>
 */
public class ParkingMapFragment extends BaseFragment<FragmentParkingMapBinding>
        implements OnMapReadyCallback, GoogleMap.OnMapClickListener,
        GoogleMap.OnMarkerClickListener, Navigable, LocationHandler {

    // Constant variables
    public static final String TAG = ParkingMapFragment.class.getName() + "UniqueTag";
    private static final int DEFAULT_ZOOM_LEVEL = 16;
    private final String UNAVAILABLE = getString(R.string.unavailable);
    private static final double UPDATE_LOCATION_THRESHOLD = 100.0D;
    private static final float MIN_ZOOM_LEVEL = 10.0f;

    // Fragment's variables
    private ParkingMapViewModel mParkingMapViewModel;
    private MarkerManager mMarkerManager;
    private DatabaseObserver<Query, QuerySnapshot> mDatabaseObserver;

    // Location related variables
    private SubsequentUpdateHelper<ParkingMapFragment, FragmentParkingMapBinding> mLocationManager;
    private GoogleMap mGoogleMap;
    private LatLng mUserCurrentLatLng;
    private LatLng mInitialUserLatLng;
    private int mLocationUpdatesCounter = 0;

    /**
     * Initialises the fragment and its {@link MarkerManager} instance.
     * Access to data send by the previous fragment via {@link #getArguments()}.
     * <p>-> Retrieves the location of the user from previous activity.
     *
     * @param savedInstanceState A bundle which contains info about previously stored data
     */
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mUserCurrentLatLng = getArguments().getParcelable(getString(R.string.user_latlng_arg));
        }
        // Initialize MarkerManager and provide it with the icon to be used to display the user's location on the map.
        mMarkerManager = new MarkerManager(
                applyDrawableColor(ResourcesCompat.getDrawable(getResources(), R.drawable.ic_user_location, requireActivity().getTheme()),
                        getResources().getColor(R.color.black, requireActivity().getTheme()))
        );
        initializeViewModel();
    }

    /**
     * Inflates our fragment's view.
     *
     * @param savedInstanceState A bundle which contains info about previously stored data
     * @param inflater           The object which will inflate (create) our layout
     * @param container          ViewGroup container
     * @see BaseFragment#onCreateView(ViewBinding, int)
     */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return super.onCreateView(FragmentParkingMapBinding.inflate(inflater), R.string.parking_map_label);
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
        initializeGoogleMaps(); // Map
        attachStateObservers(); // LiveData state objects
        attachButtonListeners(); // Ui listeners
        // Check if the anything was fetched already
        if (!mParkingMapViewModel.didPreviouslyRetrieveDocumentIds()) {
            fetchParkingLots(mUserCurrentLatLng); // HTTPS call
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
        mLocationManager = LocationManager.createSubsequentUpdateHelper(this, this);
        clearBackgroundMode();
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
        mLocationManager.onRequestPermissionsResult(this, requestCode, grantResults);
    }

    /**
     * Called when the view previously created by {@link #onCreateView} has
     * been detached from the fragment.
     *
     * @see BaseFragment#onDestroyView()
     */
    @Override
    public void onDestroyView() {
        mParkingMapViewModel.updateDocumentState(null); // Resetting its value, in case configuration changes occur.
        // Remove listeners for the map
        mGoogleMap.setOnMarkerClickListener(null);
        mGoogleMap.setOnMapClickListener(null);
        removeOnClickListeners(
                getBinding().fragmentParkingMapImgbtnDirections,
                getBinding().fragmentParkingMapBtnBooking
        );
        super.onDestroyView();
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
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera.
     * If Google Play services is not installed on the device, the user will be prompted to
     * install it inside the SupportMapFragment. This method will only be triggered once the
     * user has installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        startPostponedEnterTransition();

        mGoogleMap = googleMap; // Save a reference of the GoogleMap instance
        mGoogleMap.setMinZoomPreference(MIN_ZOOM_LEVEL);
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
     * @see LocationManager#requestUserLocationUpdates(BaseFragment)
     */
    @Override
    public void onLocationResult(LocationResult locationResult) {
        Log.d(TAG, "onLocationResult: From Map");
        if (locationResult == null) return;

        // Access the user's new location.
        final LatLng updatedPosition = new LatLng(locationResult.getLastLocation().getLatitude(),
                locationResult.getLastLocation().getLongitude());

        // If null, assign it to the location received from the first update.
        if (mInitialUserLatLng == null) mInitialUserLatLng = updatedPosition;

        checkIntervalForUpdates(updatedPosition);

        // Update the user's location with the received location
        mUserCurrentLatLng = updatedPosition;

        mMarkerManager.setUserMarker(mGoogleMap, mUserCurrentLatLng);
        // Smoothly moves the camera to the user's position
        mGoogleMap.animateCamera(CameraUpdateFactory.newLatLng(mUserCurrentLatLng));
    }

    /**
     * Every four intervals check the distance between the user's initial position
     * and the new one. If the distance is over the threshold {@link #UPDATE_LOCATION_THRESHOLD}
     * then re-fetch all the document Ids of the parking lots that are nearby the user
     *
     * @param updatedPosition The new position of the user.
     */
    private void checkIntervalForUpdates(LatLng updatedPosition) {
        mLocationUpdatesCounter++; // Increment the counter
        if (mLocationUpdatesCounter % 4 == 0) { // Every 4 location updates (20 seconds)
            if (shouldFetchParkingDocs(mInitialUserLatLng, updatedPosition)) {
                // Fetch the ids of parking lots based on the user's new position.
                mInitialUserLatLng = updatedPosition;
                fetchParkingLots(mInitialUserLatLng);
            }
        }
    }

    /**
     * Checks whether it is time to re-fetch the document Ids of the
     * nearby parking lots. This takes into consideration the user's
     * initial position and the newly received one.
     * Every time the user moves 100m away from his original position
     * it returns true.
     *
     * @param initialPosition The user's current location.
     * @param updatedPosition The
     * @return True if the difference is over 100m. Otherwise, false.
     * @see #UPDATE_LOCATION_THRESHOLD
     */
    private boolean shouldFetchParkingDocs(LatLng initialPosition, LatLng updatedPosition) {
        // Calculate the distance between the user's initial position
        // and the updated one.
        return Utils.getDistanceApart(
                initialPosition, updatedPosition
        ) > UPDATE_LOCATION_THRESHOLD;  // 100 meters
    }


    /**
     * Navigates from the current Fragment subclass to the
     * {@link AuthenticatorFragment}.
     */
    @Override
    public void toAuthenticator() {
        getNavController(requireActivity())
                .navigate(
                        ParkingMapFragmentDirections
                                .actionNavParkingMapFragmentToNavAuthenticatorFragment()
                );
    }

    /**
     * Navigates from the current Fragment subclass to the
     * {@link ViewBookingsFragment}.
     */
    @Override
    public void toBookings() {
        getNavController(requireActivity())
                .navigate(
                        ParkingMapFragmentDirections.actionNavParkingMapFragmentToNavViewBookings()
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
                        ParkingMapFragmentDirections.actionNavParkingMapFragmentToNavAccount()
                );
    }

    /**
     * Navigates from the current Fragment subclass to the
     * {@link FeedbackFragment}.
     */
    @Override
    public void toFeedback() {
        getNavController(requireActivity())
                .navigate(
                        ParkingMapFragmentDirections.actionNavParkingMapFragmentToNavFeedback()
                );
    }

    /**
     * Navigates from the current Fragment subclass to the
     * {@link HomeFragment}.
     */
    @Override
    public void toHome() {
        getNavController(requireActivity())
                .navigate(
                        ParkingMapFragmentDirections.actionNavParkingMapFragmentToNavHome()
                );
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
        postponeEnterTransition();
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
    }

    /**
     * Creates and launches a GoogleMaps intent.
     * The user will be navigated to his GoogleMaps app
     * and directions will be set for the given position.
     */
    private void getDirections() {
        if (mMarkerManager.getSelectedParkingLot() == null) return;
        LocationManager.launchGoogleMaps(
                this,
                // Access the coordinates of the selected marker
                mMarkerManager.getSelectedMarkerLatitude(),
                mMarkerManager.getSelectedMarkerLongitude(),
                mMarkerManager.getSelectedParkingLot().getLotName()
        );
    }

    /**
     * Iterates through the document changes (updated parking lot instances)
     * list and updates its corresponding local document (parking lot instance)
     * based on its type (e.g. {@link DocumentChange.Type#MODIFIED}).
     *
     * @param documentChanges The latest parking lot instances from the database.
     */
    private void updateLocalDocuments(List<DocumentChange> documentChanges) {
        if (documentChanges == null || documentChanges.isEmpty()) return;

        // Traverse through all the document changes
        for (DocumentChange dc : documentChanges) {
            switch (dc.getType()) {
                case ADDED: // If the document was newly added
                    addLot(dc);
                    break;
                case REMOVED:
                    removeLot(dc);
                    break;
                case MODIFIED:
                    updateLot(dc);
                    break;
            }
        }
    }

    /**
     * Adds a {@link Marker} to the map based on the coordinates of the
     * {@link ParkingLot} object. It also, associates the Marker with the
     * ParkingLot object via a {@link java.util.HashMap} to enable fast look up.
     *
     * @param dc The newly received DocumentChange, containing info about how
     *           a parking lot object in the database got changed.
     */
    private void addLot(DocumentChange dc) {
        // Add a marker to the parking's coordinates
        // Add to HashMap to keep track of each marker's corresponding ParkingLot object
        Log.d(TAG, "updateLocalDocuments: ADDED");
        mMarkerManager.addMarkerWithContents(mGoogleMap, ParkingLot.toParkingLot(dc));
    }

    /**
     * Update the Marker's ParkingLot object with the new one (can be looked up via its parkingId).
     *
     * @param dc The newly received DocumentChange, containing info about how
     *           a parking lot object in the database got changed.
     */
    private void updateLot(DocumentChange dc) {
        final ParkingLot receivedParkingLot = ParkingLot.toParkingLot(dc);
        updateMarkerContents(receivedParkingLot, markerOfParking -> {
            // Keep track of the old value of the object's AvailableSpaces
            int oldAvailableSpaces = mMarkerManager
                    .getParkingLotOf(markerOfParking).getAvailableSpaces();
            // Replace the old lot with the new lot.
            mMarkerManager.replaceMarkerContents(receivedParkingLot, markerOfParking);
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
        });
    }

    /**
     * Remove the marker that is associated with the given parking lot object.
     *
     * @param dc The newly received DocumentChange, containing info about how
     *           a parking lot object in the database got changed.
     */
    private void removeLot(DocumentChange dc) {
        final ParkingLot receivedParkingLot = ParkingLot.toParkingLot(dc);
        updateMarkerContents(receivedParkingLot, markerOfParking -> {
            mMarkerManager.removeMarker(markerOfParking);
            // If its info was showing, hide it and inform the user
            boolean wasVisibleBefore = mParkingMapViewModel.hideInfoLayoutWithStateCheck();
            if (wasVisibleBefore) {
                getGlobalStateViewModel().updateToastMessage(R.string.parking_got_removed);
            }
        });
    }

    /**
     * Traverse all markers currently on the map till you find the one that is linked
     * to the given {@link ParkingLot} object. Then execute the given method.
     *
     * @param receivedParkingLot The Lot to be updated.
     * @param updatable          The interface to update the lot once found.
     */
    private void updateMarkerContents(ParkingLot receivedParkingLot, Consumer<Marker> updatable) {
        // Traverse the markers
        for (Marker markerOfParking : mMarkerManager.getKeySets()) { // the key is of type Marker
            // Check whether the marker is associated with a ParkingLot object (marker -> null)
            //  and check whether the marker's Parking lot is not the same with the received one
            if (!mMarkerManager.exists(markerOfParking)
                    || !mMarkerManager.getParkingLotOf(markerOfParking).equals(receivedParkingLot))
                continue;

            // then update the private parking's content according to its DocumentChange.Type
            updatable.accept(markerOfParking);
        }
    }

    /**
     * Called when the Fragment is no longer resumed.
     * If the loading bar was shown, then hide it.
     */
    @Override
    public void onPause() {
        super.onPause();
        if (getGlobalStateViewModel().isLoadingBarShowing()) {
            getGlobalStateViewModel().hideLoadingBar();
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
     * @param latLng The latest recorded latitude and longitude of the user.
     * @see #onDestroyView()
     */
    public void fetchParkingLots(@NotNull LatLng latLng) {
        getGlobalStateViewModel().showLoadingBar();
        Log.d(TAG, "fetchParkingLots: Loading Bar ON");
        mParkingMapViewModel.fetchParkingLots(latLng.latitude, latLng.longitude,
                () -> getGlobalStateViewModel().hideLoadingBar() // when complete do this
        );
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
     * Initializes the fragment's ViewModel ({@link #mParkingMapViewModel}).
     */
    private void initializeViewModel() {
        // Initialize the mParkingMapViewModel
        mParkingMapViewModel = new ViewModelProvider(this, new ParkingMapViewModelFactory())
                .get(ParkingMapViewModel.class);
        getGlobalStateViewModel().updateToastMessage(R.string.loading_map);
    }

    /**
     * Attaches observers to the info layout's state (whenever its state changes, the its visibility
     * on the Ui is updated accordingly) and the state of the currently selected parking lot.
     */
    private void attachStateObservers() throws IllegalArgumentException {
        // Attach observer to ParkingMapViewModel's info layout state
        mParkingMapViewModel.getInfoLayoutState()
                .observe(getViewLifecycleOwner(), this::updateInfoLayoutVisibilityTo);
        // Attach observer to ParkingMapViewModel's selected lot state
        mParkingMapViewModel.getSelectedLotState().observe(getViewLifecycleOwner(), lot -> {
            if (lot != null) {
                showDetails(lot);
                mMarkerManager.setSelectedParkingLot(lot);
            }
        });

        // Attach observer to the latest retrieved document changes
        mParkingMapViewModel.getDocumentChangesState().observe(getViewLifecycleOwner(),
                this::updateLocalDocuments);

        mParkingMapViewModel.getDocumentIdsOfNearbyLots().observe(getViewLifecycleOwner(), documentIds -> {
            if (mGoogleMap != null)
                mGoogleMap.moveCamera(CameraUpdateFactory.zoomTo(DEFAULT_ZOOM_LEVEL));
            Log.d(TAG, "IdsState observer: " + mDatabaseObserver + " ids: " + documentIds);
            if (documentIds == null || documentIds.isEmpty()) {
                // Display message
                getGlobalStateViewModel().updateToastMessage(R.string.no_nearby_parking_lots);
                return;
            }

            // Get all the parking lots where their doc ids are in the documentIds list set
            // then attach an observer to them
            if (mDatabaseObserver == null) {
                // Create a database observer
                (mDatabaseObserver = DatabaseObserver
                        .createQueryObserver( // Internally adds a snapshot listener
                                mParkingMapViewModel.getParkingLots(documentIds), // Query reference
                                (value, error) -> { // Event handler
                                    if (value != null)
                                        mParkingMapViewModel.updateDocumentState(value.getDocumentChanges());
                                }
                        )).registerLifecycleObserver(getLifecycle());
            } else {
                // Update the query from the database
                mDatabaseObserver.updateDatabaseReference(
                        mParkingMapViewModel.getParkingLots(documentIds)
                );
            }
        });

        mParkingMapViewModel.getPromptingState().observe(getViewLifecycleOwner(), timeToPromptTheUser -> {
            AlertBuilder.showSingleActionAlert(
                    getChildFragmentManager(),
                    R.string.volley_error_title,
                    R.string.volley_error_body,
                    (v) -> goBack(requireActivity())); // go back to home screen
        });

        mParkingMapViewModel.getNavigationToBookingState().observe(getViewLifecycleOwner(), selectedLot ->
                getNavController(requireActivity())
                        .navigate(ParkingMapFragmentDirections
                                .actionNavParkingMapFragmentToParkingBookingFragment(selectedLot))
        );
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
            slotOffer = getString(R.string.best_offer) + " " + lot.getBestOffer().toString() + "";
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
        slideBottom(getBinding().idFragmentParkingMap,
                getBinding().fragmentParkingMapCvInfoLayout, visibility == GONE, 250L, null);
    }

    /**
     * Navigate the user to the booking screen.
     */
    private void navigateToBookingScreen() {
        mParkingMapViewModel.navigateToBookingScreen(getUser(), mMarkerManager.getSelectedParkingLot(),
                getGlobalStateViewModel()::updateToastMessage);
    }
}