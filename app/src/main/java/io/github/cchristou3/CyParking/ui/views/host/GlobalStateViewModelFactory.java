package io.github.cchristou3.CyParking.ui.views.host;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import io.github.cchristou3.CyParking.data.repository.AuthenticatorRepository;

/**
 * Purpose: <p>ViewModel provider factory to instantiate GlobalStateViewModel.
 * Required given GlobalStateViewModel has a non-empty constructor</p>
 *
 * @author Charalambos Christou
 * @version 1.0 24/12/20
 */
public class GlobalStateViewModelFactory implements ViewModelProvider.Factory {

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
        if (modelClass.isAssignableFrom(GlobalStateViewModel.class)) {
            return (T) new GlobalStateViewModel(new AuthenticatorRepository());
        } else {
            throw new IllegalArgumentException("Unknown ViewModel class");
        }
    }
}