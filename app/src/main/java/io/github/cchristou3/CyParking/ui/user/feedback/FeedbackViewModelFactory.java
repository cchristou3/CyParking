package io.github.cchristou3.CyParking.ui.user.feedback;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import io.github.cchristou3.CyParking.data.repository.FeedbackRepository;

/**
 * Purpose: <p>ViewModel provider factory to instantiate FeedbackViewModel.
 * Required given FeedbackViewModel has a non-empty constructor</p>
 *
 * @author Charalambos Christou
 * @version 1.0 14/01/21
 */
public class FeedbackViewModelFactory implements ViewModelProvider.Factory {
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
        if (modelClass.isAssignableFrom(FeedbackViewModel.class)) {
            return (T) new FeedbackViewModel(new FeedbackRepository());
        } else {
            throw new IllegalArgumentException("Unknown ViewModel class");
        }
    }
}
