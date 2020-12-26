package io.github.cchristou3.CyParking.data.manager;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.DefaultLifecycleObserver;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleOwner;

import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.lang.ref.WeakReference;

import static io.github.cchristou3.CyParking.MainHostActivity.TAG;

/**
 * Purpose: Offer a Lifecycle-aware component that handles
 * the {@link Query#addSnapshotListener(EventListener)} of a view.
 * Further, it manages its own observer clean up.
 * The view (any subclass of {@link LifecycleOwner}/{@link AppCompatActivity}/{@link androidx.fragment.app.Fragment})
 * must provide an implementation for the interface {@link EventListener}.
 * The {@link EventListener#onEvent(Object, FirebaseFirestoreException)} will get triggered
 * whenever the {@link EventListener} receives an update or the Query's data is initially loaded.
 * <p>
 *
 * @author Charalambos Christou
 * @version 1.0 25/12/20
 */
public class DatabaseObserver implements DefaultLifecycleObserver {

    // Data members
    private final Query mQuery;
    private final EventListener<QuerySnapshot> mEventListener;
    private WeakReference<Lifecycle> mWeakLifecycle;
    private ListenerRegistration mListenerRegistration;

    /**
     * Public Constructor.
     * Initializes the {@link EventListener} and the {@link Query}
     * with the specified Query and Listener objects.
     *
     * @param eventListener The callback that is used whenever the query's data changes
     *                      or is initially loaded.
     */
    public DatabaseObserver(Query query, EventListener<QuerySnapshot> eventListener) {
        this.mQuery = query;
        this.mEventListener = eventListener;
    }

    /**
     * Registers the given Lifecycle instance to the current DatabaseObserver instance.
     * Also, saves a reference of the given Lifecycle which will later be used to
     * clean up the observer.
     *
     * @param lifecycle The Lifecycle of a LifecycleOwner
     */
    public void registerLifecycleObserver(@NonNull Lifecycle lifecycle) {
        Log.d(TAG, "Lifecycle observer added!");
        this.mWeakLifecycle = new WeakReference<>(lifecycle);
        // Subscribe LifeCycleOwner to lifecycle changes
        mWeakLifecycle.get().addObserver(this);
    }

    /**
     * Removes the object's lifecycle observer and query's listener.
     */
    public void unregisterLifecycleObserver() {
        Log.d(TAG, "Lifecycle observer removed!");
        mWeakLifecycle.get().removeObserver(this);
        unregisterDatabaseObserver();
    }

    /**
     * Removes the object's query listener.
     */
    public void unregisterDatabaseObserver() {
        if (mListenerRegistration != null) {
            Log.d(TAG, "unregisterDatabaseObserver: SnapshotListener removed!");
            mListenerRegistration.remove();
            mListenerRegistration = null;
        }
    }

    /**
     * Notifies that {@code ON_RESUME} event occurred.
     * <p>
     * This method will be called after the {@link LifecycleOwner}'s {@code onResume}
     * method returns.
     * Adds an {@link EventListener} to the current {@link Query} instance.
     * Further, the reference of the returned {@link ListenerRegistration}
     * instance is saved to be used for cleaning up the EventListener in
     * {@link #unregisterDatabaseObserver()}.
     *
     * @param owner the component, whose state was changed
     */
    @Override
    public void onResume(@NonNull LifecycleOwner owner) {
        Log.d(TAG, "onResume: SnapshotListener added!");
        mListenerRegistration = mQuery.addSnapshotListener(mEventListener);
    }

    /**
     * Notifies that {@code ON_PAUSE} event occurred.
     * <p>
     * This method will be called before the {@link LifecycleOwner}'s {@code onPause} method
     * is called.
     * Remove the {@link EventListener} from the current
     * {@link Query} instance.
     *
     * @param owner the component, whose state was changed
     */
    @Override
    public void onPause(@NonNull LifecycleOwner owner) {
        unregisterDatabaseObserver();
    }

    /**
     * Notifies that {@code ON_DESTROY} event occurred.
     * <p>
     * This method will be called before the {@link LifecycleOwner}'s {@code onDestroy} method
     * is called.
     * The observer added previously in {@link DatabaseObserver#registerLifecycleObserver} is removed.
     *
     * @param owner the component, whose state was changed
     */
    @Override
    public void onDestroy(@NonNull LifecycleOwner owner) {
        unregisterLifecycleObserver();
    }
}
