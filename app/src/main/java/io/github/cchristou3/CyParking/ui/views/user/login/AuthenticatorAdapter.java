package io.github.cchristou3.CyParking.ui.views.user.login;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

/**
 * Purpose: <p>A [FragmentPagerAdapter] that returns a fragment corresponding to
 * one of the sections/tabs/pages.</p>
 *
 * @author Charalambos Christou
 * @version 3.0 07/11/20
 */
public class AuthenticatorAdapter extends FragmentStateAdapter {

    // Adapter's constants
    public static final short LOGIN_TAB = 0;
    public static final short REGISTRATION_TAB = 1;

    /**
     * AuthenticationAdapter's public Constructor
     *
     * @param fragmentManager The host's fragment manager
     * @param lifecycle       The current lifecycle
     */
    public AuthenticatorAdapter(FragmentManager fragmentManager, Lifecycle lifecycle) {
        super(fragmentManager, lifecycle);
    }

    /**
     * @param position The position of the fragment in the tabs.
     * @return an instance of AuthenticatorHosteeFragment.
     */
    @NonNull
    @Override
    public Fragment createFragment(int position) {
        return (position == 0) ?
                AuthenticatorHosteeFragment.newInstance(LOGIN_TAB) :
                AuthenticatorHosteeFragment.newInstance(REGISTRATION_TAB);
    }

    /**
     * @return the number of tabs.
     */
    @Override
    public int getItemCount() {
        return 2;
    }

}