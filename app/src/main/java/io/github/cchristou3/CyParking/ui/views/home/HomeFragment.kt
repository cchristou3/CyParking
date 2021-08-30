package io.github.cchristou3.CyParking.ui.views.home

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
import io.github.cchristou3.CyParking.R
import io.github.cchristou3.CyParking.apiClient.model.data.parking.slot.booking.Booking
import io.github.cchristou3.CyParking.apiClient.model.data.parking.slot.booking.BookingDetails
import io.github.cchristou3.CyParking.apiClient.model.data.user.LoggedInUser
import io.github.cchristou3.CyParking.data.interfaces.Navigable
import io.github.cchristou3.CyParking.databinding.FragmentHomeBinding
import io.github.cchristou3.CyParking.ui.components.BaseFragment
import io.github.cchristou3.CyParking.ui.components.LocationFragment
import io.github.cchristou3.CyParking.ui.views.home.HomeFragment
import io.github.cchristou3.CyParking.ui.views.home.HomeViewModel
import io.github.cchristou3.CyParking.utilities.hide
import io.github.cchristou3.CyParking.utilities.show
import io.github.cchristou3.CyParking.utilities.slideBottom
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
class HomeFragment : LocationFragment<FragmentHomeBinding>() {
    private val mWasLocationRequested = AtomicBoolean(false)

    private var mHomeViewModel: HomeViewModel? = null

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
        mHomeViewModel!!.navigationToMap.observe(viewLifecycleOwner, { navigateToMap(it) })
        mHomeViewModel!!.navigationToOperator.observe(viewLifecycleOwner, {
            navigateToOperator()
        })

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
                binding!!.fragmentHomeBtnNavToMap
        )
        super.onDestroyView()
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
        if (loggedInUser == null) {
            cleanUpUi()
            return
        }
        if (loggedInUser.isOperator) {
            // Is an operator but not a user
            binding.fragmentHomeBtnMyLot.isEnabled = true
            binding.fragmentHomeBtnMyLot.setOnClickListener { mHomeViewModel!!.navigateToOperator() }
            //initializeOperator(loggedInUser)
        } else { // User is not an operator
            binding.fragmentHomeBtnMyLot.isEnabled = false
            initializeUser(loggedInUser)
        }
    }

    /**
     * Removes any Views that are accessible to logged in users.
     */
    private fun cleanUpUi() {
        // Hide Ui related layout related to logged in users
        hide(binding,binding!!.fragmentHomeCvUserBooking, TRANSITION_DURATION)
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
        show(binding,binding!!.fragmentHomeCvUserBooking, TRANSITION_DURATION)
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
                .setOnClickListener {
                    // Do not allow the user from clicking the booking again
                    // Otherwise, it would trigger unexpected behaviour.
                    binding!!.fragmentHomeBookingItem.bookingItemFullyCv.isClickable = false
                    getNavController(requireActivity())
                            .navigate(HomeFragmentDirections.actionNavHomeToNavBookingDetailsFragment(upcomingBooking),
                                    sharedView.build())
                }
    }


    /**
     * Navigates from the current Fragment subclass to the
     * [io.github.cchristou3.CyParking.ui.views.user.login.AuthenticatorFragment].
     */
    override fun toAuthenticator() {
        navigateTo(
                        HomeFragmentDirections.actionNavHomeToNavAuthenticatorFragment()
                )
    }

    /**
     * Navigates from the current Fragment subclass to the
     * [io.github.cchristou3.CyParking.ui.views.parking.slots.viewBooking.ViewBookingsFragment].
     */
    override fun toBookings() {
        navigateTo(
                        HomeFragmentDirections.actionNavHomeToNavViewBookings()
                )
    }

    /**
     * Navigates from the current Fragment subclass to the
     * [AccountFragment].
     */
    override fun toAccount() {
        navigateTo(
                        HomeFragmentDirections.actionNavHomeToNavAccount()
                )
    }

    /**
     * Navigates from the current Fragment subclass to the
     * [io.github.cchristou3.CyParking.ui.views.user.feedback.FeedbackFragment].
     */
    override fun toFeedback() {
        navigateTo(
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
        navigateTo(
                        HomeFragmentDirections.actionHomeToParkingMap(
                                userLocation
                        )
                )
    }

    /**
     * Navigate to the operator screen
     */
    private fun navigateToOperator() {
        navigateTo(
                        HomeFragmentDirections.actionNavHomeToNavOperatorFragment()
                )
    }

    companion object {
        // Fragment variables
        private val TAG = HomeFragment::class.java.name
        private const val TRANSITION_DURATION = 750L
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