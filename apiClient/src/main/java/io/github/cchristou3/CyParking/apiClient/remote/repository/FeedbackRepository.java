package io.github.cchristou3.CyParking.apiClient.remote.repository;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;

import org.jetbrains.annotations.NotNull;

import io.github.cchristou3.CyParking.apiClient.model.data.user.Feedback;

/**
 * Purpose: <p>contain methods to send feedback or error
 * messages to the administrator.</p>
 *
 * @author Charalambos Christou
 * @version 4.0 06/02/21
 */
public class FeedbackRepository implements DataSourceRepository.FeedbackHandler {

    /**
     * Stores the given {@link Feedback} instance in the database.
     *
     * @param feedback Feedback to be stored in the database.
     * @return Task to be handled by the view.
     */
    @NotNull
    public Task<DocumentReference> sendFeedback(Feedback feedback) {
        return getFeedbackRef().add(feedback);
    }
}