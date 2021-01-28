package io.github.cchristou3.CyParking.ui.views.user.feedback;

import org.junit.Test;

import io.github.cchristou3.CyParking.ui.views.parking.slots.booking.BookingViewModel;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * Unit tests for the {@link FeedbackViewModelFactory} class.
 */
public class FeedbackViewModelFactoryTest {

    @Test(expected = IllegalArgumentException.class)
    public void create_wrongClass_throwsException() {
        new FeedbackViewModelFactory().create(BookingViewModel.class);
    }

    public void create_correctClass_returnsNonNull() {
        assertThat(new FeedbackViewModelFactory().create(FeedbackViewModel.class), is(not(nullValue())));
    }
}