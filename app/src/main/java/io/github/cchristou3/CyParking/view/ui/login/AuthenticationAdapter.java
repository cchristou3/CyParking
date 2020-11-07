package io.github.cchristou3.CyParking.view.ui.login;

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
public class AuthenticationAdapter extends FragmentStateAdapter {

    // Adapter's constants
    public static final short LOGIN_PAGE = 0;
    public static final short REGISTRATION_PAGE = 1;

    /**
     * AuthenticationAdapter's public Constructor
     *
     * @param fragmentManager The host's fragment manager
     * @param lifecycle       The current lifecycle
     */
    public AuthenticationAdapter(FragmentManager fragmentManager, Lifecycle lifecycle) {
        super(fragmentManager, lifecycle);
    }

    /**
     * @param position The position of the fragment in the tabs.
     * @return an instance of LoginFragment.
     */
    @NonNull
    @Override
    public Fragment createFragment(int position) {
        return (position == 0) ?
                LoginFragment.newInstance(LOGIN_PAGE) :
                LoginFragment.newInstance(REGISTRATION_PAGE);
    }

    /**
     * @return the number of tabs.
     */
    @Override
    public int getItemCount() {
        return 2;
    }


}