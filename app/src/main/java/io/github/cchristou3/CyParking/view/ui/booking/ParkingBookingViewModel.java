package io.github.cchristou3.CyParking.view.ui.booking;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import io.github.cchristou3.CyParking.view.data.repository.Utility;

/**
 * A ViewModel implementation, adopted to the ParkingBookingFragment fragment.
 * purpose: Data persistence during orientation changes.
 *
 * @author Charalambos Christou
 * @version 1.0 29/10/20
 */
public class ParkingBookingViewModel extends ViewModel {

    // ViewModel constants
    private static final int CREATING_DATE_DATA = 0;
    private static final int CREATING_STARTING_TIME_DATA = 1;
    private static final int CREATING_ENDING_TIME_DATA = 2;
    private final static int STARTING_TIME_FORWARD_HOURS = 0;
    private final static int ENDING_TIME_FORWARD_HOURS = 2;

    MutableLiveData<String> mPickedDate = new MutableLiveData<>(getInitialValue(CREATING_DATE_DATA));
    MutableLiveData<String> mPickedStartingTime = new MutableLiveData<>(getInitialValue(CREATING_STARTING_TIME_DATA));
    MutableLiveData<String> mPickedEndingTime = new MutableLiveData<>(getInitialValue(CREATING_ENDING_TIME_DATA));

    public MutableLiveData<String> getmPickedDate() {
        return mPickedDate;
    }

    public MutableLiveData<String> getmPickedStartingTime() {
        return mPickedStartingTime;
    }

    public MutableLiveData<String> getmPickedEndingTime() {
        return mPickedEndingTime;
    }

    /**
     * Computes the time of the day taking, into account the amount of hours to be additionally added.
     *
     * @param forwardHours The amount of hours added to the current one.
     *                     (E.g. current= "12 : 30", forwardHours = 2 -> final = "14: 30 ")
     * @return A String which corresponds to the time (E.g. "12 : 30")
     */
    public String initializeToCurrentTime(int forwardHours) {
        // Access the current time of the day
        Calendar mcurrentTime = Calendar.getInstance();
        int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
        int minute = mcurrentTime.get(Calendar.MINUTE);
        return (Utility.getTimeOf((hour + forwardHours), minute));
    }

    /**
     * Computes the initial value of a Mutable LiveData instance based on specified condition
     *
     * @param condition Defines which String instance to compute and return
     * @return A String which a Mutable LiveData object will be initialized to
     */
    public String getInitialValue(int condition) {
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
                dataToBeSet = initializeToCurrentTime(STARTING_TIME_FORWARD_HOURS);
                break;
            }
            case CREATING_ENDING_TIME_DATA: {
                dataToBeSet = initializeToCurrentTime(ENDING_TIME_FORWARD_HOURS);
                break;
            }
            default:
                throw new IllegalArgumentException();
        }
        return dataToBeSet;
    }
}
