package io.github.cchristou3.CyParking.ui.user.account;

import android.content.res.Configuration;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;

import org.jetbrains.annotations.NotNull;

import io.github.cchristou3.CyParking.R;
import io.github.cchristou3.CyParking.data.interfaces.Navigable;
import io.github.cchristou3.CyParking.data.manager.AlertBuilder;
import io.github.cchristou3.CyParking.data.model.user.LoggedInUser;
import io.github.cchristou3.CyParking.databinding.FragmentAccountBinding;
import io.github.cchristou3.CyParking.ui.home.HomeFragment;
import io.github.cchristou3.CyParking.ui.host.AuthStateViewModel;
import io.github.cchristou3.CyParking.ui.user.account.update.UpdateAccountDialog;
import io.github.cchristou3.CyParking.ui.user.login.AuthenticatorHosteeFragment;

/**
 * Purpose: <p>Going to get replaced with another Navigation option</p>
 *
 * @author Charalambos Christou
 * @version 3.0 28/12/20
 */
public class AccountFragment extends Fragment implements Navigable {

    // Fragment's data members
    private final String TAG = AccountFragment.this.getClass().getName();
    private AuthStateViewModel mAuthStateViewModel;
    private FragmentAccountBinding mFragmentAccountBinding;

    /**
     * Called to have the fragment instantiate its user interface view.
     */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        mFragmentAccountBinding = FragmentAccountBinding.inflate(inflater);
        return mFragmentAccountBinding.getRoot();
    }

    /**
     * Initialize the fragment's ViewModel and observe
     *
     * @param view               The View returned by {@link #onCreateView(LayoutInflater, ViewGroup, Bundle)}.
     * @param savedInstanceState If non-null, this fragment is being re-constructed
     */
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // Initialize AuthStateViewModel of the fragment
        mAuthStateViewModel = new ViewModelProvider(requireActivity()).get(AuthStateViewModel.class);

        // Add observer to the user's state
        mAuthStateViewModel.getUserState().observe(getViewLifecycleOwner(), this::updateUi);
    }

    /**
     * Called when the view previously created by {@link #onCreateView} has
     * been detached from the fragment.
     */
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mFragmentAccountBinding = null;
    }

    /**
     * Prepare the Ui for a non-logged in user.
     */
    private void updateUiWithoutUser() {
        // Hide layout for a logged in user
        getBinding().fragmentAccountClLoggedIn.setVisibility(View.GONE);
        // Show layout for a non-logged in user
        getBinding().fragmentAccountClNotLoggedIn.setVisibility(View.VISIBLE);
        // Attach listener to the "click me" button
        getBinding().fragmentAccountBtnToAuth.setOnClickListener(v -> toAuthenticator());
    }

    /**
     * Prepare the Ui for a logged in user.
     */
    private void updateUiWithUser() {
        // Show layout for a logged in user
        getBinding().fragmentAccountClLoggedIn.setVisibility(View.VISIBLE);
        // Hide layout for a non-logged in user
        getBinding().fragmentAccountClNotLoggedIn.setVisibility(View.GONE);

        // Hook up listeners to the UI buttons
        setUpButtonListenerOf(getBinding().fragmentAccountMbtnUpdateName,
                UpdateAccountDialog.UPDATE_DISPLAY_NAME);
        setUpButtonListenerOf(getBinding().fragmentAccountMbtnUpdateEmail,
                UpdateAccountDialog.UPDATE_EMAIL);
        setUpButtonListenerOf(getBinding().fragmentAccountMbtnUpdatePassword,
                UpdateAccountDialog.UPDATE_PASSWORD);

        // Display the user's name and email
        getBinding().fragmentAccountMtvDisplayName.setText(mAuthStateViewModel.getUser().getDisplayName());
        getBinding().fragmentAccountMtvEmail.setText(mAuthStateViewModel.getUser().getEmail());
    }

    /**
     * Update the Ui based on the specified {@link LoggedInUser} instance.
     *
     * @param loggedInUser The current {@link LoggedInUser} instance of the application.
     */
    private void updateUi(LoggedInUser loggedInUser) {
        if (loggedInUser != null) {
            // Set up remaining Ui
            updateUiWithUser();
        } else {
            updateUiWithoutUser();
        }
    }

    /**
     * Hook up the button given the specified button id,
     * with an on click listener that instantiates a
     * dialog.
     *
     * @param button The button view to attach the on click listener.
     * @param action The kind of action to be performed in the dialog.
     */
    private void setUpButtonListenerOf(@NotNull Button button, short action) {
        button.setOnClickListener(getButtonListener(action));
    }

    /**
     * Creates a new instance of View.OnClickListener. When triggered,
     * creates a dialog that displays info about the specified parameters.
     *
     * @param action The kind of action to be performed in dialog.
     * @return A View.OnClickListener instance
     */
    public View.OnClickListener getButtonListener(short action) {
        return v -> {
            FragmentManager fm = isAdded() ? getParentFragmentManager() : null;
            if (fm != null) {
                if (mAuthStateViewModel.getUser() == null) {
                    AlertBuilder.promptUserToLogIn(requireContext(),
                            requireActivity(),
                            this, // Access the parent's Navigable interface implementation
                            R.string.not_logged_in_account_2);
                    return;
                }

                // Access the device's night mode configurations
                int nightModeFlags = this.requireContext()
                        .getResources()
                        .getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
                UpdateAccountDialog dialog = UpdateAccountDialog.newInstance(
                        nightModeFlags,
                        action);
                dialog.setTargetFragment(this, 1);
                dialog.show(fm, TAG);
            }
        };
    }

    /**
     * Access the {@link #mFragmentAccountBinding}.
     *
     * @return A reference to {@link #mFragmentAccountBinding}.
     */
    private FragmentAccountBinding getBinding() {
        return mFragmentAccountBinding;
    }

    /**
     * Navigates from the current Fragment subclass to the
     * {@link io.github.cchristou3.CyParking.ui.user.login.AuthenticatorFragment}.
     * If the user is logged in, his email is passed on when navigating to the
     * authenticator fragment. The email is then set to the email text field.
     *
     * @see AuthenticatorHosteeFragment#checkIfUserReAuthenticating()
     */
    @Override
    public void toAuthenticator() {
        Bundle emailBundle = null;
        if (mAuthStateViewModel.getUser() != null) {
            emailBundle = new Bundle();
            emailBundle.putString(getString(R.string.email_low), mAuthStateViewModel.getUser().getEmail());
        }
        getNavController(requireActivity())
                .navigate(R.id.action_nav_account_to_nav_authenticator_fragment, emailBundle);
    }

    /**
     * Navigates from the current Fragment subclass to the
     * {@link io.github.cchristou3.CyParking.ui.parking.slots.viewBooking.ViewBookingsFragment}.
     */
    @Override
    public void toBookings() {
        getNavController(requireActivity())
                .navigate(R.id.action_nav_account_to_nav_view_bookings);
    }

    /**
     * Navigates from the current Fragment subclass to the
     * {@link AccountFragment}.
     */
    @Override
    public void toAccount() {
        // Already in this screen. Thus, no need to implement this method.
    }

    /**
     * Navigates from the current Fragment subclass to the
     * {@link io.github.cchristou3.CyParking.ui.user.feedback.FeedbackFragment}.
     */
    @Override
    public void toFeedback() {
        getNavController(requireActivity())
                .navigate(R.id.action_nav_account_to_nav_feedback);
    }

    /**
     * Navigates from the current Fragment subclass to the
     * {@link HomeFragment}.
     */
    @Override
    public void toHome() {
        getNavController(requireActivity())
                .navigate(R.id.action_nav_account_to_nav_home);
    }
}