package io.github.cchristou3.CyParking.ui.user.feedback;

import androidx.annotation.Nullable;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import io.github.cchristou3.CyParking.R;
import io.github.cchristou3.CyParking.data.pojo.user.FeedbackFormState;
import io.github.cchristou3.CyParking.data.pojo.user.LoggedInUser;
import io.github.cchristou3.CyParking.ui.user.login.AuthenticatorViewModel;


/**
 * Purpose: <p>Data persistence when orientation changes.
 * Used when the users try to send feedback to the development team.</p>
 *
 * @author Charalambos Christou
 * @version 1.0 05/11/20
 */
public class FeedbackViewModel extends ViewModel {

    final private MutableLiveData<String> email = new MutableLiveData<>();
    final private MutableLiveData<String> feedback = new MutableLiveData<>();
    final private MutableLiveData<FeedbackFormState> formState = new MutableLiveData<>();

    /**
     * Updates the {@link #formState}'s value based on the given arguments.
     *
     * @param user            The current LoggedInUser instance if there is one
     * @param feedbackMessage The user's current feedback message.
     * @param recipientEmail  The user's email.
     */
    public void formDataChanged(LoggedInUser user, String feedbackMessage, @Nullable String recipientEmail) {
        feedback.setValue(feedbackMessage);
        if (user == null) {
            email.setValue(recipientEmail); // Persist the email string

            // If not logged in, validate the given email
            if (!AuthenticatorViewModel.isEmailValid(recipientEmail)) {
                formState.setValue(new FeedbackFormState(R.string.invalid_email, R.string.invalid_feedback));
                return; // If not valid, do not check for further user faults (in text).
            }
        }
        // Validate feedback message
        if (feedbackMessage != null && !feedbackMessage.trim().isEmpty()) {
            formState.setValue(new FeedbackFormState(null, true));
        } else {
            formState.setValue(new FeedbackFormState(null, R.string.invalid_feedback));
        }
    }

    /**
     * Getter to its data members
     */
    public MutableLiveData<String> getEmail() {
        return email;
    }

    public MutableLiveData<String> getFeedback() {
        return feedback;
    }

    public MutableLiveData<FeedbackFormState> getFormState() {
        return formState;
    }
}