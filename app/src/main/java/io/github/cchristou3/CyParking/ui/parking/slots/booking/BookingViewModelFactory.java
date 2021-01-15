package io.github.cchristou3.CyParking.ui.parking.slots.booking;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import io.github.cchristou3.CyParking.data.repository.BookingRepository;

/**
 * Purpose: <p>ViewModel provider factory to instantiate BookingViewModel.
 * Required given BookingViewModel has a non-empty constructor</p>
 *
 * @author Charalambos Christou
 * @version 1.0 13/01/21
 */
public class BookingViewModelFactory implements ViewModelProvider.Factory {

    /**
     * Creates a new instance of the given {@code Class}.
     * <p>
     *
     * @param modelClass a {@code Class} whose instance is requested
     * @return a newly created ViewModel
     */
    @NonNull
    @Override
    @SuppressWarnings("unchecked")
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(BookingViewModel.class)) {
            return (T) new BookingViewModel(new BookingRepository());
        } else {
            throw new IllegalArgumentException("Unknown ViewModel class");
        }
    }
}