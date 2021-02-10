package io.github.cchristou3.CyParking.ui.views.host;

import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.transition.Slide;
import androidx.transition.Transition;
import androidx.transition.TransitionManager;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;

import org.jetbrains.annotations.NotNull;

import io.github.cchristou3.CyParking.R;
import io.github.cchristou3.CyParking.data.interfaces.Navigable;
import io.github.cchristou3.CyParking.data.model.user.LoggedInUser;
import io.github.cchristou3.CyParking.databinding.ActivityMainHostBinding;
import io.github.cchristou3.CyParking.ui.helper.AlertBuilder;

import static io.github.cchristou3.CyParking.utilities.ViewUtility.updateVisibilityOfLoadingBarTo;

/**
 * <p>Main host activity of the Application.
 * Purpose: Build the drawer navigation and action bar
 * of all fragments which it is the host of.</p>
 *
 * @author Charalambos Christou
 * @version 8.0 09/02/21
 */
public class MainHostActivity extends AppCompatActivity {

    // Activity constants
    public static final String TAG = MainHostActivity.class.getName() + "UniqueTag";
    // Drawer constants
    private static final int HOME = R.id.nav_home;
    private static final int VIEW_BOOKINGS = R.id.nav_view_bookings;
    private static final int MY_ACCOUNT = R.id.nav_account;
    private static final int FEEDBACK = R.id.nav_feedback;
    private static final int TO_SETTINGS = R.id.action_settings;
    // Action bar constants
    private static final int SIGN_OUT = R.id.action_sign_out;
    private static final int SIGN_IN = R.id.action_sign_in;
    // Activity variables
    private Menu mActionBarMenu;
    private GlobalStateViewModel mGlobalStateViewModel;
    private ActivityMainHostBinding mBinding;

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
        mBinding = ActivityMainHostBinding.inflate(getLayoutInflater()); // Inflate activity's View Binding
        setContentView(mBinding.getRoot());

        setUpNavigation(); // Set up drawer and action bar

        // Initialize the activity's ViewModel instance
        mGlobalStateViewModel = new ViewModelProvider(this, new GlobalStateViewModelFactory(this))
                .get(GlobalStateViewModel.class);

        addObserversToStates(); // Attach observers to the global states

        // Acquire the user's info if already logged in
        mGlobalStateViewModel.getUserInfo(this, FirebaseAuth.getInstance().getCurrentUser());
    }

    /**
     * Clean up the activity's resources.
     * Unregister network callback.
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        mBinding = null; // Ready to be GCed
    }

    /**
     * Shows or hides the loading bar based on the given flag.
     *
     * @param shouldShowLoadingBar Indicates whether to show or hide the loading bar.
     */
    private void updateLoadingBarVisibility(boolean shouldShowLoadingBar) {
        updateVisibilityOfLoadingBarTo(mBinding.activityMainHostCpiProgressBar, shouldShowLoadingBar);
    }

    /**
     * Set up the activity's action bar and drawer menus.
     * Also, set up the application's {@link NavController}.
     */
    private void setUpNavigation() {
        // Set up ActionBar //
        setSupportActionBar(mBinding.activityMainHostTbToolbar);

        // Set up drawer //
        // Attach listeners to the drawer's items
        final int numOfDrawerItems = getDrawerMenu().size();
        for (int i = 0; i < numOfDrawerItems; i++) {
            getDrawerMenu().getItem(i).setOnMenuItemClickListener(this::onMenuItemClick);
        }
        // Set Up global NavController //
        setApplicationNavController();
    }

    /**
     * Add observers to the device's connection state,
     * the `no internet connection warning`'s visiblity state,
     * and the user's state.
     */
    private void addObserversToStates() {
        // Internet Connection state //
        mGlobalStateViewModel.getConnectionState().observe(this, isConnected -> {
            mGlobalStateViewModel
                    .updateNoConnectionWarningState(
                            isConnected ? View.GONE : View.VISIBLE
                    );
        });

        // Internet Connection Warning state //
        mGlobalStateViewModel.getNoConnectionWarningState().observe(
                this,
                this::changeNoConnectionWarningVisibilityTo); // callback

        // User state //
        mGlobalStateViewModel.getUserState().observe(this, this::updateDrawer);

        // Loading Bar state //
        mGlobalStateViewModel.getLoadingBarState().observe(this, this::updateLoadingBarVisibility);
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
        updateActionBar(mGlobalStateViewModel.getUserState().getValue());
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
                mGlobalStateViewModel.signOut();
                // Display a message to the user
                Toast.makeText(this, "You have been logged out!", Toast.LENGTH_SHORT).show();
                break;
            case SIGN_IN:
                try {
                    getActiveNavigableFragment().toAuthenticator();
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
     * Binds the {@link NavController} instance with the
     * {@link ActivityMainHostBinding#activityMainHostNvNavView} NavigationView.
     * All fragments of the application can access the
     * same instance of the NavController via the
     * NavigationView.
     *
     * @see Navigable#getNavController(FragmentActivity)
     */
    public void setApplicationNavController() {
        // Set up the NavController, bind it with the specified NavigationView
        // The fragments can access the same controller by passing in the same NavigationView
        Navigation.setViewNavController(getNavigationView(),
                NavHostFragment.findNavController(getActiveFragment()));
    }

    /**
     * Animate the {@link ActivityMainHostBinding#activityMainHostTxtNoConnectionWarning}
     * upwards or downwards based on the given flag.
     *
     * @param visibility Whether to the show (slide up) or hide (slide down) the above view.
     */
    private void changeNoConnectionWarningVisibilityTo(int visibility) {
        // Get a reference to:
        // The view we want to animate and
        // its parent ViewGroup
        View warning = mBinding.activityMainHostTxtNoConnectionWarning;
        ViewGroup parent = mBinding.activityMainHostClLayout;

        // BOTTOM indicates that we want to push the view to the bottom of its container,
        // without changing its size.
        Transition transition = new Slide(Gravity.BOTTOM);
        transition.setDuration(1000); // how long the animation will last
        // Add the id of the target view that this Transition is interested in
        transition.addTarget(warning.getId()); // In this case it is our "No connection" warning View

        // The TransitionManager starts the animation and
        // handles updating the scene between the frames
        TransitionManager.beginDelayedTransition(parent, transition);

        // If visibility set to VISIBLE, it will slide up
        // Otherwise, it will slide down.
        warning.setVisibility(visibility);
    }

    /**
     * Updates the drawer's items based on the given LoggedInUser object.
     *
     * @param currentUser The latest {@link LoggedInUser} object.
     */
    private void updateDrawer(LoggedInUser currentUser) {
        // Logged in user can
        // see/perform bookings
        if (currentUser != null) { // User is logged in
            // Enable all 'logged in user' features'
            enableLoggedInDestinations();
        } else {
            // TODO: Update drawer to only show non-loggedIn-specific actions
            getDrawerMenu().findItem(R.id.nav_view_bookings).setVisible(false);
        }
    }

    private void enableLoggedInDestinations() {
        // Show 'view bookings' in drawer
        getDrawerMenu().findItem(R.id.nav_view_bookings).setVisible(true);
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
                getActiveNavigableFragment().toHome();
                break;
            case VIEW_BOOKINGS:
                getActiveNavigableFragment().toBookings();
                break;
            case MY_ACCOUNT:
                getActiveNavigableFragment().toAccount();
                break;
            case FEEDBACK:
                getActiveNavigableFragment().toFeedback();
                break;
        }
        mBinding.activityMainHostDlDrawerLayout.close();
        return false;
    }

    /**
     * Access the current active fragment that implements
     * the {@link Navigable} interface.
     *
     * @return A reference to the current active fragment's
     * {@link Navigable} interface.
     */
    private Navigable getActiveNavigableFragment() {
        try {
            return (Navigable) getActiveFragment();
        } catch (NullPointerException | ClassCastException e) {
            AlertBuilder.showSingleActionAlert(getSupportFragmentManager(),
                    R.string.app_name,
                    R.string.unexpected_error_title,
                    null // TODO: Find counter-measure for this test case
            );
            return Navigable.empty();
        }
    }

    /**
     * Access the current active fragment.
     *
     * @return A reference to the current active fragment.
     */
    private Fragment getActiveFragment() {
        return getSupportFragmentManager().getFragments().get(0) // Access the NavHostFragment
                .getChildFragmentManager().getFragments().get(0); // Get a reference to the visible fragment
    }

    /**
     * Access the activity's {@link NavigationView} instance.
     *
     * @return A reference to the the activity's {@link NavigationView} instance.
     */
    public NavigationView getNavigationView() {
        return mBinding.activityMainHostNvNavView;
    }

    /**
     * Access the activity's Drawer {@link Menu} instance.
     *
     * @return A reference to the the activity's Drawer {@link Menu} instance.
     */
    public Menu getDrawerMenu() {
        return mBinding.activityMainHostNvNavView.getMenu();
    }
}