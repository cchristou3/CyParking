package io.github.cchristou3.CyParking.view.data.interfaces;

/**
 * Purpose: Provide a Navigation API to be implemented by all the
 * Fragments (subclasses) of the application.
 * Its methods to be used by the current active Fragment subclass
 * to navigate up to another Fragment subclass.
 * Any Fragment subclass (independent of the Navigation's back stack) can navigate
 * to up to 5 different global (Drawer/ActionBar) destinations (not top level).
 *
 * @author Charalambos Christou
 * @version 1.0 14/12/20
 */
public interface Navigate {
    /**
     * Navigates from the current Fragment subclass to the
     * {@link io.github.cchristou3.CyParking.view.ui.user.login.AuthenticatorFragment}.
     */
    void toAuthenticator();

    /**
     * Navigates from the current Fragment subclass to the
     * {@link io.github.cchristou3.CyParking.view.ui.parking.slots.viewBooking.ViewBookingsFragment}.
     */
    void toBookings();

    /**
     * Navigates from the current Fragment subclass to the
     * {@link io.github.cchristou3.CyParking.view.ui.user.AccountFragment}.
     */
    void toAccount();

    /**
     * Navigates from the current Fragment subclass to the
     * {@link io.github.cchristou3.CyParking.view.ui.user.feedback.FeedbackFragment}.
     */
    void toFeedback();

    /**
     * Navigates from the current Fragment subclass to the
     * {@link io.github.cchristou3.CyParking.view.ui.HomeFragment}.
     */
    void toHome();
}