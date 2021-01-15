package io.github.cchristou3.CyParking.ui.user.account.update;

import org.junit.Test;

import io.github.cchristou3.CyParking.ui.parking.slots.booking.BookingViewModel;
import io.github.cchristou3.CyParking.ui.user.feedback.FeedbackViewModel;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * Unit tests for the {@link UpdateViewModelFactory} class.
 */
public class UpdateViewModelFactoryTest {
    @Test(expected = IllegalArgumentException.class)
    public void create_wrongClass_throwsException() {
        new UpdateViewModelFactory().create(BookingViewModel.class);
    }

    public void create_correctClass_returnsNonNull() {
        assertThat(new UpdateViewModelFactory().create(FeedbackViewModel.class), is(not(nullValue())));
    }
}