package io.github.cchristou3.CyParking.data.manager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.DefaultLifecycleObserver;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleOwner;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.lang.ref.WeakReference;

import io.github.cchristou3.CyParking.data.interfaces.ViewActionHandler;

/**
 * Purpose: Offer a Lifecycle-aware component that handles
 * the {@link FirebaseAuth.AuthStateListener} of a view.
 * Further, it manages its own observer clean up.
 * The view (any subclass of {@link LifecycleOwner}/{@link AppCompatActivity}/{@link androidx.fragment.app.Fragment})
 * must provide an implementation for the interface {@link ViewActionHandler}.
 * The {@link ViewActionHandler#updateUi(FirebaseUser)} will get triggered
 * whenever the {@link FirebaseAuth.AuthStateListener} receives an update.
 * <p>
 *
 * @author Charalambos Christou
 * @version 1.0 14/12/20
 */
public class AuthObserver implements DefaultLifecycleObserver {

    // Data member
    private final FirebaseAuth.AuthStateListener mAuthStateListener;
    private WeakReference<Lifecycle> mWeakLifecycle;

    /**
     * Private Constructor.
     * Initializes the {@link AuthObserver#mAuthStateListener}
     * based on the given ViewActionHandler instance.
     *
     * @param viewActionHandler The callback that is used whenever the authentication state changes.
     */
    private AuthObserver(ViewActionHandler viewActionHandler) {
        this.mAuthStateListener = firebaseAuth -> {
            // User is logged in. Do UI related change here
            viewActionHandler.updateUi(firebaseAuth.getCurrentUser());
        };
    }

    /**
     * Factory method to create a new instance of {@link AuthObserver}.
     *
     * @param viewActionHandler The callback that is used whenever the authentication state changes.
     * @return A new instance of AuthObserver
     */
    @NotNull
    @Contract(value = "_ -> new", pure = true)
    public static AuthObserver newInstance(ViewActionHandler viewActionHandler) {
        return new AuthObserver(viewActionHandler);
    }

    /**
     * Registers the given Lifecycle instance to the current AuthObserver instance.
     * Also, saves a reference of the given Lifecycle which will later be used to
     * clean up the observer.
     *
     * @param lifecycle The Lifecycle of a LifecycleOwner
     */
    public void registerObserver(@NonNull Lifecycle lifecycle) {
        this.mWeakLifecycle = new WeakReference<>(lifecycle);
        // Subscribe LifeCycleOwner to lifecycle changes
        mWeakLifecycle.get().addObserver(this);

    }

    /**
     * Notifies that {@code ON_RESUME} event occurred.
     * <p>
     * This method will be called after the {@link LifecycleOwner}'s {@code onResume}
     * method returns.
     * <p>
     * Adds an {@link FirebaseAuth.AuthStateListener} to the current
     * {@link FirebaseAuth} instance.
     *
     * @param owner the component, whose state was changed
     */
    @Override
    public void onResume(@NonNull LifecycleOwner owner) {
        FirebaseAuth.getInstance().addAuthStateListener(mAuthStateListener);
    }

    /**
     * Notifies that {@code ON_PAUSE} event occurred.
     * <p>
     * This method will be called before the {@link LifecycleOwner}'s {@code onPause} method
     * is called.
     * <p>
     * Remove the {@link FirebaseAuth.AuthStateListener} from the current
     * {@link FirebaseAuth} instance.
     *
     * @param owner the component, whose state was changed
     */
    @Override
    public void onPause(@NonNull LifecycleOwner owner) {
        FirebaseAuth.getInstance().removeAuthStateListener(mAuthStateListener);
    }

    /**
     * Notifies that {@code ON_STOP} event occurred.
     * <p>
     * This method will be called before the {@link LifecycleOwner}'s {@code onStop} method
     * is called.
     * <p>
     * The observer added previously in {@link AuthObserver#registerObserver} is removed.
     *
     * @param owner the component, whose state was changed
     */
    @Override
    public void onStop(@NonNull LifecycleOwner owner) {
        mWeakLifecycle.get().removeObserver(this);
    }
}
