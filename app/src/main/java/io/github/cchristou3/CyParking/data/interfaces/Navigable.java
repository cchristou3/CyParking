package io.github.cchristou3.CyParking.data.interfaces;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

/**
 * Purpose: Provide a Navigation API to be implemented by all the
 * Fragments (subclasses) of the application.
 * Its methods to be used by the current active Fragment subclass
 * to navigate up to another Fragment subclass.
 * Any Fragment subclass (independent of the Navigation's back stack) can navigate
 * to up to 5 different global (Drawer/ActionBar) destinations (not top level).
 *
 * @author Charalambos Christou
 * @version 2.0 21/12/20
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
            public void toAuthenticator() {
            }

            public void toBookings() {
            }

            public void toAccount() {
            }

            public void toFeedback() {
            }

            public void toHome() {
            }
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
     * {@link io.github.cchristou3.CyParking.ui.HomeFragment}.
     */
    void toHome();
}