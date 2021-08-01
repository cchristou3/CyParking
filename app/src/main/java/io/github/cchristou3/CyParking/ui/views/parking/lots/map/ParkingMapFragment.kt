package io.github.cchristou3.CyParking.ui.views.parking.lots.map

import android.app.Dialog
import android.content.Intent
import android.location.Location
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import androidx.core.util.Consumer
import androidx.lifecycle.ViewModelProvider
import androidx.viewbinding.ViewBinding
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.GoogleMap.OnMapClickListener
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot
import io.github.cchristou3.CyParking.R
import io.github.cchristou3.CyParking.apiClient.model.data.parking.lot.ParkingLot
import io.github.cchristou3.CyParking.apiClient.utils.getDistanceApart
import io.github.cchristou3.CyParking.data.interfaces.Navigable
import io.github.cchristou3.CyParking.data.manager.DatabaseObserver
import io.github.cchristou3.CyParking.data.manager.MarkerManager
import io.github.cchristou3.CyParking.databinding.FragmentParkingMapBinding
import io.github.cchristou3.CyParking.ui.components.BaseFragment
import io.github.cchristou3.CyParking.ui.components.LocationFragment
import io.github.cchristou3.CyParking.ui.helper.AlertBuilder
import io.github.cchristou3.CyParking.utilities.animateAvailabilityColorChanges
import io.github.cchristou3.CyParking.utilities.applyDrawableColor
import io.github.cchristou3.CyParking.utilities.slideBottom
import mumayank.com.airlocationlibrary.AirLocation
import java.lang.ref.WeakReference

/**
 * Purpose:
 *
 * View all nearby parking.
 * The user can select a parking and view more details.
 * Lastly, there is an option to book a specific parking.
 *
 *
 *
 * Implementation wise the fragment uses a [AirLocation]
 * and a [MarkerManager] that are responsible for acquiring
 * the user's location and managing the markers on the Google Map.
 *
 *
 * The fragment receives the user's location from [HomeFragment]
 * via the [.getArguments].
 *
 *
 * In terms of Authentication, this is achieved by communicating with the hosting
 * activity [MainHostActivity] via the [GlobalStateViewModel].
 *
 *
 * @author Charalambos Christou
 * @version 17.0 27/03/21
 *
 *
 * New changes:
 *
 * **On server**: via a cloud function retrieve the document ids of all
 * the Parking Lots that are nearby the user and send it to the client.
 * **On Client**: query the parking lots with the specified document ids
 * (retrieved from the server side) and listen for changes.
 *
 * The client is fetching the ids only when entering the fragment. Navigating
 * back to this fragment will not result into re-fetching the ids. The ids, are
 * persisted via the ViewModel.
 *
 * Also added a couple more state LiveData to its ViewModel,
 * to enable a smooth workflow between
 * fetching lot's document ids <-> listening for updates on the specified lots
 * <-> update the Ui.
 *
 *
 *
 * Pending use cases:
 *
 *  * No nearby lots where found. What to do?
 *  * No connection initially. What to do?
 *  * No connection initially, then it got restored. What to do?
 *  * Loaded doc Ids, then connection got lost, then its got restored. What to do?
 *
 *
 *
 *
 * TODO: Additional functionality
 *
 *  * Cache the ids in an appropriate time
 *
 *
 */
class ParkingMapFragment : LocationFragment<FragmentParkingMapBinding>(), OnMapReadyCallback, OnMapClickListener, OnMarkerClickListener, Navigable {
    private var UNAVAILABLE: String? = null

    // Fragment's variables
    private var mParkingMapViewModel: ParkingMapViewModel? = null
    private var mMarkerManager: MarkerManager? = null
    private var mDatabaseObserver: DatabaseObserver<Query, QuerySnapshot>? = null

    // Location related variables
    private var mGoogleMap: GoogleMap? = null
    private var mUserCurrentLatLng: LatLng? = null
    private var mInitialUserLatLng: LatLng? = null
    private var mLocationUpdatesCounter = 0

    /**
     * Initialises the fragment and its [MarkerManager] instance.
     * Access to data send by the previous fragment via [.getArguments].
     *
     * -> Retrieves the location of the user from previous activity.
     *
     * @param savedInstanceState A bundle which contains info about previously stored data
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            mUserCurrentLatLng = it.getParcelable(getString(R.string.user_latlng_arg))
        }
        // Initialize MarkerManager and provide it with the icon to be used to display the user's location on the map.
        mMarkerManager = MarkerManager(
                applyDrawableColor(ResourcesCompat.getDrawable(resources, R.drawable.ic_user_location, requireActivity().theme)!!,
                        resources.getColor(R.color.black, requireActivity().theme))
        )
        UNAVAILABLE = getString(R.string.unavailable)
        initializeViewModel()
    }

    /**
     * Inflates our fragment's view.
     *
     * @param savedInstanceState A bundle which contains info about previously stored data
     * @param inflater           The object which will inflate (create) our layout
     * @param container          ViewGroup container
     * @see BaseFragment.onCreateView
     */
    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return super.onCreateView(FragmentParkingMapBinding.inflate(inflater), R.string.parking_map_label)
    }

    /**
     * Invoked at the completion of onCreateView.
     * Requests a Support Fragment to place the Google Map
     * and instantiates it with a GoogleMaps instance.
     *
     * @param view               The view of the fragment
     * @param savedInstanceState A bundle which contains info about previously stored data
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initializeGoogleMaps() // Map
        attachStateObservers() // LiveData state objects
        attachButtonListeners() // Ui listeners
        // Check if the anything was fetched already
        if (!mParkingMapViewModel!!.didPreviouslyRetrieveDocumentIds()) {
            fetchParkingLots(mUserCurrentLatLng!!) // HTTPS call
        }
    }

    /**
     * Gets invoked after onCreate Callback.
     * Initialises an observer to a node of the database. When changes occur on specified node,
     * parking data is being updated.
     */
    override fun onStart() {
        super.onStart()
        clearBackgroundMode()
    }

    /**
     * Called when the view previously created by [.onCreateView] has
     * been detached from the fragment.
     *
     * @see BaseFragment.onDestroyView
     */
    override fun onDestroyView() {
        mParkingMapViewModel!!.updateDocumentState(null) // Resetting its value, in case configuration changes occur.
        // Remove listeners for the map
        mGoogleMap!!.setOnMarkerClickListener(null)
        mGoogleMap!!.setOnMapClickListener(null)
        removeOnClickListeners(
                binding!!.fragmentParkingMapImgbtnDirections,
                binding!!.fragmentParkingMapBtnBooking
        )
        super.onDestroyView()
    }

    /**
     * Gets triggered whenever the user taps on the map.
     * If the user did not tap any of the markers (including
     * his/hers) then the info plane is hidden.
     *
     * @param latLng The position of the map that the user tapped.
     */
    override fun onMapClick(latLng: LatLng) {
        // Check whether the same coordinates exist inside our global hash map
        // If not then the user did not tap any of the markers. Thus, we hide the info layout.
        val containsValue = mMarkerManager!!.anyMatchWithCoordinates(latLng)
        if (!containsValue) mParkingMapViewModel!!.hideInfoLayout()
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
     * @see MarkerManager.setUserMarker
     */
    override fun onMarkerClick(marker: Marker): Boolean {
        // If the marker of the user is clicked, ignore it
        if (marker.tag != null) {
            mParkingMapViewModel!!.hideInfoLayout()
            return false
        }
        mParkingMapViewModel!!.updateSelectedLotState(
                mMarkerManager!!.getParkingLotOf(marker)
        )
        return false
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera.
     * If Google Play services is not installed on the device, the user will be prompted to
     * install it inside the SupportMapFragment. This method will only be triggered once the
     * user has installed Google Play services and returned to the app.
     */
    override fun onMapReady(googleMap: GoogleMap) {
        startPostponedEnterTransition()
        mGoogleMap = googleMap // Save a reference of the GoogleMap instance
        mGoogleMap!!.setMinZoomPreference(MIN_ZOOM_LEVEL)
        // Start listening to the user's location updates
        startLocationUpdates()

        // Add listeners to the markers + map
        mGoogleMap!!.setOnMarkerClickListener(this)
        mGoogleMap!!.setOnMapClickListener(this)
    }

    /**
     * Every four intervals check the distance between the user's initial position
     * and the new one. If the distance is over the threshold [.UPDATE_LOCATION_THRESHOLD]
     * then re-fetch all the document Ids of the parking lots that are nearby the user
     *
     * @param updatedPosition The new position of the user.
     */
    private fun checkIntervalForUpdates(updatedPosition: LatLng) {
        mLocationUpdatesCounter++ // Increment the counter
        if (mLocationUpdatesCounter % 4 == 0) { // Every 4 location updates (20 seconds)
            if (shouldFetchParkingDocs(mInitialUserLatLng, updatedPosition)) {
                // Fetch the ids of parking lots based on the user's new position.
                mInitialUserLatLng = updatedPosition
                fetchParkingLots(mInitialUserLatLng!!)
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
     * @see .UPDATE_LOCATION_THRESHOLD
     */
    private fun shouldFetchParkingDocs(initialPosition: LatLng?, updatedPosition: LatLng): Boolean {
        // Calculate the distance between the user's initial position
        // and the updated one.
        return getDistanceApart(
                initialPosition!!, updatedPosition
        ) > UPDATE_LOCATION_THRESHOLD // 100 meters
    }

    /**
     * Navigates from the current Fragment subclass to the
     * [AuthenticatorFragment].
     */
    override fun toAuthenticator() {
        getNavController(requireActivity())
                .navigate(
                        ParkingMapFragmentDirections
                                .actionNavParkingMapFragmentToNavAuthenticatorFragment()
                )
    }

    /**
     * Navigates from the current Fragment subclass to the
     * [ViewBookingsFragment].
     */
    override fun toBookings() {
        getNavController(requireActivity())
                .navigate(
                        ParkingMapFragmentDirections.actionNavParkingMapFragmentToNavViewBookings()
                )
    }

    /**
     * Navigates from the current Fragment subclass to the
     * [AccountFragment].
     */
    override fun toAccount() {
        getNavController(requireActivity())
                .navigate(
                        ParkingMapFragmentDirections.actionNavParkingMapFragmentToNavAccount()
                )
    }

    /**
     * Navigates from the current Fragment subclass to the
     * [FeedbackFragment].
     */
    override fun toFeedback() {
        getNavController(requireActivity())
                .navigate(
                        ParkingMapFragmentDirections.actionNavParkingMapFragmentToNavFeedback()
                )
    }

    /**
     * Navigates from the current Fragment subclass to the
     * [HomeFragment].
     */
    override fun toHome() {
        getNavController(requireActivity())
                .navigate(
                        ParkingMapFragmentDirections.actionNavParkingMapFragmentToNavHome()
                )
    }

    /**
     * Initializes the fragment's GoogleMaps Ui.
     */
    private fun initializeGoogleMaps() {
        val mapFragment = childFragmentManager.findFragmentById(R.id.fragment_parking_map_fcv_map) as SupportMapFragment?
        mapFragment?.getMapAsync(this)
        postponeEnterTransition()
    }

    /**
     * The method's solely purpose is to remove the bug that occurs
     * when navigating back to the map.
     * Bug: the map is extremely laggy.
     */
    private fun clearBackgroundMode() =
    // Solution to
    // https://stackoverflow.com/questions/27978188/google-maps-v2-mapfragment-is-extremely-laggy-on-returning-from-the-backstack
            // TODO: 15/01/2021 Find a better workaround
            WeakReference(Dialog(requireContext())).apply {
                this.get()?.show()
                this.get()!!.show()
                this.get()!!.dismiss()
                this.clear()
            }

    /**
     * Creates and launches a GoogleMaps intent.
     * The user will be navigated to his GoogleMaps app
     * and directions will be set for the given position.
     */
    private val directions: Unit
        get() {
            if (mMarkerManager!!.selectedParkingLot == null) return
            launchGoogleMaps(
                    this,  // Access the coordinates of the selected marker
                    mMarkerManager!!.selectedMarkerLatitude,
                    mMarkerManager!!.selectedMarkerLongitude,
                    mMarkerManager!!.selectedParkingLot.lotName
            )
        }

    /**
     * Iterates through the document changes (updated parking lot instances)
     * list and updates its corresponding local document (parking lot instance)
     * based on its type (e.g. [DocumentChange.Type.MODIFIED]).
     *
     * @param documentChanges The latest parking lot instances from the database.
     */
    private fun updateLocalDocuments(documentChanges: List<DocumentChange>?) {
        if (documentChanges == null || documentChanges.isEmpty()) return

        // Traverse through all the document changes
        for (dc in documentChanges) {
            when (dc.type) {
                DocumentChange.Type.ADDED -> addLot(dc)
                DocumentChange.Type.REMOVED -> removeLot(dc)
                DocumentChange.Type.MODIFIED -> updateLot(dc)
            }
        }
    }

    /**
     * Adds a [Marker] to the map based on the coordinates of the
     * [ParkingLot] object. It also, associates the Marker with the
     * ParkingLot object via a [java.util.HashMap] to enable fast look up.
     *
     * @param dc The newly received DocumentChange, containing info about how
     * a parking lot object in the database got changed.
     */
    private fun addLot(dc: DocumentChange) {
        // Add a marker to the parking's coordinates
        // Add to HashMap to keep track of each marker's corresponding ParkingLot object
        Log.d(TAG, "updateLocalDocuments: ADDED")
        mMarkerManager!!.addMarkerWithContents(mGoogleMap!!, ParkingLot.toParkingLot(dc))
    }

    /**
     * Update the Marker's ParkingLot object with the new one (can be looked up via its parkingId).
     *
     * @param dc The newly received DocumentChange, containing info about how
     * a parking lot object in the database got changed.
     */
    private fun updateLot(dc: DocumentChange) {
        val receivedParkingLot = ParkingLot.toParkingLot(dc)
        updateMarkerContents(receivedParkingLot) { markerOfParking: Marker? ->
            // Keep track of the old value of the object's AvailableSpaces
            val oldAvailableSpaces = mMarkerManager
                    ?.getParkingLotOf(markerOfParking)?.availableSpaces
            // Replace the old lot with the new lot.
            mMarkerManager!!.replaceMarkerContents(receivedParkingLot, markerOfParking!!)
            // Update the info layout's contents if it's showing
            if (mParkingMapViewModel!!.isInfoLayoutShown) {
                mParkingMapViewModel!!.updateSelectedLotState(receivedParkingLot)
                val updatedAvailableSpaces = receivedParkingLot.availableSpaces
                animateAvailabilityColorChanges(
                        binding!!.fragmentParkingMapCvInfoLayout,  // Parent card view
                        binding!!.fragmentParkingMapTxtAvailability,  // child
                        updatedAvailableSpaces,
                        oldAvailableSpaces!!
                )
            }
        }
    }

    /**
     * Remove the marker that is associated with the given parking lot object.
     *
     * @param dc The newly received DocumentChange, containing info about how
     * a parking lot object in the database got changed.
     */
    private fun removeLot(dc: DocumentChange) {
        val receivedParkingLot = ParkingLot.toParkingLot(dc)
        updateMarkerContents(receivedParkingLot) { markerOfParking: Marker? ->
            mMarkerManager!!.removeMarker(markerOfParking)
            // If its info was showing, hide it and inform the user
            val wasVisibleBefore = mParkingMapViewModel!!.hideInfoLayoutWithStateCheck()
            if (wasVisibleBefore) {
                globalStateViewModel.updateToastMessage(R.string.parking_got_removed)
            }
        }
    }

    /**
     * Traverse all markers currently on the map till you find the one that is linked
     * to the given [ParkingLot] object. Then execute the given method.
     *
     * @param receivedParkingLot The Lot to be updated.
     * @param updatable          The interface to update the lot once found.
     */
    private fun updateMarkerContents(receivedParkingLot: ParkingLot, updatable: Consumer<Marker>) {
        // Traverse the markers
        for (markerOfParking in mMarkerManager!!.keySets) { // the key is of type Marker
            // Check whether the marker is associated with a ParkingLot object (marker -> null)
            //  and check whether the marker's Parking lot is not the same with the received one
            if (!mMarkerManager!!.exists(markerOfParking)
                    || mMarkerManager!!.getParkingLotOf(markerOfParking) != receivedParkingLot) continue

            // then update the private parking's content according to its DocumentChange.Type
            updatable.accept(markerOfParking)
        }
    }

    /**
     * Called when the Fragment is no longer resumed.
     * If the loading bar was shown, then hide it.
     */
    override fun onPause() {
        super.onPause()
        if (globalStateViewModel.isLoadingBarShowing) {
            globalStateViewModel.hideLoadingBar()
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
     * @see .onDestroyView
     */
    fun fetchParkingLots(latLng: LatLng) {
        globalStateViewModel.showLoadingBar()
        Log.d(TAG, "fetchParkingLots: Loading Bar ON")
        mParkingMapViewModel!!.fetchParkingLots(latLng.latitude, latLng.longitude
        ) { globalStateViewModel.hideLoadingBar() } // when complete do this

    }

    /**
     * Hooks up "directions" and "book" buttons with appropriate on click listeners.
     */
    private fun attachButtonListeners() {
        // Hook up the "directions" button with an on click listener
        binding!!.fragmentParkingMapImgbtnDirections.setOnClickListener { v: View? -> directions }

        // Hook up the "book" button with an onClick listener
        binding!!.fragmentParkingMapBtnBooking.setOnClickListener { v: View? -> navigateToBookingScreen() }
    }

    /**
     * Initializes the fragment's ViewModel ([.mParkingMapViewModel]).
     */
    private fun initializeViewModel() {
        // Initialize the mParkingMapViewModel
        mParkingMapViewModel = ViewModelProvider(this, ParkingMapViewModelFactory())
                .get(ParkingMapViewModel::class.java)
        globalStateViewModel.updateToastMessage(R.string.loading_map)
    }

    /**
     * Attaches observers to the info layout's state (whenever its state changes, the its visibility
     * on the Ui is updated accordingly) and the state of the currently selected parking lot.
     */
    @Throws(IllegalArgumentException::class)
    private fun attachStateObservers() {
        // Attach observer to ParkingMapViewModel's info layout state
        mParkingMapViewModel!!.infoLayoutState
                .observe(viewLifecycleOwner, { visibility: Int -> updateInfoLayoutVisibilityTo(visibility) })
        // Attach observer to ParkingMapViewModel's selected lot state
        mParkingMapViewModel!!.selectedLotState.observe(viewLifecycleOwner, { lot: ParkingLot? ->
            if (lot != null) {
                showDetails(lot)
                mMarkerManager!!.selectedParkingLot = lot
            }
        })

        // Attach observer to the latest retrieved document changes
        mParkingMapViewModel!!.documentChangesState.observe(viewLifecycleOwner, { documentChanges: List<DocumentChange>? -> updateLocalDocuments(documentChanges) })
        mParkingMapViewModel!!.documentIdsOfNearbyLots.observe(viewLifecycleOwner, { documentIds: Set<String?>? ->
            if (mGoogleMap != null) mGoogleMap!!.moveCamera(CameraUpdateFactory.zoomTo(DEFAULT_ZOOM_LEVEL.toFloat()))
            Log.d(TAG, "IdsState observer: $mDatabaseObserver ids: $documentIds")
            if (documentIds == null || documentIds.isEmpty()) {
                // Display message
                globalStateViewModel.updateToastMessage(R.string.no_nearby_parking_lots)
                return@observe
            }

            // Get all the parking lots where their doc ids are in the documentIds list set
            // then attach an observer to them
            if (mDatabaseObserver == null) {
                // Create a database observer
                DatabaseObserver
                        .createQueryObserver( // Internally adds a snapshot listener
                                mParkingMapViewModel!!.getParkingLots(documentIds)
                        )  // Query reference
                        { value: QuerySnapshot?, error: FirebaseFirestoreException? ->  // Event handler
                            if (value != null) mParkingMapViewModel!!.updateDocumentState(value.documentChanges)
                        }.also { mDatabaseObserver = it }.registerLifecycleObserver(lifecycle)
            } else {
                // Update the query from the database
                mDatabaseObserver!!.updateDatabaseReference(
                        mParkingMapViewModel!!.getParkingLots(documentIds)
                )
            }
        })
        mParkingMapViewModel!!.promptingState.observe(viewLifecycleOwner, { timeToPromptTheUser: Any? ->
            AlertBuilder.showSingleActionAlert(
                    childFragmentManager,
                    R.string.volley_error_title,
                    R.string.volley_error_body
            ) { v: View? -> goBack(requireActivity()) } // go back to home screen
        })
        mParkingMapViewModel!!.navigationToBookingState.observe(viewLifecycleOwner, { selectedLot: ParkingLot? ->
            getNavController(requireActivity())
                    .navigate(ParkingMapFragmentDirections
                            .actionNavParkingMapFragmentToParkingBookingFragment(selectedLot!!))
        }
        )
    }

    /**
     * Shows a plane that contains information about the marker's
     * associated [ParkingLot] object.
     *
     * @param lot The lot that got tapped on the map.
     */
    private fun showDetails(lot: ParkingLot?) {
        mParkingMapViewModel!!.showInfoLayout()

        // Declare strings and initialize them with default values
        var name = UNAVAILABLE
        var availability = UNAVAILABLE
        var slotOffer = UNAVAILABLE
        if (lot != null) {
            // Get the corresponding hash map object
            name = lot.lotName
            availability = lot.getLotAvailability(requireContext())
            slotOffer = getString(R.string.best_offer) + " " + lot.bestOffer.toString() + ""
        }
        // Get a reference to the view and update each infoLayout field with the clicked marker's corresponding data
        binding!!.fragmentParkingMapTxtName.text = name
        binding!!.fragmentParkingMapTxtOffer.text = slotOffer
        binding!!.fragmentParkingMapTxtAvailability.text = availability
    }

    /**
     * Changes the visibility of the info plane based on the specified attribute.
     *
     * @param visibility The state of the visibility (E.g. View.Gone / View.VISIBLE / View.INVISIBLE)
     */
    private fun updateInfoLayoutVisibilityTo(visibility: Int) {
        slideBottom(binding!!.idFragmentParkingMap,
                binding!!.fragmentParkingMapCvInfoLayout, visibility == View.GONE, 250L, null)
    }

    /**
     * Navigate the user to the booking screen.
     */
    private fun navigateToBookingScreen() {
        mParkingMapViewModel!!.navigateToBookingScreen(user, mMarkerManager!!.selectedParkingLot) { message: Int? -> globalStateViewModel.updateToastMessage(message!!) }
    }

    /**
     * Launch a Google Maps intent in which the given latitude and longitude
     * are set the center point of the map. Also, the point is given the
     * specified label.
     * If the device has no apps that support this operation, then a Toast
     * message is displayed.
     *
     * @param fragment   The fragment to make use of.
     * @param latitude  the center point of the map in the Y axis.
     * @param longitude the center point of the map in the X axis.
     * @param label     The label of the point in the map.
     */
    private fun <T : BaseFragment<S>, S : ViewBinding> launchGoogleMaps(fragment: T, latitude: Double, longitude: Double, label: String) {
        // Create Uri (query string) for a Google Maps Intent
        // Launch Google Maps activity
        Intent(Intent.ACTION_VIEW,
                Uri.parse("geo:0,0?q=" + latitude
                        + "," + longitude + "("
                        + label
                        + ")")
        ).setPackage("com.google.android.apps.maps")
                .let {
                    if (it.resolveActivity(fragment.requireContext().packageManager) != null) {
                        fragment.requireContext().startActivity(it)
                    } else {
                        fragment.globalStateViewModel.updateToastMessage(R.string.no_google_maps_app_found)
                    }
                }
    }

    companion object {
        // Constant variables
        val TAG = ParkingMapFragment::class.java.name + "UniqueTag"
        private const val DEFAULT_ZOOM_LEVEL = 16
        private const val UPDATE_LOCATION_THRESHOLD = 100.0
        private const val MIN_ZOOM_LEVEL = 10.0f
        private const val INTERVAL_TIME = 5000L
    }

    /**
     * Returns an instance of [AirLocation] that contains
     * information about the location retrieval behaviour.
     * @return An instance of [AirLocation].
     */
    override fun initializeLocationApi(): AirLocation = AirLocation(
            this.requireActivity(),
            object : AirLocation.Callback {

                override fun onSuccess(locations: ArrayList<Location>) {
                    Log.d(TAG, "onLocationResult: From Map")
                    if (locations.isNullOrEmpty()) return

                    // Access the user's new location.
                    val updatedPosition = LatLng(locations[0].latitude, locations[0].longitude)

                    // If null, assign it to the location received from the first update.
                    if (mInitialUserLatLng == null) mInitialUserLatLng = updatedPosition
                    checkIntervalForUpdates(updatedPosition)

                    // Update the user's location with the received location
                    mUserCurrentLatLng = updatedPosition
                    mMarkerManager!!.setUserMarker(mGoogleMap, mUserCurrentLatLng)
                    // Smoothly moves the camera to the user's position
                    mGoogleMap!!.animateCamera(CameraUpdateFactory.newLatLng(mUserCurrentLatLng))
                }

                override fun onFailure(locationFailedEnum: AirLocation.LocationFailedEnum) {
                    // the reason for failure is given in locationFailedEnum

                    // TODO: Show message based on the locationFailedEnum
                    globalStateViewModel.updateToastMessage(R.string.error_retrieving_location)
                }
            }, false, INTERVAL_TIME
    )
}