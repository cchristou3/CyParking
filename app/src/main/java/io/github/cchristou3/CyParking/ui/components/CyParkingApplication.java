package io.github.cchristou3.CyParking.ui.components;

import android.app.Application;

import com.google.firebase.FirebaseApp;

// TODO: 07/02/2021 Document it
public class CyParkingApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        FirebaseApp.initializeApp(this.getApplicationContext()); // Initialize Firebase
    }
}
