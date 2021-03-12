package io.github.cchristou3.CyParking.ui.views.parking.slots.viewBooking;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import io.github.cchristou3.CyParking.apiClient.remote.repository.BookingRepository;

/**
 * Purpose: <p>ViewModel provider factory to instantiate ViewBookingsViewModel.
 * Required given ViewBookingsViewModel has a non-empty constructor</p>
 *
 * @author Charalambos Christou
 * @version 1.0 14/01/21
 */
public class ViewBookingsViewModelFactory implements ViewModelProvider.Factory {

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
        if (modelClass.isAssignableFrom(ViewBookingsViewModel.class)) {
            return (T) new ViewBookingsViewModel(new BookingRepository());
        } else {
            throw new IllegalArgumentException("Unknown ViewModel class");
        }
    }
}