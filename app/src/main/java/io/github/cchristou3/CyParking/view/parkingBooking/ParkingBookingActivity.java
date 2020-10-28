package io.github.cchristou3.CyParking.view.parkingBooking;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModelProvider;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.jetbrains.annotations.NotNull;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import io.github.cchristou3.CyParking.R;

public class ParkingBookingActivity extends AppCompatActivity {

    private SimpleDateFormat mSimpleDateFormat;
    private ParkingBookingViewModel mParkingBookingViewModel;

    // TODO: add to Helper method namespace
    public static String getTimeOf(final int finalHours, int minute) {
        final String finalMinutes = ((minute < 10) ? "0" : "") + minute;
        return "" + finalHours + " : " + finalMinutes;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parking_booking);

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
                            mParkingBookingViewModel.getmPickedDate().setValue(mSimpleDateFormat.format(getDate(year1, month1, dayOfMonth))),
                    year, month, day);
            datePickerDialog.show();
        });
    }

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

    private void attachListenerToTimePicker(@NotNull Button timePickerButton,
                                            @NotNull MutableLiveData<String> stringMutableLiveData) {
        timePickerButton.setOnClickListener(v -> {
            TimePickerDialog timePickerDialog = new TimePickerDialog(ParkingBookingActivity.this,
                    AlertDialog.THEME_HOLO_DARK, // TODO: useful for night mode THEME_HOLO_LIGHT
                    // Triggers textview Update
                    (view, hourOfDay, minute) -> stringMutableLiveData.setValue(getTimeOf(hourOfDay, minute)),
                    Calendar.getInstance().get(Calendar.HOUR),
                    Calendar.getInstance().get(Calendar.MINUTE),
                    true);
            timePickerDialog.show();
        });
    }

    public Date getDate(int year, int month, int day) {
        final Calendar innerCalendar = Calendar.getInstance();
        innerCalendar.set(Calendar.YEAR, year);
        innerCalendar.set(Calendar.MONTH, month);
        innerCalendar.set(Calendar.DAY_OF_MONTH, day);
        innerCalendar.set(Calendar.HOUR_OF_DAY, 0);
        innerCalendar.set(Calendar.MINUTE, 0);
        innerCalendar.set(Calendar.SECOND, 0);
        innerCalendar.set(Calendar.MILLISECOND, 0);
        return innerCalendar.getTime();
    }
}