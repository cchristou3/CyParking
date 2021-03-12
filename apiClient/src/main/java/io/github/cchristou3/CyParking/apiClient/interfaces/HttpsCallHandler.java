package io.github.cchristou3.CyParking.apiClient.interfaces;

import com.google.android.gms.tasks.Task;

/**
 * Purpose: provide the view an interface for handling
 * HttpsCallableResults returned by Callable Cloud-Functions.
 *
 * @author Charalambos Christou
 * @version 1.0 21/01/21
 */
public interface HttpsCallHandler {
    /**
     * Gets triggered when the task has been <u>successfully</u> completed.
     * E.g. if {@link Task#isSuccessful()} returned <u>true</u>.
     *
     * @param rawJsonResponse The string that holds the json object from the backend.
     */
    void onSuccess(String rawJsonResponse);

    /**
     * Gets triggered when the task has been completed.
     * E.g. if {@link Task#isComplete()} returned true.
     */
    void onComplete();

    /**
     * Gets triggered when the task has been completed with a <u>failure</u>.
     * E.g. if {@link Task#isSuccessful()} returned <u>false</u>.
     *
     * @param exception The exception as thrown from the backend.
     */
    void onFailure(Exception exception);
}