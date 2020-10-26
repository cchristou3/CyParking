package io.github.cchristou3.CyParking.view.parkingBooking;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import io.github.cchristou3.CyParking.R;
import io.github.cchristou3.CyParking.view.parkingMap.ParkingMapActivity;

public class ParkingBookingActivity extends AppCompatActivity {

    private final int STARTING_TIME_FORWARD_HOURS = 0;
    private final int ENDING_TIME_FORWARD_HOURS = 2;
    private SimpleDateFormat mSimpleDateFormat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parking_booking);

        // Get references to the UI elements
        TextView datePickerTextView = findViewById(R.id.activity_parking_booking_txt_date);
        Button datePickerButton = findViewById(R.id.activity_parking_booking_btn_date_button);
        TextView startingTimeTextView = findViewById(R.id.activity_parking_booking_txt_starting_time);
        Button startingTimePickerButton = findViewById(R.id.activity_parking_booking_btn_starting_time_button);
        TextView endingTimeTextView = findViewById(R.id.activity_parking_booking_txt_ending_time);
        Button endingTimePickerButton = findViewById(R.id.activity_parking_booking_btn_ending_time_button);

        // Access the current time of the day
        Calendar mcurrentTime = Calendar.getInstance();
        int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
        Log.d(ParkingMapActivity.TAG, "onCreate: " + hour);
        int minute = mcurrentTime.get(Calendar.MINUTE);

        // Set up time picker listeners
        setUpTimePicker(startingTimeTextView, startingTimePickerButton, hour, minute, STARTING_TIME_FORWARD_HOURS);
        setUpTimePicker(endingTimeTextView, endingTimePickerButton, hour, minute, ENDING_TIME_FORWARD_HOURS);

        // Set up date picker listener
        // Set the textview's text to the current date
        final Date currentDate = Calendar.getInstance().getTime();
        mSimpleDateFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
        String currentDateInString = mSimpleDateFormat.format(currentDate);
        datePickerTextView.setText(currentDateInString);

        datePickerButton.setOnClickListener(v -> {
            // Access the current date via the a Calendar object
            final Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);

            // Instantiate a DatePickerDialog and how it to the user
            // The dialog's date will be set to the current date.
            DatePickerDialog datePickerDialog = new DatePickerDialog(ParkingBookingActivity.this,
                    (view, year1, month1, dayOfMonth) ->
                            datePickerTextView.setText(mSimpleDateFormat.format(getDate(year1, month1, dayOfMonth))),
                    year, month, day);
            datePickerDialog.show();
        });
    }

    private void setUpTimePicker(TextView timeTextView, Button timePickerButton, int hour, int minute, int hoursForward) {
        timeTextView.setText(getTimeOf((hour + hoursForward), minute));
        timePickerButton.setOnClickListener(v -> {
            TimePickerDialog timePickerDialog = new TimePickerDialog(ParkingBookingActivity.this,
                    AlertDialog.THEME_HOLO_DARK, // TODO: useful for night mode THEME_HOLO_LIGHT
                    (view, hourOfDay, minute1) -> {
                        timeTextView.setText(getTimeOf(hourOfDay, minute1));
                    }, hour, minute, true);
            timePickerDialog.show();
        });
    }

    public String getTimeOf(final int finalHours, int minute) {
        final String finalMinutes = ((minute < 10) ? "0" : "") + minute;
        final String finalTime = "" + finalHours + " : " + finalMinutes;
        return finalTime;
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