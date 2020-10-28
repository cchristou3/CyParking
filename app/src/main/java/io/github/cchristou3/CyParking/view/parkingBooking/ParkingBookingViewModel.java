package io.github.cchristou3.CyParking.view.parkingBooking;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class ParkingBookingViewModel extends ViewModel {

    private static final int CREATING_DATE_DATA = 0;
    private static final int CREATING_STARTING_TIME_DATA = 1;
    private static final int CREATING_ENDING_TIME_DATA = 2;

    private final static int STARTING_TIME_FORWARD_HOURS = 0;
    private final static int ENDING_TIME_FORWARD_HOURS = 2;

    MutableLiveData<String> mPickedDate = new MutableLiveData<>(getValue(CREATING_DATE_DATA));
    MutableLiveData<String> mPickedStartingTime = new MutableLiveData<>(getValue(CREATING_STARTING_TIME_DATA));
    MutableLiveData<String> mPickedEndingTime = new MutableLiveData<>(getValue(CREATING_ENDING_TIME_DATA));

    public MutableLiveData<String> getmPickedDate() {
        return mPickedDate;
    }

    public MutableLiveData<String> getmPickedStartingTime() {
        return mPickedStartingTime;
    }

    public MutableLiveData<String> getmPickedEndingTime() {
        return mPickedEndingTime;
    }

    public String setUpToCurrentTime(int forwardHours) {
        // Access the current time of the day
        Calendar mcurrentTime = Calendar.getInstance();
        int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
        int minute = mcurrentTime.get(Calendar.MINUTE);
        return (ParkingBookingActivity.getTimeOf((hour + forwardHours), minute));
    }

    public String getValue(int condition) {
        String dataToBeSet = "";
        switch (condition) {
            case CREATING_DATE_DATA: {
                // Set the livedata's text to the current date
                final Date currentDate = Calendar.getInstance().getTime();
                SimpleDateFormat mSimpleDateFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
                String currentDateInString = mSimpleDateFormat.format(currentDate);
                dataToBeSet = currentDateInString;
                break;
            }
            case CREATING_STARTING_TIME_DATA: {
                dataToBeSet = setUpToCurrentTime(STARTING_TIME_FORWARD_HOURS);
                break;
            }
            case CREATING_ENDING_TIME_DATA: {
                dataToBeSet = setUpToCurrentTime(ENDING_TIME_FORWARD_HOURS);
                break;
            }
            default:
                throw new IllegalArgumentException();
        }
        return dataToBeSet;
    }
}
