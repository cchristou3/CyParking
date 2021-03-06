package io.github.cchristou3.CyParking.ui.views.parking.slots.bookingDetails

import android.graphics.PorterDuff
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import io.github.cchristou3.CyParking.R
import io.github.cchristou3.CyParking.data.interfaces.Navigable
import io.github.cchristou3.CyParking.data.manager.location.LocationManager
import io.github.cchristou3.CyParking.data.model.parking.lot.ParkingLot
import io.github.cchristou3.CyParking.data.model.parking.slot.booking.Booking
import io.github.cchristou3.CyParking.data.model.parking.slot.booking.BookingDetails
import io.github.cchristou3.CyParking.databinding.BookingDetailsFragmentBinding
import io.github.cchristou3.CyParking.ui.components.BaseFragment
import io.github.cchristou3.CyParking.ui.helper.AlertBuilder
import io.github.cchristou3.CyParking.ui.widgets.QRCodeDialog
import io.github.cchristou3.CyParking.utilities.scaleToMatchParent


/**
 * Purpose: Allow the user to view more details about a specific booking.
 * Details include the booking's QR Code, the lot's location (directions), etc.
 *
 * @author Charalambos Christou
 * @since 26/02/21
 */
class BookingDetailsFragment : BaseFragment<BookingDetailsFragmentBinding>(), Navigable {

    companion object {
        // static methods should be placed here
    }

    private lateinit var viewModel: BookingDetailsViewModel
    private var mIsCreated: Boolean = true

    /**
     * Inflate the fragment's Ui.
     * @see BaseFragment.onCreateView
     */
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return super.onCreateView(BookingDetailsFragmentBinding.inflate(inflater))
    }

    /**
     * Called immediately after [.onCreateView]
     * has returned, but before any saved state has been restored in to the view.
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Initialize the viewmodel
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
        binding.bookingDetailsFragmentBtnQrCode.setOnClickListener(null)
        binding.bookingDetailsFragmentFabDirections.setOnClickListener(null)
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
            val selectedBooking: Booking = args.getParcelable(getString(R.string.selected_booking_arg))
            loadPhoto(selectedBooking)
            displayContents(selectedBooking)
            setListenerToQRCodeButton(selectedBooking.qrCode)
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
        binding.bookingDetailsFragmentFabDirections.drawable.setColorFilter(
                resources.getColor(R.color.purple_700),
                // Source = Drawable, Destination = purple_700
                // Thus, the drawable shape will remain unchanged
                // and its color will be blended with purple_700.
                PorterDuff.Mode.SRC_IN)
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
        binding.bookingDetailsFragmentTxtStatus.text =
                (Booking.getStatusText(requireContext(), selectedBooking.isCompleted))
        binding.bookingDetailsFragmentTxtParkingName.text =
                selectedBooking.lotName
        binding.bookingDetailsFragmentTxtDate.text =
                BookingDetails.getDateText(selectedBooking.bookingDetails.dateOfBooking)
        binding.bookingDetailsFragmentTxtStartTime.text =
                selectedBooking.bookingDetails.startingTime.toString()
        // Calculate the end time based on the starting time and the picked slot offer
        val endTime = BookingDetails.Time.getEndTime(selectedBooking.bookingDetails)
        binding.bookingDetailsFragmentTxtEndTime.text = endTime.toString()
        binding.bookingDetailsFragmentTxtOffer.text = selectedBooking.bookingDetails.slotOffer.toString()
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