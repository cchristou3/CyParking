package io.github.cchristou3.CyParking.ui.views.user.login;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import org.jetbrains.annotations.NotNull;

import io.github.cchristou3.CyParking.R;
import io.github.cchristou3.CyParking.data.interfaces.Navigable;
import io.github.cchristou3.CyParking.databinding.FragmentAuthenticationBinding;
import io.github.cchristou3.CyParking.ui.helper.TabHelper;
import io.github.cchristou3.CyParking.ui.views.home.HomeFragment;
import io.github.cchristou3.CyParking.ui.views.user.account.AccountFragment;

/**
 * Purpose: <p>To host the two tabs (Sign in, Sign up) </p>
 * Both tabs, use this fragment as their ViewModelStoreOwner to both
 * acquire the same instance of {@link AuthenticatorViewModel}.
 *
 * @author Charalambos Christou
 * @version 6.0 22/04/21
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

        final TabHelper tabHelper = new TabHelper();

        new TabLayoutMediator(tabLayout, viewPager,
                (tab, position) -> {
                    tabHelper.initializeTab(requireContext(), tab, position);
                }
        ).attach();

        tabLayout.addOnTabSelectedListener(tabHelper.getOnTabSelectedListener(requireContext()));

        checkIfUserWantsToRegister(tabLayout);

        return binding.getRoot();
    }

    /**
     * Checks whether the user navigated to the {@link AuthenticatorFragment}
     * to register. If yes, then he is navigated directly to the "sign up" tab.
     *
     * @param tabLayout The tab layout to use.
     * @see AccountFragment#toAuthenticator()
     */
    private void checkIfUserWantsToRegister(TabLayout tabLayout) {
        if (getArguments() != null && getArguments().getBoolean(getString(R.string.sign_up_arg))) {
            tabLayout.selectTab(tabLayout
                            .getTabAt(1), // Sign up tab
                    true);
        }
    }


    /**
     * Navigates from the current Fragment subclass to the
     * {@link io.github.cchristou3.CyParking.ui.views.user.login.AuthenticatorFragment}.
     */
    @Override
    public void toAuthenticator() {
        // The user is already in this fragment. Thus, no need to implement this method.
    }

    /**
     * Navigates from the current Fragment subclass to the
     * {@link io.github.cchristou3.CyParking.ui.views.parking.slots.viewBooking.ViewBookingsFragment}.
     */
    @Override
    public void toBookings() {
        getNavController(requireActivity())
                .navigate(
                        AuthenticatorFragmentDirections
                                .actionNavAuthenticatorFragmentToNavViewBookings()
                );
    }

    /**
     * Navigates from the current Fragment subclass to the
     * {@link AccountFragment}.
     */
    @Override
    public void toAccount() {
        getNavController(requireActivity())
                .navigate(
                        AuthenticatorFragmentDirections.actionNavAuthenticatorFragmentToNavAccount()
                );
    }

    /**
     * Navigates from the current Fragment subclass to the
     * {@link io.github.cchristou3.CyParking.ui.views.user.feedback.FeedbackFragment}.
     */
    @Override
    public void toFeedback() {
        getNavController(requireActivity())
                .navigate(
                        AuthenticatorFragmentDirections.actionNavAuthenticatorFragmentToNavFeedback()
                );
    }

    /**
     * Navigates from the current Fragment subclass to the
     * {@link HomeFragment}.
     */
    @Override
    public void toHome() {
        getNavController(requireActivity())
                .navigate(
                        AuthenticatorFragmentDirections.actionNavAuthenticatorFragmentToNavHome()
                );
    }
}