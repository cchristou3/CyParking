package io.github.cchristou3.CyParking.view;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
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

import io.github.cchristou3.CyParking.R;
import io.github.cchristou3.CyParking.pojo.parking.PrivateParking;
import io.github.cchristou3.CyParking.repository.ParkingRepository;

public class ParkingMapActivity extends FragmentActivity implements OnMapReadyCallback {

    // Constant variables
    public static final String TAG = "cchristou3-CyParking";
    public static final int LOCATION_PERMISSION_REQUEST_CODE = 321;
    public static final long INTERVAL_TIME = 5000L;
    private static final int ZOOM_LEVEL = 16;

    // Location related variables
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private LocationCallback mLocationCallback;
    private LocationRequest mLocationRequest;

    HashMap<Marker, PrivateParking> mHashMapToValuesOfMarkersInScene = new HashMap<Marker, PrivateParking>();
    private LatLng mCurrentLatLngOfUser;
    private boolean mRegistered;
    // Activity variables
    private GoogleMap mGoogleMap;
    private boolean mDataHasBeenRetrieved;

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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parking_map);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        // set flag to false and call registerCurrentLocationUpdates method
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        mLocationCallback = getLocationCallback();

        // Initialize location based variables
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(INTERVAL_TIME);
        mRegistered = false;
        mDataHasBeenRetrieved = false;
        registerCurrentLocationUpdates();

    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        registerCurrentLocationUpdates();

        mGoogleMap = googleMap;

        // Todo Create a custom InfoWindowsAdapter
        //mGoogleMap.setInfoWindowAdapter(new CustomInfoWindowAdapter(this, mHashMapToValuesOfMarkersInScene));
        mGoogleMap.moveCamera(CameraUpdateFactory.zoomTo(ZOOM_LEVEL));
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
                        Log.d(TAG, "New parking: " + dc.getDocument().getData());
                        break;
                    case MODIFIED:
                        Log.d(TAG, "Modified parking: " + dc.getDocument().getData());
                        break;
                    case REMOVED:
                        Log.d(TAG, "Removed parking: " + dc.getDocument().getData());
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
                        double parkingLatitude = parking.getmCoordinates().get(ParkingRepository.LATITUDE_KEY);
                        double parkingLongitude = parking.getmCoordinates().get(ParkingRepository.LONGITUDE_KEY);
                        LatLng parkingLatLng = new LatLng(parkingLatitude, parkingLongitude);

                        // Add a marker to the parking's coordinates
                        Marker marker = mGoogleMap.addMarker(new MarkerOptions()
                                .title(parking.toString())
                                .position(parkingLatLng)
                                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)).snippet("Just a snippet example"));

                        // Add to hashmap to keep track of each marker's corresponding PrivateParking object
                        mHashMapToValuesOfMarkersInScene.put(marker, parking);
                    }

                }, error -> {
            // todo: inform the user
            Toast.makeText(this, error.getMessage(), Toast.LENGTH_SHORT).show();
            Log.e(TAG, "Volley error: " + error.getMessage());
        });
        requestQueue.add(requestForPrivateParking);
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

                // Todo replace drawbale of my location
                Drawable drawable = ResourcesCompat.getDrawable(getResources(), R.drawable.ic_menu_gallery, null);
                drawable.setAlpha(50); // = Opacity
                Bitmap bitmapForMarker = drawableToBitmap(drawable);

                Marker marker = mGoogleMap.addMarker(new MarkerOptions()
                        .title("Title")
                        .position(mCurrentLatLngOfUser)
                        .icon(BitmapDescriptorFactory.fromBitmap(bitmapForMarker)) // TODO: Replace with an actual icon
                        .snippet("Current Location!"));

                // Smoothly moves the camera to location
                mGoogleMap.animateCamera(CameraUpdateFactory.newLatLng(marker.getPosition()));
            }
        };
    }
}