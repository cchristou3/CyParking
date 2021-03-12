package io.github.cchristou3.CyParking.ui.views.user.feedback;

import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.google.android.gms.tasks.Task;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import io.github.cchristou3.CyParking.apiClient.model.user.Feedback;
import io.github.cchristou3.CyParking.apiClient.model.user.LoggedInUser;
import io.github.cchristou3.CyParking.apiClient.remote.repository.FeedbackRepository;
import io.github.cchristou3.CyParking.ui.InstantTaskRuler;

import static io.github.cchristou3.CyParking.ui.LiveDataTestUtil.getOrAwaitValue;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.when;

/**
 * Unit tests for the {@link FeedbackViewModel} class.
 */
@RunWith(AndroidJUnit4.class)
public class FeedbackViewModelTest extends InstantTaskRuler {

    private final LoggedInUser notLoggedInUser = null;
    private final String validFeedback = "Valid message";
    private final String invalidFeedback = "";
    // Subject under test
    private FeedbackViewModel feedbackViewModel;
    @Mock
    private LoggedInUser loggedInUser;

    @Mock
    private FeedbackRepository repository;

    @Mock
    private Feedback feedback;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        when(repository.sendFeedback(feedback)).thenReturn(Mockito.mock(Task.class));
        feedbackViewModel = new FeedbackViewModel(repository);
    }

    @Test
    public void formDataChanged_nonNullUserValidFeedbackMsg_invalidForm() throws InterruptedException {
        // When a logged in user enters a valid feedback message.
        feedbackViewModel.formDataChanged(loggedInUser, validFeedback, null);
        assertThat(getOrAwaitValue(feedbackViewModel.getFormState()).getFeedbackMessageError(), is(nullValue()));
        assertThat(getOrAwaitValue(feedbackViewModel.getFormState()).isDataValid(), is(true));

    }

    @Test
    public void formDataChanged_nonNullUserInvalidFeedbackMsg_invalidForm() throws InterruptedException {
        // When a logged in user enters a valid feedback message.
        feedbackViewModel.formDataChanged(loggedInUser, invalidFeedback, null);
        assertThat(getOrAwaitValue(feedbackViewModel.getFormState()).getFeedbackMessageError(), is(not(nullValue())));
        assertThat(getOrAwaitValue(feedbackViewModel.getFormState()).isDataValid(), is(not(true)));
    }

    @Test
    public void formDataChanged_nullUserValidFeedbackMsgValidEmail_invalidForm() throws InterruptedException {
        // When a logged in user enters a valid feedback message.
        feedbackViewModel.formDataChanged(notLoggedInUser, validFeedback, "a@gmail.com");
        assertThat(getOrAwaitValue(feedbackViewModel.getFormState()).getFeedbackMessageError(), is(nullValue()));
        assertThat(getOrAwaitValue(feedbackViewModel.getFormState()).getEmailError(), is(nullValue()));
        assertThat(getOrAwaitValue(feedbackViewModel.getFormState()).isDataValid(), is(true));
        assertThat(getOrAwaitValue(feedbackViewModel.getFeedbackState()), is(validFeedback));
        assertThat(feedbackViewModel.getFeedback(), is(validFeedback));
    }


    @Test
    public void formDataChanged_nullUserInvalidFeedbackMsgValidEmail_invalidForm() throws InterruptedException {
        // When a logged in user enters a valid feedback message.
        feedbackViewModel.formDataChanged(notLoggedInUser, invalidFeedback, "a@gmail.com");
        assertThat(getOrAwaitValue(feedbackViewModel.getFormState()).getFeedbackMessageError(), is(not(nullValue())));
        assertThat(getOrAwaitValue(feedbackViewModel.getFormState()).getEmailError(), is(nullValue()));
        assertThat(getOrAwaitValue(feedbackViewModel.getFormState()).isDataValid(), is(not(true)));
        assertThat(getOrAwaitValue(feedbackViewModel.getEmailState()), is("a@gmail.com"));
        assertThat(feedbackViewModel.getEmail(), is("a@gmail.com"));
        assertThat(getOrAwaitValue(feedbackViewModel.getFeedbackState()), is(invalidFeedback));
        assertThat(feedbackViewModel.getFeedback(), is(invalidFeedback));
    }

    @Test
    public void formDataChanged_nullUserInvalidFeedbackMsgInvalidEmail_invalidForm() throws InterruptedException {
        // When a logged in user enters a valid feedback message.
        feedbackViewModel.formDataChanged(notLoggedInUser, invalidFeedback, null);
        assertThat(getOrAwaitValue(feedbackViewModel.getFormState()).getFeedbackMessageError(), is(nullValue()));
        assertThat(getOrAwaitValue(feedbackViewModel.getFormState()).getEmailError(), is(not(nullValue())));
        assertThat(getOrAwaitValue(feedbackViewModel.getFormState()).isDataValid(), is(not(true)));
    }

    @Test
    public void formDataChanged_nullUserValidFeedbackMsgInvalidEmail_invalidForm() throws InterruptedException {
        // When a logged in user enters a valid feedback message.
        feedbackViewModel.formDataChanged(notLoggedInUser, validFeedback, null);
        assertThat(getOrAwaitValue(feedbackViewModel.getFormState()).getFeedbackMessageError(), is(nullValue()));
        assertThat(getOrAwaitValue(feedbackViewModel.getFormState()).getEmailError(), is(not(nullValue())));
        assertThat(getOrAwaitValue(feedbackViewModel.getFormState()).isDataValid(), is(not(true)));
        assertThat(getOrAwaitValue(feedbackViewModel.getEmailState()), is(nullValue()));
        assertThat(feedbackViewModel.getEmail(), is(nullValue()));
    }

    @Test
    public void sendFeedback_returnsNonNull() {
        assertThat(feedbackViewModel.sendFeedback(feedback), is(not(nullValue())));
    }
}