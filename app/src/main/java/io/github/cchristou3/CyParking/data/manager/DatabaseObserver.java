package io.github.cchristou3.CyParking.data.manager;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.DefaultLifecycleObserver;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleOwner;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.lang.ref.WeakReference;

import static io.github.cchristou3.CyParking.ui.host.MainHostActivity.TAG;

/**
 * Purpose: Offer a Lifecycle-aware component that handles
 * the attachment and removal of a
 * SnapshotListener(EventListener) of a view.
 * Further, it manages its own observer clean up.
 * The view (any subclass of {@link LifecycleOwner}/{@link AppCompatActivity}/{@link androidx.fragment.app.Fragment})
 * must provide an implementation for the interface {@link EventListener}.
 * The {@link EventListener#onEvent(Object, FirebaseFirestoreException)} will get triggered
 * whenever the {@link EventListener} receives an update or T's data is initially loaded.
 * <p>
 *
 * @param <K> type of {@link Query}, {@link com.google.firebase.firestore.DocumentReference}
 *            or {@link com.google.firebase.firestore.CollectionReference}.
 * @param <V> type of {@link com.google.firebase.firestore.QuerySnapshot}
 *            or {@link com.google.firebase.firestore.DocumentSnapshot}.
 * @author Charalambos Christou
 * @version 1.0 25/12/20
 * <p>
 */
public abstract class DatabaseObserver<K, V> implements DefaultLifecycleObserver {

    // Data members
    private final K mDatabaseReference;
    private final EventListener<V> mEventListener;
    private WeakReference<Lifecycle> mWeakLifecycle;
    private ListenerRegistration mListenerRegistration;

    /**
     * Public Constructor.
     * Initializes the {@link EventListener} and the {@link K}
     * with the specified T and Listener objects.
     *
     * @param query         A data base query.
     * @param eventListener The callback that is used whenever the query's data changes
     *                      or is initially loaded.
     */
    private DatabaseObserver(K query, EventListener<V> eventListener) {
        this.mDatabaseReference = query;
        this.mEventListener = eventListener;
    }

    /**
     * Creates a new instance of DatabaseObserver of type <DocumentReference, DocumentSnapshot>.
     * The DocumentReference refers to a reference of a <u>single</u> document in the database.
     * E.g. By calling {@link FirebaseFirestore#collection(String)} followed up by a call to
     * {@link CollectionReference#document(String)}.
     * Whereas, the DocumentSnapshot refers to the type of data the EventListener will retrieve
     * throughout its lifetime.
     * Thus, the EventListener will trigger when initially attached to the node of that document and
     * when that document's attributes change.
     *
     * @param documentReference The query corresponds to a single document in the database.
     * @param eventListener     The handler of the DocumentSnapshot instances retrieved from
     *                          the database.
     * @return An instance of DatabaseObserver<DocumentReference, DocumentSnapshot>.
     */
    @NotNull
    @Contract(value = "_, _ -> new", pure = true)
    public static DatabaseObserver<DocumentReference, DocumentSnapshot> createDocumentReferenceObserver(
            DocumentReference documentReference, EventListener<DocumentSnapshot> eventListener
    ) {
        return new DatabaseObserver<DocumentReference, DocumentSnapshot>(documentReference,
                eventListener) {
            @Override
            public void addSnapshotListenerToDatabaseQuery() {
                setListenerRegistration(
                        getDatabaseReference()
                                .addSnapshotListener(getEventListener())
                );
            }
        };
    }

    /**
     * Creates a new instance of DatabaseObserver of type <Query, QuerySnapshot>.
     * The Query refers to a reference of zero or many documents in the database.
     * A query focuses on retrieving data from the database based on specified filters.
     * E.g. By chaining the following methods {@link FirebaseFirestore#collection(String)},
     * {@link CollectionReference#whereEqualTo(String, Object)} (or any of its where methods).
     * Whereas, the QuerySnapshot refers to the type of data the EventListener will retrieve
     * throughout its lifetime.
     * Thus, the EventListener will trigger when initially attached to the nodes that the
     * Query has retrieved and when any of the nodes' attributes change.
     *
     * @param query         The query corresponds to filtered data in the database.
     * @param eventListener The handler of the QuerySnapshot instances retrieved from
     *                      the database.
     * @return An instance of DatabaseObserver<Query, QuerySnapshot>.
     */
    @NotNull
    @Contract(value = "_, _ -> new", pure = true)
    public static DatabaseObserver<Query, QuerySnapshot> createQueryObserver(
            Query query, EventListener<QuerySnapshot> eventListener
    ) {
        return new DatabaseObserver<Query, QuerySnapshot>(query,
                eventListener) {
            @Override
            public void addSnapshotListenerToDatabaseQuery() {
                setListenerRegistration(
                        getDatabaseReference()
                                .addSnapshotListener(getEventListener())
                );
            }
        };
    }

    /**
     * Creates a new instance of DatabaseObserver of type <CollectionReference, QuerySnapshot>.
     * The CollectionReference refers to a reference of a <u>single</u> collection in the database.
     * A collection may contain none or many documents.
     * E.g. By calling {@link FirebaseFirestore#collection(String)}.
     * Whereas, the QuerySnapshot refers to the type of data the EventListener will retrieve
     * throughout its lifetime.
     * Thus, the EventListener will trigger when initially attached to the collection's nodes
     * and when any of the collection's documents change.
     *
     * @param collectionReference The query corresponds to filtered data in the database.
     * @param eventListener       The handler of the QuerySnapshot instances retrieved from
     *                            the database.
     * @return An instance of DatabaseObserver<CollectionReference, QuerySnapshot>.
     */
    @NotNull
    @Contract(value = "_, _ -> new", pure = true)
    public static DatabaseObserver<CollectionReference, QuerySnapshot> createCollectionReferenceObserver(
            CollectionReference collectionReference, EventListener<QuerySnapshot> eventListener
    ) {
        return new DatabaseObserver<CollectionReference, QuerySnapshot>(collectionReference,
                eventListener) {
            @Override
            public void addSnapshotListenerToDatabaseQuery() {
                setListenerRegistration(
                        getDatabaseReference()
                                .addSnapshotListener(getEventListener())
                );
            }
        };
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
     * Removes the object's lifecycle observer and database reference's listener.
     * Can be called by its creator, to prematurely stop the
     * {@link DatabaseObserver} instance.
     */
    public void unregisterLifecycleObserver() {
        Log.d(TAG, "Lifecycle observer removed!");
        mWeakLifecycle.get().removeObserver(this);
        removeSnapshotListenerFromDatabaseQuery();
    }

    /**
     * Removes the object's database reference's ({@link #mDatabaseReference}) listener.
     */
    private void removeSnapshotListenerFromDatabaseQuery() {
        if (mListenerRegistration != null) {
            Log.d(TAG, "SnapshotListener removed!");
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
     * {@link #removeSnapshotListenerFromDatabaseQuery()}.
     *
     * @param owner the component, whose state was changed
     */
    @Override
    public void onResume(@NonNull LifecycleOwner owner) {
        addSnapshotListenerToDatabaseQuery();
    }

    /**
     * Notifies that {@code ON_PAUSE} event occurred.
     * <p>
     * This method will be called before the {@link LifecycleOwner}'s {@code onPause} method
     * is called.
     * Remove the {@link EventListener<V>} from the current
     * {@link K} instance.
     *
     * @param owner the component, whose state was changed
     */
    @Override
    public void onPause(@NonNull LifecycleOwner owner) {
        removeSnapshotListenerFromDatabaseQuery();
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

    /**
     * Access the {@link #mDatabaseReference}
     *
     * @return A reference to the database query
     */
    public K getDatabaseReference() {
        return mDatabaseReference;
    }

    /**
     * Access the object's EventListener instance.
     *
     * @return The {@link #mEventListener}.
     */
    public EventListener<V> getEventListener() {
        return mEventListener;
    }

    /**
     * Setter method for {@link #mListenerRegistration}.
     *
     * @param mListenerRegistration The new value of {@link #mListenerRegistration}.
     */
    public void setListenerRegistration(ListenerRegistration mListenerRegistration) {
        this.mListenerRegistration = mListenerRegistration;
    }

    /**
     * Adds to the query of type K the Event listener of type V.
     * To be overridden by subclasses or when instantiating a
     * {@link DatabaseObserver}.
     */
    public abstract void addSnapshotListenerToDatabaseQuery();
}
