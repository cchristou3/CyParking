package io.github.cchristou3.CyParking.ui.user;

import android.content.res.Configuration;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.navigation.Navigation;

import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.auth.FirebaseAuth;

import org.jetbrains.annotations.NotNull;

import io.github.cchristou3.CyParking.R;
import io.github.cchristou3.CyParking.data.interfaces.Navigable;
import io.github.cchristou3.CyParking.ui.home.HomeFragment;
import io.github.cchristou3.CyParking.ui.widgets.update.UpdateAccountDialog;

/**
 * Purpose: <p>Going to get replaced with another Navigation option</p>
 *
 * @author Charalambos Christou
 * @version 2.0 215/12/20
 */
public class AccountFragment extends Fragment implements Navigable {

    // Fragment's data members
    private final String TAG = AccountFragment.this.getClass().getName();
    private UpdateAccountDialog mUpdateAccountDialog;

    /**
     * Called to have the fragment instantiate its user interface view.
     */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_account, container, false);
    }

    /**
     * @param view               The View returned by {@link #onCreateView(LayoutInflater, ViewGroup, Bundle)}.
     * @param savedInstanceState If non-null, this fragment is being re-constructed
     */
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // Hook up listeners to the UI buttons
        setUpButtonListenerOf(view, R.id.fragment_account_mbtn_update_name,
                R.string.prompt_name, "Updating name", UpdateAccountDialog.UPDATE_DISPLAY_NAME);

        setUpButtonListenerOf(view, R.id.fragment_account_mbtn_update_email,
                R.string.prompt_email, "Updating email", UpdateAccountDialog.UPDATE_EMAIL);

        setUpButtonListenerOf(view, R.id.fragment_account_mbtn_update_password,
                R.string.prompt_password, "Updating password", UpdateAccountDialog.UPDATE_PASSWORD);


        // If users are logged in, show their name and email
        if (FirebaseAuth.getInstance().getCurrentUser() != null) { // If user is logged in
            updateButtonTextTo(view, R.id.fragment_account_mtv_display_name,
                    FirebaseAuth.getInstance().getCurrentUser().getDisplayName());
            updateButtonTextTo(view, R.id.fragment_account_mtv_email,
                    FirebaseAuth.getInstance().getCurrentUser().getEmail());
        }
    }

    /**
     * Hook up the button given the specified button id,
     * with an on click listener that instantiates a
     * dialog.
     *
     * @param view             The user interface view
     * @param buttonId         The id of the button
     * @param fieldToBeUpdated The field that the user prompt to update
     * @param title            The title of the dialog
     * @param dialogType       The type of the dialog
     */
    private void setUpButtonListenerOf(
            @NotNull View view, @IdRes int buttonId, @StringRes int fieldToBeUpdated, String title, short dialogType
    ) {
        view.findViewById(buttonId)
                .setOnClickListener(
                        getButtonListener(fieldToBeUpdated, title, dialogType));
    }

    /**
     * Creates a new instance of View.OnClickListener. When triggered,
     * creates a dialog that displays info about the specified parameters.
     *
     * @param fieldToBeUpdated A String Resource Id
     * @param dialogTitle      The title of the dialog
     * @param updateState      The kind of dialog
     * @return A View.OnClickListener instance
     */
    public View.OnClickListener getButtonListener(@StringRes int fieldToBeUpdated, String dialogTitle, short updateState) {
        return v -> {
            FragmentManager fm = isAdded() ? getParentFragmentManager() : null;
            if (fm != null) {
                // Access the device's night mode configurations
                int nightModeFlags = this.requireContext().getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
                mUpdateAccountDialog = UpdateAccountDialog.newInstance(dialogTitle,
                        getString(fieldToBeUpdated),
                        nightModeFlags,
                        updateState);
                mUpdateAccountDialog.show(fm, TAG);
            }
        };
    }

    /**
     * Finds the view given the specified view id
     * and sets its text value to the specified string.
     *
     * @param view   The user interface view
     * @param viewID The id of a UI element
     * @param text   The text to be assigned to the UI element
     */
    private void updateButtonTextTo(@NotNull View view, @IdRes Integer viewID, final String text) {
        ((MaterialTextView) view.findViewById(viewID)).setText(text);
    }

    /**
     * Navigates from the current Fragment subclass to the
     * {@link io.github.cchristou3.CyParking.ui.user.login.AuthenticatorFragment}.
     */
    @Override
    public void toAuthenticator() {
        Navigation.findNavController(getActivity().findViewById(R.id.fragment_main_host_nv_nav_view))
                .navigate(R.id.action_nav_account_to_nav_authenticator_fragment);
    }

    /**
     * Navigates from the current Fragment subclass to the
     * {@link io.github.cchristou3.CyParking.ui.parking.slots.viewBooking.ViewBookingsFragment}.
     */
    @Override
    public void toBookings() {
        Navigation.findNavController(getActivity().findViewById(R.id.fragment_main_host_nv_nav_view))
                .navigate(R.id.action_nav_account_to_nav_view_bookings);
    }

    /**
     * Navigates from the current Fragment subclass to the
     * {@link io.github.cchristou3.CyParking.ui.user.AccountFragment}.
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
        Navigation.findNavController(getActivity().findViewById(R.id.fragment_main_host_nv_nav_view))
                .navigate(R.id.action_nav_account_to_nav_feedback);
    }

    /**
     * Navigates from the current Fragment subclass to the
     * {@link HomeFragment}.
     */
    @Override
    public void toHome() {
        Navigation.findNavController(getActivity().findViewById(R.id.fragment_main_host_nv_nav_view))
                .navigate(R.id.action_nav_account_to_nav_home);
    }
}