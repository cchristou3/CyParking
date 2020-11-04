package io.github.cchristou3.CyParking.view;

import android.os.Bundle;
import android.view.Menu;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.FirebaseApp;

import io.github.cchristou3.CyParking.R;

/**
 * <p>Main host activity of the Application.
 * Purpose: Build the drawer navigation and action bar
 * of all fragments which it is the host of.</p>
 *
 * @author Charalambos Christou
 * @version 1.0 30/10/20
 */
public class MainHostActivity extends AppCompatActivity {

    // Activity variables
    private AppBarConfiguration mAppBarConfiguration;

    /**
     * Initialises the activity.
     * Builds the activity's Toolbar and Drawer navigation which
     * all fragments will use.
     *
     * @param savedInstanceState A bundle which contains info about previously stored data
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FirebaseApp.initializeApp(this);
        setContentView(R.layout.fragment_main_hotst);

        Toolbar toolbar = findViewById(R.id.fragment_main_host_tb_toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.fragment_main_host_nv_nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_view_bookings, R.id.nav_slideshow)
                .setOpenableLayout(drawer)
                .build();
        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_main_host_fcv_nav_host_fragment);
        NavController navController = navHostFragment.getNavController();
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

        //ParkingRepository.addDummyParkingData(); //TODO: Remove hardcoded data

    }

    /**
     * Inflates items on the activity's action bar
     *
     * @param menu A Menu object
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    /**
     * This method is called whenever the user chooses to navigate Up within your application's
     * activity hierarchy from the Drawer.
     *
     * @return true if Up navigation completed successfully and this Activity was finished,
     * false otherwise.
     */
    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.fragment_main_host_fcv_nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }
}