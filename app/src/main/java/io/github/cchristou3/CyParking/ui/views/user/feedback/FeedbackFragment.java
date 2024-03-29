package io.github.cchristou3.CyParking.ui.views.user.feedback;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewbinding.ViewBinding;

import org.jetbrains.annotations.NotNull;

import io.github.cchristou3.CyParking.R;
import io.github.cchristou3.CyParking.apiClient.model.data.user.Feedback;
import io.github.cchristou3.CyParking.apiClient.model.data.user.LoggedInUser;
import io.github.cchristou3.CyParking.data.interfaces.Navigable;
import io.github.cchristou3.CyParking.databinding.FeedbackFragmentBinding;
import io.github.cchristou3.CyParking.ui.components.BaseFragment;
import io.github.cchristou3.CyParking.ui.components.NavigatorFragment;
import io.github.cchristou3.CyParking.ui.views.home.HomeFragment;
import io.github.cchristou3.CyParking.ui.views.host.GlobalStateViewModel;
import io.github.cchristou3.CyParking.ui.views.host.MainHostActivity;
import io.github.cchristou3.CyParking.ui.views.user.account.AccountFragment;
import io.github.cchristou3.CyParking.utils.ViewUtility;

import static io.github.cchristou3.CyParking.utils.ViewUtility.getStringOrEmpty;

/**
 * Purpose: <p>Allow the user to send feedback to the development team. </p>
 * <p>
 * In terms of Authentication, this is achieved by communicating with the hosting
 * activity {@link MainHostActivity} via the {@link GlobalStateViewModel}.
 * </p>
 *
 * @author Charalambos Christou
 * @version 9.0 24/03/21
 */
public class FeedbackFragment extends NavigatorFragment<FeedbackFragmentBinding> implements TextWatcher {

    // Fragment data members
    private FeedbackViewModel mFeedbackViewModel;

    /**
     * Called to have the fragment instantiate its user interface view.
     *
     * @see BaseFragment#onCreateView(ViewBinding, int)
     */
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return super.onCreateView(FeedbackFragmentBinding.inflate(inflater), R.string.menu_feedback);
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
        initializeViewModel();

        initializeUi();

        addObserversToStates();
    }

    /**
     * Called when the view previously created by {@link #onCreateView} has
     * been detached from the fragment.
     *
     * @see BaseFragment#onDestroyView()
     */
    @Override
    public void onDestroyView() {
        getBinding().feedbackFragmentMbtnSendFeedback.setOnClickListener(null);
        getBinding().feedbackFragmentEtFeedbackInput.removeTextChangedListener(this);
        if (getUser() == null) {
            getBinding().feedbackFragmentEtEmailInput.removeTextChangedListener(this);
        }
        super.onDestroyView();
    }

    /**
     * Initialize the Ui's listeners and set up the text area for the user.
     */
    private void initializeUi() {
        setUpButton(); // "Send feedback" button
        // If feedbackTextArea is inside a ScrollView then there is an issue while scrolling TextArea’s inner contents.
        // So, when touching the feedbackTextArea forbid the ScrollView from intercepting touch events.
        ViewUtility.disableParentScrollingInterferenceOf(getBinding().feedbackFragmentEtFeedbackInput);
        getBinding().feedbackFragmentEtFeedbackInput.addTextChangedListener(this);
    }

    /**
     * Initially disable the button and hook it up with an on click listener.
     * onClick: Stores the message to the remote database.
     */
    private void setUpButton() {
        // Hook up the send feedback button with an onClickListener and disable it initially
        getBinding().feedbackFragmentMbtnSendFeedback.setEnabled(false);
        getBinding().feedbackFragmentMbtnSendFeedback.setOnClickListener(v ->
                // Store message to Firestore and (TODO) send notification to administrator via cloud function
                mFeedbackViewModel.sendFeedback(
                        new Feedback(
                                ((getUser() != null) ? // If logged in
                                        getUser().getEmail() // its email
                                        : mFeedbackViewModel.getEmail()), // Otherwise, inputted email
                                mFeedbackViewModel.getFeedback()
                        ), getGlobalStateViewModel()::updateToastMessage
                )
        );
    }

    /**
     * Attach observer to the user's state and the feedback form's state.
     */
    private void addObserversToStates() {
        // Update the UI based on the user's logged in status
        observeUserState(this::updateUI);
        // Add an observer to the form to wait for changes
        mFeedbackViewModel.getFormState().observe(getViewLifecycleOwner(), feedbackFormState -> {
            getBinding().feedbackFragmentMbtnSendFeedback
                    .setEnabled(feedbackFormState.isDataValid());
            // Show validity status for the feedback message
            ViewUtility.updateErrorOf(requireContext(), getBinding().feedbackFragmentTilFeedbackInput, feedbackFormState.getFeedbackMessageError());
            if (getUser() == null) {
                // Update Email error
                ViewUtility.updateErrorOf(requireContext(), getBinding().feedbackFragmentTilEmailInput, feedbackFormState.getEmailError());
            }
        });

        mFeedbackViewModel.getGoBackState().observe(getViewLifecycleOwner(), x -> goBack());
    }

    /**
     * Initialize the fragment's {@link #mFeedbackViewModel}.
     */
    private void initializeViewModel() {
        mFeedbackViewModel = new ViewModelProvider(this,
                new FeedbackViewModelFactory()).get(FeedbackViewModel.class);
    }

    /**
     * Updates the UI based on the specified FirebaseUser argument.
     *
     * @param user The current LoggedInUser instance if there is one
     */
    public void updateUI(@Nullable LoggedInUser user) {
        if (user != null) { // User is logged in
            updateUIWithUser(user);
            cleanUpEmailErrors();
        } else {// User is NOT logged in
            updateUIWithoutUser();
        }
    }

    /**
     * Remove any email related errors as the user is logged in and
     * there is no need to input it.
     */
    private void cleanUpEmailErrors() {
        ViewUtility.updateErrorOf(requireContext(), getBinding().feedbackFragmentTilEmailInput, null);
    }

    /**
     * Updates the Ui appropriate for a non-logged in user.
     */
    private void updateUIWithoutUser() {
        // Hide the name details
        getBinding().feedbackFragmentTxtNameTitle.setVisibility(View.GONE);
        getBinding().feedbackFragmentTxtNameBody.setVisibility(View.GONE);

        // Hide the TextView related to the email - shows user's email when logged in
        getBinding().feedbackFragmentTxtEmailBody.setVisibility(View.GONE);

        // Show the edit text related to the email and add a listener
        getBinding().feedbackFragmentEtEmailInput.setVisibility(View.VISIBLE);
        getBinding().feedbackFragmentTilEmailInput.setVisibility(View.VISIBLE);
        getBinding().feedbackFragmentEtEmailInput.addTextChangedListener(this);
        getBinding().feedbackFragmentTxtEmailTitle.setVisibility(View.VISIBLE);
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
        getBinding().feedbackFragmentEtEmailInput.setVisibility(View.GONE);
        getBinding().feedbackFragmentTilEmailInput.setVisibility(View.GONE);

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
        final LoggedInUser user = getUser();
        final String email = (user != null)
                ? getUser().getEmail()
                : getStringOrEmpty(getBinding().feedbackFragmentEtEmailInput);
        mFeedbackViewModel.formDataChanged(user, textFromFeedbackArea.toString(), email);
    }

    /**
     * Navigates from the current Fragment subclass to the
     * {@link io.github.cchristou3.CyParking.ui.views.user.login.AuthenticatorFragment}.
     */
    @Override
    public void toAuthenticator() {
        navigateTo(
                        FeedbackFragmentDirections
                                .actionNavFeedbackToNavAuthenticatorFragment()
                );
    }

    /**
     * Navigates from the current Fragment subclass to the
     * {@link io.github.cchristou3.CyParking.ui.views.parking.slots.viewBooking.ViewBookingsFragment}.
     */
    @Override
    public void toBookings() {
        navigateTo(
                        FeedbackFragmentDirections.actionNavFeedbackToNavViewBookings()
                );
    }

    /**
     * Navigates from the current Fragment subclass to the
     * {@link AccountFragment}.
     */
    @Override
    public void toAccount() {
        navigateTo(
                        FeedbackFragmentDirections.actionNavFeedbackToNavAccount()
                );
    }

    /**
     * Navigates from the current Fragment subclass to the
     * {@link io.github.cchristou3.CyParking.ui.views.user.feedback.FeedbackFragment}.
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
        navigateTo(
                        FeedbackFragmentDirections.actionNavFeedbackToNavHome()
                );
    }
}