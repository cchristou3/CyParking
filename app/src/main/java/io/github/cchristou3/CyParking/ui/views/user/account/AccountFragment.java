package io.github.cchristou3.CyParking.ui.views.user.account;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentManager;
import androidx.viewbinding.ViewBinding;

import org.jetbrains.annotations.NotNull;

import io.github.cchristou3.CyParking.R;
import io.github.cchristou3.CyParking.apiClient.model.data.user.LoggedInUser;
import io.github.cchristou3.CyParking.data.interfaces.Navigable;
import io.github.cchristou3.CyParking.databinding.FragmentAccountBinding;
import io.github.cchristou3.CyParking.ui.components.BaseFragment;
import io.github.cchristou3.CyParking.ui.components.NavigatorFragment;
import io.github.cchristou3.CyParking.ui.helper.AlertBuilder;
import io.github.cchristou3.CyParking.ui.views.home.HomeFragment;
import io.github.cchristou3.CyParking.ui.views.user.account.update.UpdateAccountDialog;
import io.github.cchristou3.CyParking.ui.views.user.login.AuthenticatorHosteeFragment;

/**
 * Purpose: <p>Allow logged in users to change their private data.</p>
 *
 * @author Charalambos Christou
 * @version 8.0 24/03/21
 */
public class AccountFragment extends NavigatorFragment<FragmentAccountBinding> {

    // Fragment's data members
    private final String TAG = AccountFragment.this.getClass().getName();
    private boolean signUpWasClicked = false;

    /**
     * Called to have the fragment instantiate its user interface view.
     *
     * @see BaseFragment#onCreateView(ViewBinding, int)
     */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        return super.onCreateView(FragmentAccountBinding.inflate(inflater), R.string.menu_account);
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
        // Add observer to the user's state
        observeUserState(this::updateUi);
    }

    /**
     * Called when the view previously created by {@link #onCreateView} has
     * been detached from the fragment.
     *
     * @see BaseFragment#onDestroyView()
     */
    @Override
    public void onDestroyView() {
        super.removeOnClickListeners(
                getBinding().fragmentAccountMbLogIn,
                getBinding().fragmentAccountMbSignUp,
                getBinding().fragmentAccountMbtnUpdateName,
                getBinding().fragmentAccountMbtnUpdateEmail,
                getBinding().fragmentAccountMbtnUpdatePassword
        );
        super.onDestroyView();
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
        getBinding().fragmentAccountMbLogIn.setOnClickListener(v -> {
            signUpWasClicked = false;
            toAuthenticator();
        });
        getBinding().fragmentAccountMbSignUp.setOnClickListener(v -> {
            signUpWasClicked = true;
            toAuthenticator();
        });
    }

    /**
     * Prepare the Ui for a logged in user.
     *
     * @param loggedInUser The current instance of {@link LoggedInUser}.
     */
    private void updateUiWithUser(@NotNull LoggedInUser loggedInUser) {
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
        getBinding().fragmentAccountMtvDisplayName.setText(loggedInUser.getDisplayName());
        getBinding().fragmentAccountMtvEmail.setText(loggedInUser.getEmail());
    }

    /**
     * Update the Ui based on the specified {@link LoggedInUser} instance.
     *
     * @param loggedInUser The current {@link LoggedInUser} instance of the application.
     */
    private void updateUi(LoggedInUser loggedInUser) {
        if (loggedInUser != null) {
            // Set up remaining Ui
            updateUiWithUser(loggedInUser);
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
                if (getGlobalStateViewModel().getUser() == null) {
                    AlertBuilder.promptUserToLogIn(getChildFragmentManager(),
                            requireActivity(),
                            this, // Access the parent's Navigable interface implementation
                            R.string.not_logged_in_account_2);
                    return;
                }

                UpdateAccountDialog dialog = UpdateAccountDialog.newInstance(action);
                dialog.setTargetFragment(this, 1);
                dialog.show(fm, TAG);
            }
        };
    }

    /**
     * Navigates from the current Fragment subclass to the
     * {@link io.github.cchristou3.CyParking.ui.views.user.login.AuthenticatorFragment}.
     * If the user is logged in, his email is passed on when navigating to the
     * authenticator fragment. The email is then set to the email text field.
     * If the user, pressed the "sign up" button, he will be navigated directly,
     * to the sign up tab.
     *
     * @see AuthenticatorHosteeFragment#checkIfUserReAuthenticating()
     */
    @Override
    public void toAuthenticator() {
        AccountFragmentDirections.ActionNavAccountToNavAuthenticatorFragment
                directions = AccountFragmentDirections.actionNavAccountToNavAuthenticatorFragment();
        if (getGlobalStateViewModel().getUser() != null) {
            directions.setEmail(getGlobalStateViewModel().getUser().getEmail());
        } else if (signUpWasClicked) {
            directions.setSignUp(true);
        }
        navigateTo(directions);
    }

    /**
     * Navigates from the current Fragment subclass to the
     * {@link io.github.cchristou3.CyParking.ui.views.parking.slots.viewBooking.ViewBookingsFragment}.
     */
    @Override
    public void toBookings() {
        navigateTo(
                        AccountFragmentDirections.actionNavAccountToNavViewBookings()
                );
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
     * {@link io.github.cchristou3.CyParking.ui.views.user.feedback.FeedbackFragment}.
     */
    @Override
    public void toFeedback() {
        navigateTo(
                        AccountFragmentDirections.actionNavAccountToNavFeedback()
                );
    }

    /**
     * Navigates from the current Fragment subclass to the
     * {@link HomeFragment}.
     */
    @Override
    public void toHome() {
        navigateTo(
                        AccountFragmentDirections.actionNavAccountToNavHome()
                );
    }
}