package io.github.cchristou3.CyParking.data.interfaces;

import android.view.View;

import androidx.navigation.Navigation;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import io.github.cchristou3.CyParking.ui.home.HomeFragment;

/**
 * Purpose: Provide a Navigation API to be implemented by all the
 * Fragments (subclasses) of the application.
 * Its methods to be used by the current active Fragment subclass
 * to navigate up to another Fragment subclass.
 * Any Fragment subclass (independent of the Navigation's back stack) can navigate
 * to up to 5 different global (Drawer/ActionBar) destinations (not top level).
 *
 * @author Charalambos Christou
 * @version 3.0 27/12/20
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
     * {@link io.github.cchristou3.CyParking.ui.user.AccountFragment}.
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
     * @param navHostView The {@link androidx.navigation.fragment.NavHostFragment} of the
     *                    caller.
     */
    default void goBack(View navHostView) {
        Navigation.findNavController(navHostView).popBackStack();
    }
}