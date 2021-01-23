package io.github.cchristou3.CyParking.ui;

import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;

import androidx.fragment.app.Fragment;
import androidx.viewbinding.ViewBinding;

import org.jetbrains.annotations.NotNull;

public class ViewBindingFragment<T extends ViewBinding> extends Fragment {

    private T mViewBinding;

    /**
     * Called to have the fragment instantiate its user interface view.
     */
    public View onCreateView(T mViewBinding) {
        // Return the root view from the onCreateView() method to make it the active view on the screen.
        return setBinding(mViewBinding).getRoot();
    }

    /**
     * Called when the view previously created by {@link #onCreateView} has
     * been detached from the fragment.  The next time the fragment needs
     * to be displayed, a new view will be created.
     */
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        setBinding(null);
    }

    protected void removeOnClickListeners(@NotNull View... clickableViews) {
        for (View view :
                clickableViews) {
            view.setOnClickListener(null);
        }
    }

    protected void removeTextWatchers(TextWatcher textWatcher, @NotNull EditText... editTexts) {
        for (EditText editText :
                editTexts) {
            editText.removeTextChangedListener(textWatcher);
        }
    }

    /**
     * Access the {@link #mViewBinding}.
     *
     * @return A reference to {@link #mViewBinding}.
     */
    public final T getBinding() {
        return mViewBinding;
    }

    /**
     * Access the {@link #mViewBinding}.
     *
     * @param mViewBinding A reference to {@link #mViewBinding}.
     * @return This ViewBinding object to allow for chaining of calls to methods.
     */
    public final T setBinding(T mViewBinding) {
        this.mViewBinding = mViewBinding;
        return getBinding();
    }
}
