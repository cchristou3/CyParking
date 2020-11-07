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
import androidx.core.widget.ContentLoadingProgressBar;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
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
 * @version 2.0 05/11/20
 */
public class ViewBookingsFragment extends Fragment {

    private static final int INITIAL_DATA_RETRIEVAL = 0;
    private static final int LISTENING_TO_DATA_CHANGES = 1;
    // Fragment variables
    private ArrayList<PrivateParkingBooking> privateParkingBookingArrayList;
    private final View.OnClickListener mOnItemClickListener = new View.OnClickListener() {
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
    };
    private int databaseDataState;
    private BookingAdapter bookingAdapter;

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
        // Get a reference of the loading bar from the layout
        final ContentLoadingProgressBar loadingProgressBar = view.findViewById(R.id.fragment_view_bookings_pb_loadingBookings);
        loadingProgressBar.getIndeterminateDrawable().setColorFilter(0xFFFF0000, android.graphics.PorterDuff.Mode.MULTIPLY);
        loadingProgressBar.show(); // Show the progress bar to the user

        // Initialize the fragment's ViewModel
        final ViewBookingsViewModel viewBookingsViewModel = new ViewModelProvider(requireActivity()).get(ViewBookingsViewModel.class);
        final MutableLiveData<List<PrivateParkingBooking>> bookingListMutableLiveData =
                viewBookingsViewModel.getBookingListMutableLiveData();

        final Button pendingButton = view.findViewById(R.id.fragment_view_bookings_btn_pending);
        final Button completedButton = view.findViewById(R.id.fragment_view_bookings_btn_completed);
        // TODO: Add appropriate listeners to the above filtering buttons

        // Check whether the user is logged in
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            // Retrieve all bookings which belong to this user and listen to changes
            ParkingRepository.retrieveUserBookings(FirebaseAuth.getInstance().getCurrentUser().getUid())
                    .addSnapshotListener((value, error) -> {
                        if (error != null || value == null) return; // TODO: Inform user
                        if (!value.isEmpty()) {
                            switch (databaseDataState) {
                                case INITIAL_DATA_RETRIEVAL: { // Happens when we first attach the listener
                                    // Fill the list with all the retrieved documents
                                    InitializeBookingList(view, value, bookingListMutableLiveData);
                                    // Hide the loading bar and move on to the next state
                                    databaseDataState = LISTENING_TO_DATA_CHANGES;
                                    loadingProgressBar.hide();
                                    break;
                                }
                                case LISTENING_TO_DATA_CHANGES:
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
        // Create new adapter and pass the fetched data.
        bookingAdapter = new BookingAdapter(privateParkingBookingArrayList);
        // Pass an onItemClickListener to the adapter
        bookingAdapter.setOnItemClickListener(mOnItemClickListener);

        // Set up RecyclerView
        RecyclerView recyclerView = view.findViewById(R.id.fragment_view_bookings_rv_recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerView.setHasFixedSize(true);
        // Set the recyclerview's adapter
        recyclerView.setAdapter(bookingAdapter);
    }

    public void findAndUpdateBooking(@NotNull ArrayList<PrivateParkingBooking> bookingArrayList, PrivateParkingBooking privateParkingBooking) {
        // Traverse the booking list, searching for an object
        for (int i = 0; i < bookingArrayList.size(); i++) {
            if (bookingArrayList.get(i).generateUniqueId()
                    .equals(privateParkingBooking.generateUniqueId())) {
                // Update its contents with the newly retrieved ones
                bookingArrayList.get(i).updateContents(privateParkingBooking);
                // Inform the adapter to update the current view slot
                bookingAdapter.notifyItemChanged(i);
                return;
            }
        }
    }
}