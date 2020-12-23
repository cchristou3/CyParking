package io.github.cchristou3.CyParking.ui.parking.slots.booking;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.greenrobot.eventbus.EventBus;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

import io.github.cchristou3.CyParking.R;
import io.github.cchristou3.CyParking.data.interfaces.Navigable;
import io.github.cchristou3.CyParking.data.pojo.parking.lot.ParkingLot;
import io.github.cchristou3.CyParking.data.pojo.parking.slot.booking.PrivateParkingBooking;
import io.github.cchristou3.CyParking.data.repository.ParkingRepository;
import io.github.cchristou3.CyParking.ui.HomeFragment;
import io.github.cchristou3.CyParking.ui.parking.slots.viewBooking.ViewBookingsFragment;
import io.github.cchristou3.CyParking.ui.user.AccountFragment;
import io.github.cchristou3.CyParking.ui.user.feedback.FeedbackFragment;
import io.github.cchristou3.CyParking.ui.user.login.AuthenticatorFragment;
import io.github.cchristou3.CyParking.utilities.Utility;

import static io.github.cchristou3.CyParking.ui.parking.lots.ParkingMapFragment.TAG;

/**
 * Purpose: <p>View parking details,
 * and book the specific parking for a specific date and time.</p>
 * <p>
 * TODO: - choose a payment method
 * - generate QR code
 * - Change "end date" to duration. -> Simpler validation
 * -> Update {@link PrivateParkingBooking}
 *
 * @author Charalambos Christou
 * @version 4.0 23/12/20
 */
public class ParkingBookingFragment extends Fragment implements Navigable {

    // Fragment variables
    private ParkingBookingViewModel mParkingBookingViewModel;
    private ParkingLot mSelectedParking;
    private TextView parkingAvailability;
    private TextView parkingCapacity;

    /**
     * Initialises the fragment. Uses the EventBus, to get access to data send by the previous fragment.
     *
     * @param savedInstanceState A bundle which contains info about previously stored data
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            mSelectedParking = Objects.requireNonNull(EventBus.getDefault().getStickyEvent(ParkingLot.class));
        } catch (ClassCastException | NullPointerException e) {
            Log.e(TAG, "onCreateView: ", e); // TODO: Plan B
        }
    }

    /**
     * Inflates our fragment's view. Saves a reference to the fragment's view
     *
     * @param inflater           Inflater which will inflate our view
     * @param container          The parent view
     * @param savedInstanceState A bundle which contains info about previously stored data
     * @return The view of the fragment
     */
    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_parking_booking, container, false);
    }

    /**
     * Invoked at the completion of onCreateView. Initializes fragment's ViewModel.
     * Lastly, it attaches listeners to our UI elements and observers to our LiveData instances.
     *
     * @param view               The view of the fragment
     * @param savedInstanceState A bundle which contains info about previously stored data
     */
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Get references to the UI elements
        final TextView parkingName = view.findViewById(R.id.fragment_parking_booking_txt_parking_name);
        parkingCapacity = view.findViewById(R.id.fragment_parking_booking_txt_parking_capacity);
        parkingAvailability = view.findViewById(R.id.fragment_parking_booking_txt_parking_availability);
        final TextView datePickerTextView = view.findViewById(R.id.fragment_parking_booking_txt_date);
        final Button datePickerButton = view.findViewById(R.id.fragment_parking_booking_btn_date_button);
        final TextView startingTimeTextView = view.findViewById(R.id.fragment_parking_booking_txt_starting_time);

        // Set their text to their corresponding value
        final String parkingID = "ParkingID: " + mSelectedParking.getParkingID();
        final String capacity = "Capacity: " + mSelectedParking.getCapacity();
        final String availableSpaces = "AvailableSpaces: " + mSelectedParking.getAvailableSpaces();
        parkingName.setText(parkingID);
        parkingCapacity.setText(capacity);
        parkingAvailability.setText(availableSpaces);

        // Instantiate a view model object for the activity
        mParkingBookingViewModel =
                new ViewModelProvider(this).get(ParkingBookingViewModel.class);

        final Button startingTimePickerButton = view.findViewById(R.id.fragment_parking_booking_btn_starting_time_button);
        final TextView endingTimeTextView = view.findViewById(R.id.fragment_parking_booking_txt_ending_time);
        final Button endingTimePickerButton = view.findViewById(R.id.fragment_parking_booking_btn_ending_time_button);

        // Set up LiveData's Observers
        mParkingBookingViewModel.getPickedDate().observe(getViewLifecycleOwner(), datePickerTextView::setText);
        mParkingBookingViewModel.getPickedStartingTime().observe(getViewLifecycleOwner(), startingTimeTextView::setText);
        mParkingBookingViewModel.getPickedEndingTime().observe(getViewLifecycleOwner(), endingTimeTextView::setText);
        datePickerTextView.setText(mParkingBookingViewModel.getPickedDate().getValue());
        startingTimeTextView.setText(mParkingBookingViewModel.getPickedStartingTime().getValue());
        endingTimeTextView.setText(mParkingBookingViewModel.getPickedEndingTime().getValue());

        // Set up time pickers' listeners
        startingTimePickerButton.setOnClickListener(buildListenerForTimePicker(mParkingBookingViewModel.getPickedStartingTime()));
        endingTimePickerButton.setOnClickListener(buildListenerForTimePicker(mParkingBookingViewModel.getPickedEndingTime()));

        // Set up date picker listener
        datePickerButton.setOnClickListener(v -> {
            // Access the current date via the a Calendar object
            final int year = Calendar.getInstance().get(Calendar.YEAR);
            final int month = Calendar.getInstance().get(Calendar.MONTH);
            final int day = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);

            // Instantiate a DatePickerDialog and how it to the user
            // The dialog's date will be set to the current date.
            DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(),
                    (viewObject, year1, month1, dayOfMonth) ->
                            mParkingBookingViewModel.getPickedDate().setValue(new SimpleDateFormat("dd-MM-yyyy",
                                    Locale.getDefault())
                                    .format(Utility.getDateOf(year1, month1, dayOfMonth))),
                    year, month, day);
            datePickerDialog.show();
        });

        // Set listener to "BOOK" button
        view.findViewById(R.id.fragment_parking_btn_booking_button).setOnClickListener(v -> {
            try {
                bookParking();
            } catch (ParseException e) {
                Toast.makeText(getContext(), "Unexpected Error occurred! Try Restarting the application!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * An observer is attached to the current document, to listen for changes (number of available spaces).
     * Removal of observer is self-managed by the hosting activity.
     */
    @Override
    public void onStart() {
        super.onStart();
        ParkingRepository.observeSelectedParking(mSelectedParking)
                .addSnapshotListener(requireActivity(), (value, error) -> {
                    if (error != null || (value == null || value.toObject(ParkingLot.class) == null))
                        return;
                    final String updatedAvailability = "AvailableSpaces: " +
                            value.toObject(ParkingLot.class).getAvailableSpaces();
                    final String updatedCapacity = "AvailableSpaces: " +
                            value.toObject(ParkingLot.class).getCapacity();

                    parkingAvailability.setText(updatedAvailability);
                    parkingCapacity.setText(updatedAvailability);
                });
    }

    /**
     * Validates the global LivaData objects.
     * If they are valid, a booking object is created and stored in the database.
     */
    public void bookParking() throws ParseException {
        // TODO: Migrate boiler-plate code into a Date Validation Class
        //  and simply call its methods.
        // Access parking operator's details via the Intent object
        final String pickedDate = mParkingBookingViewModel.getPickedDate().getValue();
        final String pickedStartingTime = mParkingBookingViewModel.getPickedStartingTime().getValue();
        final String pickedEndingTime = mParkingBookingViewModel.getPickedEndingTime().getValue();

        // Check that pickedStartingTime < pickedEndingTime && validate Date as well!
        int startingHours = Integer.parseInt(Objects.requireNonNull(pickedStartingTime).substring(0, 2)); // Access first two digits
        int startingMinutes = Integer.parseInt(pickedStartingTime.substring(5, 7)); // Access last two digits

        int endingHours = Integer.parseInt(Objects.requireNonNull(pickedEndingTime).substring(0, 2)); // Access first two digits
        int endingMinutes = Integer.parseInt(pickedEndingTime.substring(5, 7)); // Access last two digits

        final Date pickedDateObject = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
                .parse(Objects.requireNonNull(pickedDate));
        Log.d(TAG, "bookParking: Starting time: " + startingHours + " : " + startingMinutes);
        Log.d(TAG, "bookParking: Ending time: " + endingHours + " : " + endingMinutes);

        // Access the current date
        final Calendar currentDate = Calendar.getInstance();
        currentDate.setTime(Calendar.getInstance().getTime());
        currentDate.set(Calendar.HOUR_OF_DAY, 0);
        currentDate.set(Calendar.MINUTE, 0);
        currentDate.set(Calendar.SECOND, 0);
        currentDate.set(Calendar.MILLISECOND, 0);
        Log.d(TAG, "bookParking: today date: " + currentDate.getTime());
        Log.d(TAG, "bookParking: picked date: " + pickedDateObject);

        if (((startingHours == endingHours && startingMinutes < endingMinutes) || (startingHours < endingHours))
                && Objects.requireNonNull(pickedDateObject).compareTo(currentDate.getTime()) >= 0) {
            // Proceed with transaction and create a booking
            // Create a new PrivateParkingBooking instance which will hold all data of the booking.
            FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
            FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
            String username = (firebaseUser != null) ? firebaseUser.getDisplayName() : "Anonymous";
            String userID = (firebaseUser != null) ? firebaseUser.getUid() : "OneRandomUserID";

            final PrivateParkingBooking privateParkingBooking = new PrivateParkingBooking(
                    mSelectedParking.getCoordinates(), mSelectedParking.getParkingID(), Integer.toString(mSelectedParking.getParkingID()),
                    Integer.toString(mSelectedParking.getParkingID()), userID, username,
                    pickedDateObject, pickedStartingTime, pickedEndingTime, 2.0);

            // Store to the database
            Task<Void> docRef = ParkingRepository.bookParking(privateParkingBooking);
            docRef.addOnCompleteListener(task -> {
                // Inform the user booking was successful and offer a temporary UNDO option
                if (task.isSuccessful()) {
                    Snackbar.make(requireView(), "Parking has been booked successfully!", Snackbar.LENGTH_LONG)
                            .setAction("UNDO", v -> ParkingRepository.cancelParking(privateParkingBooking.generateUniqueId())).show();
                    Navigation.findNavController(getActivity().findViewById(R.id.fragment_main_host_nv_nav_view))
                            .navigate(R.id.action_nav_parking_booking_fragment_pop);
                }
                // TODO: Generate QR Code
            });
        } else {  // Otherwise, inform the user he inputted incorrect data
            Toast.makeText(getContext(), "The start time must be less than the ending time!", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Creates an OnClickListener to the given button.
     * OnClick: Creates a TimePickerDialog with its own OnTimeSetListener.
     * OnTimeSetListener-onClick: Updates the value of the specified LiveData Object.
     *
     * @param stringMutableLiveData LiveData which handles persistence of a String
     * @return An View.OnClickListener
     */
    @NotNull
    @Contract(pure = true)
    private View.OnClickListener buildListenerForTimePicker(@NotNull MutableLiveData<String> stringMutableLiveData) {
        return v -> {
            TimePickerDialog timePickerDialog = new TimePickerDialog(getContext(),
                    AlertDialog.THEME_HOLO_DARK, // TODO: useful for night mode THEME_HOLO_LIGHT
                    // Triggers TextView's Update
                    (view, hourOfDay, minute) -> stringMutableLiveData.setValue(Utility.getTimeOf(hourOfDay, minute)),
                    Calendar.getInstance().get(Calendar.HOUR),
                    Calendar.getInstance().get(Calendar.MINUTE),
                    true);
            timePickerDialog.show();
        };
    }


    /**
     * Navigates from the current Fragment subclass to the
     * {@link AuthenticatorFragment}.
     */
    @Override
    public void toAuthenticator() {
        // The user must be logged in to be in this fragment (Booking screen). Thus, no need to implement this method.
    }

    /**
     * Navigates from the current Fragment subclass to the
     * {@link ViewBookingsFragment}.
     */
    @Override
    public void toBookings() {
        Navigation.findNavController(getActivity().findViewById(R.id.fragment_main_host_nv_nav_view))
                .navigate(R.id.action_nav_parking_booking_fragment_to_nav_view_bookings);
    }

    /**
     * Navigates from the current Fragment subclass to the
     * {@link AccountFragment}.
     */
    @Override
    public void toAccount() {
        Navigation.findNavController(getActivity().findViewById(R.id.fragment_main_host_nv_nav_view))
                .navigate(R.id.action_nav_parking_booking_fragment_to_nav_account);
    }

    /**
     * Navigates from the current Fragment subclass to the
     * {@link FeedbackFragment}.
     */
    @Override
    public void toFeedback() {
        Navigation.findNavController(getActivity().findViewById(R.id.fragment_main_host_nv_nav_view))
                .navigate(R.id.action_nav_parking_booking_fragment_to_nav_feedback);
    }

    /**
     * Navigates from the current Fragment subclass to the
     * {@link HomeFragment}.
     */
    @Override
    public void toHome() {
        Navigation.findNavController(getActivity().findViewById(R.id.fragment_main_host_nv_nav_view))
                .navigate(R.id.action_nav_parking_booking_fragment_to_nav_home);
    }

}