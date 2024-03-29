package io.github.cchristou3.CyParking.ui.views.operator;


import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import io.github.cchristou3.CyParking.apiClient.remote.repository.DefaultOperatorRepository;

/**
 * Purpose: <p>ViewModel provider factory to instantiate OperatorViewModel.
 * Required given OperatorViewModel has a non-empty constructor</p>
 *
 * @author Charalambos Christou
 * @version 1.0 12/01/21
 */
public class OperatorViewModelFactory implements ViewModelProvider.Factory {

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
        if (modelClass.isAssignableFrom(OperatorViewModel.class)) {
            return (T) new OperatorViewModel(new DefaultOperatorRepository());
        } else {
            throw new IllegalArgumentException("Unknown ViewModel class");
        }
    }
}