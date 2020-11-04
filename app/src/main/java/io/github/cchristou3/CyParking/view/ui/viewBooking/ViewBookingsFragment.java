package io.github.cchristou3.CyParking.view.ui.viewBooking;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

import io.github.cchristou3.CyParking.R;
import io.github.cchristou3.CyParking.view.data.pojo.parking.booking.PrivateParkingBooking;
import io.github.cchristou3.CyParking.view.data.repository.ParkingRepository;

/**
 * Purpose: <p>Shows pending / completed bookings of the user / operator?</p>
 *
 * @author Charalambos Christou
 * @version 2.0 05/11/20
 */
public class ViewBookingsFragment extends Fragment {

    // TODO: Add a button (cancel) to the list items and wire it with the database
    // Fragment variables
    private ArrayList<PrivateParkingBooking> privateParkingBookingArrayList;
    private final View.OnClickListener mOnItemClickListener = v -> {
        RecyclerView.ViewHolder viewHolder = (RecyclerView.ViewHolder) v.getTag();
        int position = viewHolder.getAdapterPosition();
        final String bookingToBeCancelledId = privateParkingBookingArrayList.get(position).generateUniqueId();
        ParkingRepository.cancelParking(bookingToBeCancelledId);
    };

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Initialize the array-list
        privateParkingBookingArrayList = new ArrayList<>();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_view_bookings, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        final Button pendingButton = view.findViewById(R.id.fragment_view_bookings_btn_pending);
        final Button completedButton = view.findViewById(R.id.fragment_view_bookings_btn_completed);
        // TODO: Add appropriate listeners to the above buttons

        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            // TODO: Move database logic to repository
            //  Instantiate ViewModel & listen to database changes (listen only to pending bookings, not completed)

            // Retrieve all bookings which belong to this user
            FirebaseFirestore.getInstance()
                    .collection("private_parking_bookings")
                    .whereEqualTo("userID", FirebaseAuth.getInstance().getCurrentUser().getUid())
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            // Add all results into the array list
                            for (DocumentSnapshot doc : task.getResult()) {
                                privateParkingBookingArrayList.add(doc.toObject(PrivateParkingBooking.class));
                            }
                            // Create new adapter and pass the fetched data.
                            BookingAdapter bookingAdapter = new BookingAdapter(privateParkingBookingArrayList);
                            // Set up RecyclerView
                            RecyclerView recyclerView = view.findViewById(R.id.fragment_view_bookings_rv_recyclerview);
                            recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
                            recyclerView.setHasFixedSize(true);
                            // Pass an onItemClickListener to the adapter
                            bookingAdapter.setOnItemClickListener(mOnItemClickListener);
                            // Set the recyclerview's adapter
                            recyclerView.setAdapter(bookingAdapter);
                        }
                    });
        }
    }
}