package io.github.cchristou3.CyParking.ui.views.parking.lots.register;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.core.app.ActivityCompat;
import androidx.core.util.Consumer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewbinding.ViewBinding;

import com.bumptech.glide.Glide;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.material.textfield.TextInputLayout;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.List;

import io.github.cchristou3.CyParking.R;
import io.github.cchristou3.CyParking.apiClient.model.data.parking.Parking;
import io.github.cchristou3.CyParking.apiClient.model.data.parking.lot.ParkingLot;
import io.github.cchristou3.CyParking.apiClient.model.data.parking.lot.SlotOffer;
import io.github.cchristou3.CyParking.data.interfaces.LocationHandler;
import io.github.cchristou3.CyParking.data.interfaces.Navigable;
import io.github.cchristou3.CyParking.data.manager.location.LocationManager;
import io.github.cchristou3.CyParking.data.manager.location.SingleUpdateHelper;
import io.github.cchristou3.CyParking.databinding.RegisterLotFragmentBinding;
import io.github.cchristou3.CyParking.ui.components.BaseFragment;
import io.github.cchristou3.CyParking.ui.components.BaseItemTouchHelper;
import io.github.cchristou3.CyParking.ui.components.LocationServiceViewModel;
import io.github.cchristou3.CyParking.ui.helper.AlertBuilder;
import io.github.cchristou3.CyParking.ui.helper.DropDownMenuHelper;
import io.github.cchristou3.CyParking.ui.views.home.HomeFragment;
import io.github.cchristou3.CyParking.ui.views.user.account.AccountFragment;
import io.github.cchristou3.CyParking.utils.Utility;
import io.github.cchristou3.CyParking.utils.ViewUtility;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;
import static io.github.cchristou3.CyParking.utils.Utility.isPermissionGranted;
import static io.github.cchristou3.CyParking.utils.ViewUtility.getStringOrEmpty;

/**
 * Purpose: Allow the operator-typed user to register
 * their Parking Lot to the application's system.
 * <p>
 *
 * @author Charalambos Christou
 * @version 12.0 27/03/21
 */
public class RegisterLotFragment extends BaseFragment<RegisterLotFragmentBinding>
        implements Navigable, LocationHandler, TextWatcher, View.OnClickListener {

    // Fragment's constants
    public static final String TAG = RegisterLotFragment.class.getName() + "UniqueTag";
    private static final int RC_EXTERNAL_STORAGE_CODE = 400;
    private static final int RC_PHOTO_PICKER = 730;

    // Fragment's members
    private RegisterLotViewModel mRegisterLotViewModel;
    private SingleUpdateHelper<RegisterLotFragment, RegisterLotFragmentBinding> mLocationManager;
    private SlotOfferAdapter mSlotOfferAdapter;
    private int slotOfferCounter;
    private int mTextColour;

    /**
     * Called to do initial creation of a fragment.
     *
     * @param savedInstanceState If the fragment is being re-created from
     *                           a previous saved state, this is the state.
     */
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initializeViewModel();
    }

    /**
     * Called to have the fragment instantiate its user interface view.
     *
     * @see BaseFragment#onCreateView(ViewBinding, int)
     */
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return super.onCreateView(RegisterLotFragmentBinding.inflate(inflater), R.string.register_lot_label);
    }

    /**
     * Called immediately after {@link #onCreateView(LayoutInflater, ViewGroup, Bundle)}
     * has returned, but before any saved state has been restored in to the view.
     * <p>
     * Initialize the fragment ViewModel and sets up the UI's:
     * <p> - Spinners with {@link ArrayAdapter}
     * <p> - Buttons with {@link View.OnClickListener}
     * <p> - RecyclerView with a {@link SlotOfferAdapter}
     *
     * @param view               The View returned by {@link #onCreateView(LayoutInflater, ViewGroup, Bundle)}.
     * @param savedInstanceState If non-null, this fragment is being re-constructed
     */
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        addObserversToStates();
        initializeUi();
        // Keep track of the slot offer list's size
        slotOfferCounter = mRegisterLotViewModel.getSlotOfferList().size();
    }

    /**
     * Gets invoked after the user has been asked for a permission for a given package.
     * If permission was granted, request for the user's latest known location.
     *
     * @param requestCode  The code of the user's request.
     * @param permissions  The permission that were asked.
     * @param grantResults The results of the user's response.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NotNull String[] permissions, @NotNull int[] grantResults) {
        switch (requestCode) {
            case RC_EXTERNAL_STORAGE_CODE:
                if (isPermissionGranted(grantResults)) openPhotoGallery();
                else
                    getGlobalStateViewModel().updateToastMessage(R.string.permission_not_granted);
                break;
            case LocationManager.RC_LOCATION_PERMISSION:
                if (mLocationManager != null) {
                    mLocationManager.onRequestPermissionsResult(this, requestCode, grantResults);
                }
                break;
        }
    }


    /**
     * Called when the view previously created by {@link #onCreateView} has
     * been detached from the fragment.
     *
     * @see BaseFragment#onDestroyView()
     */
    @Override
    public void onDestroyView() {
        // Remove OnClickListeners
        super.removeOnClickListeners(
                getBinding().registerLotFragmentBtnRegisterLot,
                getBinding().registerLotFragmentBtnAdd,
                getBinding().registerLotFragmentMbtnGetLocation,
                getBinding().registerLotFragmentIvPickPhoto
        );
        // Remove TextWatchers
        super.removeTextWatchers(
                this,
                getBinding().registerLotFragmentEtPhoneBody,
                getBinding().registerLotFragmentEtLotName,
                getBinding().registerLotFragmentEtCapacity,
                getBinding().registerLotFragmentEtLocationLat,
                getBinding().registerLotFragmentEtLocationLng
        );
        // Remove On item selected listeners
        DropDownMenuHelper.cleanUp(getBinding().registerLotFragmentSDuration);
        DropDownMenuHelper.cleanUp(getBinding().registerLotFragmentSPrice);
        super.onDestroyView();
    }

    /**
     * Receive the result from a previous call to
     * {@link #startActivityForResult(Intent, int)}.
     *
     * @param requestCode The integer request code originally supplied to
     *                    startActivityForResult(), allowing you to identify who this
     *                    result came from.
     * @param resultCode  The integer result code returned by the child activity
     *                    through its setResult().
     * @param data        An Intent, which can return result data to the caller
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (mLocationManager != null)
            mLocationManager.onActivityResult(this, requestCode, resultCode, data);

        if (requestCode == RC_PHOTO_PICKER) {
            if (resultCode == RESULT_OK) {
                if (data != null) {
                    mRegisterLotViewModel.updateImageUri(
                            data.getData() // uri of selected image
                    );
                }
            } else if (resultCode == RESULT_CANCELED) {
                getGlobalStateViewModel().updateToastMessage(R.string.photo_not_picked);
            }
        }
    }

    /**
     * Add observers to the user's state, the registration form
     * and the slot offer list.
     */
    private void addObserversToStates() {
        addObserverToUserState();
        addObserverToSlotOfferList();
        addObserverToPickedPhoto();
        addObserverToForm();
        addObserverToSlotOfferArguments();
        addObserverToNavigatingBack();
        addObserverToLocationServicesError();
    }

    /**
     * Observer for when a location service failure occurs.
     */
    private void addObserverToLocationServicesError() {
        LocationServiceViewModel
                .addObserverToLocationServicesError(mRegisterLotViewModel, this);
    }

    /**
     * Observe for when the user should be navigated to the previous screen.
     */
    private void addObserverToNavigatingBack() {
        mRegisterLotViewModel.getNavigateBackState().observe(getViewLifecycleOwner(),
                timeToNavigateBack -> goBack(requireActivity()));
    }

    /**
     * Observe the state of the image Uri.
     * Once picked, display it.
     */
    private void addObserverToPickedPhoto() {
        mRegisterLotViewModel.getImageUriState().observe(getViewLifecycleOwner(), uri -> {
            if (uri != null) {
                // Hide the hint text
                getBinding().registerLotFragmentTvPickPhoto.setVisibility(View.GONE);
                displayPickedPhoto(uri);
            }
        });
    }

    /**
     * Displays the given Image Uri in an ImageView.
     *
     * @param selectedImageUri The Uri of an image.
     */
    private void displayPickedPhoto(Uri selectedImageUri) {
        Glide.with(this).asBitmap().load(selectedImageUri)
                .override(
                        // take 70% (0.7f) of the parent's size
                        (int) (getBinding().registerLotFragmentClMainCl.getMeasuredWidth() * 0.7f),
                        (int) (getBinding().registerLotFragmentClMainCl.getMeasuredWidth() * 0.7f)
                )
                .into(getBinding().registerLotFragmentIvPhoto);
    }

    /**
     * Initialize the fragment's {{@link #mRegisterLotViewModel} instance.
     */
    private void initializeViewModel() {
        // Initialize the fragment's ViewModel instance
        mRegisterLotViewModel = new ViewModelProvider(this,
                new RegisterLotViewModelFactory()).get(RegisterLotViewModel.class);
    }

    /**
     * Observes the slot offer list's state.
     * Whenever, an updated version of the list is received
     * - the {@link #mSlotOfferAdapter}'s items are updated
     * - the fragment scrolls down to its bottom
     * - the {@link #mRegisterLotViewModel}'s livedata objects get updated
     * - a Toast message is shown to the user
     */
    private void addObserverToSlotOfferList() {
        mRegisterLotViewModel.getSlotOfferListState().observe(getViewLifecycleOwner(), slotOffers -> {
            mSlotOfferAdapter.submitList(slotOffers); // Inform the adapter
            if (slotOffers.size() == slotOfferCounter) return;
            Log.d(TAG, "addObserverToSlotOfferList: " + slotOffers);
            // Scroll down-wards to the "register" button
            ViewUtility.scrollTo(getBinding().registerLotFragmentBtnRegisterLot);
            // Update the ViewModel's state
            triggerViewModelUpdate();
            int message = R.string.item_removed;
            if (slotOffers.size() > slotOfferCounter) { // If the size got increased since last update
                // then an item was added. Otherwise, an item got removed.
                message = R.string.item_added;
            }
            slotOfferCounter = slotOffers.size();
            // Display a message to the user
            getGlobalStateViewModel().updateToastMessage(message);
        });
    }

    /**
     * Observer the user's auth state.
     * When the user logs out, he is prompted to either return to previous
     * screen or log in.
     */
    private void addObserverToUserState() {
        observeUserState(loggedInUser -> {
            if (loggedInUser == null) { // User has logged out
                AlertBuilder.promptUserToLogIn(getChildFragmentManager(), requireActivity(), this,
                        R.string.logout_register_lot_screen_msg);
            }
        });
    }

    /**
     * Attaches an observer to the form's state.
     * Whenever, the state of the form changes, the
     * Ui gets updated accordingly.
     */
    private void addObserverToForm() {
        // Add an observer to the ViewModel's RegisterLotFormState
        mRegisterLotViewModel.getRegisterLotFormState().observe(getViewLifecycleOwner(), registerLotFormState -> {
            if (registerLotFormState == null) return;
            getBinding().registerLotFragmentBtnRegisterLot.setEnabled(registerLotFormState.isDataValid());

            // Update the view's related to the lot's info
            if (ViewUtility.updateErrorOf(requireContext(),
                    getBinding().registerLotFragmentTilPhoneBody, registerLotFormState.getMobileNumberError())) {
                return;
            }
            if (ViewUtility.updateErrorOf(requireContext(),
                    getBinding().registerLotFragmentTilLotName, registerLotFormState.getLotNameError())) {
                return;
            }
            if (ViewUtility.updateErrorOf(requireContext(),
                    getBinding().registerLotFragmentTilCapacity, registerLotFormState.getLotCapacityError())) {
                return;
            }
            // Update the view's related to the lot's location
            if (ViewUtility.updateErrorOf(requireContext(),
                    getBinding().registerLotFragmentTilLocationLat, registerLotFormState.getLatLngError())) {
                return;
            }
            if (ViewUtility.updateErrorOf(requireContext(),
                    getBinding().registerLotFragmentTilLocationLng, registerLotFormState.getLatLngError())) {
                return;
            }
            if (registerLotFormState.getPhotoError() != null) {
                getBinding().registerLotFragmentCvPhoto.setStrokeColor(getResources().getColor(R.color.red, requireActivity().getTheme()));
                getBinding().registerLotFragmentTvPickPhoto.setTextColor(getResources().getColor(R.color.red, requireActivity().getTheme()));
            } else {
                getBinding().registerLotFragmentCvPhoto.setStrokeColor(getResources().getColor(R.color.purple_700, requireActivity().getTheme()));
                getBinding().registerLotFragmentTvPickPhoto.setTextColor(mTextColour);
            }
            // Update the view's related to the lot's slot offers
            if (registerLotFormState.getSlotOfferError() != null) {
                // Show the warning
                getBinding().registerLotFragmentTxtSlotOfferWarning.setVisibility(View.VISIBLE);
                getBinding().registerLotFragmentTxtSlotOfferWarning.setText(getString(registerLotFormState.getSlotOfferError()));
                // Connect the disclaimer's top with the warning's bottom
                adjustLayoutConstraints(getBinding().registerLotFragmentTxtSlotOfferWarning.getId());
            } else {
                // Hide the warning
                getBinding().registerLotFragmentTxtSlotOfferWarning.setVisibility(View.GONE);
                // Connect the warning's top with the RecyclerView's bottom
                adjustLayoutConstraints(getBinding().registerLotFragmentRvPriceList.getId());
            }
        });
    }

    /**
     * Observes the user's selection of duration and price.
     * The 'add' button is disabled till the user selects a
     * value for both of them.
     */
    private void addObserverToSlotOfferArguments() {
        mRegisterLotViewModel.getSelectedSlotOfferArgumentsState().observe(getViewLifecycleOwner(), formState -> {
            if (formState == null) return;
            getBinding().registerLotFragmentBtnAdd.setEnabled(formState.isDataValid());
        });
    }

    /**
     * Connects the view's (of id {@link R.id#register_lot_fragment_txt_disclaimer})
     * top with the bottom of the view with the given id.
     *
     * @param viewId The id of the view to has its bottom connected to the disclaimer's top.
     */
    private void adjustLayoutConstraints(int viewId) {
        ConstraintSet constraintSet = new ConstraintSet();
        constraintSet.clone(getBinding().registerLotFragmentClList);
        constraintSet.connect(
                R.id.register_lot_fragment_txt_disclaimer, // Connect the disclaimer's top
                ConstraintSet.TOP,
                viewId, // with this view's bottom
                ConstraintSet.BOTTOM);
        constraintSet.applyTo(getBinding().registerLotFragmentClList);
    }

    /**
     * Initializes the fragment's Ui contents and listeners.
     */
    private void initializeUi() {
        if (getUser() == null) return;

        // Keep track of the text's initial colour
        mTextColour = getBinding().registerLotFragmentTvPickPhoto.getCurrentTextColor();

        // Initially the button is disabled
        getBinding().registerLotFragmentBtnRegisterLot.setEnabled(false);

        getBinding().registerLotFragmentTxtPrice.setText(getString(R.string.price) + " (" + Utility.getCurrency().getSymbol() + ")");

        // Set the user's current email
        getBinding().registerLotFragmentTxtEmail.setText(
                getUser().getEmail()
        );

        // Set up both spinners
        setUpSpinner(getBinding().registerLotFragmentSDuration, mRegisterLotViewModel::updateSelectedDuration, 1.0f);
        // Minimum charge amount: 0.50 cents in euros: https://stripe.com/docs/currencies#minimum-and-maximum-charge-amounts
        setUpSpinner(getBinding().registerLotFragmentSPrice, mRegisterLotViewModel::updateSelectedPrice, 0.5f);

        preparePhotoPickerButton();
        prepareGetLocationButton();
        prepareAddButton();
        setUpRecyclerViewWithAdapter();

        // Hook up a listener to the "Register" button
        getBinding().registerLotFragmentBtnRegisterLot.setOnClickListener(this);

        // Attach a textWatcher to the UI's EditTexts
        getBinding().registerLotFragmentEtPhoneBody.addTextChangedListener(this);
        getBinding().registerLotFragmentEtLotName.addTextChangedListener(this);
        getBinding().registerLotFragmentEtCapacity.addTextChangedListener(this);
        getBinding().registerLotFragmentEtLocationLat.addTextChangedListener(this);
        getBinding().registerLotFragmentEtLocationLng.addTextChangedListener(this);
    }

    /**
     * Hook up the photo picker button with a listener.
     * on-click: Open up the device photo gallery.
     */
    private void preparePhotoPickerButton() {
        getBinding().registerLotFragmentIvPickPhoto
                .setOnClickListener(v -> requestPhotoGallery());
    }

    /**
     * Request from the android framework access to the photo gallery.
     * If permission is not granted, then the user is prompt.
     */
    private void requestPhotoGallery() {
        if (ActivityCompat.checkSelfPermission(
                requireContext(), android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
        ) {
            requestPermissions(new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE}, RC_EXTERNAL_STORAGE_CODE);
        } else {
            // no need to ask for permission
            // Do something... fetch position
            // Create an intent for accessing the device's content (gallery)
            openPhotoGallery();
        }
    }

    /**
     * Open up the device photo gallery.
     */
    private void openPhotoGallery() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/jpeg");
        intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
        startActivityForResult(Intent.createChooser(intent, "Complete action using"), RC_PHOTO_PICKER);
    }

    /**
     * Attaches an {@link android.view.View.OnClickListener} to the
     * 'add' button.
     * onClick: adds the slot offer with the specified arguments onto the list.
     */
    private void prepareAddButton() {
        // Hook up a listener to the "add" button
        getBinding().registerLotFragmentBtnAdd.setOnClickListener(
                v -> mRegisterLotViewModel.addToList(getGlobalStateViewModel()::updateToastMessage));
    }

    /**
     * Attaches an {@link android.view.View.OnClickListener} to the
     * 'get location' button.
     * onClick: initializes the {@link #mLocationManager} if not already
     * initialized and requests the user's location.
     *
     * @see SingleUpdateHelper#prepareCallback(BaseFragment)
     */
    private void prepareGetLocationButton() {
        // Hook up a listener to the "get location" button
        getBinding().registerLotFragmentMbtnGetLocation.setOnClickListener(v -> {
            getGlobalStateViewModel().updateToastMessage(R.string.get_location_with_gps);
            // Initialize the SingleLocationManager object
            if (mLocationManager == null) {
                mLocationManager = LocationManager.createSingleUpdateHelper(this, this, null,
                        mRegisterLotViewModel::postLocationServicesError);
                Log.d(TAG, "SingleUpdateHelper initialized");
            } else {
                Log.d(TAG, "SingleUpdateHelper prepareCallback");
                mLocationManager.prepareCallback(this);
            }
            mLocationManager.requestUserLocationUpdates(this);
        });

    }

    /**
     * Initializes the fragment's RecyclerView and
     * {@link SlotOfferAdapter} instance.
     */
    private void setUpRecyclerViewWithAdapter() {
        // Set up the recycler view
        final RecyclerView recyclerView = getBinding().registerLotFragmentRvPriceList;
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        // Set up the RecyclerView's adapter
        mSlotOfferAdapter = new SlotOfferAdapter(new SlotOffersDiffCallback(), getItemTouchHelper());
        // Bind recyclerView with its adapter
        recyclerView.setAdapter(mSlotOfferAdapter);

        // If recyclerView is inside a ScrollView then there is an issue while scrolling recyclerView’s inner contents.
        // So, when touching the recyclerView forbid the ScrollView from intercepting touch events.
        ViewUtility.disableParentScrollingInterferenceOf(recyclerView);
    }

    /**
     * Returns an instance of {@link ItemTouchHelper}.
     * onSwipeLeft: Remove the item from the list
     * and notify the adapter.
     *
     * @return An instance of {@link ItemTouchHelper}.
     */
    @NotNull
    @Contract(" -> new")
    private ItemTouchHelper getItemTouchHelper() {
        return new ItemTouchHelper(new BaseItemTouchHelper(
                itemPosition -> {
                    // Access the current list - with a new reference
                    List<SlotOffer> newOffers = Utility.cloneList(mRegisterLotViewModel.getSlotOfferList());
                    // Remove the booking fro the list
                    newOffers.remove((int) itemPosition); // Cast to primitive to trigger appropriate method
                    // Update the adapter's list
                    mRegisterLotViewModel.updateSlotOfferList(newOffers);
                }, getResources(), R.id.slot_offer_item__cv
        ));
    }

    /**
     * Gather all inputted information into a single ParkingLot object
     * and return it.
     *
     * @return A ParkingLot object containing all necessary info of an operator's
     * lot.
     */
    @Nullable
    private ParkingLot buildParkingLot() {
        // Instantiate the ParkingLot object
        // and return it
        return (getUser() == null) ? null :
                new ParkingLot(
                        new Parking.Coordinates(
                                extractDoubleValue(getBinding().registerLotFragmentEtLocationLat),
                                extractDoubleValue(getBinding().registerLotFragmentEtLocationLng)
                        ),  // coordinates
                        getStringOrEmpty(getBinding().registerLotFragmentEtLotName), // lotName
                        getUser().getUserId(), // operatorId
                        getBinding().registerLotFragmentEtPhoneBody.getNonSpacedText(), // operatorMobileNumber
                        Integer.parseInt(getStringOrEmpty(getBinding().registerLotFragmentEtCapacity)), // capacity,
                        null, // to be found at a later stage
                        mRegisterLotViewModel.getSlotOfferList() // slotOfferList
                );
    }

    /**
     * Extracts the numeric text of the given argument
     * and converts it to a double.
     *
     * @param editText The view to extract the text from.
     * @return A double representation of the numeric text.
     */
    private double extractDoubleValue(@NotNull EditText editText) {
        return Double.parseDouble(getStringOrEmpty(editText));
    }

    /**
     * Invokes {@link RegisterLotViewModel#lotRegistrationDataChanged(String, Integer, String, LatLng, List)}
     * with the current input.
     */
    private void triggerViewModelUpdate() {
        // Check the value of the lot capacity EditText to avoid exceptions
        final Integer lotCapacity = getStringOrEmpty(getBinding().registerLotFragmentEtCapacity).length() == 0 ? 0 :
                Integer.parseInt(
                        getStringOrEmpty(getBinding().registerLotFragmentEtCapacity));

        LatLng lotLatLng = null;
        // Check the value of the location EditTexts to avoid exceptions
        if (!getStringOrEmpty(getBinding().registerLotFragmentEtLocationLat).isEmpty()
                && !getStringOrEmpty(getBinding().registerLotFragmentEtLocationLng).isEmpty()) {
            lotLatLng = new LatLng(
                    extractDoubleValue(getBinding().registerLotFragmentEtLocationLat),
                    extractDoubleValue(getBinding().registerLotFragmentEtLocationLng)
            );
        }
        // Check the value of the Phone EditText to avoid exceptions
        final String phoneNumber = getBinding().registerLotFragmentEtPhoneBody.getText() != null ?
                getBinding().registerLotFragmentEtPhoneBody.getText().toString().replace(" ", "") : "";

        mRegisterLotViewModel.lotRegistrationDataChanged(
                phoneNumber,
                lotCapacity,
                getStringOrEmpty(getBinding().registerLotFragmentEtLotName),
                lotLatLng,
                mRegisterLotViewModel.getSlotOfferList()
        );
    }

    /**
     * Based on the given view find the {@link Spinner} with the given spinnerId
     * and initialize its values. Also, hook it up with an {@link AdapterView.OnItemSelectedListener}.
     * Whenever the listener gets triggered, set the value of the current spinner to the value of the
     * {@link #mRegisterLotViewModel}'s corresponding LiveData member
     * ({@link RegisterLotViewModel#updateSelectedDuration(Float)}/{@link RegisterLotViewModel#updateSelectedPrice(Float)}).
     *
     * @param textInputLayout    A reference of the spinner to be set up.
     * @param consumer           The interface's method to act as a callback inside the listener.
     * @param volumeMultiplicand A float determining the sequence of values of the spinner.
     */
    private void setUpSpinner(@NotNull TextInputLayout textInputLayout, Consumer<Float> consumer, float volumeMultiplicand) {
        // Create an array that will hold all the values of the spinner, based on a multiplicand
        final String[] volume = Utility.getVolume(volumeMultiplicand, 1, 10);
        DropDownMenuHelper.setUpSlotOfferDropDownMenu(requireContext(), textInputLayout, volume,
                new DropDownMenuHelper.ItemHandler<String>() {
                    @NotNull
                    @Override
                    public String onOutput(String item) {
                        return item;
                    }

                    @NotNull
                    @Override
                    public String castItem(@NotNull ListAdapter parent, int position) {
                        return parent.getItem(position).toString();
                    }

                    @Override
                    public void onItemSelected(@NotNull String item) {
                        // Convert the spinner's value into a float and pass it in, to the consumer's method.
                        consumer.accept(Float.parseFloat(item));
                    }
                });
    }

    /**
     * Called when the "register" button has been clicked.
     *
     * @param v The view that was clicked.
     */
    @Override
    public void onClick(View v) {
        // Create a ParkingLot object to hold all necessary info.
        ParkingLot lotToBeRegistered = buildParkingLot();
        if (lotToBeRegistered == null) return;
        // User is not logged in
        // Callback added on addObserverToUserState should kick in.
        // Otherwise, finish registration
        getGlobalStateViewModel().showLoadingBar();
        mRegisterLotViewModel.registerParkingLot(lotToBeRegistered,
                () -> getGlobalStateViewModel().hideLoadingBar(),
                getGlobalStateViewModel()::updateToastMessage);

    }

    /**
     * Unused TextWatcher methods.
     */
    public void beforeTextChanged(CharSequence s, int start, int count, int after) { /* ignore */ }

    public void onTextChanged(CharSequence s, int start, int before, int count) { /* ignore */ }

    /**
     * Triggers an update of the fragment's ViewModel instance.
     */
    @Override
    public void afterTextChanged(@NotNull Editable s) {
        triggerViewModelUpdate();
    }

    /**
     * Callback invoked when the user's location is received.
     *
     * @param locationResult The result of the user's requested location.
     * @see LocationManager#requestUserLocationUpdates(BaseFragment)
     */
    @Override
    public void onLocationResult(LocationResult locationResult) {
        Log.d(TAG, "onLocationResult: From Register");
        if (locationResult != null) {
            // Access the latest location
            Location currentLocation = locationResult.getLastLocation();
            // Set the Lat and Lng editTexts' text with the retrieved location's values.
            getBinding().registerLotFragmentEtLocationLat.setText(String.valueOf(currentLocation.getLatitude()));
            getBinding().registerLotFragmentEtLocationLng.setText(String.valueOf(currentLocation.getLongitude()));
            ViewUtility.hideKeyboard(requireActivity(), requireView());
        } else {
            // Inform the user something wrong happened
            getGlobalStateViewModel().updateToastMessage(R.string.error_retrieving_location);
        }
    }

    /**
     * Navigates from the current Fragment subclass to the
     * {@link io.github.cchristou3.CyParking.ui.views.user.login.AuthenticatorFragment}.
     */
    @Override
    public void toAuthenticator() {
        getNavController(requireActivity())
                .navigate(
                        RegisterLotFragmentDirections
                                .actionNavRegisterLotFragmentToNavAuthenticatorFragment()
                );
    }

    /**
     * Navigates from the current Fragment subclass to the
     * {@link io.github.cchristou3.CyParking.ui.views.parking.slots.viewBooking.ViewBookingsFragment}.
     */
    @Override
    public void toBookings() {
        getNavController(requireActivity())
                .navigate(
                        RegisterLotFragmentDirections.actionNavRegisterLotFragmentToNavViewBookings()
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
                        RegisterLotFragmentDirections.actionNavRegisterLotFragmentToNavAccount()
                );
    }

    /**
     * Navigates from the current Fragment subclass to the
     * {@link io.github.cchristou3.CyParking.ui.views.user.feedback.FeedbackFragment}.
     */
    @Override
    public void toFeedback() {
        getNavController(requireActivity())
                .navigate(
                        RegisterLotFragmentDirections.actionNavRegisterLotFragmentToNavFeedback()
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
                        RegisterLotFragmentDirections.actionNavRegisterLotFragmentToNavHome()
                );
    }
}