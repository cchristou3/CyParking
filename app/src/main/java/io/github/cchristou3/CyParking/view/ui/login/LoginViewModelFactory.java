package io.github.cchristou3.CyParking.view.ui.login;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.google.firebase.auth.FirebaseAuth;

import io.github.cchristou3.CyParking.view.data.repository.LoginRepository;

/**
 * Purpose: <p>ViewModel provider factory to instantiate LoginViewModel.
 * Required given LoginViewModel has a non-empty constructor</p>
 *
 * @author Charalambos Christou
 * @version 1.0 1/11/20
 */
public class LoginViewModelFactory implements ViewModelProvider.Factory {

    @NonNull
    @Override
    @SuppressWarnings("unchecked")
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(LoginViewModel.class)) {
            return (T) new LoginViewModel(LoginRepository.getInstance(FirebaseAuth.getInstance()));
        } else {
            throw new IllegalArgumentException("Unknown ViewModel class");
        }
    }
}