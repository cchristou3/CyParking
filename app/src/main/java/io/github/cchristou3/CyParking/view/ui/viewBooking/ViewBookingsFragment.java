package io.github.cchristou3.CyParking.view.ui.viewBooking;

import android.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QuerySnapshot;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import io.github.cchristou3.CyParking.R;
import io.github.cchristou3.CyParking.view.data.pojo.parking.booking.PrivateParkingBooking;
import io.github.cchristou3.CyParking.view.data.repository.ParkingRepository;

import static io.github.cchristou3.CyParking.view.ui.ParkingMapFragment.TAG;

/**
 * Purpose: <p>Shows pending / completed bookings of the user / operator?</p>
 *
 * @author Charalambos Christou
 * @version 3.0 07/11/20
 */
// TODO: If placeholder animation is taking a long time ~seconds
//  show dialog to user to check his connectivity
public class ViewBookingsFragment extends Fragment {

    // Constant variables
    private static final int INITIAL_DATA_RETRIEVAL = 0;
    private static final int LISTENING_TO_DATA_CHANGES = 1;

    // Fragment variables
    private ArrayList<PrivateParkingBooking> privateParkingBookingArrayList;

    private int databaseDataState;
    private BookingAdapter bookingAdapter;
    private ShimmerFrameLayout mShimmerViewContainer;
    private MutableLiveData<List<PrivateParkingBooking>> bookingListMutableLiveData;
    private Button pendingButton;
    private Button completedButton;
    private ListenerRegistration mListenerRegistration;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Initialize the array-list
        privateParkingBookingArrayList = new ArrayList<>();
        // Initialize the data retrieval state
        databaseDataState = INITIAL_DATA_RETRIEVAL;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_view_bookings, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // Get a reference of the placeholder container from the layout
        mShimmerViewContainer = view.findViewById(R.id.fragment_view_bookings_sfl_shimmer_view_container);

        // Initialize the fragment's ViewModel / LiveData
        bookingListMutableLiveData =
                new ViewModelProvider(requireActivity()).get(ViewBookingsViewModel.class).getBookingListMutableLiveData();

        pendingButton = view.findViewById(R.id.fragment_view_bookings_btn_pending);
        completedButton = view.findViewById(R.id.fragment_view_bookings_btn_completed);
        // TODO: Add appropriate listeners to the above filtering buttons

    }

    @Override
    public void onResume() {
        super.onResume();
        // Check whether the user is logged in
        // TODO: If not show appropriate feedback
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            // Show placeholder layout and hide the filter buttons
            mShimmerViewContainer.startShimmerAnimation();
            pendingButton.setVisibility(View.GONE);
            completedButton.setVisibility(View.GONE);
            // Retrieve all bookings which belong to this user and listen to changes
            mListenerRegistration = ParkingRepository.retrieveUserBookings(FirebaseAuth.getInstance().getCurrentUser().getUid())
                    .addSnapshotListener((value, error) -> {
                        if (error != null || value == null) return; // TODO: Inform user
                        if (!value.isEmpty()) {
                            switch (databaseDataState) {
                                case INITIAL_DATA_RETRIEVAL: { // Happens when we first attach the listener
                                    Log.d(TAG, "INITIAL_DATA_RETRIEVAL: ");
                                    // Fill the list with all the retrieved documents
                                    InitializeBookingList(getView(), value, bookingListMutableLiveData);
                                    // Hide the placeholder container and move on to the next state
                                    mShimmerViewContainer.stopShimmerAnimation();
                                    mShimmerViewContainer.setVisibility(View.GONE);
                                    // Show filter buttons
                                    pendingButton.setVisibility(View.VISIBLE);
                                    completedButton.setVisibility(View.VISIBLE);
                                    databaseDataState = LISTENING_TO_DATA_CHANGES;
                                    break;
                                }
                                case LISTENING_TO_DATA_CHANGES:
                                    Log.d(TAG, "LISTENING_TO_DATA_CHANGES: ");
                                    /* Amend the list's contents: More efficient.
                                       Instead of creating a new list and re-adding all elements.
                                       We simply add/update/remove the objects that got added/updated/removed */
                                    UpdateBookingList(value, bookingListMutableLiveData);
                                    break;
                                default:
                                    throw new IllegalStateException("The state must be one of those:\n" +
                                            "INITIAL_DATA_RETRIEVAL\n" +
                                            "LISTENING_TO_DATA_CHANGES");
                            }
                        }
                    });
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mListenerRegistration != null) mListenerRegistration.remove();
        mShimmerViewContainer.stopShimmerAnimation();
    }

    private void UpdateBookingList(QuerySnapshot value, MutableLiveData<List<PrivateParkingBooking>> mutableLiveData) {
        for (DocumentChange dc : value.getDocumentChanges()) {
            switch (dc.getType()) {
                case ADDED:
                    Log.d(TAG, "onEvent: Added " + dc.getDocument().toObject(PrivateParkingBooking.class));
                    privateParkingBookingArrayList.add(dc.getDocument()
                            .toObject(PrivateParkingBooking.class));
                    // Inform the adapter to add the new item to the view
                    bookingAdapter.notifyItemInserted(privateParkingBookingArrayList.size());
                    break;
                case MODIFIED:
                    Log.d(TAG, "onEvent: Modified " + dc.getDocument().toObject(PrivateParkingBooking.class));
                    findAndUpdateBooking(privateParkingBookingArrayList,
                            dc.getDocument().toObject(PrivateParkingBooking.class));
                    break;
                case REMOVED:
                    Log.d(TAG, "onEvent: Removed " + dc.getDocument().toObject(PrivateParkingBooking.class));
                    int indexOfRemovedObject = privateParkingBookingArrayList.indexOf(dc.getDocument().toObject(PrivateParkingBooking.class));
                    privateParkingBookingArrayList.remove(dc.getDocument().
                            toObject(PrivateParkingBooking.class));
                    // Inform the adapter to remove the item from the view
                    bookingAdapter.notifyItemRemoved(indexOfRemovedObject);
                    break;
            }
        }
        mutableLiveData.setValue(privateParkingBookingArrayList);
    }

    private void InitializeBookingList(View view, QuerySnapshot value, MutableLiveData<List<PrivateParkingBooking>> mutableLiveData) {
        for (DocumentSnapshot doc : value.getDocuments()) {
            privateParkingBookingArrayList.add(doc.toObject(PrivateParkingBooking.class));
        }
        // Set the retrieved data as the value of the LiveData
        mutableLiveData.setValue(privateParkingBookingArrayList);

        // Set up RecyclerView
        RecyclerView recyclerView = view.findViewById(R.id.fragment_view_bookings_rv_recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerView.setHasFixedSize(true);
        //recyclerView.setItemAnimator(new DefaultItemAnimator());

        // Create new adapter and pass the fetched data.
        bookingAdapter = new BookingAdapter(privateParkingBookingArrayList);
        // Pass an onItemClickListener to the adapter
        bookingAdapter.setOnItemClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(getContext()).setTitle(android.R.string.dialog_alert_title)
                        .setPositiveButton(android.R.string.yes, (dialog, which) -> {
                            RecyclerView.ViewHolder viewHolder = (RecyclerView.ViewHolder) v.getTag();
                            int position = viewHolder.getAdapterPosition();
                            final String bookingToBeCancelledId = privateParkingBookingArrayList.get(position).generateUniqueId();
                            ParkingRepository.cancelParking(bookingToBeCancelledId);
                        })
                        .setNegativeButton(android.R.string.cancel, (dialog, which) -> { /* Do nothing */ })
                        .setMessage("Are you sure you would like to cancel the booking?").show();
            }
        });
        bookingAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeMoved(int fromPosition, int toPosition, int itemCount) {
                Log.d(TAG, "onItemRangeMoved: ");
                if (fromPosition >= 2)
                    recyclerView.smoothScrollToPosition(0);
            }
        });

        // Set the recyclerview's adapter
        recyclerView.setAdapter(bookingAdapter);
    }

    /**
     * Searches the list for the specified PrivateParkingBooking and updates it accordingly
     *
     * @param bookingArrayList      List of all the bookings of the current logged in user
     * @param privateParkingBooking The booking which got changed
     */
    public void findAndUpdateBooking(@NotNull ArrayList<PrivateParkingBooking> bookingArrayList, PrivateParkingBooking privateParkingBooking) {
        // Traverse the booking list, searching for an object
        for (int i = 0; i < bookingArrayList.size(); i++) {
            // If there was a matching
            if (bookingArrayList.get(i).generateUniqueId()
                    .equals(privateParkingBooking.generateUniqueId())) {
                // If it was changed from "Pending" to "Completed", move it at the end of the list
                if (!bookingArrayList.get(i).isCompleted() && privateParkingBooking.isCompleted()) {
                    // Remove item and notify the adapter
                    bookingArrayList.remove(i);
                    bookingAdapter.notifyItemRemoved(i);
                    // Add the item at the end of the list and notify the adapter
                    bookingArrayList.add(bookingArrayList.size() - 1, privateParkingBooking);
                    bookingAdapter.notifyItemInserted(bookingArrayList.size() - 1);
                }
                return; // Finish iterating
            }
        }
    }
}