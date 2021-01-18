package io.github.cchristou3.CyParking.data.interfaces;

import androidx.fragment.app.FragmentActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import io.github.cchristou3.CyParking.ui.home.HomeFragment;
import io.github.cchristou3.CyParking.ui.host.MainHostActivity;
import io.github.cchristou3.CyParking.ui.user.account.AccountFragment;

/**
 * Purpose: Provide a Navigation API to be implemented by all the
 * Fragments (subclasses) of the application.
 * Its methods to be used by the current active Fragment subclass
 * to navigate to another Fragment subclass.
 * Any Fragment subclass (independent of the Navigation's back stack) can navigate
 * to up to 5 different global (Drawer/ActionBar) destinations (not top level).
 * Simultaneously, the interface provides a default method ({@link #goBack(FragmentActivity)})
 * to allow the fragments to pop the navigation BackStack - to return to previous
 * fragment instance.
 * Lastly, it allows access to a {@link NavController} via its
 * default method {@link #getNavController(FragmentActivity)}.
 *
 * @author Charalambos Christou
 * @version 4.0 18/01/21
 */
public interface Navigable {

    /**
     * Creates an instance of the Navigable interface
     * with no method implementations.
     *
     * @return An non-logic Navigable instance.
     */
    @NotNull
    @Contract(value = " -> new", pure = true)
    static Navigable empty() {
        return new Navigable() {
            public void toAuthenticator() { /* ignore - empty */ }

            public void toBookings() { /* ignore - empty */ }

            public void toAccount() { /* ignore - empty */ }

            public void toFeedback() { /* ignore - empty */ }

            public void toHome() { /* ignore - empty */ }
        };
    }

    /**
     * Navigates from the current Fragment subclass to the
     * {@link io.github.cchristou3.CyParking.ui.user.login.AuthenticatorFragment}.
     */
    void toAuthenticator();

    /**
     * Navigates from the current Fragment subclass to the
     * {@link io.github.cchristou3.CyParking.ui.parking.slots.viewBooking.ViewBookingsFragment}.
     */
    void toBookings();

    /**
     * Navigates from the current Fragment subclass to the
     * {@link AccountFragment}.
     */
    void toAccount();

    /**
     * Navigates from the current Fragment subclass to the
     * {@link io.github.cchristou3.CyParking.ui.user.feedback.FeedbackFragment}.
     */
    void toFeedback();

    /**
     * Navigates from the current Fragment subclass to the
     * {@link HomeFragment}.
     */
    void toHome();

    /**
     * Navigates to the latest destination of the BackStack.
     *
     * @param activity The activity that contains the
     *                 {@link androidx.navigation.fragment.NavHostFragment}.
     */
    default void goBack(@NotNull FragmentActivity activity) {
        getNavController(activity).popBackStack();
    }

    /**
     * Provides a convenient way of accessing the
     * {@link NavController} instance of the application.
     *
     * @param activity The parent {@link FragmentActivity}.
     * @return The NavController associated with the NavigationView
     * of id NAV_VIEW_ID.
     * @see MainHostActivity#NAV_VIEW_ID
     * @see MainHostActivity#setApplicationNavController()
     */
    default NavController getNavController(@NotNull FragmentActivity activity) {
        return Navigation.findNavController(
                activity.findViewById(MainHostActivity.NAV_VIEW_ID));
    }
}