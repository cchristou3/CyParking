package io.github.cchristou3.CyParking.apiClient.model.user;

import org.junit.Test;

import io.github.cchristou3.CyParking.apiClient.model.data.user.Feedback;

import static org.junit.Assert.assertTrue;

/**
 * Unit tests for the {@link Feedback} class.
 */
public class FeedbackTest {

    @Test
    public void Feedback_initializesCorrectAttributes() {
        // When a Feedback object is created
        Feedback feedback = new Feedback("email", "the main message");
        // Then the getters should return the same values
        assertTrue(feedback.getEmail().equals("email") && feedback.getBody().equals("the main message"));
    }
}