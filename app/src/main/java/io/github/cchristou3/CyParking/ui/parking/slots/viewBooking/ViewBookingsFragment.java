package io.github.cchristou3.CyParking.ui.parking.slots.viewBooking;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import io.github.cchristou3.CyParking.R;
import io.github.cchristou3.CyParking.data.interfaces.Navigable;
import io.github.cchristou3.CyParking.data.manager.AlertBuilder;
import io.github.cchristou3.CyParking.data.manager.DatabaseObserver;
import io.github.cchristou3.CyParking.data.pojo.parking.slot.booking.PrivateParkingBooking;
import io.github.cchristou3.CyParking.data.repository.ParkingRepository;
import io.github.cchristou3.CyParking.databinding.FragmentViewBookingsBinding;
import io.github.cchristou3.CyParking.ui.home.HomeFragment;
import io.github.cchristou3.CyParking.ui.host.AuthStateViewModel;
import io.github.cchristou3.CyParking.ui.host.MainHostActivity;
import io.github.cchristou3.CyParking.ui.user.AccountFragment;
import io.github.cchristou3.CyParking.ui.user.feedback.FeedbackFragment;
import io.github.cchristou3.CyParking.ui.user.login.AuthenticatorFragment;

import static io.github.cchristou3.CyParking.ui.parking.lots.ParkingMapFragment.TAG;

/**
 * Purpose: <p>Shows pending / completed bookings of the user / operator?</p>
 * <p>
 * In terms of Authentication, this is achieved by communicating with the hosting
 * activity {@link MainHostActivity} via the {@link AuthStateViewModel}.
 * </p>
 *
 * @author Charalambos Christou
 * @version 4.0 28/12/20
 */
public class ViewBookingsFragment extends Fragment implements Navigable {

    // Constant variables
    private static final int INITIAL_DATA_RETRIEVAL = 0;
    private static final int LISTENING_TO_DATA_CHANGES = 1;

    // Fragment variables
    private ArrayList<PrivateParkingBooking> privateParkingBookingArrayList;
    private AuthStateViewModel mAuthStateViewModel;
    private FragmentViewBookingsBinding mFragmentViewBookingsBinding;
    private int databaseDataState;
    private BookingAdapter bookingAdapter;
    private MutableLiveData<List<PrivateParkingBooking>> bookingList;

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
        mFragmentViewBookingsBinding = FragmentViewBookingsBinding.inflate(inflater);
        return mFragmentViewBookingsBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // Initialize the mAuthStateViewModel
        mAuthStateViewModel = new ViewModelProvider(requireActivity()).get(AuthStateViewModel.class);

        // Attach an observer to the user's state.
        // If the user logs out while being in this screen, an alert is shown
        mAuthStateViewModel.getUserState().observe(getViewLifecycleOwner(), loggedInUser -> {
            if (loggedInUser == null) {
                AlertBuilder.promptUserToLogIn(requireContext(), requireActivity(), this,
                        R.string.logout_view_bookings_screen_msg);
            }
        });

        // Initialize the fragment's ViewModel / LiveData
        bookingList =
                new ViewModelProvider(this).get(ViewBookingsViewModel.class).getBookingList();
    }

    /**
     * Called when the Fragment is visible to the user.
     * Retrieve all bookings that belong to this user and listen to changes.
     * If no bookings were found, show a message to the user.
     */
    @Override
    public void onStart() {
        super.onStart();
        // Check whether the user is logged in
        if (mAuthStateViewModel.getUser() != null) {
            // Show placeholder layout and hide the filter buttons
            getBinding().fragmentViewBookingsSflShimmerLayout.startShimmerAnimation();

            DatabaseObserver.createQueryObserver(ParkingRepository
                            .retrieveUserBookings(mAuthStateViewModel.getUser().getUserId()),
                    (value, error) -> {
                        if (error != null || value == null) return; // TODO: Inform user
                        if (!value.isEmpty()) {
                            switch (databaseDataState) {
                                case INITIAL_DATA_RETRIEVAL: { // Happens when we first attach the listener
                                    Log.d(TAG, "INITIAL_DATA_RETRIEVAL: ");
                                    // Fill the list with all the retrieved documents
                                    InitializeBookingList(value, bookingList);
                                    // Hide the placeholder container and move on to the next state
                                    getBinding().fragmentViewBookingsSflShimmerLayout.stopShimmerAnimation();
                                    getBinding().fragmentViewBookingsSflShimmerLayout.setVisibility(View.GONE);
                                    databaseDataState = LISTENING_TO_DATA_CHANGES;
                                    break;
                                }
                                case LISTENING_TO_DATA_CHANGES:
                                    Log.d(TAG, "LISTENING_TO_DATA_CHANGES: ");
                                    /* Amend the list's contents: More efficient.
                                       Instead of creating a new list and re-adding all elements.
                                       We simply add/update/remove the objects that got added/updated/removed */
                                    UpdateBookingList(value, bookingList);
                                    break;
                                default:
                                    throw new IllegalStateException("The state must be one of those:\n" +
                                            "INITIAL_DATA_RETRIEVAL\n" +
                                            "LISTENING_TO_DATA_CHANGES");
                            }
                        } else {
                            // Display message to user
                            getBinding().fragmentViewBookingsTxtNoBookings.setVisibility(View.VISIBLE);
                            // Hide view's related to displaying
                            getBinding().fragmentViewBookingsRvRecyclerview.setVisibility(View.GONE);
                            getBinding().fragmentViewBookingsSflShimmerLayout.stopShimmerAnimation(); // Also, stop animating
                            getBinding().fragmentViewBookingsSvScrollView.setVisibility(View.GONE);
                        }
                    }
            ).registerLifecycleObserver(getLifecycle());
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        // Stop animating, if still animating
        if (getBinding().fragmentViewBookingsSflShimmerLayout.isAnimationStarted()) {
            getBinding().fragmentViewBookingsSflShimmerLayout.stopShimmerAnimation();
        }
    }

    /**
     * Called when the view previously created by {@link #onCreateView} has
     * been detached from the fragment.
     */
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        bookingAdapter.setOnItemClickListener(null);
        mFragmentViewBookingsBinding = null;
    }

    /**
     * Access the {@link #mFragmentViewBookingsBinding}.
     *
     * @return A reference to {@link #mFragmentViewBookingsBinding}.
     */
    private FragmentViewBookingsBinding getBinding() {
        return mFragmentViewBookingsBinding;
    }

    /**
     * Syncs the local booking list of the user with the one on the server.
     *
     * @param bookingsOnServer The bookings list on the server
     * @param localBookings    The bookings on the client side.
     */
    private void UpdateBookingList(@NotNull QuerySnapshot bookingsOnServer, MutableLiveData<List<PrivateParkingBooking>> localBookings) {
        for (DocumentChange dc : bookingsOnServer.getDocumentChanges()) {
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
        localBookings.setValue(privateParkingBookingArrayList);
    }

    /**
     * Initializes the fragment's RecyclerView and its adapter instance.
     * Also, initially loads the current QuerySnapshot's documents ({@link PrivateParkingBooking})
     * objects to the fragment's ViewModel.
     *
     * @param bookingsOnServer The bookings list on the server
     * @param localBookings    The bookings on the client side.
     */
    private void InitializeBookingList(@NotNull QuerySnapshot bookingsOnServer, MutableLiveData<List<PrivateParkingBooking>> localBookings) {
        for (DocumentSnapshot doc : bookingsOnServer.getDocuments()) {
            privateParkingBookingArrayList.add(doc.toObject(PrivateParkingBooking.class));
        }
        // Set the retrieved data as the value of the LiveData
        localBookings.setValue(privateParkingBookingArrayList);

        // Set up RecyclerView
        getBinding().fragmentViewBookingsRvRecyclerview
                .setLayoutManager(new LinearLayoutManager(requireContext()));
        getBinding().fragmentViewBookingsRvRecyclerview.setHasFixedSize(true);

        // Create new adapter and pass the fetched data.
        bookingAdapter = new BookingAdapter(privateParkingBookingArrayList);
        // Pass an onItemClickListener to the adapter
        bookingAdapter.setOnItemClickListener(v ->
                AlertBuilder.showAlert(requireContext(),
                        android.R.string.dialog_alert_title,
                        R.string.booking_cancellation_confirmation,
                        android.R.string.yes,
                        android.R.string.cancel,
                        (dialog, which) -> {
                            RecyclerView.ViewHolder viewHolder = (RecyclerView.ViewHolder) v.getTag();
                            int position = viewHolder.getAdapterPosition();
                            final String bookingToBeCancelledId = privateParkingBookingArrayList.get(position).generateUniqueId();
                            ParkingRepository.cancelParkingBooking(bookingToBeCancelledId);
                        },
                        null));

        bookingAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeMoved(int fromPosition, int toPosition, int itemCount) {
                Log.d(TAG, "onItemRangeMoved: ");
                if (fromPosition >= 2) {
                    // Scroll to the beginning of the list
                    getBinding().fragmentViewBookingsRvRecyclerview
                            .smoothScrollToPosition(0);
                }
            }
        });

        // Set the recyclerview's adapter
        getBinding().fragmentViewBookingsRvRecyclerview.setAdapter(bookingAdapter);
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
                return; // stop iterating
            }
        }
    }

    /**
     * Navigates from the current Fragment subclass to the
     * {@link AuthenticatorFragment}.
     */
    @Override
    public void toAuthenticator() {
        /* Only logged in users can navigate the current screen (View bookings).
         *  If a user is logged in, the action bar option to "sign in" is hidden.
         *  However, in case the user is in this screen and decides to logout,
         *  an alert will be displayed. */
        Navigation.findNavController(getActivity().findViewById(R.id.fragment_main_host_nv_nav_view))
                .navigate(R.id.action_nav_view_bookings_to_nav_authenticator_fragment);
    }

    /**
     * Navigates from the current Fragment subclass to the
     * {@link ViewBookingsFragment}.
     */
    @Override
    public void toBookings() {
        // Already in this screen. Thus, no need to implement it.
    }

    /**
     * Navigates from the current Fragment subclass to the
     * {@link AccountFragment}.
     */
    @Override
    public void toAccount() {
        Navigation.findNavController(getActivity().findViewById(R.id.fragment_main_host_nv_nav_view))
                .navigate(R.id.action_nav_view_bookings_to_nav_account);
    }

    /**
     * Navigates from the current Fragment subclass to the
     * {@link FeedbackFragment}.
     */
    @Override
    public void toFeedback() {
        Navigation.findNavController(getActivity().findViewById(R.id.fragment_main_host_nv_nav_view))
                .navigate(R.id.action_nav_view_bookings_to_nav_feedback);
    }

    /**
     * Navigates from the current Fragment subclass to the
     * {@link HomeFragment}.
     */
    @Override
    public void toHome() {
        Navigation.findNavController(getActivity().findViewById(R.id.fragment_main_host_nv_nav_view))
                .navigate(R.id.action_nav_view_bookings_to_nav_home);
    }
}