package io.github.cchristou3.CyParking.view.parkingMap;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.FragmentActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.gson.Gson;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Set;

import io.github.cchristou3.CyParking.R;
import io.github.cchristou3.CyParking.pojo.parking.PrivateParking;
import io.github.cchristou3.CyParking.repository.ParkingRepository;
import io.github.cchristou3.CyParking.view.parkingBooking.ParkingBookingActivity;

public class ParkingMapActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnMapClickListener,
        GoogleMap.OnMarkerClickListener {

    // Constant variables
    public static final String TAG = "cchristou3-CyParking";
    public static final int LOCATION_PERMISSION_REQUEST_CODE = 321;
    public static final long INTERVAL_TIME = 5000L;
    private static final int ZOOM_LEVEL = 16;

    // Location related variables
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private LocationCallback mLocationCallback;
    private LocationRequest mLocationRequest;

    // Activity variables
    private GoogleMap mGoogleMap;
    private boolean mDataHasBeenRetrieved;
    private HashMap<Marker, PrivateParking> mHashMapToValuesOfMarkersInScene = new HashMap<Marker, PrivateParking>();
    private LatLng mCurrentLatLngOfUser;
    private boolean mRegistered;
    private PrivateParking mSelectedPrivateParking;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parking_map);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.activity_parking_map_fg_google_map);
        mapFragment.getMapAsync(this);

        // set flag to false and call registerCurrentLocationUpdates method
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        mLocationCallback = getLocationCallback();

        // Initialize location based variables
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(INTERVAL_TIME);
        mRegistered = false;
        mDataHasBeenRetrieved = false;
    }

    // TODO: Place all helper functions in a namespace
    public static Bitmap drawableToBitmap(Drawable drawable) {
        Bitmap bitmap;

        if (drawable instanceof BitmapDrawable) {
            BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
            if (bitmapDrawable.getBitmap() != null) {
                return bitmapDrawable.getBitmap();
            }
        }

        if (drawable.getIntrinsicWidth() <= 0 || drawable.getIntrinsicHeight() <= 0) {
            bitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888); // Single color bitmap will be created of 1x1 pixel
        } else {
            bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        }

        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);
        return bitmap;
    }

    @Override
    protected void onPause() {
        super.onPause();
        unRegisterCurrentLocationUpdates();
    }

    @Override
    protected void onStart() {
        super.onStart();
        // Initialize a firebase firestore observer to inform us about changes in the DB
        // By passing the activity (this), the listener will get removed automatically when the
        // activity calls onStop()
        FirebaseFirestore.getInstance().collection("private_parking").addSnapshotListener(this, (value, error) -> {
            // ref: https://firebase.google.com/docs/firestore/query-data/listen#view_changes_between_snapshots
            if (error != null) {
                Log.w(TAG, "listen:error", error);
                return;
            }
            for (DocumentChange dc : value.getDocumentChanges()) {
                switch (dc.getType()) {
                    case ADDED:
                        //Log.d(TAG, "New parking: " + dc.getDocument().getData());
                        break;
                    case MODIFIED:
                        //Log.d(TAG, "Modified parking: " + dc.getDocument().getData());
                        break;
                    case REMOVED:
                        //Log.d(TAG, "Removed parking: " + dc.getDocument().getData());
                        break;
                }
            }
            if (mCurrentLatLngOfUser != null) { // re-fetch the data from the server
                fetchPrivateParking(mCurrentLatLngOfUser);
                Toast.makeText(this, "Database change triggered!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NotNull String[] permissions, @NotNull int[] grantResults) {

        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {

            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                // start to find location..
                registerCurrentLocationUpdates();

            } else { // if permission is not granted

                // decide what you want to do if you don't get permissions
                Toast.makeText(this, "Permission is not granted!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void registerCurrentLocationUpdates() {
        if (!mRegistered) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                if (Build.VERSION.SDK_INT >= 23) // Marshmallow
                    ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
            } else { // no need to ask for permission
                // start to find location...
                mFusedLocationProviderClient.requestLocationUpdates(mLocationRequest, mLocationCallback, getMainLooper());
                mRegistered = true;
            }
        }
    }

    private void unRegisterCurrentLocationUpdates() {
        if (mRegistered) {
            mFusedLocationProviderClient.removeLocationUpdates(mLocationCallback);
            mRegistered = false;
        }
    }

    public void fetchPrivateParking(LatLng latLng) {
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        // TODO: While the user waits -> show loading dialog
        //  OR -> Lazy load the markers
        //  -> animate user's location ref: https://codinginfinite.com/android-example-animate-marker-map-current-location/

        double userLatitude = latLng.latitude;
        double userLongitude = latLng.longitude;
        final Request<String> requestForPrivateParking = new StringRequest(Request.Method.GET,
                getResources().getString(R.string.firestore_api_url) + "?latitude=" + userLatitude + "&longitude=" + userLongitude,
                response -> {
                    // Convert json object into an array of objects
                    final PrivateParking[] privateParking = new Gson().fromJson(response, PrivateParking[].class);

                    // clear all the current markers from the map except fromt the user's one.
                    for (Marker marker : mHashMapToValuesOfMarkersInScene.keySet()) { // the key is of type Marker
                        marker.remove();
                    }

                    // Traverse through the fetched objects and add them to the map.
                    for (PrivateParking parking : privateParking) {
                        // Get the coordinates of the parking
                        try {
                            double parkingLatitude = parking.getmCoordinates().get(ParkingRepository.LATITUDE_KEY);
                            double parkingLongitude = parking.getmCoordinates().get(ParkingRepository.LONGITUDE_KEY);
                            LatLng parkingLatLng = new LatLng(parkingLatitude, parkingLongitude);
                            Log.d(TAG, "fetchPrivateParking: " + parking.toString());
                            // Add a marker to the parking's coordinates
                            Marker marker = mGoogleMap.addMarker(new MarkerOptions()
                                    .position(parkingLatLng)
                                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));

                            // Add to hashmap to keep track of each marker's corresponding PrivateParking object
                            mHashMapToValuesOfMarkersInScene.put(marker, parking);
                        } catch (NullPointerException e) {
                            Log.d(TAG, "Parking producing null coordinates!");
                        }
                    }
                }, error -> {
            // todo: inform the user
            Toast.makeText(this, "Unexpected error occurred!\n" + error.getMessage(), Toast.LENGTH_SHORT).show();
            Log.e(TAG, "Volley error: " + error.getMessage());
        });
        requestQueue.add(requestForPrivateParking);
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mGoogleMap = googleMap;
        registerCurrentLocationUpdates();

        // Todo Create a custom InfoWindowsAdapter
        //mGoogleMap.setInfoWindowAdapter(new CustomInfoWindowAdapter(this, mHashMapToValuesOfMarkersInScene));

        // Add listeners to the markers + map
        mGoogleMap.setOnMarkerClickListener(this);
        mGoogleMap.setOnMapClickListener(this);

        mGoogleMap.moveCamera(CameraUpdateFactory.zoomTo(ZOOM_LEVEL));
    }

    @Override
    public void onMapClick(LatLng latLng) {
        Toast.makeText(this, "Map has been clicked!", Toast.LENGTH_SHORT).show();
        Set<Marker> allKeysOfVisibleMarkers = mHashMapToValuesOfMarkersInScene.keySet();
//        HashMap<String, Double> a = new HashMap<String, Double>() {
//            {
//                put(ParkingRepository.LATITUDE_KEY, latLng.latitude);
//                put(ParkingRepository.LONGITUDE_KEY, latLng.longitude);
//            }};
//
//        boolean containsValue = mHashMapToValuesOfMarkersInScene.containsValue(a);
//        

        for (Marker key : allKeysOfVisibleMarkers) { // Marker -> Data
            if (mHashMapToValuesOfMarkersInScene.get(key) != null) {
                // Check whether the map was clicked and not any of the markers
                if (mHashMapToValuesOfMarkersInScene.get(key).getmCoordinates().get(ParkingRepository.LATITUDE_KEY) == latLng.latitude
                        && mHashMapToValuesOfMarkersInScene.get(key).getmCoordinates().get(ParkingRepository.LONGITUDE_KEY) == latLng.latitude) {
                    showDetails(key);
                    return;
                }
            }
        }
        changeInfoLayoutVisibilityTo(View.GONE);
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        showDetails(marker);
        mSelectedPrivateParking = mHashMapToValuesOfMarkersInScene.get(marker);
        return false;
    }

    private void showDetails(Marker marker) {
        Toast.makeText(this, "Marker has been clicked!", Toast.LENGTH_SHORT).show();
        changeInfoLayoutVisibilityTo(View.VISIBLE);
        // Get references to the view elements
        TextView nameTextView = findViewById(R.id.activity_parking_map_txt_name);
        TextView priceTextView = findViewById(R.id.activity_parking_map_txt_price);
        TextView capacityTextView = findViewById(R.id.activity_parking_map_txt_capacity);
        // Get the corresponding hash map object
        PrivateParking dataOfSelectedMarker = mHashMapToValuesOfMarkersInScene.get(marker);

        // Set their texts the according the clicked marker's data
        String nameTextViewString = "Name: " + ((dataOfSelectedMarker != null) ? dataOfSelectedMarker.getmOpeningHours() : "Unavailable");
        String capacityTextViewString = "Capacity: " + ((dataOfSelectedMarker != null) ? dataOfSelectedMarker.getmCapacity() : "Unavailable");
        String priceTextViewString = "Price: " + ((dataOfSelectedMarker != null) ? dataOfSelectedMarker.getmAvailableSpaces() : "Unavailable");
        nameTextView.setText(nameTextViewString);
        capacityTextView.setText(capacityTextViewString);
        priceTextView.setText(priceTextViewString);
    }

    private void changeInfoLayoutVisibilityTo(final int visibility) {
        // Get a reference to the hidden info layout
        LinearLayout infoLayout = findViewById(R.id.activity_parking_map_li_infoLayout);
        // Check if the current visibility is already set with the given one (to avoid unnecessary actions)
        if (infoLayout.getVisibility() != visibility)
            infoLayout.setVisibility(visibility);
    }

    public void navigateToBookingActivity(View view) {
        if (mSelectedPrivateParking != null) {
            Intent intentForBooking = new Intent(ParkingMapActivity.this, ParkingBookingActivity.class);
            intentForBooking.putExtra("bookingDetails", mSelectedPrivateParking);
            //startActivity();
        } else {
            Toast.makeText(this, "Oops something went wrong!", Toast.LENGTH_SHORT).show();
        }

    }

    // Gets triggered every time we receive info about the user's location
    private LocationCallback getLocationCallback() {
        return new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);
                if (locationResult == null)
                    return;

                Location receivedLocation = locationResult.getLastLocation();
                mCurrentLatLngOfUser = new LatLng(receivedLocation.getLatitude(), receivedLocation.getLongitude());

                // We fetch the data only once, when we receive our first location update.
                // TODO: access the location from previous activity and fetch data from this
                //  activity's onCreate method in order to reduce the loading time
                if (!mDataHasBeenRetrieved) {
                    mDataHasBeenRetrieved = true;
                    fetchPrivateParking(mCurrentLatLngOfUser);
                }

                // Todo replace drawable of my location
                try {
                    Drawable drawable = ResourcesCompat.getDrawable(getResources(), R.drawable.ic_menu_gallery, null);
                    drawable.setAlpha(50); // = Opacity
                    Bitmap bitmapForMarker = drawableToBitmap(drawable);

                    Marker marker = mGoogleMap.addMarker(new MarkerOptions()
                            .title("Title")
                            .position(mCurrentLatLngOfUser)
                            .icon(BitmapDescriptorFactory.fromBitmap(bitmapForMarker)) // TODO: Replace with an actual icon
                            .snippet("Current Location!")
                            .title("Me"));

                    // Smoothly moves the camera to location
                    mGoogleMap.animateCamera(CameraUpdateFactory.newLatLng(marker.getPosition()));
                } catch (NullPointerException e) {
                    Log.d(TAG, "onLocationResult: " + e.getMessage());
                }

            }
        };
    }
}