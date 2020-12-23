package io.github.cchristou3.CyParking.data.manager;

import android.graphics.drawable.Drawable;

import androidx.annotation.NonNull;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Set;

import io.github.cchristou3.CyParking.data.pojo.parking.lot.ParkingLot;
import io.github.cchristou3.CyParking.data.repository.ParkingRepository;
import io.github.cchristou3.CyParking.utilities.Utility;

/**
 * Purpose: manage {@link Marker} objects of {@link io.github.cchristou3.CyParking.ui.parking.lots.ParkingMapFragment}.
 * Responsible for:
 * <p> - Maintaining the user's marker.
 * <p> - Adding/updating/removing markers.
 * <p> - Associating markers with {@link ParkingLot} objects.
 * <p> - Retrieving the marker's corresponding {@link ParkingLot} object.
 *
 * @author Charalambos Christou
 * @version 1.0 22/12/20
 */
public class MarkerManager {

    private static final double DEFAULT_DOUBLE_VALUE = 0.0D;

    // Holds all Marker - ParkingLot pairs. Used for look up: Marker -> ParkingLot
    final private HashMap<Marker, ParkingLot> mMarkerToValueMap;
    private final Drawable mUserMapIcon;
    private ParkingLot mSelectedParkingLot;
    private Marker mUserMarker;

    /**
     * Public Constructor.
     * Initializes its {@link #mMarkerToValueMap} HashMap
     * and assigns its {@link #mUserMapIcon} with the specified
     * drawable.
     *
     * @param drawable The icon to be displayed on the user's position.
     */
    public MarkerManager(Drawable drawable) {
        this.mUserMapIcon = drawable;
        mUserMapIcon.setAlpha(95); // = Opacity
        this.mMarkerToValueMap = new HashMap<>();
    }

    /**
     * Places a marker on the user's position with the icon
     * {@link #mUserMapIcon} and adds it a tag. The marker is
     * then saved as a reference to {@link #mUserMarker}.
     * If previously the user already had a marker on the map,
     * then it gets replaced by the new marker.
     *
     * @param mGoogleMap           The GoogleMap instance hosting the markers.
     * @param mCurrentLatLngOfUser The position of the user.
     */
    public void setUserMarker(GoogleMap mGoogleMap, LatLng mCurrentLatLngOfUser) {
        // Keep track of the user's marker. When their location changes, remove current one
        // and add a new one.
        if (mUserMarker != null) mUserMarker.remove();

        mUserMarker = mGoogleMap.addMarker(new MarkerOptions()
                .title("Title")
                .position(mCurrentLatLngOfUser)
                .icon(BitmapDescriptorFactory.fromBitmap(Utility.drawableToBitmap(mUserMapIcon))) // TODO: Replace with an actual icon
                .snippet("Current Location!")
                .title("Me"));
        // Add a tag to it, to differentiate it from the other markers on the map
        mUserMarker.setTag(new Object());
    }

    /**
     * Compares the given marker's coordinates with the specified
     * latitude and longitude.
     *
     * @param markerOfParking  The marker to has its coordinates compared with the specified coordinates.
     * @param parkingLatitude  The latitude of a parking.
     * @param parkingLongitude The longitude of a parking.
     * @return True if the coordinates match. Otherwise, false.
     */
    public boolean areCoordinatesTheSame(Marker markerOfParking, double parkingLatitude, double parkingLongitude) {
        // Access the marker's latitude and longitude
        double markerLat = getLocationAttributeOf(markerOfParking, ParkingRepository.LATITUDE_KEY);
        double markerLng = getLocationAttributeOf(markerOfParking, ParkingRepository.LONGITUDE_KEY);
        return markerLat == parkingLatitude && markerLng == parkingLongitude;
    }

    /**
     * Access the {@link #mSelectedParkingLot}.
     *
     * @return A reference to the selected marker's associated {@link ParkingLot} object.
     */
    public ParkingLot getSelectedParkingLot() {
        return mSelectedParkingLot;
    }

    /**
     * Access the key-set of the {@link #mMarkerToValueMap}.
     *
     * @return A Set of all the keys of {@link #mMarkerToValueMap}.
     */
    public Set<Marker> getKeySets() {
        return mMarkerToValueMap.keySet();
    }

    /**
     * Access the selected parking lot's coordinates specified by a key.
     * Could be either the Latitude or the Longitude.
     *
     * @param key The key used for look up.
     * @return The value corresponding to the key.
     */
    public double getSelectedLocationAttribute(String key) {
        return getCoordinateAttributeWithKey(mSelectedParkingLot, key);
    }

    /**
     * Access the marker's associated parking lot's coordinates
     * based on a key.
     * Could be either the Latitude or the Longitude.
     *
     * @param marker
     * @param key    The key used for look up.
     * @return The value corresponding to the key.
     */
    public double getLocationAttributeOf(Marker marker, String key) {
        return getCoordinateAttributeWithKey(mMarkerToValueMap.get(marker), key);
    }

    /**
     * Access the a parking lot's coordinates
     * based on a key.
     * Could be either the Latitude or the Longitude.
     *
     * @param lot A {@link ParkingLot} object to have it coordinates extracted from.
     * @param key The key used for look up.
     * @return The value corresponding to the key.
     */
    private double getCoordinateAttributeWithKey(@NotNull ParkingLot lot, String key) {
        if (lot.getCoordinates().get(key) != null)
            return lot.getCoordinates().get(key);
        else
            return DEFAULT_DOUBLE_VALUE;
    }

    /**
     * Check whether the specified marker
     * is associated with a {@link ParkingLot} object.
     *
     * @param marker The marker to be looked up.
     * @return True if the marker is attached to a ParkingLot object. Otherwise, false.
     */
    public boolean exists(Marker marker) {
        return (mMarkerToValueMap.get(marker) != null);
    }

    /**
     * Access the {@link ParkingLot} object associated with the specified
     * marker.
     *
     * @param marker The marker to be used to look up it corresponding ParkingLot object.
     * @return A reference to the marker's corresponding ParkingLot object.
     */
    public ParkingLot getParkingLotOf(Marker marker) {
        return mMarkerToValueMap.get(marker);
    }

    /**
     * Removes the marker from both the {@link #mMarkerToValueMap}
     * and the map.
     * Called whenever a ParkingLot is REMOVED from the database.
     *
     * @param marker The marker to be erased.
     * @see io.github.cchristou3.CyParking.ui.parking.lots.ParkingMapFragment#retrieveDataAndListenForChanges
     */
    public void removeMarker(Marker marker) {
        // Remove it from the HashMap
        mMarkerToValueMap.remove(marker);
        // Remove it from the map
        marker.remove();
    }

    /**
     * Checks if we have any marker on the map which has the same LatLng
     * as the given LatLng.
     *
     * @param latLng The coordinates of an object/event on the map.
     * @return True, if we have any marker which has the same coordinates. Otherwise, false.
     */
    public boolean anyMatchWithCoordinates(@NotNull LatLng latLng) {
        // Create a hash map based on clicked location
        return mMarkerToValueMap.containsValue(new HashMap<String, Double>() {{
            put(ParkingRepository.LATITUDE_KEY, latLng.latitude);
            put(ParkingRepository.LONGITUDE_KEY, latLng.longitude);
        }});
    }

    /**
     * One of many overloaded versions of {@link #addMarker}.
     * Places a marker on the given GoogleMap instance on the specified
     * position, and associates it with the given {@link ParkingLot} object.
     * Called whenever a newly ADDED ParkingLot is received from the database.
     *
     * @param mGoogleMap      A reference to the map.
     * @param lot             The lot to be attached to the created marker.
     * @param markerLatitude  The latitude of the marker.
     * @param markerLongitude The longitude of the marker.
     * @see io.github.cchristou3.CyParking.ui.parking.lots.ParkingMapFragment#retrieveDataAndListenForChanges
     */
    public void addMarker(@NotNull GoogleMap mGoogleMap, @NotNull ParkingLot lot, double markerLatitude, double markerLongitude) {
        add(
                mGoogleMap.addMarker(new MarkerOptions()
                        .position(new LatLng(markerLatitude, markerLongitude))
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE))),

                lot);
    }

    /**
     * One of many overloaded versions of {@link #addMarker}.
     * Associates the given marker with the given {@link ParkingLot} object.
     * Called whenever a MODIFIED ParkingLot is received from the database.
     *
     * @param lot    The lot to be attached to the given marker.
     * @param marker The marker to be associated with the given ParkingLot object.
     * @see io.github.cchristou3.CyParking.ui.parking.lots.ParkingMapFragment#retrieveDataAndListenForChanges
     */
    public void addMarker(@NotNull ParkingLot lot, @NonNull Marker marker) {
        add(marker, lot);
    }

    /**
     * One of many overloaded versions of {@link #addMarker}.
     * Places a marker on the given GoogleMap instance on the specified
     * position, and associates it with the given {@link ParkingLot} object.
     *
     * @param mGoogleMap A reference to the map.
     * @param lot        The lot to be attached to the created marker.
     * @see #addAll(GoogleMap, ParkingLot[])
     */
    private void addMarker(@NotNull GoogleMap mGoogleMap, @NotNull ParkingLot lot) {
        double parkingLatitude = lot.getParkingLotAttribute(ParkingRepository.LATITUDE_KEY);
        double parkingLongitude = lot.getParkingLotAttribute(ParkingRepository.LONGITUDE_KEY);
        addMarker(mGoogleMap, lot, parkingLatitude, parkingLongitude);
    }

    /**
     * Iterates the given array and for each ParkingLot instance,
     * creates a marker, associates it with it and places it on the specified
     * GoogleMap instance.
     *
     * @param map  A reference to the map.
     * @param lots An array of {@link ParkingLot} instances.
     */
    public void addAll(GoogleMap map, @NotNull ParkingLot[] lots) {
        // Traverse through the fetched objects and add them to the map.
        for (ParkingLot parking : lots) {
            // Get the coordinates of the parking
            addMarker(map, parking);
        }
    }

    /**
     * Creates a key-value relationship between the specified marker
     * and the specified Parking lot.
     * <p>{@link Marker} -> {@link ParkingLot}
     *
     * @param marker The marker to be associated with the given ParkingLot object.
     * @param lot    The lot to be attached to the given marker.
     */
    private void add(Marker marker, ParkingLot lot) {
        mMarkerToValueMap.put(marker, lot);
    }

    /**
     * Sets the value of {@link #mSelectedParkingLot} with the
     * ParkingLot object associated with the specified marker.
     *
     * @param marker The marker (key) whose value will be assigned to {@link #mSelectedParkingLot}.
     */
    public void setSelectedParking(Marker marker) {
        mSelectedParkingLot = mMarkerToValueMap.get(marker);
    }
}