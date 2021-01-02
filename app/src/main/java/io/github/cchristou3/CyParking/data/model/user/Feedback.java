package io.github.cchristou3.CyParking.data.model.user;

/**
 * Purpose: Encapsulate attributes related to feedback.
 * <p>POJO to be used to transfer and receive data
 * * via activities / fragments and HTTP requests.
 * Referenced in {@link io.github.cchristou3.CyParking.ui.user.feedback.FeedbackFragment}.
 *
 * @author Charalambos Christou
 * @version 1.0 30/12/20
 */
public class Feedback {

    private final String email;
    private final String body;

    /**
     * Initializes the instance's member
     * with the given arguments.
     *
     * @param email The recipient's email.
     * @param body  The inputted feedback.
     */
    public Feedback(String email, String body) {
        this.email = email;
        this.body = body;
    }

    /**
     * Access the recipient's email.
     *
     * @return The email of the sender.
     */
    public String getEmail() {
        return email;
    }

    /**
     * The actual feedback message.
     *
     * @return The body of the feedback.
     */
    public String getBody() {
        return body;
    }
}
