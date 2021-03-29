package io.github.cchristou3.CyParking.ui.components;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import static io.github.cchristou3.CyParking.utils.Utility.isInMainThread;

/**
 * Purpose: persists data related to the Loading Bar's state
 * when configuration changes and dispatching toast messages.
 * The View simply observes its state and when updated it
 * deals with the visual representation of the Loading Bar
 * based on its new value. The same goes for any
 * incoming the Toast messages.
 * Inherited by: {@link io.github.cchristou3.CyParking.ui.views.host.GlobalStateViewModel}
 * and {@link io.github.cchristou3.CyParking.ui.views.user.account.update.UpdateViewModel}.
 *
 * @author Charalambos Christou
 * @version 2.0 27/02/21
 */
public class LoadingBarViewModel extends ToastViewModel {

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
        if (getLoadingBarState().getValue() != null && getLoadingBarState().getValue()) return;
        if (!isInMainThread())
            updateLoadingBarStateFromBackground(true);
        else
            updateLoadingBarState(true);
    }

    /**
     * Updates the value of {@link #mLoadingBarState} to
     * false (to hide itself).
     */
    public void hideLoadingBar() {
        if (getLoadingBarState().getValue() != null && !getLoadingBarState().getValue()) return;
        if (!isInMainThread())
            updateLoadingBarStateFromBackground(false);
        else
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


    /**
     * Assign the value of {@link #mLoadingBarState}
     * the given argument.
     *
     * @param shouldShowLoadingBar the new value of {@link #mLoadingBarState}.
     */
    private void updateLoadingBarStateFromBackground(boolean shouldShowLoadingBar) {
        this.mLoadingBarState.postValue(shouldShowLoadingBar);
    }
}
