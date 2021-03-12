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
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewbinding.ViewBinding;

import com.bumptech.glide.Glide;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Currency;
import java.util.List;
import java.util.Locale;

import io.github.cchristou3.CyParking.R;
import io.github.cchristou3.CyParking.apiClient.model.parking.Parking;
import io.github.cchristou3.CyParking.apiClient.model.parking.lot.ParkingLot;
import io.github.cchristou3.CyParking.apiClient.model.parking.lot.SlotOffer;
import io.github.cchristou3.CyParking.data.interfaces.LocationHandler;
import io.github.cchristou3.CyParking.data.interfaces.Navigable;
import io.github.cchristou3.CyParking.data.manager.location.LocationManager;
import io.github.cchristou3.CyParking.data.manager.location.SingleUpdateHelper;
import io.github.cchristou3.CyParking.databinding.RegisterLotFragmentBinding;
import io.github.cchristou3.CyParking.ui.components.BaseFragment;
import io.github.cchristou3.CyParking.ui.components.BaseItemTouchHelper;
import io.github.cchristou3.CyParking.ui.helper.AlertBuilder;
import io.github.cchristou3.CyParking.ui.helper.DropDownMenuHelper;
import io.github.cchristou3.CyParking.ui.views.home.HomeFragment;
import io.github.cchristou3.CyParking.ui.views.user.account.AccountFragment;
import io.github.cchristou3.CyParking.utils.Utility;
import io.github.cchristou3.CyParking.utils.ViewUtility;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;

/**
 * Purpose: Allow the operator-typed user to register
 * their Parking Lot to the application's system.
 * <p>
 *
 * @author Charalambos Christou
 * @version 8.0 27/02/21
 */
public class RegisterLotFragment extends BaseFragment<RegisterLotFragmentBinding>
        implements Navigable, LocationHandler, TextWatcher, View.OnClickListener {

    // Fragment's constants
    public static final String TAG = RegisterLotFragment.class.getName() + "UniqueTag";
    private static final int EXTERNAL_STORAGE_CODE = 400;
    private static final int RC_PHOTO_PICKER = 730;

    // Fragment's members
    private RegisterLotViewModel mRegisterLotViewModel;
    private Float mSelectedDuration = null;
    private Float mSelectedPrice = null;
    private SingleUpdateHelper mLocationManager;
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
     * @see BaseFragment#onCreateView(ViewBinding)
     */
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return super.onCreateView(RegisterLotFragmentBinding.inflate(inflater));
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
        slotOfferCounter = mRegisterLotViewModel.getSlotOfferList().size();
        // TODO: 27/02/2021 Move to a more appropriate position
        if (ActivityCompat.checkSelfPermission(
                requireContext(), android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
        ) {
            requestPermissions(new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE}, EXTERNAL_STORAGE_CODE);
        } else {
            // no need to ask for permission
            // Do something... fetch position
        }
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
        if (mLocationManager != null) {
            mLocationManager.onRequestPermissionsResult(requireContext(), requestCode, grantResults);
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
                getBinding().registerLotFragmentEtPhoneBody,
                getBinding().registerLotFragmentEtLotName,
                getBinding().registerLotFragmentEtCapacity,
                getBinding().registerLotFragmentEtLocationLat,
                getBinding().registerLotFragmentEtLocationLng
        );
        // Remove On item selected listeners
        ((AutoCompleteTextView) getBinding().registerLotFragmentSDuration.getEditText())
                .setOnItemSelectedListener(null);
        ((AutoCompleteTextView) getBinding().registerLotFragmentSPrice.getEditText())
                .setOnItemSelectedListener(null);
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
        if (requestCode == RC_PHOTO_PICKER) {
            if (resultCode == RESULT_OK) {
                if (data != null) {
                    mRegisterLotViewModel.updateImageUri(
                            data.getData() // uri of selected image
                    );
                }
            } else if (resultCode == RESULT_CANCELED) {
                Toast.makeText(requireContext(), "A photo was not picked.", Toast.LENGTH_SHORT).show();
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
            String message = "Item removed!";
            if (slotOffers.size() > slotOfferCounter) { // If the size got increased since last update
                // then an item was added. Otherwise, an item got removed.
                message = "Item added!";
            }
            slotOfferCounter = slotOffers.size();
            // Display a message to the user
            Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show();
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
                getBinding().registerLotFragmentCvPhoto.setStrokeColor(getResources().getColor(R.color.red));
                getBinding().registerLotFragmentTvPickPhoto.setTextColor(getResources().getColor(R.color.red));
            } else {
                getBinding().registerLotFragmentCvPhoto.setStrokeColor(getResources().getColor(R.color.purple_700));
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

        getBinding().registerLotFragmentTxtPrice.setText("Price (" + Currency.getInstance(Locale.getDefault()).getCurrencyCode() + ")");

        // Set the user's current email
        getBinding().registerLotFragmentTxtEmail.setText(
                getUser().getEmail()
        );

        // Set up both spinners
        setUpSpinner(getBinding().registerLotFragmentSDuration, this::setSelectedDuration, 1.0f);
        setUpSpinner(getBinding().registerLotFragmentSPrice, this::setSelectedPrice, 0.5f);

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
                .setOnClickListener(v -> openPhotoGallery());
    }

    /**
     * Open up the device photo gallery.
     */
    private void openPhotoGallery() {
        // Create an intent for accessing the device's content (gallery)
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
        getBinding().registerLotFragmentBtnAdd.setOnClickListener(v -> {
            // Add to the adapter's list
            List<SlotOffer> newSlotOfferList = mRegisterLotViewModel.getSlotOfferList();
            if (newSlotOfferList == null) {
                newSlotOfferList = new ArrayList<>();
            } else {
                newSlotOfferList = Utility.cloneList(newSlotOfferList);
            }
            SlotOffer newSlotOffer = new SlotOffer(mSelectedDuration, mSelectedPrice);

            if (Utility.contains(newSlotOfferList, newSlotOffer)) {
                Toast.makeText(requireContext(), "This offer already exists on the list.", Toast.LENGTH_SHORT).show();
                // TODO: 24/01/2021 Animate color to that item
                return;
            }
            newSlotOfferList.add(newSlotOffer);
            mRegisterLotViewModel.updateSlotOfferList(newSlotOfferList);
        });
    }

    /**
     * Attaches an {@link android.view.View.OnClickListener} to the
     * 'get location' button.
     * onClick: initializes the {@link #mLocationManager} if not already
     * initialized and requests the user's location.
     *
     * @see SingleUpdateHelper#prepareCallback()
     */
    private void prepareGetLocationButton() {
        // Hook up a listener to the "get location" button
        getBinding().registerLotFragmentMbtnGetLocation.setOnClickListener(v -> {
            Toast.makeText(requireContext(), "Location retrieved!", Toast.LENGTH_SHORT).show();
            // Initialize the SingleLocationManager object
            if (mLocationManager == null) {
                mLocationManager = LocationManager.createSingleUpdateHelper(requireContext(), this);
                Log.d(TAG, "SingleUpdateHelper initialized");
            } else {
                Log.d(TAG, "SingleUpdateHelper prepareCallback");
                mLocationManager.prepareCallback();
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
        try {
            return new ParkingLot(
                    new Parking.Coordinates(
                            fromViewTextToDouble(getBinding().registerLotFragmentEtLocationLat),
                            fromViewTextToDouble(getBinding().registerLotFragmentEtLocationLng)
                    ),  // coordinates
                    getBinding().registerLotFragmentEtLotName.getText().toString(), // lotName
                    getUser().getUserId(), // operatorId
                    getBinding().registerLotFragmentEtPhoneBody.getNonSpacedText(), // operatorMobileNumber
                    Integer.parseInt(getBinding().registerLotFragmentEtCapacity.getText().toString()), // capacity,
                    null, //
                    mRegisterLotViewModel.getSlotOfferList() // slotOfferList
            );
        } catch (NullPointerException exception) {
            return null;
        }
    }

    /**
     * Extracts the numeric text of the given argument
     * and converts it to a double.
     *
     * @param editText The view to extract the text from.
     * @return A double representation of the numeric text.
     */
    private double fromViewTextToDouble(@NotNull EditText editText) {
        return Double.parseDouble(editText.getText().toString());
    }

    /**
     * Invokes {@link RegisterLotViewModel#lotRegistrationDataChanged(String, Integer, String, LatLng, List)}
     * with the current input.
     */
    private void triggerViewModelUpdate() {
        // Check the value of the lot capacity EditText to avoid exceptions
        final Integer lotCapacity = getBinding().registerLotFragmentEtCapacity.getText().toString().length() == 0 ? 0 :
                Integer.parseInt(getBinding().registerLotFragmentEtCapacity.getText().toString());

        LatLng lotLatLng = null;
        // Check the value of the location EditTexts to avoid exceptions
        if (!getBinding().registerLotFragmentEtLocationLat.getText().toString().isEmpty()
                && !getBinding().registerLotFragmentEtLocationLng.getText().toString().isEmpty()) {
            lotLatLng = new LatLng(
                    Double.parseDouble(getBinding().registerLotFragmentEtLocationLat.getText().toString()),
                    Double.parseDouble(getBinding().registerLotFragmentEtLocationLng.getText().toString())
            );
        }
        // Check the value of the Phone EditText to avoid exceptions
        final String phoneNumber = getBinding().registerLotFragmentEtPhoneBody.getText() != null ?
                getBinding().registerLotFragmentEtPhoneBody.getText().toString().replace(" ", "") : "";

        mRegisterLotViewModel.lotRegistrationDataChanged(
                phoneNumber,
                lotCapacity,
                getBinding().registerLotFragmentEtLotName.getText().toString(),
                lotLatLng,
                mRegisterLotViewModel.getSlotOfferList()
        );
    }

    /**
     * Based on the given view find the {@link Spinner} with the given spinnerId
     * and initialize its values. Also, hook it up with an {@link AdapterView.OnItemSelectedListener}.
     * Whenever the listener gets triggered, set the value of the current spinner to the value of the
     * fragment's corresponding data member ({@link #mSelectedDuration}/{@link #mSelectedPrice}).
     *
     * @param textInputLayout    A reference of the spinner to be set up.
     * @param settable           The interface's method to act as a callback inside the listener.
     * @param volumeMultiplicand A float determining the sequence of values of the spinner.
     */
    private void setUpSpinner(@NotNull TextInputLayout textInputLayout, Settable settable, float volumeMultiplicand) {
        // Create an array that will hold all the values of the spinner, based on a multiplicand
        final String[] volume = Utility.getVolume(volumeMultiplicand, 1, 10);
        DropDownMenuHelper.setUpSlotOfferDropDownMenu(requireContext(), textInputLayout, volume,
                new DropDownMenuHelper.ItemHandler<String>() {
                    @Override
                    public String castItem(@NotNull ListAdapter parent, int position) {
                        return parent.getItem(position).toString();
                    }

                    @Override
                    public void onItemSelected(String item) {
                        // Convert the spinner's value into a float and pass it in, to the settable's method.
                        settable.setVolume(Float.parseFloat(item));
                    }
                });
    }

    /**
     * Setter for {@link #mSelectedDuration}
     * Used to substitute {@link Settable#setVolume(float)}
     * in {@link #setUpSpinner(TextInputLayout, Settable, float)}
     *
     * @param mSelectedDuration The latest selected duration of our duration spinner.
     */
    public void setSelectedDuration(float mSelectedDuration) {
        this.mSelectedDuration = mSelectedDuration;
        mRegisterLotViewModel.updateSelectedSlotOfferArguments(mSelectedDuration, mSelectedPrice);
    }

    /**
     * Setter for {@link #mSelectedPrice}
     * Used to substitute {@link Settable#setVolume(float)}
     * in {@link #setUpSpinner(TextInputLayout, Settable, float)}
     *
     * @param mSelectedPrice The latest selected price of our price spinner.
     */
    public void setSelectedPrice(float mSelectedPrice) {
        this.mSelectedPrice = mSelectedPrice;
        mRegisterLotViewModel.updateSelectedSlotOfferArguments(mSelectedDuration, mSelectedPrice);
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
        mRegisterLotViewModel.registerParkingLot(lotToBeRegistered)
                .addOnCompleteListener((Task<Boolean> task) -> {
                    getGlobalStateViewModel().hideLoadingBar();
                    if (task.isSuccessful()) {
                        boolean wasRegistrationSuccessful = task.getResult();
                        if (wasRegistrationSuccessful) {
                            // Display message to user.
                            Toast.makeText(RegisterLotFragment.this.requireContext(),
                                    getString(R.string.success_lot_registration), Toast.LENGTH_SHORT).show();
                            // Navigate back to home screen
                            getNavController(requireActivity())
                                    .popBackStack();
                        } else {
                            // Display error message to user that the parking lot already exists
                            Toast.makeText(RegisterLotFragment.this.requireContext(),
                                    getString(R.string.error_lot_already_exists), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
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
     * @see LocationManager#requestUserLocationUpdates(Fragment)
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
            Toast.makeText(requireContext(), getString(R.string.error_retrieving_location), Toast.LENGTH_SHORT).show();
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

    /**
     * Interface used in {@link #setUpSpinner(TextInputLayout, Settable, float)}.
     */
    private interface Settable {
        void setVolume(float selectedVolume);
    }
}