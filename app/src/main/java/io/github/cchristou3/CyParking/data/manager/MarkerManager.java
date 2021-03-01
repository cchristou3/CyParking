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

import io.github.cchristou3.CyParking.data.model.parking.lot.ParkingLot;
import io.github.cchristou3.CyParking.ui.views.parking.lots.map.ParkingMapFragment;
import io.github.cchristou3.CyParking.utilities.DrawableUtility;

/**
 * Purpose: manage {@link Marker} objects of {@link ParkingMapFragment}.
 * Responsible for:
 * <p> - Maintaining the user's marker.
 * <p> - Adding/updating/removing markers.
 * <p> - Associating markers with {@link ParkingLot} objects.
 * <p> - Retrieving the marker's corresponding {@link ParkingLot} object.
 *
 * @author Charalambos Christou
 * @version 3.0 21/01/21
 */
public class MarkerManager {

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
                .icon(BitmapDescriptorFactory.fromBitmap(DrawableUtility.drawableToBitmap(mUserMapIcon)))
                // TODO: Replace with an actual icon
                .snippet("Current Location!")
                .title("Me"));
        // Add a tag to it, to differentiate it from the other markers on the map
        mUserMarker.setTag(new Object());
    }

    /**
     * Compares the given marker's coordinates with the specified
     * {@link ParkingLot} instance.
     *
     * @param markerOfParking The marker to has its coordinates compared with the specified coordinates.
     * @return True if the coordinates match. Otherwise, false.
     */
    public boolean areCoordinatesTheSame(Marker markerOfParking, @NotNull ParkingLot parkingLot) {
        return areCoordinatesTheSame(markerOfParking, parkingLot.getLatitude(), parkingLot.getLongitude());
    }

    /**
     * Compares the given marker's coordinates with the specified
     * latitude and longitude.
     *
     * @param markerOfParking The marker to has its coordinates compared with the specified coordinates.
     * @return True if the coordinates match. Otherwise, false.
     */
    private boolean areCoordinatesTheSame(Marker markerOfParking, double lat, double lng) {
        // Access the marker's latitude and longitude
        double markerLat = getParkingLotOf(markerOfParking).getLatitude();
        double markerLng = getParkingLotOf(markerOfParking).getLongitude();
        return markerLat == lat && markerLng == lng;
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
     * Sets the value of the {@link #mSelectedParkingLot} with
     * the specified argument.
     */
    public void setSelectedParkingLot(ParkingLot lot) {
        mSelectedParkingLot = lot;
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
     * Access the selected parking lot's latitude.
     *
     * @return The lot's latitude.
     */
    public double getSelectedMarkerLatitude() {
        return mSelectedParkingLot.getLatitude();
    }

    /**
     * Access the selected parking lot's longitude.
     *
     * @return The lot's longitude.
     */
    public double getSelectedMarkerLongitude() {
        return mSelectedParkingLot.getLongitude();
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
     * @see ParkingMapFragment#onStart()
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
        // Iterate all markers till you find one that has the same coordinates with the given ones.
        for (Marker key : this.mMarkerToValueMap.keySet()) {
            if (this.areCoordinatesTheSame(key, latLng.latitude, latLng.longitude)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Places a marker on the given GoogleMap instance on the specified
     * position, and associates it with the given {@link ParkingLot} object.
     * Called whenever a newly ADDED ParkingLot is received from the database.
     * If the the parking lot already exists within {@link #mMarkerToValueMap}
     * then the additional {@link ParkingLot} object is ignored.
     *
     * @param mGoogleMap A reference to the map.
     * @param lot        The lot to be attached to the created marker.
     * @see ParkingMapFragment#onStart()
     */
    public void addMarkerWithContents(@NotNull GoogleMap mGoogleMap, @NotNull ParkingLot lot) {
        if (mMarkerToValueMap.containsValue(lot)) return; // check if it already exists
        put(
                mGoogleMap.addMarker(new MarkerOptions()
                        .position(new LatLng(lot.getLatitude(), lot.getLongitude()))
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE))),
                lot
        );
    }

    /**
     * Associates the given marker with the given {@link ParkingLot} object.
     * Called whenever a MODIFIED ParkingLot is received from the database.
     *
     * @param lot    The lot to be attached to the given marker.
     * @param marker The marker to be associated with the given ParkingLot object.
     * @see ParkingMapFragment#onStart()
     */
    public void replaceMarkerContents(@NotNull ParkingLot lot, @NonNull Marker marker) {
        put(marker, lot);
    }

    /**
     * Creates a key-value relationship between the specified marker
     * and the specified Parking lot.
     * <p>{@link Marker} -> {@link ParkingLot}
     *
     * @param marker The marker to be associated with the given ParkingLot object.
     * @param lot    The lot to be attached to the given marker.
     */
    private void put(Marker marker, ParkingLot lot) {
        mMarkerToValueMap.put(marker, lot);
    }
}