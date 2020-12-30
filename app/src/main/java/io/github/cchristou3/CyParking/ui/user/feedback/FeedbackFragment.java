package io.github.cchristou3.CyParking.ui.user.feedback;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import org.jetbrains.annotations.NotNull;

import io.github.cchristou3.CyParking.R;
import io.github.cchristou3.CyParking.data.interfaces.Navigable;
import io.github.cchristou3.CyParking.data.pojo.user.LoggedInUser;
import io.github.cchristou3.CyParking.databinding.FeedbackFragmentBinding;
import io.github.cchristou3.CyParking.ui.home.HomeFragment;
import io.github.cchristou3.CyParking.ui.host.AuthStateViewModel;
import io.github.cchristou3.CyParking.ui.host.MainHostActivity;
import io.github.cchristou3.CyParking.utilities.ViewUtility;

/**
 * Purpose: <p>Allow the user to send feedback to the development team. </p>
 * <p>
 * In terms of Authentication, this is achieved by communicating with the hosting
 * activity {@link MainHostActivity} via the {@link AuthStateViewModel}.
 * </p>
 *
 * @author Charalambos Christou
 * @version 2.0 28/12/20
 */
public class FeedbackFragment extends Fragment implements Navigable, TextWatcher {

    // Fragment data members
    private FeedbackViewModel mFeedbackViewModel;
    private AuthStateViewModel mAuthStateViewModel;
    private FeedbackFragmentBinding mFeedbackFragmentBinding;

    /**
     * Called to have the fragment instantiate its user interface view.
     */
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        mFeedbackFragmentBinding = FeedbackFragmentBinding.inflate(inflater);
        return mFeedbackFragmentBinding.getRoot();
    }

    /**
     * Called immediately after {@link #onCreateView(LayoutInflater, ViewGroup, Bundle)}
     * has returned, but before any saved state has been restored in to the view.
     *
     * @param view               The View returned by {@link #onCreateView(LayoutInflater, ViewGroup, Bundle)}.
     * @param savedInstanceState If non-null, this fragment is being re-constructed
     */
    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // Initialize mFeedbackViewModel and mAuthStateViewModel
        mFeedbackViewModel = new ViewModelProvider(this).get(FeedbackViewModel.class);
        mAuthStateViewModel = new ViewModelProvider(requireActivity()).get(AuthStateViewModel.class);

        // Update the UI based on the user's logged in status
        updateUI(mAuthStateViewModel.getUser());

        // Hook up the send feedback button with an onClickListener and disable it initially
        getBinding().feedbackFragmentMbtnSendFeedback.setEnabled(false);
        getBinding().feedbackFragmentMbtnSendFeedback.setOnClickListener(v -> {
            // TODO: Store message to Firestore & send notification to administrator via cloud function
            Toast.makeText(requireContext(), "Not implemented yet!", Toast.LENGTH_SHORT).show();
        });

        // Get a reference to the feedback TextView of the UI
        final EditText feedbackTextArea = getBinding().feedbackFragmentEtFeedbackBody;

        // If feedbackTextArea is inside a ScrollView then there is an issue while scrolling TextArea’s inner contents.
        // So, when touching the feedbackTextArea forbid the ScrollView from intercepting touch events.
        ViewUtility.disableParentScrollingInterferenceOf(feedbackTextArea);

        feedbackTextArea.addTextChangedListener(this);

        // Add an observer to the form to wait for changes
        mFeedbackViewModel.getFormState().observe(getViewLifecycleOwner(), feedbackFormState -> {
            getBinding().feedbackFragmentMbtnSendFeedback
                    .setEnabled(feedbackFormState.isDataValid());
            // Show validity status for the feedback message
            if (feedbackFormState.getFeedbackMessageError() != null) {
                feedbackTextArea.setError(getString(feedbackFormState.getFeedbackMessageError()));
            } else {
                // Hide error if it shows
                if (feedbackTextArea.getError() != null) {
                    feedbackTextArea.setError(null, null);
                }
            }
            if (mAuthStateViewModel.getUser() == null) {
                // Update Email error
                if (feedbackFormState.getEmailError() != null) {
                    getBinding().feedbackFragmentEtEmailBody
                            .setError(getString(feedbackFormState.getEmailError()));
                } else {
                    // Hide error if it shows
                    if (getBinding().feedbackFragmentEtEmailBody.getError() != null) {
                        getBinding().feedbackFragmentEtEmailBody.setError(null, null);
                    }
                }
            }
        });
    }

    /**
     * Called when the view previously created by {@link #onCreateView} has
     * been detached from the fragment.
     */
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        getBinding().feedbackFragmentMbtnSendFeedback.setOnClickListener(null);
        getBinding().feedbackFragmentEtFeedbackBody.removeTextChangedListener(this);
        getBinding().feedbackFragmentEtEmailBody.removeTextChangedListener(this);
        mFeedbackFragmentBinding = null;
    }

    /**
     * Access the {@link #mFeedbackFragmentBinding}.
     *
     * @return A reference to {@link #mFeedbackFragmentBinding}.
     */
    private FeedbackFragmentBinding getBinding() {
        return mFeedbackFragmentBinding;
    }

    /**
     * Updates the UI based on the specified FirebaseUser argument.
     *
     * @param user The current LoggedInUser instance if there is one
     */
    public void updateUI(@Nullable LoggedInUser user) {
        if (user != null) { // User is logged in
            updateUIWithUser(user);
        } else {// User is NOT logged in
            updateUIWithoutUser();
        }
    }

    /**
     * Updates the Ui appropriate for a non-logged in user.
     */
    private void updateUIWithoutUser() {
        // Hide the name details
        getBinding().feedbackFragmentTxtNameTitle.setVisibility(View.GONE);
        getBinding().feedbackFragmentTxtNameBody.setVisibility(View.GONE);

        // Hide the TextView related to the email
        getBinding().feedbackFragmentTxtEmailBody.setVisibility(View.GONE);

        // Show the edit text related to the email and add a listener
        getBinding().feedbackFragmentEtEmailBody.setVisibility(View.VISIBLE);
        getBinding().feedbackFragmentEtEmailBody.addTextChangedListener(this);
    }

    /**
     * Updates the Ui appropriate for a logged in user.
     *
     * @param user The current LoggedInUser instance.
     */
    private void updateUIWithUser(@NotNull LoggedInUser user) {
        // Show the name section
        getBinding().feedbackFragmentTxtNameTitle.setVisibility(View.VISIBLE);
        getBinding().feedbackFragmentTxtNameBody.setVisibility(View.VISIBLE);

        // Show the email section
        getBinding().feedbackFragmentTxtEmailBody.setVisibility(View.VISIBLE);

        // Hide the edit text related to the email
        getBinding().feedbackFragmentEtEmailBody.setVisibility(View.GONE);

        // Display user info
        String userDisplayName = user.getDisplayName();
        if (userDisplayName == null || userDisplayName.isEmpty()) {
            userDisplayName = user.getEmail();
        }
        getBinding().feedbackFragmentTxtNameBody.setText(userDisplayName);
        getBinding().feedbackFragmentTxtEmailBody.setText(user.getEmail());
    }

    /**
     * Unused TextWatcher methods.
     */
    public void beforeTextChanged(CharSequence s, int start, int count, int after) { /* ignore */ }

    public void onTextChanged(CharSequence s, int start, int before, int count) { /* ignore */ }

    /**
     * Gets triggered whenever, a text of an EditText changes.
     *
     * @param textFromFeedbackArea The new instance of editable of the editText.
     */
    @Override
    public void afterTextChanged(@NotNull Editable textFromFeedbackArea) {
        // Access the user's email if logged in. Otherwise, use the email
        // that the user entered.
        final LoggedInUser user = mAuthStateViewModel.getUser();
        final String email = (user != null)
                ? mAuthStateViewModel.getUser().getEmail()
                : getBinding().feedbackFragmentEtEmailBody.getText().toString();
        mFeedbackViewModel.formDataChanged(user, textFromFeedbackArea.toString(), email);
    }

    /**
     * Navigates from the current Fragment subclass to the
     * {@link io.github.cchristou3.CyParking.ui.user.login.AuthenticatorFragment}.
     */
    @Override
    public void toAuthenticator() {
        Navigation.findNavController(getActivity().findViewById(R.id.fragment_main_host_nv_nav_view))
                .navigate(R.id.action_nav_feedback_to_nav_authenticator_fragment);
    }

    /**
     * Navigates from the current Fragment subclass to the
     * {@link io.github.cchristou3.CyParking.ui.parking.slots.viewBooking.ViewBookingsFragment}.
     */
    @Override
    public void toBookings() {
        Navigation.findNavController(getActivity().findViewById(R.id.fragment_main_host_nv_nav_view))
                .navigate(R.id.action_nav_feedback_to_nav_view_bookings);
    }

    /**
     * Navigates from the current Fragment subclass to the
     * {@link io.github.cchristou3.CyParking.ui.user.AccountFragment}.
     */
    @Override
    public void toAccount() {
        Navigation.findNavController(getActivity().findViewById(R.id.fragment_main_host_nv_nav_view))
                .navigate(R.id.action_nav_feedback_to_nav_account);
    }

    /**
     * Navigates from the current Fragment subclass to the
     * {@link io.github.cchristou3.CyParking.ui.user.feedback.FeedbackFragment}.
     */
    @Override
    public void toFeedback() {
        // Already in this screen. Thus, no need to implement this method.
    }

    /**
     * Navigates from the current Fragment subclass to the
     * {@link HomeFragment}.
     */
    @Override
    public void toHome() {
        Navigation.findNavController(getActivity().findViewById(R.id.fragment_main_host_nv_nav_view))
                .navigate(R.id.action_nav_feedback_to_nav_home);
    }
}