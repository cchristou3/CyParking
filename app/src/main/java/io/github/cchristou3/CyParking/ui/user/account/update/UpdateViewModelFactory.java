package io.github.cchristou3.CyParking.ui.user.account.update;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.google.firebase.auth.FirebaseAuth;

import io.github.cchristou3.CyParking.data.repository.AccountRepository;

/**
 * Purpose: <p>ViewModel provider factory to instantiate UpdateViewModel.
 * Required given UpdateViewModel has a non-empty constructor</p>
 *
 * @author Charalambos Christou
 * @version 1.0 23/11/20
 */
public class UpdateViewModelFactory implements ViewModelProvider.Factory {

    @NonNull
    @Override
    @SuppressWarnings("unchecked")
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(UpdateViewModel.class)) {
            return (T) new UpdateViewModel(new AccountRepository(FirebaseAuth.getInstance()));
        } else {
            throw new IllegalArgumentException("Unknown ViewModel class");
        }
    }
}