package io.github.cchristou3.CyParking.ui.views.user.login;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import io.github.cchristou3.CyParking.apiClient.remote.repository.AuthenticatorRepository;

/**
 * Purpose: <p>ViewModel provider factory to instantiate AuthenticatorViewModel.
 * Required given AuthenticatorViewModel has a non-empty constructor</p>
 *
 * @author Charalambos Christou
 * @version 1.0 1/11/20
 */
public class AuthenticatorViewModelFactory implements ViewModelProvider.Factory {

    private final AuthenticatorRepository mAuthenticatorRepository;

    public AuthenticatorViewModelFactory(AuthenticatorRepository authenticatorRepository) {
        this.mAuthenticatorRepository = authenticatorRepository;
    }

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
        if (modelClass.isAssignableFrom(AuthenticatorViewModel.class)) {
            return (T) new AuthenticatorViewModel(mAuthenticatorRepository);
        } else {
            throw new IllegalArgumentException("Unknown ViewModel class");
        }
    }
}