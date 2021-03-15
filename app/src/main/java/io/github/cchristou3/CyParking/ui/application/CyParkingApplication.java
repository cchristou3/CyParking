package io.github.cchristou3.CyParking.ui.application;

import com.google.firebase.FirebaseApp;

import io.github.cchristou3.CyParking.StripeApp;

/**
 * Purpose: Execute crucial configurations calls need by the application.
 *
 * @author Charalambos Christou
 * @version 1.0 12/03/21
 */
public class CyParkingApplication extends StripeApp {

    /**
     * First method to be called when the application loads.
     */
    @Override
    public void onCreate() {
        super.onCreate();
        // Initialize the Firebase configs
        FirebaseApp.initializeApp(this.getApplicationContext()); // Initialize Firebase
    }
}
