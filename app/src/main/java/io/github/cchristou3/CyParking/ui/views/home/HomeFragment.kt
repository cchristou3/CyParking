package io.github.cchristou3.CyParking.ui.views.home

import android.content.Intent
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.FragmentNavigator
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot
import com.google.zxing.integration.android.IntentIntegrator
import io.github.cchristou3.CyParking.R
import io.github.cchristou3.CyParking.apiClient.model.data.parking.lot.ParkingLot
import io.github.cchristou3.CyParking.apiClient.model.data.parking.slot.booking.Booking
import io.github.cchristou3.CyParking.apiClient.model.data.parking.slot.booking.BookingDetails
import io.github.cchristou3.CyParking.apiClient.model.data.user.LoggedInUser
import io.github.cchristou3.CyParking.data.interfaces.Navigable
import io.github.cchristou3.CyParking.data.manager.DatabaseObserver
import io.github.cchristou3.CyParking.databinding.FragmentHomeBinding
import io.github.cchristou3.CyParking.ui.components.BaseFragment
import io.github.cchristou3.CyParking.ui.components.LocationFragment
import io.github.cchristou3.CyParking.ui.views.home.HomeFragment
import io.github.cchristou3.CyParking.ui.views.home.HomeViewModel
import io.github.cchristou3.CyParking.utilities.slideBottom
import io.github.cchristou3.CyParking.utilities.slideTop
import io.github.cchristou3.CyParking.utils.updateViewVisibilityTo
import mumayank.com.airlocationlibrary.AirLocation
import java.util.concurrent.atomic.AtomicBoolean

/**
 * Purpose:
 *
 *Show to the user all available action options
 * If the user is logged in and has a role of "Operator"
 * then a lot information area is shown. If the user does not
 * have a lot registered, an option for registering one is displayed.
 * Otherwise, if the user has a lot, then display critical info about it.
 *
 *
 * In terms of Authentication, this is achieved by communicating with the hosting
 * activity [MainHostActivity] via the [GlobalStateViewModel].
 *
 *
 * @author Charalambos Christou
 * @version 16.0 27/03/21
 */
class HomeFragment : LocationFragment<FragmentHomeBinding>(), Navigable {
    private val mWasLocationRequested = AtomicBoolean(false)

    // Members related to the Operator
    private var mOperatorViewModel: OperatorViewModel? = null
    private var mHomeViewModel: HomeViewModel? = null
    private var mDatabaseObserver: DatabaseObserver<Query, QuerySnapshot>? = null
    private var mIntentIntegrator: IntentIntegrator? = null

    /**
     * Initialize the fragment.
     *
     * @param savedInstanceState If the fragment is being re-created from
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mHomeViewModel = ViewModelProvider(this).get(HomeViewModel::class.java)
    }

    /**
     * Inflates our fragment's view.
     *
     * @param inflater           Inflater which will inflate our view
     * @param container          The parent view
     * @param savedInstanceState A bundle which contains info about previously stored data
     * @return The view of the fragment
     * @see BaseFragment.onCreateView
     */
    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?, savedInstanceState: Bundle?): View? =
            super.onCreateView(FragmentHomeBinding.inflate(inflater), R.string.menu_home)


    /**
     * Invoked at the completion of onCreateView. Initializes fragment's ViewModel.
     * Lastly, listeners are attached to all buttons.
     *
     * @param view               The view of the fragment
     * @param savedInstanceState A bundle which contains info about previously stored data
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Listen to when the user should be navigated to the map
        mHomeViewModel!!.navigationToMap.observe(viewLifecycleOwner, { userLocation: LatLng -> navigateToMap(userLocation) })

        // Listen to the user state
        observeUserState { loggedInUser: LoggedInUser? -> updateUi(loggedInUser) }
        initializeButtonListeners()

        // Each time the home screen becomes visible reset the map button back to normal
        resetMapButton()

        // TODO: Add splash screen till the app has been initialized (FirebaseApp, Network broadcasters, validating user's data, etc.).
    }

    /**
     * Called when the view previously created by [.onCreateView] has
     * been detached from the fragment.  The next time the fragment needs
     * to be displayed, a new view will be created.  This is called
     * after [.onStop] and before [.onDestroy].  It is called
     * *regardless* of whether [.onCreateView] returned a
     * non-null view.  Internally it is called after the view's state has
     * been saved but before it has been removed from its parent.
     *
     * @see BaseFragment.onDestroyView
     */
    override fun onDestroyView() {
        super.removeOnClickListeners(
                binding!!.fragmentHomeMbtnRegisterParkingLot,
                binding!!.fragmentHomeMbtnRegisterParkingLot,
                binding!!.fragmentHomeBtnIncrement,
                binding!!.fragmentHomeBtnDecrement,
                binding!!.fragmentHomeBtnNavToMap
        )
        super.onDestroyView()
    }

    /**
     * Receive the result from a previous call to
     * [.startActivityForResult].
     *
     * @param requestCode The integer request code originally supplied to
     * startActivityForResult(), allowing you to identify who this
     * result came from.
     * @param resultCode  The integer result code returned by the child activity
     * through its setResult().
     * @param data        An Intent, which can return result data to the caller
     */
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (mOperatorViewModel != null) mOperatorViewModel!!.handleQRCodeScannerContents( // Access the qr code's payload
                IntentIntegrator.parseActivityResult(requestCode, resultCode, data),  // Access the previously stored lot reference
                intentIntegrator!!.moreExtras[LOT_REFERENCE_KEY] as DocumentReference?) { message: Int? -> globalStateViewModel.updateToastMessage(message!!) }
    }

    /**
     * Attach on click listeners to the 'scan lot' and 'parking map' buttons.
     * 'Scan lot' on click: open up the QR code scanner.
     * 'Parking map' on click: transition to the Google maps fragment.
     */
    private fun initializeButtonListeners() {
        // TODO: 10/02/2021 Implement the buttons and add descriptions below then
        binding!!.fragmentHomeBtnScanLot.setOnClickListener { Toast.makeText(requireContext(), "Not implemented yet!", Toast.LENGTH_SHORT).show() }

        // Attach listener to "Parking Map" button
        binding!!.fragmentHomeBtnNavToMap.setOnClickListener { requestLocation() }
    }

    /**
     * Request the user's location.
     * Ensures that only a single request is sent
     * even when the user has clicked the button
     * multiple consecutive times.
     */
    private fun requestLocation() {
        if (mWasLocationRequested.get()) return
        mWasLocationRequested.compareAndSet(false, true)
        // Request for the user's latest known location
        Log.d(TAG, "requestUserLocationUpdates")
        startLocationUpdates()
    }

    /**
     * Enable the map button to accept further clicks.
     */
    private fun resetMapButton() = mWasLocationRequested.set(false)


    /**
     * Update the Ui based on the given [LoggedInUser] instance.
     * If the user is not logged in, or is but is not as an operator,
     * everything related to the operator is hidden.
     *
     * @param loggedInUser The current [LoggedInUser] instance.
     */
    private fun updateUi(loggedInUser: LoggedInUser?) {

        // TODO: 29/03/2021 In the following cases:
        //   - If the user is not logged in,
        //   - The user is logged in but not as an operator and he/she has no upcoming bookings
        //   Then the lower screen will be left blank (which looks ugly).
        //   Instead of that, use that section to recommend to the user nearby parking lots, offers, etc.
        if (loggedInUser == null) {
            // If operator logged out, remove observer to its parking lot
            if (mDatabaseObserver != null) mDatabaseObserver!!.unregisterLifecycleObserver()
            cleanUpUi()
            return
        }

        // TODO: 10/02/2021 Display QR Code scanner button:
        //  users use it to can scan the QR Code of an operator and
        //  they will be navigated to the payment screen (see Instant App)
        if (loggedInUser.isOperator) {
            // Is an operator but not a user
            initializeOperator(loggedInUser)
        } else { // User is not an operator
            // Hide anything related to parking lot from the user
            binding!!.fragmentHomeCvLotInfo.visibility = View.GONE
            // And display an upcoming booking if there is one
            initializeUser(loggedInUser)
        }
    }

    /**
     * Removes any Views that are accessible to logged in users.
     */
    private fun cleanUpUi() {
        if (binding!!.fragmentHomeBtnScanBooking.isShown) { // Operator has a lot
            // Hide the QR Code scanner to be used to scan bookings via animation
            slideTop(
                    binding!!.root, binding!!.fragmentHomeBtnScanBooking, true, TRANSITION_DURATION
            )  // Once that view is hidden, hide the lot info card view
            /* next animation */
            { hide(binding!!.fragmentHomeCvLotInfo) }
        } else {
            // This is the case when the user did not register a lot and only the register lot layout is shown
            hide(binding!!.fragmentHomeCvLotInfo)
        }

        // Hide Ui related layout related to logged in users
        hide(binding!!.fragmentHomeCvUserBooking)
    }

    /**
     * Hide the given view if it is shown.
     *
     * @param view the view to hide.
     */
    private fun hide(view: View) {
        if (view.isShown) {
            slideBottom(binding!!.root, view, true, TRANSITION_DURATION, null)
        }
    }

    /**
     * Show the given view if it is hidden.
     *
     * @param view the view to show.
     */
    private fun show(view: View) {
        if (!view.isShown) {
            slideBottom(binding!!.root, view, false, TRANSITION_DURATION, null)
        }
    }

    /**
     * Initialize components related to the user.
     *
     * @param loggedInUser the current instance of [LoggedInUser]
     */
    private fun initializeUser(loggedInUser: LoggedInUser) {
        // Instantiate the ViewModel
        val mUserViewModel = ViewModelProvider(this, UserViewModel.Factory()).get(UserViewModel::class.java)
        mUserViewModel.upcomingBooking.observe(viewLifecycleOwner, { upcomingBooking: Booking -> displayBooking(upcomingBooking) })
        getUpcomingBooking(mUserViewModel, loggedInUser)
        mUserViewModel.hideBooking.observe(viewLifecycleOwner, { hideBookingAndGetNext(mUserViewModel, loggedInUser) })
    }

    /**
     * Hides the current booking, and fetches the next one.
     *
     * @param mUserViewModel the business logic handler.
     * @param loggedInUser   the current instance of LoggedInUser.
     */
    private fun hideBookingAndGetNext(mUserViewModel: UserViewModel, loggedInUser: LoggedInUser) {
        slideBottom(binding!!.root,
                binding!!.fragmentHomeCvUserBooking,
                true, TRANSITION_DURATION) { // This time we want to fetch the next upcoming booking
            // and not animate another view
            getUpcomingBooking(mUserViewModel, loggedInUser)
        }
    }

    /**
     * Retrieve the most recent upcoming booking of the given user.
     *
     * @param mUserViewModel Does the business logic.
     * @param loggedInUser   The current instance of [LoggedInUser].
     */
    private fun getUpcomingBooking(mUserViewModel: UserViewModel, loggedInUser: LoggedInUser) {
        mUserViewModel.getUpcomingBooking(loggedInUser.userId, requireActivity()) { message: Int? -> globalStateViewModel.updateToastMessage(message!!) }
    }

    /**
     * Perform necessary Ui updates to display the given booking,
     * while also attach it an on click listener
     * on-click: navigate to booking details fragment.
     *
     * @param upcomingBooking The upcoming booking.
     */
    private fun displayBooking(upcomingBooking: Booking) {
        // Make the user related CardView visible if it is not already
        show(binding!!.fragmentHomeCvUserBooking)

        // Update the contents of its children.
        binding!!.fragmentHomeBookingItem.bookingItemFullyTxtDate.text = BookingDetails.getDateText(upcomingBooking.bookingDetails.dateOfBooking)
        binding!!.fragmentHomeBookingItem.bookingItemFullyTxtOffer.text = upcomingBooking.bookingDetails.slotOffer.toString(requireContext())
        binding!!.fragmentHomeBookingItem.bookingItemFullyTxtParkingName.text = upcomingBooking.lotName
        binding!!.fragmentHomeBookingItem.bookingItemFullyTxtStatus.text = Booking.getStatusText(requireContext(), upcomingBooking.isCompleted)
        binding!!.fragmentHomeBookingItem.bookingItemFullyTxtStartTime.text = upcomingBooking.bookingDetails.startingTime.toString()
        binding!!.fragmentHomeBookingItem.bookingItemFullyTxtEndTime.text = BookingDetails.Time.getEndTime(upcomingBooking.bookingDetails).toString()

        // Set the shared views to participate in the transition - see below
        val sharedView = FragmentNavigator.Extras.Builder()
        sharedView.addSharedElement(binding!!.fragmentHomeBookingItem.bookingItemFullyCv, getString(R.string.shared_booking_card_view))
        sharedView.addSharedElement(binding!!.fragmentHomeCvUserBooking, getString(R.string.shared_parent))

        // Make the booking clickable
        binding!!.fragmentHomeBookingItem.bookingItemFullyCv.isClickable = true
        // Hook up the whole card view with an on click listener
        // on-click: navigate to booking details.
        binding!!.fragmentHomeBookingItem.bookingItemFullyCv
                .setOnClickListener { v: View? ->
                    // Do not allow the user from clicking the booking again
                    // Otherwise, it would trigger unexpected behaviour.
                    binding!!.fragmentHomeBookingItem.bookingItemFullyCv.isClickable = false
                    getNavController(requireActivity())
                            .navigate(HomeFragmentDirections.actionNavHomeToNavBookingDetailsFragment(upcomingBooking),
                                    sharedView.build())
                }
    }

    /**
     * Initializes both the Ui and the database logic related to the operator.
     *
     * @param loggedInUser current user.
     */
    private fun initializeOperator(loggedInUser: LoggedInUser) {
        // TODO: 19/01/2021 Encapsulate all operator logic to a fragment and simply inflate it
        // Initialize the OperatorViewModel
        mOperatorViewModel = ViewModelProvider(this,
                OperatorViewModelFactory()).get(OperatorViewModel::class.java)

        // Attach observer to update the view's parking lot info whenever it changes
        mOperatorViewModel!!.parkingLotState.observe(viewLifecycleOwner, { userParkingLot: ParkingLot -> updateLotContents(userParkingLot) }) // Display the parking lot's contents

        // Get the operator's lot info from the database.
        getParkingLotInfo(loggedInUser.userId)

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
    private fun getParkingLotInfo(operatorId: String) {
        // Initialize the fragment's QueryObserver
        mDatabaseObserver = DatabaseObserver.createQueryObserver(
                mOperatorViewModel!!.observeParkingLot(operatorId)
        )  // The Query
        { value: QuerySnapshot?, error: FirebaseFirestoreException? ->  // The Event listener
            if (error != null || value == null) return@createQueryObserver   // TODO: Handle error
            if (value.isEmpty) { // The operator did not register lot yet
                displayLotRegistrationLayout()
                return@createQueryObserver
            }

            // Remove listeners from the register lot button button
            binding!!.fragmentHomeMbtnRegisterParkingLot.setOnClickListener(null)

            // If the operator has registered a lot already, display its info
            val userParkingLot = value.documents[0].toObject(ParkingLot::class.java)
                    ?: return@createQueryObserver
            if (user != null && userParkingLot.operatorId != user!!.userId) return@createQueryObserver

            // Get a reference to the document
            val ref = value.documents[0].reference

            // Hook up buttons with listeners
            setUpOperatorButtons(ref, userParkingLot)

            // Trigger parking lot update.
            binding!!.fragmentHomeClRegisterLotInfo.visibility = View.VISIBLE
            mOperatorViewModel!!.updateLotState(userParkingLot)
        }
        // Register it for lifecycle observation
        mDatabaseObserver!!.registerLifecycleObserver(lifecycle)
    }

    /**
     * Hook up the `scan booking` button with an on click listener.
     * onclick: initialize the QR Code scanner.
     *
     * @param lotRef A document reference to the operator's parking lot.
     */
    private fun setUpScanBookingButton(lotRef: DocumentReference?) {
        binding!!.fragmentHomeBtnScanBooking.setOnClickListener {
            // Only available to operators
            intentIntegrator
                    ?.addExtra(LOT_REFERENCE_KEY, lotRef)
                    ?.initiateScan()
        }
    }

    /**
     * Return the fragment's [.mIntentIntegrator].
     * Lazy initialization is used.
     *
     * @return the fragment's [.mIntentIntegrator].
     */
    private val intentIntegrator: IntentIntegrator?
        get() {
            if (mIntentIntegrator == null) {
                mIntentIntegrator = IntentIntegrator
                        .forSupportFragment(this@HomeFragment)
                        .setDesiredBarcodeFormats(IntentIntegrator.QR_CODE)
                        .setCameraId(0)
                        .setPrompt(getString(R.string.qr_code_scanner_bottom_text))
                        .setBeepEnabled(true)
                        .setBarcodeImageEnabled(true)
                        .setCaptureActivity(PortraitCaptureActivity::class.java)
            }
            return mIntentIntegrator
        }

    /**
     * Updates the views related to the lot with the specified [ParkingLot]
     * instance.
     *
     * @param userParkingLot The updated version of the parking lot.
     */
    private fun updateLotContents(userParkingLot: ParkingLot) {
        checkVisibilityOfAppropriateLayout(View.VISIBLE, View.GONE)
        // Display the lot's details
        binding!!.fragmentHomeTxtLotCapacity.text = userParkingLot.getLotAvailability(requireContext())
        binding!!.fragmentHomeTxtLotName.text = String.format(getString(R.string.lot_name), userParkingLot.lotName)
    }

    /**
     * Displays a view giving the operator the option
     * to register a parking lot.
     */
    private fun displayLotRegistrationLayout() {
        checkVisibilityOfAppropriateLayout(View.GONE, View.VISIBLE)
        // Attach listener to "Register Parking lot" button
        binding!!.fragmentHomeMbtnRegisterParkingLot
                .setOnClickListener { v: View? ->  // Navigate to the parking lot registration form
                    getNavController(requireActivity())
                            .navigate(
                                    HomeFragmentDirections.actionNavHomeToNavRegisterLotFragment()
                            )
                }
    }

    /**
     * Changes the visibility of "lot info" layout and "register lot" layout,
     * according to the specified arguments.
     * If the above layouts' parent is not visible, then animate it to the UI.
     *
     * @param lotInfoVisibility     The new visibility of the lot info layout.
     * @param registerLotVisibility The new visibility of register lot layout.
     */
    private fun checkVisibilityOfAppropriateLayout(lotInfoVisibility: Int, registerLotVisibility: Int) {
        updateViewVisibilityTo(binding!!.fragmentHomeClShowLotInfo, lotInfoVisibility) // showLotInfo layout
        updateViewVisibilityTo(binding!!.fragmentHomeClRegisterLotInfo, registerLotVisibility) // registerLotInfo layout
        if (lotInfoVisibility == View.VISIBLE) { // Operator has a lot
            // Display the QR Code scanner to be used to scan bookings via animation
            slideTop(
                    binding!!.root, binding!!.fragmentHomeBtnScanBooking, false, TRANSITION_DURATION
            )  // Once that view is shown, display the lot info card view
            /* next animation */
            {
                slideBottom(binding!!.root,
                        binding!!.fragmentHomeCvLotInfo, false, TRANSITION_DURATION, null)
            }
        } else { // Only show the lot info card view
            show(binding!!.fragmentHomeCvLotInfo)
        }
    }

    /**
     * Hooks up "increment", "decrement", and "scan booking" buttons with on click listeners.
     *
     * @param ref            A DocumentReference of the parking lot in the database.
     * @param userParkingLot The latest retrieved parking lot of the database.
     */
    private fun setUpOperatorButtons(ref: DocumentReference, userParkingLot: ParkingLot) {
        // add an appropriate on click listener
        setUpScanBookingButton(ref)

        // Attach listeners to "increment", "decrement" buttons
        binding!!.fragmentHomeBtnIncrement.setOnClickListener { v: View? ->
            // If the lot has available spaces decrease its value by one
            // E.g. 40/40 -> do nothing
            if (userParkingLot.availableSpaces > 0) {
                mOperatorViewModel!!.incrementPersonCount(ref)
            }
        }
        binding!!.fragmentHomeBtnDecrement.setOnClickListener { v: View? ->
            // If the lot has the same number of available spaces as its capacity do nothing.
            // E.g. 0/40 -> do nothing
            if (userParkingLot.availableSpaces < userParkingLot.capacity) {
                // Otherwise, increase in its value by one.
                mOperatorViewModel!!.decrementPersonCount(ref)
            }
        }
    }

    /**
     * Navigates from the current Fragment subclass to the
     * [io.github.cchristou3.CyParking.ui.views.user.login.AuthenticatorFragment].
     */
    override fun toAuthenticator() {
        getNavController(requireActivity())
                .navigate(
                        HomeFragmentDirections.actionNavHomeToNavAuthenticatorFragment()
                )
    }

    /**
     * Navigates from the current Fragment subclass to the
     * [io.github.cchristou3.CyParking.ui.views.parking.slots.viewBooking.ViewBookingsFragment].
     */
    override fun toBookings() {
        getNavController(requireActivity())
                .navigate(
                        HomeFragmentDirections.actionNavHomeToNavViewBookings()
                )
    }

    /**
     * Navigates from the current Fragment subclass to the
     * [AccountFragment].
     */
    override fun toAccount() {
        getNavController(requireActivity())
                .navigate(
                        HomeFragmentDirections.actionNavHomeToNavAccount()
                )
    }

    /**
     * Navigates from the current Fragment subclass to the
     * [io.github.cchristou3.CyParking.ui.views.user.feedback.FeedbackFragment].
     */
    override fun toFeedback() {
        getNavController(requireActivity())
                .navigate(
                        HomeFragmentDirections.actionNavHomeToNavFeedback()
                )
    }

    /**
     * Navigates from the current Fragment subclass to the
     * [HomeFragment].
     */
    override fun toHome() { /* Already in this screen. Thus, no need to implement this method. */
    }

    /**
     * Navigate to the GoogleMaps fragment.
     * Transfer the user's latest location.
     *
     * @param userLocation The user's latest known location.
     */
    private fun navigateToMap(userLocation: LatLng) {
        getNavController(requireActivity())
                .navigate(
                        HomeFragmentDirections.actionHomeToParkingMap(
                                userLocation
                        )
                )
    }

    companion object {
        // Fragment variables
        private val TAG = HomeFragment::class.java.name
        private const val TRANSITION_DURATION = 750L
        private const val LOT_REFERENCE_KEY = "ref"
    }

    override fun initializeLocationApi(): AirLocation =
            AirLocation(this.requireActivity(), object : AirLocation.Callback {

                override fun onSuccess(locations: ArrayList<Location>) = mHomeViewModel!!.navigateToMap(locations)

                override fun onFailure(locationFailedEnum: AirLocation.LocationFailedEnum) {
                    // the reason for failure is given in locationFailedEnum

                    // TODO: Show message based on the locationFailedEnum
                    globalStateViewModel.updateToastMessage(R.string.error_retrieving_location)
                    resetMapButton()
                }
            }, true)
}