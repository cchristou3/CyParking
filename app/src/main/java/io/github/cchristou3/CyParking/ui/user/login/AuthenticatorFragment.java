package io.github.cchristou3.CyParking.ui.user.login;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import org.jetbrains.annotations.NotNull;

import io.github.cchristou3.CyParking.R;
import io.github.cchristou3.CyParking.data.interfaces.Navigable;
import io.github.cchristou3.CyParking.databinding.FragmentAuthenticationBinding;
import io.github.cchristou3.CyParking.ui.home.HomeFragment;
import io.github.cchristou3.CyParking.ui.user.account.AccountFragment;

/**
 * Purpose: <p>To host the two tabs (Sign in, Sign up) </p>
 * Both tabs, use this fragment as their ViewModelStoreOwner to both
 * acquire the same instance of {@link AuthenticatorViewModel}.
 *
 * @author Charalambos Christou
 * @version 3.0 15/12/20
 */
public class AuthenticatorFragment extends Fragment implements Navigable {

    public AuthenticatorFragment() {/* Required empty public constructor */}

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        FragmentAuthenticationBinding binding = FragmentAuthenticationBinding.inflate(inflater);
        AuthenticatorAdapter sectionsPagerAdapter = new AuthenticatorAdapter(getChildFragmentManager(),
                getLifecycle());
        final ViewPager2 viewPager = binding.fragmentAuthenticationVp2ViewPager2;
        viewPager.setAdapter(sectionsPagerAdapter);
        final TabLayout tabLayout = binding.fragmentAuthenticationTlTabs;
        new TabLayoutMediator(tabLayout, viewPager,
                (tab, position) -> {
                    tab.setText((position == 0) ? getString(R.string.sign_in) : getString(R.string.sign_up));
                    tab.setTabLabelVisibility(TabLayout.TAB_LABEL_VISIBILITY_LABELED);
                }
        ).attach();
        return binding.getRoot();
    }


    /**
     * Navigates from the current Fragment subclass to the
     * {@link io.github.cchristou3.CyParking.ui.user.login.AuthenticatorFragment}.
     */
    @Override
    public void toAuthenticator() {
        // The user is already in this fragment. Thus, no need to implement this method.
    }

    /**
     * Navigates from the current Fragment subclass to the
     * {@link io.github.cchristou3.CyParking.ui.parking.slots.viewBooking.ViewBookingsFragment}.
     */
    @Override
    public void toBookings() {
        Navigation.findNavController(getActivity().findViewById(R.id.fragment_main_host_nv_nav_view))
                .navigate(R.id.action_nav_authenticator_fragment_to_nav_view_bookings);
    }

    /**
     * Navigates from the current Fragment subclass to the
     * {@link AccountFragment}.
     */
    @Override
    public void toAccount() {
        Navigation.findNavController(getActivity().findViewById(R.id.fragment_main_host_nv_nav_view))
                .navigate(R.id.action_nav_authenticator_fragment_to_nav_account);
    }

    /**
     * Navigates from the current Fragment subclass to the
     * {@link io.github.cchristou3.CyParking.ui.user.feedback.FeedbackFragment}.
     */
    @Override
    public void toFeedback() {
        Navigation.findNavController(getActivity().findViewById(R.id.fragment_main_host_nv_nav_view))
                .navigate(R.id.action_nav_authenticator_fragment_to_nav_feedback);
    }

    /**
     * Navigates from the current Fragment subclass to the
     * {@link HomeFragment}.
     */
    @Override
    public void toHome() {
        Navigation.findNavController(getActivity().findViewById(R.id.fragment_main_host_nv_nav_view))
                .navigate(R.id.action_nav_authenticator_fragment_to_nav_home);
    }
}