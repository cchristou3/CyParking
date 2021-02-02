package io.github.cchristou3.CyParking.ui.components;

import android.os.Bundle;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewbinding.ViewBinding;

import org.jetbrains.annotations.NotNull;

import io.github.cchristou3.CyParking.data.model.user.LoggedInUser;
import io.github.cchristou3.CyParking.databinding.FeedbackFragmentBinding;
import io.github.cchristou3.CyParking.ui.views.host.GlobalStateViewModel;

/**
 * Purpose: encapsulate common logic found in the application's screens:
 * <ul>
 *     <li>{@link io.github.cchristou3.CyParking.ui.views.home.HomeFragment}</li>
 *     <li>{@link io.github.cchristou3.CyParking.ui.views.parking.lots.map.ParkingMapFragment}</li>
 *     <li>{@link io.github.cchristou3.CyParking.ui.views.parking.slots.booking.BookingFragment}</li>
 *     <li>{@link io.github.cchristou3.CyParking.ui.views.user.account.AccountFragment}</li>
 *     <li>{@link io.github.cchristou3.CyParking.ui.views.parking.slots.viewBooking.ViewBookingsFragment}</li>
 *     <li>{@link io.github.cchristou3.CyParking.ui.views.parking.lots.register.RegisterLotFragment}</li>
 *     <li>{@link io.github.cchristou3.CyParking.ui.views.user.feedback.FeedbackFragment}</li>
 *     <li>{@link io.github.cchristou3.CyParking.ui.views.user.login.AuthenticatorHosteeFragment}</li>
 * </ul>
 * <p>Such logic involves:
 * <ul>
 *     <li>Utilizing the {@link GlobalStateViewModel} to access the following states:
 *     <ul>
 *         <li>User state</li>
 *         <li>Internet Connection state</li>
 *         <li>Loading bar state</li>
 *     </ul></li>
 *     <li>Using the {@link ViewBinding}, resulting into a more safer view manipulation.</li>
 * </ul>
 * </p>
 *
 * @param <T> Any type that implements the {@link ViewBinding} interface.
 * @author Charalambos Christou
 * @version 2.0 28/01/21
 * @see ViewBinding
 * @see GlobalStateViewModel
 * @see FeedbackFragmentBinding
 * @see io.github.cchristou3.CyParking.databinding.FragmentHomeBinding
 */
public class CommonFragment<T extends ViewBinding> extends Fragment {

    private T mViewBinding;
    private GlobalStateViewModel mGlobalStateViewModel;

    /**
     * Called to do initial creation of a fragment.
     *
     * @param savedInstanceState If the fragment is being re-created from
     *                           a previous saved state, this is the state.
     */
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Initialize GlobalStateViewModel of the fragment
        mGlobalStateViewModel = new ViewModelProvider(requireActivity())
                .get(GlobalStateViewModel.class);
    }

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
     * Note: Always unset listeners before calling the subclass' onDestroyView
     * method. E.g.
     * <pre>
     *     {@literal @}Override
     *      public void onDestroyView() {
     *         getBinding().someButton.setOnClickListener(null);
     *         super.onDestroyView();
     *     }
     * </pre>
     */
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        setBinding(null);
    }

    /**
     * Removes the {@link android.view.View.OnClickListener} of the given views.
     *
     * @param clickableViews An array of clickable views.
     */
    protected void removeOnClickListeners(@NotNull View... clickableViews) {
        for (View view :
                clickableViews) {
            view.setOnClickListener(null);
        }
    }

    /**
     * Removes the {@link TextWatcher} of the given editTexts.
     *
     * @param textWatcher The {@link TextWatcher} to remove.
     * @param editTexts   An array of editTexts.
     */
    protected void removeTextWatchers(TextWatcher textWatcher, @NotNull EditText... editTexts) {
        for (EditText editText :
                editTexts) {
            editText.removeTextChangedListener(textWatcher);
        }
    }

    /**
     * Access the {@link #mGlobalStateViewModel}.
     *
     * @return A reference to {@link #mGlobalStateViewModel}.
     */
    public GlobalStateViewModel getGlobalStateViewModel() {
        return this.mGlobalStateViewModel;
    }

    /**
     * Access the current {@link LoggedInUser} instance
     * if there is one.
     *
     * @return A reference to current {@link LoggedInUser} instance
     * if there is one.
     */
    @Nullable
    public LoggedInUser getUser() {
        return mGlobalStateViewModel.getUser();
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

    /**
     * Adds an observer to the user's state.
     * When triggered, it is handled by the given
     * {@link UserStateUiHandler}.
     *
     * @param userStateUiHandler The handler for user state updates.
     */
    public void observeUserState(@NotNull UserStateUiHandler userStateUiHandler) {
        mGlobalStateViewModel.getUserState()
                .observe(getViewLifecycleOwner(), userStateUiHandler::onUserStateChanged);
    }

    /**
     * Purpose: Provide the subclasses of {@link CommonFragment}
     * an interface to update the Ui based on the
     * the value of {@link GlobalStateViewModel#getUserState()}.
     */
    public interface UserStateUiHandler {
        /**
         * Invoked whenever the value of {@link GlobalStateViewModel#getUserState()}
         * gets changed.
         * <strong>Note: if its value has not been set, then it won't get triggered.</strong>
         *
         * @param user The new value of {@link GlobalStateViewModel#getUserState()}.
         */
        void onUserStateChanged(@Nullable LoggedInUser user);
    }
}
