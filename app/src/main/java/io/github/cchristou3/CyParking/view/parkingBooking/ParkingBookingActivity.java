package io.github.cchristou3.CyParking.view.parkingBooking;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModelProvider;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.jetbrains.annotations.NotNull;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import io.github.cchristou3.CyParking.R;
import io.github.cchristou3.CyParking.pojo.parking.PrivateParking;
import io.github.cchristou3.CyParking.repository.Utility;
import io.github.cchristou3.CyParking.view.parkingMap.ParkingMapActivity;

public class ParkingBookingActivity extends AppCompatActivity {

    private SimpleDateFormat mSimpleDateFormat;
    private ParkingBookingViewModel mParkingBookingViewModel;

    /**
     * Initialises the activity.
     * TODO: Builds the activity's Toolbar and Drawer navigation.
     * Adds listeners to our UI's Buttons.
     * Instantiates a ViewModel for the activity.
     * Adds observers to our LiveData Objects
     * TODO: Listens for changes in the Database for the specified document.
     *
     * @param savedInstanceState A bundle which contains info about previously stored data
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parking_booking);

        // Access the Intent's extras
        PrivateParking selectedParking = (PrivateParking) getIntent().getExtras().getParcelable(ParkingMapActivity.BOOKING_DETAILS_KEY);
        Toast.makeText(this, selectedParking.toString(), Toast.LENGTH_SHORT).show();

        // Get a reference to UI TextViews
        TextView parkingname = findViewById(R.id.activity_parking_booking_txt_parking_name);
        TextView parkingcapacity = findViewById(R.id.activity_parking_booking_txt_parking_capacity);
        TextView parkingavailability = findViewById(R.id.activity_parking_booking_txt_parking_availability);

        // Set their text to their corresponding value
        parkingname.setText("ParkingID: " + Integer.toString(selectedParking.getmParkingID()));
        parkingcapacity.setText("Capacity: " + Integer.toString(selectedParking.getmCapacity()));
        parkingavailability.setText("AvailableSpaces: " + Integer.toString(selectedParking.getmAvailableSpaces()));

        // Instantiate a view model object for the activity
        mParkingBookingViewModel =
                new ViewModelProvider(this).get(ParkingBookingViewModel.class);

        // Get references to the UI elements
        TextView datePickerTextView = findViewById(R.id.activity_parking_booking_txt_date);
        Button datePickerButton = findViewById(R.id.activity_parking_booking_btn_date_button);
        TextView startingTimeTextView = findViewById(R.id.activity_parking_booking_txt_starting_time);
        Button startingTimePickerButton = findViewById(R.id.activity_parking_booking_btn_starting_time_button);
        TextView endingTimeTextView = findViewById(R.id.activity_parking_booking_txt_ending_time);
        Button endingTimePickerButton = findViewById(R.id.activity_parking_booking_btn_ending_time_button);

        // Set up LiveData's Observers
        mParkingBookingViewModel.getmPickedDate().observe(this, datePickerTextView::setText);
        mParkingBookingViewModel.getmPickedStartingTime().observe(this, startingTimeTextView::setText);
        mParkingBookingViewModel.getmPickedEndingTime().observe(this, endingTimeTextView::setText);
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
            DatePickerDialog datePickerDialog = new DatePickerDialog(ParkingBookingActivity.this,
                    (view, year1, month1, dayOfMonth) ->
                            mParkingBookingViewModel.getmPickedDate().setValue(mSimpleDateFormat.format(Utility.getDateOf(year1, month1, dayOfMonth))),
                    year, month, day);
            datePickerDialog.show();
        });
    }

    /**
     * Validates the global LivaData objects.
     * If they are valid, a booking object is created and stored in the database.
     *
     * @param view UI context
     */
    public void bookParking(View view) {
        // Access parking operator's details via the Intent object
        String pickedDate = mParkingBookingViewModel.getmPickedDate().getValue();
        String pickedStartingTime = mParkingBookingViewModel.getmPickedStartingTime().getValue();
        String pickedEndingTime = mParkingBookingViewModel.getmPickedEndingTime().getValue();

        // TODO: Check that pickedStartingTime < pickedEndingTime

        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        String username;
        String userID;
        if (firebaseUser != null) {
            username = firebaseUser.getDisplayName();
            userID = firebaseUser.getUid();
        } else {
            username = "Anonymous";
            userID = "OneRandomUserID";
        }

        // Create a new object that will hold all the data necessary for a booking
        //PrivateParkingBooking parkingToBeBooked = new PrivateParkingBooking();
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
            TimePickerDialog timePickerDialog = new TimePickerDialog(ParkingBookingActivity.this,
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