package io.github.cchristou3.CyParking.view.ui.user.feedback;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import io.github.cchristou3.CyParking.R;
import io.github.cchristou3.CyParking.view.data.interfaces.Navigate;
import io.github.cchristou3.CyParking.view.utilities.Utility;

/**
 * Purpose: <p>Allow the user to send feedback to the development team. </p>
 *
 * @author Charalambos Christou
 * @version 1.0 12/12/20
 */
public class FeedbackFragment extends Fragment implements Navigate {

    private FeedbackViewModel mViewModel;
    private EditText mEmailEditText;

    /**
     * Called to have the fragment instantiate its user interface view.
     */
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.feedback_fragment, container, false);
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
        mViewModel = new ViewModelProvider(this).get(FeedbackViewModel.class);

        if (FirebaseAuth.getInstance().getCurrentUser() == null)
            mEmailEditText = view.findViewById(R.id.feedback_fragment_et_email_body);

        // Update the UI based on the user's logged in status
        updateUI(view, FirebaseAuth.getInstance().getCurrentUser());

        // Hook up the send feedback button with an onClickListener and disable it initially
        Button sendFeedbackButton = view.findViewById(R.id.feedback_fragment_mbtn_send_feedback);
        sendFeedbackButton.setEnabled(false);
        sendFeedbackButton.setOnClickListener(v -> {
            // TODO: Store message to Firestore & send notification to administrator
            Toast.makeText(requireContext(), "Not implemented yet!", Toast.LENGTH_SHORT).show();
        });

        // Get a reference to the feedback TextView of the UI
        EditText feedbackTextArea = view.findViewById(R.id.feedback_fragment_et_feedback_body);

        // If feedbackTextArea is inside a ScrollView then there is an issue while scrolling TextArea’s inner contents.
        // So, when touching the feedbackTextArea forbid the ScrollView from intercepting touch events.
        Utility.disableParentScrollingInterferenceOf(feedbackTextArea);

        feedbackTextArea.addTextChangedListener(getTextChangedListener());

        // Add an observer to the form to wait for changes
        mViewModel.getFormState().observe(getViewLifecycleOwner(), feedbackFormState -> {
            sendFeedbackButton.setEnabled(feedbackFormState.isDataValid());
            // Show validity status for the feedback message
            if (feedbackFormState.getFeedbackMessageError() != null) {
                feedbackTextArea.setError(getString(feedbackFormState.getFeedbackMessageError()));
            } else {
                // Hide error if it shows
                if (feedbackTextArea.getError() != null) {
                    feedbackTextArea.setError(null, null);
                }
            }
            if (!mViewModel.isLoggedIn()) {
                // Update Email error
                if (feedbackFormState.getEmailError() != null) {
                    mEmailEditText.setError(getString(feedbackFormState.getEmailError()));
                } else {
                    // Hide error if it shows
                    if (mEmailEditText.getError() != null) {
                        mEmailEditText.setError(null, null);
                    }
                }
            }
        });
    }

    /**
     * Updates the UI based on the specified FirebaseUser argument.
     *
     * @param view The user interface view
     * @param user The current FirebaseUser instance if there is one
     */
    public void updateUI(@NonNull View view, @Nullable FirebaseUser user) {
        if (user != null) { // User is logged in
            // Show the name section
            view.findViewById(R.id.feedback_fragment_txt_name_title).setVisibility(View.VISIBLE);
            TextView nameTextView = view.findViewById(R.id.feedback_fragment_txt_name_body);
            nameTextView.setVisibility(View.VISIBLE);

            // Show the email section
            view.findViewById(R.id.feedback_fragment_txt_name_body).setVisibility(View.VISIBLE);
            TextView emailTextView = view.findViewById(R.id.feedback_fragment_txt_email_body);
            emailTextView.setVisibility(View.VISIBLE);

            // Hide the edit text related to the email
            view.findViewById(R.id.feedback_fragment_et_email_body).setVisibility(View.GONE);

            // Display user info
            String userDisplayName = user.getDisplayName();
            if (userDisplayName.equals("")) {
                userDisplayName = getString(R.string.name_not_found);
            }
            nameTextView.setText(userDisplayName);
            emailTextView.setText(user.getEmail());

        } else {// User is NOT logged in
            // Hide the name details
            view.findViewById(R.id.feedback_fragment_txt_name_title).setVisibility(View.GONE);
            view.findViewById(R.id.feedback_fragment_txt_name_body).setVisibility(View.GONE);

            // Hide the TextView related to the email
            view.findViewById(R.id.feedback_fragment_txt_email_body).setVisibility(View.GONE);

            // Show the edit text related to the email and add a listener
            mEmailEditText.setVisibility(View.VISIBLE);
            mEmailEditText.addTextChangedListener(getTextChangedListener());
        }
    }

    /**
     * Creates a new instance of TextWatcher.
     * Its {@link TextWatcher#afterTextChanged} method triggers
     * an update to the Fragment's FeedbackViewModel instance.
     *
     * @return An instance of TextWatcher
     */
    @NotNull
    @Contract(value = " -> new", pure = true)
    private TextWatcher getTextChangedListener() {
        return new TextWatcher() {
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { /* ignore */ }

            public void onTextChanged(CharSequence s, int start, int before, int count) { /* ignore */ }

            @Override
            public void afterTextChanged(Editable textFromFeedbackArea) {
                // Access the user's email if logged in. Otherwise, use the email
                // that the user entered.
                final String email = mViewModel.isLoggedIn()
                        ? FirebaseAuth.getInstance().getCurrentUser().getEmail() : mEmailEditText.getText().toString();
                mViewModel.formDataChanged(textFromFeedbackArea.toString(), email);
            }
        };
    }

    /**
     * Navigates from the current Fragment subclass to the
     * {@link io.github.cchristou3.CyParking.view.ui.user.login.AuthenticatorFragment}.
     */
    @Override
    public void toAuthenticator() {
        Navigation.findNavController(getActivity().findViewById(R.id.fragment_main_host_nv_nav_view))
                .navigate(R.id.action_nav_feedback_to_nav_authenticator_fragment);
    }

    /**
     * Navigates from the current Fragment subclass to the
     * {@link io.github.cchristou3.CyParking.view.ui.parking.slots.viewBooking.ViewBookingsFragment}.
     */
    @Override
    public void toBookings() {
        Navigation.findNavController(getActivity().findViewById(R.id.fragment_main_host_nv_nav_view))
                .navigate(R.id.action_nav_feedback_to_nav_view_bookings);
    }

    /**
     * Navigates from the current Fragment subclass to the
     * {@link io.github.cchristou3.CyParking.view.ui.user.AccountFragment}.
     */
    @Override
    public void toAccount() {
        Navigation.findNavController(getActivity().findViewById(R.id.fragment_main_host_nv_nav_view))
                .navigate(R.id.action_nav_feedback_to_nav_account);
    }

    /**
     * Navigates from the current Fragment subclass to the
     * {@link io.github.cchristou3.CyParking.view.ui.user.feedback.FeedbackFragment}.
     */
    @Override
    public void toFeedback() {
        // Already in this screen. Thus, no need to implement this method.
    }

    /**
     * Navigates from the current Fragment subclass to the
     * {@link io.github.cchristou3.CyParking.view.ui.HomeFragment}.
     */
    @Override
    public void toHome() {
        Navigation.findNavController(getActivity().findViewById(R.id.fragment_main_host_nv_nav_view))
                .navigate(R.id.action_nav_feedback_to_nav_home);
    }
}