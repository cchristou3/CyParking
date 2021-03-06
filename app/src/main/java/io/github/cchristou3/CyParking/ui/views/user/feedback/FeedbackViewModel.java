package io.github.cchristou3.CyParking.ui.views.user.feedback;

import androidx.annotation.Nullable;
import androidx.core.util.Consumer;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import io.github.cchristou3.CyParking.R;
import io.github.cchristou3.CyParking.apiClient.model.data.user.Feedback;
import io.github.cchristou3.CyParking.apiClient.model.data.user.LoggedInUser;
import io.github.cchristou3.CyParking.apiClient.remote.repository.FeedbackRepository;
import io.github.cchristou3.CyParking.data.pojo.form.feedback.FeedbackFormState;
import io.github.cchristou3.CyParking.ui.components.SingleLiveEvent;

import static io.github.cchristou3.CyParking.ui.views.user.login.AuthenticatorViewModel.isEmailValid;


/**
 * Purpose: <p>Data persistence when configuration changes.
 * Used when the users try to send feedback to the development team.</p>
 *
 * @author Charalambos Christou
 * @version 1.0 05/11/20
 */
public class FeedbackViewModel extends ViewModel {

    final private MutableLiveData<String> mEmailState = new MutableLiveData<>();
    final private MutableLiveData<String> mFeedbackState = new MutableLiveData<>();
    final private MutableLiveData<FeedbackFormState> mFormState = new MutableLiveData<>();
    final private MutableLiveData<Object> mGoBackState = new SingleLiveEvent<>();

    private final FeedbackRepository mFeedbackRepository;

    /**
     * Initialize the ViewModel's FeedbackRepository instance
     * with the given argument.
     *
     * @param feedbackRepository An FeedbackRepository instance.
     */
    public FeedbackViewModel(FeedbackRepository feedbackRepository) {
        this.mFeedbackRepository = feedbackRepository;
    }

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
            if (!isEmailValid(recipientEmail)) {
                mFormState.setValue(new FeedbackFormState(R.string.invalid_email, null));
                return; // If not valid, do not check for further user faults (in text).
            }
        }
        // Validate feedback message
        if (feedbackMessage != null && !feedbackMessage.trim().isEmpty()) {
            mFormState.setValue(new FeedbackFormState(true));
        } else {
            mFormState.setValue(new FeedbackFormState(null, R.string.invalid_feedback));
        }
    }

    /**
     * Stores the given {@link Feedback} instance in the database.
     *
     * @param feedback     Feedback to be stored in the database.
     * @param displayToast A handler to sending toast messages.
     */
    public void sendFeedback(Feedback feedback, Consumer<Integer> displayToast) {
        mFeedbackRepository.sendFeedback(feedback).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                displayToast.accept(R.string.feedback_success);
                mGoBackState.setValue(null);
            } else {
                displayToast.accept(R.string.feedback_failed);
            }
        });
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
    public LiveData<String> getEmailState() {
        return mEmailState;
    }

    public LiveData<String> getFeedbackState() {
        return mFeedbackState;
    }

    public LiveData<FeedbackFormState> getFormState() {
        return mFormState;
    }

    public LiveData<Object> getGoBackState() {
        return mGoBackState;
    }
}