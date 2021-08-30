package io.github.cchristou3.CyParking.ui.views.operator

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot
import com.google.zxing.integration.android.IntentIntegrator
import io.github.cchristou3.CyParking.R
import io.github.cchristou3.CyParking.apiClient.model.data.parking.lot.ParkingLot
import io.github.cchristou3.CyParking.apiClient.model.data.user.LoggedInUser
import io.github.cchristou3.CyParking.data.interfaces.Navigable
import io.github.cchristou3.CyParking.data.manager.DatabaseObserver
import io.github.cchristou3.CyParking.databinding.FragmentOperatorBinding
import io.github.cchristou3.CyParking.ui.components.BaseFragment
import io.github.cchristou3.CyParking.ui.components.NavigatorFragment
import io.github.cchristou3.CyParking.ui.helper.AlertBuilder
import io.github.cchristou3.CyParking.ui.views.home.*
import io.github.cchristou3.CyParking.utilities.show
import io.github.cchristou3.CyParking.utilities.slideBottom
import io.github.cchristou3.CyParking.utilities.slideTop
import io.github.cchristou3.CyParking.utils.checkAndRemoveOnClickListener
import io.github.cchristou3.CyParking.utils.checkAndSetOnClickListener
import io.github.cchristou3.CyParking.utils.updateViewVisibilityTo

/**
 * A simple [Fragment] subclass.
 * Use the [OperatorFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class OperatorFragment : NavigatorFragment<FragmentOperatorBinding>() {

    // Members related to the Operator
    private val mOperatorViewModel: OperatorViewModel by lazy {
        ViewModelProvider(this,
                OperatorViewModelFactory()).get(OperatorViewModel::class.java)
    }
    private var mDatabaseObserver: DatabaseObserver<Query, QuerySnapshot>? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return super.onCreateView(FragmentOperatorBinding.inflate(inflater), R.string.my_parking_lot)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Listen to the user state
        observeUserState { loggedInUser: LoggedInUser? -> updateUi(loggedInUser) }
    }

    private fun updateUi(loggedInUser: LoggedInUser?) {
        if (loggedInUser == null) {
            // If operator logged out, remove observer to its parking lot
            mDatabaseObserver?.unregisterLifecycleObserver()
            AlertBuilder
                    .promptUserToLogIn(childFragmentManager, activity, this, R.string.not_logged_in_account_2)
            return
        }
        // Initialize UI here
        // Add listeners
        initializeOperator(loggedInUser)
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
        mOperatorViewModel.handleQRCodeScannerContents( // Access the qr code's payload
                IntentIntegrator.parseActivityResult(requestCode, resultCode, data),  // Access the previously stored lot reference
                mIntentIntegrator.moreExtras[LOT_REFERENCE_KEY] as DocumentReference?) { message: Int? -> globalStateViewModel.updateToastMessage(message!!) }
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
                binding!!.fragmentOperatorMbtnRegisterParkingLot,
                binding!!.fragmentOperatorMbtnRegisterParkingLot,
                binding!!.fragmentOperatorBtnIncrement,
                binding!!.fragmentOperatorBtnDecrement
        )
        super.onDestroyView()
    }


    /**
     * Return the fragment's [.mIntentIntegrator].
     * Lazy initialization is used.
     *
     * @return the fragment's [.mIntentIntegrator].
     */
    private val mIntentIntegrator: IntentIntegrator by lazy {
        IntentIntegrator
                .forSupportFragment(this@OperatorFragment)
                .setDesiredBarcodeFormats(IntentIntegrator.QR_CODE)
                .setCameraId(0)
                .setPrompt(getString(R.string.qr_code_scanner_bottom_text))
                .setBeepEnabled(true)
                .setBarcodeImageEnabled(true)
                .setCaptureActivity(PortraitCaptureActivity::class.java)
    }

    /**
     * Initializes both the Ui and the database logic related to the operator.
     *
     * @param loggedInUser current user.
     */
    private fun initializeOperator(loggedInUser: LoggedInUser) {
        // Attach observer to update the view's parking lot info whenever it changes
        mOperatorViewModel.parkingLotState.observe(viewLifecycleOwner, { updateLotContents(it) }) // Display the parking lot's contents

        // Get the operator's lot info from the database.
        getParkingLotInfo(loggedInUser.userId)

        binding.fragmentOperatorBtnToSlotOffers.checkAndSetOnClickListener {
            if (mOperatorViewModel.parkingLotState.value == null) {
                globalStateViewModel.updateToastMessage(R.string.in_flight_mode_message)
                return@checkAndSetOnClickListener
            }
            navigateToSlotOffers()
        }

        // TODO: 10/02/2021 Display QR scanner button: scan QR code of users that have booked a slot.
    }

    private fun navigateToSlotOffers() {
        if (mOperatorViewModel.parkingLotState.value == null) return

        navigateTo(
                OperatorFragmentDirections
                        .actionNavOperatorToNavSlotOffers(
                                mOperatorViewModel.parkingLotState.value!!.slotOfferList.toTypedArray(),
                                mOperatorViewModel.parkingLotState.value!!.generateDocumentId()
                        )
        )
    }

    /**
     * Fetch the user's parking lot and observe for any of its updates.
     * On initial and consecutive data loads the Ui related to the
     * operator's lot is updated accordingly.
     *
     * @param operatorId The id of the operator.
     */
    private fun getParkingLotInfo(operatorId: String) {
        mOperatorViewModel.observeParkingLot(operatorId).addSnapshotListener(requireActivity()) { value: QuerySnapshot?, error: FirebaseFirestoreException? ->
            if (binding == null) return@addSnapshotListener
            if (error != null || value == null || value.isEmpty) { // The operator did not register lot yet
                displayLotRegistrationLayout()
                return@addSnapshotListener
            }

            // Remove listeners from the register lot button button
            binding!!.fragmentOperatorMbtnRegisterParkingLot.checkAndRemoveOnClickListener()

            // If the operator has registered a lot already, display its info
            val userParkingLot = value.documents[0].toObject(ParkingLot::class.java)
                    ?: return@addSnapshotListener
            if (user != null && userParkingLot.operatorId != user!!.userId) return@addSnapshotListener

            // Get a reference to the document
            val ref = value.documents[0].reference

            // Hook up buttons with listeners
            setUpOperatorButtons(ref, userParkingLot)

            binding!!.fragmentOperatorClRegisterLotInfo.visibility = View.VISIBLE
            // Trigger parking lot update.
            mOperatorViewModel.updateLotState(userParkingLot)
        }
    }

    /**
     * Hook up the `scan booking` button with an on click listener.
     * onclick: initialize the QR Code scanner.
     *
     * @param lotRef A document reference to the operator's parking lot.
     */
    private fun setUpScanBookingButton(lotRef: DocumentReference?) {
        binding!!.fragmentOperatorBtnScanBooking.checkAndSetOnClickListener {
            // Only available to operators
            mIntentIntegrator
                    .addExtra(LOT_REFERENCE_KEY, lotRef)
                    .initiateScan()
        }
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
        binding!!.fragmentOperatorTxtLotCapacity.text = userParkingLot.getLotAvailability(requireContext())
        binding!!.fragmentOperatorTxtLotName.text = String.format(getString(R.string.lot_name), userParkingLot.lotName)
    }


    /**
     * Displays a view giving the operator the option
     * to register a parking lot.
     */
    private fun displayLotRegistrationLayout() {
        checkVisibilityOfAppropriateLayout(View.GONE, View.VISIBLE)
        // Attach listener to "Register Parking lot" button
        binding!!.fragmentOperatorMbtnRegisterParkingLot
                .setOnClickListener { // Navigate to the parking lot registration form
                    getNavController(requireActivity())
                            .navigate(
                                    OperatorFragmentDirections.actionNavOperatorFragmentToNavRegisterLotFragment()
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
        updateViewVisibilityTo(binding!!.fragmentOperatorClShowLotInfo, lotInfoVisibility) // showLotInfo layout
        updateViewVisibilityTo(binding!!.fragmentOperatorClRegisterLotInfo, registerLotVisibility) // registerLotInfo layout
        if (lotInfoVisibility == View.VISIBLE) { // Operator has a lot
            // Display the QR Code scanner to be used to scan bookings via animation
            slideBottom(binding!!.root,
                    binding!!.fragmentOperatorCvLotInfo, false, TRANSITION_DURATION)
            /* next animation */
            {
                slideTop(
                        binding!!.root, binding!!.fragmentOperatorBtnScanBooking, false, TRANSITION_DURATION
                )
                {
                    slideBottom(binding!!.root,
                            binding!!.fragmentOperatorBtnToSlotOffers, false, TRANSITION_DURATION, null)
                }
            }
        } else { // Only show the lot info card view
            show(binding, binding!!.fragmentOperatorCvLotInfo, TRANSITION_DURATION)
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
        binding.fragmentOperatorBtnIncrement.setOnClickListener {
            // If the lot has available spaces decrease its value by one
            // E.g. 40/40 -> do nothing
            if (userParkingLot.availableSpaces > 0) {
                mOperatorViewModel.incrementPersonCount(ref)
            }
        }
        binding.fragmentOperatorBtnDecrement.setOnClickListener {
            // If the lot has the same number of available spaces as its capacity do nothing.
            // E.g. 0/40 -> do nothing
            if (userParkingLot.availableSpaces < userParkingLot.capacity) {
                // Otherwise, increase its value by one.
                mOperatorViewModel.decrementPersonCount(ref)
            }
        }
    }

    companion object {
        // Fragment variables
        private const val LOT_REFERENCE_KEY = "ref"
        private const val TRANSITION_DURATION = 650L
    }

    /**
     * Navigates from the current Fragment subclass to the
     * [io.github.cchristou3.CyParking.ui.views.user.login.AuthenticatorFragment].
     */
    override fun toAuthenticator() {
        navigateTo(OperatorFragmentDirections.actionNavOperatorFragmentToNavAuthenticatorFragment())
    }

    /**
     * Navigates from the current Fragment subclass to the
     * [io.github.cchristou3.CyParking.ui.views.parking.slots.viewBooking.ViewBookingsFragment].
     */
    override fun toBookings() {
        navigateTo(OperatorFragmentDirections.actionNavOperatorFragmentToNavViewBookings())
    }

    /**
     * Navigates from the current Fragment subclass to the
     * [AccountFragment].
     */
    override fun toAccount() {
        navigateTo(OperatorFragmentDirections.actionNavOperatorFragmentToNavAccount())
    }

    /**
     * Navigates from the current Fragment subclass to the
     * [io.github.cchristou3.CyParking.ui.views.user.feedback.FeedbackFragment].
     */
    override fun toFeedback() {
        navigateTo(OperatorFragmentDirections.actionNavOperatorFragmentToNavFeedback())
    }

    /**
     * Navigates from the current Fragment subclass to the
     * [HomeFragment].
     */
    override fun toHome() = goBack()

}