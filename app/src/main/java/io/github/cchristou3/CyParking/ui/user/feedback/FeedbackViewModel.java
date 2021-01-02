package io.github.cchristou3.CyParking.ui.user.feedback;

import androidx.annotation.Nullable;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;

import io.github.cchristou3.CyParking.R;
import io.github.cchristou3.CyParking.data.model.user.Feedback;
import io.github.cchristou3.CyParking.data.model.user.LoggedInUser;
import io.github.cchristou3.CyParking.data.pojo.form.FeedbackFormState;
import io.github.cchristou3.CyParking.data.repository.FeedbackRepository;
import io.github.cchristou3.CyParking.ui.user.login.AuthenticatorViewModel;


/**
 * Purpose: <p>Data persistence when orientation changes.
 * Used when the users try to send feedback to the development team.</p>
 *
 * @author Charalambos Christou
 * @version 1.0 05/11/20
 */
public class FeedbackViewModel extends ViewModel {

    final private MutableLiveData<String> mEmailState = new MutableLiveData<>();
    final private MutableLiveData<String> mFeedbackState = new MutableLiveData<>();
    final private MutableLiveData<FeedbackFormState> mFormState = new MutableLiveData<>();

    /**
     * Updates the {@link #mFormState}'s value based on the given arguments.
     *
     * @param user            The current LoggedInUser instance if there is one
     * @param feedbackMessage The user's current feedback message.
     * @param recipientEmail  The user's email.
     */
    public void formDataChanged(LoggedInUser user, String feedbackMessage, @Nullable String recipientEmail) {
        mFeedbackState.setValue(feedbackMessage);
        if (user == null) {
            mEmailState.setValue(recipientEmail); // Persist the email string

            // If not logged in, validate the given email
            if (!AuthenticatorViewModel.isEmailValid(recipientEmail)) {
                mFormState.setValue(new FeedbackFormState(R.string.invalid_email, R.string.invalid_feedback));
                return; // If not valid, do not check for further user faults (in text).
            }
        }
        // Validate feedback message
        if (feedbackMessage != null && !feedbackMessage.trim().isEmpty()) {
            mFormState.setValue(new FeedbackFormState(null, true));
        } else {
            mFormState.setValue(new FeedbackFormState(null, R.string.invalid_feedback));
        }
    }

    /**
     * Stores the given {@link Feedback} instance in the database.
     *
     * @param feedback Feedback to be stored in the database.
     * @return Task to be handled by the view.
     */
    public Task<DocumentReference> sendFeedback(Feedback feedback) {
        return FeedbackRepository.sendFeedback(feedback);
    }

    /**
     * Access the value of the {@link #mEmailState}.
     *
     * @return The value of the email state.
     */
    public String getEmail() {
        return mEmailState.getValue();
    }

    /**
     * Access the value of the {@link #mFeedbackState}.
     *
     * @return The value of the feedback state.
     */
    public String getFeedback() {
        return mFeedbackState.getValue();
    }

    /**
     * Getter to its data members
     */
    public MutableLiveData<String> getEmailState() {
        return mEmailState;
    }

    public MutableLiveData<String> getFeedbackState() {
        return mFeedbackState;
    }

    public MutableLiveData<FeedbackFormState> getFormState() {
        return mFormState;
    }
}