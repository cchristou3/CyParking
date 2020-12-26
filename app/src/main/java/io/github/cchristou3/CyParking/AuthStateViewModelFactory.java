package io.github.cchristou3.CyParking;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.google.firebase.auth.FirebaseAuth;

import io.github.cchristou3.CyParking.data.repository.AuthenticatorRepository;

/**
 * Purpose: <p>ViewModel provider factory to instantiate AuthStateViewModel.
 * Required given AuthStateViewModel has a non-empty constructor</p>
 *
 * @author Charalambos Christou
 * @version 1.0 24/12/20
 */
public class AuthStateViewModelFactory implements ViewModelProvider.Factory {

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
        if (modelClass.isAssignableFrom(AuthStateViewModel.class)) {
            return (T) new AuthStateViewModel(AuthenticatorRepository.getInstance(FirebaseAuth.getInstance()));
        } else {
            throw new IllegalArgumentException("Unknown ViewModel class");
        }
    }
}
