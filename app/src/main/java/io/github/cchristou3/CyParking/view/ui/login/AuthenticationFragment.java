package io.github.cchristou3.CyParking.view.ui.login;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import io.github.cchristou3.CyParking.R;

/**
 * Purpose: <p>To host the two tabs (Sign in, Sign up) and provide to both the same instance of</p>
 * LoginViewModel.
 *
 * @author Charalambos Christou
 * @version 1.0 1/11/20
 */
public class AuthenticationFragment extends Fragment {

    public AuthenticationFragment() {/* Required empty public constructor */}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View mView = inflater.inflate(R.layout.fragment_authentication, container, false);

        LoginViewModel sharedLoginViewModel = new ViewModelProvider(this, new LoginViewModelFactory())
                .get(LoginViewModel.class);

        // Inflate the layout for this fragment
        AuthenticationAdapter sectionsPagerAdapter = new AuthenticationAdapter(requireActivity(),
                sharedLoginViewModel);
        ViewPager2 viewPager = mView.findViewById(R.id.fragment_authentication_vp2_view_pager_2);
        viewPager.setAdapter(sectionsPagerAdapter);
        TabLayout tabLayout = mView.findViewById(R.id.fragment_authentication_tl_tabs);

        new TabLayoutMediator(tabLayout, viewPager,
                (tab, position) -> {
                    tab.setText((position == 0) ? "Sign in" : "Sign up");
                    tab.setTabLabelVisibility(TabLayout.TAB_LABEL_VISIBILITY_LABELED);
                }
        ).attach();
        return mView;

    }

}