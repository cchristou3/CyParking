package io.github.cchristou3.CyParking.ui.components;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

/**
 * Purpose: persists data related to the Loading Bar's state
 * when configuration changes.
 * The View simply observes its state and when updated it
 * deals with the visual representation of the Loading Bar
 * based on its new value.
 * Inherited by: {@link io.github.cchristou3.CyParking.ui.views.host.GlobalStateViewModel}
 * and {@link io.github.cchristou3.CyParking.ui.views.user.account.update.UpdateViewModel}.
 *
 * @author Charalambos Christou
 * @version 1.0 28/01/21
 */
public class LoadingBarViewModel extends ViewModel {

    // Loading bar state
    private final MutableLiveData<Boolean> mLoadingBarState = new MutableLiveData<>();

    /**
     * Return the {@link #mLoadingBarState} as a {@link LiveData} instance.
     *
     * @return A {@link LiveData} reference of {@link #mLoadingBarState}.
     */
    public LiveData<Boolean> getLoadingBarState() {
        return this.mLoadingBarState;
    }

    /**
     * Updates the value of {@link #mLoadingBarState} to
     * true (to start displaying).
     */
    public void showLoadingBar() {
        updateLoadingBarState(true);
    }

    /**
     * Updates the value of {@link #mLoadingBarState} to
     * false (to hide itself).
     */
    public void hideLoadingBar() {
        updateLoadingBarState(false);
    }

    /**
     * Checks whether the state of {@link #mLoadingBarState}
     * is set to true.
     *
     * @return The value of {@link #mLoadingBarState}.
     */
    public boolean isLoadingBarShowing() {
        return this.mLoadingBarState.getValue() != null && this.mLoadingBarState.getValue();
    }

    /**
     * Assign the value of {@link #mLoadingBarState}
     * the given argument.
     *
     * @param shouldShowLoadingBar the new value of {@link #mLoadingBarState}.
     */
    private void updateLoadingBarState(boolean shouldShowLoadingBar) {
        this.mLoadingBarState.setValue(shouldShowLoadingBar);
    }
}
