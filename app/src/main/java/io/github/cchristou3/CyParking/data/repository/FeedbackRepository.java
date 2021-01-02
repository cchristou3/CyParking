package io.github.cchristou3.CyParking.data.repository;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import org.jetbrains.annotations.NotNull;

import io.github.cchristou3.CyParking.data.model.user.Feedback;

/**
 * Purpose: <p>contain methods to send feedback or error
 * messages to the administrator.</p>
 *
 * @author Charalambos Christou
 * @version 1.0 30/12/20
 */
public class FeedbackRepository {

    public static final String FEEDBACK = "feedback";

    /**
     * Stores the given {@link Feedback} instance in the database.
     *
     * @param feedback Feedback to be stored in the database.
     * @return Task to be handled by the view.
     */
    @NotNull
    public static Task<DocumentReference> sendFeedback(Feedback feedback) {
        return FirebaseFirestore.getInstance().collection(FEEDBACK).add(feedback);
    }
}