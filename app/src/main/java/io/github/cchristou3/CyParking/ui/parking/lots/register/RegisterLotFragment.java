package io.github.cchristou3.CyParking.ui.parking.lots.register;

import android.location.Location;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.location.LocationResult;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.Task;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import io.github.cchristou3.CyParking.R;
import io.github.cchristou3.CyParking.data.interfaces.LocationHandler;
import io.github.cchristou3.CyParking.data.interfaces.Navigable;
import io.github.cchristou3.CyParking.data.manager.LocationManager;
import io.github.cchristou3.CyParking.data.model.parking.lot.ParkingLot;
import io.github.cchristou3.CyParking.data.model.parking.lot.SlotOffer;
import io.github.cchristou3.CyParking.data.model.parking.slot.Parking;
import io.github.cchristou3.CyParking.databinding.RegisterLotFragmentBinding;
import io.github.cchristou3.CyParking.ui.home.HomeFragment;
import io.github.cchristou3.CyParking.ui.host.AuthStateViewModel;
import io.github.cchristou3.CyParking.utilities.Utility;
import io.github.cchristou3.CyParking.utilities.ViewUtility;

/**
 * Purpose: Allow the operator-typed user to register
 * their Parking Lot to the application's system.
 * <p>
 * TODO: Observe user state
 *
 * @author Charalambos Christou
 * @version 2.0 28/12/20
 */
public class RegisterLotFragment extends Fragment implements Navigable, LocationHandler,
        TextWatcher, View.OnClickListener {

    // Fragment's constants
    public static final String TAG = RegisterLotFragment.class.getName() + "UniqueTag";

    // Fragment's members
    private RegisterLotViewModel mRegisterLotViewModel;
    private AuthStateViewModel mAuthStateViewModel;
    private List<SlotOffer> mSlotOfferList;
    private float mSelectedDuration;
    private float mSelectedPrice;
    private RegisterLotFragmentBinding mRegisterLotFragmentBinding;
    private LocationManager mLocationManager;
    private SlotOfferAdapter mSlotOfferAdapter;

    /**
     * Called to have the fragment instantiate its user interface view.
     */
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        // Create an instance of the binding class for the fragment to use.
        mRegisterLotFragmentBinding = RegisterLotFragmentBinding.inflate(getLayoutInflater());
        // Return the root view from the onCreateView() method to make it the active view on the screen.
        return mRegisterLotFragmentBinding.getRoot();
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

        // Initialize the fragment's ViewModel instance
        mRegisterLotViewModel = new ViewModelProvider(this).get(RegisterLotViewModel.class);

        mAuthStateViewModel = new ViewModelProvider(requireActivity()).get(AuthStateViewModel.class);

        // Initialize the slot offer container
        mSlotOfferList = new ArrayList<>();

        initializeUi();
        addObserverToForm();
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
        mLocationManager.onRequestPermissionsResult(requireContext(), requestCode, grantResults);
    }

    /**
     * Called when the view previously created by {@link #onCreateView} has
     * been detached from the fragment.
     */
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        // Remove OnClickListeners
        getBinding().registerLotFragmentBtnRegisterLot.setOnClickListener(null);
        getBinding().registerLotFragmentBtnAdd.setOnClickListener(null);
        getBinding().registerLotFragmentMbtnGetLocation.setOnClickListener(null);
        SlotOfferAdapter.setOnItemClickListener(null);
        // Remove TextWatchers
        getBinding().registerLotFragmentEtPhoneBody.removeTextChangedListener(this);
        getBinding().registerLotFragmentEtLotName.removeTextChangedListener(this);
        getBinding().registerLotFragmentEtCapacity.removeTextChangedListener(this);
        getBinding().registerLotFragmentEtLocationLat.removeTextChangedListener(this);
        getBinding().registerLotFragmentEtLocationLng.removeTextChangedListener(this);

        mRegisterLotFragmentBinding = null; // Ready to get garbage collected
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
            if (updateErrorOf(getBinding().registerLotFragmentEtPhoneBody, registerLotFormState.getMobileNumberError())) {
                return;
            }
            if (updateErrorOf(getBinding().registerLotFragmentEtLotName, registerLotFormState.getLotNameError())) {
                return;
            }
            if (updateErrorOf(getBinding().registerLotFragmentEtCapacity, registerLotFormState.getLotCapacityError())) {
                return;
            }
            // Update the view's related to the lot's location
            if (updateErrorOf(getBinding().registerLotFragmentEtLocationLat, registerLotFormState.getLatLngError())) {
                return;
            }
            if (updateErrorOf(getBinding().registerLotFragmentEtLocationLng, registerLotFormState.getLatLngError())) {
                return;
            }
            // Update the view's related to the lot's slot offers
            if (registerLotFormState.getSlotOfferError() != null) {
                getBinding().registerLotFragmentTxtSlotOfferWarning.setVisibility(View.VISIBLE);
                getBinding().registerLotFragmentTxtSlotOfferWarning.setText(getString(registerLotFormState.getSlotOfferError()));
            } else {
                getBinding().registerLotFragmentTxtSlotOfferWarning.setVisibility(View.GONE);
            }
        });
    }

    /**
     * Initializes the fragment's Ui contents and listeners.
     */
    private void initializeUi() {
        // Initially the button is disabled
        getBinding().registerLotFragmentBtnRegisterLot.setEnabled(false);

        // Set the user's current email
        getBinding().registerLotFragmentTxtEmail.setText(
                mAuthStateViewModel.getUser().getEmail()
        );

        // Set up both spinners
        setUpSpinner(getBinding().registerLotFragmentSDuration, this::setSelectedDuration, 1.0f);
        setUpSpinner(getBinding().registerLotFragmentSPrice, this::setSelectedPrice, 0.5f);

        // Hook up a listener to the "get location" button
        getBinding().registerLotFragmentMbtnGetLocation.setOnClickListener(v -> {
            Toast.makeText(requireContext(), "Location retrieved!", Toast.LENGTH_SHORT).show();
            if (mLocationManager == null)
                mLocationManager = new LocationManager(requireContext(), this, true);
            mLocationManager.requestUserLocationUpdates(this);
        });

        setUpRecyclerViewWithAdapter();

        // Hook up a listener to the "Register" button
        final Button registerButton = getBinding().registerLotFragmentBtnRegisterLot;
        registerButton.setOnClickListener(this);

        // Hook up a listener to the "add" button
        getBinding().registerLotFragmentBtnAdd.setOnClickListener(v -> {
            // Add to the adapter's list
            mSlotOfferAdapter.insert(new SlotOffer(mSelectedDuration, mSelectedPrice));
            Toast.makeText(requireContext(), "Item added!", Toast.LENGTH_SHORT).show();
            // Scrolls down-wards to the "register" button
            ViewUtility.scrollTo(registerButton);
            // Update the ViewModel's state
            triggerViewModelUpdate();
        });

        // Attach a textWatcher to the UI's EditTexts
        getBinding().registerLotFragmentEtPhoneBody.addTextChangedListener(this);
        getBinding().registerLotFragmentEtLotName.addTextChangedListener(this);
        getBinding().registerLotFragmentEtCapacity.addTextChangedListener(this);
        getBinding().registerLotFragmentEtLocationLat.addTextChangedListener(this);
        getBinding().registerLotFragmentEtLocationLng.addTextChangedListener(this);
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
        mSlotOfferAdapter = new SlotOfferAdapter(mSlotOfferList);
        SlotOfferAdapter.setOnItemClickListener(v -> {
            // Access the item's position
            RecyclerView.ViewHolder viewHolder = (RecyclerView.ViewHolder) v.getTag();
            int position = viewHolder.getAdapterPosition();
            // Remove it from the list and notify the adapter
            if (position >= 0) {
                mSlotOfferAdapter.remove(position);
            }
            triggerViewModelUpdate();
        });
        // Bind recyclerView with its adapter
        recyclerView.setAdapter(mSlotOfferAdapter);

        // If recyclerView is inside a ScrollView then there is an issue while scrolling recyclerView’s inner contents.
        // So, when touching the recyclerView forbid the ScrollView from intercepting touch events.
        ViewUtility.disableParentScrollingInterferenceOf(recyclerView);
    }

    /**
     * Gather all inputted information into a single ParkingLot object
     * and return it.
     *
     * @return A ParkingLot object containing all necessary info of an operator's
     * lot.
     */
    @NotNull
    private ParkingLot buildParkingLotObject() {
        // Instantiate the ParkingLot object
        // and return it
        // coordinates lotName operatorId operatorMobileNumber capacity capacityForDisabled availableSpacesForDisabled slotOfferList
        return new ParkingLot(
                new Parking.Coordinates(
                        Double.parseDouble(getBinding().registerLotFragmentEtLocationLat.getText().toString()),
                        Double.parseDouble(getBinding().registerLotFragmentEtLocationLng.getText().toString())
                ),  // coordinates
                getBinding().registerLotFragmentEtLotName.getText().toString(), // lotName
                mAuthStateViewModel.getUser().getUserId(), // operatorId
                getBinding().registerLotFragmentEtPhoneBody.getNonSpacedText(), // operatorMobileNumber
                Integer.parseInt(getBinding().registerLotFragmentEtCapacity.getText().toString()), // capacity
                0, // capacityForDisabled
                0, // availableSpacesForDisabled
                mSlotOfferList // slotOfferList
        );
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

        int a = mSlotOfferList.size();
        mRegisterLotViewModel.lotRegistrationDataChanged(
                phoneNumber,
                lotCapacity,
                getBinding().registerLotFragmentEtLotName.getText().toString(),
                lotLatLng,
                mSlotOfferList
        );
    }

    /**
     * Updates the specified TextView's error status with the given error.
     * The method is used for Buttons and EditTexts (Derived classes of TextView).
     *
     * @param viewToBeUpdated A Button or EditText instance.
     * @param error           The id of the error associated with the specified View object.
     */
    private boolean updateErrorOf(TextView viewToBeUpdated, @Nullable Integer error) {
        if (error != null) {
            viewToBeUpdated.setError(getString(error));
            return true;
        } else {
            if (viewToBeUpdated.getError() != null) {
                viewToBeUpdated.setError(null, null);
            }
            return false;
        }
    }

    /**
     * Based on the given view find the {@link Spinner} with the given spinnerId
     * and initialize its values. Also, hook it up with an {@link AdapterView.OnItemSelectedListener}.
     * Whenever the listener gets triggered, set the value of the current spinner to the value of the
     * fragment's corresponding data member ({@link #mSelectedDuration}/{@link #mSelectedPrice}).
     *
     * @param targetSpinner      A reference of the spinner to be set up.
     * @param settable           The interface's method to act as a callback inside the listener.
     * @param volumeMultiplicand A float determining the sequence of values of the spinner.
     */
    private void setUpSpinner(@NotNull Spinner targetSpinner, Settable settable, float volumeMultiplicand) {
        // Get a reference to the spinner with the specified spinner id
        // Attach an OnItemSelectedListener on it
        targetSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // Cast the selected object to a String
                // TODO: Convert it to Float straight away.
                final String selectedVolume = (String) parent.getItemAtPosition(position);
                // Convert the spinner's value into a float and pass it in, to the settable's method.
                settable.setVolume(Float.parseFloat(selectedVolume));
            }

            public void onNothingSelected(AdapterView<?> parent) { /* ignore */ }
        });

        // Create an array that will hold all the values of the spinner, based on a multiplicand
        final String[] volume = Utility.getVolume(volumeMultiplicand, 1, 10);
        // Initialize an ArrayAdapter
        final ArrayAdapter<String> volumeAdapter = new ArrayAdapter<>(getContext(),
                android.R.layout.simple_spinner_dropdown_item,
                volume);
        // Bind the spinner with its adapter
        targetSpinner.setAdapter(volumeAdapter);
    }

    /**
     * Setter for {@link #mSelectedDuration}
     * Used to substitute {@link Settable#setVolume(float)}
     * in {@link #setUpSpinner(Spinner, Settable, float)}
     *
     * @param mSelectedDuration The latest selected duration of our duration spinner.
     */
    public void setSelectedDuration(float mSelectedDuration) {
        this.mSelectedDuration = mSelectedDuration;
    }

    /**
     * Setter for {@link #mSelectedPrice}
     * Used to substitute {@link Settable#setVolume(float)}
     * in {@link #setUpSpinner(Spinner, Settable, float)}
     *
     * @param mSelectedPrice The latest selected price of our price spinner.
     */
    public void setSelectedPrice(float mSelectedPrice) {
        this.mSelectedPrice = mSelectedPrice;
    }

    /**
     * Getter for {@link #mRegisterLotFragmentBinding}
     *
     * @return A reference to the fragment's binding instance.
     */
    public RegisterLotFragmentBinding getBinding() {
        return mRegisterLotFragmentBinding;
    }

    /**
     * Called when the "register" button has been clicked.
     *
     * @param v The view that was clicked.
     */
    @Override
    public void onClick(View v) {
        // Create a ParkingLot object to hold all necessary info.
        mRegisterLotViewModel.registerParkingLot(buildParkingLotObject())
                .addOnCompleteListener((Task<Void> task) -> {
                    if (task.getException() == null) {
                        // Display message to user.
                        Toast.makeText(RegisterLotFragment.this.requireContext(), RegisterLotFragment.this.getString(R.string.success_lot_registration), Toast.LENGTH_SHORT).show();
                        // Navigate back to home screen
                        Navigation.findNavController(RegisterLotFragment.this.getActivity().findViewById(R.id.fragment_main_host_nv_nav_view))
                                .popBackStack();
                    } else if (task.getException() instanceof NullPointerException
                            && task.getException().getMessage().equals("Continuation returned null")) {
                        // Display error message to user that the parking lot already exists
                        Toast.makeText(RegisterLotFragment.this.requireContext(), RegisterLotFragment.this.getString(R.string.error_lot_already_exists), Toast.LENGTH_SHORT).show();
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
        if (locationResult != null) {
            // Access the latest location
            Location currentLocation = locationResult.getLastLocation();
            // Set the Lat and Lng editTexts' text with the retrieved location's values.
            getBinding().registerLotFragmentEtLocationLat.setText(String.valueOf(currentLocation.getLatitude()));
            getBinding().registerLotFragmentEtLocationLng.setText(String.valueOf(currentLocation.getLongitude()));
        } else {
            // Inform the user something wrong happened
            Toast.makeText(requireContext(), getString(R.string.error_retrieving_location), Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Navigates from the current Fragment subclass to the
     * {@link io.github.cchristou3.CyParking.ui.user.login.AuthenticatorFragment}.
     */
    @Override
    public void toAuthenticator() {
        Navigation.findNavController(getActivity().findViewById(R.id.fragment_main_host_nv_nav_view))
                .navigate(R.id.action_nav_register_lot_fragment_to_nav_authenticator_fragment);
    }

    /**
     * Navigates from the current Fragment subclass to the
     * {@link io.github.cchristou3.CyParking.ui.parking.slots.viewBooking.ViewBookingsFragment}.
     */
    @Override
    public void toBookings() {
        Navigation.findNavController(getActivity().findViewById(R.id.fragment_main_host_nv_nav_view))
                .navigate(R.id.action_nav_register_lot_fragment_to_nav_view_bookings);
    }

    /**
     * Navigates from the current Fragment subclass to the
     * {@link io.github.cchristou3.CyParking.ui.user.AccountFragment}.
     */
    @Override
    public void toAccount() {
        Navigation.findNavController(getActivity().findViewById(R.id.fragment_main_host_nv_nav_view))
                .navigate(R.id.action_nav_register_lot_fragment_to_nav_account);
    }

    /**
     * Navigates from the current Fragment subclass to the
     * {@link io.github.cchristou3.CyParking.ui.user.feedback.FeedbackFragment}.
     */
    @Override
    public void toFeedback() {
        Navigation.findNavController(getActivity().findViewById(R.id.fragment_main_host_nv_nav_view))
                .navigate(R.id.action_nav_register_lot_fragment_to_nav_feedback);
    }

    /**
     * Navigates from the current Fragment subclass to the
     * {@link HomeFragment}.
     */
    @Override
    public void toHome() {
        Navigation.findNavController(getActivity().findViewById(R.id.fragment_main_host_nv_nav_view))
                .navigate(R.id.action_nav_register_lot_fragment_to_nav_home);
    }

    /**
     * Interface used in {@link #setUpSpinner(Spinner, Settable, float)}.
     */
    private interface Settable {
        void setVolume(float selectedVolume);
    }
}