package io.github.cchristou3.CyParking.ui.views.parking.slots.booking

import android.content.Context
import android.content.Intent
import androidx.core.util.Consumer
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.EventListener
import io.github.cchristou3.CyParking.PaymentSessionHelper
import io.github.cchristou3.CyParking.R
import io.github.cchristou3.CyParking.apiClient.model.data.parking.lot.ParkingLot
import io.github.cchristou3.CyParking.apiClient.model.data.parking.lot.SlotOffer
import io.github.cchristou3.CyParking.apiClient.model.data.parking.slot.booking.Booking
import io.github.cchristou3.CyParking.apiClient.model.data.parking.slot.booking.BookingDetails
import io.github.cchristou3.CyParking.apiClient.model.data.user.LoggedInUser
import io.github.cchristou3.CyParking.apiClient.remote.repository.BookingRepository
import io.github.cchristou3.CyParking.data.manager.DatabaseObserver
import io.github.cchristou3.CyParking.data.manager.EncryptionManager
import io.github.cchristou3.CyParking.data.manager.EncryptionManager.Companion.hex
import io.github.cchristou3.CyParking.ui.components.SingleLiveEvent
import io.github.cchristou3.CyParking.utils.DateTimeUtility.isInValidTime
import io.github.cchristou3.CyParking.utils.Utility
import org.jetbrains.annotations.Contract
import java.text.ParseException
import java.util.*

/**
 * A ViewModel implementation, adopted to the [BookingFragment] fragment.
 * Purpose: Data persistence during configuration changes.
 *
 * @author Charalambos Christou
 * @since 6.0 27/03/21
 * @constructor Initialize the [BookingViewModel]'s [BookingRepository] and [PaymentSessionHelper] instances.
 * @param mBookingRepository the ViewModel's data source
 * @param mPaymentSessionHelper the ViewModel's payment manager.
 */
class BookingViewModel(private val mBookingRepository: BookingRepository, private val mPaymentSessionHelper: PaymentSessionHelper)
    : ViewModel() {

    // Data members
    private val mPickedDate = MutableLiveData(
            io.github.cchristou3.CyParking.utils.DateTimeUtility
                    .dateToString(io.github.cchristou3.CyParking.utils.DateTimeUtility.getCurrentDate())
    )
    private val mPickedStartingTimeState = MutableLiveData(BookingDetails.Time.getCurrentTime())
    private val mPickedSlotOffer = MutableLiveData<SlotOffer?>()
    private val mBookingButtonState = MutableLiveData(false) // Initially disabled
    private val mQRCodeButtonState = MutableLiveData(false) // Initially hidden
    private val mSnackBarState = SingleLiveEvent<String>()
    private val mPaymentMethod = SingleLiveEvent<String>()
    private val mAlertState = SingleLiveEvent<String>()
    private val mShouldStartPaymentFlow = SingleLiveEvent<Boolean>()
    private val mNewParkingLotVersionState = MutableLiveData<ParkingLot>()
    private val mIsBookingCompleted = MutableLiveData<Boolean>(false)

    /**
     * Sets the value of [.mQRCodeMessage]
     * with the given argument.
     */
    var qRCodeMessage: String? = null

    /**
     * Updates the value of [.snackBarState]
     * with the given argument.
     */
    fun updateAlertErrorState(error: String) {
        mAlertState.value = error
    }

    /**
     * Updates the value of [.snackBarState]
     * with the given argument.
     */
    fun updatePaymentMethodState(paymentMethodDetails: String) {
        mPaymentMethod.value = paymentMethodDetails
    }

    /**
     * Updates the value of [.snackBarState]
     * with the given argument.
     */
    fun updateSnackBarState(bookingId: String) {
        mSnackBarState.value = bookingId
    }


    /**
     * Updates the value of [.mQRCodeButtonState]
     * with the given argument.
     */
    fun updateQRCodeButtonState(show: Boolean) {
        mQRCodeButtonState.value = show
    }


    /**
     * Updates the value of [.mBookingButtonState]
     * with the given argument. If the state already has
     * that value then it is ignored.
     */
    fun updateBookingButtonState(isEnabled: Boolean) {
        if (mBookingButtonState.value == isEnabled) return
        mBookingButtonState.value = isEnabled
    }

    /**
     * Updates the value of [.mPickedSlotOffer] with the
     * specified one.
     *
     * @param newSlotOffer The latest selected slot offer.
     */
    fun updateSlotOffer(newSlotOffer: SlotOffer?) {
        mPickedSlotOffer.value = newSlotOffer
    }

    /**
     * Updates the value of [.mPickedStartingTime] with the
     * specified one.
     *
     * @param hours   The selected hour.
     * @param minutes The selected minutes.
     */
    fun updateStartingTime(hours: Int, minutes: Int) {
        mPickedStartingTimeState.value = BookingDetails.Time(hours, minutes)
    }

    /**
     * Updates the value of [.mPickedDate] with the
     * date formed by the specified arguments.
     *
     * @param selectedYear  The selected Year.
     * @param selectedMonth The selected Month.
     * @param selectedDay   The selected Day.
     */
    fun updatePickedDate(selectedYear: Int, selectedMonth: Int, selectedDay: Int) {
        mPickedDate.value = io.github.cchristou3.CyParking.utils.DateTimeUtility.dateToString(selectedYear, selectedMonth, selectedDay)
    }

    /**
     * Getters for all(almost) its LiveData members
     * and their values.
     */
    val pickedDate: String?
        get() = mPickedDate.value
    val pickedStartingTime: BookingDetails.Time?
        get() = mPickedStartingTimeState.value
    val startingTimeState: LiveData<BookingDetails.Time>
        get() = mPickedStartingTimeState
    val slotOffer: SlotOffer?
        get() = mPickedSlotOffer.value
    val dateState: LiveData<String>
        get() = mPickedDate
    val slotOfferState: LiveData<SlotOffer?>
        get() = mPickedSlotOffer
    val snackBarState: LiveData<String>
        get() = mSnackBarState
    val bookingButtonState: LiveData<Boolean>
        get() = mBookingButtonState
    val paymentMethod: LiveData<String>
        get() = mPaymentMethod
    val alertErrorState: LiveData<String>
        get() = mAlertState
    val qRCodeButtonState: LiveData<Boolean>
        get() = mQRCodeButtonState
    val paymentFlow: LiveData<Boolean>
        get() = mShouldStartPaymentFlow
    val newParkingLotVersion: LiveData<ParkingLot>
        get() = mNewParkingLotVersionState
    val isBookingCompleted: LiveData<Boolean>
        get() = mIsBookingCompleted


    /**
     * Returns a DocumentReference of the specified parking lot
     * in the database.
     *
     * @param selectedLot A parking lot object.
     * @return A reference of the specified lot in the database.
     */
    private fun getParkingLotReference(selectedLot: ParkingLot?): DocumentReference {
        return mBookingRepository.getParkingLot(selectedLot!!)
    }

    /**
     * Create a new [EventListener] to handle updates related to the
     * selected parking lot.
     *
     * @param context The context to make use of.
     * @return An instance of [EventListener].
     */
    private fun getSnapShotListener(context: Context): EventListener<DocumentSnapshot> = EventListener<DocumentSnapshot> { value, error ->
        // The Event Listener
        if (error != null || value == null) return@EventListener
        val lot = value.toObject(ParkingLot::class.java)
        if (lot == null) {
            updateAlertErrorState(context.getString(R.string.no_lot_found_title))
            return@EventListener
        }
        // Trigger observe update
        mNewParkingLotVersionState.value = lot
    }

    /**
     * Creates a new instance of [DatabaseObserver]
     * that will observe the selected parking lot
     * and trigger the given [EventListener] when
     * updates occur.
     *
     * @param context the context to make use of.
     * @param selectedParking the selected parking lot.
     */
    fun observeParkingLotToBeBooked(context: Context, selectedParking: ParkingLot): DatabaseObserver<DocumentReference, DocumentSnapshot>? {
        isBookingCompleted.value?.let {
            if (it) {
                return DatabaseObserver.createDocumentReferenceObserver(
                        getParkingLotReference(selectedParking),  // The Document Reference
                        getSnapShotListener(context)) // The event listener
            }
        }
        return null
    }

    /**
     * Validate the booking's data.
     * If not valid then terminate the method.
     * Otherwise, perform a server-side validation (check if it already exists in the database).
     * If also valid, then charge the user and store its booking in the database.
     *
     * @param user the current instance of [LoggedInUser].
     * @param selectedParking the selected [ParkingLot] instance.
     * @param showLoadingBar a [Runnable] responsible for showing the Ui's loading bar.
     * @param hideLoadingBar a [Runnable] responsible for hiding the Ui's loading bar.
     */
    fun bookParkingLot(
            user: LoggedInUser?, selectedParking: ParkingLot, showLoadingBar: Runnable,
            hideLoadingBar: Runnable, displayToast: Consumer<Int>
    ) {
        val isValid = validateData(user, selectedParking, displayToast)
        if (!isValid) return

        // TODO: 05/02/2021 Check time if the date is the same if the user picked today's date
        //  e.g. today: 01/01/21 16:00, user picked 01/01/21 13:00 -> not allowed
        //  cannot book, the time has already passed.

        mBookingButtonState.value = false // Disable the book button
        // to restrict the user from clicking it again, causing subsequent requests.

        // Passed client side validation //
        showLoadingBar.run() // show loading bar
        // Create a new Booking instance that will hold all data of the booking.
        val booking = buildBooking(user, date, selectedParking)
        // add QR code text to the booking object
        booking.qrCode = generateQRCodeText(booking)
        bookParkingLot(booking, hideLoadingBar, displayToast)
    }

    /**
     * Get the [Date] object of the user's selected string-represented date.
     */
    private val date: Date?
        get() = try {
            io.github.cchristou3.CyParking.utils.DateTimeUtility.fromStringToDate(pickedDate)
        } catch (e: ParseException) {
            null
        }

    /**
     * Get the QRCode text that corresponds to the given booking.
     *
     * @param booking the booking that contains all the user's input.
     */
    private fun generateQRCodeText(booking: Booking): String {
        return try {
            hex(EncryptionManager().encrypt(booking.toString()))
        } catch (ignore: Exception) {
            // used Exception for brevity as there are too many kinds of encryption related exceptions
            ""
        }
    }

    /**
     * Check whether:
     * - the user is logged in
     * - the selected parking lot that the user wants to make a booking
     * has available spaces.
     * - the user has selected a payment method
     * - the string-typed date can be parsed into a [Date] object.
     *
     * @param user the current instance of [LoggedInUser].
     * @param selectedParking the parking lot the user wants to issue a booking for.
     * @return True, if all of the conditions above are true. Otherwise, false.
     */
    private fun validateData(user: LoggedInUser?, selectedParking: ParkingLot, displayToast: Consumer<Int>): Boolean {
        if (user == null) return false // If not logged in, exit the method
        getValidationError(selectedParking.availableSpaces)?.let {
            displayToast.accept(it)
            return false
        }
        return true
    }

    /**
     * Check for potential validation errors.
     *
     * @return True, if there are no available spaces or if the user did not
     * selected a payment method, or if the given date cannot be parsed into a string (should never be the case).
     */
    private fun getValidationError(availableSpaces: Int): Int? {
        // Check whether there are available spaces
        if (availableSpaces == 0) return R.string.no_space_msg
        // Check whether the user picked a payment method
        if (paymentMethod.value == null) return R.string.no_payment_method_selected
        if (date == null) return R.string.parse_error_msg

        if (pickedStartingTime != null) {
            if (isInValidTime(date, pickedStartingTime!!.hour, pickedStartingTime!!.minute))
                return R.string.invalid_time;
        }
        return null
    }

    /**
     * Check whether the given booking already exist in the database.
     * If not, then trigger a payment flow.
     * Otherwise, display an appropriate message.
     *
     * @param booking A [Booking] object containing all the user's input.
     * @param hideLoadingBar a [Runnable] responsible for hiding the Ui's loading bar.
     */
    private fun bookParkingLot(
            booking: Booking, hideLoadingBar: Runnable, displayToast: Consumer<Int>
    ) {
        mBookingRepository.checkIfAlreadyBookedBySameUser(booking)
                // Attach an onComplete listener to handle the task's result
                .addOnCompleteListener { task: Task<Boolean> ->
                    if (task.isSuccessful && task.exception == null) {
                        val wasAlreadyBooked = task.result
                        if (wasAlreadyBooked) {
                            onFlowFailure(hideLoadingBar, displayToast)
                            return@addOnCompleteListener
                        }

                        // Passed server side validation - It does not already exist in the DB //
                        // Set appropriate handler for the payment
                        mPaymentSessionHelper.setPaymentHandler(object : PaymentSessionHelper.PaymentHandler {
                            override fun onPaymentSuccess() {
                                onPaymentSuccess(booking, hideLoadingBar)
                            }

                            override fun onPaymentFailure(error: Int) {
                                onFlowFailure(hideLoadingBar, displayToast)
                            }
                        })
                        // Trigger payment flow
                        this.mShouldStartPaymentFlow.value = true

                        return@addOnCompleteListener
                    }
                    // Otherwise, display an error message to the user
                    onFlowFailure(hideLoadingBar, displayToast)
                }
    }

    /**
     * Invoked when an error occurred concerning the payment intent's
     * creation.
     * Displays a message to the user, re-enables the 'book' button
     * and hides the loading bar.
     *
     * @param hideLoadingBar a [Runnable] responsible for hiding the Ui's loading bar.
     */
    fun onFlowFailure(hideLoadingBar: Runnable, displayToast: Consumer<Int>) {
        displayToast.accept(R.string.slot_already_booked)
        mBookingButtonState.value = true
        hideLoadingBar.run() // hide loading bar
    }

    /**
     * Invoked when a payment intent was successfully created and its client secret
     * is accessible.
     * Stores the given booking in te database and triggers Ui updates.
     *
     * @param booking the issued booking.
     * @param hideLoadingBar a [Runnable] responsible for hiding the Ui's loading bar.
     */
    fun onPaymentSuccess(booking: Booking, hideLoadingBar: Runnable) {
        // When the payment flow is successful
        // Then
        // Store the booking in the database
        mBookingRepository.storeToDatabase(booking)
                .addOnSuccessListener {
                    // Booking was successfully charged and stored.
                    hideLoadingBar.run() // hide loading bar

                    // Save the value of the QR Code message in the ViewModel
                    qRCodeMessage = booking.qrCode

                    // Display undo option
                    updateSnackBarState(booking.generateDocumentId())

                    // Disable any further Ui input
                    mIsBookingCompleted.value = true

                    // Display the 'View QR Code' button
                    updateQRCodeButtonState(true)

                    // Disable the 'book' button
                    updateBookingButtonState(false)
                }
    }

    /**
     * Confirms the payment intent's charge.
     *
     * @param fragment the hosted fragment.
     * @param currentUser the current [LoggedInUser] object.
     * @param lot the lot that the booking will be issued for.
     * @param currency the currency of the payment.
     */
    fun confirmPayment(fragment: Fragment, currentUser: LoggedInUser?, lot: ParkingLot, currency: String) {
        mPaymentSessionHelper.confirmPayment(fragment, currentUser?.userId, lot.generateDocumentId(),
                Utility.indexOf(lot.slotOfferList, slotOffer), currency)
    }

    /**
     * Deletes the specified document using the document ID
     * and re-enables the UI for user input.
     *
     * @param idOfBookingToBeCancelled The id of the document which we want to delete
     */
    fun cancelBooking(idOfBookingToBeCancelled: String) {
        mBookingRepository.cancelParkingBooking(idOfBookingToBeCancelled)
        mIsBookingCompleted.value = false
        mBookingButtonState.value = true
        mQRCodeButtonState.value = false
    }

    /**
     * Gather all information needed to create a Booking instance
     * based on the user's input and return it.
     *
     * @param user       The current LoggedInUser instance.
     * @param pickedDate The current date.
     * @return A Booking instance.
     */
    @Contract("_, _, _ -> new")
    private fun buildBooking(user: LoggedInUser?, pickedDate: Date?, selectedParking: ParkingLot): Booking {
        val userID = user?.userId
        return Booking(
                selectedParking.parkingId,
                selectedParking.operatorId,
                selectedParking.lotName,
                userID,
                BookingDetails(
                        pickedDate,
                        pickedStartingTime,
                        slotOffer
                )
        )
    }

    /**
     * This method should be called in [Fragment.onActivityResult]
     * to handle the payment method data chosen in
     * [presentPaymentMethodSelection].
     */
    fun handlePaymentData(requestCode: Int, resultCode: Int, data: Intent) {
        mPaymentSessionHelper.handlePaymentData(requestCode, resultCode, data)
    }

    /**
     * Presents the payment selection flow to the user.
     */
    fun presentPaymentMethodSelection() {
        mPaymentSessionHelper.presentPaymentMethodSelection()
    }

    /**
     * Set up the [mPaymentSessionHelper]'s payment session.
     */
    fun setUpPaymentSession(fragment: Fragment) {
        mPaymentSessionHelper.setUpPaymentSession(fragment)
    }

    /**
     * Update the value of appropriate livedata if the booking
     * is not completed.
     *
     * @param item the selected slot offer.
     */
    fun handleSelectedItem(item: SlotOffer) {
        isBookingCompleted.value?.let { isCompleted ->
            if (!isCompleted) {
                updateSlotOffer(item)
                // and display the booking button
                updateBookingButtonState(true)
            }
        }
    }
}