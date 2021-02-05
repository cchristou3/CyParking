package io.github.cchristou3.CyParking.data.manager;

import android.content.Context;
import android.os.Parcel;
import android.text.format.DateFormat;

import com.google.android.material.datepicker.CalendarConstraints;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;
import com.google.android.material.timepicker.MaterialTimePicker;
import com.google.android.material.timepicker.TimeFormat;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import io.github.cchristou3.CyParking.utilities.DateTimeUtility;

/**
 * Purpose: Contain all logic related to creating {@link MaterialTimePicker}
 * and {@link MaterialDatePicker} instances.
 *
 * @author Charalambos Christou
 * @version 1.0 05/02/21
 * @see io.github.cchristou3.CyParking.ui.views.parking.slots.booking.BookingFragment
 */
public class DateTimePicker {

    /**
     * Create a new instance of {@link MaterialDatePicker<Long>}.
     * The {@link MaterialDatePicker.Builder} is used to initialize its
     * selected date to today's date, its {@link CalendarConstraints} and its
     * {@link MaterialPickerOnPositiveButtonClickListener}.
     *
     * @param dateSelectedListener The handler for the date selection events.
     * @return a new instance of {@link MaterialDatePicker<Long>}.
     * @see #getCalendarConstraints(long, long)
     */
    @NotNull
    public static MaterialDatePicker<Long> getDatePicker(OnDateSelectedListener dateSelectedListener) {
        // Instantiate a DatePickerDialog and how it to the user
        // The dialog's date will be set to the current date.
        Calendar calendar = Calendar.getInstance(Locale.getDefault());
        calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), 1);
        long startMonth = calendar.getTimeInMillis();
        calendar.roll(Calendar.MONTH, true); // From today's month till next month
        long endMonth = calendar.getTimeInMillis();

        MaterialDatePicker<Long> materialDatePicker =
                MaterialDatePicker.Builder.datePicker()
                        .setSelection(new Date().getTime()) // Select the current date
                        .setCalendarConstraints(
                                getCalendarConstraints(startMonth, endMonth)
                        )
                        .build();
        // Attach it an on positive click listener
        materialDatePicker.addOnPositiveButtonClickListener(selection -> {
            // Access the selected date's year, month and day
            calendar.setTime(new Date(selection));
            int selectedYear = calendar.get(Calendar.YEAR);
            int selectedMonth = calendar.get(Calendar.MONTH);
            int selectedDayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
            // Trigger the given onSelected callback
            dateSelectedListener.onSelected(
                    selectedYear,
                    selectedMonth + 1, // As the Gregorian Calendar's months start from 0
                    selectedDayOfMonth
            );
        });
        return materialDatePicker;
    }

    /**
     * Create a new instance of {@link MaterialTimePicker}.
     * The {@link MaterialTimePicker.Builder} is used to initialize the picker's
     * time format based on the system's format,
     * its selected current time,
     * its input mode,
     * and its title.
     *
     * @param context              The context to make use of to access the device's locale.
     * @param timeSelectedListener The handler for the time selection events.
     * @return a new instance of {@link MaterialTimePicker}.
     */
    @NotNull
    public static MaterialTimePicker getTimePicker(Context context, OnTimeSelectedListener timeSelectedListener) {
        // Access the device's time locale
        boolean isSystem24Hour = DateFormat.is24HourFormat(context);

        MaterialTimePicker materialTimePicker = new MaterialTimePicker.Builder()
                .setTimeFormat(
                        isSystem24Hour ? TimeFormat.CLOCK_24H : TimeFormat.CLOCK_12H
                )
                .setHour(Calendar.getInstance().get(Calendar.HOUR_OF_DAY)) // current hour
                .setMinute(Calendar.getInstance().get(Calendar.MINUTE)) // current minute
                .setInputMode(MaterialTimePicker.INPUT_MODE_CLOCK)
                .setTitleText("Select Booking Time")
                .build();

        // Add a listener to its positive button
        materialTimePicker
                .addOnPositiveButtonClickListener(v1 -> timeSelectedListener.onSelected(
                        materialTimePicker.getHour(),
                        materialTimePicker.getMinute()));

        return materialTimePicker;
    }


    /**
     * Create an instance of {@link CalendarConstraints}.
     * The {@link CalendarConstraints.Builder} is used to initialize
     * the the calendar's constraints and month range.
     *
     * @param startMonth The first available month of the calendar.
     * @param endMonth   The last available month of the calendar.
     * @return an instance of {@link CalendarConstraints}.
     */
    @NotNull
    private static CalendarConstraints getCalendarConstraints(long startMonth, long endMonth) {
        return new CalendarConstraints.Builder().setValidator(
                getDateValidator()
        )
                .setStart(startMonth)
                .setOpenAt(startMonth)
                .setEnd(endMonth)
                .build();
    }

    /**
     * Create a new instance of {@link CalendarConstraints.DateValidator}.
     * Its {@link CalendarConstraints.DateValidator#isValid(long)} method
     * is used, to make invalid dates unavailable.
     * This DateValidator returns true to all dates that are equal or greater to
     * today's date.
     *
     * @return a new instance of {@link CalendarConstraints.DateValidator}.
     */
    @NotNull
    @Contract(value = " -> new", pure = true)
    private static CalendarConstraints.DateValidator getDateValidator() {
        return new CalendarConstraints.DateValidator() {
            public int describeContents() { /* ignore */
                return 0;
            }

            public void writeToParcel(Parcel dest, int flags) { /* ignore */ }

            @Override
            public boolean isValid(long date) {
                // Greater or equal than today's date
                return DateTimeUtility.isGreaterOrEqualToCurrentDate(new Date(date));
            }
        };
    }

    /**
     * Purpose: Used to allow the caller to handle
     * the event of when a user has selected
     * a date from the {@link MaterialDatePicker} returned by
     * {@link #getDatePicker(OnDateSelectedListener)}.
     */
    public interface OnDateSelectedListener {
        /**
         * Gets triggered whenever the user loads a {@link MaterialDatePicker},
         * selects a date and presses 'OK'.
         *
         * @param selectedYear  The selected Year.
         * @param selectedMonth The selected Month.
         * @param selectedDay   The selected Day.
         */
        void onSelected(int selectedYear, int selectedMonth, int selectedDay);
    }

    /**
     * Purpose: Used to allow the caller to handle
     * the event of when a user has selected
     * a time from the {@link MaterialTimePicker} returned by
     * {@link #getTimePicker(Context, OnTimeSelectedListener)}.
     */
    public interface OnTimeSelectedListener {
        /**
         * Gets triggered whenever the user loads a {@link MaterialTimePicker},
         * selects a time and presses 'OK'.
         *
         * @param hours   The selected hour.
         * @param minutes The selected minutes.
         */
        void onSelected(int hours, int minutes);
    }
}
