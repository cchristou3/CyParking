package io.github.cchristou3.CyParking.ui.views.parking.lots.register

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ListAdapter
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.app.ActivityCompat
import androidx.core.util.Consumer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.google.android.gms.maps.model.LatLng
import com.google.android.material.textfield.TextInputLayout
import io.github.cchristou3.CyParking.R
import io.github.cchristou3.CyParking.apiClient.model.data.parking.Parking.Coordinates
import io.github.cchristou3.CyParking.apiClient.model.data.parking.lot.ParkingLot
import io.github.cchristou3.CyParking.apiClient.model.data.parking.lot.SlotOffer
import io.github.cchristou3.CyParking.apiClient.model.data.user.LoggedInUser
import io.github.cchristou3.CyParking.data.interfaces.Navigable
import io.github.cchristou3.CyParking.data.pojo.form.FormState
import io.github.cchristou3.CyParking.data.pojo.form.operator.RegisterLotFormState
import io.github.cchristou3.CyParking.databinding.RegisterLotFragmentBinding
import io.github.cchristou3.CyParking.ui.components.BaseFragment
import io.github.cchristou3.CyParking.ui.components.BaseItemTouchHelper
import io.github.cchristou3.CyParking.ui.components.LocationFragment
import io.github.cchristou3.CyParking.ui.helper.AlertBuilder
import io.github.cchristou3.CyParking.ui.helper.DropDownMenuHelper
import io.github.cchristou3.CyParking.ui.helper.DropDownMenuHelper.Companion.cleanUp
import io.github.cchristou3.CyParking.ui.helper.DropDownMenuHelper.Companion.setUpSlotOfferDropDownMenu
import io.github.cchristou3.CyParking.utils.*
import mumayank.com.airlocationlibrary.AirLocation
import org.jetbrains.annotations.Contract

/**
 * Purpose: Allow the operator-typed user to register
 * their Parking Lot to the application's system.
 *
 *
 *
 * @author Charalambos Christou
 * @version 12.0 27/03/21
 */
class RegisterLotFragment : LocationFragment<RegisterLotFragmentBinding>(), Navigable, TextWatcher, View.OnClickListener {
    // Fragment's members
    private var mRegisterLotViewModel: RegisterLotViewModel? = null
    private var mSlotOfferAdapter: SlotOfferAdapter? = null
    private var slotOfferCounter = 0
    private var mTextColour = 0

    /**
     * Called to do initial creation of a fragment.
     *
     * @param savedInstanceState If the fragment is being re-created from
     * a previous saved state, this is the state.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initializeViewModel()
    }

    /**
     * Called to have the fragment instantiate its user interface view.
     *
     * @see BaseFragment.onCreateView
     */
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return super.onCreateView(RegisterLotFragmentBinding.inflate(inflater), R.string.register_lot_label)
    }

    /**
     * Called immediately after [.onCreateView]
     * has returned, but before any saved state has been restored in to the view.
     *
     *
     * Initialize the fragment ViewModel and sets up the UI's:
     *
     *  - Spinners with [ArrayAdapter]
     *
     *  - Buttons with [View.OnClickListener]
     *
     *  - RecyclerView with a [SlotOfferAdapter]
     *
     * @param view               The View returned by [.onCreateView].
     * @param savedInstanceState If non-null, this fragment is being re-constructed
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        addObserversToStates()
        initializeUi()
        // Keep track of the slot offer list's size
        slotOfferCounter = mRegisterLotViewModel!!.slotOfferList.size
    }

    /**
     * Gets invoked after the user has been asked for a permission for a given package.
     * If permission was granted, request for the user's latest known location.
     *
     * @param requestCode  The code of the user's request.
     * @param permissions  The permission that were asked.
     * @param grantResults The results of the user's response.
     */
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            RC_EXTERNAL_STORAGE_CODE -> if (Utility.isPermissionGranted(grantResults)) openPhotoGallery() else globalStateViewModel.updateToastMessage(R.string.permission_not_granted)
        }
    }

    /**
     * Called when the view previously created by [.onCreateView] has
     * been detached from the fragment.
     *
     * @see BaseFragment.onDestroyView
     */
    override fun onDestroyView() {
        // Remove OnClickListeners
        super.removeOnClickListeners(
                binding!!.registerLotFragmentBtnRegisterLot,
                binding!!.registerLotFragmentBtnAdd,
                binding!!.registerLotFragmentMbtnGetLocation,
                binding!!.registerLotFragmentIvPickPhoto
        )
        // Remove TextWatchers
        super.removeTextWatchers(
                this,
                binding!!.registerLotFragmentEtPhoneBody,
                binding!!.registerLotFragmentEtLotName,
                binding!!.registerLotFragmentEtCapacity,
                binding!!.registerLotFragmentEtLocationLat,
                binding!!.registerLotFragmentEtLocationLng
        )
        // Remove On item selected listeners
        cleanUp(binding!!.registerLotFragmentSDuration)
        cleanUp(binding!!.registerLotFragmentSPrice)
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
        if (requestCode == RC_PHOTO_PICKER) {
            if (resultCode == Activity.RESULT_OK) {
                if (data != null) {
                    mRegisterLotViewModel!!.updateImageUri(
                            data.data // uri of selected image
                    )
                }
            } else if (resultCode == Activity.RESULT_CANCELED) {
                globalStateViewModel.updateToastMessage(R.string.photo_not_picked)
            }
        }
    }

    /**
     * Add observers to the user's state, the registration form
     * and the slot offer list.
     */
    private fun addObserversToStates() {
        addObserverToUserState()
        addObserverToSlotOfferList()
        addObserverToPickedPhoto()
        addObserverToForm()
        addObserverToSlotOfferArguments()
        addObserverToNavigatingBack()
    }

    /**
     * Observe for when the user should be navigated to the previous screen.
     */
    private fun addObserverToNavigatingBack() {
        mRegisterLotViewModel!!.navigateBackState.observe(viewLifecycleOwner,
                { goBack(requireActivity()) })
    }

    /**
     * Observe the state of the image Uri.
     * Once picked, display it.
     */
    private fun addObserverToPickedPhoto() {
        mRegisterLotViewModel!!.imageUriState.observe(viewLifecycleOwner, { uri: Uri? ->
            uri?.let {  // Hide the hint text
                binding!!.registerLotFragmentTvPickPhoto.visibility = View.GONE
                displayPickedPhoto(it)
            }
        })
    }

    /**
     * Displays the given Image Uri in an ImageView.
     *
     * @param selectedImageUri The Uri of an image.
     */
    private fun displayPickedPhoto(selectedImageUri: Uri) {
        Glide.with(this).asBitmap().load(selectedImageUri)
                .override( // take 70% (0.7f) of the parent's size
                        (binding!!.registerLotFragmentClMainCl.measuredWidth * 0.7f).toInt(),
                        (binding!!.registerLotFragmentClMainCl.measuredWidth * 0.7f).toInt()
                )
                .into(binding!!.registerLotFragmentIvPhoto)
    }

    /**
     * Initialize the fragment's [.mRegisterLotViewModel] instance.
     */
    private fun initializeViewModel() {
        // Initialize the fragment's ViewModel instance
        mRegisterLotViewModel = ViewModelProvider(this,
                RegisterLotViewModelFactory()).get(RegisterLotViewModel::class.java)
    }

    /**
     * Observes the slot offer list's state.
     * Whenever, an updated version of the list is received
     * - the [.mSlotOfferAdapter]'s items are updated
     * - the fragment scrolls down to its bottom
     * - the [.mRegisterLotViewModel]'s livedata objects get updated
     * - a Toast message is shown to the user
     */
    private fun addObserverToSlotOfferList() {
        mRegisterLotViewModel!!.slotOfferListState.observe(viewLifecycleOwner, { slotOffers: List<SlotOffer?> ->
            mSlotOfferAdapter!!.submitList(slotOffers) // Inform the adapter
            if (slotOffers.size == slotOfferCounter) return@observe
            Log.d(TAG, "addObserverToSlotOfferList: $slotOffers")
            // Scroll down-wards to the "register" button
            scrollTo(binding!!.registerLotFragmentBtnRegisterLot)
            // Update the ViewModel's state
            triggerViewModelUpdate()
            var message = R.string.item_removed
            if (slotOffers.size > slotOfferCounter) { // If the size got increased since last update
                // then an item was added. Otherwise, an item got removed.
                message = R.string.item_added
            }
            slotOfferCounter = slotOffers.size
            // Display a message to the user
            globalStateViewModel.updateToastMessage(message)
        })
    }

    /**
     * Observer the user's auth state.
     * When the user logs out, he is prompted to either return to previous
     * screen or log in.
     */
    private fun addObserverToUserState() {
        observeUserState { loggedInUser: LoggedInUser? ->
            if (loggedInUser == null) { // User has logged out
                AlertBuilder.promptUserToLogIn(childFragmentManager, requireActivity(), this,
                        R.string.logout_register_lot_screen_msg)
            }
        }
    }

    /**
     * Attaches an observer to the form's state.
     * Whenever, the state of the form changes, the
     * Ui gets updated accordingly.
     */
    private fun addObserverToForm() {
        // Add an observer to the ViewModel's RegisterLotFormState
        mRegisterLotViewModel!!.registerLotFormState.observe(viewLifecycleOwner, { registerLotFormState: RegisterLotFormState? ->
            if (registerLotFormState == null) return@observe
            binding!!.registerLotFragmentBtnRegisterLot.isEnabled = registerLotFormState.isDataValid

            // Update the view's related to the lot's info
            if (updateErrorOf(requireContext(),
                            binding!!.registerLotFragmentTilPhoneBody, registerLotFormState.mobileNumberError)) {
                return@observe
            }
            if (updateErrorOf(requireContext(),
                            binding!!.registerLotFragmentTilLotName, registerLotFormState.lotNameError)) {
                return@observe
            }
            if (updateErrorOf(requireContext(),
                            binding!!.registerLotFragmentTilCapacity, registerLotFormState.lotCapacityError)) {
                return@observe
            }
            // Update the view's related to the lot's location
            if (updateErrorOf(requireContext(),
                            binding!!.registerLotFragmentTilLocationLat, registerLotFormState.latLngError)) {
                return@observe
            }
            if (updateErrorOf(requireContext(),
                            binding!!.registerLotFragmentTilLocationLng, registerLotFormState.latLngError)) {
                return@observe
            }
            if (registerLotFormState.photoError != null) {
                binding!!.registerLotFragmentCvPhoto.strokeColor = resources.getColor(R.color.red, requireActivity().theme)
                binding!!.registerLotFragmentTvPickPhoto.setTextColor(resources.getColor(R.color.red, requireActivity().theme))
            } else {
                binding!!.registerLotFragmentCvPhoto.strokeColor = resources.getColor(R.color.purple_700, requireActivity().theme)
                binding!!.registerLotFragmentTvPickPhoto.setTextColor(mTextColour)
            }
            // Update the view's related to the lot's slot offers
            if (registerLotFormState.slotOfferError != null) {
                // Show the warning
                binding!!.registerLotFragmentTxtSlotOfferWarning.visibility = View.VISIBLE
                binding!!.registerLotFragmentTxtSlotOfferWarning.text = getString(registerLotFormState.slotOfferError!!)
                // Connect the disclaimer's top with the warning's bottom
                adjustLayoutConstraints(binding!!.registerLotFragmentTxtSlotOfferWarning.id)
            } else {
                // Hide the warning
                binding!!.registerLotFragmentTxtSlotOfferWarning.visibility = View.GONE
                // Connect the warning's top with the RecyclerView's bottom
                adjustLayoutConstraints(binding!!.registerLotFragmentRvPriceList.id)
            }
        })
    }

    /**
     * Observes the user's selection of duration and price.
     * The 'add' button is disabled till the user selects a
     * value for both of them.
     */
    private fun addObserverToSlotOfferArguments() {
        mRegisterLotViewModel!!.selectedSlotOfferArgumentsState.observe(viewLifecycleOwner, { formState: FormState? ->
            if (formState == null) return@observe
            binding!!.registerLotFragmentBtnAdd.isEnabled = formState.isDataValid
        })
    }

    /**
     * Connects the view's (of id [R.id.register_lot_fragment_txt_disclaimer])
     * top with the bottom of the view with the given id.
     *
     * @param viewId The id of the view to has its bottom connected to the disclaimer's top.
     */
    private fun adjustLayoutConstraints(viewId: Int) {
        val constraintSet = ConstraintSet()
        constraintSet.clone(binding!!.registerLotFragmentClList)
        constraintSet.connect(
                R.id.register_lot_fragment_txt_disclaimer,  // Connect the disclaimer's top
                ConstraintSet.TOP,
                viewId,  // with this view's bottom
                ConstraintSet.BOTTOM)
        constraintSet.applyTo(binding!!.registerLotFragmentClList)
    }

    /**
     * Initializes the fragment's Ui contents and listeners.
     */
    private fun initializeUi() {
        if (user == null) return

        // Keep track of the text's initial colour
        mTextColour = binding!!.registerLotFragmentTvPickPhoto.currentTextColor

        // Initially the button is disabled
        binding!!.registerLotFragmentBtnRegisterLot.isEnabled = false
        binding!!.registerLotFragmentTxtPrice.text = getString(R.string.price) + " (" + Utility.getCurrency().symbol + ")"

        // Set the user's current email
        binding!!.registerLotFragmentTxtEmail.text = user!!.email

        // Set up both spinners
        setUpSpinner(binding!!.registerLotFragmentSDuration, { selectedDuration: Float? -> mRegisterLotViewModel!!.updateSelectedDuration(selectedDuration) }, 1.0f)
        // Minimum charge amount: 0.50 cents in euros: https://stripe.com/docs/currencies#minimum-and-maximum-charge-amounts
        setUpSpinner(binding!!.registerLotFragmentSPrice, { selectedPrice: Float? -> mRegisterLotViewModel!!.updateSelectedPrice(selectedPrice) }, 0.5f)
        preparePhotoPickerButton()
        prepareGetLocationButton()
        prepareAddButton()
        setUpRecyclerViewWithAdapter()

        // Hook up a listener to the "Register" button
        binding!!.registerLotFragmentBtnRegisterLot.setOnClickListener(this)

        // Attach a textWatcher to the UI's EditTexts
        binding!!.registerLotFragmentEtPhoneBody.addTextChangedListener(this)
        binding!!.registerLotFragmentEtLotName.addTextChangedListener(this)
        binding!!.registerLotFragmentEtCapacity.addTextChangedListener(this)
        binding!!.registerLotFragmentEtLocationLat.addTextChangedListener(this)
        binding!!.registerLotFragmentEtLocationLng.addTextChangedListener(this)
    }

    /**
     * Hook up the photo picker button with a listener.
     * on-click: Open up the device photo gallery.
     */
    private fun preparePhotoPickerButton() {
        binding!!.registerLotFragmentIvPickPhoto
                .setOnClickListener { v: View? -> requestPhotoGallery() }
    }

    /**
     * Request from the android framework access to the photo gallery.
     * If permission is not granted, then the user is prompt.
     */
    private fun requestPhotoGallery() {
        if (ActivityCompat.checkSelfPermission(
                        requireContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), RC_EXTERNAL_STORAGE_CODE)
        } else {
            // no need to ask for permission
            // Do something... fetch position
            // Create an intent for accessing the device's content (gallery)
            openPhotoGallery()
        }
    }

    /**
     * Open up the device photo gallery.
     */
    private fun openPhotoGallery() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "image/jpeg"
        intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true)
        startActivityForResult(Intent.createChooser(intent, "Complete action using"), RC_PHOTO_PICKER)
    }

    /**
     * Attaches an [android.view.View.OnClickListener] to the
     * 'add' button.
     * onClick: adds the slot offer with the specified arguments onto the list.
     */
    private fun prepareAddButton() {
        // Hook up a listener to the "add" button
        binding!!.registerLotFragmentBtnAdd.setOnClickListener { v: View? -> mRegisterLotViewModel!!.addToList { message: Int? -> globalStateViewModel.updateToastMessage(message!!) } }
    }

    /**
     * Attaches an [android.view.View.OnClickListener] to the
     * 'get location' button.
     * onClick: requests the user's location.
     */
    private fun prepareGetLocationButton() {
        // Hook up a listener to the "get location" button
        binding!!.registerLotFragmentMbtnGetLocation.setOnClickListener { v: View? ->
            globalStateViewModel.updateToastMessage(R.string.get_location_with_gps)
            startLocationUpdates()
        }
    }

    /**
     * Initializes the fragment's RecyclerView and
     * [SlotOfferAdapter] instance.
     */
    private fun setUpRecyclerViewWithAdapter() {
        // Set up the recycler view
        val recyclerView = binding!!.registerLotFragmentRvPriceList
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.itemAnimator = DefaultItemAnimator()

        // Set up the RecyclerView's adapter
        mSlotOfferAdapter = SlotOfferAdapter(SlotOffersDiffCallback(), itemTouchHelper)
        // Bind recyclerView with its adapter
        recyclerView.adapter = mSlotOfferAdapter

        // If recyclerView is inside a ScrollView then there is an issue while scrolling recyclerView’s inner contents.
        // So, when touching the recyclerView forbid the ScrollView from intercepting touch events.
        disableParentScrollingInterferenceOf(recyclerView)
    }// Access the current list - with a new reference
    // Remove the booking fro the list
    // Cast to primitive to trigger appropriate method
    // Update the adapter's list
    /**
     * Returns an instance of [ItemTouchHelper].
     * onSwipeLeft: Remove the item from the list
     * and notify the adapter.
     *
     * @return An instance of [ItemTouchHelper].
     */
    @get:Contract(" -> new")
    private val itemTouchHelper: ItemTouchHelper
        private get() = ItemTouchHelper(BaseItemTouchHelper(
                { itemPosition: Int ->
                    // Access the current list - with a new reference
                    val newOffers = Utility.cloneList(mRegisterLotViewModel!!.slotOfferList)
                    // Remove the booking fro the list
                    newOffers.removeAt(itemPosition) // Cast to primitive to trigger appropriate method
                    // Update the adapter's list
                    mRegisterLotViewModel!!.updateSlotOfferList(newOffers)
                }, resources, R.id.slot_offer_item__cv
        ))

    /**
     * Gather all inputted information into a single ParkingLot object
     * and return it.
     *
     * @return A ParkingLot object containing all necessary info of an operator's
     * lot.
     */
    private fun buildParkingLot(): ParkingLot? {
        // Instantiate the ParkingLot object
        // and return it
        return if (user == null) null else ParkingLot(
                Coordinates(
                        extractDoubleValue(binding!!.registerLotFragmentEtLocationLat),
                        extractDoubleValue(binding!!.registerLotFragmentEtLocationLng)
                ),  // coordinates
                getStringOrEmpty(binding!!.registerLotFragmentEtLotName),  // lotName
                user!!.userId,  // operatorId
                binding!!.registerLotFragmentEtPhoneBody.nonSpacedText, getStringOrEmpty(binding!!.registerLotFragmentEtCapacity).toInt(),  // capacity,
                null,  // to be found at a later stage
                mRegisterLotViewModel!!.slotOfferList // slotOfferList
        )
    }

    /**
     * Extracts the numeric text of the given argument
     * and converts it to a double.
     *
     * @param editText The view to extract the text from.
     * @return A double representation of the numeric text.
     */
    private fun extractDoubleValue(editText: EditText): Double {
        return getStringOrEmpty(editText).toDouble()
    }

    /**
     * Invokes [RegisterLotViewModel.lotRegistrationDataChanged]
     * with the current input.
     */
    private fun triggerViewModelUpdate() {
        // Check the value of the lot capacity EditText to avoid exceptions
        val lotCapacity = if (getStringOrEmpty(binding!!.registerLotFragmentEtCapacity).length == 0) 0 else
            getStringOrEmpty(binding!!.registerLotFragmentEtCapacity).toInt()
        var lotLatLng: LatLng? = null
        // Check the value of the location EditTexts to avoid exceptions
        if (!getStringOrEmpty(binding!!.registerLotFragmentEtLocationLat).isEmpty()
                && !getStringOrEmpty(binding!!.registerLotFragmentEtLocationLng).isEmpty()) {
            lotLatLng = LatLng(
                    extractDoubleValue(binding!!.registerLotFragmentEtLocationLat),
                    extractDoubleValue(binding!!.registerLotFragmentEtLocationLng)
            )
        }
        // Check the value of the Phone EditText to avoid exceptions
        val phoneNumber = if (binding!!.registerLotFragmentEtPhoneBody.text != null) binding!!.registerLotFragmentEtPhoneBody.text.toString().replace(" ", "") else ""
        mRegisterLotViewModel!!.lotRegistrationDataChanged(
                phoneNumber,
                lotCapacity,
                getStringOrEmpty(binding!!.registerLotFragmentEtLotName),
                lotLatLng,
                mRegisterLotViewModel!!.slotOfferList
        )
    }

    /**
     * Based on the given view find the [Spinner] with the given spinnerId
     * and initialize its values. Also, hook it up with an [AdapterView.OnItemSelectedListener].
     * Whenever the listener gets triggered, set the value of the current spinner to the value of the
     * [.mRegisterLotViewModel]'s corresponding LiveData member
     * ([RegisterLotViewModel.updateSelectedDuration]/[RegisterLotViewModel.updateSelectedPrice]).
     *
     * @param textInputLayout    A reference of the spinner to be set up.
     * @param consumer           The interface's method to act as a callback inside the listener.
     * @param volumeMultiplicand A float determining the sequence of values of the spinner.
     */
    private fun setUpSpinner(textInputLayout: TextInputLayout, consumer: Consumer<Float>, volumeMultiplicand: Float) {
        // Create an array that will hold all the values of the spinner, based on a multiplicand
        val volume = Utility.getVolume(volumeMultiplicand, 1, 10)
        setUpSlotOfferDropDownMenu<String>(requireContext(), textInputLayout, volume,
                object : DropDownMenuHelper.ItemHandler<String> {
                    override fun onOutput(item: String): String {
                        return item
                    }

                    override fun castItem(parent: ListAdapter, position: Int): String {
                        return parent.getItem(position).toString()
                    }

                    override fun onItemSelected(item: String) {
                        // Convert the spinner's value into a float and pass it in, to the consumer's method.
                        consumer.accept(item.toFloat())
                    }
                })
    }

    /**
     * Called when the "register" button has been clicked.
     *
     * @param v The view that was clicked.
     */
    override fun onClick(v: View) {
        // Create a ParkingLot object to hold all necessary info.
        val lotToBeRegistered = buildParkingLot() ?: return
        // User is not logged in
        // Callback added on addObserverToUserState should kick in.
        // Otherwise, finish registration
        globalStateViewModel.showLoadingBar()
        mRegisterLotViewModel!!.registerParkingLot(lotToBeRegistered,
                { globalStateViewModel.hideLoadingBar() }) { message: Int? -> globalStateViewModel.updateToastMessage(message!!) }
    }

    /**
     * Unused TextWatcher methods.
     */
    override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) { /* ignore */
    }

    override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) { /* ignore */
    }

    /**
     * Triggers an update of the fragment's ViewModel instance.
     */
    override fun afterTextChanged(s: Editable) {
        triggerViewModelUpdate()
    }


    /**
     * Navigates from the current Fragment subclass to the
     * [io.github.cchristou3.CyParking.ui.views.user.login.AuthenticatorFragment].
     */
    override fun toAuthenticator() {
        getNavController(requireActivity())
                .navigate(
                        RegisterLotFragmentDirections
                                .actionNavRegisterLotFragmentToNavAuthenticatorFragment()
                )
    }

    /**
     * Navigates from the current Fragment subclass to the
     * [io.github.cchristou3.CyParking.ui.views.parking.slots.viewBooking.ViewBookingsFragment].
     */
    override fun toBookings() {
        getNavController(requireActivity())
                .navigate(
                        RegisterLotFragmentDirections.actionNavRegisterLotFragmentToNavViewBookings()
                )
    }

    /**
     * Navigates from the current Fragment subclass to the
     * [AccountFragment].
     */
    override fun toAccount() {
        getNavController(requireActivity())
                .navigate(
                        RegisterLotFragmentDirections.actionNavRegisterLotFragmentToNavAccount()
                )
    }

    /**
     * Navigates from the current Fragment subclass to the
     * [io.github.cchristou3.CyParking.ui.views.user.feedback.FeedbackFragment].
     */
    override fun toFeedback() {
        getNavController(requireActivity())
                .navigate(
                        RegisterLotFragmentDirections.actionNavRegisterLotFragmentToNavFeedback()
                )
    }

    /**
     * Navigates from the current Fragment subclass to the
     * [HomeFragment].
     */
    override fun toHome() {
        getNavController(requireActivity())
                .navigate(
                        RegisterLotFragmentDirections.actionNavRegisterLotFragmentToNavHome()
                )
    }

    companion object {
        // Fragment's constants
        val TAG = RegisterLotFragment::class.java.name + "UniqueTag"
        private const val RC_EXTERNAL_STORAGE_CODE = 400
        private const val RC_PHOTO_PICKER = 730
    }

    /**
     * Returns an instance of [AirLocation] that contains
     * information about the location retrieval behaviour.
     * @return An instance of [AirLocation].
     */
    override fun initializeLocationApi(): AirLocation = AirLocation(this.requireActivity(), object : AirLocation.Callback {

        override fun onSuccess(locations: ArrayList<Location>) {
            if (!locations.isNullOrEmpty()) {
                // Set the Lat and Lng editTexts' text with the retrieved location's values.
                binding!!.registerLotFragmentEtLocationLat.setText(locations[0].latitude.toString())
                binding!!.registerLotFragmentEtLocationLng.setText(locations[0].longitude.toString())
                hideKeyboard(requireActivity(), requireView())
            }
        }

        override fun onFailure(locationFailedEnum: AirLocation.LocationFailedEnum) {
            // the reason for failure is given in locationFailedEnum

            // TODO: Show message based on the locationFailedEnum
            globalStateViewModel.updateToastMessage(R.string.error_retrieving_location)
        }
    }, true)
}