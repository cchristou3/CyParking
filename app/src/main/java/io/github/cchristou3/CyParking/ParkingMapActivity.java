package io.github.cchristou3.CyParking;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.FragmentActivity;

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


    private GoogleMap mGoogleMap;
    private LatLng mCurrentLatLngOfUser;
    private boolean mRegistered;

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
        mLocationCallback = new LocationCallback(){
            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);
                if (locationResult == null)
                    return;

                Location receivedLocation = locationResult.getLastLocation();
                mCurrentLatLngOfUser = new LatLng(receivedLocation.getLatitude(),receivedLocation.getLongitude());

                // Todo replace drawbale of my location
                Drawable drawable =  ResourcesCompat.getDrawable(getResources(), R.drawable.ic_menu_gallery,null);
                drawable.setAlpha(50); // = Opacity
                Bitmap bitmapForMarker = drawableToBitmap(drawable);


                Marker marker = mGoogleMap.addMarker(new MarkerOptions()
                        .title("Title")
                        .position(mCurrentLatLngOfUser)
                        .icon(BitmapDescriptorFactory.fromBitmap(bitmapForMarker))
                        .snippet("Current Location!"));

                // Smoothly moves the camera to location
                mGoogleMap.animateCamera(CameraUpdateFactory.newLatLng(marker.getPosition()));
            }
        };
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(INTERVAL_TIME);
        mRegistered = false;
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
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {

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
                Toast.makeText(this, "Fetching your location...", Toast.LENGTH_SHORT).show();
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

    public static Bitmap drawableToBitmap (Drawable drawable) {
        Bitmap bitmap = null;

        if (drawable instanceof BitmapDrawable) {
            BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
            if(bitmapDrawable.getBitmap() != null) {
                return bitmapDrawable.getBitmap();
            }
        }

        if(drawable.getIntrinsicWidth() <= 0 || drawable.getIntrinsicHeight() <= 0) {
            bitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888); // Single color bitmap will be created of 1x1 pixel
        } else {
            bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        }

        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);
        return bitmap;
    }
}