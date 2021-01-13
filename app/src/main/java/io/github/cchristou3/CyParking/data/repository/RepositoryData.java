package io.github.cchristou3.CyParking.data.repository;

/**
 * Purpose: Hold constants relevant to most repositories
 * in one location.
 *
 * @author Charalambos Christou
 * @version 1.0 11/01/21
 */
class RepositoryData {

    // Firebase Firestore paths (nodes)
    static final String PARKING_LOTS = "parking_lots";
    static final String BOOKING = "bookings";
    static final String FEEDBACK = "feedback";
    static final String USERS = "users";

    // Additional fields for payments...

    // Cloud functions
    static final String FILTER_LOCATIONS = "filterLocations";

    // Database fields
    static final String BOOKING_USER_ID = "bookingUserId";
    static final String COMPLETED = "completed";
    static final String USER_EMAIL = "email";
    static final String USER_DISPLAY_NAME = "displayName";
    static final String LATITUDE = "latitude";
    static final String LONGITUDE = "longitude";

}
