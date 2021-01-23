package io.github.cchristou3.CyParking.data.repository;

/**
 * Purpose: Hold constants relevant to most repositories
 * in one location.
 * <strong>Note:</strong> all fields are package-private so that
 * only the repository classes can access them.
 *
 * @author Charalambos Christou
 * @version 2.0 21/01/21
 */
class RepositoryData {

    // Firebase Firestore paths (nodes)
    static final String PARKING_LOTS = "parking_lots";
    static final String BOOKINGS = "bookings";
    static final String FEEDBACK = "feedback";
    static final String USERS = "users";

    // Additional fields for payments...

    // Cloud functions
    static final String FILTER_LOCATIONS = "filterLocations";
    static final String GET_NEARBY_PARKING_LOTS = "getNearbyParkingLots";


    // Database fields
    static final String USER_EMAIL = "email";
    static final String USER_DISPLAY_NAME = "displayName";
    static final String LATITUDE = "latitude";
    static final String LONGITUDE = "longitude";

}
