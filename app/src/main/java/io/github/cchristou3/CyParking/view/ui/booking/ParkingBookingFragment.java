package io.github.cchristou3.CyParking.view.ui.booking;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
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

import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.jetbrains.annotations.NotNull;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

import io.github.cchristou3.CyParking.R;
import io.github.cchristou3.CyParking.view.data.pojo.parking.PrivateParking;
import io.github.cchristou3.CyParking.view.data.pojo.parking.PrivateParkingResultSet;
import io.github.cchristou3.CyParking.view.data.pojo.parking.booking.PrivateParkingBooking;
import io.github.cchristou3.CyParking.view.data.repository.ParkingRepository;
import io.github.cchristou3.CyParking.view.data.repository.Utility;

/**
 * purpose: View parking details, (TODO:) choose a payment method
 * and book the specific parking for a specific date and time.
 *
 * @author Charalambos Christou
 * @version 1.0 29/10/20
 */
public class ParkingBookingFragment extends Fragment {

    // Fragment variables
    private SimpleDateFormat mSimpleDateFormat;
    private ParkingBookingViewModel mParkingBookingViewModel;
    private View mView;
    private PrivateParkingResultSet mSelectedParking;
    private TextView parkingAvailability;

    /**
     * Initialises the fragment. Uses the EventBus, to get access to data send by the previous fragment.
     *
     * @param savedInstanceState A bundle which contains info about previously stored data
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /*
         * By registering the EventBus we have access to both ongoing and sticky events.
         * The onResultReceived method is our subscriber.
         * If "sticky" is set to true, the subscriber method delivers the most recent sticky event (posted with
         * {@link EventBus#postSticky(Object)}) to this subscriber (if event available).
         */
        EventBus.getDefault().register(this);
        // onResultReceived gets invoked, no need to further listen for updates. Thus, unregister.
        EventBus.getDefault().unregister(this);
    }

    /**
     * Our event subscriber method. In this case, the event is the POJO PrivateParking.
     * Receives the latest event that was posted using EventBus.getInstance().postSticky([object]),
     * once the EventBus is registered.
     *
     * @param privateParking The PrivateParking instance which was selected in the previous fragment (ParkingMapFragment)
     */
    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    public void onResultReceived(PrivateParkingResultSet privateParking) {
        mSelectedParking = privateParking;
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mView = inflater.inflate(R.layout.fragment_parking_booking, container, false);
        return mView;
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
        TextView parkingName = mView.findViewById(R.id.fragment_parking_booking_txt_parking_name);
        TextView parkingCapacity = mView.findViewById(R.id.fragment_parking_booking_txt_parking_capacity);
        parkingAvailability = mView.findViewById(R.id.fragment_parking_booking_txt_parking_availability);
        TextView datePickerTextView = mView.findViewById(R.id.fragment_parking_booking_txt_date);
        Button datePickerButton = mView.findViewById(R.id.fragment_parking_booking_btn_date_button);
        TextView startingTimeTextView = mView.findViewById(R.id.fragment_parking_booking_txt_starting_time);

        // Set their text to their corresponding value
        final String parkingID = "ParkingID: " + mSelectedParking.getParking().getParkingID();
        final String capacity = "Capacity: " + mSelectedParking.getParking().getCapacity();
        final String availableSpaces = "AvailableSpaces: " + mSelectedParking.getParking().getAvailableSpaces();
        parkingName.setText(parkingID);
        parkingCapacity.setText(capacity);
        parkingAvailability.setText(availableSpaces);

        // Instantiate a view model object for the activity
        mParkingBookingViewModel =
                new ViewModelProvider(this).get(ParkingBookingViewModel.class);


        Button startingTimePickerButton = mView.findViewById(R.id.fragment_parking_booking_btn_starting_time_button);
        TextView endingTimeTextView = mView.findViewById(R.id.fragment_parking_booking_txt_ending_time);
        Button endingTimePickerButton = mView.findViewById(R.id.fragment_parking_booking_btn_ending_time_button);

        // Set up LiveData's Observers
        mParkingBookingViewModel.getmPickedDate().observe(getViewLifecycleOwner(), datePickerTextView::setText);
        mParkingBookingViewModel.getmPickedStartingTime().observe(getViewLifecycleOwner(), startingTimeTextView::setText);
        mParkingBookingViewModel.getmPickedEndingTime().observe(getViewLifecycleOwner(), endingTimeTextView::setText);
        datePickerTextView.setText(mParkingBookingViewModel.getmPickedDate().getValue());
        startingTimeTextView.setText(mParkingBookingViewModel.getmPickedStartingTime().getValue());
        endingTimeTextView.setText(mParkingBookingViewModel.getmPickedEndingTime().getValue());

        // Set up time pickers' listeners
        attachListenerToTimePicker(startingTimePickerButton, mParkingBookingViewModel.getmPickedStartingTime());
        attachListenerToTimePicker(endingTimePickerButton, mParkingBookingViewModel.getmPickedEndingTime());

        // Set up date picker listener
        datePickerButton.setOnClickListener(v -> {
            // Access the current date via the a Calendar object
            final Calendar calendar = Calendar.getInstance();
            final int year = calendar.get(Calendar.YEAR);
            final int month = calendar.get(Calendar.MONTH);
            final int day = calendar.get(Calendar.DAY_OF_MONTH);

            // Instantiate a DatePickerDialog and how it to the user
            // The dialog's date will be set to the current date.
            mSimpleDateFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
            DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(),
                    (viewObject, year1, month1, dayOfMonth) ->
                            mParkingBookingViewModel.getmPickedDate().setValue(mSimpleDateFormat.format(Utility.getDateOf(year1, month1, dayOfMonth))),
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

    @Override
    public void onStart() {
        super.onStart();
        FirebaseFirestore.getInstance().collection("private_parking")
                .document(mSelectedParking.getDocumentID())
                .addSnapshotListener(requireActivity(), (value, error) -> {
                    if (error != null) return;
                    final String updatedParkingSlots = "AvailableSpaces: " + Objects.requireNonNull(Objects.requireNonNull(value)
                            .toObject(PrivateParking.class)).getAvailableSpaces();

                    parkingAvailability.setText(updatedParkingSlots);
                });
    }

    /**
     * Validates the global LivaData objects.
     * If they are valid, a booking object is created and stored in the database.
     */
    public void bookParking() throws ParseException {
        // Access parking operator's details via the Intent object
        String pickedDate = mParkingBookingViewModel.getmPickedDate().getValue();
        String pickedStartingTime = mParkingBookingViewModel.getmPickedStartingTime().getValue();
        String pickedEndingTime = mParkingBookingViewModel.getmPickedEndingTime().getValue();

        // Check that pickedStartingTime < pickedEndingTime && validate Date as well!
        int startingHours = Integer.parseInt(Objects.requireNonNull(pickedStartingTime).substring(0, 2)); // Access first two digits
        int startingMinutes = Integer.parseInt(pickedStartingTime.substring(5, 7)); // Access last two digits

        int endingHours = Integer.parseInt(Objects.requireNonNull(pickedEndingTime).substring(0, 2)); // Access first two digits
        int endingMinutes = Integer.parseInt(pickedEndingTime.substring(5, 7)); // Access last two digits

        Date pickedDateObject = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).parse(Objects.requireNonNull(pickedDate));

        if (((startingHours == endingHours && startingMinutes < endingMinutes) || (startingHours < endingHours))
                && Objects.requireNonNull(pickedDateObject).compareTo(Calendar.getInstance().getTime()) >= 0) {
            // Proceed with transaction and create a booking
            // Create a new PrivateParkingBooking instance which will hold all data of the booking.
            FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
            FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
            String username = (firebaseUser != null) ? firebaseUser.getDisplayName() : "Anonymous";
            String userID = (firebaseUser != null) ? firebaseUser.getUid() : "OneRandomUserID";

            PrivateParking parking = mSelectedParking.getParking();

            PrivateParkingBooking privateParkingBooking = new PrivateParkingBooking(
                    parking.getCoordinates(), parking.getParkingID(), Integer.toString(parking.getParkingID()),
                    Integer.toString(parking.getParkingID()), userID, username,
                    pickedDateObject, pickedStartingTime, pickedEndingTime, 2.0);

            // Store to the database
            Task<DocumentReference> docRef = ParkingRepository.bookParking(privateParkingBooking);
            docRef.addOnCompleteListener(task -> {
                // Inform the user booking was successful and offer a temporary UNDO option
                if (task.isSuccessful())
                    Snackbar.make(mView, "Parking has been booked successfully!", Snackbar.LENGTH_LONG)
                            .setAction("UNDO", v -> ParkingRepository.cancelParking(task.getResult().getId())).show();
            });
        } else {  // Otherwise, inform the user he inputted incorrect data
            Toast.makeText(getContext(), "The start time must be less than the ending time!", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Attaches an OnClickListener to the given button.
     * OnClick: Creates a TimePickerDialog with its own OnTimeSetListener.
     * OnTimeSetListener-onClick: Updates the value of the specified LiveData Object.
     *
     * @param timePickerButton      reference to a button in the layout
     * @param stringMutableLiveData LiveData which handles persistence of a String
     */
    private void attachListenerToTimePicker(@NotNull Button timePickerButton,
                                            @NotNull MutableLiveData<String> stringMutableLiveData) {
        timePickerButton.setOnClickListener(v -> {
            TimePickerDialog timePickerDialog = new TimePickerDialog(getContext(),
                    AlertDialog.THEME_HOLO_DARK, // TODO: useful for night mode THEME_HOLO_LIGHT
                    // Triggers textview Update
                    (view, hourOfDay, minute) -> stringMutableLiveData.setValue(Utility.getTimeOf(hourOfDay, minute)),
                    Calendar.getInstance().get(Calendar.HOUR),
                    Calendar.getInstance().get(Calendar.MINUTE),
                    true);
            timePickerDialog.show();
        });
    }
}