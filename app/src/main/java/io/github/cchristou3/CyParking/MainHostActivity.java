package io.github.cchristou3.CyParking;

import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import io.github.cchristou3.CyParking.data.interfaces.Navigable;
import io.github.cchristou3.CyParking.data.manager.AuthObserver;
import io.github.cchristou3.CyParking.data.manager.SharedPreferencesManager;
import io.github.cchristou3.CyParking.data.pojo.user.User;
import io.github.cchristou3.CyParking.data.repository.AuthenticatorRepository;

/**
 * <p>Main host activity of the Application.
 * Purpose: Build the drawer navigation and action bar
 * of all fragments which it is the host of.</p>
 *
 * @author Charalambos Christou
 * @version 3.0 15/12/20
 * <p>
 * TODO: Solve race condition issue between
 * the {@link io.github.cchristou3.CyParking.ui.user.login.AuthenticatorViewModel#login && #register} methods
 * and
 * the {@link MainHostActivity} -> Auth listener
 */
public class MainHostActivity extends AppCompatActivity {

    // Activity constants
    public static final String USER = "User";
    public static final String OPERATOR = "Operator";
    public static final String TAG = MainHostActivity.class.getName() + "Unique_Tag";
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
        Log.d(TAG, "onCreate: Invoked!");
        FirebaseApp.initializeApp(this);
        setContentView(R.layout.fragment_main_host);

        Toolbar toolbar = findViewById(R.id.fragment_main_host_tb_toolbar);
        setSupportActionBar(toolbar);

        mDrawerLayout = findViewById(R.id.fragment_main_host_dl_drawer_layout);
        NavigationView navigationView = findViewById(R.id.fragment_main_host_nv_nav_view);

        mDrawerMenu = navigationView.getMenu();
        final int numOfDrawerItems = mDrawerMenu.size();
        for (int i = 0; i < numOfDrawerItems; i++) {
            mDrawerMenu.getItem(i).setOnMenuItemClickListener(this::onMenuItemClick);
        }

        Navigation.setViewNavController(findViewById(R.id.fragment_main_host_nv_nav_view),
                NavHostFragment.findNavController(getSupportFragmentManager().getFragments().get(0) // Access the NavHostFragment
                        .getChildFragmentManager().getFragments().get(0)));

        // TODO: Solution to the Auth Race condition
        //  ViewModel for Main -> LiveData for a User object (User/ FirebaseUser)
        //  Initialize it with FireAuth.getInstance().getCurrentUser
        //  Fragments can access it and when Auth changes, they update it.
        //  (Add more details and design concerning the UI updates (Drawer/ActionBar and
        //  fragment related UI updates))


        AuthObserver.newInstance(currentFirebaseUser -> {
            Log.d(TAG, "onCreate AuthObserver: invoked!");
            // UpdateDrawer
            updateDrawer(currentFirebaseUser);

            // TODO: Do UI related changes here
        }).registerObserver(getLifecycle());
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "onStart: Invoked");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume: Invoked");
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
        updateActionBar(FirebaseAuth.getInstance().getCurrentUser());
        return super.onPrepareOptionsMenu(menu);
    }

    /**
     * This hook is called whenever an item in your options menu is selected.
     *
     * @param item The menu item that was selected.
     * @return boolean Return false to allow normal menu processing to
     * proceed, true to consume it here.
     */
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case TO_SETTINGS:
                // TODO: Show navigate to settings
                Toast.makeText(this, "Settings!", Toast.LENGTH_SHORT).show();
                break;
            case SIGN_OUT:
                FirebaseAuth.getInstance().getCurrentUser().delete();
                FirebaseAuth.getInstance().signOut();
                Toast.makeText(this, "You have been logged out!", Toast.LENGTH_SHORT).show();
                break;
            case SIGN_IN:
                try {
                    /*
                     * All fragments but ParkingBookingFragment, ViewBookingsFragment, AuthenticatorHosteeFragment, AuthenticatorFragment
                     * implement the Navigate interface and provide code to its functions.
                     * Thus, via polymorphism, the appropriate {@link Navigate#toAuthenticator()} gets invoked.
                     */
                    getActiveFragment()
                            .toAuthenticator();

                } catch (IllegalStateException e) {
                    Toast.makeText(this, "Failed to navigate to login screen!", Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }

    private void updateDrawer(FirebaseUser currentUser) {
        // TODO: If a user signs out while being in a fragment that requires authentication
        //  then show message to user (alert) and navigate him back to home fragment (+ pop back stack)
        Log.d(TAG, "updateDrawer: User is " + currentUser);
        if (currentUser != null) {
            // Access the user's roles via the Preference manager
            // Get a reference to the application's SharedPreferences instance
            SharedPreferencesManager preferencesManager = new SharedPreferencesManager(this);

            List<String> setOfRoles = preferencesManager.getKey(currentUser.getUid());

            // Logged in user can
            // see/perform bookings
            // TODO: continue with the use cases

            if (setOfRoles == null || setOfRoles.isEmpty()) {
                // Query the roles via the cloud database
                AuthenticatorRepository.getInstance(FirebaseAuth.getInstance()).getUser(currentUser)
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                final User user = task.getResult().toObject(User.class);
                                if (user == null) return;
                                boolean isUser = user.getRoles().contains(USER);
                                boolean isOperator = user.getRoles().contains(OPERATOR);
                                Log.d(TAG, "updateDrawer: Fetched from database: User: " + isUser + ", Operator: " + isOperator);
                                // TODO: Do underlying Drawer update
                                if (isUser)
                                    mDrawerMenu.findItem(R.id.nav_view_bookings).setVisible(true);
                            } else {
                                Log.d(TAG, "updateDrawer: Fetched from database failed!");
                            }
                        });
            } else {
                boolean isUser = setOfRoles.contains(USER);
                boolean isOperator = setOfRoles.contains(OPERATOR);
                // TODO: Do underlying Drawer update
                Log.d(TAG, "updateDrawer: Fetched locally: User: " + isUser + ", Operator: " + isOperator);
                if (isUser) mDrawerMenu.findItem(R.id.nav_view_bookings).setVisible(true);
            }
        } else {
            // TODO: Update drawer to only show user-specific actions
            // User cannot book -> thus cannot see their bookings
            // Remove Bookings from drawer
            mDrawerMenu.findItem(R.id.nav_view_bookings).setVisible(false);
        }
    }

    private void updateActionBar(FirebaseUser currentUser) {
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

    private Navigable getActiveFragment() {
        try {
            Fragment activeFragment = getSupportFragmentManager().getFragments().get(0) // Access the NavHostFragment
                    .getChildFragmentManager().getFragments().get(0); // Get a reference to the visible fragment
            return (Navigable) activeFragment;
        } catch (NullPointerException | ClassCastException e) {
            new AlertDialog.Builder(this)
                    .setMessage(R.string.unexpected_error_info)
                    .setNeutralButton(android.R.string.ok, (dialog, which) -> {
                        // TODO: Find counter-measure for this test case
                    }).create().show();
            return Navigable.empty();
        }
    }
}