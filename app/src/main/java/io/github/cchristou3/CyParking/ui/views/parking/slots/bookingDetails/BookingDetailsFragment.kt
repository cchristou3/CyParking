package io.github.cchristou3.CyParking.ui.views.parking.slots.bookingDetails

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import androidx.lifecycle.ViewModelProvider
import androidx.transition.TransitionInflater
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.Target
import io.github.cchristou3.CyParking.R
import io.github.cchristou3.CyParking.apiClient.model.data.parking.lot.ParkingLot
import io.github.cchristou3.CyParking.apiClient.model.data.parking.slot.booking.Booking
import io.github.cchristou3.CyParking.apiClient.model.data.parking.slot.booking.BookingDetails
import io.github.cchristou3.CyParking.data.interfaces.Navigable
import io.github.cchristou3.CyParking.data.manager.location.LocationManager
import io.github.cchristou3.CyParking.databinding.BookingDetailsFragmentBinding
import io.github.cchristou3.CyParking.ui.components.BaseFragment
import io.github.cchristou3.CyParking.ui.helper.AlertBuilder
import io.github.cchristou3.CyParking.ui.widgets.QRCodeDialog
import io.github.cchristou3.CyParking.utilities.scaleToMatchParent
import io.github.cchristou3.CyParking.utilities.setColor


/**
 * Purpose: Allow the user to view more details about a specific booking.
 * Details include the booking's QR Code, the lot's location (directions), etc.
 *
 * @author Charalambos Christou
 * @since 2.0 24/03/21
 */
class BookingDetailsFragment : BaseFragment<BookingDetailsFragmentBinding>(), Navigable {

    companion object {
        // static methods should be placed here
    }

    private lateinit var viewModel: BookingDetailsViewModel
    private var mIsCreated: Boolean = true


    /**
     * Called to do initial creation of a fragment.
     * @see <a href='https://jtmuller5-98869.medium.com/fragment-transitions-with-shared-elements-using-android-navigation-7dcfe01aacd'>
     * Using Navigation Component</a>
     * @see <a href='https://medium.com/androiddevelopers/fragment-transitions-ea2726c3f36f'>
     * Using FragmentManager and transactions</a>
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Set both the enter and the return transitions.
        // Used for shared views to allow for a more interactive
        // transition between the home fragment to this one.
        sharedElementEnterTransition = TransitionInflater.from(requireContext())
                .inflateTransition(R.transition.shared_booking).apply { this.duration = 500 }
        sharedElementReturnTransition = sharedElementEnterTransition
    }

    /**
     * Inflate the fragment's Ui.
     * @see BaseFragment.onCreateView
     */
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Postpone enter transition till fragment's data has loaded.
        postponeEnterTransition()
        return super.onCreateView(BookingDetailsFragmentBinding.inflate(inflater), R.string.booking_label)
    }

    /**
     * Called immediately after [.onCreateView]
     * has returned, but before any saved state has been restored in to the view.
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Initialize the ViewModel
        viewModel = ViewModelProvider(this, BookingDetailsViewModelFactory())
                .get(BookingDetailsViewModel::class.java)

        // Observe the user's state
        observeUserState {
            it ?: run { // if logged out, then prompt to login
                promptUserToLoginOrGoBack()
            }
        }
        initializeUi()
    }

    /**
     * Called when the view previously created by [.onCreateView] has
     * been detached from the fragment.  The next time the fragment needs
     * to be displayed, a new view will be created.
     * Note: Always unset listeners before calling the subclass' onDestroyView
     * method.
     */
    override fun onDestroyView() {
        super.removeOnClickListeners(
                binding.bookingDetailsFragmentBtnQrCode,
                binding.bookingDetailsFragmentFabDirections)
        super.onDestroyView()
    }

    /**
     * Prompts the user to either login to gain access rights or
     * to go back to previous screen.
     */
    private fun promptUserToLoginOrGoBack() {
        AlertBuilder.promptUserToLogIn(childFragmentManager, activity, this, R.string.logout_bookings_details_screen_msg)
    }

    /**
     * Initialize the main Ui components.
     */
    private fun initializeUi() {
        arguments?.let { args ->
            val selectedBooking: Booking? = args.getParcelable(getString(R.string.selected_booking_arg))
            selectedBooking?.let {
                loadPhoto(selectedBooking)
                displayContents(selectedBooking)
                setListenerToQRCodeButton(selectedBooking.qrCode)
            }
        }
    }

    /**
     * Load the booking's associated parking lot's photo.
     * Also, prepares the `directions` button.
     */
    private fun loadPhoto(selectedBooking: Booking) {
        // Add an observer to when data about the booking's associated
        // parking lot are fetched.
        viewModel.lotOfBooking.observe(viewLifecycleOwner, {
            displayPhoto(it) // Display its photo
            setListenerToDirectionsButton(it)
        })
        // Fetch the booking's associated parking lot
        viewModel.getLotOfBooking(selectedBooking)
    }

    /**
     * Hook up the `directions` button with a listener.
     * on-click: Launch a Google Maps intent, having the lot's
     * coordinates as its center.
     *
     * @param lot The lot associated with the selected booking.
     */
    private fun setListenerToDirectionsButton(lot: ParkingLot) {
        binding.bookingDetailsFragmentFabDirections.drawable.setColor(
                resources.getColor(R.color.purple_700, activity?.theme))
        binding.bookingDetailsFragmentFabDirections.setOnClickListener {
            LocationManager.launchGoogleMaps(
                    requireContext(),
                    lot.latitude,
                    lot.longitude,
                    lot.lotName
            )
        }
    }

    /**
     * Hook up the `QR Code` button with a listener.
     * on-click: Display a dialog with the Booking's
     * QR Code.
     */
    private fun setListenerToQRCodeButton(qrCode: String) {
        binding.bookingDetailsFragmentBtnQrCode.setOnClickListener {
            // Show a fragment that will display the QR code
            QRCodeDialog(
                    requireContext(),
                    binding.bookingDetailsFragmentClMainCl,
                    qrCode)
                    .show()
        }
    }

    /**
     * Display necessary data of the given booking.
     */
    private fun displayContents(selectedBooking: Booking) {
        binding.bookingItemFullyCv.bookingItemFullyTxtStatus.text =
                (Booking.getStatusText(requireContext(), selectedBooking.isCompleted))
        binding.bookingItemFullyCv
                .bookingItemFullyTxtParkingName.text =
                selectedBooking.lotName
        binding.bookingItemFullyCv
                .bookingItemFullyTxtDate.text =
                BookingDetails.getDateText(selectedBooking.bookingDetails.dateOfBooking)
        binding.bookingItemFullyCv
                .bookingItemFullyTxtStartTime.text =
                selectedBooking.bookingDetails.startingTime.toString()
        // Calculate the end time based on the starting time and the picked slot offer
        val endTime = BookingDetails.Time.getEndTime(selectedBooking.bookingDetails)
        binding.bookingItemFullyCv
                .bookingItemFullyTxtEndTime.text = endTime.toString()
        binding.bookingItemFullyCv
                .bookingItemFullyTxtOffer.text = selectedBooking.bookingDetails.slotOffer.toString()
    }

    /**
     * Display the given lot's photo.
     */
    private fun displayPhoto(parkingLot: ParkingLot) {
        if (!mIsCreated) {
            // If the user is returning to the fragment then
            // the method will get called when the view has not been drawn yet.
            // So, display the photo once the layout has been drawn.
            binding.bookingDetailsFragmentClMainCl.viewTreeObserver
                    .addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
                        override fun onGlobalLayout() {
                            // Once triggered remove listener
                            binding.bookingDetailsFragmentClMainCl.viewTreeObserver.removeOnGlobalLayoutListener(this)
                            loadImageUrl(parkingLot)
                        }
                    })
        } else {
            loadImageUrl(parkingLot)
            mIsCreated = false
        }
    }

    /**
     * Loads the lot's photo to the [ImageView]. if there is one.
     * Otherwise, display a `No image supplied` drawable.
     */
    private fun loadImageUrl(parkingLot: ParkingLot) {
        Glide.with(this)
                .load(parkingLot.lotPhotoUrl)
                .listener(object : RequestListener<Drawable> {
                    override fun onLoadFailed(e: GlideException?, model: Any?, target: Target<Drawable>?, isFirstResource: Boolean): Boolean {
                        startPostponedEnterTransition()
                        return false
                    }

                    override fun onResourceReady(resource: Drawable?, model: Any?, target: Target<Drawable>?, dataSource: DataSource?, isFirstResource: Boolean): Boolean {
                        startPostponedEnterTransition()
                        return false
                    }
                })
                .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                .apply(RequestOptions()
                        .error(scaleToMatchParent(resources, binding.bookingDetailsFragmentClMainCl, R.drawable.ic_photo_not_supplied,
                                0.15f))
                        .placeholder(scaleToMatchParent(resources, binding.bookingDetailsFragmentClMainCl, R.drawable.ic_photo_placeholder,
                                0.15f))
                        .fitCenter()
                        .override(
                                (binding.bookingDetailsFragmentClMainCl.measuredWidth * 0.9f).toInt(),
                                (binding.bookingDetailsFragmentClMainCl.measuredHeight * 0.35f).toInt()
                        ))
                .into(binding.bookingDetailsFragmentIvLotPhoto)
    }

    /**
     * Navigates from the current Fragment subclass to the
     * [io.github.cchristou3.CyParking.ui.views.user.login.AuthenticatorFragment].
     */
    override fun toAuthenticator() {
        getNavController(requireActivity())
                .navigate(BookingDetailsFragmentDirections.actionNavBookingDetailsFragmentToNavAuthenticatorFragment())
    }

    /**
     * Navigates from the current Fragment subclass to the
     * [io.github.cchristou3.CyParking.ui.views.parking.slots.viewBooking.ViewBookingsFragment].
     */
    override fun toBookings() {
        goBack(requireActivity())
    }

    /**
     * Navigates from the current Fragment subclass to the
     * [AccountFragment].
     */
    override fun toAccount() {
        getNavController(requireActivity())
                .navigate(BookingDetailsFragmentDirections.actionNavBookingDetailsFragmentToNavAccount())
    }

    /**
     * Navigates from the current Fragment subclass to the
     * [io.github.cchristou3.CyParking.ui.views.user.feedback.FeedbackFragment].
     */
    override fun toFeedback() {
        getNavController(requireActivity())
                .navigate(BookingDetailsFragmentDirections.actionNavBookingDetailsFragmentToNavFeedback())
    }

    /**
     * Navigates from the current Fragment subclass to the
     * [HomeFragment].
     */
    override fun toHome() {
        getNavController(requireActivity())
                .navigate(BookingDetailsFragmentDirections.actionNavBookingDetailsFragmentToNavHome())
    }
}