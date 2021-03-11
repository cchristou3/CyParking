package io.github.cchristou3.CyParking.ui.application;

import com.google.firebase.FirebaseApp;

import io.github.cchristou3.CyParking.StripeApp;

// TODO: 07/02/2021 Document it
public class CyParkingApplication extends StripeApp {

    @Override
    public void onCreate() {
        super.onCreate();
        FirebaseApp.initializeApp(this.getApplicationContext()); // Initialize Firebase
    }
}
