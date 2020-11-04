package io.github.cchristou3.CyParking.view.ui.login;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import io.github.cchristou3.CyParking.R;

/**
 * Purpose: <p>A [FragmentPagerAdapter] that returns a fragment corresponding to
 * one of the sections/tabs/pages.</p>
 *
 * @author Charalambos Christou
 * @version 1.0 1/11/20
 */
public class AuthenticationAdapter extends FragmentStateAdapter {

    private final LoginViewModel mLoginViewModel;

    /**
     * AuthenticationAdapter's public Constructor
     *
     * @param fragmentActivity The FragmentActivity which hosts the tabs.
     * @param mLoginViewModel  The LoginViewModel which will be shared by all tabs.
     */
    public AuthenticationAdapter(FragmentActivity fragmentActivity, LoginViewModel mLoginViewModel) {
        super(fragmentActivity);
        this.mLoginViewModel = mLoginViewModel;
    }

    /**
     * @param position The position of the fragment in the tabs.
     * @return an instance of LoginFragment.
     */
    @NonNull
    @Override
    public Fragment createFragment(int position) {
        return (position == 0) ?
                LoginFragment.newInstance(true, R.layout.fragment_login, mLoginViewModel) :
                LoginFragment.newInstance(false, R.layout.fragment_login, mLoginViewModel);
    }

    /**
     * @return the number of tabs.
     */
    @Override
    public int getItemCount() {
        return 2;
    }

}