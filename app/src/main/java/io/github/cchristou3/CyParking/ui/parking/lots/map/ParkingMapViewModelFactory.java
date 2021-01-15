package io.github.cchristou3.CyParking.ui.parking.lots.map;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import io.github.cchristou3.CyParking.data.repository.ParkingMapRepository;

/**
 * Purpose: <p>ViewModel provider factory to instantiate ParkingMapViewModel.
 * Required given ParkingMapViewModel has a non-empty constructor</p>
 *
 * @author Charalambos Christou
 * @version 1.0 1/11/20
 */
public class ParkingMapViewModelFactory implements ViewModelProvider.Factory {

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
        if (modelClass.isAssignableFrom(ParkingMapViewModel.class)) {
            return (T) new ParkingMapViewModel(new ParkingMapRepository());
        } else {
            throw new IllegalArgumentException("Unknown ViewModel class");
        }
    }
}
