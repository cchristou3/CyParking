package io.github.cchristou3.CyParking.ui.host;

import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.FirebaseApp;

import org.jetbrains.annotations.NotNull;

import io.github.cchristou3.CyParking.R;
import io.github.cchristou3.CyParking.data.interfaces.Navigable;
import io.github.cchristou3.CyParking.data.manager.AlertBuilder;
import io.github.cchristou3.CyParking.data.model.user.LoggedInUser;

/**
 * <p>Main host activity of the Application.
 * Purpose: Build the drawer navigation and action bar
 * of all fragments which it is the host of.</p>
 *
 * @author Charalambos Christou
 * @version 5.0 25/12/20
 */
public class MainHostActivity extends AppCompatActivity {

    // Activity constants
    public static final String USER = "User";
    public static final String OPERATOR = "Operator";
    public static final String TAG = MainHostActivity.class.getName() + "UniqueTag";
    private static final int HOME = R.id.nav_home;
    private static final int VIEW_BOOKINGS = R.id.nav_view_bookings;
    private static final int MY_ACCOUNT = R.id.nav_account;
    private static final int FEEDBACK = R.id.nav_feedback;
    private static final int TO_SETTINGS = R.id.action_settings;
    private static final int SIGN_OUT = R.id.action_sign_out;
    private static final int SIGN_IN = R.id.action_sign_in;

    // Activity variables
    private Menu mActionBarMenu;
    private Menu mDrawerMenu;
    private DrawerLayout mDrawerLayout;
    private AuthStateViewModel mAuthStateViewModel;

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

        // Set up ActionBar
        Toolbar toolbar = findViewById(R.id.fragment_main_host_tb_toolbar);
        setSupportActionBar(toolbar);

        // Set up drawer
        mDrawerLayout = findViewById(R.id.fragment_main_host_dl_drawer_layout);
        NavigationView navigationView = findViewById(R.id.fragment_main_host_nv_nav_view);

        mDrawerMenu = navigationView.getMenu();
        // Attach listeners to the drawer's items
        final int numOfDrawerItems = mDrawerMenu.size();
        for (int i = 0; i < numOfDrawerItems; i++) {
            mDrawerMenu.getItem(i).setOnMenuItemClickListener(this::onMenuItemClick);
        }

        // Set up the NavController, bind it with the specified view (fragment_main_host_nv_nav_view)
        // The fragments can access the same controller by passing in the same view (fragment_main_host_nv_nav_view)
        Navigation.setViewNavController(findViewById(R.id.fragment_main_host_nv_nav_view),
                NavHostFragment.findNavController(getSupportFragmentManager().getFragments().get(0) // Access the NavHostFragment
                        .getChildFragmentManager().getFragments().get(0)));

        // Initialize the activity's ViewModel instance
        mAuthStateViewModel = new ViewModelProvider(this, new AuthStateViewModelFactory())
                .get(AuthStateViewModel.class);

        mAuthStateViewModel.getUserState().observe(this, MainHostActivity.this::updateDrawer);

        mAuthStateViewModel.getUserInfo(this);
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
        this.mActionBarMenu = menu; // Save a reference to the menu object
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
        updateActionBar(mAuthStateViewModel.getUserState().getValue());
        return super.onPrepareOptionsMenu(menu);
    }

    /**
     * This hook is called whenever an item in your options menu is selected.
     * <p>
     * In the case of {@link #SIGN_IN}:
     * <p>
     * All fragments except ParkingBookingFragment, ViewBookingsFragment, AuthenticatorHosteeFragment, AuthenticatorFragment
     * implement the Navigate interface and provide code to its functions.
     * Thus, via polymorphism, the appropriate {@link Navigable#toAuthenticator()} gets invoked.
     *
     * @param item The menu item that was selected.
     * @return boolean Return false to allow normal menu processing to
     * proceed, true to consume it here.
     */
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case TO_SETTINGS:
                // TODO: Navigate to settings (implement settings)
                Toast.makeText(this, "Settings!", Toast.LENGTH_SHORT).show();
                break;
            case SIGN_OUT:
                mAuthStateViewModel.signOut();
                // Display a message to the user
                Toast.makeText(this, "You have been logged out!", Toast.LENGTH_SHORT).show();
                break;
            case SIGN_IN:
                try {
                    getActiveFragment().toAuthenticator();
                } catch (IllegalStateException e) {
                    Log.d(TAG, "onOptionsItemSelected: error: " + e.getMessage());
                    Toast.makeText(this, "Failed to navigate to login screen!", Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }

    /**
     * Updates the drawer's items based on the given LoggedInUser object.
     *
     * @param currentUser The latest {@link LoggedInUser} object.
     */
    private void updateDrawer(LoggedInUser currentUser) {
        // TODO: If a user signs out while being in a fragment that requires authentication
        //  then show message to user (alert) and navigate him back to home fragment (+ pop back stack)
        // Logged in user can
        // see/perform bookings
        if (currentUser != null && !(currentUser.getRoles() == null || currentUser.getRoles().isEmpty())) {
            boolean isUser = currentUser.getRoles().contains(USER);
            boolean isOperator = currentUser.getRoles().contains(OPERATOR);
            // TODO: Do underlying Drawer update
            if (!isOperator && !isUser) return;// If neither stop here.
            if (isOperator && !isUser) { // Is an operator but not a user
            } else if (isUser && !isOperator) { // Is a user but not an operator
                // Show bookings in drawer
                mDrawerMenu.findItem(R.id.nav_view_bookings).setVisible(true);
            } else { // Is both
                // Show bookings in drawer
                mDrawerMenu.findItem(R.id.nav_view_bookings).setVisible(true);
            }


        } else {
            // TODO: Update drawer to only show non-loggedIn-specific actions
            // View Map
            // User cannot book -> thus cannot see their bookings
            // Remove Bookings from drawer
            mDrawerMenu.findItem(R.id.nav_view_bookings).setVisible(false);
        }
    }

    /**
     * Updates the action bar's items based on the given LoggedInUser object.
     *
     * @param currentUser The latest {@link LoggedInUser} object.
     */
    private void updateActionBar(LoggedInUser currentUser) {
        if (currentUser != null) {
            // User is logged in!
            // Hide sign in option
            mActionBarMenu.findItem(R.id.action_sign_in).setVisible(false);
            // Show log out option
            mActionBarMenu.findItem(R.id.action_sign_out).setVisible(true);
        } else {
            // User not logged in
            // Hide log out option
            mActionBarMenu.findItem(R.id.action_sign_out).setVisible(false);
            // Show sign option
            mActionBarMenu.findItem(R.id.action_sign_in).setVisible(true);
        }
    }

    /**
     * On click listener for all menu items.
     * Based on the item's id perform its corresponding
     * navigation action and close the drawer.
     *
     * @param item The drawer item that got clicked.
     * @return By default false.
     */
    private boolean onMenuItemClick(@NotNull MenuItem item) {
        // Access the visible view
        final int menuItemId = item.getItemId();
        switch (menuItemId) {
            case HOME:
                getActiveFragment().toHome();
                break;
            case VIEW_BOOKINGS:
                getActiveFragment().toBookings();
                break;
            case MY_ACCOUNT:
                getActiveFragment().toAccount();
                break;
            case FEEDBACK:
                getActiveFragment().toFeedback();
                break;
        }
        mDrawerLayout.close();
        return false;
    }

    /**
     * Access the current active fragment.
     *
     * @return A reference to the current active fragment.
     */
    private Navigable getActiveFragment() {
        try {
            Fragment activeFragment = getSupportFragmentManager().getFragments().get(0) // Access the NavHostFragment
                    .getChildFragmentManager().getFragments().get(0); // Get a reference to the visible fragment
            return (Navigable) activeFragment;
        } catch (NullPointerException | ClassCastException e) {
            AlertBuilder.showAlert(this,
                    R.string.app_name,
                    R.string.unexpected_error_title,
                    android.R.string.ok,
                    null // TODO: Find counter-measure for this test case
            );
            return Navigable.empty();
        }
    }
}