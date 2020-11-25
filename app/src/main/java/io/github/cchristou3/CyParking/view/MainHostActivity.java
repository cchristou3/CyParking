package io.github.cchristou3.CyParking.view;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.jetbrains.annotations.NotNull;

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
    private Menu mMenu;

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
        setContentView(R.layout.fragment_main_host);

        Toolbar toolbar = findViewById(R.id.fragment_main_host_tb_toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.fragment_main_host_nv_nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_view_bookings, R.id.nav_account)
                .setOpenableLayout(drawer)
                .build();
        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_main_host_fcv_nav_host_fragment);
        NavController navController = navHostFragment.getNavController();
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);
        FirebaseAuth.getInstance().addAuthStateListener(firebaseAuth -> {
//                updatedMenu(firebaseAuth.getCurrentUser());
//                this.onPrepareOptionsMenu(mMenu);
//                this.invalidateOptionsMenu();
        });
    }

    private void updatedMenu(FirebaseUser user) {
        if (mMenu == null) return;
        if (user != null) { // User is logged in!
            mMenu.removeItem(R.id.action_sign_in);
            mMenu.add(1, R.id.action_sign_out, 1, "Sign out!");
        } else {
            mMenu.add(1, R.id.action_sign_in, 1, "Sign in!");
            mMenu.removeItem(R.id.action_sign_out);
//            mMenu.setGroupEnabled(R.id.action_sign_in, true);
//            mMenu.setGroupEnabled(R.id.action_sign_out, false);
        }
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
        this.mMenu = menu; // Save a reference to the menu object
        return true;
    }

    /**
     * Prepare the Screen's standard options menu to be displayed.
     * If the user is logged in, the sign out option is shown.
     * Otherwise, the sign in option is shown.
     *
     * @param menu The options menu as last shown or first initialized by
     *             onCreateOptionsMenu().
     * @return You must return true for the menu to be displayed;
     * if you return false it will not be shown.
     * @see #onCreateOptionsMenu
     */
    @Override
    public boolean onPrepareOptionsMenu(@NotNull Menu menu) {
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            // User is logged in!
            // Hide sign in option
            mMenu.findItem(R.id.action_sign_in).setVisible(false);
            // Show log out option
            mMenu.findItem(R.id.action_sign_out).setVisible(true);
        } else {
            // User not logged in
            // Hide log out option
            mMenu.findItem(R.id.action_sign_out).setVisible(false);
            // Show sign option
            mMenu.findItem(R.id.action_sign_in).setVisible(true);
        }
        return super.onPrepareOptionsMenu(menu);
    }

    /**
     * This hook is called whenever an item in your options menu is selected.
     *
     * @param item The menu item that was selected.
     * @return boolean Return false to allow normal menu processing to
     * proceed, true to consume it here.
     */
    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                // TODO: Show navigate to settings
                Toast.makeText(this, "Settings!", Toast.LENGTH_SHORT).show();
                break;
            case R.id.action_sign_out:
                FirebaseAuth.getInstance().signOut();
                Toast.makeText(this, "You have been logged out!", Toast.LENGTH_SHORT).show();
                break;
            case R.id.action_sign_in:
                try {
                    Navigation.findNavController(getSupportFragmentManager().getFragments().get(0).requireView()).navigate(R.id.to_authentication_fragment);
                } catch (IllegalStateException e) {
                    Toast.makeText(this, "Failed to navigate to login screen!", Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
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