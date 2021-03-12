package io.github.cchristou3.CyParking.ui.views.host;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import io.github.cchristou3.CyParking.apiClient.remote.repository.AuthenticatorRepository;

/**
 * Purpose: <p>ViewModel provider factory to instantiate GlobalStateViewModel.
 * Required given GlobalStateViewModel has a non-empty constructor</p>
 *
 * @author Charalambos Christou
 * @version 2.0 02/02/21
 */
public class GlobalStateViewModelFactory implements ViewModelProvider.Factory {

    private final Context mContext;

    public GlobalStateViewModelFactory(Context context) {
        this.mContext = context;
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
        if (modelClass.isAssignableFrom(GlobalStateViewModel.class)) {
            return (T) new GlobalStateViewModel(mContext, new AuthenticatorRepository());
        } else {
            throw new IllegalArgumentException("Unknown ViewModel class");
        }
    }
}
