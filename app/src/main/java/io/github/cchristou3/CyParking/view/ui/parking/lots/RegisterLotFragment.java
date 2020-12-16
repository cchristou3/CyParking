package io.github.cchristou3.CyParking.view.ui.parking.lots;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import io.github.cchristou3.CyParking.R;
import io.github.cchristou3.CyParking.view.data.interfaces.Navigate;
import io.github.cchristou3.CyParking.view.data.pojo.parking.lot.SlotOffer;
import io.github.cchristou3.CyParking.view.utilities.Utility;

/**
 * Purpose: Allow the operator-typed user to register
 * their Parking Lot to the application's system.
 *
 * @author cchar
 * @version 1.0 14/12/20
 */
public class RegisterLotFragment extends Fragment implements Navigate {

    // Fragment's members
    private RegisterLotViewModel mViewModel;
    private List<SlotOffer> mSlotOfferList;
    private Float mSelectedDuration;
    private Float mSelectedPrice;

    /**
     * Called to have the fragment instantiate its user interface view.
     */
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.register_lot_fragment, container, false);
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
        mViewModel = new ViewModelProvider(this).get(RegisterLotViewModel.class);

        // TODO: Bind UI with ViewModel

        // Initialize the slot offer container
        mSlotOfferList = new ArrayList<>();

        // Set up both spinners
        setUpSpinner(view, R.id.register_lot_fragment_s_duration, this::setSelectedDuration, 1.0f);
        setUpSpinner(view, R.id.register_lot_fragment_s_price, this::setSelectedPrice, 0.5f);

        // Hook up a listener to the "get location" button
        view.findViewById(R.id.register_lot_fragment_mbtn_get_location).setOnClickListener(v -> {
            // TODO: Get user's latest known location. After, extract the lat and lng attributes from it.
            //  and finally update the EditTexts' text.
            Toast.makeText(requireContext(), "Get location!", Toast.LENGTH_SHORT).show();
        });

        // Set up the recycler view
        RecyclerView recyclerView = view.findViewById(R.id.register_lot_fragment_rv_price_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        // Set up the RecyclerView's adapter
        final SlotOfferAdapter slotOfferAdapter = new SlotOfferAdapter(mSlotOfferList);
        SlotOfferAdapter.setOnItemClickListener(v -> {
            // Access the item's position
            RecyclerView.ViewHolder viewHolder = (RecyclerView.ViewHolder) v.getTag();
            int position = viewHolder.getAdapterPosition();
            // Remove it from the list and notify the adapter
            if (position >= 0) {
                slotOfferAdapter.remove(position);
            }
        });
        // Bind recyclerView with its adapter
        recyclerView.setAdapter(slotOfferAdapter);

        // If recyclerView is inside a ScrollView then there is an issue while scrolling recyclerView’s inner contents.
        // So, when touching the recyclerView forbid the ScrollView from intercepting touch events.
        Utility.disableParentScrollingInterferenceOf(recyclerView);

        // Hook up a listener to the "Register" button
        Button registerButton = view.findViewById(R.id.register_lot_fragment_btn_register_lot);
        registerButton.setOnClickListener(v -> {
            Toast.makeText(requireContext(), "Lot registered!", Toast.LENGTH_SHORT).show();
        });

        // Hook up a listener to the "add" button
        view.findViewById(R.id.register_lot_fragment_btn_add).setOnClickListener(v -> {
            // Add to the adapter's list
            slotOfferAdapter.insert(new SlotOffer(mSelectedDuration, mSelectedPrice));
            Toast.makeText(requireContext(), "Item added!", Toast.LENGTH_SHORT).show();
            // Scrolls down towards the "register" button
            registerButton.getParent().requestChildFocus(registerButton, registerButton);
        });
    }

    /**
     * Based on the given view find the {@link Spinner} with the given spinnerId
     * and initialize its values. Also, hook it up with an {@link AdapterView.OnItemSelectedListener}.
     * Whenever the listener gets triggered, set the value of the current spinner to the value of the
     * fragment's corresponding data member ({@link #mSelectedDuration}/{@link #mSelectedPrice}).
     *
     * @param view               The view to search the specified Spinner.
     * @param spinnerId          The id of the spinner to look for and set up.
     * @param settable           The interface's method to act as a callback inside the listener.
     * @param volumeMultiplicand A float determining the sequence of values of the spinner.
     */
    private void setUpSpinner(@NotNull View view, int spinnerId, Settable settable, float volumeMultiplicand) {
        // Get a reference to the spinner with the specified spinner id
        Spinner volumeSpinner = view.findViewById(spinnerId);
        // Attach an OnItemSelectedListener on it
        volumeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
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
        String[] volume = getVolume(volumeMultiplicand);
        // Initialize an ArrayAdapter
        ArrayAdapter<String> volumeAdapter = new ArrayAdapter<>(getContext(),
                android.R.layout.simple_spinner_dropdown_item,
                volume);
        // Bind the spinner with its adapter
        volumeSpinner.setAdapter(volumeAdapter);
    }

    /**
     * Generates a sequence of numbers and stores them in an Array.
     *
     * @param multiplicand The number to multiply with every index (multiplier) of the array.
     * @return An array of string that holds numeric values.
     */
    @NotNull
    private String[] getVolume(float multiplicand) {
        String[] volumes = new String[10];
        for (int multiplier = 0; multiplier < 10; multiplier++) {
            volumes[multiplier] = String.valueOf((multiplier * multiplicand));
        }
        return volumes;
    }

    /**
     * Setter for {@link #mSelectedDuration}
     * Used to substitute {@link Settable#setVolume(float)}
     * in {@link #setUpSpinner(View, int, Settable, float)}
     *
     * @param mSelectedDuration The latest selected duration of our duration spinner.
     */
    public void setSelectedDuration(float mSelectedDuration) {
        this.mSelectedDuration = mSelectedDuration;
    }

    /**
     * Setter for {@link #mSelectedPrice}
     * Used to substitute {@link Settable#setVolume(float)}
     * in {@link #setUpSpinner(View, int, Settable, float)}
     *
     * @param mSelectedPrice The latest selected price of our price spinner.
     */
    public void setSelectedPrice(float mSelectedPrice) {
        this.mSelectedPrice = mSelectedPrice;
    }

    /**
     * Navigates from the current Fragment subclass to the
     * {@link io.github.cchristou3.CyParking.view.ui.user.login.AuthenticatorFragment}.
     */
    @Override
    public void toAuthenticator() {
        Navigation.findNavController(getActivity().findViewById(R.id.fragment_main_host_nv_nav_view))
                .navigate(R.id.action_nav_register_lot_fragment_to_nav_authenticator_fragment);
    }

    /**
     * Navigates from the current Fragment subclass to the
     * {@link io.github.cchristou3.CyParking.view.ui.parking.slots.viewBooking.ViewBookingsFragment}.
     */
    @Override
    public void toBookings() {
        Navigation.findNavController(getActivity().findViewById(R.id.fragment_main_host_nv_nav_view))
                .navigate(R.id.action_nav_register_lot_fragment_to_nav_view_bookings);
    }

    /**
     * Navigates from the current Fragment subclass to the
     * {@link io.github.cchristou3.CyParking.view.ui.user.AccountFragment}.
     */
    @Override
    public void toAccount() {
        Navigation.findNavController(getActivity().findViewById(R.id.fragment_main_host_nv_nav_view))
                .navigate(R.id.action_nav_register_lot_fragment_to_nav_account);
    }

    /**
     * Navigates from the current Fragment subclass to the
     * {@link io.github.cchristou3.CyParking.view.ui.user.feedback.FeedbackFragment}.
     */
    @Override
    public void toFeedback() {
        Navigation.findNavController(getActivity().findViewById(R.id.fragment_main_host_nv_nav_view))
                .navigate(R.id.action_nav_register_lot_fragment_to_nav_feedback);
    }

    /**
     * Navigates from the current Fragment subclass to the
     * {@link io.github.cchristou3.CyParking.view.ui.HomeFragment}.
     */
    @Override
    public void toHome() {
        Navigation.findNavController(getActivity().findViewById(R.id.fragment_main_host_nv_nav_view))
                .navigate(R.id.action_nav_register_lot_fragment_to_nav_home);
    }

    /**
     * Interface used in {@link #setUpSpinner(View, int, Settable, float)}.
     */
    private interface Settable {
        void setVolume(float selectedVolume);
    }
}